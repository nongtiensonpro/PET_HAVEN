<script setup lang="ts">
import {useDoiDichVuStores} from "~/stores/DoiDichVuStores";
import Hoadon from "~/models/Hoadon";

const useDoiDichVu = useDoiDichVuStores();
const listAllHoaDonChuaThanhToan = ref<Hoadon[]>([]);

async function fetchHoaDonChuaThanhToan() {
  useDoiDichVu.fetchAllHoaDonDoiDichVu;
  listAllHoaDonChuaThanhToan.value = useDoiDichVu.listAllHoaDonDoiDichVu;
  console.log(listAllHoaDonChuaThanhToan.value);
}
onMounted(() => {
  fetchHoaDonChuaThanhToan();
})

</script>

<template>
  <div class="card">
    <div class="card-header row">
      <p class="col">
        Danh sách hóa đơn sau khi đổi dịch vụ
      </p>
      <div class="col">
        <button type="button" class="custom-button" @click="fetchHoaDonChuaThanhToan">Làm mới</button>
      </div>
    </div>
    <div class="card-body">
      {{listAllHoaDonChuaThanhToan}}
    </div>
  </div>
</template>

<style scoped>

</style>