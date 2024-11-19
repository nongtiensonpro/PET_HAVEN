package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.updateCaDTO;
import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.CaLichHenService;
import com.yellowcat.backend.service.LichHenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyRole('admin')")
@RequestMapping(("/api/ca-lich-hen"))
public class CaLichHenController {

    @Autowired
    private CaLichHenService caLichHenService;
    @Autowired
    private LichHenService lichHenService;

//-----------------------------------------------------------
    @GetMapping("/all")
    public List<Calichhen> getList(){
        List<Calichhen> listCa = caLichHenService.findAll();
        return listCa;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOne(@Valid @RequestBody updateCaDTO dto){
        Calichhen calichhen = new Calichhen();
        calichhen.setTenca(dto.getName());
        LocalTime time = LocalTime.parse(dto.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
        calichhen.setThoigianca(time);
        calichhen.setTrangthai(true);
        caLichHenService.addOrUpdate(calichhen);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOne(@Valid @RequestBody updateCaDTO dto){
        Optional<Calichhen> calichhenOptional = caLichHenService.findById(dto.getId());
        if(!calichhenOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Calichhen calichhenUpdate = calichhenOptional.get();
        LocalTime time = LocalTime.parse(dto.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
        calichhenUpdate.setThoigianca(time);
        calichhenUpdate.setTenca(dto.getName());
        caLichHenService.addOrUpdate(calichhenUpdate);
        return ResponseEntity.ok().build();
    }
//-----------------------------------------------------------

    @PutMapping("/cap-nhap-ngay-nghi")
    public ResponseEntity<Void> falseAllCa(@RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        caLichHenService.UpdateNgayNghi(ngay);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/cap-nhap-trang-thai-ca")
    public ResponseEntity<?> capNhapTrangThaiCa(@RequestBody Map<String, Object> payload) {
        int idCaLichHen = (int) payload.get("idCaLichHen");
        boolean isConfirmed = payload.containsKey("isConfirmed") && (boolean) payload.get("isConfirmed");

        Optional<Calichhen> calichhenOptional = caLichHenService.findById(idCaLichHen);
        if (!calichhenOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Calichhen calichhen = calichhenOptional.get();
        List<Lichhen> listLich = lichHenService.getLichhenByDateAndIdCa(idCaLichHen);
        List<Lichhen> lichDaDat = new ArrayList<>();

        // Kiểm tra trạng thái ban đầu của ca
        Boolean trangThai = calichhen.getTrangthai();

        // Nếu chưa được xác nhận và có lịch trùng đã đặt, trả về danh sách để xác nhận
        if (!isConfirmed && trangThai) {
            for (Lichhen l : listLich) {
                if (l.getTrangthaica()) {
                    lichDaDat.add(l);
                }
            }
            if (!lichDaDat.isEmpty()) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                        Map.of(
                                "message", "Có các lịch hẹn đã được đặt trùng với ca này. Bạn có chắc chắn muốn đổi trạng thái không?",
                                "lichDaDat", lichDaDat
                        )
                );
            }
        }

        // Thay đổi trạng thái của ca
        trangThai = !trangThai;
        calichhen.setTrangthai(trangThai);
        caLichHenService.addOrUpdate(calichhen);

        // Cập nhật trạng thái các lịch liên quan
        if (!trangThai) {
            for (Lichhen l : listLich) {
                if (!l.getTrangthaica() && l.getTrangthai() == 5) {
                    l.setTrangthaica(true);
                    lichHenService.addOrUpdate(l);
                }
            }
        } else {
            for (Lichhen l : listLich) {
                if (l.getTrangthaica() && l.getTrangthai() == 5) {
                    l.setTrangthaica(false);
                    lichHenService.addOrUpdate(l);
                }
            }
        }

        return ResponseEntity.ok(calichhen);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
