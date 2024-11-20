import {defineStore} from "pinia";
import type CaLichHen from "~/models/CaHen";
import {tr} from "cronstrue/dist/i18n/locales/tr";
import {ca} from "cronstrue/dist/i18n/locales/ca";

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
            const formatTimeToHHMM = (time: string): string => {
                const date = new Date(time);
                const hours = date.getHours().toString().padStart(2, '0');
                const minutes = date.getMinutes().toString().padStart(2, '0');
                return `${hours}:${minutes}`;
            };
            const formattedTime = formatTimeToHHMM(caHen.thoigianca);
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
            const formatTimeToHHMM = (time: string | Date): string => {
                if (time instanceof Date) {
                    return `${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`;
                }

                const timeRegex = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9])(:([0-5][0-9]))?$/;
                const match = time.match(timeRegex);
                if (match) {
                    return `${match[1].padStart(2, '0')}:${match[2]}`;
                }

                console.error("Invalid time format:", time);
                return "00:00";
            };
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/update", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                    method: 'PUT',
                    body: JSON.stringify({
                        'id': caHen.id,
                        'time': formatTimeToHHMM(caHen.thoigianca),
                        'name': caHen.tenca
                    })
                });
                if (!response.ok) {
                    throw new Error("Lỗi cập nhật ca lịch hẹn");
                }
                const data = await response;
                console.log(data);
            } catch (e) {
                throw new Error("Lỗi cập nhật ca lịch hẹn: " + e);
            }
        }
    }
});