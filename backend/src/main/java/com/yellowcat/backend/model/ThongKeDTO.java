package com.yellowcat.backend.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ThongKeDTO {
    private Long totalAppointments;
    private Long completedAppointments;
    private Long canceledAppointments;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long totalPets;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
