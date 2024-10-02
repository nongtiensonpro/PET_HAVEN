package com.yellowcat.backend.repository;


import com.yellowcat.backend.model.Dichvu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DichvuRepository extends JpaRepository<Dichvu, Long> {

    List<Dichvu> findByTendichvuContainingIgnoreCase(String tendichvu);
}