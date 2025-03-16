export default class NgayNghi{
    id: number;
    ngaynghi : Date;
    trangthai: boolean;

    constructor(id: number, ngaynghi: Date, trangthai: boolean){
        this.id = id;
        this.ngaynghi = ngaynghi;
        this.trangthai = trangthai;
    }
}