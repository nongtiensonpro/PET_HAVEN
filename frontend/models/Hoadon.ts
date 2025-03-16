export interface ThuCung {
    id: number;
    ten: string;
    cannang: number;
    tuoi: number;
    giong: string;
    idtaikhoan: string;
    image: string | null;
    gioitinh: boolean;
    cophaimeokhong: boolean;
    tinhtrangsuckhoe: string;
    mota: string;
}

export interface TuyChonCanNang {
    id: number;
    cannangmin: number;
    cannangmax: number | null;
    giatien: number;
    trangthai: boolean;
}

export interface CaLichHen {
    id: number;
    thoigianca: string;
    trangthai: boolean;
}

export interface LichHen {
    id: number;
    idkhachhang: string;
    thucung: ThuCung;
    tuyChonCanNang: TuyChonCanNang;
    emailNguoiDat: string;
    date: string;
    trangthai: number;
    idcalichhen: CaLichHen;
    trangthaica: boolean;
    thoigianhuy: string | null;
    thoigianthaydoi: string | null;
    solanthaydoi: number;
    solannhacnho: number;
}

export default class Hoadon {
    id: number;
    idlichhen: LichHen;
    date: string;
    sotienbandau: number;
    sotien: number;
    ngaythanhtoan: string;
    phuongthucthanhtoan: string;
    trangthai: number;
    nguoithanhtoan: string;
    magiaodich: string;
    idgiamgia: string | null;
    ghichu: string | null;

    constructor(
        id: number,
        idlichhen: LichHen,
        date: string,
        sotienbandau: number,
        sotien: number,
        ngaythanhtoan: string,
        phuongthucthanhtoan: string,
        trangthai: number,
        nguoithanhtoan: string,
        magiaodich: string,
        idgiamgia: string | null,
        ghichu: string | null = null
    ) {
        this.id = id;
        this.idlichhen = idlichhen;
        this.date = date;
        this.sotienbandau = sotienbandau;
        this.sotien = sotien;
        this.ngaythanhtoan = ngaythanhtoan;
        this.phuongthucthanhtoan = phuongthucthanhtoan;
        this.trangthai = trangthai;
        this.nguoithanhtoan = nguoithanhtoan;
        this.magiaodich = magiaodich;
        this.idgiamgia = idgiamgia;
        this.ghichu = ghichu;
    }

    static fromJSON(json: any): Hoadon {
        return new Hoadon(
            json.id,
            json.idlichhen,
            json.date,
            json.sotienbandau,
            json.sotien,
            json.ngaythanhtoan,
            json.phuongthucthanhtoan,
            json.trangthai,
            json.nguoithanhtoan,
            json.magiaodich,
            json.idgiamgia
        );
    }

    toJSON(): object {
        return {
            id: this.id,
            idlichhen: this.idlichhen,
            date: this.date,
            sotienbandau: this.sotienbandau,
            sotien: this.sotien,
            ngaythanhtoan: this.ngaythanhtoan,
            phuongthucthanhtoan: this.phuongthucthanhtoan,
            trangthai: this.trangthai,
            nguoithanhtoan: this.nguoithanhtoan,
            magiaodich: this.magiaodich,
            idgiamgia: this.idgiamgia
        };
    }
}