export default class ThuCungKhachHang{
    id: number;
    tenthuocung: string;
    cannang: number;
    tuoi: number;
    giongcho: string;
    idtaikhoan: string;
    image: string;

    constructor(id: number, tenthuocung: string, cannang: number, tuoi: number, giongcho: string, idtaikhoan: string, image: string){
        this.id = id;
        this.tenthuocung = tenthuocung;
        this.cannang = cannang;
        this.tuoi = tuoi;
        this.giongcho = giongcho;
        this.idtaikhoan = idtaikhoan;
        this.image = image;  // Add the image property to the constructor.
    }
}