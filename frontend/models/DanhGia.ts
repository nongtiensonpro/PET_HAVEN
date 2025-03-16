export default class DanhGia {
    id: number;
    idtaikhoan: string;
    sosao: number;
    mota: string;
    date: Date;
    trangthai: boolean;
    idhoadon: {
        id: number;
        idlichhen: {
            id: number;
            idkhachhang: string;
            thucung: {
                id: number;
                ten: string;
                cannang: number;
                tuoi: number;
                giong: string;
                idtaikhoan: string;
                image: string | null;
            };
            dichvu: {
                id: number;
                tendichvu: string;
                mota: string;
                anh: string | null;
                giatien: number;
                trangthai: boolean;
            };
            emailNguoiDat: string;
            date: string;
            trangthai: number;
            idcalichhen: {
                id: number;
                tenca: string;
                thoigianca: string;
                trangthai: boolean;
            };
            trangthaica: boolean;
            thoigianhuy: string | null;
            thoigianthaydoi: string | null;
            solanthaydoi: number;
            solannhacnho: number | null;
        };
        date: string;
        sotien: number;
        ngaythanhtoan: string;
        phuongthucthanhtoan: string;
        trangthai: number;
        nguoithanhtoan: string;
        magiaodich: string;
        idgiamgia: {
            id: number;
            phantramgiam: number;
            ngaybatdau: string;
            ngayketthuc: string;
            mota: string;
            trangthai: boolean;
        };
    };

    constructor(
        id: number,
        idtaikhoan: string,
        sosao: number,
        mota: string,
        date: Date,
        trangthai: boolean,
        idhoadon: any
    ) {
        this.id = id;
        this.idtaikhoan = idtaikhoan;
        this.sosao = sosao;
        this.mota = mota;
        this.date = date;
        this.trangthai = trangthai;
        this.idhoadon = idhoadon;
    }

    static fromJSON(json: any): DanhGia {
        return new DanhGia(
            json.id,
            json.idtaikhoan,
            Number(json.sosao),
            json.mota,
            new Date(json.date),
            json.trangthai,
            json.idhoadon
        );
    }

    toJSON(): object {
        return {
            id: this.id,
            idtaikhoan: this.idtaikhoan,
            sosao: this.sosao,
            mota: this.mota,
            date: this.date.toISOString(),
            trangthai: this.trangthai,
            idhoadon: this.idhoadon
        };
    }
}