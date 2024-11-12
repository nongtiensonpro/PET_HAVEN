package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.repository.HoadonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HoaDonService {
    @Autowired
    HoadonRepository hoadonRepository;

    @Autowired
    DichVuService dichVuService;

    @Autowired
    GiamGiaService giamGiaService;

    public void addOrUpdate(Hoadon hoadon){hoadonRepository.save(hoadon);}

    public Float TinhGiaTien(Integer idDichVu){
        float giaDichVu = dichVuService.findById(idDichVu).get().getGiatien();
        Optional<Giamgia> giamgiaOptional = giamGiaService.findGiamGiaTheoNgayHienTai();
        float phanTramGiam;
        if(!giamgiaOptional.isPresent()){
            phanTramGiam = 0;
        }
        else {
            phanTramGiam = giamgiaOptional.get().getPhantramgiam();
        }
        float giaTien = giaDichVu - giaDichVu*phanTramGiam/100;
        return giaTien;
    }
}
