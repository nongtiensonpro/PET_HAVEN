package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.repository.HoadonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HoaDonService {
    @Autowired
    HoadonRepository hoadonRepository;

    @Autowired
    DichVuService dichVuService;

    @Autowired
    GiamGiaService giamGiaService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 17;
    private static final SecureRandom RANDOM = new SecureRandom();

    public void addOrUpdate(Hoadon hoadon){hoadonRepository.save(hoadon);}

    public Double TinhGiaTien(Integer idDichVu,Hoadon hoadon){
        float giaDichVu = dichVuService.findById(idDichVu).get().getGiatien();
        Optional<Giamgia> giamgiaOptional = giamGiaService.findGiamGiaTheoNgayHienTai();
        Giamgia giamgia = giamgiaOptional.get();
        float phanTramGiam;
        if(!giamgiaOptional.isPresent()){
            phanTramGiam = 0;
        }
        else {
            phanTramGiam = giamgiaOptional.get().getPhantramgiam();
        }
        Double giaTien = (double) (giaDichVu - giaDichVu*phanTramGiam/100);
        hoadon.setIdgiamgia(giamgia);
        return giaTien;
    }
    public List<Hoadon> getAllHoaDonChuaThanhToan(int idTT){
        return hoadonRepository.findByTrangthai(idTT);
    }

    public List<Hoadon> getALl(){return hoadonRepository.findAll();}

    public Optional<Hoadon> findById(int id){return hoadonRepository.findById(id);}

    public List<Hoadon> LichSuThanhToanHoaDonTheoTaiKhoan(String email){return hoadonRepository.findByNguoithanhtoanAndPhuongthucthanhtoan(email,"Offline");}

    public Optional<Hoadon> finHoadonByIdLich(Integer id){return hoadonRepository.findByIdlichhen_IdAndTrangthai(id,1);}

    public  Optional<Hoadon> findHoaDonOnline(String idPayPal)
    {return hoadonRepository.findByMagiaodich(idPayPal);}

    public static String MaGiaoDichRandom() {
        StringBuilder transactionId = new StringBuilder(LENGTH);

        // Thêm thời gian (milliseconds) vào mã giao dịch để tạo tính duy nhất
        transactionId.append(new Date().getTime());  // Thời gian hiện tại tính bằng milliseconds

        // Thêm phần ngẫu nhiên còn lại để hoàn thiện mã
        for (int i = 0; i < LENGTH - 13; i++) {  // Đảm bảo tổng độ dài là 17 ký tự
            transactionId.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return transactionId.toString();  // Trả về mã giao dịch duy nhất
    }
    public void deleteHoadonById(Integer id){
        hoadonRepository.deleteById(id);
    }
}
