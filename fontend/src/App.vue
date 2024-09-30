<!--Khởi tạo ứng dụng Vue-->
<template>
  <div id="app">
    <AnimalList />
  </div>
</template>

<script setup>
import AnimalList from './components/AnimalList.vue';
</script>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
