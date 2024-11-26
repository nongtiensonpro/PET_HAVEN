export default class Voucher{
    id: number;
    phantramgiamgia: number;
    ngaybatdau: Date;
    ngayketthuc: Date;
    mota: String;
    trangthai: boolean;
    constructor(id: number, phantramgiamgia: number, ngaybatdau: Date, ngayketthuc: Date, mota: String, trangthai: boolean){
        this.id = id;
        this.phantramgiamgia = phantramgiamgia;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.mota = mota;
        this.trangthai = trangthai;
    }
}