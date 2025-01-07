<template>
  <div v-if="!accessToken || !viewRole" class="container my-5">
    <div class="alert alert-warning" role="alert">
      <i class="fas fa-exclamation-triangle me-2"></i>
      {{ t('loginRequired') }}
    </div>
  </div>
  <div v-else class="container py-5">
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <h4 class="card-title mb-4"><i class="fas fa-info-circle me-2"></i>{{ t('bookinginformation') }}</h4>
            <div v-if="tempData">
              <div class="card mb-3">
                <div class="card-header bg-light text fs-4">
                  <i class="fas fa-calendar-check me-2"></i>{{ t('appointmentDetails') }}
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <div v-if="tempData.idlichhen?.calichhen">
                      <h5 class="text-muted mb-3"><i class="fas fa-paw text-primary me-2"></i>{{ t('appointmentTime') }}
                      </h5>
                      <div class="col mb-3">
                        <div class="row">
                          <div class="col-12">
                            <strong>{{ t('time') }} : </strong>
                            <div class="row">
                              <div class="col">
                                {{ t('time') }} : {{ tempData.idlichhen.calichhen.thoigianca }}
                              </div>
                              <div class="col">
                                {{ t('date') }} : {{ tempData.idlichhen.date.toLocaleDateString() }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-if="tempData && tempData.dichvu">
                      <h5 class="text-muted mb-3"><i class="fas fa-concierge-bell text-primary me-2"></i>{{
                        t('selectedService') }}
                      </h5>
                      <div class="col mb-3">
                        <div class="row">
                          <div class="col-12">
                            <strong>{{ t('serviceName') }}: </strong>{{ tempData.dichvu.tendichvu }}
                          </div>
                          <div class="col-12">
                            <strong>{{ t('serviceDescription') }}: </strong>{{ tempData.dichvu.mota }}
                          </div>
                          <div class="col-12" v-if="tempData.tuyChonDichVu">
                            <strong>{{ t('serviceOption') }}: </strong>{{ tempData.tuyChonDichVu.tentuychon }}
                          </div>
                          <div class="col-12" v-if="tempData.tuyChonCanNang">
                            <strong>{{ t('weightOption') }}: </strong>
                            {{ tempData.tuyChonCanNang.cannangmin }} -
                            {{ tempData.tuyChonCanNang.cannangmax ? tempData.tuyChonCanNang.cannangmax : t('andUp') }}
                            {{ t('kg') }}
                          </div>
                          <div class="col-12" v-if="tempData.tuyChonCanNang">
                            <strong>{{ t('price') }}: </strong>{{ tempData.tuyChonCanNang.giatien }} USD
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-if="tempData.thucung" class="col-12">
                      <h5 class="text-muted mb-3"><i class="fas fa-paw text-primary me-2"></i>{{ t('petInfo') }}</h5>
                      <div class="row">
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>{{ t('name') }}: </strong> {{ tempData.thucung.ten }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>{{ t('age') }}:</strong> {{ tempData.thucung.tuoi }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>{{ t('breed') }}:</strong> {{ tempData.thucung.giong }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>{{ t('weight') }}:</strong> {{ tempData.thucung.cannang }} {{ t('kg')
                            }}</p>
                        </div>
                        <div class="col-12">
                          <p class="mb-0"><strong>{{ t('note') }}:</strong> {{ tempData.thucung.ghichu || t('noNote') }}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center">
              <img src="~/assets/image/cat3.jpg" alt="{{ t('chooseService') }}" class="img-fluid rounded mb-3"
                style="max-height: 200px;">
              <p class="lead">{{ t('chossetime') }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-6">
        <div class="accordion" id="bookingAccordion">
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne"
                :aria-expanded="!isDateTimeSelected" :class="{ 'collapsed': isDateTimeSelected }">
                <i class="fas fa-calendar-alt me-2"></i>{{ t('chossedayandtime') }}
              </button>
            </h2>
            <div id="collapseOne" class="accordion-collapse collapse" :class="{ 'show': !isDateTimeSelected }"
              data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <Calendar />
              </div>
            </div>
          </div>
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo"
                :aria-expanded="!isServiceSelected" :class="{ 'collapsed': isServiceSelected }">
                <i class="fas fa-paw me-2"></i>{{ t('chosseservice') }}
              </button>
            </h2>
            <div id="collapseTwo" class="accordion-collapse collapse" :class="{ 'show': !isServiceSelected }"
              data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <ChonDichVuDatLich />
              </div>
            </div>
          </div>
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseThree"
                :aria-expanded="!isPetSelected" :class="{ 'collapsed': isPetSelected }">
                <i class="fas fa-paw me-2"></i>{{ t('chosspet') }}
              </button>
            </h2>
            <div id="collapseThree" class="accordion-collapse collapse" :class="{ 'show': !isPetSelected }"
              data-bs-parent="#bookingAccordion">
              <div class="accordion-body">
                <Pet />
              </div>
            </div>
          </div>
          <div v-if="isBookingComplete">
            <LoiChaoKhiKhachHangDatLichNew />
          </div>
        </div>
      </div>
    </div>
    <div v-if="isBookingComplete">
      <div class="col">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title">{{ t('confirmBooking') }}</h4>
            <button class="custom-button" @click="payAtCounter" :disabled="isLoading">
              <span v-if="!isLoading">{{ t('confirm') }}</span>
              <span v-else>
                <i class="fas fa-spinner fa-spin me-2"></i>
                {{ t('processing') }} ({{ elapsedTime }}{{ t('seconds') }})
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import Calendar from '~/components/Calendar.vue'
import Pet from '~/components/Pet.vue'
import {useMauKhachDatDichVu} from '~/stores/MauKhachDatDichVu'
import {useToast} from 'vue-toastification'
import {useDatLichStore} from '~/stores/DatLichStores'
import ChonDichVuDatLich from "~/components/ChonDichVuDatLich.vue";
import LoiChaoKhiKhachHangDatLich from '~/components/LoiChaoKhiKhachHangDatLich.vue';
import {ref, computed} from 'vue';
import Swal from 'sweetalert2';
import {useI18n} from 'vue-i18n';


const {t, locale} = useI18n();

const bookinginformation = computed(() => t('bookinginformation'));
const chossetime = computed(() => t('chossetime'));
const chossedayandtime = computed(() => t('chossedayandtime'));
const chosseservice = computed(() => t('chosseservice'));
const chosspet = computed(() => t('chosspet'));

const accessToken = localStorage.getItem('access_token');
const viewRole = localStorage.getItem('viewRole');

const {getTempData} = useMauKhachDatDichVu()
const tempData = computed(() => getTempData())

definePageMeta({
  middleware: ['auth']
})

const datLichStore = useDatLichStore();

const isDateTimeSelected = computed(() => {
  return !!tempData.value?.idlichhen?.calichhen;
});
const isServiceSelected = computed(() => {
  return !!tempData.value?.dichvu;
});

const isPetSelected = computed(() => {
  return !!tempData.value?.thucung;
});
const isBookingComplete = computed(() => {
  return tempData.value &&
      tempData.value.dichvu &&
      tempData.value.tuyChonDichVu &&
      tempData.value.tuyChonCanNang &&
      tempData.value.thucung &&
      tempData.value.idlichhen?.calichhen &&
      tempData.value.idlichhen?.date;
});

const isLoading = ref(false);
const elapsedTime = ref(0);
const toast = useToast();

async function payAtCounter() {
  isLoading.value = true;
  elapsedTime.value = 0;
  const timer = setInterval(() => {
    elapsedTime.value++;
  }, 1000);
  const result = await Swal.fire({
    title: t('confirmBooking'),
    text: t('confirmBookingText'),
    icon: 'success',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('confirm'),
    cancelButtonText: t('cancel')
  });
  if (result.isConfirmed) {
    try {
      await datLichStore.xacNhanDatLich();
      clearInterval(timer);
      isLoading.value = false;
      return navigateTo('/customer/pay/payinfo');
    } catch (error) {
      clearInterval(timer);
      isLoading.value = false;
      toast.error(t('errorOccurred'));
    }
  }
}
</script>

<style scoped>
.accordion-button:not(.collapsed) {
  background-color: #f6f6ea;
  color: #400D01;
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