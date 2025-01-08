package com.yellowcat.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.yellowcat.backend.model.Hoadon;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChiTietThongKeRepository extends JpaRepository<Hoadon, Long> {
    
    @Query("SELECT dv.tendichvu, COUNT(h), SUM(h.sotien) " +
       "FROM Hoadon h " +
       "JOIN h.idlichhen l " +
       "JOIN l.tuyChonCanNang tc " +
       "JOIN tc.tuyChonDichVu tdv " +
       "JOIN tdv.dichvu dv " +
       "WHERE h.date BETWEEN :startDate AND :endDate " +
       "AND h.trangthai = 2 " +
       "GROUP BY dv.tendichvu")
List<Object[]> thongKeDoanhThuTheoDichVu(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);

    // Thống kê số lượng thú cưng theo loại (mèo/chó)
    @Query("SELECT tc.cophaimeokhong, COUNT(tc) " +
           "FROM Lichhen l " +
           "JOIN l.thucung tc " +
           "WHERE l.date BETWEEN :startDate AND :endDate " +
           "GROUP BY tc.cophaimeokhong")
    List<Object[]> thongKeThuCungTheoLoai(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);

    // Thống kê tỷ lệ hủy lịch
    @Query("SELECT " +
           "COUNT(CASE WHEN l.trangthai = 2 THEN 1 END) * 100.0 / COUNT(*), " +
           "COUNT(*) " +
           "FROM Lichhen l " +
           "WHERE l.date BETWEEN :startDate AND :endDate")
    Object[] tyLeHuyLich(@Param("startDate") LocalDate startDate, 
                        @Param("endDate") LocalDate endDate);

    // Thống kê thời gian đặt lịch phổ biến
    @Query("SELECT FUNCTION('HOUR', l.idcalichhen.thoigianca), COUNT(*) " +
           "FROM Lichhen l " +
           "WHERE l.date BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('HOUR', l.idcalichhen.thoigianca) " +
           "ORDER BY COUNT(*) DESC")
    List<Object[]> thongKeGioDatLichPhoBien(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
}
