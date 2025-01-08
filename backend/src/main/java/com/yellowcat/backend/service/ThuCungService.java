package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.repository.ThucungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThuCungService {

    private final ThucungRepository thucungRepository;

    public ThuCungService(ThucungRepository thucungRepository) {
        this.thucungRepository = thucungRepository;
    }

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

    public Page<Thucung> pageThuCung(Pageable pageable){
        return thucungRepository.findAll(pageable);
    }

    public Optional<Thucung> findThuCungById(int id) {
        return thucungRepository.findById(id);
    }
}
