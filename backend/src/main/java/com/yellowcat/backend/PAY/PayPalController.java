package com.yellowcat.backend.PAY;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.LichHenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/payPal")
public class PayPalController {
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private LichHenService lichHenService;

    @PostMapping("/payment/create")
    public ResponseEntity<String> createPayment(@RequestHeader String idLichHen) {
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(Integer.parseInt(idLichHen));
        Lichhen lichhen = lichHenService.findById(Integer.parseInt(idLichHen));
        if (!hoadonOptional.isPresent() || lichhen == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại");
        }
        Hoadon hoadon = hoadonOptional.get();
        try {
            String cancelUrl = "http://localhost:8080/api/payPal/payment/cancel";
            String successUrl = "http://localhost:8080/api/payPal/payment/success";
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
                    hoadon.setPhuongthucthanhtoan("Online");
                    hoadon.setMagiaodich(payment.getId());
                    hoaDonService.addOrUpdate(hoadon);

                    lichhen.setTrangthai(6);
                    lichHenService.addOrUpdate(lichhen);
                    return ResponseEntity.ok(links.getHref());
                }
            }
        }catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể tạo thanh toán");
    }

    @GetMapping("/payment/cancel")
    public String cancelPayment() {
        return "Thanh toán đã bị hủy!";
    }

    @GetMapping("/payment/success")
    public ResponseEntity<String> successPayment(@RequestParam("paymentId") String paymentId,
                                                 @RequestParam("PayerID") String payerId) {
        try {
            // Thực hiện thanh toán
            Payment payment = payPalService.executePayment(paymentId, payerId);

            // Kiểm tra nếu thanh toán thành công
            if (payment != null && payment.getState().equals("approved")) {
                System.out.println("hello");
                // Lấy hóa đơn từ dịch vụ dựa trên payerId
                Optional<Hoadon> hoadonOptional = hoaDonService.findHoaDonOnline(paymentId);
                if (hoadonOptional.isPresent()) {
                    Hoadon hoadon = hoadonOptional.get();
                    hoadon.setNguoithanhtoan(payerId);
                    // Cập nhật trạng thái của hóa đơn thành "đã thanh toán"
                    hoadon.setTrangthai(2);
                    hoaDonService.addOrUpdate(hoadon);

                    // Trả về phản hồi thành công
                    return ResponseEntity.ok().body("Thanh toán thành công!");
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

}
