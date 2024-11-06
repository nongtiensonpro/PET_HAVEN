package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.repository.CalichhenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class CaLichHenService {
    @Autowired
    private CalichhenRepository caLichHenRepository;


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

    public Optional<Calichhen> findById (Integer id){
        return caLichHenRepository.findById(id)
    ;}

    public void DoiTrangthaiCaTrongNgay(int idCaLichHen, LocalDate ngay, boolean trangThai){
        caLichHenRepository.updateTrangThaiCaTrongNgay(idCaLichHen, ngay, trangThai);
    }

    public List<Calichhen> getAllByDate(LocalDate date){
        return caLichHenRepository.findAllCaAndStatusByDateaAndTrangthaiFalse(date);
    }

    public List<Calichhen> findAll(){
        return caLichHenRepository.findAll();
    }

}
