<script setup lang="ts">

import {useMauKhachDatDichVu} from "~/stores/MauKhachDatDichVu";
import {computed} from "vue";

function thanhToanTaiQuay(){
  return navigateTo('/customer/success');
}
definePageMeta({
  middleware: 'auth',
});



function thanhToanOnline() {
  const { getTempData } = useMauKhachDatDichVu()
  const tempData = computed(() => getTempData())
  const token = sessionStorage.getItem('access_token');
  fetch('http://localhost:8080/api/payPal/payment/create', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'idLichHen' : tempData.value.id
    }
  })
      .then(response => response.text()) // Xử lý phản hồi chuỗi (URL)
      .then(url => {
        if (url) {
         window.location.href = url;
          // Redirect người dùng đến URL thanh toán của PayPal
        } else {
          console.error("Không thể tạo thanh toán");
        }
      })
      .catch(error => {
        console.error("Lỗi:", error);
      });
}

</script>

<template>
  <div class="container">
      <div class="card no-border">
        <h4 class="card-title text-center text p-4">Tùy chọn thanh toán</h4>
        <div class="card-body">
          <div class="row">
            <div class="col">
              <div class="card no-border">
                <button type="button" class="custom-button"><img class="card-img-top" src="~/assets/image/vnpay.jpg" height="350rem" width="350rem" alt=""></button>
                <div class="card-body">
                  <button type="button" class="custom-button" @click="thanhToanOnline">Thanh toán với VN Pay </button>
                </div>
              </div>
            </div>
            <div class="col">
              <div class="card no-border">
               <button type="button" @click="thanhToanTaiQuay" class="custom-button"><img class="card-img-top" src="~/assets/image/tratienmat.png" height="350rem" width="350rem" alt=""></button>
              <div class="card-body">
                <button type="button" @click="thanhToanTaiQuay" class="custom-button">Thanh toán tại quầy</button>
              </div>
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<style scoped>
.no-border {
  border: none !important;
  box-shadow: none !important;
}


.card-img-top {
  width: 100%;
  height: auto;
  max-height: 350px;
  object-fit: contain;
}
</style>