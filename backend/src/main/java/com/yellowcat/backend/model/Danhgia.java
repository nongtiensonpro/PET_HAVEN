package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "danhgia")
public class Danhgia {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "danhgia_id_gen")
    @SequenceGenerator(name = "danhgia_id_gen", sequenceName = "danhgia_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @JsonProperty
    @NotNull
    @Column(name = "idtaikhoan", nullable = false)
    private String idtaikhoan;

    @JsonProperty
    @Column(name = "sosao")
    private String sosao;

    @JsonProperty
    @Column(name = "mota", length = Integer.MAX_VALUE)
    private String mota;

    public void setSosao(String sosao) {
        this.sosao = sosao;
    }

    @JsonProperty
    @Column(name = "date")
    private LocalDateTime date;

    @JsonProperty
    @Column(name = "trangthai")
    private Boolean trangthai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idhoadon")
    private Hoadon idhoadon;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdtaikhoan() {
        return idtaikhoan;
    }

    public void setIdtaikhoan(String idtaikhoan) {
        this.idtaikhoan = idtaikhoan;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

    public Hoadon getIdhoadon() {
        return idhoadon;
    }

    public void setIdhoadon(Hoadon idhoadon) {
        this.idhoadon = idhoadon;
    }

}