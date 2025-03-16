import type DichVu from "~/models/DichVu";
import type ThuCungKhachHang from "~/models/ThuCungKhachHang";
import CaHen from '../models/CaHen';

export default class LichHenDetails{
    id: number;
    idkhachhang: string;
    thucung: ThuCungKhachHang;
    dichvu: DichVu;
    emailNguoiDat: string;
    date: string;
    trangthai: number;
    idcalichhen: CaHen;
    trangthaica: boolean;
    thoigianhuy: string | null;
    thoigianthaydoi: string | null;
    solanthaydoi: number;


    constructor(id: number, idkhachhang: string, thucung: {
        cannang: number;
        cophaimeokhong: boolean;
        gioitinh: boolean;
        giong: string;
        id: number;
        idtaikhoan: string;
        mota: string | null;
        ten: string;
        tinhtrangsuckhoe: string | null;
        tuoi: number
    }, dichvu: {
        anh: string | null;
        hien: boolean;
        id: number;
        mota: string;
        tendichvu: string;
        trangthai: boolean;
        tuyChonDichVus: {
            id: number;
            idDichVu: number;
            moTa: string | null;
            tenTuyChon: string;
            trangThai: boolean;
            tuyChonCanNangs: {
                canNangMax: number | null;
                canNangMin: number | null;
                giaTien: number;
                id: number;
                idTuyChonDichVu: number;
                trangThai: boolean
            }[]
        }[]
    }, emailNguoiDat: string, date: string, trangthai: number, idcalichhen: any, trangthaica: boolean, thoigianhuy: string | null, thoigianthaydoi: string | null, solanthaydoi: number) {
        this.id = id;
        this.idkhachhang = idkhachhang;
        this.thucung = thucung;
        this.dichvu = dichvu;
        this.emailNguoiDat = emailNguoiDat;
        this.date = date;
        this.trangthai = trangthai;
        this.idcalichhen = idcalichhen;
        this.trangthaica = trangthaica;
        this.thoigianhuy = thoigianhuy;
        this.thoigianthaydoi = thoigianthaydoi;
        this.solanthaydoi = solanthaydoi;
    }
}