package com.yellowcat.backend.service;

import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.DTO.DoiDichVuDTO;
import com.yellowcat.backend.PAY.PayPalService;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Hoadondoidichvu;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.repository.HoadondoidichvuRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class HoaDonDoiDichVuService {

    private final HoadondoidichvuRepository hoadondoidichvuRepository;
    private final HoaDonService hoaDonService;
    private final TuyChonCanNangService tuyChonCanNangService;
    private final LichHenService lichHenService;
    private final PayPalService  payPalService;

    public HoaDonDoiDichVuService(HoadondoidichvuRepository hoadondoidichvuRepository, HoaDonService hoaDonService, TuyChonCanNangService tuyChonCanNangService, LichHenService lichHenService, PayPalService payPalService) {
        this.hoadondoidichvuRepository = hoadondoidichvuRepository;
        this.hoaDonService = hoaDonService;
        this.tuyChonCanNangService = tuyChonCanNangService;
        this.lichHenService = lichHenService;
        this.payPalService = payPalService;
    }

    public void addOrUpdateHoadon(Hoadondoidichvu hoadondoidichvu) {
         hoadondoidichvuRepository.save(hoadondoidichvu);
    }


    @Transactional
    public Hoadondoidichvu DoiDichVu(DoiDichVuDTO dto,String nguoiThanhToan){
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(dto.getIdLichHen());
        if (!hoadonOptional.isPresent()){
            return null;
        }
        Optional<TuyChonCanNang> tuyChonCanNangOptional = tuyChonCanNangService.findById(dto.getIdTuyChonCanNang());
        if (!tuyChonCanNangOptional.isPresent()){
            return null;
        }
        TuyChonCanNang tuyChonCanNang = tuyChonCanNangOptional.get();
        Hoadon hoadon = hoadonOptional.get();

        float SoTienChenhLech = (float) (tuyChonCanNang.getGiatien() - hoadon.getIdlichhen().getTuyChonCanNang().getGiatien());
        System.out.println(SoTienChenhLech);

            Hoadondoidichvu hoadondoidichvu = new Hoadondoidichvu();
            hoadondoidichvu.setIdhoadon(hoadon);
            if (dto.getGhiChu() != null && !dto.getGhiChu().isEmpty()){
                hoadondoidichvu.setGhichu(dto.getGhiChu());
            }
            String maGiaoDich = hoaDonService.MaGiaoDichRandom();
            hoadondoidichvu.setMagiaodich(maGiaoDich);
            hoadondoidichvu.setIdtuychoncannang(tuyChonCanNang);
            hoadondoidichvu.setSotien(Double.valueOf(Math.abs(SoTienChenhLech)));
            hoadondoidichvu.setNgaythanhtoan(LocalDateTime.now());
            hoadondoidichvu.setTrangthaithanhtoan(false);
            hoadondoidichvu.setNgaytao(LocalDate.now());
            hoadondoidichvu.setGiadichvudoi(tuyChonCanNang.getGiatien());

            if (SoTienChenhLech <0){
                hoadondoidichvu.setTrangthai(1);
            }else {
                hoadondoidichvu.setTrangthai(2);
            }
            hoadondoidichvu.setNguoithanhtoan(nguoiThanhToan);
            hoadondoidichvuRepository.save(hoadondoidichvu);
            return hoadondoidichvu;
    }

    public Optional<Hoadondoidichvu> findHoadondoidichvuById(Integer id){
        return hoadondoidichvuRepository.findById(id);
    }

    public ResponseEntity<?> thanhToanHDDoiDV(Integer id) throws PayPalRESTException {
        Optional<Hoadondoidichvu> hoadondoidichvuOptional = findHoadondoidichvuById(id);
        if (!hoadondoidichvuOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Hoadondoidichvu hoadondoidichvu = hoadondoidichvuOptional.get();
        if (hoadondoidichvu.getTrangthaithanhtoan()){
            return ResponseEntity.notFound().build();
        }
        Optional<Lichhen> lichhenOptional = Optional.ofNullable(lichHenService.findById(hoadondoidichvu.getIdhoadon().getIdlichhen().getId()));
        if (!lichhenOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Lichhen lichhen = lichhenOptional.get();

        if (hoadondoidichvu.getTrangthai() == 1){ // hoàn tiền nếu trạng thái là 1 (hoàn tiền)
            Refund refund = payPalService.refundPayment(hoadondoidichvu.getIdhoadon().getMagiaodich(), hoadondoidichvu.getSotien(), "USD");
            System.out.println(refund);
            if ("completed".equals(refund.getState())) {
                hoadondoidichvu.setTrangthaithanhtoan(true);
                hoadondoidichvu.setNgaythanhtoan(LocalDateTime.now());
                lichhen.setTuyChonCanNang(hoadondoidichvu.getIdtuychoncannang());
                lichHenService.addOrUpdate(lichhen);
                hoadondoidichvuRepository.save(hoadondoidichvu);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Hoàn tiền thất bại: " + refund.getReason());
            }
        }else {
            hoadondoidichvu.setTrangthaithanhtoan(true);
            hoadondoidichvu.setNgaythanhtoan(LocalDateTime.now());
            lichhen.setTuyChonCanNang(hoadondoidichvu.getIdtuychoncannang());
            lichHenService.addOrUpdate(lichhen);
            hoadondoidichvuRepository.save(hoadondoidichvu);
        }
        return ResponseEntity.ok(hoadondoidichvu);
    }
}
