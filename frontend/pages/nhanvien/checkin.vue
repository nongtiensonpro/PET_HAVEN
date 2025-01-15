<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12 mb-4">
        <div class="card shadow">
          <div class="card-header text d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ t('todayAppointments') }}</h5>
            <button @click="refreshData" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> {{ t('refresh') }}
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>{{ t('time') }}</th>
                  <th>{{ t('appointmentId') }}</th>
                  <th>{{ t('customerEmail') }}</th>
                  <th>{{ t('petName') }}</th>
                  <th>{{ t('appointmentDate') }}</th>
                  <th>{{ t('amount') }}</th>
                  <th>{{ t('status') }}</th>
                  <th>{{ t('actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hoaDon in paginatedHoaDonList" :key="hoaDon.id">
                  <td>{{ hoaDon.idlichhen.idcalichhen.thoigianca }}</td>
                  <td>{{ hoaDon.idlichhen.id }}</td>
                  <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                  <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                  <td>{{ formatDate(hoaDon.idlichhen.date) }}</td>
                  <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                  <td><span class="badge bg-warning">{{ getTrangThai(hoaDon.idlichhen.trangthai) }}</span></td>
                  <td>
                    <button
                        v-if=" hoaDon.idlichhen.trangthai === 3 || hoaDon.idlichhen.trangthai === 4 ||hoaDon.idlichhen.trangthai === 6 ||hoaDon.idlichhen.trangthai === 8"
                        @click="thanhToanHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      {{ t('checkin') }}
                    </button>
                    <button
                        @click="viewHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      {{ t('viewDetails') }}
                    </button>
                    <button
                        @click="DoiDichVu(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      {{ t('change_service') }}
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
              <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage1 === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage1 - 1, 1)">{{
                        t('previous')
                      }}</a>
                  </li>
                  <li v-for="page in totalPages1" :key="page" class="page-item"
                      :class="{ active: currentPage1 === page }">
                    <a class="page-link" href="#" @click.prevent="changePage(page, 1)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage1 === totalPages1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage1 + 1, 1)">{{ t('next') }}</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12">
        <div class="card shadow">
          <div class="card-header text d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ t('paidInvoices') }}</h5>
            <button @click="refreshData" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> {{ t('refresh') }}
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>{{ t('invoiceId') }}</th>
                  <th>{{ t('appointmentId') }}</th>
                  <th>{{ t('customerEmail') }}</th>
                  <th>{{ t('petName') }}</th>
                  <th>{{ t('appointmentDate') }}</th>
                  <th>{{ t('amount') }}</th>
                  <th>{{ t('status') }}</th>
                  <th>{{ t('actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hoaDon in paginatedHoaDonThanhToanList" :key="hoaDon.id">
                  <td>{{ hoaDon.id }}</td>
                  <td>{{ hoaDon.idlichhen.id }}</td>
                  <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                  <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                  <td>{{ formatDate(hoaDon.date) }}</td>
                  <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                  <td><span class="badge bg-success">{{ getTrangThaiHoaDon(hoaDon.trangthai) }}</span></td>
                  <td>
                    <button @click="taiHoaDon(hoaDon.id)" class="btn btn-sm btn-outline-success">
                      <i class="fas fa-check-circle me-1"></i> {{ t('downloadInvoice') }}
                    </button>
                    <button
                        @click="viewHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      {{ t('viewDetails') }}
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
              <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage2 === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage2 - 1, 2)">{{
                        t('previous')
                      }}</a>
                  </li>
                  <li v-for="page in totalPages2" :key="page" class="page-item"
                      :class="{ active: currentPage2 === page }">
                    <a class="page-link" href="#" @click.prevent="changePage(page, 2)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage2 === totalPages2 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage2 + 1, 2)">{{ t('next') }}</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
      <div class="col-12">
        <div class="card shadow">
          <div class="card-header text d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ t('listOfInvoicesAfterServiceChange') }}</h5>
            <button @click="refreshData" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> {{ t('refresh') }}
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>{{ t('time') }}</th>
                  <th>{{ t('appointmentId') }}</th>
                  <th>{{ t('customerEmail') }}</th>
                  <th>{{ t('petName') }}</th>
                  <th>{{ t('appointmentDate') }}</th>
                  <th>{{ t('amount') }}</th>
                  <th>{{ t('status') }}</th>
                  <th>{{ t('note') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hoaDon in paginatedHoaDonDoiDichVuList" :key="hoaDon.id">
                  <td>{{ hoaDon.idhoadon.idlichhen.idcalichhen.thoigianca }}</td>
                  <td>{{ hoaDon.idhoadon.idlichhen.id }}</td>
                  <td>{{ hoaDon.nguoithanhtoan}}</td>
                  <td>{{ hoaDon.idhoadon.idlichhen.thucung.ten }}</td>
                  <td>{{ formatDate(hoaDon.ngaythanhtoan) }}</td>
                  <td>{{ formatCurrency(hoaDon.idhoadon.sotien ) }}</td>
                  <td><span class="badge bg-success">{{ getTrangThaiHoaDon(hoaDon.idhoadon.trangthai) }}
                  </span></td>
                  <td>{{ hoaDon.ghichu || '-' }}</td>
                </tr>
                </tbody>
              </table>
              <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage3 === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage3 - 1, 3)">{{
                        t('previous')
                      }}</a>
                  </li>
                  <li v-for="page in totalPages3" :key="page" class="page-item"
                      :class="{ active: currentPage3 === page }">
                    <a class="page-link" href="#" @click.prevent="changePage(page, 3)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage3 === totalPages3 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage3 + 1, 3)">{{ t('next') }}</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useCheckInStore} from '~/stores/CheckInStores'
