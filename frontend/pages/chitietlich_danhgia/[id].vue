<template>
  <div class="container py-5">
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div v-if="thayDoiLichHenStore.lichHenDetails">
              <!-- Thông tin lịch hẹn -->
              <div class="row g-3">
                <h5 class="text-muted"><i class="fas fa-info-circle text-primary me-2"></i>Thông tin lịch hẹn</h5>
                <p><strong>ID:</strong> {{ thayDoiLichHenStore.lichHenDetails.id }}</p>
                <p><strong>Ngày hẹn:</strong> {{ formatDate(thayDoiLichHenStore.getDate) }}</p>
                <p><strong>Giờ hẹn:</strong> {{ thayDoiLichHenStore.getCaLichHen.thoigianca }}</p>
                <p><strong>Email người đặt:</strong> {{ thayDoiLichHenStore.lichHenDetails.emailNguoiDat }}</p>
                <p><strong>Trạng thái ca:</strong> {{ thayDoiLichHenStore.lichHenDetails.trangthaica ? 'Còn trống' : 'Đã đặt' }}</p>
                <p><strong>Số lần thay đổi:</strong> {{ thayDoiLichHenStore.lichHenDetails.solanthaydoi }}</p>
                <p><strong>Số lần nhắc nhở:</strong> {{ thayDoiLichHenStore.lichHenDetails.solannhacnho }}</p>
              </div>

              <!-- Thông tin dịch vụ -->
              <div class="col-md-6">
                <h5 class="text-muted"><i class="fas fa-clipboard-list text-primary me-2"></i>Thông tin dịch vụ</h5>
                <p><strong>Tên dịch vụ:</strong> {{ thayDoiLichHenStore.getDichVu?.tendichvu }}</p>
                <p><strong>Mô tả:</strong> {{ thayDoiLichHenStore.getDichVu?.mota }}</p>
                <p><strong>Tùy chọn dịch vụ:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.giatien }} USD</p>
                <p><strong>Cân nặng tối thiểu:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmin }} kg</p>
                <p><strong>Cân nặng tối đa:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmax || 'Không giới hạn' }} kg</p>
                <img v-if="thayDoiLichHenStore.getDichVu?.anh" class="img-fluid mt-2" :src="thayDoiLichHenStore.getDichVu.anh" alt="Hình ảnh dịch vụ">
              </div>

              <!-- Thông tin thú cưng -->
              <div class="col-md-6">
                <h5 class="text-muted"><i class="fas fa-paw text-primary me-2"></i>Thông tin thú cưng</h5>
                <p><strong>Tên:</strong> {{ thayDoiLichHenStore.getThuCung.ten }}</p>
                <p><strong>Tuổi:</strong> {{ thayDoiLichHenStore.getThuCung.tuoi }}</p>
                <p><strong>Giống:</strong> {{ thayDoiLichHenStore.getThuCung.giong }}</p>
                <p><strong>Cân nặng:</strong> {{ thayDoiLichHenStore.getThuCung.cannang }} kg</p>
                <p><strong>Giới tính:</strong> {{ thayDoiLichHenStore.getThuCung.gioitinh ? 'Đực' : 'Cái' }}</p>
                <p><strong>Loại:</strong> {{ thayDoiLichHenStore.getThuCung.cophaimeokhong ? 'Mèo' : 'Chó' }}</p>
                <p><strong>Tình trạng sức khỏe:</strong> {{ thayDoiLichHenStore.getThuCung.tinhtrangsuckhoe }}</p>
                <p v-if="thayDoiLichHenStore.getThuCung.mota"><strong>Mô tả:</strong> {{ thayDoiLichHenStore.getThuCung.mota }}</p>
              </div>

              <!-- Các thông tin khác -->
              <div class="col-md-6">
                <h5 class="text-muted"><i class="fas fa-cog text-primary me-2"></i>Thông tin khác</h5>
                <p><strong>ID Khách hàng:</strong> {{ thayDoiLichHenStore.lichHenDetails.idkhachhang }}</p>
                <p><strong>ID Ca lịch hẹn:</strong> {{ thayDoiLichHenStore.getCaLichHen.id }}</p>
                <p><strong>Thời gian hủy:</strong> {{ thayDoiLichHenStore.lichHenDetails.thoigianhuy ? formatDate(thayDoiLichHenStore.lichHenDetails.thoigianhuy) : 'Chưa hủy' }}</p>
                <p><strong>Thời gian thay đổi:</strong> {{ thayDoiLichHenStore.lichHenDetails.thoigianthaydoi ? formatDate(thayDoiLichHenStore.lichHenDetails.thoigianthaydoi) : 'Chưa thay đổi' }}</p>
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
      <div class="col-lg-6 mb-4"
           v-if="thayDoiLichHenStore.lichHenDetails && thayDoiLichHenStore.lichHenDetails.trangthai === 0">
        <div class="card p-4">
          <h4 class="card-title mb-4">Đánh giá lịch hẹn của bạn</h4>
          <div v-if="chiTietDanhGia">
            <div class="mb-3 p-3 border rounded">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                  Đánh giá :
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
              <div class="row p-4">
                <div class="col">
                  {{ formatDate(chiTietDanhGia.date) }}
                </div>
                <div class="col">
                  <CapNhatDanhGia
                      :danhGia="convertToDanhGia(chiTietDanhGia)"
                  />
                </div>
              </div>
            </div>
          </div>
          <div v-else>
            <ThemDanhGia :idLichHen="String(thayDoiLichHenStore.lichHenDetails.id)"/>
            <p class="text-center">Chưa có đánh giá nào cho lịch hẹn này hãy thêm đánh giá!</p>
          </div>
        </div>
      </div>
      <div v-else class="col">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title text-center">Bạn cần sử dụng dịch vụ sau đó mới có quyền đánh giá dịch vụ</h4>
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
import type {ChiTietDanhGia} from '~/models/ChiTietDanhGia';
import {useToast} from "vue-toastification";
import CapNhatDanhGia from "~/components/CapNhatDanhGia.vue";

