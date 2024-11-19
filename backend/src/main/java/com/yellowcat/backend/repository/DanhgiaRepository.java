package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Danhgia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DanhgiaRepository extends JpaRepository<Danhgia, Integer> {
    List<Danhgia> findByIdhoadon_Idlichhen_Dichvu_Id(int idDichVu);
    Optional<Danhgia> findByIdhoadon_IdAndIdtaikhoan(int idHd, String idTk);
    Optional<Danhgia> findById(Integer id);
}