export default class Voucher{
    id: number;
    phantramgiam: number;
    ngaybatdau: Date;
    ngayketthuc: Date;
    mota: String;
    trangthai: boolean;
    constructor(id: number, phantramgiam: number, ngaybatdau: Date, ngayketthuc: Date, mota: String, trangthai: boolean){
        this.id = id;
        this.phantramgiam = phantramgiam;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.mota = mota;
        this.trangthai = trangthai;
    }
}