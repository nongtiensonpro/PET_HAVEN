<template>
  <div class="container bg-light p-4">
    <h1>Quản lý hóa đơn</h1>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="row">
        <div class="col">
          <button @click="refreshData" class="custom-button">Làm mới dữ liệu</button>
        </div>
        <div class="col">
          <div class="d-flex">
            <input v-model="searchTerm" type="text" class="form-control me-2 custom-button" placeholder="Tìm kiếm...">
            <button @click="search" class="btn btn-primary custom-button">Tìm kiếm</button>
          </div>
        </div>
      </div>


    </div>
    <table class="table table-striped mt-3">
      <thead>
      <tr>
        <th>ID</th>
        <th>Ngày</th>
        <td>Email người đặt</td>
        <th>Số tiền</th>
        <th>Trạng thái</th>
        <th>Thao tác</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="hoaDon in paginatedHoaDonList" :key="hoaDon.id">
        <td>{{ hoaDon.idlichhen.id }}</td>
        <td>{{ formatDate(hoaDon.date) }}</td>
        <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
        <td>{{ hoaDon.sotien }} USD</td>
        <td>{{ getTrangThai(hoaDon.trangthai) }}</td>
        <td>
          <button @click="viewHoaDon(hoaDon.id)" class="btn btn-sm btn-outline-secondary">Chi tiết</button>
        </td>
      </tr>
      </tbody>
    </table>
    <div class="pagination">
      <div class="row">
        <div class="col">
          <button @click="prevPage" :disabled="currentPage === 1" class="custom-button">Trước</button>
        </div>
        <div class="col">
          <span class="text fs-5">Trang {{ currentPage }} / {{ totalPages }}</span>
        </div>
        <div class="col">
          <button @click="nextPage" :disabled="currentPage === totalPages" class="custom-button">Sau</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, onUnmounted, computed, watch} from 'vue';
import {useQuanLyHoaDonStore} from '~/stores/QuanLyHoaDon';
import HoaDonKhachHang from "~/models/HoaDonKhachHang";

const store = useQuanLyHoaDonStore();
const hoaDonList = ref<HoaDonKhachHang[]>([]);
const filteredHoaDonList = ref<HoaDonKhachHang[]>([]);
const currentPage = ref(1);
const itemsPerPage = 5;
const searchTerm = ref('');

definePageMeta({
  middleware: ['auth']
})

const fetchHoaDon = async () => {
  await store.fetchListHoaDon();
  hoaDonList.value = store.ListHoaDon as HoaDonKhachHang[];
  filteredHoaDonList.value = hoaDonList.value;
};

const {data} = await useAsyncData('hoaDonList', () => store.fetchListHoaDon());

watch(data, () => {
  hoaDonList.value = store.ListHoaDon as HoaDonKhachHang[];
  filteredHoaDonList.value = hoaDonList.value;
});

const refreshData = () => {
  fetchHoaDon();
  currentPage.value = 1;
  searchTerm.value = '';
};

const search = () => {
  if (searchTerm.value) {
    filteredHoaDonList.value = hoaDonList.value.filter(hoaDon =>
        hoaDon.idlichhen.id.toString().includes(searchTerm.value) ||
        hoaDon.phuongthucthanhtoan.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
        hoaDon.sotien.toString().includes(searchTerm.value) ||
        hoaDon.idlichhen.emailNguoiDat.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
        getTrangThai(hoaDon.trangthai).toLowerCase().includes(searchTerm.value.toLowerCase())
    );
  } else {
    filteredHoaDonList.value = hoaDonList.value;
  }
  currentPage.value = 1;
};

let intervalId: NodeJS.Timeout;

onMounted(() => {
  refreshData()
  intervalId = setInterval(refreshData, 60 * 1000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${day}/${month}/${year} ${hours}:${minutes}`;
};

const getTrangThai = (trangthai: number) => {
  switch (trangthai) {
    case 1:
      return 'Chờ thanh toán';
    case 2:
      return 'Thành công';
    case 3:
      return 'Đã hoàn tiền';
    default:
      return 'Không xác định';
  }
};

const viewHoaDon = (id: number) => {
  navigateTo(`/admin/chitiethoadon/${id}`);
};

const paginatedHoaDonList = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredHoaDonList.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(filteredHoaDonList.value.length / itemsPerPage));

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

watch(searchTerm, () => {
  search();
});
</script>

<style scoped>
.table {
  width: 100%;
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
}
</style>