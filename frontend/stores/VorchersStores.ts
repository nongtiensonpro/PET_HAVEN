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
                return data;
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
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(voucher)
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.text();
                console.log('Update response:', data);

                // Update the local state
                const index = this.ListVoucher.findIndex(v => v.id === voucher.id);
                if (index !== -1) {
                    this.ListVoucher[index] = voucher;
                }

                return data; // Return the response message
            } catch (e) {
                console.error('Error updating voucher:', e);
                throw e; // Re-throw the error for the component to handle
            }
        },
        async updateTrangThaiVoucher(id: Number) {
            const token = localStorage.getItem('access_token');
            try {
                const response = await fetch('http://localhost:8080/api/giam-gia/update-tt/' + id, {
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