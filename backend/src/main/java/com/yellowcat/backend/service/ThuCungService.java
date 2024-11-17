package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.repository.ThucungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThuCungService {
    @Autowired
    private ThucungRepository thucungRepository;

    public Thucung saveOrUpdate(Thucung thucung) {
        if (thucung.getTen() == null || thucung.getTen().isEmpty()) {
            throw new IllegalArgumentException("Tên thú cưng không được để trống");
        }

        if (thucung.getGiong() == null || thucung.getGiong().isEmpty()) {
            throw new IllegalArgumentException("Loại thú cưng không được để trống");
        }

        if (thucung.getCannang() == null || thucung.getCannang().isNaN()) {
            throw new IllegalArgumentException("Cân nặng phải là số và không được bỏ trống");
        }

        if (thucung.getTuoi() <= 0) {
            throw new IllegalArgumentException("Tuổi thú cưng phải lớn hơn 0");
        }

        return thucungRepository.save(thucung);
    }

    public List<Thucung> findListThuCungByidChu(String idTK) {return thucungRepository.findByIdtaikhoan(idTK);}
}
