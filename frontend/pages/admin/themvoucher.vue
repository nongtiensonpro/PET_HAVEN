<template>
  <form  class="needs-validation" novalidate>
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
        voucher.value = new Voucher(0, 0, new Date(), new Date(), '', true);
        toast.success(t('statusSuccess'));
        return navigateTo('/admin/vouchers');
      }catch (error) {
        toast.error(t('failure'));
      }
    }
  }

}
</script>

<style scoped>

</style>