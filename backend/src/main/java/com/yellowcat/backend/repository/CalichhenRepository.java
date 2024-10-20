package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Calichhen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CalichhenRepository extends JpaRepository<Calichhen, Integer> {
    @Modifying
    @Query("UPDATE Calichhen c SET c.trangthai = :trangThai")
    void updateTrangThaiAll(@Param("trangThai") boolean trangThai);

    @Modifying
    @Query("UPDATE Calichhen c SET c.trangthai = TRUE WHERE c.id IN " +
            "(SELECT lh.idcalichhen FROM Lichhen lh WHERE lh.date = :ngay AND lh.trangthai = 0)")
    void updateTrangThaiCaDatThanhCong(@Param("ngay") LocalDate ngay);

    @Modifying
    @Query("UPDATE Calichhen c SET c.trangthai = true WHERE c.id IN " +
            "(SELECT lh.idcalichhen FROM Lichhen lh WHERE lh.date = :ngay)")
    void updateNgayNghi(@Param("ngay") LocalDate ngay);

    @Modifying
    @Query("UPDATE Calichhen c SET c.trangthai = :trangThai WHERE c.id = :idCaLichHen AND EXISTS " +
            "(SELECT lh FROM Lichhen lh WHERE lh.idcalichhen = :idCaLichHen AND lh.date = :ngay)")
    void updateTrangThaiCaTrongNgay(@Param("idCaLichHen") int idCaLichHen,
                                    @Param("ngay") LocalDate ngay,
                                    @Param("trangThai") boolean trangThai);

    // Truy vấn để lấy tất cả các trạng thái của các ca trong một ngày
    @Query("SELECT c, lh.trangthai FROM Calichhen c LEFT JOIN Lichhen lh ON c.id = lh.idcalichhen.id " +
            "WHERE lh.date = :ngay")
    List<Object[]> findAllCaAndStatusByDate(@Param("ngay") LocalDate ngay);

//    Đổi trang thai ca khi dat lich thanh cong
    @Modifying
    @Query("UPDATE Calichhen c SET c.trangthai = TRUE WHERE c.id = :caId AND EXISTS (SELECT lh FROM Lichhen lh WHERE lh.idCaLichHen.id = c.id AND lh.date = :ngay AND lh.trangthai = 0)")
    void updateTrangThaiCaDatThanhCong(@Param("caId") int caId, @Param("ngay") LocalDate ngay);

}