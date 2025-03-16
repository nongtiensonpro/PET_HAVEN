import DichVu from '../models/DichVu';
import Pageable from '../models/Pageable';
import API_ENDPOINTS from '../apiconfig/ApiConfig';

export const useServiceStore = defineStore('serviceStore', {
    state: () => ({
        services: [] as DichVu[],
        pageable: {} as Pageable,
    })
    ,
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
        async addDichVu(service: DichVu, file?: File) {
            const token = localStorage.getItem('access_token');

            const formData = new FormData();

            const dichVuDTO = {
                tenDichVu: service.tendichvu,
                moTa: service.mota,
                trangThai: service.trangthai,
                tuyChonDichVu: service.tuyChonDichVus.map(tuyChon => ({
                    tenTuyChon: tuyChon.tenTuyChon,
                    moTa: tuyChon.moTa,
                    trangThai: tuyChon.trangThai,
                    tuyChonCanNang: tuyChon.tuyChonCanNangs.map(canNang => ({
                        canNangMin: canNang.canNangMin,
                        canNangMax: canNang.canNangMax,
                        giaTien: canNang.giaTien,
                        trangThai: true
                    }))
                }))
            };

            formData.append('dichVu', JSON.stringify(dichVuDTO));

            if (file) {
                formData.append('file', file);
            }

            console.log('Sending data to server:', {
                service: service,
                file: file ? file.name : 'No file',
                formData: Object.fromEntries(formData)
            });

            try {
                const response = await fetch(API_ENDPOINTS.API_ENDPOINTS.dichVu.addDichVu, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                    body: formData
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const data = await response.json();
                console.log("Dịch vụ đã được thêm:", data);

                return {success: true, message: 'Thêm dịch vụ thành công'};
            } catch (error) {
                console.error('Lỗi khi thêm dịch vụ:', error);
                return {success: false, message: 'Lỗi thêm dịch vụ: '};
            }
        },
        async updateDichVu(service: DichVu) {
            const updateDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.updateDichVu + service.id;
            const token = localStorage.getItem('access_token');
            const formDataUpdate = new FormData();

            formDataUpdate.append('tenDichVu', service.tendichvu);
            formDataUpdate.append('moTa', service.mota);


            const fileInput = document.querySelector('#fileUpdate') as HTMLInputElement;
            if (fileInput && fileInput.files && fileInput.files.length > 0) {
                formDataUpdate.append('file', fileInput.files[0]);
            }
            try {
                const response = await fetch(updateDichVuUrl, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                    body: formDataUpdate,
                });

                if (!response.ok) {
                    throw new Error(`Đã có lỗi xảy ra: ${response.status}`);
                }

                const data = await response.json();
                console.log("Dữ liệu nhận được từ server:", data); // In ra dữ liệu nhận được
                this.services = data.content;
                this.pageable = data.page;
                return {success: true, message: 'Lưu thành công'};
            } catch (error) {
                return {success: false, message: `Đã có lỗi xảy ra: `};
            }
        }
        ,
        async deleteDichVu(serviceId: number) {
            const updateDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.deleteDichVu + serviceId;
            const token = localStorage.getItem('access_token');
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
                return {success: true};
            } catch (error) {
                console.error('Error deleting service:', error);
                return {success: false};
            }
        },
        async getDichVuByName(name: string) {
            const updateDichVuUrl = `${API_ENDPOINTS.API_ENDPOINTS.dichVu.getDichVuByName}?namedv=${encodeURIComponent(name)}`;
            const token = localStorage.getItem('access_token');

            if (!token) {
                return {status: false, message: 'Vui lòng đăng nhập lại để sử dụng dịch vụ'};
            }
            try {
                const response = await fetch(updateDichVuUrl, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.status === 200) {
                    const data = await response.json();
                    this.services = data.content;
                    this.pageable = data.page;
                    console.log("Dữ liệu trả về: ", data.content);
                    return {status: true, message: 'Tìm kiếm hoạt động ổn'};
                } else if (response.status === 404) {
                    return {status: false, message: 'Không tìm thấy gì'};
                } else if (response.status === 500) {
                    return {status: false, message: 'Lỗi máy chủ vui lòng thử lại'};
                } else {
                    return {status: false, message: `Lỗi không xác định mã ${response.status}`};
                }
            } catch (error) {
                return {status: false, message: 'Đã có lỗi máy chủ xảy ra vui lòng thử lại'};
            }
        }
        ,


        // Cập nhật Trạng thái dịch vụ
        async updateTTDV(serviceId: number) {
            const updateTTDichVuUrl = API_ENDPOINTS.API_ENDPOINTS.dichVu.updateTTDichVu + serviceId;
            const token = localStorage.getItem('access_token');

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
                return {success: true};
            } catch (error) {
                console.error('Lỗi khi cập nhật dịch vụ:', error);
                throw error;
            }
        }
        ,
        async updateTTHien(serviceId: number) {
            const updateTTDichVuUrl = "http://localhost:8080/api/dich-vu/update-hien/" + serviceId;
            const token = localStorage.getItem('access_token');

            try {
                const response = await fetch(updateTTDichVuUrl, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },

                });

                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }

                const data = await response.json();
                return {success: true};
            } catch (error) {
                console.error('Lỗi khi cập nhật dịch vụ:', error);
                throw error;
            }
        },
        async updateService(dichVu: DichVu) {
            const token = localStorage.getItem('access_token');
            const formData = new FormData();

            const dichVuDTO = {
                tenDichVu: dichVu.tendichvu,
                moTa: dichVu.mota,
                trangThai: dichVu.trangthai,
                tuyChonDichVu: dichVu.tuyChonDichVus.map(tuyChon => ({
                    id:tuyChon.id,
                    tenTuyChon: tuyChon.tentuychon,
                    moTa: tuyChon.mota,
                    trangThai: tuyChon.trangthai,
                    tuyChonCanNang: tuyChon.tuyChonCanNangs.map(canNang => ({
                        id: canNang.id,
                        canNangMin: canNang.cannangmin,
                        canNangMax: canNang.cannangmax,
                        giaTien: canNang.giatien,
                        trangThai: canNang.trangthai
                    }))
                }))
            };

            formData.append('dichVu', JSON.stringify(dichVuDTO));

            const fileInput = document.querySelector('#fileUpdate') as HTMLInputElement;
            if (fileInput && fileInput.files && fileInput.files.length > 0) {
                formData.append('file', fileInput.files[0]);
            }

            try {
                const response = await fetch(`${API_ENDPOINTS.API_ENDPOINTS.dichVu.updateDichVu}${dichVu.id}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    body: formData
                });

                if (!response.ok) {
                    throw new Error(`Đã có lỗi xảy ra: ${response.status}`);
                }

                await this.fetchServices();
                return { success: true, message: 'Cập nhật dịch vụ thành công' };
            } catch (error) {
                console.error('Lỗi khi cập nhật dịch vụ:', error);
                throw error;
            }
        },
        async doiTrangThaiTuyChonDichVu(id : number){
            try {
                const response = await fetch('http://localhost:8080/api/dich-vu/update-trang-thai-tuy-chon/'+id,{
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                    },
                });
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }
                await this.fetchServices();
                return { success: true, message: 'Cập nhật trạng thái tuy chọn dịch vụ thành công' };
            } catch (error) {
                return { success: false, message: 'Cập nhật trạng thái tuy chọn dịch vụ thất bại' };
            }
        },
        async doiTrangThaiTuyChonCanNang(id : number){
            try {
                const response = await fetch('http://localhost:8080/api/dich-vu/update-trang-thai-can-nang/'+id,{
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('access_token')}`
                    },
                });
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }
                await this.fetchServices();
                return { success: true, message: 'Cập nhật trạng thái tuy cân nặng vụ thành công' };
            } catch (error) {
                return { success: false, message: 'Cập nhật trạng thái tuy chọn cân nặng thất bại' };
            }
        }
    },
});
