package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

public class VietDanhGiaDTO {
    private Integer id;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull String getMoTa() {
        return moTa;
    }


}
