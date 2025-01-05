<template>
  <div class="container">
    <div class="card">
      <div class="card-header text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0 text fs-4">{{ t('allAppointments') }}</h5>
        <button @click="fetchHoaDon" class="btn btn-sm btn-outline-success">
          {{ t('refresh') }}
        </button>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <input v-model="searchQuery" @input="handleSearch" type="text" class="form-control"
            :placeholder="t('searchAppointmentPlaceholder')">
        </div>
        <table class="table">
          <thead>
            <tr>
              <th>{{ t('appointmentId') }}</th>
              <th>{{ t('customerEmail') }}</th>
              <th>{{ t('petName') }}</th>
              <th>{{ t('time') }}</th>
              <th>{{ t('appointmentDate') }}</th>
              <th>{{ t('status') }}</th>
              <th>{{ t('actions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="lichhen in paginatedHoaDon" :key="lichhen.id">
              <td>{{ lichhen.id }}</td>
              <td>{{ lichhen.emailNguoiDat }}</td>
              <td>{{ lichhen.thucung.ten }}</td>
              <td>{{ lichhen.idcalichhen.thoigianca }}</td>
              <td>{{ formatDate(lichhen.date) }}</td>
              <td><span class="badge bg-success">{{ getTrangThai(lichhen.trangthai) }}</span></td>
              <td>
                <div class="row">
                  <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-danger" @click="thayDoiThoiGian(lichhen)">
                      {{ t('changeTime') }}
                    </button>
                  </div>
                  <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-success" @click="thayDoiTrangThai(lichhen)">
                      {{ t('changeStatus') }}
                    </button>
                  </div>
                  <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-info" @click="viewHoaDon(lichhen.idcalichhen.id)">
                      {{ t('viewDetails') }}
                    </button>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="d-flex justify-content-center align-items-center mt-4">
          <div class="d-flex align-items-center">
            <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1" class="custom-button me-3">
              {{ t('previous') }}
            </button>
            <span class="fs-5 mx-3">{{ t('page') }} {{ currentPage }} / {{ totalPages }}</span>
            <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages" class="custom-button ms-3">
              {{ t('next') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onUnmounted, computed } from 'vue';
import HoaDonKhachHang from '~/models/HoaDonKhachHang';
import { useQuanLyLichHenAdminStore } from '~/stores/QuanLyLichHenAdmin';
import Swal from 'sweetalert2';
import { useToast } from 'vue-toastification';
import { useI18n } from 'vue-i18n';
import CalendarAdmin from "~/pages/admin/thaydoithoigian/[id].vue";
import type { Lichhen } from "~/models/LichSuDatLich";
import Thaydoitrangthai from "~/pages/admin/thaydoitrangthai/[id].vue";

const { t } = useI18n();
const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const lichhen = ref<Lichhen[]>([]);
const filteredHoaDon = ref<Lichhen[]>([]);
const selectedTrangThai = ref<number>(0);
const toast = useToast();
let refreshInterval: NodeJS.Timeout;
const itemsPerPage = 5;
const currentPage = ref(1);
const searchQuery = ref('');
const selectedLichHen = ref<Lichhen | null>(null);

function selectLichHen(lichHenItem: Lichhen) {
  selectedLichHen.value = lichHenItem;
}

onMounted(() => {
  fetchHoaDon();
  refreshInterval = setInterval(fetchHoaDon, 5 * 60 * 1000);
});

const totalPages = computed(() => Math.ceil(filteredHoaDon.value.length / itemsPerPage));

const paginatedHoaDon = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredHoaDon.value.slice(start, end);
});

const handleSearch = () => {
  filteredHoaDon.value = lichhen.value.filter(hoaDon =>
    hoaDon.emailNguoiDat.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    hoaDon.thucung.ten.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    hoaDon.dichvu.tendichvu.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
  currentPage.value = 1;
};

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval);
});

const fetchHoaDon = async () => {
  try {
    await useQuanLyAdmin.fetchHoaDonKhachHangs();
    lichhen.value = useQuanLyAdmin.hoaDonKhachHangs;
    filteredHoaDon.value = lichhen.value;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu:', error);
    toast.error(t('loadDataError'));
  }
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'USD' }).format(amount);
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

async function doiNgayHen(ngayHen: string, idcalichhen: number) {
  const result = await Swal.fire({
    title: t('confirmTitle'),
    text: t('confirmChangeTime'),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no'),
  });
  if (result.isConfirmed) {
    try {
      await useQuanLyAdmin.doiThoiGian(ngayHen, idcalichhen);
      toast.success(t('changeTimeSuccess'), { timeout: 3000 });
      await fetchHoaDon();
    } catch (error) {
      toast.error(t('changeTimeFailed'), { timeout: 3000 });
    }
  }
}

const viewHoaDon = (id: number) => {
  navigateTo(`/admin/chitiethoadon/${id}`);
};

const thayDoiTrangThai = (lichHen: Lichhen) => {
  navigateTo(`/admin/thaydoitrangthai/${lichHen.id}`);
}

const thayDoiThoiGian = (lichHen: Lichhen) => {
  navigateTo(`/admin/thaydoithoigian/${lichHen.id}`);
}
</script>

<style scoped>
.chart {
  height: 400px;
}
</style>