package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DoiLichDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @NotNull
    private String idcalichhen;

    public @NotNull LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    public @NotNull String getIdcalichhen() {
        return idcalichhen;
    }

    public void setIdcalichhen(@NotNull String idcalichhen) {
        this.idcalichhen = idcalichhen;
    }
}
