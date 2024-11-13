package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {

    List<Hoadon> findByTrangthai(Integer trangthai);

    List<Hoadon> findByNguoithanhtoan(String email);
}