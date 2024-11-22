package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;


public class ThongKeDTO {
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

}
