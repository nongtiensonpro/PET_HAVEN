import type CaLichHen from "~/models/CaHen";
import { data } from "autoprefixer";
import type NgayNghi from "~/models/NgayNghi"

interface CaLichHenStoreState {
    calichhen: CaLichHen[];
    ngayNghi: NgayNghi[];
}

export const useCaLichHenStore = defineStore('useCalichhen', {
    state: (): CaLichHenStoreState => ({
        calichhen: [] as CaLichHen[],
        ngayNghi: [] as NgayNghi[]
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
                        // 'name': caHen.tenca
                    })
                });

                if (!response.ok) {
                    throw new Error("Lỗi thêm ca lịch hẹn");
                }
            } catch (e) {
                throw new Error("Lỗi thêm ca lịch hẹn: " + e);
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
                        idCaLichHen: caHen.id,
                        isConfirmed: false,
                    }),
                });

                if (response.status === 202) {
                    const data = await response.json();

                    // Hiển thị danh sách lịch trùng và yêu cầu xác nhận
                    const message = `${data.message}\n\nDanh sách lịch trùng:\n` +
                        data.lichDaDat
                            .map((lich: any) =>
                                `- Dịch vụ: ${lich.dichvu.tendichvu}, Ngày: ${lich.date}, Ca: ${lich.idcalichhen.tenca}`
                            )
                            .join("\n");

                    // Hỏi người dùng xác nhận
                    const isConfirmed = confirm(`${message}\n\nBạn có chắc chắn muốn tiếp tục không?`);

                    if (isConfirmed) {
                        // Nếu người dùng đồng ý, gửi yêu cầu xác nhận
                        const confirmResponse = await fetch("http://localhost:8080/api/ca-lich-hen/cap-nhap-trang-thai-ca", {
                            method: "PUT",
                            headers: {
                                Authorization: `Bearer ${token}`,
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify({
                                idCaLichHen: caHen.id,
                                isConfirmed: true,
                            }),
                        });

                        if (confirmResponse.ok) {
                            console.log("Cập nhật ca lịch hẹn thành công!");
                        } else {
                            console.log("Cập nhật ca lịch hẹn thất bại!");
                        }
                    } else {
                        console.log("Bạn đã hủy thao tác cập nhật ca lịch hẹn.");
                    }
                } else if (response.ok) {
                    // Nếu phản hồi thành công và không có lịch trùng
                    console.log("Cập nhật ca lịch hẹn thành công!");
                } else {
                    // Xử lý lỗi khác
                    const errorText = await response.text();
                    console.log(`Lỗi cập nhật ca lịch hẹn: ${errorText}`);
                }
            } catch (error) {
                // Xử lý lỗi kết nối hoặc ngoại lệ
                console.error("Lỗi khi cập nhật ca lịch hẹn:", error);
                console.log("Có lỗi xảy ra khi cập nhật ca lịch hẹn.");
            }
        },
        async capNhatCaLichHen(caHen: CaLichHen) {
            console.log(caHen + "Gau Gau")
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
                        // 'name': caHen.tenca
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
        },
        async themNgayNghi(ngayNghi: string) {
            console.log(ngayNghi + "Gau Gau")
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch("http://localhost:8080/api/ca-lich-hen/cap-nhap-ngay-nghi", {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                    method: 'PUT',
                    body: JSON.stringify({
                        'date': ngayNghi.valueOf()
                    })
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Lỗi cập nhật ngày nghỉ: ${response.status} ${errorText}`);
                }

                const data = await response.json();
                console.log("Ngày nghỉ đã được cập nhật:", data);
                return data;
            } catch (error) {
                console.error("Lỗi khi cập nhật ngày nghỉ:", error);
                throw error; // Re-throw the error for the caller to handle
            }
        },
        async huyNgayNghi(ngayNghi: NgayNghi) {
            const idNgayNghi = ngayNghi.id;
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch(`http://localhost:8080/api/ngay-nghi/doi-trang-thai-nghi?idNgayNghi=${idNgayNghi}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                console.log("Ngày nghỉ đã được hủy:", data);
                return data;
            } catch (e) {
                console.error("Lỗi khi hủy ngày nghỉ:", e);
                throw e;
            }
        },
        async fetchNgayNghi() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/ngay-nghi/all', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                });
                if (!response.ok) {
                    throw new Error("Failed to fetch ngày nghỉ");
                }
                const data = await response.json();
                this.ngayNghi = data;
                return this.ngayNghi;
            } catch (e) {
                console.error("Error fetching ngày nghỉ:", e);
                throw e;
            }
        }
    }
});