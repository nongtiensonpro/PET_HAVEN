package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {

    List<Hoadon> findByDate(LocalDate date);

    @Query("SELECT h FROM Hoadon h WHERE h.nguoithanhtoan = :email " +
            "AND h.phuongthucthanhtoan = 'Offline' " +
            "AND h.date = :date ")
    List<Hoadon> findByNguoithanhtoanAndPhuongthucthanhtoan(@Param("email") String email
    , @Param("date") LocalDate date);
    Optional<Hoadon> findByIdlichhen_Id(Integer idlichhen);

    Optional<Hoadon> findByMagiaodich(String idPayMent);

    List<Hoadon> findByIdlichhen_TrangthaiAndTrangthaiAndDate(int TrangthaiLich, int Trangthai,LocalDate date);


//    ________________Thống kê______________________
// Thống kê theo ngày
    @Query("SELECT FUNCTION('DATE', h.date) AS ngay, SUM(h.sotien) AS doanhthu " +
            "FROM Hoadon h " +
            "WHERE h.trangthai = 2 AND FUNCTION('DATE', h.date) BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', h.date) " +
            "ORDER BY FUNCTION('DATE', h.date)")
    List<Object[]> thongKeTheoNgay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', h.date) AS thang, SUM(h.sotien) AS doanhthu " +
            "FROM Hoadon h " +
            "WHERE h.trangthai = 2 AND h.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'month', h.date) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'month', h.date)")
    List<Object[]> thongKeTheoThang(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'year', h.date) AS nam, SUM(h.sotien) AS doanhthu " +
            "FROM Hoadon h " +
            "WHERE h.trangthai = 2 AND h.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'year', h.date) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'year', h.date)")
    List<Object[]> thongKeTheoNam(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
    SELECT
        l.emailnguoidat AS emailnguoidat,
        COUNT(*) AS tong_so_lich_hen,
        SUM(d.giatien) AS tong_so_tien
    FROM
        lichhen l
        JOIN
        dichvu d
        ON
        l.iddichvu = d.id
    WHERE
        l.date BETWEEN :ngayBatDau AND :ngayKetThuc
    GROUP BY
        l.emailnguoidat
    ORDER BY
        tong_so_tien DESC,
        tong_so_lich_hen DESC
    LIMIT 10
""", nativeQuery = true)
    List<Object[]> findTopCustomers(
            @Param("ngayBatDau") LocalDate ngayBatDau,
            @Param("ngayKetThuc") LocalDate ngayKetThuc
    );


}