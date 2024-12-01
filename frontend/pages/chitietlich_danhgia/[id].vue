<template>
  <div class="container py-5">
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div v-if="thayDoiLichHenStore.lichHenDetails">
              <div class="card border-primary mb-3">
                <div class="card-header bg-primary text-white">
                  <i class="fas fa-calendar-check me-2"></i>Chi tiết lịch hẹn
                  {{ thayDoiLichHenStore.lichHenDetails.id }}
                  <div :class="getTrangThaiClass(thayDoiLichHenStore.lichHenDetails.trangthai)">
                    <span class="badge bg-success"> {{
                        getTrangThaiText(thayDoiLichHenStore.lichHenDetails.trangthai)
                      }}</span>
                  </div>
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <div v-if="thayDoiLichHenStore.getDichVu && thayDoiLichHenStore.getCaLichHen">
                      <div class="col mb-3">
                        <div class="row">
                          <div class="col">
                            <h5 class="text-muted"><i class="fas fa-clipboard-list text-primary me-2"></i>Dịch vụ</h5>
                            <p class="mb-0"><strong>Tên:</strong> {{ thayDoiLichHenStore.getDichVu.tendichvu }}</p>
                            <p class="mb-0"><strong>Giá tiền:</strong>
                              {{ formatCurrency(thayDoiLichHenStore.getDichVu.giatien) }}</p>
                            <div class="col">
                              <p class="mb-0"><strong>Mô tả:</strong> {{ thayDoiLichHenStore.getDichVu.mota }}</p>
                            </div>
                          </div>
                          <div class="col">
                            <img class="card-img-top" src="/assets/image/cat2.jpg" alt="VN Pay">
                          </div>
                          <div class="col-12">
                            <strong>Thời gian : </strong>
                            <div class="row">
                              <div class="col">
                                Tên ca: {{ thayDoiLichHenStore.getCaLichHen.tenca }}
                              </div>
                              <div class="col">
                                Giờ : {{ thayDoiLichHenStore.getCaLichHen.thoigianca }}
                              </div>
                              <div class="col">
                                Ngày : {{ new Date(thayDoiLichHenStore.getDate).toLocaleDateString() }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-if="thayDoiLichHenStore.getThuCung" class="col-12">
                      <h5 class="text-muted mb-3"><i class="fas fa-paw text-primary me-2"></i>Thông tin thú cưng</h5>
                      <div class="row">
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Tên:</strong> {{ thayDoiLichHenStore.getThuCung.ten }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Tuổi:</strong> {{ thayDoiLichHenStore.getThuCung.tuoi }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Giống:</strong> {{ thayDoiLichHenStore.getThuCung.giong }}</p>
                        </div>
                        <div class="col-md-6 mb-2">
                          <p class="mb-0"><strong>Cân nặng:</strong> {{ thayDoiLichHenStore.getThuCung.cannang }} kg</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center">
              <img src="/assets/image/cat3.jpg" alt="Chọn dịch vụ" class="img-fluid rounded mb-3"
                   style="max-height: 200px;">
              <p class="lead">Đang tải thông tin lịch hẹn...</p>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-6 mb-4" v-if="thayDoiLichHenStore.lichHenDetails">
        <div class="card p-4">
          <h4 class="card-title mb-4">Đánh giá lịch hẹn của bạn</h4>
          <div v-if="chiTietDanhGia">
            <CapNhatDanhGia
                :idLichHen="thayDoiLichHenStore.lichHenDetails.id"
                :initialRating="chiTietDanhGia.sosao"
                :initialReview="chiTietDanhGia.mota"
            />
            <div class="mb-3 p-3 border rounded">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                  Đánh giá  :
                </div>
                <div class="heart-rating">
                  <span v-for="n in Number(chiTietDanhGia.sosao)" :key="n" class="text-warning">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                         class="bi bi-star-fill" viewBox="0 0 16 16">
                  <path
                      d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
                </svg>
                  </span>
                </div>
              </div>
              <p class="mb-1">{{ chiTietDanhGia.mota }}</p>
              <small class="text-muted">{{ new Date(chiTietDanhGia.date).toLocaleDateString() }}</small>
            </div>
          </div>
          <div v-else >
            <ThemDanhGia :idLichHen="thayDoiLichHenStore.lichHenDetails.id" />
            <p class="text-center">Chưa có đánh giá nào cho lịch hẹn này hãy thêm đánh giá !.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import {ref, onMounted} from 'vue';
import {useThayDoiLichHenStore} from '~/stores/ThayDoiLichHen'
import {useDanhGiaStore} from '~/stores/DanhGiaStores';
import ChiTietDanhGia from '~/models/ChiTietDanhGia';
import {useToast} from "vue-toastification";
const toast = useToast();


const thayDoiLichHenStore = useThayDoiLichHenStore()
const danhGiaStore = useDanhGiaStore()
const chiTietDanhGia = ref<ChiTietDanhGia | null>(null);

const route = useRoute()
const id = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id;

onMounted(async () => {
  await thayDoiLichHenStore.fetchLichHenDetails(Number(id))
  await hienThiDanhGia(id)
})

async function hienThiDanhGia(idLichHen: string) {
  const result = await danhGiaStore.chitietdanhgiatheolichhen(idLichHen);
  if ('success' in result) {
    // Handle the case where the result is { success: boolean; message: string }
    console.log(result.message);
    chiTietDanhGia.value = null;
  } else {
    chiTietDanhGia.value = result;
  }
}

async function capNhatDanhGia(idLichHen: string, mota: string, sosao: number) {
  try {
    await danhGiaStore.capNhatDanhGia(idLichHen, mota, sosao);
    toast.success('Cập nhật đánh giá thành công!')
  }catch (error) {
    toast.error('Cập nhật đánh giá thất bại!')
  }
}

definePageMeta({
  middleware: ['auth']
})

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value);
};

const getTrangThaiText = (trangthai: number) => {
  const trangThaiMap: Record<number, string> = {
    0: 'Thành công',
    1: 'Thất bại',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công'
  }
  return trangThaiMap[trangthai] || 'Không xác định'
}

const getTrangThaiClass = (trangthai: number) => {
  const classMap: Record<number, string> = {
    0: 'text-success',
    1: 'text-danger',
    2: 'text-danger',
    3: 'text-info',
    4: 'text-warning',
    5: 'text-secondary',
    6: 'text-success'
  }
  return classMap[trangthai] || ''
}
</script>

<style scoped>

</style>