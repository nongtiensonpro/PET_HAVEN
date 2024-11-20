package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Lichhen;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichhenRepository extends JpaRepository<Lichhen, Integer> {
    Page<Lichhen> findByIdkhachhang(String idKhachHang, Pageable pageable);

    Page<Lichhen> findByEmailNguoiDat(String idKhachHang, Pageable pageable);

    boolean existsByDateAndIdcalichhen_Id(LocalDate date, int idCaLichHen);

    List<Lichhen> findByEmailNguoiDatAndDateBefore(String emailNguoiDat, LocalDate date);

    Optional<Lichhen> findByDateAndIdcalichhen_IdAndTrangthai(LocalDate date, int idCaLichHen, int trangthai);

    Page<Lichhen> findByTrangthaicaAndDateAfter(boolean trangthaica, LocalDate date, Pageable pageable);

    List<Lichhen> findByDate(LocalDate date);

    List<Lichhen> findByIdcalichhen_IdAndDateAfter(int idcalichhen_Id, LocalDate date);

    @Modifying
    @Transactional
    @Query("UPDATE Lichhen l Set l.trangthaica = true WHERE l.date =:ngay")
    void updateNgayNghi(@Param("ngay") LocalDate ngay);

}