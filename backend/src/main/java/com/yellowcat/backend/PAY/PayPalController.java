package com.yellowcat.backend.PAY;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/payPal")
public class PayPalController {
    @Autowired
    private PayPalService payPalService;

    @PostMapping("/payment/create")
    public ResponseEntity<String> createPayment() {
        try {
            String cancelUrl = "http://localhost:8080/api/payPal/payment/cancel";
            String successUrl = "http://localhost:8080/api/payPal/payment/success";
            Payment payment = payPalService.createPayment(
                    10.0,
                    "USD",
                    "paypal",
                    "sale",
                    "hello em iu",
                    cancelUrl,
                    successUrl
            );
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
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
    public String successPayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return "Thanh toán thành công!";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "Thanh toán thất bại!";
    }

}
