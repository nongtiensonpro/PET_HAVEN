<template>
  <form class="needs-validation" novalidate>
    <div class="mb-3">
      <label for="mota" class="form-label">{{t('description')}}:</label>
      <input type="text" class="form-control" id="mota" v-model="voucher.mota" required>
      <div class="invalid-feedback" v-if="errors.mota">{{ errors.mota }}</div>
    </div>
    <div class="mb-3">
      <label for="phantramgiamgia" class="form-label">{{t('discount')}} (%):</label>
      <input type="number" class="form-control" id="phantramgiamgia" v-model="voucher.phantramgiam" required min="1" max="100">
      <div class="invalid-feedback" v-if="errors.phantramgiamgia">{{ errors.phantramgiamgia }}</div>
    </div>
    <div class="mb-3">
      <label for="ngaybatdau" class="form-label">{{t('startDate')}}:</label>
      <input type="date" class="form-control" id="ngaybatdau" v-model="voucher.ngaybatdau" required>
      <div class="invalid-feedback" v-if="errors.ngaybatdau">{{ errors.ngaybatdau }}</div>
    </div>
    <div class="mb-3">
      <label for="ngayketthuc" class="form-label">{{t('endDate')}}:</label>
      <input type="date" class="form-control" id="ngayketthuc" v-model="voucher.ngayketthuc" required>
      <div class="invalid-feedback" v-if="errors.ngayketthuc">{{ errors.ngayketthuc }}</div>
    </div>
    <div class="d-flex justify-content-end">
      <button type="button" class="custom-button" @click="submitVoucher">{{t('add')}} Voucher</button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import Voucher from '~/models/Voucher';
import {useToast} from 'vue-toastification';
import { useI18n } from 'vue-i18n';
import Swal from "sweetalert2";

const { t } = useI18n();
const toast = useToast();
const voucherStore = useVoucherStore();
const voucher = ref(new Voucher(0, 0, new Date(), new Date(), '', true));
const errors = ref<{ [key: string]: string }>({});

function validate() {
  const allErrors: string[] = [];

  // Validate description
  if (!voucher.value.mota || voucher.value.mota.trim().length < 3) {
    allErrors.push(t('description_required_min_length'));
  }

  // Validate discount percentage
  if (!isNumeric(voucher.value.phantramgiam)) {
    allErrors.push(t('discount_must_be_numeric'));
  }

  if (!isPositiveNumber(voucher.value.phantramgiam)) {
    allErrors.push(t('discount_must_be_positive'));
  }

  const discountValue = parseFloat(voucher.value.phantramgiam);
  if (discountValue < 1 || discountValue > 100) {
    allErrors.push(t('discount_range_invalid'));
  }

  // Validate start date
  if (!voucher.value.ngaybatdau) {
    allErrors.push(t('start_date_required'));
  }

  // Validate end date
  if (!voucher.value.ngayketthuc) {
    allErrors.push(t('end_date_required'));
  }

  // Validate date range
  const startDate = new Date(voucher.value.ngaybatdau);
  const endDate = new Date(voucher.value.ngayketthuc);
  const today = new Date();

  if (startDate < today) {
    allErrors.push(t('start_date_must_be_future'));
  }

  if (startDate >= endDate) {
    allErrors.push(t('end_date_must_be_after_start_date'));
  }

  // Hiển thị lỗi nếu có
  if (allErrors.length > 0) {
    allErrors.forEach(error => {
      toast.error(error);
    });
    return false;
  }

  return true;
}

// Thêm các hàm validation
const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) >= 0;
};

async function submitVoucher() {
  const result = await Swal.fire({
    title: t('confirm'),
    text: t('do_you_want_to_add_voucher'),
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });

  if (result.isConfirmed) {
    if (validate()) {
      try {
        await voucherStore.addVoucher(voucher.value);
        toast.success(t('voucher_added_successfully'));
        return navigateTo('/admin/vouchers');
      } catch (error) {
        toast.error(t('error_adding_voucher'));
        console.error('Error adding voucher:', error);
      }
    }
  }
}
</script>

<style scoped>

</style>