<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12 mb-4">
        <div class="card shadow">
          <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Hóa đơn chưa thanh toán</h5>
            <button @click="fetchHoaDon" class="btn btn-light btn-sm">
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
                    <th>Dịch Vụ</th>
                    <th>Ngày Hẹn</th>
                    <th>Số Tiền</th>
                    <th>Trạng Thái</th>
                    <th>Thao Tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="hoaDon in hoaDonList" :key="hoaDon.id">
                    <td>{{ hoaDon.id }}</td>
                    <td>{{ hoaDon.idlichhen.id }}</td>
                    <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                    <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                    <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                    <td>{{ formatDate(hoaDon.date) }}</td>
                    <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                    <td><span class="badge bg-warning">{{ getTrangThai(hoaDon.trangthai) }}</span></td>
                    <td>
                      <button @click="thanhToanHoaDon(hoaDon.idlichhen.id)" class="btn btn-sm btn-outline-primary">
                        Thanh toán
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12">
        <div class="card shadow">
          <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
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
                    <th>Dịch Vụ</th>
                    <th>Ngày Hẹn</th>
                    <th>Số Tiền</th>
                    <th>Trạng Thái</th>
                    <th>Thao Tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="hoaDon in hoaDonThanhToanList" :key="hoaDon.id">
                    <td>{{ hoaDon.id }}</td>
                    <td>{{ hoaDon.idlichhen.id }}</td>
                    <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                    <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                    <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                    <td>{{ formatDate(hoaDon.date) }}</td>
                    <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                    <td><span class="badge bg-success">{{ getTrangThai(hoaDon.trangthai) }}</span></td>
                    <td>
                      <button @click="taiHoaDon(hoaDon.id)" class="btn btn-sm btn-outline-success">
                        <i class="fas fa-check-circle me-1"></i> Tải hóa đơn
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCheckInStore } from '~/stores/CheckInStores'
import { ref, onMounted } from "vue";
import { useQuanLyHoaDonStore } from '~/stores/QuanLyHoaDon';

const useQuanLyHoaDon = useQuanLyHoaDonStore();
const checkInStore = useCheckInStore()

const hoaDonList = ref([]);
const hoaDonThanhToanList = ref([]);

const fetchHoaDon = async () => {
  await checkInStore.fetchHoaDon();
  hoaDonList.value = checkInStore.ListHoaDon;
};

const fetchHoaDonThanhToan = async () => {
  await checkInStore.fetchHoaDonDaThanhToan();
  hoaDonThanhToanList.value = checkInStore.ListHoaDonDaThanhToan;
}

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

const getTrangThai = (status) => {
  switch(status) {
    case 1: return 'Chưa thanh toán';
    case 2: return 'Đã thanh toán';
    default: return 'Không xác định';
  }
};

function taiHoaDon(id: string){
  useQuanLyHoaDon.inHoaDon(id)
  fetchHoaDon();
  fetchHoaDonThanhToan();
};


function thanhToanHoaDon (id: string) {
  checkInStore.checkIn(id);
  fetchHoaDon();
  fetchHoaDonThanhToan();
}
onMounted(() => {
  fetchHoaDon();
  fetchHoaDonThanhToan();
});
</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
</style>