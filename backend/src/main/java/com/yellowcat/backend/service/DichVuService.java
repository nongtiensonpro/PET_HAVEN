package com.yellowcat.backend.service;

import com.yellowcat.backend.model.DichVu;
import com.yellowcat.backend.repository.DichvuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DichVuService {

    private final DichvuRepository dichvuRepository;

    public DichVuService(DichvuRepository dichvuRepository) {
        this.dichvuRepository = dichvuRepository;
    }


    public Page<DichVu> getAllDichVu(Pageable pageable) {
        return dichvuRepository.findAll(pageable);
    }

}
