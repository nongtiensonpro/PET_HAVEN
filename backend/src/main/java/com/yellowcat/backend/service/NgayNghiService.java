package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Ngaynghi;
import com.yellowcat.backend.repository.NgaynghiRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NgayNghiService {
    @Autowired
    NgaynghiRepository ngaynghiRepository;

    public void addOrUpdate(Ngaynghi ngaynghi) {
        ngaynghiRepository.save(ngaynghi);
    }

    public Optional<Ngaynghi> getNgaynghi(LocalDate date) {
        return ngaynghiRepository.findByNgaynghi(date);
    }

    public List<Ngaynghi> getAllNgaynghi() {
        return ngaynghiRepository.findByTrangthaiTrue();
    }
}
