package com.yellowcat.backend.DTO;

import com.yellowcat.backend.model.Calichhen;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public class UpdateCaLichHenDTO {
    @NotNull
    private Integer id;
    @NotNull
    private String tenCa;
    @NotNull
    private String ThoiGian;

    public @NotNull Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public @NotNull String getTenCa() {
        return tenCa;
    }

    public void setTenCa(@NotNull String tenCa) {
        this.tenCa = tenCa;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }
}
