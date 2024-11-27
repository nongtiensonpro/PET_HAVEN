<template>
  <div class="voucher-list container mt-5">
    <h1 class="title mb-4">Quản lý Voucher</h1>

    <div class="row mb-4">
      <div class="col-md-6">
        <add-voucher @added="fetchVouchers" />
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
            <th>Mô tả</th>
            <th>Giảm giá</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="voucher in vouchers" :key="voucher.id">
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
                  @updated="fetchVouchers"
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import AddVoucher from '~/components/AddVoucher.vue';
import CapNhatVoucher from '~/components/CapNhatVoucher.vue';
import type Voucher from "~/models/Voucher";
import {useToast} from 'vue-toastification';

const toast = useToast();

const voucherStore = useVoucherStore();
const vouchers = ref<Voucher[]>([]);

onMounted(() => {
  fetchVouchers();
});

async function fetchVouchers() {
  try {
    await voucherStore.fetchVoucher();
    vouchers.value = voucherStore.ListVoucher;
    console.log('Fetched vouchers:', vouchers.value);
  } catch (error) {
    console.error('Error fetching vouchers:', error);
  }
}

function refreshVouchers() {
  fetchVouchers();
}

function formatDate(date: Date | string) {
  if (typeof date === 'string') {
    return new Date(date).toLocaleDateString('vi-VN');
  }
  return date.toLocaleDateString('vi-VN');
}


async function updateTrangThaiVoucher(id: number) {
  try {

      await voucherStore.updateTrangThaiVoucher(id);
      fetchVouchers();
      toast.success('Cập nhật trạng thái voucher thành công!');

  } catch (error) {
    toast.error('Cập nhật trạng thái voucher thất bại!');
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