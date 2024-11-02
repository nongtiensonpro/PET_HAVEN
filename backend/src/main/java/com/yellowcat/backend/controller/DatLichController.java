package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.CaLichHenService;
import com.yellowcat.backend.service.DichVuService;
import com.yellowcat.backend.service.LichHenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/dat-lich")
public class DatLichController {
    @Autowired
    public LichHenService lichHenService;

    @Autowired
    private CaLichHenService caLichHenService;

    @Autowired
    private DichVuService dichVuService;

    @GetMapping("/dat-lich-info")
    public ResponseEntity<Map<String, Object>> getDatLichInfo(@RequestParam("ngay") LocalDate ngay) {
        Map<String, Object> response = new HashMap<>();

        // Lấy danh sách dịch vụ
        List<Dichvu> danhSachDichVu = dichVuService.getAll();
        response.put("dichVu", danhSachDichVu);

        // Lấy các ca có thể đặt
        List<Calichhen> CaLichHen = caLichHenService.getAllByDate(ngay);
        response.put("CaLichHen", CaLichHen);

        return ResponseEntity.ok(response);
    }

    // API tạo lịch hẹn khi khách hàng ấn nút xác nhận
    @PostMapping("/dat-lich")
    public ResponseEntity<Lichhen> createLichhen(@RequestBody Lichhen lichhen) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String idUser = authentication.getName(); // Đây là idUser
        String email = jwt.getClaimAsString("email");

        lichhen.setIdkhachhang(idUser);
        lichhen.setEmailNguoiDat(email);
        lichhen.setTrangthai(4);

//        check xem có đặt lịch trong quá khứ không
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        if (lichhen.getDate().isBefore(ChronoLocalDate.from(now))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Trả về lỗi nếu ngày hẹn nằm trong quá khứ
        }

        // Kiểm tra lịch hẹn có trùng ca trong ngày không
        if (lichHenService.isCaTrungTrongNgay(lichhen.getDate(), lichhen.getIdcalichhen().getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Trả về lỗi nếu trùng ca
        }

        Lichhen createLich = lichHenService.addOrUpdate(lichhen);
        return new ResponseEntity<>(createLich, HttpStatus.CREATED);
    }


}
