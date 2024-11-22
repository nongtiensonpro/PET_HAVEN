import { defineStore } from 'pinia';
import DanhGia from '../models/DanhGia';
import API_ENDPOINTS from '../apiconfig/ApiConfig';

export const useDanhGiaStore = defineStore('danhGiaStore', {
    state: () => ({
        danhGias: [] as DanhGia[],
    }),
    actions: {
        async fetchDanhGiaByDichVu(idDichVu: number) {
            try {
                const response = await fetch(`http://localhost:8080/api/danh-gia/danh-gia-theo-dich-vu/${idDichVu}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                const data = await response.json();
                this.danhGias = data; // Giả sử API trả về danh sách đánh giá trực tiếp
            } catch (error) {
                console.error('Error fetching danh gia:', error);
            }
        },

        async addDanhGia(danhGia: DanhGia) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/danh-gia/danh-gia-theo-dich-vu/1`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(danhGia)
                });

                if (!response.ok) {
                    throw new Error(`Failed to add danh gia. Status: ${response.status}`);
                }

                const newDanhGia = await response.json();
                this.danhGias.push(newDanhGia);
                return { success: true, message: 'Thêm đánh giá thành công' };
            } catch (error) {
                console.error('Error adding danh gia:', error);
                return { success: false, message: 'Lỗi thêm đánh giá' };
            }
        },

        async deleteDanhGia(danhGiaId: number) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/danh-gia/danh-gia-theo-dich-vu/1`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error(`Failed to delete danh gia. Status: ${response.status}`);
                }

                this.danhGias = this.danhGias.filter(danhGia => danhGia.id !== danhGiaId);
                return { success: true };
            } catch (error) {
                console.error('Error deleting danh gia:', error);
                return { success: false, message: 'Lỗi xóa đánh giá' };
            }
        }
    },
});