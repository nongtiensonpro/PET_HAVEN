package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dichvu")

public class Dichvu {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dichvu_id_gen")
    @SequenceGenerator(name = "dichvu_id_gen", sequenceName = "dichvu_id_dichvu_seq", allocationSize = 1)
    @Column(name = "id_dichvu", nullable = false)
    private Long id;

    @JsonProperty
    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @JsonProperty
    @Column(name = "mota", nullable = false, length = Integer.MAX_VALUE)
    private String mota;

    @JsonProperty
    @Column(name = "anh", length = Integer.MAX_VALUE)
    private String anh;

    @JsonProperty
    @Column(name = "giatien", nullable = false)
    private Integer giatien;

}