<template>
  <div class="voucher-list card container mt-5 bg-light p-4" style="border-radius: 25px">
    <h1 class="title mb-4">Quản lý Voucher</h1>
    <div class="row mb-4">
      <div class="col">
        <button type="button" class="custom-button" @click="themVoucher">Thêm</button>
      </div>
      <div class="col">
          <input
            v-model="searchTerm"
            type="text"
            class="custom-button w-100"
            placeholder="Tìm kiếm voucher..."
            @input="handleSearch"
          >

      </div>
      <div class="col">
        <button class="custom-button" type="button" @click="handleSearch">
          Tìm kiếm
        </button>
        <button @click="refreshVouchers" class="custom-button">
          Làm mới
        </button>
      </div>
    </div>
    <div class="table-responsive">
      <table class="table table-hover">
        <thead class="thead-light">
          <tr>
            <th>ID</th>
            <th>Mô tả</th>
            <th>Giảm giá</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="voucher in paginatedVouchers" :key="voucher.id">
            <td>{{ voucher.id }}</td>
            <td>{{ voucher.mota }}</td>
            <td>
              <span class="badge bg-success text-white">
                {{ voucher.phantramgiam }}%
              </span>
            </td>
            <td>{{ formatDate(voucher.ngaybatdau) }}</td>
            <td>{{ formatDate(voucher.ngayketthuc) }}</td>
            <td>
              <span :class="['badge', voucher.trangthai ? 'bg-success' : 'bg-danger']">
                {{ voucher.trangthai ? 'Hoạt động' : 'Không hoạt động' }}
              </span>
            </td>
            <td>
              <div class="d-flex">
                <button type="button" class=" btn btn-sm m-1 btn-outline-success" @click="capNhatVoucher(voucher)">Cập nhật</button>
                <button
                  @click="updateTrangThaiVoucher(voucher.id)"
                  type="button"
                  class="btn btn-sm  m-1"
                  :class="voucher.trangthai ? 'btn-outline-danger' : 'btn-outline-success'"
                >
                  {{ voucher.trangthai ? 'Hủy' : 'Kích hoạt' }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- Pagination -->
    <div class="pagination d-flex justify-content-center align-items-center mt-3">
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
import { onMounted, onUnmounted, ref, computed, watch } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import AddVoucher from '~/components/AddVoucher.vue';
import CapNhatVoucher from '~/pages/admin/chitietvoucher/CapNhatVoucher.vue';
import type Voucher from "~/models/Voucher";
import { useToast } from 'vue-toastification';
import Swal from "sweetalert2";

const toast = useToast();
const voucherStore = useVoucherStore();

const { data: voucherData, refresh } = await useAsyncData<Voucher[]>(
  'vouchers',
  () => voucherStore.fetchVoucher()
);

// Search functionality
const searchTerm = ref('');
const filteredVouchers = ref<Voucher[]>([]);

// Pagination
const itemsPerPage = 5;
const currentPage = ref(1);

const paginatedVouchers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredVouchers.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredVouchers.value.length / itemsPerPage);
});

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

let intervalId: NodeJS.Timeout;

onMounted(() => {
  intervalId = setInterval(() => {
    refresh();
  }, 5 * 60 * 1000); // Refresh every 5 minutes
  handleSearch(); // Initial search to populate filteredVouchers
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function refreshVouchers() {
  try {
    await refresh();
    currentPage.value = 1; // Reset to first page after refresh
    handleSearch(); // Re-apply search after refresh
    toast.success('Làm mới vouchers thành công!');
  } catch (e) {
    toast.error('Làm mới vouchers thất bại!');
  }
}

function formatDate(date: Date | string) {
  if (typeof date === 'string') {
    return new Date(date).toLocaleDateString('vi-VN');
  }
  return date.toLocaleDateString('vi-VN');
}

async function updateTrangThaiVoucher(id: number) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: "Bạn có muốn cập nhật trạng thái Voucher không?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    try {
      await voucherStore.updateTrangThaiVoucher(id);
      await refresh();
      handleSearch(); // Re-apply search after update
      toast.success('Cập nhật trạng thái voucher thành công!');
    } catch (error) {
      toast.error('Cập nhật trạng thái voucher thất bại!');
    }
  }
}

function handleSearch() {
  if (!voucherData.value) return;

  filteredVouchers.value = voucherData.value.filter(voucher =>
    voucher.id.toString().includes(searchTerm.value) ||
    voucher.mota.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
    voucher.phantramgiam.toString().includes(searchTerm.value) ||
    formatDate(voucher.ngaybatdau).includes(searchTerm.value) ||
    formatDate(voucher.ngayketthuc).includes(searchTerm.value) ||
    (voucher.trangthai ? 'hoạt động' : 'không hoạt động').includes(searchTerm.value.toLowerCase())
  );
  currentPage.value = 1; // Reset to first page after search
}


watch([voucherData, searchTerm], () => {
  handleSearch();
}, { immediate: true });

function themVoucher(){
  return navigateTo('/admin/themvoucher');
}

function capNhatVoucher(voucher: Voucher){
  return navigateTo(`/admin/chitietvoucher/${voucher.id}`);
}

</script>

<style scoped>

</style>