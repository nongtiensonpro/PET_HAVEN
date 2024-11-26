<template>
  <button
    type="button"
    class="btn btn-sm btn-success"
    :class="additionalClass"
    data-bs-toggle="modal"
    :data-bs-target="'#updateVoucherModal' + voucher.id"
  >
    <i class="fas fa-edit me-2"></i>Cập nhật
  </button>

  <div
    class="modal fade"
    :id="'updateVoucherModal' + voucher.id"
    tabindex="-1"
    :aria-labelledby="'updateVoucherModalLabel' + voucher.id"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title" :id="'updateVoucherModalLabel' + voucher.id">
            Cập nhật Voucher
          </h5>
          <button
            type="button"
            class="btn-close btn-close-white"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="updateVoucher">
            <div class="mb-3">
              <label for="mota" class="form-label">Mô tả:</label>
              <input
                id="mota"
                v-model="voucher.mota"
                class="form-control"
                placeholder="Mô tả"
                required
              />
            </div>
            <div class="mb-3">
              <label for="phantramgiamgia" class="form-label">Giảm giá (%):</label>
              <input
                id="phantramgiamgia"
                v-model.number="voucher.phantramgiamgia"
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
                v-model="voucher.ngaybatdau"
                type="date"
                class="form-control"
                required
              />
            </div>
            <div class="mb-3">
              <label for="ngayketthuc" class="form-label">Ngày kết thúc:</label>
              <input
                id="ngayketthuc"
                v-model="voucher.ngayketthuc"
                type="date"
                class="form-control"
                required
              />
            </div>
            <div class="d-flex justify-content-end">
              <button
                type="button"
                class="btn btn-secondary me-2"
                data-bs-dismiss="modal"
              >
                Hủy
              </button>
              <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import Voucher from '~/models/Voucher';

const props = defineProps<{
  voucher: Voucher;
  additionalClass?: string;
}>();

const emit = defineEmits(['updated']);

const voucherStore = useVoucherStore();

async function updateVoucher() {
  if (validateVoucher(props.voucher)) {
    try {
      await voucherStore.updateVoucher(props.voucher);
      emit('updated');
      closeModal();
    } catch (error) {
      console.error('Error updating voucher:', error);
      alert('Có lỗi xảy ra khi cập nhật voucher. Vui lòng thử lại.');
    }
  } else {
    alert('Vui lòng điền đầy đủ thông tin hợp lệ.');
  }
}

function validateVoucher(voucher: Voucher) {
  return (
    voucher.mota &&
    voucher.phantramgiamgia > 0 &&
    voucher.phantramgiamgia <= 100 &&
    voucher.ngaybatdau &&
    voucher.ngayketthuc &&
    new Date(voucher.ngaybatdau) < new Date(voucher.ngayketthuc)
  );
}

function closeModal() {
  const modal = document.getElementById('updateVoucherModal' + props.voucher.id);
  if (modal) {
    const bsModal = (window as any).bootstrap.Modal.getInstance(modal);
    bsModal?.hide();
  }
}
</script>

<style scoped>
.modal-header {
  border-bottom: none;
}

.form-control:focus {
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0056b3;
  border-color: #0056b3;
}
</style>