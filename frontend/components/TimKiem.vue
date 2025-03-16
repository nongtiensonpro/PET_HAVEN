<script setup lang="ts">
import {useI18n} from 'vue-i18n';
import { useServiceStore } from '~/stores/DichVuStores';
import { computed, ref, watch } from 'vue';
import Fuse from 'fuse.js';

const {t, locale} = useI18n();
const serviceStore = useServiceStore();
const searchQuery = ref('');
const fuse = ref<Fuse<any> | null>(null);

const searchPlaceholder = computed(() => t('searchPlaceholder'));
// Khởi tạo Fuse.js khi services thay đổi
watch(() => serviceStore.services, (services) => {
  fuse.value = new Fuse(services, {
    keys: ['tendichvu', 'mota','giatien'],
    threshold: 0.4, // Điều chỉnh ngưỡng để tăng/giảm độ chính xác
    includeScore: true
  });
}, { immediate: true });

const filteredServices = computed(() => {
  if (!searchQuery.value || !fuse.value) return [];
  return fuse.value.search(searchQuery.value);
});

const searchService = () => {
  // Tìm kiếm được thực hiện tự động thông qua computed property
};
</script>

<template>
  <div class="search-container">
    <div class="search-input-wrapper">
      <input
        class="search-input"
        type="text"
        v-model="searchQuery"
        :placeholder="searchPlaceholder"
        @input="searchService"
      />
      <i class="fas fa-search search-icon"></i>
    </div>
    <transition name="fade">
      <div class="search-results card" v-if="searchQuery">
        <div v-if="filteredServices.length === 0" class="no-results">
          <i class="fas fa-exclamation-circle"></i>
          Không tìm thấy dịch vụ phù hợp.
        </div>
        <ul v-else class="results-list">
          <li v-for="service in filteredServices" :key="service.item.id">
            <nuxt-link class="result-item" :to="`/services/${service.item.id}`">
              <span class="service-name">{{ service.item.tendichvu }}</span>
            </nuxt-link>
          </li>
        </ul>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.search-input-wrapper {
  position: relative;
  width: 100%;
}

.search-input {
  width: 100%;
  padding: 12px 40px 12px 15px;
  font-size: 16px;
  border: none;
  border-radius: 25px;
  background: rgba(246, 246, 234, 0.62);
  color: #400D01;
  font-family: 'Baloo 2', sans-serif;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  box-shadow: 0 0 10px 5px rgba(246, 246, 234, 0.3);
}

.search-input:focus {
  outline: none;
  background: rgba(246, 246, 234, 0.8);
  box-shadow: 0 0 15px 5px rgba(246, 246, 234, 0.4);
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #400D01;
  opacity: 0.7;
}

.search-results {
  margin-top: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.results-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  text-decoration: none;
  color: #400D01;
  transition: all 0.3s ease;
  border-bottom: 1px solid rgba(64, 13, 1, 0.1);
}

.result-item:last-child {
  border-bottom: none;
}

.result-item:hover {
  background: rgba(246, 246, 234, 0.8);
  transform: translateX(5px);
}

.service-name {
  font-weight: 500;
}

.service-price {
  font-weight: 600;
  color: #400D01;
}

.no-results {
  padding: 20px;
  text-align: center;
  color: #400D01;
  font-weight: 500;
}

.no-results i {
  margin-right: 8px;
}

/* Animation */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>