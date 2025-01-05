package com.yellowcat.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.yellowcat.backend.entity.LichHen;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Repository
public interface ThongKeRepository extends JpaRepository<LichHen, Long> {
    
    @Query("SELECT COUNT(l) FROM LichHen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countAppointmentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(l) FROM LichHen l WHERE l.trangThai = 0 AND l.date BETWEEN :startDate AND :endDate")
    Long countCompletedAppointments(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(l) FROM LichHen l WHERE l.trangThai = 2 AND l.date BETWEEN :startDate AND :endDate")
    Long countCanceledAppointments(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(h.soTien) FROM HoaDon h WHERE h.trangThai = 2 AND h.date BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT l.idKhachHang) FROM LichHen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countUniqueCustomers(@Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT l.idThuCung) FROM LichHen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countUniquePets(@Param("startDate") LocalDateTime startDate, 
                         @Param("endDate") LocalDateTime endDate);
}
