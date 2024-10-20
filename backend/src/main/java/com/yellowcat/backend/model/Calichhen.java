package com.yellowcat.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalTime;


@Entity
@Table(name = "calichhen")
public class Calichhen {
    @Id
    @ColumnDefault("nextval('calichhen_id_seq'::regclass)")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "tenca", length = 20)
    private String tenca;

    @NotNull
    @Column(name = "thoigianca", nullable = false)
    private LocalTime thoigianca;

    @Column(name = "trangthai")
    private Boolean trangthai;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @Size(max = 20) String getTenca() {
        return tenca;
    }

    public void setTenca(@Size(max = 20) String tenca) {
        this.tenca = tenca;
    }

    public @NotNull LocalTime getThoigianca() {
        return thoigianca;
    }

    public void setThoigianca(@NotNull LocalTime thoigianca) {
        this.thoigianca = thoigianca;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }
}