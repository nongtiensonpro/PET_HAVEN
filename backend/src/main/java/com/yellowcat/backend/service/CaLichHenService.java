package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.repository.CalichhenRepository;
import com.yellowcat.backend.repository.LichhenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class CaLichHenService {
    @Autowired
    private CalichhenRepository caLichHenRepository;

    @Autowired
    private LichhenRepository lichHenRepository;

    @Transactional
    public void resetTrangThaiCa(LocalDate ngay) {
        // Bước 1: Đặt tất cả các ca về false
        caLichHenRepository.updateTrangThaiAll(false);

        // Bước 2: Đặt những ca đã được đặt trong ngày đó về true
        caLichHenRepository.updateTrangThaiCaDatThanhCong(ngay);
    }

    public void UpdateNgayNghi(LocalDate ngay){
        caLichHenRepository.updateNgayNghi(ngay);
    }

    public void addOrUpdate(Calichhen calichhen){
        caLichHenRepository.save(calichhen);
    }

    public void DoiTrangthaiCaTrongNgay(int idCaLichHen, LocalDate ngay, boolean trangThai){
        caLichHenRepository.updateTrangThaiCaTrongNgay(idCaLichHen, ngay, trangThai);
    }

    public List<?> getAllByDate(LocalDate date){
        return caLichHenRepository.findAllCaAndStatusByDate(date);
    }



}
