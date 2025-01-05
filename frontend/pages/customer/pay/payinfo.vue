<template>
  <div class="container">
    <div class="card no-border">
      <div class="col-12">
        <div class="text text-center">
          <div>{{ t('bookingSuccessful') }}</div>
          <p>{{ t('paymentTimeLimit') }}</p>
          <p>{{ t('autoCancelNotice') }}</p>
        </div>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="row">
            <div class="col-md-6">
              <div class="card mb-4">
                <div class="card-body">
                  <h5 class="card-title text-center">{{ t('payWithPayPal') }}</h5>
                  <img src="~/assets/image/PayPal-logo.png" alt="PayPal logo" class="img-fluid mb-3">
                  <div class="row">
                    <div class="col">
                      <button @click="thanhToanOnline" class="custom-button">
                        {{ t('payNow') }}
                      </button>
                    </div>
                    <div class="col">
                      <button @click="thanhThayDoiLich" class="custom-button">
                        {{ t('cancelOrChangeTime') }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title mb-4">{{ t('paypalInfo') }}</h5>
                  <div class="accordion" id="paypalInfo">
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseOne">
                          {{ t('whatIsPaypal') }}
                        </button>
                      </h2>
                      <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <p>{{ t('paypalDescription') }}</p>
                          <ul>
                            <li>{{ t('sendReceiveMoney') }}</li>
                            <li>{{ t('protectInfo') }}</li>
                            <li>{{ t('actAsIntermediary') }}</li>
                          </ul>
                          <p>{{ t('howToUse') }}</p>
                          <ol>
                            <li>{{ t('createPaypalAccount') }}</li>
                            <li>{{ t('linkCards') }}</li>
                            <li>{{ t('useEmailPassword') }}</li>
                          </ol>
                        </div>
                      </div>
                    </div>
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseTwo">
                          {{ t('howToRegister') }}
                        </button>
                      </h2>
                      <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <h6>{{ t('registration') }}</h6>
                          <ol>
                            <li>{{ t('visitPaypal') }}</li>
                            <li>{{ t('chooseAccountType') }}</li>
                            <li>{{ t('enterPersonalInfo') }}</li>
                            <li>{{ t('linkPaymentMethod') }}</li>
                            <li>{{ t('verifyEmail') }}</li>
                          </ol>
                          <h6>{{ t('accountVerification') }}</h6>
                          <ol>
                            <li>{{ t('receiveCode') }}</li>
                            <li>{{ t('enterCode') }}</li>
                            <li>{{ t('setupPassword') }}</li>
                            <li>{{ t('provideIdentity') }}</li>
                          </ol>
                        </div>
                      </div>
                    </div>
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseThree">
                          {{ t('paypalFees') }}
                        </button>
                      </h2>
                      <div id="collapseThree" class="accordion-collapse collapse" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <p>{{ t('feesDescription') }}</p>
                          <table class="table table-striped">
                            <thead>
                            <tr>
                              <th>{{ t('transactionType') }}</th>
                              <th>{{ t('fees') }}</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                              <td>{{ t('domesticTransaction') }}</td>
                              <td>2,5% + 1.000 VND</td>
                            </tr>
                            <tr>
                              <td>{{ t('internationalTransaction') }}</td>
                              <td>4,4% + 1.000 VND + phí chuyển đổi ngoại tệ</td>
                            </tr>
                            <tr>
                              <td>{{ t('withdrawal') }}</td>
                              <td>1,5% + 1.000 VND</td>
                            </tr>
                            </tbody>
                          </table>
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
  </div>
</template>

<script setup lang="ts">
import {ref, computed} from 'vue';
import {useMauKhachDatDichVu} from "~/stores/MauKhachDatDichVu";
import Swal from 'sweetalert2';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const {getTempData} = useMauKhachDatDichVu()
const token = localStorage.getItem('access_token');
const isLoading = ref(false);
const countdown = ref(10);
const showOverlay = ref(false);

definePageMeta({
  middleware: ['auth']
})

function thanhToanTaiQuay() {
  return navigateTo('/customer/success');
}

function startCountdown() {
  showOverlay.value = true;
  isLoading.value = true;
  countdown.value = 10;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      isLoading.value = false;
      showOverlay.value = false;
    }
  }, 1000);
}

const dichVuVaThuCungKhachHangDat = getTempData();

async function thanhThayDoiLich() {
  return navigateTo('/chi-tiet-lich/' + dichVuVaThuCungKhachHangDat?.id);
}

async function thanhToanOnline() {
  const result = await Swal.fire({
    title: t('confirmPayment'),
    text: t('confirmPaymentText'),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });

  if (result.isConfirmed) {
    const {getTempData} = useMauKhachDatDichVu()
    const tempData = computed(() => getTempData())

    startCountdown();

    fetch('http://localhost:8080/api/payPal/payment/create', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'idLichHen': tempData.value.id
      }
    })
      .then(response => response.text())
      .then(url => {
        if (url) {
          window.location.href = url;
        } else {
          console.error("Không thể tạo thanh toán");
          isLoading.value = false;
          showOverlay.value = false;
        }
      })
      .catch(error => {
        console.error("Lỗi:", error);
        isLoading.value = false;
        showOverlay.value = false;
      });
  }
}
</script>

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

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.overlay-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

.text {
  font-size: 1.5rem;
  padding-top: 10px;
}
</style>