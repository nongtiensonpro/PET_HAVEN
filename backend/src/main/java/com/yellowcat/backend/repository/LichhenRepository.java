package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Lichhen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichhenRepository extends JpaRepository<Lichhen, Integer> {
    Page<Lichhen> findByIdkhachhang(String idKhachHang, Pageable pageable);

    Page<Lichhen> findByEmailNguoiDat(String idKhachHang, Pageable pageable);


}