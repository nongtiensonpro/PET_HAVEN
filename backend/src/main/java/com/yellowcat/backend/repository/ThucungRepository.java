package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Thucung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThucungRepository extends JpaRepository<Thucung, Integer> {
    Optional<Thucung> findById(Integer id);
}