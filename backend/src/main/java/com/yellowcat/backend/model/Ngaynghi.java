package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "ngaynghi")
public class Ngaynghi {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ngaynghi_id_gen")
    @SequenceGenerator(name = "ngaynghi_id_gen", sequenceName = "ngaynghi_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonProperty
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "ngaynghi", nullable = false)
    private LocalDate ngaynghi;
    @JsonProperty
    @Column(name = "trangthai")
    private Boolean trangthai;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getNgaynghi() {
        return ngaynghi;
    }

    public void setNgaynghi(LocalDate ngaynghi) {
        this.ngaynghi = ngaynghi;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

}