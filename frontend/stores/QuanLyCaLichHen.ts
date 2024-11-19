import {defineStore} from "pinia";
import type CaLichHen from "~/models/CaHen";

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
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/add", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    method: 'POST',
                    body: JSON.stringify(caHen)
                });
                if (!response.ok) {
                    throw new Error("Lỗi thêm ca lịch hẹn");
                }
                const data = await response.json();
                console.log(data)
            } catch (e) {
                throw new Error("Lỗi thêm ca lịch hẹn" + e);
            }
        },
        async capNhatTrangThaiCa(caHen: CaLichHen) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/cap-nhap-trang-thai-ca", {
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
                throw new Error("Lỗi cập nhật ca lịch hẹn" +e );
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