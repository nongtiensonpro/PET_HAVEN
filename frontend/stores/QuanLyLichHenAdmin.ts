import HoaDonKhachHang from "~/models/HoaDonKhachHang";

interface QuanLyLichHenAdminState {
    hoaDonKhachHangs: HoaDonKhachHang[];
}

export const useQuanLyLichHenAdminStore = defineStore('quanLyLichHenAdminStore', {
    state: (): QuanLyLichHenAdminState => ({
        hoaDonKhachHangs: []
    }),
    actions: {
        async fetchHoaDonKhachHangs() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/hoa-don/all', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (!response.ok) {
                    console.error(`HTTP error! status: ${response.status}`);
                    return;
                }
                const data = await response.json();
                console.log('Hoa don:', data);
                this.hoaDonKhachHangs = data;
                return data;
            } catch (error) {
                console.error('Error fetching hoa don:', error);
            }
        },
        async thayDoiTrangThai(id: number, idTrangThai: number) {
            console.log('id'+ id +'thay doi trang thai thanh'+ idTrangThai);
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/lich-hen/updateTrangThai/${id}/${idTrangThai}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    throw new Error(`Lỗi cập nhật trạng thái. Mã lỗi: ${response.status}`);
                }
                const updatedLichHen = await response.json();
                console.log('Trạng thái lịch hẹn đã được cập nhật:', updatedLichHen);
                return updatedLichHen;
            } catch (error) {
                console.error('Lỗi khi cập nhật trạng thái lịch hẹn:', error);
                throw error;
            }
        },
        async doiThoiGian(thoiGian: String, idcalichhen: Number) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/lich-hen//update-time/` + thoiGian, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (!response.ok) {
                    console.error(`HTTP error! status: ${response.status}`);
                    throw new Error(`Failed to update status. Status: ${response.status}`);
                }
                console.log('Lich hen da duoc huy');
            } catch (error) {
                console.error('Error huy lich hen:', error);
                throw error;
            }
        },
        async thayDoiLichHenAdmin(id: string, date: string, idCalichHen: string) {
            const token = localStorage.getItem('access_token');
            if (!token) {
                console.error('No access token found');
                throw new Error('Authentication required');
            }

            try {
                const response = await fetch(`http://localhost:8080/api/dat-lich/update-time/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        date: date,
                        idcalichhen: idCalichHen
                    })
                });

                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    const data = await response.json();
                    if (!response.ok) {
                        console.error('Error response:', data);
                        throw new Error(`Failed to change appointment. Status: ${response.status}` + `id=` +id  +`date=:`+ date +`idcalichhen=`+ idCalichHen);
                    }
                    console.log('Appointment changed successfully:', data);
                    return data;
                } else {
                    const text = await response.text();
                    if (!response.ok) {
                        console.error('Error response:', text);
                        throw new Error(`Failed to change appointment. Status: ${response.status}` + `id=` +id  +`date=:`+ date +`idcalichhen=`+ idCalichHen);
                    }
                    console.log('Appointment changed successfully:', text);
                    return text;
                }
            } catch (error) {
                console.error('Error changing appointment time:', error);
                throw error;
            }
        }
    }
})