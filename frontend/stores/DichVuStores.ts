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
            const token = sessionStorage.getItem('access_token');
            try {
                const response = await fetch(API_ENDPOINTS.dichVu.addDichVu, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
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
        async updateDichVu(service) {
            const updateDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.updateDichVu+service.id;
            const token = sessionStorage.getItem('access_token');

            try {
                const response = await fetch(updateDichVuUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}` // Gửi token trong header
                    },
                    body: JSON.stringify(service),
                });

                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }

                const data = await response.json();
                return data;
            } catch (error) {
                console.error('Lỗi khi cập nhật dịch vụ:', error);
                throw error;
            }
        }
        ,

        // Xóa dịch vụ
        async deleteDichVu(serviceId: number) {
            const updateDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.deleteDichVu + serviceId;
            const token = sessionStorage.getItem('access_token');
            try {
                const response = await fetch(updateDichVuUrl, {
                    method: 'DELETE',
                    headers: { // Chú ý là phần headers phải nằm trong đối tượng này
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error(`Failed to delete service. Status: ${response.status}`);
                    return false;
                }

                this.services = this.services.filter(service => service.id !== serviceId);
                return { success: true };
            } catch (error) {
                console.error('Error deleting service:', error);
                return { success: false, message: response.status };
            }
        },

        async getDichVuByName(name: string) {
            const updateDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.getDichVuByName+ name;
            const token = sessionStorage.getItem('access_token');
            try {
                const response = await fetch(updateDichVuUrl,
                    {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        }
                }
                );

                if (!response.ok) {
                    throw new Error(`Failed to find service by name. Status: ${response.status}`);
                }

                const data = await response.json();
                this.services = data.content; // Cập nhật danh sách dịch vụ với kết quả tìm kiếm
            } catch (error) {
                console.error('Error finding service by name:', error);
            }
        },


        // Cập nhật Trạng thái dịch vụ
        async updateTTDV(serviceId: number) {
            const updateTTDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.updateTTDichVu+serviceId;
            const token = sessionStorage.getItem('access_token');

            try {
                const response = await fetch(updateTTDichVuUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}` // Gửi token trong header
                    },

                });

                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }

                const data = await response.json();
                return data;
            } catch (error) {
                console.error('Lỗi khi cập nhật dịch vụ:', error);
                throw error;
            }
        }
        ,

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
