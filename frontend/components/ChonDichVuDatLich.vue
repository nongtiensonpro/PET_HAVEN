<template>
  <div>
    <h2>Danh sách Dịch Vụ</h2>
    <ul>
      <li v-for="dichVu in listDichVu" :key="dichVu.id">
        {{ dichVu.tendichvu }} - {{ dichVu.mota }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DichVu from "~/models/DichVu";
import { useServiceStore } from "~/stores/DichVuStores"

const serviceStore = useServiceStore();
const listDichVu = ref<DichVu[]>([]);

onMounted(async () => {
  await serviceStore.fetchServices();
  listDichVu.value = serviceStore.services;
});
</script>