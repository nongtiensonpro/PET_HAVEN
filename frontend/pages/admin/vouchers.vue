<template>
  <div class="voucher-list card container mt-5 p-4" style="border-radius: 25px">
    <h1 class="title mb-4">Quản lý Voucher</h1>
    <div class="row mb-4">
      <div class="col-md-4 d-flex align-items-center">
        <button type="button" class="custom-button me-2" @click="themVoucher">
          <span class="me-2">Thêm</span>
          <font-awesome-icon icon="fa-solid fa-plus" />
        </button>
      </div>
      <div class="col-md-4">
        <input
            v-model="searchTerm"
            type="text"
            class="form-control"
            placeholder="Tìm kiếm voucher..."
            @input="handleSearch"
        >
      </div>
      <div class="col-md-4 d-flex align-items-center justify-content-end">
        <button @click="refreshVouchers" class="custom-button me-2" title="Làm mới">
          <font-awesome-icon icon="fa-solid fa-arrows-rotate" />
        </button>
        <button class="custom-button" type="button" @click="handleSearch" title="Tìm kiếm">
          <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
        </button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-hover">
        <thead class="table-light">
        <tr>
          <th scope="col">ID</th>
          <th scope="col">Mô tả</th>
          <th scope="col">Giảm giá</th>
          <th scope="col">Ngày bắt đầu</th>
          <th scope="col">Ngày kết thúc</th>
          <th scope="col">Trạng thái</th>
          <th scope="col">Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="voucher in paginatedVouchers" :key="voucher.id">
          <td>{{ voucher.id }}</td>
          <td>{{ voucher.mota }}</td>
          <td>
              <span class="badge rounded-pill" :class="voucher.phantramgiam > 50 ? 'bg-danger' : 'bg-success'">
                {{ voucher.phantramgiam }}%
              </span>
          </td>
          <td>{{ formatDate(voucher.ngaybatdau) }}</td>
          <td>{{ formatDate(voucher.ngayketthuc) }}</td>
          <td>
              <span :class="['badge', voucher.trangthai ? 'bg-success' : 'bg-warning text-dark']">
                {{ voucher.trangthai ? 'Hoạt động' : 'Không hoạt động' }}
              </span>
          </td>
          <td>
            <div class="d-flex">
              <button type="button" class="btn btn-sm btn-outline-primary me-2" @click="capNhatVoucher(voucher)">
                <font-awesome-icon icon="fa-solid fa-pen-to-square" />
              </button>
              <button
                  @click="updateTrangThaiVoucher(voucher.id)"
                  type="button"
                  class="btn btn-sm"
                  :class="voucher.trangthai ? 'btn-outline-danger' : 'btn-outline-success'"
              >
                <font-awesome-icon :icon="voucher.trangthai ? 'fa-solid fa-ban' : 'fa-solid fa-check'" />
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-center align-items-center mt-3">
      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li class="page-item">
            <button
                @click="prevPage"
                :disabled="currentPage === 1"
                class="page-link custom-button"
            >
              Trước
            </button>
          </li>
          <li class="page-item disabled">
            <span class="page-link">Trang {{ currentPage }} / {{ totalPages }}</span>
          </li>
          <li class="page-item">
            <button
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="page-link custom-button"
            >
              Sau
            </button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, computed, watch } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
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