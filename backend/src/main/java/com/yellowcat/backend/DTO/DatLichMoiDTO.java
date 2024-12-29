package com.yellowcat.backend.DTO;

import com.yellowcat.backend.model.Thucung;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class DatLichMoiDTO {

    @NotNull
    private Thucung idThuCung;

    @NotNull
    private String date;

    @NotNull
    private Integer idcalichhen;

    @NotNull
    private Integer idDichVu;

    @NotNull
    private Integer idTuyChonDichVu;

    @NotNull
    private Integer idTuyChonCanNang;

    // Constructors
    public DatLichMoiDTO() {}

    // Getters and Setters
    public Thucung getIdThuCung() {
        return idThuCung;
    }

    public void setIdThuCung(Thucung idThuCung) {
        this.idThuCung = idThuCung;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getIdcalichhen() {
        return idcalichhen;
    }

    public void setIdcalichhen(Integer idcalichhen) {
        this.idcalichhen = idcalichhen;
    }

    public Integer getIdDichVu() {
        return idDichVu;
    }

    public void setIdDichVu(Integer idDichVu) {
        this.idDichVu = idDichVu;
    }

    public Integer getIdTuyChonDichVu() {
        return idTuyChonDichVu;
    }

    public void setIdTuyChonDichVu(Integer idTuyChonDichVu) {
        this.idTuyChonDichVu = idTuyChonDichVu;
    }

    public Integer getIdTuyChonCanNang() {
        return idTuyChonCanNang;
    }

    public void setIdTuyChonCanNang(Integer idTuyChonCanNang) {
        this.idTuyChonCanNang = idTuyChonCanNang;
    }

    @Override
    public String toString() {
        return "DatLichMoiDTO{" +
                "idThuCung=" + idThuCung.toString() +
                ", date='" + date + '\'' +
                ", idcalichhen=" + idcalichhen +
                ", idDichVu=" + idDichVu +
                ", idTuyChonDichVu=" + idTuyChonDichVu +
                ", idTuyChonCanNang=" + idTuyChonCanNang +
                '}';
    }
}