package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "hoadondoidichvu")
public class Hoadondoidichvu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hoadondoidichvu_id_gen")
    @SequenceGenerator(name = "hoadondoidichvu_id_gen", sequenceName = "hoadondoidichvu_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonProperty
    @Size(max = 255)
    @NotNull
    @Column(name = "magiaodich", nullable = false)
    private String magiaodich;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idhoadon")
    private Hoadon idhoadon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idtuychoncannang")
    private TuyChonCanNang idtuychoncannang;

    @JsonProperty
    @NotNull
    @Column(name = "sotien", nullable = false)
    private Double sotien;

    @JsonProperty
    @Column(name = "ngaythanhtoan")
    private LocalDate ngaythanhtoan;

    @JsonProperty
    @Size(max = 1000)
    @Column(name = "ghichu", length = 1000)
    private String ghichu;

    @JsonProperty
    @NotNull
    @Column(name = "trangthai", nullable = false)
    private Integer trangthai;

    @JsonProperty
    @Size(max = 255)
    @Column(name = "nguoithanhtoan")
    private String nguoithanhtoan;

    @JsonProperty
    @Column(name = "trangthaithanhtoan")
    private Boolean trangthaithanhtoan;

    @Column(name = "ngaytao")
    private LocalDate ngaytao;

    public LocalDate getNgaytao() {
        return ngaytao;
    }

    public void setNgaytao(LocalDate ngaytao) {
        this.ngaytao = ngaytao;
    }

    public Boolean getTrangthaithanhtoan() {
        return trangthaithanhtoan;
    }

    public void setTrangthaithanhtoan(Boolean trangthaithanhtoan) {
        this.trangthaithanhtoan = trangthaithanhtoan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMagiaodich() {
        return magiaodich;
    }

    public void setMagiaodich(String magiaodich) {
        this.magiaodich = magiaodich;
    }

    public Hoadon getIdhoadon() {
        return idhoadon;
    }

    public void setIdhoadon(Hoadon idhoadon) {
        this.idhoadon = idhoadon;
    }

    public TuyChonCanNang getIdtuychoncannang() {
        return idtuychoncannang;
    }

    public void setIdtuychoncannang(TuyChonCanNang idtuychoncannang) {
        this.idtuychoncannang = idtuychoncannang;
    }

    public Double getSotien() {
        return sotien;
    }

    public void setSotien(Double sotien) {
        this.sotien = sotien;
    }

    public LocalDate getNgaythanhtoan() {
        return ngaythanhtoan;
    }

    public void setNgaythanhtoan(LocalDate ngaythanhtoan) {
        this.ngaythanhtoan = ngaythanhtoan;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public Integer getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Integer trangthai) {
        this.trangthai = trangthai;
    }

    public String getNguoithanhtoan() {
        return nguoithanhtoan;
    }

    public void setNguoithanhtoan(String nguoithanhtoan) {
        this.nguoithanhtoan = nguoithanhtoan;
    }

}