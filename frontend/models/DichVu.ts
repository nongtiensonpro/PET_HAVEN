export default class DichVu {
    id: number;
    tendichvu: string;
    mota: string;
    anh: string | null;
    trangthai: boolean;
    hien: boolean;
    tuyChonDichVus: TuyChonDichVu[];

    constructor(id: number, tendichvu: string, mota: string, anh: string | null, trangthai: boolean, hien: boolean, tuyChonDichVus: TuyChonDichVu[] = []) {
        this.id = id;
        this.tendichvu = tendichvu;
        this.mota = mota;
        this.anh = anh;
        this.trangthai = trangthai;
        this.hien = hien;
        this.tuyChonDichVus = tuyChonDichVus;
    }
}

export class TuyChonDichVu {
    id: number;
    idDichVu: number;
    tenTuyChon: string;
    moTa: string | null;
    trangThai: boolean;
    tuyChonCanNangs: TuyChonCanNang[];

    constructor(id: number, idDichVu: number, tenTuyChon: string, moTa: string | null, trangThai: boolean, tuyChonCanNangs: TuyChonCanNang[] = []) {
        this.id = id;
        this.idDichVu = idDichVu;
        this.tenTuyChon = tenTuyChon;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.tuyChonCanNangs = tuyChonCanNangs;
    }
}

export class TuyChonCanNang {
    id: number;
    idTuyChonDichVu: number;
    canNangMin: number | null;
    canNangMax: number | null;
    giaTien: number;
    trangThai: boolean;

    constructor(id: number, idTuyChonDichVu: number, canNangMin: number | null, canNangMax: number | null, giaTien: number, trangThai: boolean) {
        this.id = id;
        this.idTuyChonDichVu = idTuyChonDichVu;
        this.canNangMin = canNangMin;
        this.canNangMax = canNangMax;
        this.giaTien = giaTien;
        this.trangThai = trangThai;
    }
}