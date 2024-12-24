<script setup lang="ts">
import { ref } from "vue";
import CaLichHen from "~/models/CaHen";
import { useToast } from 'vue-toastification';

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
    toast.success('Thêm ca hẹn thành công');
    isFormVisible.value = false;
    return navigateTo('/admin/quanlyca')
  } catch (e) {
    toast.error('Thêm ca hẹn thất bại');
  }
};
</script>

<template>
  <div class="card">
    <form @submit.prevent="themCaHen">
      <div class="mb-3">
        <label for="thoigianca" class="form-label">Thời gian ca:</label>
        <input
            id="thoigianca"
            v-model="thoigianca"
            type="time"
            required
            class="form-control"
        />
      </div>
      <div class="d-flex justify-content-end">
        <button type="submit" class="btn btn-success me-2">Thêm</button>
      </div>
    </form>
  </div>
</template>

<style scoped>

</style>