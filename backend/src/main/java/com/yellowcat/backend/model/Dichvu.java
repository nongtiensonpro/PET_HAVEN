package com.yellowcat.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dichvu")
public class Dichvu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dichvu_id_gen")
    @SequenceGenerator(name = "dichvu_id_gen", sequenceName = "dichvu_id_dichvu_seq", allocationSize = 1)
    @Column(name = "id_dichvu", nullable = false)
    private Long id;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @Column(name = "mota", nullable = false, length = Integer.MAX_VALUE)
    private String mota;

    @Column(name = "anh", length = Integer.MAX_VALUE)
    private String anh;

    @Column(name = "giatien", nullable = false)
    private Integer giatien;

}