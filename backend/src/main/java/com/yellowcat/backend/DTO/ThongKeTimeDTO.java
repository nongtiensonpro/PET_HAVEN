package com.yellowcat.backend.DTO;

public class ThongKeTimeDTO {
    private String ngayBatDau;
    private Double tongTien;

    public ThongKeTimeDTO(String startDate, double tongSoTien) {
    }

    public String getNgayBatDau() {
        return ngayBatDau;
    }

    public Double getTongTien() {
        return tongTien;
    }
}
