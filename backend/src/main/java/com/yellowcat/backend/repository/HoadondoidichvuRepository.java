package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadondoidichvu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HoadondoidichvuRepository extends JpaRepository<Hoadondoidichvu, Integer> {
    List<Hoadondoidichvu> findByNguoithanhtoanAndNgaythanhtoan(String x, LocalDate date);

    List<Hoadondoidichvu> findByIdhoadon_Idlichhen_Date(LocalDate date);
}