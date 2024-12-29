package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.TuyChonCanNang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuyChonCanNangRepository extends JpaRepository<TuyChonCanNang, Integer> {

    TuyChonCanNang findTuyChonCanNangsById(Integer id);
}