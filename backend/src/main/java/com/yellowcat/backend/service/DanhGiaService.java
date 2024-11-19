package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Danhgia;
import com.yellowcat.backend.repository.DanhgiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DanhGiaService {
    @Autowired
    DanhgiaRepository DanhGiaRepository;

    public void addOrUpdateDanhGia(Danhgia danhgia) {
        DanhGiaRepository.save(danhgia);
    }

}
