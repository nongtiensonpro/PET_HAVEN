<template>
  <div>
    <h1>Danh sách dịch vụ</h1>
    <div v-if="services.length === 0">
      <p>Không có dịch vụ nào để hiển thị.</p>
    </div>
    <ul>
      <li v-for="service in services" :key="service.id">
        <h2>{{ service.ten}}</h2>
        <p>{{ service.moTa }}</p>
        <p>Giá: {{ formatCurrency(service.gia) }}</p>
        <img v-if="service.anh" :src="service.anh" alt="Ảnh dịch vụ" />
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import api from '~/service/ApiDichVu'; // Import service API
import DichVu from '~/models/DichVu'; // Import model

export default defineComponent({
  data() {
    return {
      services: [] as DichVu[],
    }
  },
  async asyncData() {
    try {
      const response = await api.getItems(); // Gọi phương thức getItems từ API
      return {
        services: response.data // Trả về dữ liệu danh sách dịch vụ
      };
    } catch (error) {
      console.error('Error fetching services:', error);
      return {
        services: []
      };
    }
  },
  methods: {
    formatCurrency(amount: number): string {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(amount);
    }
  }
});
</script>

<style scoped>
/* Thêm các kiểu CSS tùy ý cho trang */
</style>
