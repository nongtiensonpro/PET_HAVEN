export default class Hoadon {
    id: number;
    idKhachHang: string;
    tongTien: number;
    ngayLap: Date;
    trangThai: boolean;

    constructor(
        id: number,
        idKhachHang: string,
        tongTien: number,
        ngayLap: Date,
        trangThai: boolean
    ) {
        this.id = id;
        this.idKhachHang = idKhachHang;
        this.tongTien = tongTien;
        this.ngayLap = ngayLap;
        this.trangThai = trangThai;
    }

    static fromJSON(json: any): Hoadon {
        return new Hoadon(
            json.id,
            json.idKhachHang,
            json.tongTien,
            new Date(json.ngayLap),
            json.trangThai
        );
    }

    toJSON(): object {
        return {
            id: this.id,
            idKhachHang: this.idKhachHang,
            tongTien: this.tongTien,
            ngayLap: this.ngayLap.toISOString(),
            trangThai: this.trangThai
        };
    }
}