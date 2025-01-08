package com.yellowcat.backend.service;

import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.repository.TuyChonCanNangRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TuyChonCanNangService {

    private final TuyChonCanNangRepository tuyChonCanNangRepository;

    public TuyChonCanNangService(TuyChonCanNangRepository tuyChonCanNangRepository) {
        this.tuyChonCanNangRepository = tuyChonCanNangRepository;
    }

    public Optional<TuyChonCanNang> findById(int id){
        return tuyChonCanNangRepository.findById(id);
    }
}