import type ThuCung from '~/models/ThuCungKhachHang';

export const useThuCungStore = defineStore('thucungStore', {
    actions: {
        async addThuCung(thucung :ThuCung){
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/add',{

                })
            }catch (e) {
                throw new Error("Thêm thú cung thất bại "+ e)
            }
        }
    }
})