package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

public class VietDanhGiaDTO {
    private int idDanhGia;
    @NotNull
    private String moTa;
    @NotNull
    private Integer idLichHen;
    @NotNull
    private String star;

    public @NotNull String getStar() {
        return star;
    }

    public @NotNull Integer getIdLichHen() {
        return idLichHen;
    }

    public int getIdDanhGia() {
        return idDanhGia;
    }

    public @NotNull String getMoTa() {
        return moTa;
    }


}
