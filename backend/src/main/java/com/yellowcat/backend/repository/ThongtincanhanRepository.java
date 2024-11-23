package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Thongtincanhan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThongtincanhanRepository extends JpaRepository<Thongtincanhan, Integer> {
    Optional<Thongtincanhan> findByIdtaikhoan(String id);
}