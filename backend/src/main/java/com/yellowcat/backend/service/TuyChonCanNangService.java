package com.yellowcat.backend.service;

import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.repository.TuyChonCanNangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TuyChonCanNangService {
    @Autowired
    TuyChonCanNangRepository tuyChonCanNangRepository;

    public Optional<TuyChonCanNang> findById(int id){
        return tuyChonCanNangRepository.findById(id);
    }
}
