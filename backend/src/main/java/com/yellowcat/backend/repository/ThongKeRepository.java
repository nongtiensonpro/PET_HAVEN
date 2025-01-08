package com.yellowcat.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.yellowcat.backend.model.Lichhen;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Repository
public interface ThongKeRepository extends JpaRepository<Lichhen, Long> {
    
    @Query("SELECT COUNT(l) FROM Lichhen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countAppointmentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(l) FROM Lichhen l WHERE l.trangthai = 0 AND l.date BETWEEN :startDate AND :endDate")
    Long countCompletedAppointments(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(l) FROM Lichhen l WHERE l.trangthai = 2 AND l.date BETWEEN :startDate AND :endDate")
    Long countCanceledAppointments(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(h.sotien) FROM Hoadon h WHERE h.trangthai = 2 AND h.date BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT l.idkhachhang) FROM Lichhen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countUniqueCustomers(@Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT l.idkhachhang) FROM Lichhen l WHERE l.date BETWEEN :startDate AND :endDate")
    Long countUniquePets(@Param("startDate") LocalDateTime startDate, 
                         @Param("endDate") LocalDateTime endDate);
}
