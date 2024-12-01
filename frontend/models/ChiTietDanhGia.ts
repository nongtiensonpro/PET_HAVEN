export interface ThuCung {
    id: number;
    ten: string;
    cannang: number;
    tuoi: number;
    giong: string;
    idtaikhoan: string;
    image: string | null;
}

export interface DichVu {
    id: number;
    tendichvu: string;
    mota: string;
    anh: string | null;
    giatien: number;
    trangthai: boolean;
}

export interface CaLichHen {
    id: number;
    tenca: string;
    thoigianca: string;
    trangthai: boolean;
}

export interface LichHen {
    id: number;
    idkhachhang: string;
    thucung: ThuCung;
    dichvu: DichVu;
    emailNguoiDat: string;
    date: string;
    trangthai: number;
    idcalichhen: CaLichHen;
    trangthaica: boolean;
    thoigianhuy: string | null;
    thoigianthaydoi: string;
    solanthaydoi: number;
    solannhacnho: number;
}

export interface GiamGia {
    id: number;
    phantramgiam: number;
    ngaybatdau: string;
    ngayketthuc: string;
    mota: string;
    trangthai: boolean;
}

export interface HoaDon {
    id: number;
    idlichhen: LichHen;
    date: string;
    sotien: number;
    ngaythanhtoan: string;
    phuongthucthanhtoan: string;
    trangthai: number;
    nguoithanhtoan: string;
    magiaodich: string;
    idgiamgia: GiamGia;
}

export interface ChiTietDanhGia {
    id: number;
    idtaikhoan: string;
    sosao: string;
    mota: string;
    date: string;
    trangthai: boolean;
    idhoadon: HoaDon;
}

export default ChiTietDanhGia;