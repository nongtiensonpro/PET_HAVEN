<template>
  <div class="voucher-carousel">
    <div class="carousel-container">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import Voucher from '~/models/Voucher';

const props = defineProps<{
  vouchers: Voucher[]
}>();

const currentIndex = ref(0);
const intervalId = ref<number | null>(null);

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const startCarousel = () => {
  intervalId.value = window.setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % props.vouchers.length;
  }, 3000);
};

onMounted(() => {
  startCarousel();
});

onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value);
  }
});
</script>

<style scoped>
.voucher-carousel {
  overflow: hidden;
  position: relative;
  padding: 20px 0;
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
  background: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
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

.pet-icons {
  position: absolute;
  bottom: 10px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-around;
  font-size: 24px;
  color: #666;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}
</style>