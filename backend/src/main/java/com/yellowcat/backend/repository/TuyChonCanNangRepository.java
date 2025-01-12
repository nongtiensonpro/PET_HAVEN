package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.TuyChonCanNang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuyChonCanNangRepository extends JpaRepository<TuyChonCanNang, Integer> {

    TuyChonCanNang findTuyChonCanNangsById(Integer id);

    List<?> findByTrangthaiTrueAndTuyChonDichVu_TrangthaiTrueAndTuyChonDichVu_Dichvu_TrangthaiTrue();
}