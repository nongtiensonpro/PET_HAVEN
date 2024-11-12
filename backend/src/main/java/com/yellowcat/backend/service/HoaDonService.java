package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.repository.HoadonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoaDonService {
    @Autowired
    HoadonRepository hoadonRepository;

    @Autowired
    DichVuService dichVuService;

    @Autowired
    GiamGiaService giamGiaService;

    public void addOrUpdate(Hoadon hoadon){hoadonRepository.save(hoadon);}

    public void TinhGiaTien(Integer idDichVu,int idGiamGia){

    }
}
