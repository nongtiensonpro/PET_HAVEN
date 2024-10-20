package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "thucung")
public class Thucung {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "thucung_id_gen")
    @SequenceGenerator(name = "thucung_id_gen", sequenceName = "thucung_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @JsonProperty
    @Column(name = "tencho", nullable = false, length = 100)
    private String tencho;

    @Column(name = "cannang")
    @JsonProperty
    private Double cannang;

    @Column(name = "tuoi")
    @JsonProperty
    private Integer tuoi;

    @Size(max = 100)
    @JsonProperty
    @Column(name = "giongcho", length = 100)
    private String giongcho;

    @Size(max = 255)
    @NotNull
    @JsonProperty
    @Column(name = "idtaikhoan", nullable = false)
    private String idtaikhoan;

    @JsonProperty
    @Column(name = "image", length = Integer.MAX_VALUE)
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @Size(max = 100) @NotNull String getTencho() {
        return tencho;
    }

    public void setTencho(@Size(max = 100) @NotNull String tencho) {
        this.tencho = tencho;
    }

    public Double getCannang() {
        return cannang;
    }

    public void setCannang(Double cannang) {
        this.cannang = cannang;
    }

    public Integer getTuoi() {
        return tuoi;
    }

    public void setTuoi(Integer tuoi) {
        this.tuoi = tuoi;
    }

    public @Size(max = 100) String getGiongcho() {
        return giongcho;
    }

    public void setGiongcho(@Size(max = 100) String giongcho) {
        this.giongcho = giongcho;
    }

    public @Size(max = 255) @NotNull String getIdtaikhoan() {
        return idtaikhoan;
    }

    public void setIdtaikhoan(@Size(max = 255) @NotNull String idtaikhoan) {
        this.idtaikhoan = idtaikhoan;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}