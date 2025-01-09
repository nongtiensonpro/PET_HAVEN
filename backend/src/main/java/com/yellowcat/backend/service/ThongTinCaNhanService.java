package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Thongtincanhan;
import com.yellowcat.backend.repository.ThongtincanhanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThongTinCaNhanService {

    private final ThongtincanhanRepository thongtincanhanRepository;

    public ThongTinCaNhanService(ThongtincanhanRepository thongtincanhanRepository) {
        this.thongtincanhanRepository = thongtincanhanRepository;
    }

    public void addOrUpdate(Thongtincanhan thongtincanhan) {
        thongtincanhanRepository.save(thongtincanhan);
    }

    public Optional<Thongtincanhan> getThongtincanhanByIdTaiKhoan(String id) {
        return thongtincanhanRepository.findByIdtaikhoan(id);
    }

    public List<Thongtincanhan> getAllThongtincanhan() {
        return thongtincanhanRepository.findAll();
    }
}
