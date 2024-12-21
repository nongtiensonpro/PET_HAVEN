<template>
  <div class="container my-5">
    <div class="card">

      <div v-if="service">
        <LoiChaoKhiKhachHangXemDichVu :service="service"/>
        <div class="card-header">
          <div class="row">
            <div class="col">
              <h2 class="mb-0">{{ service.tendichvu }}</h2>
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
            <div class="col">
              <ChonDichVu :serviceOptions="service.tuyChonDichVus" />
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-danger p-4">{{ errorMessage }}</div>
    </div>

    <div class="mt-5">
      <h3 class="mb-4">Đánh giá và nhận xét</h3>
      <div v-if="danhGias.length > 0">
        <div style="padding-bottom: 10px">
          <TongHopBinhLuanGemini :danhGias="danhGias"/>
        </div>
        <div v-for="danhGia in danhGias" :key="danhGia.id" class="card mb-3">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div>
                <strong class="me-2">Đánh giá:</strong>
                <span v-for="n in 5" :key="n" class="star-rating">
                  <i :class="n <= danhGia.sosao ? 'fas fa-star text-warning' : 'far fa-star text-muted'">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                         class="bi bi-star-fill" viewBox="0 0 16 16">
                      <path
                          d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
                    </svg>
                  </i>
                </span>
              </div>
              <small class="text-muted">{{ formatDate(danhGia.date) }}</small>
              <div v-if="danhGia.idhoadon.idlichhen.emailNguoiDat === userInfo?.name">
                <CapNhatDanhGia
                    :danhGia="danhGia"
                />
              </div>
              <div
                  v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin') || userInfo.role.includes('manager') || danhGia.idhoadon.idlichhen.emailNguoiDat === userInfo?.name">
                <div @click="anDanhGia(danhGia.id)">
                  Ẩn bình luận
                </div>
              </div>
            </div>

            <p class="card-text mb-3"><strong>Nội dung:</strong> {{ danhGia.mota }}</p>

            <div class="row">
              <div class="col-md-6">
                <h6 class="mb-2">Thông tin khách hàng:</h6>
                <p class="mb-1"><small class="text-muted">Email: {{ danhGia.idhoadon.idlichhen.emailNguoiDat }}</small>
                </p>
                <p class="mb-1"><small class="text-muted">Dịch vụ: {{
                    danhGia.idhoadon.idlichhen.dichvu.tendichvu
                  }}</small></p>
              </div>
              <div class="col-md-6">
                <h6 class="mb-2">Thông tin thú cưng:</h6>
                <p class="mb-1"><small class="text-muted">Tên: {{ danhGia.idhoadon.idlichhen.thucung.ten }}</small></p>
                <p class="mb-1"><small class="text-muted">Giống: {{ danhGia.idhoadon.idlichhen.thucung.giong }}</small>
                </p>
                <p class="mb-1"><small class="text-muted">Cân nặng: {{ danhGia.idhoadon.idlichhen.thucung.cannang }}
                  kg</small></p>
                <p class="mb-1"><small class="text-muted">Tuổi: {{ danhGia.idhoadon.idlichhen.thucung.tuoi }}
                  tuổi</small></p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="alert alert-info">
        <i class="fas fa-info-circle me-2"></i>
        Chưa có đánh giá nào cho dịch vụ này.
      </div>
    </div>
  </div>
  <a class="custom-button p-4" href="/" role="button">Trở về trang chủ</a>
</template>

<script setup lang="ts">
import {ref, onMounted, computed, onUnmounted, watch} from 'vue';
import {useServiceStore} from '~/stores/DichVuStores';
import {useDanhGiaStore} from '~/stores/DanhGiaStores';
import {useRoute} from 'vue-router';
import CapNhatDanhGia from '~/components/CapNhatDanhGia.vue';
import type DanhGia from "~/models/DanhGia";
import Service from "~/models/DichVu";
import {useUserStore} from '~/stores/user';
import {useToast} from 'vue-toastification';
import Swal from "sweetalert2";
import CapNhatCaHen from "~/components/CapNhatCaLichHen.vue";
import DichVu from "~/models/DichVu";
import ChonDichVu from "~/components/ChonDichVu.vue";

const toast = useToast();
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo);
const serviceStore = useServiceStore();
const danhGiaStore = useDanhGiaStore();
const route = useRoute();
const service = ref<DichVu | null>(null);
const errorMessage = ref('Có lỗi đã xảy ra vui lòng thử lại !');
const danhGias = ref<DanhGia[]>([]);
const refreshInterval = ref<number | null>(null);

const fetchData = async () => {
  const serviceId = parseInt(route.params.id as string);
  service.value = serviceStore.services.find(s => s.id === serviceId) || null;

  if (!service.value) {
    errorMessage.value = 'Dịch vụ không tồn tại hoặc đã bị xóa.';
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

const formatCurrency = (value: number): string => {
  return value.toLocaleString('vi-VN');
};

const updateReview = (updatedReview: { id: number; sosao: number; mota: string }) => {
  const index = danhGias.value.findIndex(dg => dg.id === updatedReview.id);
  if (index !== -1) {
    danhGias.value[index] = {...danhGias.value[index], ...updatedReview};
  }
};

async function anDanhGia(idDanhGia: number) {
  try {
    const result = await Swal.fire({
      title: 'Xác nhận',
      text: "Bạn có muốn ẩn đánh giá không?",
      icon: 'success',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Có',
      cancelButtonText: 'Không'
    });
    if (result.isConfirmed) {
      await danhGiaStore.anDanhGia(idDanhGia);
      danhGias.value = danhGias.value.filter(dg => dg.id !== idDanhGia);
      toast.success('Đã ẩn bình luận thành công!', {})
      await fetchData();
    }
  } catch (error) {
    toast.error('Lỗi ẩn bình luận! Vui lòng thử lại.', {})
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