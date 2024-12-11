<template>
  <div class="container">
    <div v-if="services.length === 0">Dịch vụ không khả dụng, vui lòng thử lại sau!</div>
    <div v-else>
     <h2 class="text text-center ">Dịch vụ của chúng tôi</h2>
     <div class="container">

       <div class="row">
         <div v-for="service in paginatedServices" :key="service.id" class="card col m-4">
           <nuxt-link class="nav-link" :to="`/services/${service.id}`">
           <div class="card-body">
                 <div v-if="service.anh==null">
                   <img src="~/assets/image/cat1.jpg" class="card-img-top" alt="Ảnh mặc định nè">
                 </div>
                 <div v-else>
                   <img :src="service.anh" class="card-img-top" alt="...">
                 </div>
             <div class="card-header">
               <h2 class="mb-0 fs-4">{{ service.tendichvu }} - {{ service.giatien.toLocaleString() }} USD</h2>
             </div>
           </div>
           </nuxt-link>

         </div>
       </div>
     </div>
      <div class="pagination align-content-center">
        <button @click="prevPage" :disabled="currentPage === 1">Trang trước</button>
        <span>Trang {{ currentPage }} trên {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">Trang sau</button>
      </div>

      <div v-if="vouchers">
        <h2 class="text text-center ">Chương trình khuyến mãi</h2>
        <VoucherDisplay :vouchers="vouchers" />
      </div>
      <div v-else>
        <h2 class="text text-center fs-3">Hãy đăng nhập để lấy thông tin khuyến mãi</h2>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { onMounted, computed, ref } from 'vue';
import DichVu from '~/models/DichVu';
import {useVoucherStore} from '~/stores/VorchersStores'
import Voucher from '~/models/Voucher';
import VoucherDisplay from "~/components/VoucherDisplay.vue";

const voucherStore = useVoucherStore();
const serviceStore = useServiceStore();
const itemsPerPage = 4; // Số lượng dịch vụ hiển thị trên mỗi trang
const currentPage = ref(1); // Trang hiện tại

onMounted(() => {
  serviceStore.fetchServices();
  voucherStore.fetchVoucher();
});

const services = computed((): DichVu[] => {
  return serviceStore.services.filter((service: DichVu) => service.trangthai && service.hien);
});

const vouchers = computed(() : Voucher[] => {
  return voucherStore.ListVoucher.filter((voucher: Voucher) => voucher.trangthai);
})

const totalPages = computed(() => Math.ceil(services.value.length / itemsPerPage));

// Tính toán các dịch vụ được hiển thị trên trang hiện tại
const paginatedServices = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return services.value.slice(start, start + itemsPerPage);
});

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};
</script>

<style scoped>

</style>
