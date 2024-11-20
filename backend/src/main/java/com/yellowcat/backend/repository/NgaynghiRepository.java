package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Ngaynghi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface NgaynghiRepository extends JpaRepository<Ngaynghi, Integer> {
    Optional<Ngaynghi> findByNgaynghi(LocalDate date);
}