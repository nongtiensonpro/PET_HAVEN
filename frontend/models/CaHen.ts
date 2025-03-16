export default class CaLichHen{
    id: number;
    thoigianca: Date;
    trangthai: boolean;

    constructor(id: number,  thoigianca: Date, trangthai: boolean) {
        this.id = id;
        this.thoigianca = thoigianca;
        this.trangthai = trangthai;
    }
}