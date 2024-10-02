package com.yellowcat.backend.service;


import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.repository.DichvuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DichVuService {

    private final DichvuRepository dichvuRepository;

    public DichVuService(DichvuRepository dichvuRepository) {
        this.dichvuRepository = dichvuRepository;
    }


    public Page<Dichvu> getAllDichVu(Pageable pageable) {
        return dichvuRepository.findAll(pageable);
    }

    public Dichvu addOrUpdateDichVu(Dichvu dichvu) {
        return dichvuRepository.save(dichvu);
    }
    public Optional<Dichvu> findById(int id) {
        return dichvuRepository.findById((long) id);
    }

    public void deleteDichVu(int id) {
        dichvuRepository.deleteById((long) id);
    }

    public List<Dichvu> FindByNameDV(String tenDV){
        return dichvuRepository.findByTendichvuContainingIgnoreCase(tenDV);
    }
}