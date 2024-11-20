package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.VietDanhGiaDTO;
import com.yellowcat.backend.model.Danhgia;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.service.DanhGiaService;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.LichHenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/danh-gia")
public class DanhGiaController {
    @Autowired
    DanhGiaService danhGiaService;

    @Autowired
    LichHenService lichHenService;

    @Autowired
    HoaDonService hoaDonService;

    @PostMapping("/add")
    public ResponseEntity<?> vietDanhGia(@Valid @RequestBody VietDanhGiaDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(dto.getIdLichHen());
        if (!hoadonOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Hoadon hoadon = hoadonOptional.get();
        Optional<Danhgia> danhgiaOptional = danhGiaService.getDanhGiaoTheoHD(hoadon.getId(),idUser);

//        Kiểm tra xem có phải chủ của cái lịch đã thành công ko
        if (!idUser.equalsIgnoreCase(hoadon.getIdlichhen().getIdkhachhang())){
            return ResponseEntity.notFound().build();
        }

        if (danhgiaOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("chỉ được viết 1 đánh giá cho 1 lần đặt thôi");
        }

//        Hóa đơn phải là : đã thanh toán và trạng thái lịch là thành công mới cho viết đánh giá
        if (hoadon.getTrangthai() == 2 && hoadon.getIdlichhen().getTrangthai() == 0){
            Danhgia danhgia = new Danhgia();
            danhgia.setDate(LocalDateTime.now());
            danhgia.setSosao(dto.getStar());
            danhgia.setMota(dto.getMoTa());
            danhgia.setIdtaikhoan(idUser);
            danhgia.setIdhoadon(hoadon);
            danhgia.setTrangthai(true);
            danhGiaService.addOrUpdateDanhGia(danhgia);
            return ResponseEntity.ok(danhgia);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("lỗi");
    }

    @GetMapping("/danh-gia-theo-dich-vu/{idDichVu}")
    public ResponseEntity<?> getDanhGiaTheoDichVu(@PathVariable Integer idDichVu){
        List<Danhgia> listDanhGia = danhGiaService.getAllDanhGiaTheoDichVu(idDichVu);
        return ResponseEntity.ok(listDanhGia);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDanhGia(@Valid @RequestBody VietDanhGiaDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();
        Optional<Danhgia> danhgiaOptional = danhGiaService.getDanhGiaById(dto.getId());
        if (!danhgiaOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Danhgia danhgia = danhgiaOptional.get();

        if (!idUser.equalsIgnoreCase(danhgia.getIdtaikhoan())){
            return ResponseEntity.badRequest().body("Phải là chủ mới được đổi ý");
        }

        danhgia.setTrangthai(false);
        danhGiaService.addOrUpdateDanhGia(danhgia);

        Danhgia danhgiaUpdate = new Danhgia();
        danhgiaUpdate.setTrangthai(true);
        danhgiaUpdate.setDate(LocalDateTime.now());
        danhgiaUpdate.setMota(dto.getMoTa());
        danhgiaUpdate.setSosao(dto.getStar());
        danhGiaService.addOrUpdateDanhGia(danhgiaUpdate);

        return ResponseEntity.ok(danhgiaUpdate);

    }

    @PutMapping("/an-danh-gia")
    public ResponseEntity<?> anDanhGia(@RequestBody Integer id){
       Optional<Danhgia> danhgiaOptional =  danhGiaService.getDanhGiaById(id);
       if (!danhgiaOptional.isPresent()){
           return ResponseEntity.notFound().build();
       }
       Danhgia danhgia = danhgiaOptional.get();
       danhgia.setTrangthai(false);
       danhGiaService.addOrUpdateDanhGia(danhgia);
       return ResponseEntity.ok(danhgia);
    }
}
