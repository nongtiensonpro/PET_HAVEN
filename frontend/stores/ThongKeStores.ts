interface ThongKeItem {
    date: Date;
    amount: number;
}

export const useThongKeStore = defineStore('thongKe', {
    state: () => ({
        thongKeItems: [] as ThongKeItem[],
        error: null as string | null,
        topUsers: [] as { userId: any, totalAmount: number }[]
    }),
    actions: {
        async getUserThongKeTheoNgay(ngayBatDau: string, ngayKetThuc: string) {
            this.error = null;
            console.log('Ngay bat dau:', ngayBatDau + ' - Ngay ket thuc:', ngayKetThuc);
            try {
                const token = localStorage.getItem('access_token');
                if (!token) {
                    throw new Error('No access token found');
                }

                const url = new URL('http://localhost:8080/api/thong-ke/ngay');

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'startDate': ngayBatDau,
                        'endDate': ngayKetThuc
                    })
                });

                if (!response.ok) {
                    const errorBody = await response.text();
                    console.error('Error response:', errorBody);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${errorBody}`);
                }

                const responseData = await response.json();
                console.log(responseData)
                if (responseData.data && Array.isArray(responseData.data)) {
                    this.thongKeItems = responseData.data.map((item: any) => ({
                        date: new Date(item[0]),
                        amount: item[1]
                    }));
                } else {
                    console.error('Unexpected data format:', responseData);
                    throw new Error('Unexpected data format received from server');
                }
            } catch (error) {
                console.error('Error fetching thong ke data:', error);
                this.error = error instanceof Error ? error.message : 'An unknown error occurred';
                throw error;
            }
        },
        async getUserThongKeTheoThang(ngayBatDau: string, ngayKetThuc: string) {
            this.error = null;
            console.log('Ngay bat dau:', ngayBatDau + ' - Ngay ket thuc:', ngayKetThuc);
            try {
                const token = localStorage.getItem('access_token');
                if (!token) {
                    throw new Error('No access token found');
                }

                const url = new URL('http://localhost:8080/api/thong-ke/thang');

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'startDate': ngayBatDau,
                        'endDate': ngayKetThuc
                    })
                });

                if (!response.ok) {
                    const errorBody = await response.text();
                    console.error('Error response:', errorBody);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${errorBody}`);
                }

                const responseData = await response.json();
                console.log(responseData)
                if (responseData.data && Array.isArray(responseData.data)) {
                    this.thongKeItems = responseData.data.map((item: any) => ({
                        date: new Date(item[0]),
                        amount: item[1]
                    }));
                } else {
                    console.error('Unexpected data format:', responseData);
                    throw new Error('Unexpected data format received from server');
                }
            } catch (error) {
                console.error('Error fetching thong ke data:', error);
                this.error = error instanceof Error ? error.message : 'An unknown error occurred';
                throw error;
            }
        },
        async getUserThongKeTheoNam(ngayBatDau: string, ngayKetThuc: string) {
            this.error = null;
            console.log('Ngay bat dau:', ngayBatDau + ' - Ngay ket thuc:', ngayKetThuc);
            try {
                const token = localStorage.getItem('access_token');
                if (!token) {
                    throw new Error('No access token found');
                }

                const url = new URL('http://localhost:8080/api/thong-ke/nam');

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'startDate': ngayBatDau,
                        'endDate': ngayKetThuc
                    })
                });

                if (!response.ok) {
                    const errorBody = await response.text();
                    console.error('Error response:', errorBody);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${errorBody}`);
                }

                const responseData = await response.json();
                console.log(responseData)
                if (responseData.data && Array.isArray(responseData.data)) {
                    this.thongKeItems = responseData.data.map((item: any) => ({
                        date: new Date(item[0]),
                        amount: item[1]
                    }));
                } else {
                    console.error('Unexpected data format:', responseData);
                    throw new Error('Unexpected data format received from server');
                }
            } catch (error) {
                console.error('Error fetching thong ke data:', error);
                this.error = error instanceof Error ? error.message : 'An unknown error occurred';
                throw error;
            }
        },

        async getTop10User(ngayBatDau: string, ngayKetThuc: string) {
            this.error = null;
            console.log('Ngay bat dau:', ngayBatDau + ' - Ngay ket thuc:', ngayKetThuc);
            try {
                const token = localStorage.getItem('access_token');
                if (!token) {
                    throw new Error('No access token found');
                }

                const url = new URL('http://localhost:8080/api/thong-ke/top10User');

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'startDate': ngayBatDau,
                        'endDate': ngayKetThuc
                    })
                });

                if (!response.ok) {
                    const errorBody = await response.text();
                    console.error('Error response:', errorBody);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${errorBody}`);
                }

                const responseData = await response.json();
                if (responseData.data && Array.isArray(responseData.data)) {
                    this.topUsers = responseData.data.map((item: any) => ({
                        userId: item[0],
                        totalAmount: item[1]
                    }));
                } else {
                    console.error('Unexpected data format:', responseData);
                    throw new Error('Unexpected data format received from server');
                }
                return responseData;
            } catch (error) {
                console.error('Error fetching top 10 users data:', error);
                this.error = error instanceof Error ? error.message : 'An unknown error occurred';
                throw error;
            }
        },
        async thongTinKinhDoanh(startDate: string, endDate: string) {
            try {
                const responseNgay = await fetch('/api/thong-ke/ngay', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({startDate, endDate}),
                });
                const dataNgay = await responseNgay;

                const responseThang = await fetch('/api/thong-ke/thang', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({startDate, endDate}),
                });
                const dataThang = await responseThang;
                console.log(dataNgay + '=====' + dataThang);
                return dataNgay + '=====' + dataThang;
            } catch (error) {
                console.error('Error fetching statistics data:', error);
            }
        },
        async getDoanhThuTheoDichVu(ngayBatDau: string, ngayKetThuc: string) {
            this.error = null;
            try {
                const token = localStorage.getItem('access_token');
                if (!token) {
                    throw new Error('No access token found');
                }

                const url = new URL('http://localhost:8080/api/thong-ke/doanh-thu-theo-dich-vu');

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        'startDate': ngayBatDau,
                        'endDate': ngayKetThuc
                    })
                });

                if (!response.ok) {
                    const errorBody = await response.json();
                    console.error('Error response:', errorBody);
                    throw new Error(`HTTP error! status: ${response.status}, body: ${JSON.stringify(errorBody)}`);
                }

                const responseData = await response.json();
                console.log('Response data:', responseData.data.data);

                return responseData.data.data
            } catch (error) {
                console.error('Error fetching doanh thu dich vu:', error);
                this.error = error instanceof Error ? error.message : 'An unknown error occurred';
                throw error;
            }
        },
    },
});