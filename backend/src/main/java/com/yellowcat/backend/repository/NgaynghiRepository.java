package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Ngaynghi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NgaynghiRepository extends JpaRepository<Ngaynghi, Integer> {
    Optional<Ngaynghi> findByNgaynghiAndAndTrangthai(LocalDate date,boolean tt);

    List<Ngaynghi> findByTrangthaiTrue();

    List<Ngaynghi>findByTrangthaiTrueAndNgaynghi(LocalDate ngaynghi);


}