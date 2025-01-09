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

async function updateVoucher() {
  if (selectedVoucher.value && validateVoucher(selectedVoucher.value)) {
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
        toast.success(t('statusSuccess'));
        return navigateTo("/admin/vouchers")
      } catch (error) {
        toast.error(t('failure'));
      }
    }
  } else {
    toast.error(t('failure'));
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