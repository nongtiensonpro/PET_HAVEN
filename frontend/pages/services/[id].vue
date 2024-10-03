<template>
  <div class="container my-5">
    <div class="card">
      <div class="card-header">
        <h2 class="mb-0">Chi tiết dịch vụ</h2>
      </div>
      <div class="card-body">
        <div v-if="service">
          <h5 class="card-title">Tên dịch vụ: {{ service.tendichvu }}</h5>
          <p class="card-text">Mô tả: {{ service.mota }}</p>
          <p class="card-text">Giá tiền: <strong>{{ service.giatien }} Đ</strong></p>
        </div>
        <p v-if="errorMessage" class="text-danger">{{ errorMessage }}</p> <!-- Thông báo lỗi -->
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useServiceStore } from '~/stores/DichVuStores';
import { useRoute } from 'vue-router';

const serviceStore = useServiceStore();
const route = useRoute();
const service = ref(null);
const errorMessage = ref('');

onMounted(async () => {
  await serviceStore.fetchServices();

  const serviceId = parseInt(route.params.id as string);
  service.value = serviceStore.services.find(s => s.id === serviceId) || null;

  if (!service.value) {
    errorMessage.value = 'Dịch vụ không tồn tại hoặc đã bị xóa.';
  }
});
</script>

<style scoped>
.text-danger {
  color: red;
}
.card {
  border: 1px solid #dee2e6;
  border-radius: 0.25rem;
}
</style>