import {ref, onMounted, onUnmounted, computed} from "vue";
import {useI18n} from 'vue-i18n';
import Swal from 'sweetalert2';
import HoaDonKhachHang from "~/models/HoaDonKhachHang";
import {useToast} from 'vue-toastification'
import {useDoiDichVuStores} from "~/stores/DoiDichVuStores";
import type {HoaDonDoiDichVu} from "~/models/HoaDonDoiDV";

const useDoiDichVu = useDoiDichVuStores();
const {t} = useI18n();
const toast = useToast();
const checkInStore = useCheckInStore()

const hoaDonList = ref<HoaDonKhachHang[]>([]);
const hoaDonThanhToanList = ref<HoaDonKhachHang[]>([]);
const listHoaDoiDoiDichVu = ref<HoaDonDoiDichVu[]>([]);

const itemsPerPage = 10;
const currentPage1 = ref(1);
const currentPage2 = ref(1);
const currentPage3 = ref(1);

const paginatedHoaDonList = computed(() => {
  const start = (currentPage1.value - 1) * itemsPerPage;
  return hoaDonList.value.slice(start, start + itemsPerPage);
});

const paginatedHoaDonThanhToanList = computed(() => {
  const start = (currentPage2.value - 1) * itemsPerPage;
  return hoaDonThanhToanList.value.slice(start, start + itemsPerPage);
});

const paginatedHoaDonDoiDichVuList = computed(() => {
  const start = (currentPage3.value - 1) * itemsPerPage;
  return listHoaDoiDoiDichVu.value.slice(start, start + itemsPerPage);
});

const totalPages1 = computed(() => Math.ceil(hoaDonList.value.length / itemsPerPage));
const totalPages2 = computed(() => Math.ceil(hoaDonThanhToanList.value.length / itemsPerPage));
const totalPages3 = computed(() => Math.ceil(listHoaDoiDoiDichVu.value.length / itemsPerPage));

const changePage = (newPage: number, tableNumber: number) => {
  if (tableNumber === 1) {
    currentPage1.value = newPage;
  } else if (tableNumber === 2) {
    currentPage2.value = newPage;
  } else {
    currentPage3.value = newPage;
  }
};

let refreshInterval: NodeJS.Timeout;

const fetchHoaDon = async () => {
  await checkInStore.fetchHoaDon();
  hoaDonList.value = checkInStore.ListHoaDon;
};

const fetchHoaDonThanhToan = async () => {
  await checkInStore.fetchHoaDonDaThanhToan();
  hoaDonThanhToanList.value = checkInStore.ListHoaDonDaThanhToan;
  console.log(hoaDonThanhToanList.value + ' Hóa đơn đã thanh toán')
};

const fetchHoaDoiDoiDichVu = async () => {
  await useDoiDichVu.fetchAllHoaDonDoiDichVuNhanVien();
  listHoaDoiDoiDichVu.value = useDoiDichVu.listAllHoaDonDoiDichVuNhanVien;
  console.log(listHoaDoiDoiDichVu + ' Hóa đơn đã đối dịch vụ nhân viên')
}

const refreshData = () => {
  fetchHoaDon();
  fetchHoaDonThanhToan();
  fetchHoaDoiDoiDichVu();
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'USD'}).format(amount);
};

const getTrangThai = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    0: t('statusSuccess'),
    1: t('statusChanged'),
    2: t('statusCancelled'),
    3: t('statusPendingPayment'),
    4: t('statusPendingConfirmation'),
    5: t('statusEmpty'),
    6: t('statusPaymentSuccess'),
    7: t('statusRefunded'),
    8: t('statusPendingService')
  };
  return trangThaiMap[status] || t('statusUnknown');
};

const getTrangThaiHoaDon = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    2: t('invoicePaid'),
    1: t('invoicePending'),
    3: t('invoiceCancelled'),
    4: t('invoiceRefunded')
  };
  return trangThaiMap[status] || t('statusUnknown');
};

const taiHoaDon = async (id: number) => {
  window.open(`/api/hoadon/download/${id}`, '_blank');
};

const thanhToanHoaDon = async (id: number) => {
  const result = await Swal.fire({
    title: t('confirmPayment'),
    text: t('confirmPaymentMessage'),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });

  if (result.isConfirmed) {
    try {
      await checkInStore.checkIn(id);
      toast.success(t('paymentSuccess'));
      refreshData();
    } catch (error) {
      toast.error(t('paymentError'));
    }
  }
};

onMounted(() => {
  refreshData();

  refreshInterval = setInterval(refreshData, 60 * 1000);
});

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval);
});

const viewHoaDon = (id: number) => {
  navigateTo(`/nhanvien/chitiethoadon/${id}`);
};

const DoiDichVu = (id: number) => {
  navigateTo(`/thaydoidichvu/${id}`)
}
</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
</style>