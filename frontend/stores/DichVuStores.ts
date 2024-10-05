import { defineStore } from 'pinia';
import DichVu from '../models/DichVu';
import Pageable from '../models/Pageable';
import API_ENDPOINTS from '../apiconfig/ApiConfig';

export const useServiceStore = defineStore('serviceStore', {
    state: () => ({
        services: [] as DichVu[],  // Sử dụng interface DichVu
        pageable: {} as Pageable,
    }),
    actions: {
        async fetchServices() {
            try {
                const response = await fetch(API_ENDPOINTS.API_ENDPOINTS.dichVu.getAll);

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                const data = await response.json();

                // Gán trực tiếp dữ liệu từ phản hồi API
                this.services = data.content; // Chỉ định trực tiếp
                this.pageable = data.page; // Cập nhật pageable
            } catch (error) {
                console.error('Error fetching services:', error);
            }
        }
        ,
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