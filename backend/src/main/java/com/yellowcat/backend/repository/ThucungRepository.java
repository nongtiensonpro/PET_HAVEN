package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Thucung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThucungRepository extends JpaRepository<Thucung, Integer> {
}