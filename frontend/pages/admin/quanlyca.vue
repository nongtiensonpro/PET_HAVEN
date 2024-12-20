<script setup lang="ts">
import { onMounted, ref, onUnmounted, computed } from "vue";
import { useCaLichHenStore } from "~/stores/QuanLyCaLichHen"
import type NgayNghi from "~/models/NgayNghi";
import type CaLichHen from "~/models/CaHen";
import ThemCa from '~/components/ThemCa.vue'
import CapNhatCaHen from '~/components/CapNhatCaLichHen.vue'
import CapNhatNgayNghi from "~/components/CapNhatNgayNghi.vue";
import Swal from 'sweetalert2';
import { useToast } from 'vue-toastification'

const caLichHenStore = useCaLichHenStore();
const caLichHens = ref<CaLichHen[]>([]);
const listNgayNghi = ref<NgayNghi[]>([]);
const toast = useToast();

// Pagination
const itemsPerPage = 5;
const currentPageCaLichHen = ref(1);
const currentPageNgayNghi = ref(1);

const paginatedCaLichHens = computed(() => {
  const start = (currentPageCaLichHen.value - 1) * itemsPerPage;
  return caLichHens.value.slice(start, start + itemsPerPage);
});

const paginatedNgayNghi = computed(() => {
  const start = (currentPageNgayNghi.value - 1) * itemsPerPage;
  return listNgayNghi.value.slice(start, start + itemsPerPage);
});

const totalPagesCaLichHen = computed(() => Math.ceil(caLichHens.value.length / itemsPerPage));
const totalPagesNgayNghi = computed(() => Math.ceil(listNgayNghi.value.length / itemsPerPage));

const { data: caLichHensData } = await useAsyncData('caLichHens', () => caLichHenStore.fethcCaLichHen());
const { data: ngayNghiData } = await useAsyncData('ngayNghi', () => caLichHenStore.fetchNgayNghi());

watch(caLichHensData, (newData) => {
  if (newData) caLichHens.value = newData;
});

watch(ngayNghiData, (newData) => {
  if (newData && Array.isArray(newData)) {
    listNgayNghi.value = newData;
  } else {
    listNgayNghi.value = [];
  }
});

const fetchData = async () => {
  try {
    caLichHens.value = await caLichHenStore.fethcCaLichHen();
    const ngayNghiData = await caLichHenStore.fetchNgayNghi();
    if (Array.isArray(ngayNghiData)) {
      listNgayNghi.value = ngayNghiData;
    } else {
      listNgayNghi.value = [];
    }
    currentPageCaLichHen.value = 1;
    currentPageNgayNghi.value = 1;
  } catch (e) {
    toast.error('Lấy dữ liệu thất bại!');
  }
}

let intervalId: NodeJS.Timeout;

onMounted(() => {
  fetchData()
  intervalId = setInterval(fetchData, 60 * 1000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function capNhatTrangThai(ca: CaLichHen) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: `Bạn có chắc chắn ${ca.trangthai ? 'ẩn' : 'hiện'} ${ca.thoigianca} thời gian chứ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });

  if (result.isConfirmed) {
    try {
      await caLichHenStore.capNhatTrangThaiCa(ca);
      await fetchData();
      toast.success('Cập nhật ca lịch hẹn thành công!')
    } catch (e) {
      toast.error('Cập nhật ca lịch hẹn thất bại!')
    }
  }
}

function capNhat(ca: CaLichHen) {
  caLichHenStore.capNhatCaLichHen(ca);
  fetchData();
}

function lammoi() {
  fetchData();
}

async function huyNgayNghi(ngayNghi: NgayNghi) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: `Bạn có chắc chắn ${ngayNghi.trangthai?'Hủy':'Kích hoạt'} ngày nghỉ ${ngayNghi.ngaynghi} không ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    try {
      await caLichHenStore.huyNgayNghi(ngayNghi);
      await fetchData();
      toast.success('Thay đổi trạng thái ngày nghỉ thành công!')
    } catch (e) {
      toast.error('Thay đổi trạng thái  ngày nghỉ thất bại!')
    }
  }
}

</script>

<template>
  <div class="bg-light p-4">
    <div class="text fs-1">
      Quản lý thời gian
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-2">
          <ThemCa />
        </div>
        <div class="col-3">
          <CapNhatNgayNghi />
        </div>
        <div class="col-3">
          <button type="button" @click="lammoi()" class="custom-button">Làm mới</button>
        </div>
      </div>
    </div>

  <div class="row">
    <div class="col container">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Thời gian</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ca in paginatedCaLichHens" :key="ca.id">
            <td>{{ ca.id }}</td>
            <td>{{ ca.thoigianca }}</td>
            <td>
              <span :class="ca.trangthai ? 'active' : 'inactive'">
                {{ ca.trangthai ? 'Hoạt động' : 'Không hoạt động' }}
              </span>
            </td>
            <td>
              <button type="button" @click="capNhatTrangThai(ca)" class="btn btn-sm btn-outline-warning m-1">
                {{ca.trangthai ? 'Ẩn ca' : 'Hiện ca'}}
              </button>
              <CapNhatCaHen :ca="ca" @cap-nhat="capNhat" />
            </td>
          </tr>
        </tbody>
      </table>
      <!-- Pagination for Ca Lich Hen -->
      <div class="pagination d-flex justify-content-center align-items-center mt-3">
        <div class="row">
          <div class="col">
            <button @click="currentPageCaLichHen--" :disabled="currentPageCaLichHen === 1" class="custom-button">Trước</button>
          </div>
          <div class="col">
            <span class="text fs-5">Trang {{ currentPageCaLichHen }} / {{ totalPagesCaLichHen }}</span>
          </div>
          <div class="col">
            <button @click="currentPageCaLichHen++" :disabled="currentPageCaLichHen === totalPagesCaLichHen" class="custom-button">Sau</button>
          </div>
        </div>



      </div>
    </div>
    <div class="col container">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Ngày nghỉ</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ngay in paginatedNgayNghi" :key="ngay.id">
            <td>{{ ngay.id }}</td>
            <td>{{ ngay.ngaynghi }}</td>
            <td>{{ ngay.trangthai ? 'Hoạt động' : 'Không hoạt động' }}</td>
            <td>
              <button type="button" class="btn btn-sm btn-outline-warning m-1" @click="huyNgayNghi(ngay)">{{ngay.trangthai?'Hủy':'Kích hoạt'}}</button>
            </td>
          </tr>
        </tbody>
      </table>
      <!-- Pagination for Ngay Nghi -->
      <div class="pagination d-flex justify-content-center align-items-center mt-3">
        <div class="row">
          <div class="col">
            <button @click="currentPageNgayNghi--" :disabled="currentPageNgayNghi === 1" class="custom-button">Trước</button>
          </div>
          <div class="col">
            <span class="text fs-5">Trang {{ currentPageNgayNghi }} / {{ totalPagesNgayNghi }}</span>
          </div>
          <div class="col">
            <button @click="currentPageNgayNghi++" :disabled="currentPageNgayNghi === totalPagesNgayNghi" class="custom-button">Sau</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<style scoped>
.ca-lich-hen-table th, .ca-lich-hen-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.ca-lich-hen-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.ca-lich-hen-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.ca-lich-hen-table tr:hover {
  background-color: #f5f5f5;
}
</style>