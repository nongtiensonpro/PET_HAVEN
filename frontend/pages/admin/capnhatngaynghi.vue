<script setup lang="ts">
import { ref, computed } from 'vue';
import { useToast } from 'vue-toastification';

const caLichHenStore = useCaLichHenStore();
const toast = useToast();
const ngayNghi = ref('');
const isFormVisible = ref(false);

const toggleForm = () => {
  isFormVisible.value = !isFormVisible.value;
};

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
    toast.success('Cập nhật ngày nghỉ thành công');
    ngayNghi.value = ''; // Reset form
    isFormVisible.value = false;
    return navigateTo('/admin/quanlyca');
  } catch (error) {
    toast.error('Cập nhật ngày nghỉ thất bại');
  }
}
</script>

<template>
  <div class="card">
    <form @submit.prevent="themNgayNghi">
      <div class="mb-3">
        <label for="ngayNghi" class="form-label">Ngày nghỉ:</label>
        <input
            id="ngayNghi"
            v-model="formattedNgayNghi"
            type="date"
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