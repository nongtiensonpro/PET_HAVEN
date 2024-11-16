import { defineStore } from 'pinia';
import HoaDonKhachHang from '~/models/CaLichHenKhachDat';
interface QuanLyHoaDonState {
    ListHoaDon: HoaDonKhachHang[];
}

export const useQuanLyHoaDonStore = defineStore('quanLyHoaDonStore', {
    state: (): QuanLyHoaDonState => ({
        ListHoaDon: [],
    }),
    actions: {
        async fetchListHoaDon() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/hoa-don/all`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                this.ListHoaDon = data;
                console.log(data);
            } catch (error) {
                console.error('Error fetching list hoa don:', error);
            }
        }
    }
});
