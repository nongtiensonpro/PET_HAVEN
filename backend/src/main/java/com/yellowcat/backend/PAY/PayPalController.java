package com.yellowcat.backend.PAY;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.DTO.DoiDichVuDTO;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Hoadondoidichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    EmailService emailService;

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
    public ResponseEntity<?> createPaymentHDDV(@RequestHeader String idHoaDonDoiDV) throws PayPalRESTException {
        Optional<Hoadondoidichvu> hoadonOptional = hoaDonDoiDichVuService.findHoadondoidichvuById(Integer.parseInt(idHoaDonDoiDV));

        if (!hoadonOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại");
        }
        Hoadondoidichvu hoadon = hoadonOptional.get();
        if (hoadon.getTrangthai() == 1){
            ResponseEntity<?> hoadondoidichvu = hoaDonDoiDichVuService.thanhToanHDDoiDV(Integer.valueOf(idHoaDonDoiDV));
            return hoadondoidichvu;
        }
        if (hoadon.getTrangthaithanhtoan()){
            return ResponseEntity.badRequest().build();
        }
        if (hoadon.getSotien() == 0){
            hoaDonDoiDichVuService.thanhToanHDDoiDV(Integer.valueOf(idHoaDonDoiDV));
        }
        try {
            String cancelUrl = "http://localhost:8080/api/payPal/payment/cancel/" + hoadon.getIdhoadon().getIdlichhen().getId();
            String successUrl = "http://localhost:8080/api/payPal/payment/success/hoadondoidv/"+hoadon.getId();
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

                    String emaiKhach = hoadon.getIdhoadon().getIdlichhen().getEmailNguoiDat();
                    String linkTT = links.getHref();
                    emailService.sendEmail(emaiKhach, "Thanh toán hóa đơn", "Vui lòng thanh toán tại link sau để đổi dịch vụ: " + linkTT);
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
                    Timestamp time = Timestamp.valueOf(hoadon.getNgaythanhtoan());
                    String ngayThanhToan = convertTimestamp(time);
                    String giamGia;
                    if (hoadon.getIdgiamgia() == null || hoadon.getIdgiamgia().getPhantramgiam() == null) {
                        giamGia = "Không";
                    } else {
                        giamGia = hoadon.getIdgiamgia().getPhantramgiam().toString() + "%";
                    }
                    String tenKhach = hoadon.getIdlichhen().getEmailNguoiDat();
                    String soTien = hoadon.getSotien() + "USD";
                    String soTienDau = hoadon.getSotienbandau() + "USD";
                    // Định dạng ngày giờ
                    byte[] pdfBytes = pdfExportService.generateInvoice(ngayThanhToan,hoadon.getMagiaodich(),hoadon.getPhuongthucthanhtoan(),giamGia,tenDichVu,soTienDau,soTien,thoiGian,tenKhach);

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
                Optional<Hoadondoidichvu> hoadondoidichvuOptional = hoaDonDoiDichVuService.findHoadondoidichvuById(id);
                if (!hoadondoidichvuOptional.isPresent()) {
                    return ResponseEntity.notFound().build();
                }

                Hoadondoidichvu hoadon = hoadondoidichvuOptional.get();
                hoaDonDoiDichVuService.thanhToanHDDoiDV(id);
                Timestamp time = Timestamp.valueOf(hoadon.getNgaythanhtoan());
                String ngayThanhToan = convertTimestamp(time);
                String thoiGian = hoadon.getIdhoadon().getIdlichhen().getDate().toString()+ ' ' + hoadon.getIdhoadon().getIdlichhen().getIdcalichhen().getThoigianca();
                String tenDichVu = hoadon.getIdhoadon().getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getDichvu().getTendichvu() + " -> " + hoadon.getIdhoadon().getIdlichhen().getTuyChonCanNang().getTuyChonDichVu().getTentuychon();
                String tenDichVuDoi = hoadon.getIdtuychoncannang().getTuyChonDichVu().getDichvu().getTendichvu() + " -> " + hoadon.getIdtuychoncannang().getTuyChonDichVu().getTentuychon();
                String tenKhach = hoadon.getIdhoadon().getIdlichhen().getEmailNguoiDat();
                String giaDVDau = hoadon.getIdhoadon().getSotienbandau() + "USD";
                String soTienTTDVDau = hoadon.getIdhoadon().getSotien() + "USD";
                String giaDVSau = hoadon.getGiadichvudoi() + "USD";
                byte[] pdfBytes = pdfExportService.genHdDoiDV(
                        giaDVDau,
                        tenKhach,
                        soTienTTDVDau,
                        giaDVSau,
                        ngayThanhToan
                        ,hoadon.getMagiaodich()
                        ,hoadon.getIdhoadon().getPhuongthucthanhtoan()
                        ,tenDichVu
                        ,tenDichVuDoi
                        ,hoadon.getSotien().toString()
                        ,thoiGian);

                hoaDonService.sendHoaDonSauThanhToan(hoadon.getIdhoadon().getIdlichhen(),pdfBytes);

                String redirectUrl = "http://localhost:3000/";
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(redirectUrl))
                        .build();
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
    public static String convertTimestamp(Timestamp timestamp) {
        // Chuyển đổi Timestamp sang LocalDateTime
        LocalDateTime dateTime = timestamp.toLocalDateTime();

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Chuyển LocalDateTime sang chuỗi định dạng
        return dateTime.format(formatter);
    }
}
