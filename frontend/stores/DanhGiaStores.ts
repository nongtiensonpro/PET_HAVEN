import {defineStore} from 'pinia';
import DanhGia from '../models/DanhGia';
import type ChiTietDanhGia from "~/models/ChiTietDanhGia";

export const useDanhGiaStore = defineStore('danhGiaStore', {
        state: () => ({
            danhGias: [] as DanhGia[],
            danhGiaLichHen: null as ChiTietDanhGia | null
        }),
        actions: {
            async fetchDanhGiaByDichVu(idDichVu: number) {
                try {
                    const response = await fetch(`http://localhost:8080/api/danh-gia/danh-gia-theo-dich-vu/${idDichVu}`);
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    const data = await response.json();
                    this.danhGias = data; // Giả sử API trả về danh sách đánh giá trực tiếp
                } catch (error) {
                    console.error('Error fetching danh gia:', error);
                }
            },

            async addDanhGia(moTa: String, idLichHen: Number, star: Number) {
                const token = localStorage.getItem('access_token');
                try {
                    const requestBody = {
                        moTa: moTa,
                        idLichHen: idLichHen,
                        star: star
                    };

                    const response = await fetch(`http://localhost:8080/api/danh-gia/add`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify(requestBody)
                    });

                    if (!response.ok) {
                        throw new Error(`Failed to add danh gia. Status: ${response.status}`);
                    }

                    const newDanhGia = await response.json();
                    this.danhGias.push(newDanhGia);
                    return {success: true, message: 'Thêm đánh giá thành công'};
                } catch (error) {
                    console.error('Error adding danh gia:', error);
                    return {success: false, message: 'Lỗi thêm đánh giá'};
                }
            },
            async anDanhGia(danhGiaId: number) {
                const token = localStorage.getItem('access_token');
                try {
                    const response = await fetch(`http://localhost:8080/api/danh-gia/an-danh-gia`, {
                        method: 'PUT',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(danhGiaId)  // Send the ID directly, not as an object
                    });

                    if (!response.ok) {
                        const errorMessage = `Failed to ẩn danh gia. Status: ${response.status}`;
                        console.error(errorMessage);
                        return {success: false, message: errorMessage};
                    }

                    const updatedDanhGia = await response.json();
                    this.danhGias = this.danhGias.filter(danhGia => danhGia.id !== danhGiaId);
                    return {success: true, data: updatedDanhGia};
                } catch (error) {
                    console.error('Error ẩn danh gia:', error);
                    return {success: false, message: 'Lỗi ẩn đánh giá'};
                }
            },
            async chitietdanhgiatheolichhen(idLich: String) {
                const token = localStorage.getItem('access_token');
                try {
                    const response = await fetch(`http://localhost:8080/api/danh-gia/thong-tin-danh-gia?idLich=` + idLich, {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                    if (!response.ok) {
                        throw new Error(`Failed to delete danh gia. Status: ${response.status}`);
                    }
                    const danhGia: ChiTietDanhGia = await response.json();
                    this.danhGiaLichHen = danhGia;
                    return danhGia;
                } catch (error) {
                    console.error('Error deleting danh gia:', error);
                    return {success: false, message: 'Lỗi tìm đánh giá'};
                }
            },
            async capNhatDanhGia(idDanhGia: Number, moTa: String, idLichHen: Number, star: Number) {
                const token = localStorage.getItem('access_token');
                try {
                    const requestBody = {
                        idDanhGia: idDanhGia,
                        moTa: moTa,
                        idLichHen: idLichHen,
                        star: star
                    };

                    const response = await fetch(`http://localhost:8080/api/danh-gia/update`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify(requestBody)
                    });

                    if (!response.ok) {
                        throw new Error(`Failed to add danh gia. Status: ${response.status}`);
                    }

                    const newDanhGia = await response.json();
                    this.danhGias.push(newDanhGia);
                    return {success: true, message: 'Thêm đánh giá thành công'};
                } catch (error) {
                    console.error('Error adding danh gia:', error);
                    return {success: false, message: 'Lỗi thêm đánh giá'};
                }
            }
        },
    })
;