<template>
  <div class="container py-5">
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div v-if="thayDoiLichHenStore.lichHenDetails">
              <!-- Thông tin lịch hẹn -->
              <div class="mb-4">
                <h5 class="text-primary"><i class="fas fa-calendar-alt me-2"></i>Thông tin lịch hẹn</h5>
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>ID:</strong> {{ thayDoiLichHenStore.lichHenDetails.id }}</p>
                    <p><strong>Ngày hẹn:</strong> {{ formatDate(thayDoiLichHenStore.getDate) }}</p>
                    <p><strong>Giờ hẹn:</strong> {{ thayDoiLichHenStore.getCaLichHen.thoigianca }}</p>
                    <p><strong>Email người đặt:</strong> {{ thayDoiLichHenStore.lichHenDetails.emailNguoiDat }}</p>
                  </div>
                  <div class="col-md-6">
                    <p><strong>Trạng thái ca:</strong> {{ thayDoiLichHenStore.lichHenDetails.trangthaica ? 'Còn trống' : 'Đã đặt' }}</p>
                    <p><strong>Số lần thay đổi:</strong> {{ thayDoiLichHenStore.lichHenDetails.solanthaydoi }}</p>
                    <p><strong>Số lần nhắc nhở:</strong> {{ thayDoiLichHenStore.lichHenDetails.solannhacnho }}</p>
                    <p><strong>Trạng thái:</strong> {{ ['Thành công', 'Đã hủy', 'Chờ xác nhận'][thayDoiLichHenStore.lichHenDetails.trangthai] || 'Không xác định' }}</p>
                  </div>
                </div>
              </div>

              <!-- Thông tin dịch vụ -->
              <div class="mb-4">
                <h5 class="text-primary"><i class="fas fa-clipboard-list me-2"></i>Thông tin dịch vụ</h5>
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>Tùy chọn dịch vụ:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.giatien }} USD</p>
                    <p><strong>Cân nặng tối thiểu:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmin }} kg</p>
                    <p><strong>Cân nặng tối đa:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmax || 'Không giới hạn' }} kg</p>
                  </div>
                  <div class="col-md-6">
                    <p><strong>ID Ca lịch hẹn:</strong> {{ thayDoiLichHenStore.getCaLichHen.id }}</p>
                    <p><strong>Thời gian hủy:</strong> {{ thayDoiLichHenStore.lichHenDetails.thoigianhuy ? formatDate(thayDoiLichHenStore.lichHenDetails.thoigianhuy) : 'Chưa hủy' }}</p>
                    <p><strong>Thời gian thay đổi:</strong> {{ thayDoiLichHenStore.lichHenDetails.thoigianthaydoi ? formatDate(thayDoiLichHenStore.lichHenDetails.thoigianthaydoi) : 'Chưa thay đổi' }}</p>
                  </div>
                </div>
              </div>

              <!-- Thông tin thú cưng -->
              <div class="mb-4">
                <h5 class="text-primary"><i class="fas fa-paw me-2"></i>Thông tin thú cưng</h5>
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>Tên:</strong> {{ thayDoiLichHenStore.getThuCung.ten }}</p>
                    <p><strong>Tuổi:</strong> {{ thayDoiLichHenStore.getThuCung.tuoi }}</p>
                    <p><strong>Giống:</strong> {{ thayDoiLichHenStore.getThuCung.giong }}</p>
                    <p><strong>Cân nặng:</strong> {{ thayDoiLichHenStore.getThuCung.cannang }} kg</p>
                  </div>
                  <div class="col-md-6">
                    <p><strong>Giới tính:</strong> {{ thayDoiLichHenStore.getThuCung.gioitinh ? 'Đực' : 'Cái' }}</p>
                    <p><strong>Loại:</strong> {{ thayDoiLichHenStore.getThuCung.cophaimeokhong ? 'Mèo' : 'Chó' }}</p>
                    <p><strong>Tình trạng sức khỏe:</strong> {{ thayDoiLichHenStore.getThuCung.tinhtrangsuckhoe }}</p>
                    <p v-if="thayDoiLichHenStore.getThuCung.mota"><strong>Mô tả:</strong> {{ thayDoiLichHenStore.getThuCung.mota }}</p>
                  </div>
                </div>
              </div>

              <!-- Thông tin khách hàng -->
              <div>
                <h5 class="text-primary"><i class="fas fa-user me-2"></i>Thông tin khách hàng</h5>
                <p><strong>ID Khách hàng:</strong> {{ thayDoiLichHenStore.lichHenDetails.idkhachhang }}</p>
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
          <div v-if="chiTietDanhGia">
            <div class="mb-3 p-3 border rounded">
              <CapNhatDanhGia
                  :danhGia="convertToDanhGia(chiTietDanhGia)"
              />
            </div>
          </div>
          <div v-else>
            <ThemDanhGia :idLichHen="String(thayDoiLichHenStore.lichHenDetails.id)"/>
            <p class="text-center text fs-4">Chưa có đánh giá nào cho lịch hẹn này hãy thêm đánh giá!</p>
          </div>
        </div>
      </div>
      <div v-else class="col">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title text-center text fs-4">Bạn cần sử dụng dịch vụ sau đó mới có quyền đánh giá dịch vụ</h4>
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