<script setup lang="ts">
import Calendar from '~/components/Calendar.vue'
import {useServiceStore} from '~/stores/DichVuStores';
import {computed} from "vue";
import DichVu from "~/models/DichVu";
import {useMauKhachDatDichVu} from '~/stores/MauKhachDatDichVu'

const accessToken = sessionStorage.getItem('access_token');
const viewRole = sessionStorage.getItem('viewRole');
const serviceStore = useServiceStore();
const {saveTempData, getTempData, clearTempData} = useMauKhachDatDichVu()
const tempData = computed(() => getTempData())
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

              <div class="card-body">
                <div class="row">
                  <div class="col">
                    <div v-if="tempData">
                      <div v-if="tempData" class="card border-primary mb-3">
                        <div class="card-header bg-primary text-white">
                          <i class="fas fa-calendar-check me-2"></i>Thông tin đặt  lịch
                        </div>
                        <div class="card-body">
                          <div class="row g-3">
                            <div class="col-md-6" v-if="tempData.idlichhen?.dichvu">
                              <div class="d-flex align-items-center">
                                <i class="fas fa-clipboard-list text-primary me-2"></i>
                                <div>
                                  <small class="text-muted">Dịch vụ:</small>
                                  <p class="mb-0 fw-bold">{{ tempData.idlichhen.dichvu.tendichvu }}</p>
                                </div>
                              </div>
                            </div>
                            <div class="col-md-6" v-if="tempData.idlichhen?.calichhen">
                              <div class="d-flex align-items-center">
                                <i class="far fa-clock text-primary me-2"></i>
                                <div>
                                  <small class="text-muted">Thời gian:</small>
                                  <p class="mb-0 fw-bold">{{ tempData.idlichhen.calichhen.tenca }} -
                                    {{ tempData.idlichhen.calichhen.thoigianca }}</p>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-else-if="tempData==null">
                      <div class="card-header bg-primary text-white">
                        <i class="fas fa-calendar-check me-2"></i>Hãy lựa chọn dịch vụ và thời gian
                      </div>
                      <img class="card-img-top" src="~/assets/image/cat3.jpg" alt="">
                    </div>
                  </div>
                </div>



              </div>
            </div>
          </div>
          <div class="col-6">
            <div class="accordion" id="accordionExample">
              <div class="accordion-item">
                <h2 class="accordion-header">
                  <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne"
                          aria-expanded="true" aria-controls="collapseOne">
                    Chọn ngày và giờ hẹn
                  </button>
                </h2>
                <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#accordionExample">
                  <div class="accordion-body">
                    <Calendar/>
                  </div>
                </div>
              </div>
              <div class="accordion-item">
                <h2 class="accordion-header">
                  <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                          data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                    Chọn và nhập thông tin thú cưng
                  </button>
                </h2>
                <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                  <div class="accordion-body">
                    <strong>This is the second item's accordion body.</strong> It is hidden by default, until the
                    collapse plugin adds the appropriate classes that we use to style each element. These classes
                    control the overall appearance, as well as the showing and hiding via CSS transitions. You can
                    modify any of this with custom CSS or overriding our default variables. It's also worth noting that
                    just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit
                    overflow.
                  </div>
                </div>
              </div>
              <div class="accordion-item">
                <h2 class="accordion-header">
                  <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                          data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                    Xác nhận thông tin và thanh toán
                  </button>
                </h2>
                <div id="collapseThree" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
                  <div class="accordion-body">
                    <strong>This is the third item's accordion body.</strong> It is hidden by default, until the
                    collapse plugin adds the appropriate classes that we use to style each element. These classes
                    control the overall appearance, as well as the showing and hiding via CSS transitions. You can
                    modify any of this with custom CSS or overriding our default variables. It's also worth noting that
                    just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit
                    overflow.
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

<style scoped>

</style>