<script setup lang="ts">
import {useUserStore} from '~/stores/user'
import {useQuanLyLichHenKhachHang} from '~/stores/QuanLyLichHenKhachHang'
import {onMounted, onUnmounted, ref, computed} from 'vue'

const userStore = useUserStore()
const lichHenStore = useQuanLyLichHenKhachHang()
const isLoading = ref(false);
const countdown = ref(10);
const showOverlay = ref(false);
const refreshInterval = ref(null);

definePageMeta({
  middleware: ['auth']
})

const fetchData = async () => {
  await lichHenStore.fetchAppointments();
}

onMounted(async () => {
  await fetchData();
  refreshInterval.value = setInterval(fetchData, 5*60 * 1000);
})

onUnmounted(() => {
  // Xóa interval khi component bị hủy
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value);
  }
})

function thayDoiLichHen(id: String) {
  return navigateTo(`/chi-tiet-lich/${id}`);
}

function chiTietLichHen(id: String) {
  return navigateTo(`/chitietlich_danhgia/${id}`);
}

function startCountdown() {
  showOverlay.value = true;
  isLoading.value = true;
  countdown.value = 10;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      isLoading.value = false;
      showOverlay.value = false;
    }
  }, 1000);
}

const token = localStorage.getItem('access_token');

function thanhToan(id: String) {
  const {getTempData} = useMauKhachDatDichVu()
  const tempData = computed(() => getTempData())

  startCountdown(); // Start the countdown

  fetch('http://localhost:8080/api/payPal/payment/create', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'idLichHen': id
    }
  })
      .then(response => response.text())
      .then(url => {
        if (url) {
          window.location.href = url;
        } else {
          console.error("Không thể tạo thanh toán");
          isLoading.value = false;
          showOverlay.value = false;
        }
      })
      .catch(error => {
        console.error("Lỗi:", error);
        isLoading.value = false;
        showOverlay.value = false;
      });
}

// Hàm format trạng thái
const getTrangThaiText = (trangthai: number) => {
  const trangThaiMap: Record<number, string> = {
    0: 'Thành công',
    1: 'Thất bại',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công',
    7: 'Đã hoàn tiền',
    8: 'Chờ sử dụng'
  }
  return trangThaiMap[trangthai] || 'Không xác định'
}

// Hàm format class theo trạng thái
const getTrangThaiClass = (trangthai: number) => {
  const classMap: Record<number, string> = {
    0: 'text-success',
    1: 'text-danger',
    2: 'text-danger',
    3: 'text-info',
    4: 'text-warning',
    5: 'text-secondary',
    6: 'text-success',
    7: 'text-success',
    8: 'text-warning'
  }
  return classMap[trangthai] || ''
}


const currentPage = ref(1);
const itemsPerPage = 10;

const searchTerm = ref('');

const filteredAppointments = computed(() => {
  if (!searchTerm.value) {
    return lichHenStore.appointments;
  }
  return lichHenStore.appointments.filter(appointment =>
      appointment.dichvu.tendichvu.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      appointment.thucung.ten.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      new Date(appointment.date).toLocaleDateString('vi-VN').includes(searchTerm.value) ||
      getTrangThaiText(appointment.trangthai).toLowerCase().includes(searchTerm.value.toLowerCase())
  );
});

const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return lichHenStore.appointments.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(lichHenStore.appointments.length / itemsPerPage);
});


const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};


</script>

<template>
  <div class="container py-4">
    <div class="card shadow-sm">
      <div class="card-header bg-light text-white py-3">
        <h4 class="mb-0 text fs-4">Quản lý lịch hẹn của {{ userStore.userInfo?.name }}</h4>
      </div>
      <div class="card-body">
        <div v-if="lichHenStore.isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
        </div>

        <div v-else-if="lichHenStore.error" class="alert alert-danger">
          {{ lichHenStore.error }}
          <button class="custom-button" @click="lichHenStore.clearError">Đóng</button>
        </div>

        <div v-else>
          <div class="mb-3">
            <input
                v-model="searchTerm"
                type="text"
                class="form-control"
                placeholder="Tìm kiếm lịch hẹn..."
            />
          </div>
          <div class="table-responsive">
            <table class="table table-hover">
              <thead class="table-light">
              <tr>
                <th>STT</th>
                <th>Dịch vụ</th>
                <th>Thú cưng</th>
                <th>Ngày hẹn</th>
                <th>Ca</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(item, index) in paginatedAppointments"
                  :key="item.id"
                  v-memo="[item.trangthai]">
                <td>{{ lichHenStore.currentPage * lichHenStore.pageSize + index + 1 }} : {{ item.id }}</td>
                <td>
                  <div>{{ item.dichvu.tendichvu }}</div>
                  <small class="text-muted">{{ item.dichvu.giatien.toLocaleString() }} USD</small>
                </td>
                <td>
                  <div>{{ item.thucung.ten }}</div>
                  <small class="text-muted">{{ item.thucung.giong }}</small>
                </td>
                <td>{{ new Date(item.date).toLocaleDateString('vi-VN') }}</td>
                <td>
                  {{ item.idcalichhen.tenca }}
                  <div><small>{{ item.idcalichhen.thoigianca }}</small></div>
                </td>
                <td>
                    <span :class="getTrangThaiClass(item.trangthai)">
                      {{ getTrangThaiText(item.trangthai) }}
                    </span>
                </td>
                <td>
                  <div class="row">
                    <div class="col">
                      <button v-if="item.trangthai === 4 || item.trangthai === 6"
                              class="custom-button"
                              @click="thayDoiLichHen(item.id)">
                        Hủy hoặc đổi lịch hẹn
                      </button>
                    </div>
                    <div class="col">
                      <button v-if="item.trangthai === 0 || item.trangthai === 6"
                              class="custom-button"
                              @click="chiTietLichHen(item.id)">
                        Chi tiết
                      </button>
                    </div>
                    <div class="col">
                      <button v-if="item.trangthai === 3 || item.trangthai === 4"
                              class="custom-button"
                              @click="thanhToan(item.id)">
                        Thanh toán
                      </button>
                    </div>
                  </div>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
          <div class="d-flex justify-content-between align-items-center mt-3">
            <div>
              Hiển thị {{ lichHenStore.paginatedAppointments.length }} / {{ lichHenStore.appointments.length }} lịch hẹn
            </div>
            <nav aria-label="Page navigation">
              <ul class="pagination">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <button class="custom-button" @click="prevPage" :disabled="currentPage === 1">Trước</button>
                </li>
                <li class="page-item" style="background: none !important;" v-for="page in totalPages" :key="page" :class="{ active: page === currentPage }">
                  <button class="text fs-4" style="background: none !important;" @click="currentPage = page">{{ page }}</button>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <button class="custom-button" @click="nextPage" :disabled="currentPage === totalPages">Sau</button>
                </li>
              </ul>
            </nav>
            <div class="btn-group">
              <button class="btn btn-outline-primary"
                      :disabled="lichHenStore.currentPage === 0"
                      @click="lichHenStore.prevPage">
                <i class="fas fa-chevron-left"></i>
              </button>
              <button class="btn btn-outline-primary"
                      :disabled="lichHenStore.currentPage >= lichHenStore.totalPages - 1"
                      @click="lichHenStore.nextPage">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showOverlay" class="overlay">
      <div class="overlay-content">
        <p>Đang chuyển hướng đến PayPal...</p>
        <p>Vui lòng chờ {{ countdown }} giây</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>