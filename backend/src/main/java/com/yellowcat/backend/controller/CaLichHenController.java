package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.NgayNghiDTO;
import com.yellowcat.backend.DTO.updateCaDTO;
import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.model.Ngaynghi;
import com.yellowcat.backend.service.CaLichHenService;
import com.yellowcat.backend.service.LichHenService;
import com.yellowcat.backend.service.NgayNghiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyRole('admin')")
@RequestMapping(("/api/ca-lich-hen"))
public class CaLichHenController {



    private final CaLichHenService caLichHenService;
    private final LichHenService lichHenService;
    private final NgayNghiService ngayNghiService;

    public CaLichHenController(CaLichHenService caLichHenService, LichHenService lichHenService, NgayNghiService ngayNghiService) {
        this.caLichHenService = caLichHenService;
        this.lichHenService = lichHenService;
        this.ngayNghiService = ngayNghiService;
    }

//-----------------------------------------------------------
    @GetMapping("/all")
    public List<Calichhen> getList(){
        List<Calichhen> listCa = caLichHenService.findAll();
        return listCa;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOne(@Valid @RequestBody updateCaDTO dto){
        Calichhen calichhen = new Calichhen();
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
        caLichHenService.addOrUpdate(calichhenUpdate);
        return ResponseEntity.ok().build();
    }
//-----------------------------------------------------------

    @PutMapping("/cap-nhap-ngay-nghi")
    public ResponseEntity<?> falseAllCa(@RequestBody NgayNghiDTO ngay) {
        System.out.println(ngay);
        List<Lichhen> listLich = lichHenService.getListByDate(ngay.getDate());
        List<Lichhen> listLicDaDat = new ArrayList<>();
        Optional<Ngaynghi> ngaynghiOptional = ngayNghiService.getNgaynghi(ngay.getDate());
        if(ngaynghiOptional.isPresent()){
            return ResponseEntity.badRequest().body("Ngày nghỉ đã tồn tại");
        }

        // Kiểm tra từng lịch hẹn
        for (Lichhen lichhen : listLich) {
            boolean isInvalid = lichhen.getTrangthai() != 5 && lichhen.getTrangthaica();
            if (isInvalid) {
                listLicDaDat.add(lichhen);
            }
        }

        // Nếu có lịch đã được đặt (không hợp lệ), trả về danh sách đó
        if (!listLicDaDat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "message", "Không thể cập nhật ngày nghỉ vì có lịch đã được đặt.",
                            "lichDaDat", listLicDaDat
                    ));
        }

        // Nếu không có lịch vi phạm, tiến hành cập nhật ngày nghỉ
        Ngaynghi ngaynghi = new Ngaynghi();
        ngaynghi.setNgaynghi(ngay.getDate());
        ngaynghi.setTrangthai(true);
        ngayNghiService.addOrUpdate(ngaynghi);

        // Cập nhật trạng thái ca làm việc
//        lichHenService.ThemNgayNghi(ngay.getDate());

        return ResponseEntity.ok(Map.of(
                "message", "Ngày nghỉ đã được cập nhật thành công.",
                "lichHen", listLich
        ));
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
