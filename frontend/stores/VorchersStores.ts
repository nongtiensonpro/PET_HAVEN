import {defineStore} from 'pinia'
import Voucher from '~/models/Voucher'

interface VorchersState {
    ListVoucher: Voucher[]
}

export const useVoucherStore = defineStore('voucherInState', {
    state: (): VorchersState => ({
        ListVoucher: []
    }),
    actions: {
        async fetchVoucher() {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/giam-gia/all', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    }
                })
                const data = await response.json();
                this.ListVoucher = data;
                console.log(data)
            } catch (e) {
                console.log(e)
            }
        },
        async addVoucher(voucher: Voucher) {
            console.log(voucher)
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/giam-gia/add', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(voucher)
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                console.log(data);
                return data;
            } catch (e) {
                console.error('Error adding voucher:', e);
                throw e;
            }
        },
        async updateVoucher(voucher: Voucher) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/giam-gia/update/' + voucher.id, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify(voucher)
                })
                const data = await response.json();
                console.log(data)
            } catch (e) {
                console.log(e)
            }
        },
        async updateTrangThaiVoucher(id: Number) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/giam-gia/update/' + id, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                })
                const data = await response.json();
                console.log(data)
            } catch (e) {
                console.log(e)
            }
        }

    }
})