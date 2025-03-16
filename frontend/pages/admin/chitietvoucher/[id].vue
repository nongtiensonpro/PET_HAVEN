<template>
  <div v-if="selectedVoucher">
    <h2>{{t('details')}} Voucher</h2>
    <form @submit.prevent="updateVoucher">
      <div class="mb-3">
        <label for="mota" class="form-label">{{t('description')}}:</label>
        <input
          id="mota"
          v-model="selectedVoucher.mota"
          class="form-control"
          placeholder="Mô tả"
          required
        />
      </div>
      <div class="mb-3">
        <label for="phantramgiamgia" class="form-label">{{t('discount')}} (%):</label>
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
        <label for="ngaybatdau" class="form-label">{{t('startDate')}}:</label>
        <input
          id="ngaybatdau"
          v-model="selectedVoucher.ngaybatdau"
          type="date"
          class="form-control"
          required
        />
      </div>
      <div class="mb-3">
        <label for="ngayketthuc" class="form-label">{{t('endDate')}}:</label>
        <input
          id="ngayketthuc"
          v-model="selectedVoucher.ngayketthuc"
          type="date"
          class="form-control"
          required
        />
      </div>
      <div class="d-flex justify-content-end">
        <button type="button" class="custom-button" @click="goBack">
          {{t('back')}}
        </button>
        <button type="submit" class="custom-button">{{t('saveInfo')}}</button>
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
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
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

const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) >= 0;
};

function validateVoucher(voucher: Voucher) {
  const allErrors: string[] = [];

  // Validate description
  if (!voucher.mota || voucher.mota.trim().length < 3) {
    allErrors.push(t('description_required_min_length'));
  }

  // Validate discount percentage
  if (!isNumeric(voucher.phantramgiam)) {
    allErrors.push(t('discount_must_be_numeric'));
  }

  if (!isPositiveNumber(voucher.phantramgiam)) {
    allErrors.push(t('discount_must_be_positive'));
  }

  const discountValue = parseFloat(voucher.phantramgiam);
  if (discountValue < 1 || discountValue > 100) {
    allErrors.push(t('discount_range_invalid'));
  }

  // Validate start date
  if (!voucher.ngaybatdau) {
    allErrors.push(t('start_date_required'));
  }

  // Validate end date
  if (!voucher.ngayketthuc) {
    allErrors.push(t('end_date_required'));
  }

  // Validate date range
  const startDate = new Date(voucher.ngaybatdau);
  const endDate = new Date(voucher.ngayketthuc);
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

async function updateVoucher() {
  if (!selectedVoucher.value) {
    toast.error(t('voucher_not_found'));
    return;
  }

  if (validateVoucher(selectedVoucher.value)) {
    const result = await Swal.fire({
      title: t('confirm'),
      text: t('do_you_want_to_update_voucher'),
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: t('yes'),
      cancelButtonText: t('no')
    });

    if (result.isConfirmed) {
      try {
        await voucherStore.updateVoucher(selectedVoucher.value);
        toast.success(t('voucher_updated_successfully'));
        return navigateTo("/admin/vouchers");
      } catch (error) {
        toast.error(t('error_updating_voucher'));
        console.error('Error updating voucher:', error);
      }
    }
  }
}

function goBack() {
  router.push('/admin/vouchers');
}
</script>

<style scoped>

</style>