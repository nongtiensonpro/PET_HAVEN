<script setup lang="ts"></script>

<template>
  <div>
    <div v-if="$auth?.loggedIn">
      <p>Chào mừng, {{ $auth.user?.name }}</p>
      <button @click="$auth.logout()">Đăng xuất</button>
    </div>
    <div v-else>
      <button @click="$auth.login()">Đăng nhập</button>
    </div>
  </div>
</template>

<style scoped></style>
