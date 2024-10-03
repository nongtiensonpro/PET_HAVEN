<template>
  <div class="container">
    <h2>Dịch vụ của chúng tôi</h2>
    <div class="row">
      <div v-for="service in services" :key="service.id" class="card col m-4">
        <h4 class="card-title">{{ service.tendichvu }}</h4>
        <img class="card-img-top" style="max-height: 250px; max-width: 250px" src="https://www.thesprucepets.com/thmb/0vKXtwRwIQxOAxQ0I27Fr6f72Pk=/2121x0/filters:no_upscale():strip_icc()/GingerTabbyShorthair-e3b45511a76a4b25a6fb421e60f04025.jpg" alt="">
        <div class="card-body">

          <p class="card-text">{{ service.mota }}</p>
          <p class="card-text">{{ service.giatien }} Đ</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { onMounted, computed } from 'vue';
import DichVu from '~/models/DichVu';

export default {
  setup() {
    const serviceStore = useServiceStore();

    onMounted(() => {
      serviceStore.fetchServices();
    });

    const services = computed((): DichVu[] => serviceStore.services);

    return {
      services,
    };
  },
};
</script>

<style scoped>
.card {
  background-color: transparent; /* Loại bỏ nền trắng */
  border: none; /* Nếu không muốn viền */
  box-shadow: none; /* Nếu không muốn bóng */
}

.card-title {
  color: #333; /* Điều chỉnh màu tiêu đề nếu cần */
}

.card-body {
  color: #666; /* Điều chỉnh màu văn bản nếu cần */
}
</style>
