<template>
  <div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addVoucherModal">
      <i class="fas fa-plus-circle me-2"></i>Thêm Voucher
    </button>

    <div class="modal fade" id="addVoucherModal" tabindex="-1" aria-labelledby="addVoucherModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="addVoucherModalLabel">Thêm Voucher Mới</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitVoucher" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="mota" class="form-label">Mô tả:</label>
                <input type="text" class="form-control" id="mota" v-model="voucher.mota" required>
                <div class="invalid-feedback" v-if="errors.mota">{{ errors.mota }}</div>
              </div>
              <div class="mb-3">
                <label for="phantramgiamgia" class="form-label">Giảm giá (%):</label>
                <input type="number" class="form-control" id="phantramgiamgia" v-model="voucher.phantramgiamgia" required min="1" max="100">
                <div class="invalid-feedback" v-if="errors.phantramgiamgia">{{ errors.phantramgiamgia }}</div>
              </div>
              <div class="mb-3">
                <label for="ngaybatdau" class="form-label">Ngày bắt đầu:</label>
                <input type="date" class="form-control" id="ngaybatdau" v-model="voucher.ngaybatdau" required>
                <div class="invalid-feedback" v-if="errors.ngaybatdau">{{ errors.ngaybatdau }}</div>
              </div>
              <div class="mb-3">
                <label for="ngayketthuc" class="form-label">Ngày kết thúc:</label>
                <input type="date" class="form-control" id="ngayketthuc" v-model="voucher.ngayketthuc" required>
                <div class="invalid-feedback" v-if="errors.ngayketthuc">{{ errors.ngayketthuc }}</div>
              </div>
              <div class="d-flex justify-content-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary">Thêm Voucher</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import Voucher from '~/models/Voucher';

const voucherStore = useVoucherStore();
const voucher = ref(new Voucher(0, 0, new Date(), new Date(), '', true));
const errors = ref({});

function validate() {
  errors.value = {};
  if (!voucher.value.mota) {
    errors.value.mota = 'Mô tả là bắt buộc.';
  }
  if (voucher.value.phantramgiamgia < 1 || voucher.value.phantramgiamgia > 100) {
    errors.value.phantramgiamgia = 'Giảm giá phải từ 1 đến 100.';
  }
  if (!voucher.value.ngaybatdau) {
    errors.value.ngaybatdau = 'Ngày bắt đầu là bắt buộc.';
  }
  if (!voucher.value.ngayketthuc) {
    errors.value.ngayketthuc = 'Ngày kết thúc là bắt buộc.';
  }
  if (new Date(voucher.value.ngaybatdau) >= new Date(voucher.value.ngayketthuc)) {
    errors.value.ngayketthuc = 'Ngày kết thúc phải sau ngày bắt đầu.';
  }
  return Object.keys(errors.value).length === 0;
}

async function submitVoucher() {
  if (validate()) {
    await voucherStore.addVoucher(voucher.value);
    // Reset form after submission
    voucher.value = new Voucher(0, 0, new Date(), new Date(), '', true);
    // Close modal
    const modal = document.getElementById('addVoucherModal');
    if (modal) {
      const bsModal = bootstrap.Modal.getInstance(modal);
      bsModal?.hide();
    }
  }
}
</script>

<style scoped>
.modal-header {
  border-bottom: none;
}

.modal-footer {
  border-top: none;
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

.invalid-feedback {
  display: block;
  width: 100%;
  margin-top: 0.25rem;
  font-size: 80%;
  color: #dc3545;
}
</style>