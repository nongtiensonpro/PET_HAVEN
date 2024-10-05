<template>
  <div id="app">

    <CustomerHome/>

    <!--    <NuxtRouteAnnouncer />-->
    <!--    <NuxtWelcome />-->
  </div>
</template>


<script setup lang="ts">
import CustomerHome from "~/components/CustomerHome.vue";
</script>

<style scoped>
html, body, #app {
  height: 100%; /* Đặt chiều cao cho toàn bộ trang */
  margin: 0; /* Bỏ margin mặc định */
  padding: 0; /* B�� padding mặc đ��nh */
  background: #F2F0D8 !important;
  color: #400D01 !important;
    font-family: 'Baloo 2', sans-serif !important;
}

</style>