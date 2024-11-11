<template>
  <div v-if="!accessToken || !viewRole" class="container my-5">
    <div class="alert alert-warning" role="alert">
      <i class="fas fa-exclamation-triangle me-2"></i>
      Vui lòng đăng nhập để sử dụng chức năng này!
    </div>
  </div>

  <div v-else class="container py-5">
    <h2 class="mb-4"><i class="fas fa-home me-2"></i>Nhà Haven \ <span class="text-primary">Đặt lịch</span></h2>
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h4 class="card-title mb-4"><i class="fas fa-info-circle me-2"></i>Thông tin đặt lịch</h4>
            <div v-if="tempData">
              <div class="card border-primary mb-3">
                <div class="card-header bg-primary text-white">
                  <i class="fas fa-calendar-check me-2"></i>Chi tiết lịch hẹn
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <div v-if="tempData.idlichhen?.dichvu && tempData.idlichhen?.calichhen">
                      <div class="col-md-6 mb-3">
                        <h5 class="text-muted"><i class="fas fa-clipboard-list text-primary me-2"></i>Dịch vụ</h5>
                        <p class="mb-0"><strong>Tên:</strong> {{ tempData.idlichhen.dichvu.tendichvu }}</p>
                        <p class="mb-0"><strong>Giá tiền:</strong> {{
                            formatCurrency(tempData.idlichhen.dichvu.giatien)
                          }}</p>
                      </div>
                      <div class="col-md-6 mb-3">
                        <h5 class="text-muted"><i class="far fa-clock text-primary me-2"></i>Thời gian</h5>
                        <p class="mb-0">{{ tempData.idlichhen.calichhen.tenca }} -
                          {{ tempData.idlichhen.calichhen.thoigianca }}</p>
                      </div>
                    </div>
                    <div v-if="tempData.thucung" class="col-12">
                      <h5 class="text-muted mb-3"><i class="fas fa-paw text-primary me-2"></i>Thông tin thú cưng</h5>
                      <div class="row">
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Tên:</strong> {{ tempData.thucung.name }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Tuổi:</strong> {{ tempData.thucung.age }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Giống:</strong> {{ tempData.thucung.breed }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Cân nặng:</strong> {{ tempData.thucung.weight }} kg</p>
                        </div>
                        <div class="col-12">
                          <p class="mb-0"><strong>Ghi chú:</strong> {{ tempData.thucung.notes || 'Không có' }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center">
              <img src="~/assets/image/cat3.jpg" alt="Chọn dịch vụ" class="img-fluid rounded mb-3"
                   style="max-height: 200px;">
              <p class="lead">Hãy lựa chọn dịch vụ và thời gian để bắt đầu.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-6">
        <div class="accordion" id="bookingAccordion">
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne"
                      aria-expanded="true" aria-controls="collapseOne">
                <i class="fas fa-calendar-alt me-2"></i>Chọn ngày và giờ hẹn
              </button>
            </h2>
            <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <Calendar/>
              </div>
            </div>
          </div>
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                      data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                <i class="fas fa-paw me-2"></i>Thông tin thú cưng
              </button>
            </h2>
            <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <Pet/>
              </div>
            </div>
          </div>
          <div class="accordion-item" v-if="isBookingComplete">
            <h2 class="accordion-header">
              <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                      data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                <i class="fas fa-paw me-2"></i>Thanh toán
              </button>
            </h2>
            <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <div  class="row">
                  <div class="col-6">
                    <div class="card">
                      <img class="card-img-top" src="~/assets/image/vnpay.jpg" alt="VN Pay">
                      <div class="card-body">
                        <h4 class="card-title">Thanh toán với VN Pay</h4>
                        <p class="card-text">Thanh toán an toàn và nhanh chóng qua VN Pay</p>
                        <button class="btn btn-primary" @click="payWithVNPay">Thanh toán ngay</button>
                      </div>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="card">
                      <img class="card-img-top" src="~/assets/image/tratienmat.png" alt="Thanh toán tại quầy">
                      <div class="card-body">
                        <h4 class="card-title">Thanh toán tại quầy</h4>
                        <p class="card-text">Thanh toán trực tiếp khi đến cửa hàng</p>
                        <button class="btn btn-secondary" @click="payAtCounter">Chọn phương thức này</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>


</template>

<script setup lang="ts">
import Calendar from '~/components/Calendar.vue'
import Pet from '~/components/Pet.vue'
import {useServiceStore} from '~/stores/DichVuStores';
import {useMauKhachDatDichVu} from '~/stores/MauKhachDatDichVu'
import {computed} from "vue";
import DichVu from "~/models/DichVu";

const accessToken = sessionStorage.getItem('access_token');
const viewRole = sessionStorage.getItem('viewRole');
const serviceStore = useServiceStore();
const {getTempData} = useMauKhachDatDichVu()
const tempData = computed(() => getTempData())
const services = computed((): DichVu[] => {
  return serviceStore.services.filter((service: DichVu) => service.trangthai);
});

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value);
};
const isBookingComplete = computed(() => {
  return tempData.value &&
      tempData.value.idlichhen?.dichvu &&
      tempData.value.idlichhen?.calichhen &&
      tempData.value.thucung;
});

function payWithVNPay() {

}

function payAtCounter() {

}


</script>

<style scoped>
.accordion-button:not(.collapsed) {
  background-color: #e7f1ff;
  color: #0c63e4;
}

.accordion-button:focus {
  box-shadow: none;
  border-color: rgba(0, 0, 0, .125);
}

.card {
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}
</style>