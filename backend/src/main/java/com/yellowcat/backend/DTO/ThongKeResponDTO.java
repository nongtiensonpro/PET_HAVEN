package com.yellowcat.backend.DTO;

public class ThongKeResponDTO {
    private String emailNguoiDat;
    private int tongSoLichHen;
    private double tongSoTien;

    // Constructor
    public ThongKeResponDTO(String emailNguoiDat, int tongSoLichHen, double tongSoTien) {
        this.emailNguoiDat = emailNguoiDat;
        this.tongSoLichHen = tongSoLichHen;
        this.tongSoTien = tongSoTien;
    }

    // Getters and setters
    public String getEmailNguoiDat() {
        return emailNguoiDat;
    }

    public void setEmailNguoiDat(String emailNguoiDat) {
        this.emailNguoiDat = emailNguoiDat;
    }

    public int getTongSoLichHen() {
        return tongSoLichHen;
    }

    public void setTongSoLichHen(int tongSoLichHen) {
        this.tongSoLichHen = tongSoLichHen;
    }

    public double getTongSoTien() {
        return tongSoTien;
    }

    public void setTongSoTien(double tongSoTien) {
        this.tongSoTien = tongSoTien;
    }
}
