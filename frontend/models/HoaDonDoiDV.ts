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

export interface HoaDon {
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
}

export interface HoaDonDoiDichVu {
    id: number;
    magiaodich: string;
    idhoadon: HoaDon;
    idtuychoncannang: TuyChonCanNang;
    sotien: number;
    ngaythanhtoan: string;
    ghichu: string | null;
    trangthai: number;
    nguoithanhtoan: string;
    trangthaithanhtoan: boolean;
    ngaytao: string;
}