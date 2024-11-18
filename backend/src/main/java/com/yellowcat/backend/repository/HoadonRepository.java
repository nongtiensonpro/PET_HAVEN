package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {

    List<Hoadon> findByTrangthai(Integer trangthai);

    @Query("SELECT h FROM Hoadon h WHERE h.nguoithanhtoan = :email " +
            "AND h.phuongthucthanhtoan = :phuongThuc " +
            "AND h.date = CURRENT_DATE " +
            "AND h.trangthai = 2")
    List<Hoadon> findPaidTodayByEmailAndPaymentMethod(@Param("email") String email,
                                                      @Param("phuongThuc") String phuongThuc);
    Optional<Hoadon> findByIdlichhen_Id(Integer idlichhen);

    Optional<Hoadon> findByMagiaodich(String idPayMent);
}