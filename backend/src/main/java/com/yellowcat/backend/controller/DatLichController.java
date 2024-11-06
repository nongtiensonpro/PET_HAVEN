package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.DatLichDTO;
import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dat-lich")
public class DatLichController {
    @Autowired
    public LichHenService lichHenService;

    @Autowired
    private CaLichHenService caLichHenService;

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ThuCungService thuCungService;

    @GetMapping("/dat-lich-info")
    public ResponseEntity<Map<String, Object>> getDatLichInfo(@RequestParam("ngay") LocalDate ngay) {
        Map<String, Object> response = new HashMap<>();

        // Lấy danh sách dịch vụ
        List<Dichvu> danhSachDichVu = dichVuService.getListTrangThaiTrue();
        response.put("dichVu", danhSachDichVu);

        // Lấy các ca có thể đặt
        List<Calichhen> CaLichHen = caLichHenService.getAllByDate(ngay);
        response.put("CaLichHen", CaLichHen);

        return ResponseEntity.ok(response);
    }

    // API tạo lịch hẹn khi khách hàng ấn nút xác nhận
    @PutMapping("/xac-nhan-dat")
    public ResponseEntity<Lichhen> createLichhen(
           @Valid @RequestBody DatLichDTO datLichDTO ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String idUser = authentication.getName(); // Đây là idUser
        String email = jwt.getClaimAsString("email");
        Optional<Lichhen> lichhenOptional = lichHenService.getLichHenByDateandCa(datLichDTO.getDate(),datLichDTO.getIdcalichhen());

        Thucung thucung = datLichDTO.getIdThuCung();
        thucung.setIdtaikhoan(idUser);
        thuCungService.saveOrUpdate(thucung);

        if (!lichhenOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Lichhen lichhen = lichhenOptional.get();
        lichhen.setIdkhachhang(idUser);
        lichhen.setEmailNguoiDat(email);
        lichhen.setTrangthai(4);
        lichhen.setThucung(thucung);
        lichhen.setDate(datLichDTO.getDate());
        if(!lichhen.getTrangthaica()){
            lichhen.setTrangthaica(true);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

//        check xem có đặt lịch trong quá khứ không
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        if (lichhen.getDate().isBefore(ChronoLocalDate.from(now))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Trả về lỗi nếu ngày hẹn nằm trong quá khứ
        }

        Lichhen createLich = lichHenService.addOrUpdate(lichhen);

        sendEmailWithActions(createLich);
        scheduleTrangThaiChange(createLich.getId());

        return new ResponseEntity<>(createLich, HttpStatus.CREATED);
    }

    // Sau 20p tự động đổi trạng thái thành chờ thanh toán
    @Async
    public void scheduleTrangThaiChange(Integer lichhenId) {
        try {
            Thread.sleep(20 * 60 * 1000); // Đợi 20 phút
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Sau 20 phút, kiểm tra và cập nhật trạng thái
        Lichhen lichhen = lichHenService.findById(lichhenId);
        if (lichhen != null && lichhen.getTrangthai() == 4) { // Kiểm tra trạng thái hiện tại vẫn là 4
            lichhen.setTrangthai(3); // Cập nhật sang trạng thái 3 (Chờ thanh toán)
            lichHenService.addOrUpdate(lichhen);
            System.out.println("Đã cập nhật trạng thái của lịch hẹn ID: " + lichhenId + " thành 3 (Chờ thanh toán)");
        }
    }

    private void sendEmailWithActions(Lichhen lichhen) {
        try {
            String cancelUrl = "http://localhost:3000/thay-doi-lich/" + lichhen.getId();
            String Url = "http://localhost:3000/chi-tiet-lich/" + lichhen.getId();

            String message = "Chào bạn,\n\n"
                    + "Cảm ơn bạn đã đặt lịch. Bạn có thể sử dụng các liên kết dưới đây để quản lý lịch hẹn của mình:\n\n"
                    + "Hủy hoặc thay đổi thời gian lịch: " + cancelUrl + "\n"
                    + "Chi tiết lịch hẹn: " + Url + "\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ hỗ trợ";

            emailService.sendEmail(lichhen.getEmailNguoiDat(),"Hủy hoặc đổi thời gian lịch",message);
            System.out.println("Email đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

//    Đổi và hủy lịch

    @PostMapping("/huy-lich/{id}")
    public ResponseEntity<?> huyLichHen(@PathVariable Integer id) {
        Lichhen lichhen = lichHenService.findById(id);
        if (lichhen != null && lichhen.getTrangthai() == 4) {
            lichhen.setTrangthai(2); // Đặt trạng thái là "Đã hủy"
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Lịch hẹn đã được hủy thành công.");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/thay-doi-thoi-gian/{id}")
    public ResponseEntity<?> thayDoiThoiGian(@PathVariable Integer id, @RequestBody LocalDate newDate) {
        Lichhen lichhen = lichHenService.findById(id);
        if (lichhen != null && lichhen.getTrangthai() == 4) {
            lichhen.setDate(newDate);
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Thời gian của lịch hẹn đã được cập nhật.");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
