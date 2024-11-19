import { defineStore } from 'pinia';
import HoaDonKhachHang from "~/models/CaLichHenKhachDat";



interface CheckInState {
    ListHoaDon: HoaDonKhachHang[];
    ListHoaDonDaThanhToan : HoaDonKhachHang[];
}



export const useCheckInStore = defineStore('checkInStore', {
    state: (): CheckInState => ({
        ListHoaDon: [],
        ListHoaDonDaThanhToan: []
    }),
    actions: {
        async fetchHoaDon() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/hoa-don/all-chuaTT', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                const data = await response.json();
                this.ListHoaDon = data;
                console.log(this.ListHoaDon);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async checkIn(id: String) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/lich-hen/thanh-toan/` + id, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    console.log(data);
                } else {
                    throw new Error('Check in failed');
                }
            }catch (error) {
                console.error('Error checking in:', error);
            }
        },
        async fetchHoaDonDaThanhToan() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/hoa-don/Lich-su-thanh-toan-nhan-vien', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                const data = await response.json();
                console.log(data);
                this.ListHoaDonDaThanhToan = data;
                console.log(this.ListHoaDonDaThanhToan);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        }
    }

})