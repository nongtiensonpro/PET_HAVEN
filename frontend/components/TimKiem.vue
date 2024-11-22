<script setup lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { computed, ref, watch } from 'vue';
import Fuse from 'fuse.js';

const serviceStore = useServiceStore();
const searchQuery = ref('');
const fuse = ref<Fuse<any> | null>(null);

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
    <input
        class="no-border-input"
        type="text"
        v-model="searchQuery"
        placeholder="Tìm kiếm dịch vụ..."
        @input="searchService"
    />
    <div class="search-results p-4" v-if="searchQuery">
      <div v-if="filteredServices.length === 0">
        Không tìm thấy dịch vụ phù hợp.
      </div>
      <ul v-else>
        <li v-for="service in filteredServices" :key="service.item.id">
          <nuxt-link class="no-underline-link" :to="`/services/${service.item.id}`">
            {{ service.item.tendichvu }} - {{service.item.giatien}} USD
          </nuxt-link>
        </li>
      </ul>
    </div>
  </div>
</template>


<style scoped>
.no-border-input {
  border: none;
  outline: none;
  background-color: transparent;
  padding: 5px;
  font-size: 16px;
}

.no-border-input {
  border: none;
  outline: none;
  background-color: transparent;
  padding: 5px;
  font-size: 16px;
}
.no-underline-link {
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 4px;
}

.no-underline-link:hover {
  color: #afa3a1;
  transform: translateY(-2px);
}
.search-container {
  position: relative;
  width: 100%;
}

.no-border-input {
  border: none;
  outline: none;
  background-color: transparent;
  padding: 5px;
  font-size: 16px;
  width: 100%;
}

.search-results {
  position: absolute;
  margin: 1rem;
  top: 100%;
  left: 0;
  right: 0;
  background-color: white;
  border-radius: 0.25rem;
  border: 1px solid #400D01;
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
}

.search-results ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.search-results li {
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.search-results li:last-child {
  border-bottom: none;
}

.no-underline-link {
  text-decoration: none;
  color: inherit;
}

</style>