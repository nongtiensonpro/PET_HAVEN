import { defineStore } from 'pinia';
import DichVu from '../models/DichVu';
import CaHen from '../models/CaHen';
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu';

interface DatLichState {
    DichVu: DichVu[];
    CaLichHen: CaHen[];
    date: Date;
}

export const useDatLichStore = defineStore('datLichStore', {
    state: (): DatLichState => ({
        DichVu: [],
        CaLichHen: [],
        date: new Date()
    }),
    actions: {
        async fetchCaHen(date: Date = new Date()) {
            const token = sessionStorage.getItem('access_token');
            this.date = date;
            try {
                const response = await fetch(`http://localhost:8080/api/dat-lich/dat-lich-info?ngay=${date.toISOString().split('T')[0]}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                console.log(data);
                this.DichVu = data.dichVu;
                this.CaLichHen = data.CaLichHen;
            } catch (error) {
                console.error('Error fetching services:', error);
            }
        },
        async updateDatLichInfo(date: Date) {
            const token = sessionStorage.getItem('access_token');
            this.date = date;
            try {
                const response = await fetch(`http://localhost:8080/api/dat-lich/dat-lich-info?ngay=${date.toISOString()}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                console.log('Updated data:', data);
                this.DichVu = data.dichVu;
                this.CaLichHen = data.CaLichHen;
            } catch (error) {
                console.error('Error updating Dat Lich info:', error);
            }
        },
        async xacNhanDatLich() {
            const { getTempData } = useMauKhachDatDichVu();
            const token = sessionStorage.getItem('access_token');
            const dichVuVaThuCungKhachHangDat = getTempData();
        
            if (!dichVuVaThuCungKhachHangDat || !dichVuVaThuCungKhachHangDat.idlichhen) {
                console.error('Không có dữ liệu đặt lịch tạm thời hoặc dữ liệu không hợp lệ');
                return;
            }
        
            try {
                // Chuyển đổi ngày thành định dạng yyyy-mm-dd
                const formatDate = (date: string | number | Date) => {
                    if (date instanceof Date) {
                        return date.toISOString().split('T')[0];
                    }
                    const d = new Date(date);
                    if (isNaN(d.getTime())) {
                        throw new Error('Ngày không hợp lệ');
                    }
                    return d.toISOString().split('T')[0];
                };
        
                // Log dữ liệu trước khi gửi để kiểm tra
                console.log('Dữ liệu trước khi gửi:', dichVuVaThuCungKhachHangDat);
        
                // Kiểm tra và xử lý ngày
                let formattedDate;
                try {
                    formattedDate = formatDate(dichVuVaThuCungKhachHangDat.idlichhen.date);
                } catch (error) {
                    console.error('Lỗi khi định dạng ngày:', error);
                    throw new Error('Ngày đặt lịch không hợp lệ');
                }
        
                const response = await fetch('http://localhost:8080/api/dat-lich/xac-nhan-dat', {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        idThuCung: dichVuVaThuCungKhachHangDat.thucung,
                        date: formattedDate,
                        idcalichhen: dichVuVaThuCungKhachHangDat.idlichhen.id, // Giả sử có trường 'id'
                        idDichVu: dichVuVaThuCungKhachHangDat.idlichhen.dichvu // Giả sử có trường 'dichvu'
                    })
                });
        
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('Response Error:', errorText);
                    console.error('Date:', formattedDate);
                    throw new Error(`Lỗi khi xác nhận đặt lịch: ${response.status} ${response.statusText}. ${errorText}`);
                }
        
                const result = await response.json();
                console.log('Đặt lịch thành công:', result);
                // Xử lý kết quả thành công (ví dụ: hiển thị thông báo, cập nhật UI, ...)
        
            } catch (error: unknown) {
                if (error instanceof Error) {
                    console.error('Lỗi khi xác nhận đặt lịch:', error.message);
                } else {
                    console.error('Lỗi không xác định khi xác nhận đặt lịch');
                }
                // Xử lý lỗi (ví dụ: hiển thị thông báo lỗi cho người dùng)
                throw error;
            }
        }
    }
});