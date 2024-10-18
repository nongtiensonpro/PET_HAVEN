<script setup lang="ts">
import { useUserStore } from '~/stores/user'
import { computed } from 'vue'
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)


</script>

<template>
  <div class="container">
    <h1>Thông tin người dùng:</h1>
    <div >
      <p><strong>Tên:</strong> {{ userInfo.name }}</p>
      <p><strong>Role:</strong> {{ userInfo.role }}</p>
    </div>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
}

p {
  margin-bottom: 10px;
}

strong {
  font-weight: bold;
}
</style>