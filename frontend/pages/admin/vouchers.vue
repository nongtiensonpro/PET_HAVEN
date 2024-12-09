<template>
  <div class="voucher-list container mt-5">
    <h1 class="title mb-4">Quản lý Voucher</h1>
    <div class="row mb-4">
      <div class="col-md-6">
        <add-voucher @added="refresh" />
      </div>
      <div class="col-md-6 d-flex align-items-center justify-content-end">
        <button @click="refreshVouchers" class="btn btn-outline-primary">
          <i class="fas fa-sync-alt mr-2"></i> Làm mới
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
                <CapNhatVoucher
                  :voucher="voucher"
                  @updated="refresh"
                />
                <button
                  @click="updateTrangThaiVoucher(voucher.id)"
                  type="button"
                  class="btn btn-sm ms-2"
                  :class="voucher.trangthai ? 'btn-danger' : 'btn-success'"
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
    <div class="d-flex justify-content-between align-items-center mt-3">
      <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-sm btn-secondary">Trước</button>
      <span>Trang {{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-sm btn-secondary">Sau</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, computed } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import AddVoucher from '~/components/AddVoucher.vue';
import CapNhatVoucher from '~/components/CapNhatVoucher.vue';
import type Voucher from "~/models/Voucher";
import { useToast } from 'vue-toastification';
import Swal from "sweetalert2";

const toast = useToast();
const voucherStore = useVoucherStore();

const { data: voucherData, refresh } = await useAsyncData<Voucher[]>(
  'vouchers',
  () => voucherStore.fetchVoucher()
);

// Pagination
const itemsPerPage = 5;
const currentPage = ref(1);

const paginatedVouchers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return voucherData.value?.slice(start, end) || [];
});

const totalPages = computed(() => {
  return Math.ceil((voucherData.value?.length || 0) / itemsPerPage);
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
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function refreshVouchers() {
  try {
    await refresh();
    currentPage.value = 1; // Reset to first page after refresh
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
      toast.success('Cập nhật trạng thái voucher thành công!');
    } catch (error) {
      toast.error('Cập nhật trạng thái voucher thất bại!');
    }
  }
}
</script>

<style scoped>
.voucher-list {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
}

.title {
  color: #343a40;
  font-weight: bold;
}

.table {
  background-color: white;
}

.table th {
  font-weight: 600;
}

.badge {
  font-size: 0.9em;
}
</style>