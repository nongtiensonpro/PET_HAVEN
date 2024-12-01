package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Danhgia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DanhgiaRepository extends JpaRepository<Danhgia, Integer> {
    List<Danhgia> findByIdhoadon_Idlichhen_Dichvu_IdAndTrangthai(int idDichVu,boolean tt);
    Optional<Danhgia> findByIdhoadon_IdAndIdtaikhoanAndTrangthai(int idHd, String idTk,boolean tt);
    Optional<Danhgia> findById(Integer id);
    Optional<Danhgia> findByIdhoadon_Idlichhen_IdAndTrangthaiTrue(int idDichVu);
}