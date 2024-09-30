package com.yellowcat.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dich_vu")
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dich_vu_id_gen")
    @SequenceGenerator(name = "dich_vu_id_gen", sequenceName = "dich_vu_id_dichvu_seq", allocationSize = 1)
    @Column(name = "id_dichvu", nullable = false)
    private Long id;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @Column(name = "mo_ta", nullable = false, length = Integer.MAX_VALUE)
    private String moTa;

    @Column(name = "anh", length = Integer.MAX_VALUE)
    private String anh;

    @Column(name = "gia_tien", nullable = false)
    private Double giaTien;

}