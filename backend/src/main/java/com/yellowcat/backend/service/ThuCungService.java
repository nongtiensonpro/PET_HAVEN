package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.repository.ThucungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThuCungService {
    @Autowired
    private ThucungRepository thucungRepository;

    public Thucung saveOrUpdate(Thucung thucung) {
        if (thucung.getId() != null && thucungRepository.existsById(thucung.getId())) {
            // Nếu đã tồn tại, cập nhật thông tin
            return thucungRepository.save(thucung);
        } else {
            // Nếu không tồn tại, thêm mới
            return thucungRepository.save(thucung);
        }
    }
}
