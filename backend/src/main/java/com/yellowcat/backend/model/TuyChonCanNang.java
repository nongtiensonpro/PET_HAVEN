package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tuychoncannang")
public class TuyChonCanNang {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tuychoncannang_id_gen")
    @SequenceGenerator(name = "tuychoncannang_id_gen", sequenceName = "tuychoncannang_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idtuychondichvu", nullable = false)
    private TuyChonDichVu tuyChonDichVu;

    @JsonProperty
    @Column(name = "cannangmin")
    private Float cannangmin;

    @JsonProperty
    @Column(name = "cannangmax")
    private Float cannangmax;

    @JsonProperty
    @NotNull
    @Column(name = "giatien", nullable = false)
    private Double giatien;

    @JsonProperty
    @NotNull
    @Column(name = "trangthai", nullable = false)
    private Boolean trangthai;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TuyChonDichVu getTuyChonDichVu() {
        return tuyChonDichVu;
    }

    public void setTuyChonDichVu(TuyChonDichVu tuyChonDichVu) {
        this.tuyChonDichVu = tuyChonDichVu;
    }

    public Float getCannangmin() {
        return cannangmin;
    }

    public void setCannangmin(Float cannangmin) {
        this.cannangmin = cannangmin;
    }

    public Float getCannangmax() {
        return cannangmax;
    }

    public void setCannangmax(Float cannangmax) {
        this.cannangmax = cannangmax;
    }

    public Double getGiatien() {
        return giatien;
    }

    public void setGiatien(Double giatien) {
        this.giatien = giatien;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }
}