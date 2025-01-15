import type { HoaDonDoiDichVu } from "~/models/HoaDonDoiDV";
import { useToast } from 'vue-toastification';

interface State {
  listAllHoaDonDoiDichVu: HoaDonDoiDichVu[];
  listAllHoaDonDoiDichVuNhanVien: HoaDonDoiDichVu[];
  isLoading: boolean;
}

export const useDoiDichVuStores = defineStore('useDoiDichVuStores', {
  state: (): State => ({
    listAllHoaDonDoiDichVu: [],
    listAllHoaDonDoiDichVuNhanVien: [],
    isLoading: false
  }),

  actions: {
    async fetchAllHoaDonDoiDichVu() {
      const toast = useToast();
      this.isLoading = true;
      try {
        const token = localStorage.getItem('access_token');
        if (!token) {
          throw new Error('No access token found');
        }

        const response = await fetch('http://localhost:8080/api/hoa-don/all-hoa-don-doi-dich-vu', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        this.listAllHoaDonDoiDichVu = data;
      } catch (error) {
        console.error('Error fetching hoa don:', error);
        toast.error('Không thể tải danh sách hóa đơn đổi dịch vụ');
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async fetchAllHoaDonDoiDichVuNhanVien() {
      const toast = useToast();
      this.isLoading = true;
      try {
        const token = localStorage.getItem('access_token');
        if (!token) {
          throw new Error('No access token found');
        }

        const response = await fetch('http://localhost:8080/api/hoa-don/hoa-don-doi-dich-vu', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        this.listAllHoaDonDoiDichVuNhanVien = data;
      } catch (error) {
        console.error('Error fetching hoa don:', error);
        toast.error('Không thể tải danh sách hóa đơn đổi dịch vụ nhân viên');
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async thanhToanDichVuKhiSoTienThayDoiLonHon(idHoaDonDoiDV: number) {
      const toast = useToast();
      this.isLoading = true;
      try {
        const token = localStorage.getItem('access_token');
        if (!token) {
          throw new Error('No access token found');
        }

        const response = await fetch('http://localhost:8080/api/payPal/payment/create/thanh-toan-hdDoiDVOnline', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            'idHoaDonDoiDV': idHoaDonDoiDV.toString(),
          },
        });
        console.log('Response:', response);
        if (!response.ok) {
          const errorData = await response.json().catch(() => null);
          throw new Error(errorData?.message || `HTTP error! status: ${response.status}`);
        }
        toast.success('Đã tạo link thanh toán PayPal thành công');
      } catch (error) {
        console.error('Error creating PayPal payment:', error);
        toast.error('Không thể tạo link thanh toán PayPal');
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    async thanhToanDichVuKhiSoTienThayDoiBangHoacNhoHon(idHoaDonDoiDV: number) {
      const toast = useToast();
      this.isLoading = true;
      try {
        const token = localStorage.getItem('access_token');
        if (!token) {
          throw new Error('No access token found');
        }

        const response = await fetch(`http://localhost:8080/api/hoa-don/hoa-don-doi-dich-vu/${idHoaDonDoiDV}`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
        console.log('Response:', response);
        if (!response.ok) {
          const errorData = await response.json().catch(() => null);
          throw new Error(errorData?.message || `HTTP error! status: ${response.status}`);
        }

        toast.success('Đã xử lý thanh toán thành công');
      } catch (error) {
        console.error('Error processing payment:', error);
        toast.error('Không thể xử lý thanh toán');
        throw error;
      } finally {
        this.isLoading = false;
      }
    },
  },
});