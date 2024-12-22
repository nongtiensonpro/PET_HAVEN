<template>
  <div class="voucher-carousel">
    <div v-if="vouchers.length > 0 && accessToken" class="carousel-container">
<!--      <h2 class="text text-center">Chương trình khuyến mãi</h2>-->
      <div class="carousel-track" :style="{ transform: `translateX(${-currentIndex * 100}%)` }">
        <div v-for="voucher in vouchers" :key="voucher.id" class="voucher-item">
          <div class="voucher">
            <div class="voucher-content">
              <h3>{{ voucher.mota }}</h3>
              <p class="discount">{{ voucher.phantramgiam }}% OFF</p>
              <p class="dates">
                {{ formatDate(voucher.ngaybatdau) }} - {{ formatDate(voucher.ngayketthuc) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="vouchers.length > 0 && !accessToken" class="login-message">
      <div class="message-content">
        <i class="fas fa-lock"></i>
        <h3>Đăng nhập để xem voucher</h3>
        <p>Bạn cần đăng nhập để xem các voucher khuyến mãi đặc biệt.</p>
        <button @click="navigateToLogin" class="custom-button">Đăng nhập ngay</button>
      </div>
    </div>
    <div v-else>
      <p class="text-center">Không có voucher nào.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import Voucher from '~/models/Voucher';

const props = defineProps<{
  vouchers: Voucher[]
}>();

const router = useRouter();
const currentIndex = ref(0);
const intervalId = ref<number | null>(null);
const accessToken = computed(() => localStorage.getItem('access_token'));

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const startCarousel = () => {
  intervalId.value = window.setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % props.vouchers.length;
  }, 3000);
};

const navigateToLogin = () => {
  router.push('/login');
};

onMounted(() => {
  if (props.vouchers.length > 0 && accessToken.value) {
    startCarousel();
  }
});

onUnmounted(() => {
  if (intervalId.value !== null) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
});
</script>

<style scoped>
.voucher-carousel {
  overflow: hidden;
  position: relative;
  padding: 20px 0;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-container {
  width: 100%;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease;
}

.voucher-item {
  flex: 0 0 100%;
}

.voucher {
  background: white;
  border-radius: 10px;
  padding: 20px;
  width: 300px;
  margin: 0 auto;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.voucher-content {
  text-align: center;
  color: #333;
}

h3 {
  margin-bottom: 10px;
  font-size: 1.2em;
}

.discount {
  font-size: 2em;
  font-weight: bold;
  margin: 10px 0;
  color: #e74c3c;
}

.dates {
  font-size: 0.9em;
  color: #666;
}

.login-message {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.message-content {
  max-width: 300px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-message i {
  font-size: 3em;
  color: #e74c3c;
  margin-bottom: 15px;
}

.login-message h3 {
  color: #333;
  margin-bottom: 10px;
}

.login-message p {
  color: #666;
  margin-bottom: 20px;
}

.custom-button {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.custom-button:hover {
  background-color: #c0392b;
}
</style>