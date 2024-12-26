<template>
  <div v-if="selectedVoucher">
    <h2>Chi tiết Voucher</h2>
    <form @submit.prevent="updateVoucher">
      <div class="mb-3">
        <label for="mota" class="form-label">Mô tả:</label>
        <input
          id="mota"
          v-model="selectedVoucher.mota"
          class="form-control"
          placeholder="Mô tả"
          required
        />
      </div>
      <div class="mb-3">
        <label for="phantramgiamgia" class="form-label">Giảm giá (%):</label>
        <input
          id="phantramgiamgia"
          v-model.number="selectedVoucher.phantramgiam"
          type="number"
          class="form-control"
          placeholder="Giảm giá"
          required
          min="1"
          max="100"
        />
      </div>
      <div class="mb-3">
        <label for="ngaybatdau" class="form-label">Ngày bắt đầu:</label>
        <input
          id="ngaybatdau"
          v-model="selectedVoucher.ngaybatdau"
          type="date"
          class="form-control"
          required
        />
      </div>
      <div class="mb-3">
        <label for="ngayketthuc" class="form-label">Ngày kết thúc:</label>
        <input
          id="ngayketthuc"
          v-model="selectedVoucher.ngayketthuc"
          type="date"
          class="form-control"
          required
        />
      </div>
      <div class="d-flex justify-content-end">
        <button type="button" class="btn btn-secondary me-2" @click="goBack">
          Quay lại
        </button>
        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
      </div>
    </form>
  </div>
  <div v-else>
    Không tìm thấy voucher
  </div>
</template>

<script setup lang="ts">
import { useVoucherStore } from '~/stores/VorchersStores';
import Voucher from '~/models/Voucher';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import Swal from "sweetalert2";
import { onMounted, ref } from "vue";

const toast = useToast();
const route = useRoute();
const router = useRouter();
const voucherStore = useVoucherStore();

const selectedVoucher = ref<Voucher | null>(null);

onMounted(async () => {
  const id = Number(route.params.id);
  await voucherStore.fetchVoucher();
  selectedVoucher.value = voucherStore.ListVoucher.find(v => v.id === id) || null;
});

async function updateVoucher() {
  if (selectedVoucher.value && validateVoucher(selectedVoucher.value)) {
    const result = await Swal.fire({
      title: 'Xác nhận',
      text: "Bạn có muốn cập nhật Voucher không?",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Có',
      cancelButtonText: 'Không'
    });
    if (result.isConfirmed) {
      try {
        await voucherStore.updateVoucher(selectedVoucher.value);
        toast.success('Cập nhật voucher thành công.');
      } catch (error) {
        toast.error('Có lỗi xảy ra khi cập nhật voucher. Vui lòng thử lại.');
      }
    }
  } else {
    toast.error('Vui lòng điền đầy đủ thông tin hợp lệ.');
  }
}

function validateVoucher(voucher: Voucher) {
  return (
    voucher.mota &&
    voucher.phantramgiam > 0 &&
    voucher.phantramgiam <= 100 &&
    voucher.ngaybatdau &&
    voucher.ngayketthuc &&
    new Date(voucher.ngaybatdau) < new Date(voucher.ngayketthuc)
  );
}

function goBack() {
  router.push('/admin/vouchers');
}
</script>

<style scoped>

</style>