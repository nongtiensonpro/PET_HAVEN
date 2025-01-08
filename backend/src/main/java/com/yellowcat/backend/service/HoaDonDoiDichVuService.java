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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        float SoTienChenhLech = (float) (tuyChonCanNang.getGiatien() - hoadon.getIdlichhen().getTuyChonCanNang().getGiatien());
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
            hoadondoidichvu.setSotien(Double.valueOf(Math.abs(SoTienChenhLech)));
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

    public ResponseEntity<?> thanhToanHDDoiDV(Integer id) throws PayPalRESTException {
        Optional<Hoadondoidichvu> hoadondoidichvuOptional = findHoadondoidichvuById(id);
        if (!hoadondoidichvuOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Hoadondoidichvu hoadondoidichvu = hoadondoidichvuOptional.get();

        if (hoadondoidichvu.getTrangthai() == 1){ // hoàn tiền nếu trạng thái là 1 (hoàn tiền)
            Refund refund = payPalService.refundPayment(hoadondoidichvu.getIdhoadon().getMagiaodich(), hoadondoidichvu.getSotien(), "USD");
            System.out.println(refund);
            if ("completed".equals(refund.getState())) {
                hoadondoidichvu.setTrangthaithanhtoan(true);
                hoadondoidichvuRepository.save(hoadondoidichvu);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Hoàn tiền thất bại: " + refund.getReason());
            }
        }else {
            hoadondoidichvu.setTrangthaithanhtoan(true);
            hoadondoidichvuRepository.save(hoadondoidichvu);
        }
        return ResponseEntity.ok(hoadondoidichvu);
    }
}
