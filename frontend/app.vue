<template>
  <div id="app">

    <nav-bar/>
  <nuxt-page/>
    <!--    <NuxtRouteAnnouncer />-->
    <!--    <NuxtWelcome />-->
  </div>
</template>


<script setup lang="ts">
import NavBar from "~/components/NavBar.vue";
</script>

<style scoped>
html, body, #app {
  height: 100%; /* Đặt chiều cao cho toàn bộ trang */
  margin: 0; /* Bỏ margin mặc định */
}

#app {
  background: #F2F0D8 !important;
}
</style>