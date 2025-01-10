package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.TuyChonDichVu;
import com.yellowcat.backend.model.Dichvu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TuyChonDichVuRepository extends JpaRepository<TuyChonDichVu, Integer> {
    void deleteByDichvu(Dichvu dichvu);
}