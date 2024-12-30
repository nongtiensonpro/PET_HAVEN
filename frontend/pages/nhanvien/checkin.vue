<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12 mb-4">
        <div class="card shadow">
          <div class="card-header  text d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Lịch hẹn hôm nay </h5>
            <button @click="fetchHoaDon" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> Làm mới
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>Thời gian</th>
                  <th>ID Lịch Hẹn</th>
                  <th>Email KH</th>
                  <th>Tên Thú Cưng</th>

                  <th>Ngày Hẹn</th>
                  <th>Số Tiền</th>
                  <th>Trạng Thái</th>
                  <th>Thao Tác</th>
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
                  <td><span class="badge bg-warning">{{ getTrangThai(hoaDon.idlichhen.trangthai) }} {{hoaDon.idlichhen.trangthai}}</span></td>
                  <td>

                    <button
                        v-if="hoaDon.idlichhen.trangthai === 3 || hoaDon.idlichhen.trangthai === 4 "
                        @click="thanhToanHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Thanh toán
                    </button>

                    <button
                        v-if="hoaDon.idlichhen.trangthai === 8 "
                        @click="thanhToanHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Checkin
                    </button>
                    <button
                        @click="viewHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Chi tiết
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
              <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage1 === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage1 - 1, 1)">Trước</a>
                  </li>
                  <li v-for="page in totalPages1" :key="page" class="page-item"
                      :class="{ active: currentPage1 === page }">
                    <a class="page-link" href="#" @click.prevent="changePage(page, 1)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage1 === totalPages1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage1 + 1, 1)">Sau</a>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12">
        <div class="card shadow">
          <div class="card-header  text d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Hóa đơn đã thanh toán</h5>
            <button @click="fetchHoaDonThanhToan" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> Làm mới
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>ID Hóa Đơn</th>
                  <th>ID Lịch Hẹn</th>
                  <th>Email KH</th>
                  <th>Tên Thú Cưng</th>
                  <th>Ngày Hẹn</th>
                  <th>Số Tiền</th>
                  <th>Trạng Thái</th>
                  <th>Thao Tác</th>
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
                  <td><span class="badge bg-success">{{ getTrangThaiHoaDon(hoaDon.trangthai) }} </span></td>
                  <td>
                    <button @click="taiHoaDon(hoaDon.id)" class="btn btn-sm btn-outline-success">
                      <i class="fas fa-check-circle me-1"></i> Tải hóa đơn
                    </button>
                    <button
                        @click="viewHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Chi tiết
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
              <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <li class="page-item" :class="{ disabled: currentPage2 === 1 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage2 - 1, 2)">Trước</a>
                  </li>
                  <li v-for="page in totalPages2" :key="page" class="page-item"
                      :class="{ active: currentPage2 === page }">
                    <a class="page-link" href="#" @click.prevent="changePage(page, 2)">{{ page }}</a>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage2 === totalPages2 }">
                    <a class="page-link" href="#" @click.prevent="changePage(currentPage2 + 1, 2)">Sau</a>
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
import {ref, onMounted, onUnmounted} from "vue";
import {useQuanLyHoaDonStore} from '~/stores/QuanLyHoaDon';
import Swal from 'sweetalert2';
import HoaDonKhachHang from "~/models/HoaDonKhachHang";
import {useToast} from 'vue-toastification'

const toast = useToast();
const useQuanLyHoaDon = useQuanLyHoaDonStore();
const checkInStore = useCheckInStore()

const hoaDonList = ref<HoaDonKhachHang[]>([]);
const hoaDonThanhToanList = ref<HoaDonKhachHang[]>([]);

const itemsPerPage = 5;
const currentPage1 = ref(1);
const currentPage2 = ref(1);

const paginatedHoaDonList = computed(() => {
  const start = (currentPage1.value - 1) * itemsPerPage;
  return hoaDonList.value.slice(start, start + itemsPerPage);
});

const paginatedHoaDonThanhToanList = computed(() => {
  const start = (currentPage2.value - 1) * itemsPerPage;
  return hoaDonThanhToanList.value.slice(start, start + itemsPerPage);
});

const totalPages1 = computed(() => Math.ceil(hoaDonList.value.length / itemsPerPage));
const totalPages2 = computed(() => Math.ceil(hoaDonThanhToanList.value.length / itemsPerPage));

const changePage = (newPage: number, tableNumber: number) => {
  if (tableNumber === 1) {
    currentPage1.value = newPage;
  } else {
    currentPage2.value = newPage;
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
};

const refreshData = () => {
  fetchHoaDon();
  fetchHoaDonThanhToan();
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'USD'}).format(amount);
};

const getTrangThai = (status: number): string => {
  const statusMap = {
    0: 'Thành công',
    1: 'Đã đổi',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công',
    7: 'Đã hoàn tiền',
    8: 'Chờ sử dụng'
  };
  return statusMap[status] || 'Không xác định';
};

const getTrangThaiHoaDon = (status: number): string => {
  const statusMap = {
    1: 'Chờ thanh toán',
    2: 'Thành công',
    3: 'Đã hoàn tiền'
  };
  return statusMap[status] || 'Không xác định';
};

function taiHoaDon(id: string) {
  useQuanLyHoaDon.inHoaDon(id);
  refreshData();
}

async function thanhToanHoaDon(id: string) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: "Bạn có chắc chắn muốn Checkin cho lịch này chứ?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    try {
      await checkInStore.checkIn(id);
      refreshData();
      toast.success('Check in thành công');
      return navigateTo(`/nhanvien/checkin`);
    }catch (error) {
      toast.error('Check in thất bại');
      return navigateTo(`/nhanvien/checkin`);
    }
  }
}

onMounted(() => {
  refreshData();
  refreshInterval = setInterval(refreshData, 60 * 1000);
});

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval);
});

const viewHoaDon = (id: number) => {
  navigateTo(`/admin/chitiethoadon/${id}`);
};
</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
</style>