const toast = useToast();


const thayDoiLichHenStore = useThayDoiLichHenStore()
const danhGiaStore = useDanhGiaStore()
const chiTietDanhGia = ref<ChiTietDanhGia | null>(null);
import type DanhGia from '~/models/DanhGia';
import type LichHenDetails from "~/models/LichHenDetails";

const route = useRoute()
const id = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id;
definePageMeta({
  middleware: ['auth']
})


const lichHen = ref<LichHenDetails | null>(null);

onMounted(async () => {
  await thayDoiLichHenStore.fetchLichHenDetails(Number(id))
  lichHen.value = thayDoiLichHenStore.lichHenDetails || null;
  await hienThiDanhGia(id)
})
const convertToDanhGia = (chiTietDanhGia: ChiTietDanhGia | null): DanhGia | null => {
  if (!chiTietDanhGia) return null;
  return {
    ...chiTietDanhGia,
    toJSON() {
      return {...this};
    }
  };
};

async function hienThiDanhGia(idLichHen: string) {
  const result = await danhGiaStore.chitietdanhgiatheolichhen(idLichHen);
  if ('success' in result) {
    console.log(result.message);
    chiTietDanhGia.value = null;
  } else {
    chiTietDanhGia.value = result;
  }
}


const formatDate = (date: string | number | Date) => {
  return new Date(date).toLocaleDateString('vi-VN');
};

const getTrangThaiText = (trangthai: number) => {
  const trangThaiMap: Record<number, string> = {
    0: 'Thành công',
    1: 'Đã đổi',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công',
    7: 'Đã hoàn tiền',
    8: 'Chờ sử dụng'
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
    6: 'text-success',
    7: 'text-secondary',
    8: 'text-success'
  }
  return classMap[trangthai] || ''
}

function chiTietDichVu(idDichVu : string){
  return navigateTo('/services/' + idDichVu)
}

</script>

<style scoped>

</style>