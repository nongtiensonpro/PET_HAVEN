<script setup lang="ts">

</script>

<template>
    <div class="container p-4">
      <div class="col">
        <div class="card" style="width: 18rem;">
          <img src="~/assets/image/catservice.jpg" class="card-img-top p-4" alt="...">
          <div class="card-body">
            <h5 class="card-title">Quản lý dịch vụ</h5>
            <p class="card-text">
              Quản lý các dịch vụ đang hoạt động của Pet Haven
            </p>
          </div>
          <ul class="list-group list-group-flush">
            <nuxt-link class="list-group-item" :to="`/admin/service/servicelist`">Tổng quan dịch vụ</nuxt-link>
          </ul>
        </div>
      </div>
    </div>
</template>

<style scoped>

</style>