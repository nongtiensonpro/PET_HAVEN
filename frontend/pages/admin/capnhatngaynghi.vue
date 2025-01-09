<script setup lang="ts">
import { ref, computed } from 'vue';
import { useToast } from 'vue-toastification';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const caLichHenStore = useCaLichHenStore();
const toast = useToast();
const ngayNghi = ref('');
const isFormVisible = ref(false);
const formattedNgayNghi = computed({
  get() {
    return ngayNghi.value ? new Date(ngayNghi.value).toISOString().split('T')[0] : '';
  },
  set(value) {
    ngayNghi.value = value;
  }
});

async function themNgayNghi() {
  try {
    await caLichHenStore.themNgayNghi(formattedNgayNghi.value);
    toast.success(t('holiday_update_successful'));
    ngayNghi.value = ''; // Reset form
    isFormVisible.value = false;
    return navigateTo('/admin/quanlyca');
  } catch (error) {
    toast.error(t('holiday_update_failed'));
  }
}
</script>

<template>
  <div class="card">
    <form @submit.prevent="themNgayNghi">
      <div class="mb-3">
        <label for="ngayNghi" class="form-label">{{t('day_off')}}:</label>
        <input
            id="ngayNghi"
            v-model="formattedNgayNghi"
            type="date"
            required
            class="form-control"
        />
      </div>
      <div class="d-flex justify-content-end">
        <button type="submit" class="btn btn-success me-2">{{t('add')}}</button>
      </div>
    </form>
  </div>
</template>

<style scoped>

</style>