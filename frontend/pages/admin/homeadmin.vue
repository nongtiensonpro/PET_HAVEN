<script setup lang="ts">
import { useUserStore } from '~/stores/user'
import { computed } from 'vue'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
</script>

<template>
  <div class="container">
    <h1>Thông tin người dùng:</h1>
    <div v-if="userInfo">
      <p><strong>Sub:</strong> {{ userInfo.sub }}</p>
      <p><strong>Tên:</strong> {{ userInfo.name }}</p>
      <p><strong>Tên người dùng:</strong> {{ userInfo.preferred_username }}</p>
      <p><strong>Tên:</strong> {{ userInfo.given_name }}</p>
      <p><strong>Họ:</strong> {{ userInfo.family_name }}</p>
      <p><strong>Email:</strong> {{ userInfo.email }}</p>
      <p><strong>Role:</strong> {{ userInfo.roles }}</p>

    </div>
    <p v-else>Đang tải thông tin người dùng...</p>
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