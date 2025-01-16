package com.yellowcat.backend.repository;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Hoadondoidichvu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoadonRepository extends JpaRepository<Hoadon, Integer> {

    List<Hoadon> findByIdlichhen_DateAndIdlichhen_TrangthaicaAndIdlichhen_TrangthaiNot(LocalDate date,boolean tt,int TT);

    @Query("SELECT h FROM Hoadon h WHERE h.nguoithanhtoan = :email " +
            "AND h.phuongthucthanhtoan = 'Offline' " +
            "AND h.date = :date " +
            "AND h.idlichhen.trangthai != 2"
    )
    List<Hoadon> findByNguoithanhtoanAndPhuongthucthanhtoan(@Param("email") String email
    , @Param("date") LocalDate date);
    Optional<Hoadon> findByIdlichhen_IdAndTrangthai(Integer idlichhen,int tt);

    @Query("SELECT hd FROM Hoadon hd " +
            "JOIN Lichhen lh ON lh.id = hd.idlichhen.id " +
            "WHERE hd.trangthai != 3 AND hd.trangthai != 4"+
            "AND lh.id = :idlichhen")
    Optional<Hoadon> findByIdlichhen_Id(Integer idlichhen);


    Optional<Hoadon> findByMagiaodich(String idPayMent);

    List<Hoadon> findByIdlichhen_TrangthaiAndTrangthaiAndDate(int TrangthaiLich, int Trangthai,LocalDate date);

    @Query("SELECT hd FROM Hoadon hd " +
            "JOIN Lichhen lh ON lh.id = hd.idlichhen.id " +
            "WHERE lh.trangthaica = :trangthaica " +
            "AND lh.idkhachhang = :idkhachhang " +
            "AND hd.trangthai != 3")
    List<Hoadon> findByIdlichhen_TrangthaicaAndIdlichhen_IdkhachhangAndTrangthai(
            @Param("trangthaica") Boolean trangthaica,
            @Param("idkhachhang") String idkhachhang
    );
//    List<Hoadon> findByIdlichhen_TrangthaicaAndIdlichhen_IdkhachhangAndTrangthai(boolean tt,String idKhach,int ttHD);
//    ________________Thống kê______________________
// Thống kê theo ngày
@Query("SELECT h.date, " +
        "SUM(h.sotien) +" +
        "SUM(CASE WHEN x.trangthai = 2 THEN x.sotien ELSE 0 END) - " +
        "SUM(CASE WHEN x.trangthai = 1 THEN x.sotien ELSE 0 END) AS doanhthu " +
        "FROM Hoadon h " +
        "LEFT JOIN Hoadondoidichvu x ON h.id = x.idhoadon.id " + // Sử dụng LEFT JOIN
        "WHERE h.trangthai = 2 " +
        "AND h.date BETWEEN :startDate AND :endDate " +
        "AND (x.ngaytao BETWEEN :startDate AND :endDate OR x.ngaytao IS NULL) " + // Lọc theo ngày của Hoadondoidichvu, nếu không có thì cho phép NULL
        "AND (x.trangthaithanhtoan = true OR x.trangthaithanhtoan IS NULL) " + // Lọc theo trạng thái thanh toán, nếu không có thì cho phép NULL
        "GROUP BY h.date " +
        "ORDER BY h.date")
List<Object[]> thongKeTheoNgay(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', h.date) AS thang, " +
            "SUM(h.sotien) + " +
            "SUM(CASE WHEN x.trangthai = 2 THEN x.sotien ELSE 0 END) - " +
            "SUM(CASE WHEN x.trangthai = 1 THEN x.sotien ELSE 0 END) AS doanhthu " +
            "FROM Hoadon h " +
            "LEFT JOIN Hoadondoidichvu x ON h.id = x.idhoadon.id " +
            "WHERE h.trangthai = 2 " +
            "AND h.date BETWEEN :startDate AND :endDate " +
            "AND (x.ngaytao BETWEEN :startDate AND :endDate OR x.ngaytao IS NULL) " +
            "AND (x.trangthaithanhtoan = true OR x.trangthaithanhtoan IS NULL) " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'month', h.date) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'month', h.date)")
    List<Object[]> thongKeTheoThang(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'year', h.date) AS nam, " +
            "SUM(h.sotien) + " +
            "SUM(CASE WHEN x.trangthai = 2 THEN x.sotien ELSE 0 END) - " +
            "SUM(CASE WHEN x.trangthai = 1 THEN x.sotien ELSE 0 END) AS doanhthu " +
            "FROM Hoadon h " +
            "LEFT JOIN Hoadondoidichvu x ON h.id = x.idhoadon.id " +
            "WHERE h.trangthai = 2 " +
            "AND h.date BETWEEN :startDate AND :endDate " +
            "AND (x.ngaytao BETWEEN :startDate AND :endDate OR x.ngaytao IS NULL) " +
            "AND (x.trangthaithanhtoan = true OR x.trangthaithanhtoan IS NULL) " +
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