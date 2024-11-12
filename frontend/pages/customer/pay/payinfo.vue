<script setup lang="ts">
function thanhToanTaiQuay(){
  return navigateTo('/customer/success');
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
                  <button type="button" class="custom-button">Thanh toán với VN Pay</button>
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