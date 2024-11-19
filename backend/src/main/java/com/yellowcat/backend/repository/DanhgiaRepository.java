package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Danhgia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DanhgiaRepository extends JpaRepository<Danhgia, Integer> {
}