<script setup lang="ts">
import Calendar from '~/components/Calendar.vue'
import {useServiceStore} from '~/stores/DichVuStores';
import {computed} from "vue";
import DichVu from "~/models/DichVu";
const accessToken = sessionStorage.getItem('access_token');
const viewRole = sessionStorage.getItem('viewRole');
const serviceStore = useServiceStore();


const services = computed((): DichVu[] => {
  return serviceStore.services.filter((service: DichVu) => service.trangthai);
});

</script>

<template>
  <div v-if="!accessToken || !viewRole">
    <div class="container">
      <div class="card m-4">
        <div class="card-body">
          Vui lòng đăng nhập để sử dụng chức năng này !
        </div>
      </div>
    </div>
  </div>

  <div v-else class="container p-4">
    <div class="row">
      <h2>Nhà Haven \ Đặt lịch</h2>
      <div class="col-12">
        <div class="row">
          <div class="col-6">
            <div class="card">
              <img class="card-img-top" src="~/assets/image/cat3.jpg" alt="">
              <div class="card-body">

              </div>
            </div>
          </div>
          <div class="col-6">
            <Calendar/>
          </div>
        </div>
      </div>
    </div>
  </div>


</template>

<style scoped>

</style>