package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "tuychondichvu")
public class TuyChonDichVu {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tuychondichvu_id_gen")
    @SequenceGenerator(name = "tuychondichvu_id_gen", sequenceName = "tuychondichvu_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "iddichvu", nullable = false)
    private Dichvu dichvu;

    @JsonProperty
    @NotNull
    @Column(name = "tentuychon", nullable = false, length = 100)
    private String tentuychon;

    @JsonProperty
    @Column(name = "mota")
    private String mota;

    @JsonProperty
    @NotNull
    @Column(name = "trangthai", nullable = false)
    private Boolean trangthai;

    @JsonManagedReference
    @OneToMany(mappedBy = "tuyChonDichVu", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TuyChonCanNang> tuyChonCanNangs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dichvu getDichvu() {
        return dichvu;
    }

    public void setDichvu(Dichvu dichvu) {
        this.dichvu = dichvu;
    }

    public String getTentuychon() {
        return tentuychon;
    }

    public void setTentuychon(String tentuychon) {
        this.tentuychon = tentuychon;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

    public List<TuyChonCanNang> getTuyChonCanNangs() {
        return tuyChonCanNangs;
    }

    public void setTuyChonCanNangs(List<TuyChonCanNang> tuyChonCanNangs) {
        this.tuyChonCanNangs = tuyChonCanNangs;
    }
}