package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

public class DoiDichVuDTO {
    @NotNull
    private Integer idLichHen;
    @NotNull
    private Integer idTuyChonCanNang;
    private String ghiChu;

    public @NotNull Integer getIdLichHen() {
        return idLichHen;
    }

    public void setIdLichHen(@NotNull Integer idLichHen) {
        this.idLichHen = idLichHen;
    }

    public @NotNull Integer getIdTuyChonCanNang() {
        return idTuyChonCanNang;
    }

    public void setIdTuyChonCanNang(@NotNull Integer idTuyChonCanNang) {
        this.idTuyChonCanNang = idTuyChonCanNang;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
