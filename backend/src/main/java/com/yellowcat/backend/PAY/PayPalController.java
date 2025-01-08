package com.yellowcat.backend.PAY;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Hoadondoidichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.HoaDonDoiDichVuService;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.LichHenService;
import com.yellowcat.backend.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/payPal")
public class PayPalController {

    private final PayPalService payPalService;
    private final HoaDonService hoaDonService;
    private final LichHenService lichHenService;
    private final PdfExportService pdfExportService;
    private final HoaDonDoiDichVuService hoaDonDoiDichVuService;

    public PayPalController(PayPalService payPalService, HoaDonService hoaDonService, LichHenService lichHenService, PdfExportService pdfExportService, HoaDonDoiDichVuService hoaDonDoiDichVuService) {
        this.payPalService = payPalService;
        this.hoaDonService = hoaDonService;
        this.lichHenService = lichHenService;
        this.pdfExportService = pdfExportService;
        this.hoaDonDoiDichVuService = hoaDonDoiDichVuService;
    }

    @PostMapping("/payment/create")
    public ResponseEntity<String> createPayment(@RequestHeader String idLichHen) {
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(Integer.parseInt(idLichHen));

        if (!hoadonOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại");
        }
        Hoadon hoadon = hoadonOptional.get();
        try {
            String cancelUrl = "http://localhost:8080/api/payPal/payment/cancel/" + idLichHen;
            String successUrl = "http://localhost:8080/api/payPal/payment/success/"+idLichHen;
            Payment payment = payPalService.createPayment(
                    hoadon.getSotien(),
                    "USD",
                    "paypal",
                    "sale",
                    "Thanh toán hóa đơn ",
                    cancelUrl,
                    successUrl
            );
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    hoadon.setMagiaodich(payment.getId());
                    hoaDonService.addOrUpdate(hoadon);
                    return ResponseEntity.ok(links.getHref());
                }
            }
        }catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể tạo thanh toán");
    }

    @PostMapping("/payment/create/thanh-toan-hdDoiDVOnline")
    public ResponseEntity<String> createPaymentHDDV(@RequestHeader String idHoaDonDoiDV) {
        Optional<Hoadondoidichvu> hoadonOptional = hoaDonDoiDichVuService.findHoadondoidichvuById(Integer.parseInt(idHoaDonDoiDV));

        if (!hoadonOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại");
        }
        Hoadondoidichvu hoadon = hoadonOptional.get();
        try {
            String cancelUrl = "http://localhost:8080/api/payPal/payment/cancel/" + hoadon.getIdhoadon().getIdlichhen().getId();
            String successUrl = "http://localhost:8080/api/payPal/payment/success/hoadondoidv/"+hoadon.getIdhoadon().getIdlichhen().getId();
            Payment payment = payPalService.createPayment(
                    hoadon.getSotien(),
                    "USD",
                    "paypal",
                    "sale",
                    "Thanh toán hóa đơn ",
                    cancelUrl,
                    successUrl
            );
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    hoadon.setMagiaodich(payment.getId());
                    hoaDonDoiDichVuService.addOrUpdateHoadon(hoadon);
                    return ResponseEntity.ok(links.getHref());
                }
            }
        }catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể tạo thanh toán");
    }

    @GetMapping("/payment/success/{id}")
    public ResponseEntity<String> successPayment(@RequestParam("paymentId") String paymentId,
                                                 @RequestParam("PayerID") String payerId,
                                                 @PathVariable String id
                                                 ) {
        try {
            // Thực hiện thanh toán
            Payment payment = payPalService.executePayment(paymentId, payerId);
            Sale sale = new Sale();
            sale.setId(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
            // Kiểm tra nếu thanh toán thành công
            if (payment.getState().equals("approved")) {
                System.out.println("hello");
                // Lấy hóa đơn từ dịch vụ dựa trên Id
                Lichhen lichhen = lichHenService.findById(Integer.parseInt(id));
                Optional<Hoadon> hoadonOptional = hoaDonService.findHoaDonOnline(paymentId);
                if (hoadonOptional.isPresent()) {
                    Hoadon hoadon = hoadonOptional.get();
                    hoadon.setNguoithanhtoan(payerId);
                    // Cập nhật trạng thái của hóa đơn thành "đã thanh toán"
                    hoadon.setTrangthai(2);
                    hoadon.setPhuongthucthanhtoan("Online");
                    hoadon.setMagiaodich(sale.getId());
                    hoadon.setNgaythanhtoan(LocalDateTime.now());
                    hoaDonService.addOrUpdate(hoadon);
                    // Trả về phản hồi thành công
                    lichhen.setTrangthai(6);
                    lichHenService.addOrUpdate(lichhen);

//                    Gửi hóa đơn khi thanh toán thành công
                    // Tạo file PDF hóa đơn
                    String thoiGian = hoadon.getIdlichhen().getDate().toString()+ ' ' + hoadon.getIdlichhen().getIdcalichhen().getThoigianca();
                    String tenDichVu = hoadon.getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getDichvu().getTendichvu() + " -> " + hoadon.getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getTentuychon();
                    byte[] pdfBytes = pdfExportService.generateInvoice(hoadon.getNgaythanhtoan().toString(),hoadon.getMagiaodich(),hoadon.getPhuongthucthanhtoan(),tenDichVu,hoadon.getSotienbandau(),hoadon.getSotien(),thoiGian);

                    hoaDonService.sendHoaDonSauThanhToan(lichhen,pdfBytes);


                    // Redirect đến trang chi tiết lịch hẹn
                    String redirectUrl = "http://localhost:3000/chi-tiet-lich/" + id;
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(redirectUrl));
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                } else {
                    // Trả về lỗi nếu không tìm thấy hóa đơn
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy hóa đơn tương ứng.");
                }
            } else {
                // Trả về lỗi nếu thanh toán không thành công
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán không thành công.");
            }
        } catch (PayPalRESTException e) {
            // Ghi log lỗi và trả về phản hồi lỗi chi tiết
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý thanh toán.");
        }
    }
    @GetMapping("/payment/cancel/{id}")
    public ResponseEntity<?> cancelPayment(@PathVariable String id) {
        String redirectUrl = "http://localhost:3000/chi-tiet-lich/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/payment/success/hoadondoidv/{id}")
    public ResponseEntity<String> successHdDoi(@RequestParam("paymentId") String paymentId,
                                                 @RequestParam("PayerID") String payerId,
                                                 @PathVariable Integer id
    ) {
        try {
            // Thực hiện thanh toán
            Payment payment = payPalService.executePayment(paymentId, payerId);
            Sale sale = new Sale();
            sale.setId(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
            // Kiểm tra nếu thanh toán thành công
            if (payment.getState().equals("approved")) {
                System.out.println("xin chao");
                hoaDonDoiDichVuService.thanhToanHDDoiDV(id);
                Optional<Hoadondoidichvu> hoadondoidichvuOptional = hoaDonDoiDichVuService.findHoadondoidichvuById(id);
                if (!hoadondoidichvuOptional.isPresent()) {
                    return ResponseEntity.notFound().build();
                }
                Hoadondoidichvu hoadondoidichvu = hoadondoidichvuOptional.get();

                String thoiGian = hoadondoidichvu.getIdhoadon().getIdlichhen().getDate().toString()+ ' ' + hoadondoidichvu.getIdhoadon().getIdlichhen().getIdcalichhen().getThoigianca();
                String tenDichVu = hoadondoidichvu.getIdhoadon().getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getDichvu().getTendichvu() + " -> " + hoadondoidichvu.getIdhoadon().getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getTentuychon();
                byte[] pdfBytes = pdfExportService.generateInvoice(hoadondoidichvu.getIdhoadon().getNgaythanhtoan().toString(),hoadondoidichvu.getIdhoadon().getMagiaodich(),hoadondoidichvu.getIdhoadon().getPhuongthucthanhtoan(),tenDichVu,hoadondoidichvu.getIdhoadon().getSotienbandau(),hoadondoidichvu.getSotien(),thoiGian);

                hoaDonService.sendHoaDonSauThanhToan(hoadondoidichvu.getIdhoadon().getIdlichhen(),pdfBytes);

                String redirectUrl = "http://localhost:3000/chi-tiet-lich/" + hoadondoidichvu.getIdhoadon().getIdlichhen().getId();
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(redirectUrl));
                return ResponseEntity.ok().build();
            } else {
                // Trả về lỗi nếu thanh toán không thành công
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán không thành công.");
            }
        } catch (PayPalRESTException e) {
            // Ghi log lỗi và trả về phản hồi lỗi chi tiết
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý thanh toán.");
        }
    }
}
