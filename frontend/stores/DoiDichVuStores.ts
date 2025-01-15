import type {HoaDonDoiDichVu} from "~/models/HoaDonDoiDV";


export const useDoiDichVuStores = defineStore('useDoiDichVuStores', {
    state: () => ({
        listAllHoaDonDoiDichVu: [] as HoaDonDoiDichVu[],
        listAllHoaDonDoiDichVuNhanVien: [] as HoaDonDoiDichVu[],

    }),
    actions: {
        async fetchAllHoaDonDoiDichVu() {
            try {
                const token = localStorage.getItem('access_token');
                const response = await fetch('http://localhost:8080/api/hoa-don/all-hoa-don-doi-dich-vu',
                    {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }
                )

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }
                const data = await response.json()
                this.listAllHoaDonDoiDichVu = data
                console.log('Fetched all hoa don sau khi doi dich vu  admin:', JSON.stringify(this.listAllHoaDonDoiDichVu).toString()  )
            }catch (e) {
                console.error('Error fetching hoa don:', e)
            }
        },
        async fetchAllHoaDonDoiDichVuNhanVien(){
            try {
                const token = localStorage.getItem('access_token');
                const response = await fetch('http://localhost:8080/api/hoa-don/hoa-don-doi-dich-vu',
                    {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }
                )

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }
                console.log(response+'miu miu miu')
                const data = await response.json()
                this.listAllHoaDonDoiDichVuNhanVien = data
                console.log('Fetched all hoa don sau khi doi dich vu nhan vien:', JSON.stringify(this.listAllHoaDonDoiDichVuNhanVien).toString()  )
            }catch (e) {
                console.error('Error fetching hoa don:', e)
            }
        }

    },
})