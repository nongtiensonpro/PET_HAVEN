import { defineStore } from 'pinia';
import DichVu from '../models/DichVu';
import Pageable from '../models/Pageable';
import API_ENDPOINTS from '../apiconfig/ApiConfig';

export const useServiceStore = defineStore('serviceStore', {
    state: () => ({
        services: [] as DichVu[],
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
                this.services = data.content;
                this.pageable = data.page;
            } catch (error) {
                console.error('Error fetching services:', error);
            }
        },

        // Thêm dịch vụ mới
        async addDichVu(service: DichVu) {
            try {
                const response = await fetch(API_ENDPOINTS.dichVu.addDichVu, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(this.cleanPayload(service))
                });

                if (!response.ok) {
                    throw new Error(`Failed to add service. Status: ${response.status}`);
                }

                const newService = await response.json();
                this.services.push(newService); // Thêm dịch vụ mới vào danh sách
            } catch (error) {
                console.error('Error adding service:', error);
            }
        },

        // Cập nhật dịch vụ
        async updateDichVu(service: DichVu) {
            try {
                const response = await fetch(`${API_ENDPOINTS.dichVu.updateDichVu}/${service.id}`, {
                    method: 'PUT', // Hoặc 'PATCH' tùy thuộc vào yêu cầu API của bạn
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(this.cleanPayload(service))
                });

                if (!response.ok) {
                    throw new Error(`Failed to update service. Status: ${response.status}`);
                }

                const updatedService = await response.json();
                // Cập nhật service trong danh sách
                const index = this.services.findIndex(s => s.id === service.id);
                if (index !== -1) {
                    this.services[index] = updatedService;
                }
            } catch (error) {
                console.error('Error updating service:', error);
            }
        },

        // Xóa dịch vụ
        async deleteDichVu(serviceId: number) {
            try {
                const response = await fetch(`${API_ENDPOINTS.dichVu.deleteDichVu}/${serviceId}`, {
                    method: 'DELETE',
                });

                if (!response.ok) {
                    throw new Error(`Failed to delete service. Status: ${response.status}`);
                }

                // Xóa service khỏi danh sách
                this.services = this.services.filter(service => service.id !== serviceId);
            } catch (error) {
                console.error('Error deleting service:', error);
            }
        },

        // Tìm kiếm dịch vụ theo tên
        async getDichVuByName(name: string) {
            try {
                const response = await fetch(`${API_ENDPOINTS.dichVu.getDichVuByName}?name=${encodeURIComponent(name)}`);

                if (!response.ok) {
                    throw new Error(`Failed to find service by name. Status: ${response.status}`);
                }

                const data = await response.json();
                this.services = data.content; // Cập nhật danh sách dịch vụ với kết quả tìm kiếm
            } catch (error) {
                console.error('Error finding service by name:', error);
            }
        },

        // Hàm làm sạch payload
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
