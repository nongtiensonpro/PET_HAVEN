
export default class ThuCungKhachHang{
    id: number;
    ten: string;
    cannang: number;
    tuoi: number;
    giong: string;
    idtaikhoan: string;


    constructor(id: number, ten: string, cannang: number, tuoi: number, giong: string, idtaikhoan: string) {
        this.id = id;
        this.ten = ten;
        this.cannang = cannang;
        this.tuoi = tuoi;
        this.giong = giong;
        this.idtaikhoan = idtaikhoan;
    }
}