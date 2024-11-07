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
import java.time.LocalTime;
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
//        Check xem có lịch đã được đặt chưa
        if (lichhenOptional.get().getTrangthai()!=5){
            System.out.println("Lịch đã được đặt trước rồi , vui lòng chọn thời gian khác");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        Check không cho đặt ca trong quá khứ
        if (!caLichHenService.isCaAvailable(datLichDTO.getIdcalichhen(),datLichDTO.getDate())){
            System.out.println("Không được đặt ca trong quá khứ");
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

        Lichhen createLich = lichHenService.addOrUpdate(lichhen);

        lichHenService.sendEmailWithActions(createLich);
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

//    Đổi và hủy lịch

    @PutMapping("/huy-lich/{id}")
    public ResponseEntity<?> huyLichHen(@PathVariable Integer id) {
        Lichhen lichhen = lichHenService.findById(id);
        if (lichhen != null && lichhen.getTrangthai() == 4) {
            lichhen.setTrangthai(2); // Đặt trạng thái là "Đã hủy"
            if (lichhen.getTrangthaica()){
                lichhen.setTrangthaica(false);
            }else {
                return ResponseEntity.ok("Lỗi ca");
            }
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Lịch hẹn đã được hủy thành công.");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/thay-doi-thoi-gian/{id}")
    public ResponseEntity<?> thayDoiThoiGian(@PathVariable Integer id, @RequestBody DatLichDTO datLichDTO) {
        Lichhen lichhen = lichHenService.findById(id);
        if (lichhen != null && lichhen.getTrangthai() == 4) {
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Thời gian của lịch hẹn đã được cập nhật.");
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
