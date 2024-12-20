export default class ThuCungKhachHang {
    id: number;
    ten: string;
    cannang: number;
    tuoi: number;
    giong: string;
    idtaikhoan: string;
    gioitinh: boolean;
    cophaimeokhong: boolean;
    tinhtrangsuckhoe: string | null;
    mota: string | null;

    constructor(
        id: number,
        ten: string,
        cannang: number,
        tuoi: number,
        giong: string,
        idtaikhoan: string,
        gioitinh: boolean,
        cophaimeokhong: boolean,
        tinhtrangsuckhoe: string | null,
        mota: string | null
    ) {
        this.id = id;
        this.ten = ten;
        this.cannang = cannang;
        this.tuoi = tuoi;
        this.giong = giong;
        this.idtaikhoan = idtaikhoan;
        this.gioitinh = gioitinh;
        this.cophaimeokhong = cophaimeokhong;
        this.tinhtrangsuckhoe = tinhtrangsuckhoe;
        this.mota = mota;
    }
}