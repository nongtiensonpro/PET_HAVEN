<template>
  <div class="container">
    <div class="card">
      <div v-if="service">
        <LoiChaoKhiKhachHangXemDichVu :service="service" />
        <div class="card-header">
          <div class="row">
            <div class="col">
              <div class="text fs-2">{{ service.tendichvu }}</div>
            </div>
          </div>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col">
              <div v-if="!service.anh">
                <img src="~/assets/image/cat1.jpg" class="card-img-top" alt="Default image">
              </div>
              <div v-else>
                <img :src="service.anh" class="card-img-top" :alt="service.tendichvu">
              </div>
            </div>
            <div class="col card-header ">
              <div>
                <p class="card-header text fs-4">{{ service.mota }}</p>
              </div>
              <ChonDichVu :serviceOptions="service.tuyChonDichVus" />
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-danger p-4">{{ t('errorOccurred') }}</div>
    </div>

    <div class="mt-5 card p-4">
      <h3 class="mb-4 card-header">{{ t('reviewsAndComments') }}</h3>
      <div v-if="danhGias.length > 0">
        <div style="padding-bottom: 10px">
          <TongHopBinhLuanGemini :danhGias="danhGias" />
        </div>
        <div v-for="danhGia in danhGias" :key="danhGia.id" class="card mb-3">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div>
                <strong class="me-2">{{ t('rating') }}:</strong>
                <span v-for="n in 5" :key="n" class="star-rating">
                  <i :class="n <= danhGia.sosao ? 'fas fa-star text-warning' : 'far fa-star text-muted'">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                      class="bi bi-star-fill" viewBox="0 0 16 16">
                      <path
                        d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
                    </svg>
                  </i>
                </span>
              </div>
              <small class="text-muted">{{ formatDate(danhGia.date) }}</small>
              <div
                v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin') || userInfo.role.includes('manager') || danhGia.idhoadon.idlichhen.emailNguoiDat === userInfo?.name">
                <div @click="anDanhGia(danhGia.id)">
                  {{ t('hideComment') }}
                </div>
              </div>
            </div>
            <p class="card-text mb-3"><strong>{{ t('content') }}:</strong> {{ danhGia.mota }}</p>

            <div class="row">
              <div class="col-md-6">
                <h6 class="mb-2">{{ t('customerInfo') }}:</h6>
                <p class="mb-1"><small class="text-muted">Email: {{ danhGia.idhoadon.idlichhen.emailNguoiDat }}</small>
                </p>
              </div>
              <div class="col-md-6">
                <h6 class="mb-2">{{ t('petInfo') }}:</h6>
                <p class="mb-1"><small class="text-muted">{{ t('name') }}: {{ danhGia.idhoadon.idlichhen.thucung.ten
                    }}</small></p>
                <p class="mb-1"><small class="text-muted">{{ t('breed') }}: {{ danhGia.idhoadon.idlichhen.thucung.giong
                    }}</small>
                </p>
                <p class="mb-1"><small class="text-muted">{{ t('weight') }}: {{
                  danhGia.idhoadon.idlichhen.thucung.cannang }}
                    kg</small></p>
                <p class="mb-1"><small class="text-muted">{{ t('age') }}: {{ danhGia.idhoadon.idlichhen.thucung.tuoi }}
                    {{ t('years') }}</small></p>
              </div>
            </div>
          </div>
          <div v-if="danhGia.idhoadon.idlichhen.emailNguoiDat === userInfo?.name">
            <CapNhatDanhGiaTrangChu :danhGia="danhGia" />
          </div>
        </div>
      </div>
      <div v-else class="alert alert-light">
        <i class="fas fa-info-circle me-2"></i>
        {{ t('noReviews') }}
      </div>
    </div>
    <a class="custom-button p-4" href="/" role="button">{{ t('backToHome') }}</a>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted, watch } from 'vue';
import { useServiceStore } from '~/stores/DichVuStores';
import { useDanhGiaStore } from '~/stores/DanhGiaStores';
import { useRoute } from 'vue-router';
import type DanhGia from "~/models/DanhGia";
import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';
import Swal from "sweetalert2";
import DichVu from "~/models/DichVu";
import ChonDichVu from "~/components/ChonDichVu.vue";
import CapNhatDanhGiaTrangChu from "~/components/CapNhatDanhGiaTrangChu.vue";
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const toast = useToast();
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo);
const serviceStore = useServiceStore();
const danhGiaStore = useDanhGiaStore();
const route = useRoute();
const service = ref<DichVu | null>(null);
const errorMessage = ref(t('errorOccurred'));
const danhGias = ref<DanhGia[]>([]);
const refreshInterval = ref<number | null>(null);

const fetchData = async () => {
  const serviceId = parseInt(route.params.id as string);
  service.value = serviceStore.services.find(s => s.id === serviceId) || null;

  if (!service.value) {
    errorMessage.value = t('serviceNotFound');
  } else {
    await danhGiaStore.fetchDanhGiaByDichVu(serviceId);
    danhGias.value = danhGiaStore.danhGias;
  }
};

onMounted(async () => {
  await serviceStore.fetchServices();
  await fetchData();

  refreshInterval.value = window.setInterval(fetchData, 5 * 60 * 1000);
});

onUnmounted(() => {

  if (refreshInterval.value) {
    clearInterval(refreshInterval.value);
  }
});

watch(() => route.params.id, async () => {
  await fetchData();
});

const formatDate = (dateString: string): string => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  };
  return new Date(dateString).toLocaleDateString('vi-VN', options);
};



async function anDanhGia(idDanhGia: number) {
  try {
    const result = await Swal.fire({
      title: t('confirmHideReview'),
      text: t('confirmHideReviewText'),
      icon: 'success',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: t('yes'),
      cancelButtonText: t('no')
    });
    if (result.isConfirmed) {
      await danhGiaStore.anDanhGia(idDanhGia);
      danhGias.value = danhGias.value.filter(dg => dg.id !== idDanhGia);
      toast.success(t('hideReviewSuccess'), {})
      await fetchData();
    }
  } catch (error) {
    toast.error(t('hideReviewError'), {})
  }
}

</script>

<style scoped>
.text-danger {
  color: red;
}

.star-rating {
  font-size: 1.2rem;
}
</style>