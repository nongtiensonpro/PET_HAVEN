<template>
  <div class="container my-5">
    <div class="card">
      <div v-if="service">
        <div class="card-header">
          <div class="row">
            <div class="col">
              <h2 class="mb-0">{{ service.tendichvu }} - {{ service.giatien.toLocaleString() }} USD</h2>
            </div>

          </div>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col">
              <div v-if="service.anh===null">
                <img src="~/assets/image/cat1.jpg" class="card-img-top" alt="...">
              </div>
              <div v-else>
                <img :src="service.anh" class="card-img-top" alt="...">
              </div>
            </div>
            <div class="col">
              <div class="">
                <div class="card-header">
                 Mô tả:
                </div>
                <div class="card-body">
                  <p>{{ service.mota }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-danger p-4">{{ errorMessage }}</div>
    </div>
    <a  id="" class="custom-button p-4" href="/" role="button">Trở về trang chủ</a>
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
</style>
