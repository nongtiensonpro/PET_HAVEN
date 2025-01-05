<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from 'vue-i18n';
import CaLichHen from "~/models/CaHen";
import { useToast } from 'vue-toastification';

const { t } = useI18n();
const toast = useToast();
const caLichHenStore = useCaLichHenStore();
const thoigianca = ref("");
const isFormVisible = ref(false);

const toggleForm = () => {
  isFormVisible.value = !isFormVisible.value;
};

const themCaHen = async () => {
  try {
    const [hours, minutes] = thoigianca.value.split(':');
    const date = new Date();
    date.setHours(parseInt(hours), parseInt(minutes), 0, 0);

    const newCaHen = new CaLichHen(0, date, true);
    await caLichHenStore.themCaLichHen(newCaHen);

    thoigianca.value = "";
    toast.success(t('addTimeSlotSuccess'));
    isFormVisible.value = false;
    return navigateTo('/admin/quanlyca')
  } catch (e) {
    toast.error(t('addTimeSlotFailed'));
  }
};
</script>

<template>
  <div class="card">
    <form @submit.prevent="themCaHen">
      <div class="mb-3">
        <label for="thoigianca" class="form-label">{{ t('timeSlotLabel') }}</label>
        <input
            id="thoigianca"
            v-model="thoigianca"
            type="time"
            required
            class="form-control"
        />
      </div>
      <div class="d-flex justify-content-end">
        <button type="submit" class="btn btn-success me-2">{{ t('addTimeSlot') }}</button>
      </div>
    </form>
  </div>
</template>

<style scoped>

</style>