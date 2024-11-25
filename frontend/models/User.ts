export default class User {
    id: string;
    idtaikhoan: string;
    hoten: string;
    gioitinh: string;
    email: string;
    role: string;

    constructor(
        id:string,
        idtaikhoan: string,
        hoten: string,
        gioitinh: string,
        email: string,
        role: string
    ) {
        this.id = id;
        this.idtaikhoan = idtaikhoan;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.email = email;
        this.role = role;
    }
}