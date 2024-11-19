import {defineStore} from "pinia";
import type CaLichHen from "~/models/CaHen";
import {tr} from "cronstrue/dist/i18n/locales/tr";

interface CaLichHenStoreState {
    calichhen: CaLichHen[];
}

export const useCaLichHenStore = defineStore('useCalichhen', {
    state: (): CaLichHenStoreState => ({
        calichhen: [] as CaLichHen[]
    }),
    actions: {
        async fethcCaLichHen() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/all", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },

                });
                if (!response.ok) {
                    throw new Error("Failed to fetch users");
                }
                const data = await response.json();
                this.calichhen = data;
                return this.calichhen;
            } catch (e) {
                throw new Error("Failed to fetch users" + e);
            }
        },
        async themCaLichHen(caHen: CaLichHen) {
            console.log(caHen);

            // Hàm chuyển đổi định dạng thời gian sang HH:MM
            const formatTimeToHHMM = (time: string): string => {
                const date = new Date(time); // Chuyển chuỗi thời gian sang đối tượng Date
                const hours = date.getHours().toString().padStart(2, '0'); // Lấy giờ, thêm số 0 nếu cần
                const minutes = date.getMinutes().toString().padStart(2, '0'); // Lấy phút, thêm số 0 nếu cần
                return `${hours}:${minutes}`;
            };

            // Đảm bảo thoigianca có định dạng đúng
            const formattedTime = formatTimeToHHMM(caHen.thoigianca);
            console.log(formattedTime+"MEO MEO MEO")
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/add", {
                    headers: {
                        Authorization: `Bearer ${token}`, // Thêm dấu backtick để đúng cú pháp
                        'Content-Type': 'application/json', // Thêm header Content-Type
                    },
                    method: 'POST',
                    body: JSON.stringify({
                        'time': formattedTime, 
                        'name': caHen.tenca
                    })
                });

                if (!response.ok) {
                    throw new Error("Lỗi thêm ca lịch hẹn");
                }
            } catch (e) {
                throw new Error("Lỗi thêm ca lịch hẹn: " + e.message);
            }
        }
        ,
        async capNhatTrangThaiCa(caHen: CaLichHen) {
    const token = localStorage.getItem('access_token');
    try {
        const response = await fetch("http://localhost:8080/api/ca-lich-hen/cap-nhap-trang-thai-ca", {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            method: 'PUT',
            body: JSON.stringify({
                'idCaLichHen': caHen.id,
                'isConfirmed': !caHen.trangthai
            })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            console.error('Error response:', errorData);
            throw new Error(`Lỗi cập nhật ca lịch hẹn: ${response.status} ${response.statusText}`);
        }

        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('Lỗi khi cập nhật ca lịch hẹn:', error);
        throw error; // Re-throw the error for the caller to handle
    }
},
        async capNhatCaLichHen(caHen: CaLichHen) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/update", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    method: 'POST',
                    body: JSON.stringify(caHen)
                });
                if (!response.ok) {
                    throw new Error("Lỗi cập nhật ca lịch hẹn");
                }
                const data = await response.json();
                console.log(data)
            } catch (e) {
                throw new Error("Lỗi cập nhật ca lịch hẹn" + e );
            }
        }
    }
});