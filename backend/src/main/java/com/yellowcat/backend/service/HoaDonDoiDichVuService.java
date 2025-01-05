package com.yellowcat.backend.service;

import com.yellowcat.backend.DTO.DoiDichVuDTO;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Hoadondoidichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.repository.HoadondoidichvuRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class HoaDonDoiDichVuService {
    @Autowired
    HoadondoidichvuRepository hoadondoidichvuRepository;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    TuyChonCanNangService tuyChonCanNangService;
    @Autowired
    LichHenService lichHenService;

    public void addOrUpdateHoadon(Hoadondoidichvu hoadondoidichvu) {
         hoadondoidichvuRepository.save(hoadondoidichvu);
    }

    @Transactional
    public ResponseEntity<?> DoiDichVu(DoiDichVuDTO dto,String nguoiThanhToan){
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(dto.getIdLichHen());
        if (!hoadonOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Optional<TuyChonCanNang> tuyChonCanNangOptional = tuyChonCanNangService.findById(dto.getIdTuyChonCanNang());
        if (!tuyChonCanNangOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Optional<Lichhen> lichhenOptional = Optional.ofNullable(lichHenService.findById(dto.getIdLichHen()));
        if (!lichhenOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Lichhen lichhen = lichhenOptional.get();
        TuyChonCanNang tuyChonCanNang = tuyChonCanNangOptional.get();
        Hoadon hoadon = hoadonOptional.get();

        Float SoTienChenhLech = (float) (tuyChonCanNang.getGiatien() - hoadon.getIdlichhen().getTuyChonCanNang().getGiatien());
        boolean TaoHoaDonDoi = Float.compare(SoTienChenhLech, 0) == 0;


        if (!TaoHoaDonDoi){
            Hoadondoidichvu hoadondoidichvu = new Hoadondoidichvu();
            hoadondoidichvu.setIdhoadon(hoadon);
            if (dto.getGhiChu() != null && !dto.getGhiChu().isEmpty()){
                hoadondoidichvu.setGhichu(dto.getGhiChu());
            }
            String maGiaoDich = hoaDonService.MaGiaoDichRandom();
            hoadondoidichvu.setMagiaodich(maGiaoDich);
            hoadondoidichvu.setIdtuychoncannang(tuyChonCanNang);
            hoadondoidichvu.setSotien(Float.valueOf(Math.abs(SoTienChenhLech)));
            hoadondoidichvu.setNgaythanhtoan(LocalDate.now());
            hoadondoidichvu.setTrangthaithanhtoan(false);

            if (SoTienChenhLech <0){
                hoadondoidichvu.setTrangthai(2);
            }else {
                hoadondoidichvu.setTrangthai(1);
            }
            hoadondoidichvu.setNguoithanhtoan(nguoiThanhToan);
            hoadondoidichvuRepository.save(hoadondoidichvu);
            return ResponseEntity.ok(hoadondoidichvu);
        }else {
            lichhen.setTuyChonCanNang(tuyChonCanNang);
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok(hoadon);
        }
    }

    public Optional<Hoadondoidichvu> findHoadondoidichvuById(Integer id){
        return hoadondoidichvuRepository.findById(id);
    }

    public ResponseEntity<?> thanhToanHDDoiDV(Integer id){
        Optional<Hoadondoidichvu> hoadondoidichvuOptional = findHoadondoidichvuById(id);
        if (!hoadondoidichvuOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Hoadondoidichvu hoadondoidichvu = hoadondoidichvuOptional.get();
        hoadondoidichvu.setTrangthaithanhtoan(true);
        return ResponseEntity.ok(hoadondoidichvu);
    }
}
