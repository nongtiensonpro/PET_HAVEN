<template>
  <div class="container my-5">
    <div class="card">
      <div v-if="service">
        <div class="card-header">
          <h2 class="mb-0">Chi tiết dịch vụ : {{ service.tendichvu }}</h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col">
              <img class="card-img-top" style="max-height: 550px; max-width: 550px" src="https://www.thesprucepets.com/thmb/0vKXtwRwIQxOAxQ0I27Fr6f72Pk=/2121x0/filters:no_upscale():strip_icc()/GingerTabbyShorthair-e3b45511a76a4b25a6fb421e60f04025.jpg" alt="">
            </div>
            <div class="col">
              <h3 class="card-text">Mô tả: </h3>
              <p>{{ service.mota }}</p>
              <p class="text-center">Giá tiền: <strong>{{ service.giatien }} Đ</strong></p>
              <nuxt-link class="nav-link" :to="'/'">Quay về</nuxt-link>
            </div>
          </div>


        </div>
      </div>
      <div v-else class="text-danger p-4">{{ errorMessage }}</div>
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
const errorMessage = ref('Có lỗi đã xảy ra vui lỏng thử lại !');

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
  border: 0.6px solid #400D01;
  border-radius: 0.25rem;
  background: #F2F0D8 !important;
  color: #400D01 !important;
}
</style>
