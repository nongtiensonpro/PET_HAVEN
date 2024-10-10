import { defineStore } from 'pinia';
import DichVu from '../models/DichVu';
import Pageable from '../models/Pageable';

export const useServiceStore = defineStore('serviceStore', {
    state: () => ({
        services: [] as DichVu[],  // Sử dụng interface DichVu
        pageable: {} as Pageable,
    }),
    actions: {
        async fetchServices() {
            try {
                const response = await fetch('http://localhost:8080/api/dich-vu/all');
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                const data = await response.json();

                // Ghi lại dữ liệu để kiểm tra
                console.log(data);

                // Gán trực tiếp dữ liệu từ phản hồi API
                this.services = data.content; // Chỉ định trực tiếp
                this.pageable = data.page; // Cập nhật pageable
            } catch (error) {
                console.error('Error fetching services:', error);
            }
        }
        ,
        // Hàm loại bỏ hàm và các thuộc tính không cần thiết
        cleanPayload(payload: any) {
            return JSON.parse(JSON.stringify(payload, (key, value) => {
                if (typeof value === 'function') {
                    return undefined; // Bỏ qua hàm
                }
                return value;
            }));
        }
    },
});
