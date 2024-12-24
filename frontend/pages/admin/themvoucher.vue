<template>
  <form @submit.prevent="submitVoucher" class="needs-validation" novalidate>
    <div class="mb-3">
      <label for="mota" class="form-label">Mô tả:</label>
      <input type="text" class="form-control" id="mota" v-model="voucher.mota" required>
      <div class="invalid-feedback" v-if="errors.mota">{{ errors.mota }}</div>
    </div>
    <div class="mb-3">
      <label for="phantramgiamgia" class="form-label">Giảm giá (%):</label>
      <input type="number" class="form-control" id="phantramgiamgia" v-model="voucher.phantramgiam" required min="1" max="100">
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
      <button type="submit" class="custom-button">Thêm Voucher</button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import Voucher from '~/models/Voucher';
import {useToast} from 'vue-toastification';
import bootstrap from "~/plugins/bootstrap";

const toast = useToast();
const voucherStore = useVoucherStore();
const voucher = ref(new Voucher(0, 0, new Date(), new Date(), '', true));
const errors = ref<{ [key: string]: string }>({});

function validate() {
  errors.value = {};
  if (!voucher.value.mota) {
    errors.value.mota = 'Mô tả là bắt buộc.';
  }
  if (voucher.value.phantramgiam < 1 || voucher.value.phantramgiam > 100) {
    errors.value.phantramgiam = 'Giảm giá phải từ 1 đến 100.';
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
   try {
     await voucherStore.addVoucher(voucher.value);
     voucher.value = new Voucher(0, 0, new Date(), new Date(), '', true);
     const modal = document.getElementById('addVoucherModal');
     if (modal) {
       const bsModal = bootstrap.Modal.getInstance(modal);
       bsModal?.hide();
     }
     toast.success('Thêm voucher thành công.');
   }catch (error) {
     toast.error('Thêm voucher thất bại, vui lòng thử lại sau.');
   }
  }
}
</script>

<style scoped>

</style>