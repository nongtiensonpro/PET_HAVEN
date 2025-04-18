<template>
  <div class="voucher-carousel">
    <div v-if="vouchers.length > 0 && accessToken" class="carousel-container">
      <div class="carousel-track" :style="{ transform: `translateX(${-currentIndex * 100}%)` }">
        <div v-for="voucher in vouchers" :key="voucher.id" class="voucher-item">
          <div class="voucher">
            <div class="voucher-content">
              <h3>{{ voucher.mota }}</h3>
              <p class="discount">{{ voucher.phantramgiam }}% {{ t('discount') }}</p>
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
        <h3>{{ t('loginToViewVouchers') }}</h3>
        <p>{{ t('loginRequiredVoucher') }}</p>
        <button @click="navigateToLogin" class="custom-button">{{ t('loginNow') }}</button>
      </div>
    </div>
    <div v-else>
      <iframe src="https://giphy.com/embed/o722MCBurUlRAtZkka" width="280" height="280" style="" frameBorder="0" class="giphy-embed" allowFullScreen></iframe><p><a href="https://giphy.com/stickers/cat-ardhemis-biueversailles-o722MCBurUlRAtZkka"></a></p>
<!--      <p class="text-center">{{ t('noVouchers') }}</p>-->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import Voucher from '~/models/Voucher';

const props = defineProps<{
  vouchers: Voucher[]
}>();

const router = useRouter();
const currentIndex = ref(0);
const intervalId = ref<number | null>(null);
const accessToken = computed(() => localStorage.getItem('access_token'));
const { t } = useI18n();

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
  background: rgb(242, 240, 216) !important;
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
  background-color: rgb(242, 240, 216) !important;
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
  color: rgb(242, 240, 216) !important;
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