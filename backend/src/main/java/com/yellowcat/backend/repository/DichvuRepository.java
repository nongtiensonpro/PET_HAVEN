package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Dichvu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface DichvuRepository extends JpaRepository<Dichvu, Long> {


}