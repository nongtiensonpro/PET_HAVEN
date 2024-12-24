package com.yellowcat.backend.DTO;

import java.util.List;

public class DichVuDTO {
    private String tenDichVu;
    private String moTa;
    private String anh;
    private boolean trangThai;
    private boolean hien;
    private List<TuyChonDichVuRequest> tuyChonDichVu;

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isHien() {
        return hien;
    }

    public void setHien(boolean hien) {
        this.hien = hien;
    }

    public List<TuyChonDichVuRequest> getTuyChonDichVu() {
        return tuyChonDichVu;
    }

    public void setTuyChonDichVu(List<TuyChonDichVuRequest> tuyChonDichVu) {
        this.tuyChonDichVu = tuyChonDichVu;
    }

    public static class TuyChonDichVuRequest {
        private Integer id;
        private String tenTuyChon;
        private String moTa;
        private boolean trangThai;
        private List<TuyChonCanNangRequest> tuyChonCanNang;

        public String getTenTuyChon() {
            return tenTuyChon;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setTenTuyChon(String tenTuyChon) {
            this.tenTuyChon = tenTuyChon;
        }

        public String getMoTa() {
            return moTa;
        }

        public void setMoTa(String moTa) {
            this.moTa = moTa;
        }

        public boolean isTrangThai() {
            return trangThai;
        }

        public void setTrangThai(boolean trangThai) {
            this.trangThai = trangThai;
        }

        public List<TuyChonCanNangRequest> getTuyChonCanNang() {
            return tuyChonCanNang;
        }

        public void setTuyChonCanNang(List<TuyChonCanNangRequest> tuyChonCanNang) {
            this.tuyChonCanNang = tuyChonCanNang;
        }

        public static class TuyChonCanNangRequest {
            private Integer id;
            private Float canNangMin;
            private Float canNangMax;
            private int giaTien;
            private boolean trangThai;

            // Getters và Setters


            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Float getCanNangMin() {
                return canNangMin;
            }

            public void setCanNangMin(Float canNangMin) {
                this.canNangMin = canNangMin;
            }

            public Float getCanNangMax() {
                return canNangMax;
            }

            public void setCanNangMax(Float canNangMax) {
                this.canNangMax = canNangMax;
            }

            public int getGiaTien() {
                return giaTien;
            }

            public void setGiaTien(int giaTien) {
                this.giaTien = giaTien;
            }

            public boolean isTrangThai() {
                return trangThai;
            }

            public void setTrangThai(boolean trangThai) {
                this.trangThai = trangThai;
            }
        }
    }


}
