package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Calichhen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CalichhenRepository extends JpaRepository<Calichhen, Integer> {

    // Truy vấn để lấy tất cả các trạng thái của các ca trong một ngày
    @Query("SELECT c FROM Calichhen c LEFT join Lichhen l ON c.id = l.idcalichhen.id " +
            "WHERE l.date = :ngay AND l.trangthaica = false " +
            "AND (l.date <> CURRENT_DATE OR c.thoigianca >= CURRENT_TIME)" +
            "ORDER BY c.thoigianca ASC")
    List<Calichhen> findAllCaAndStatusByDateaAndTrangthaiFalse(@Param("ngay") LocalDate ngay);

    @Query("select c from  Calichhen c ORDER BY c.thoigianca")
    List<Calichhen> findAll();

    @Query("SELECT COUNT(c) > 0 FROM Calichhen c WHERE c.thoigianca = :thoigianca AND c.id <> :id")
    boolean existsByThoigiancaAndIdNot(@Param("thoigianca") LocalTime thoigianca, @Param("id") Integer id);

}