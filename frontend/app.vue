<template>
  <NuxtLayout>
    <CustomerHome/>
  </NuxtLayout>
</template>

<script setup lang="ts">
import CustomerHome from "~/components/CustomerHome.vue";

useHead({
  title: 'Pet Haven',
  meta: [
    {
      name: 'description',
      content: 'Pet Haven - Dịch vụ chăm sóc thú cưng tốt nhất'
    }
  ]
});
</script>

<style scoped>
html, body{
  height: 100%;
  margin: 0;
  padding: 0;
  background: #F2F0D8 !important;
  color: #400D01 !important;
  font-family: 'Baloo 2', sans-serif !important;
}
</style>
