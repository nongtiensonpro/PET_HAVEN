<template>
  <div class="container bg-light mt-4 p-4">
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải thông tin hóa đơn...</span>
      </div>
    </div>
    <div v-else-if="hoaDon">
      <h2 class="mb-4">Chi tiết hóa đơn</h2>
      <div class="row g-4">
        <div class="col-md-6 info-section">
          <h3 class="section-title">Thông tin hóa đơn</h3>
          <p><strong>Mã hóa đơn:</strong> {{ hoaDon.id }}</p>
          <p><strong>Ngày lập:</strong> {{ formatDate(hoaDon.date) }}</p>
          <p><strong>Trạng thái:</strong> {{ getTrangThai(hoaDon.trangthai) }}</p>
          <p><strong>Phương thức thanh toán:</strong> {{ hoaDon.phuongthucthanhtoan }}</p>
          <p><strong>Mã giao dịch:</strong> {{ hoaDon.magiaodich }}</p>
          <p><strong>Người thanh toán:</strong> {{ hoaDon.nguoithanhtoan }}</p>
          <p v-if="hoaDon.idgiamgia"><strong>Mã giảm giá:</strong> {{ hoaDon.idgiamgia }}</p>
        </div>
        <div class="col-md-6 info-section">
          <h3 class="section-title">Thông tin thanh toán</h3>
          <p><strong>Số tiền ban đầu:</strong> {{ formatCurrency(hoaDon.sotienbandau) }}</p>
          <p><strong>Số tiền thanh toán:</strong> {{ formatCurrency(hoaDon.sotien) }}</p>
          <p><strong>Ngày thanh toán:</strong> {{ formatDate(hoaDon.ngaythanhtoan) }}</p>
        </div>
        <div class="col-md-12 info-section">
          <h3 class="section-title">Thông tin lịch hẹn</h3>
          <p><strong>Mã lịch hẹn:</strong> {{ hoaDon.idlichhen.id }}</p>
          <p><strong>Mã khách hàng:</strong> {{ hoaDon.idlichhen.idkhachhang }}</p>
          <p><strong>Ngày hẹn:</strong> {{ formatDate(hoaDon.idlichhen.date) }}</p>
          <p><strong>Trạng thái lịch hẹn:</strong> {{ getTrangThaiLichHen(hoaDon.idlichhen.trangthai) }}</p>
          <p><strong>Email người đặt:</strong> {{ hoaDon.idlichhen.emailNguoiDat }}</p>
          <p><strong>Ca lịch hẹn:</strong> {{ hoaDon.idlichhen.idcalichhen.thoigianca }}</p>
          <p><strong>Trạng thái ca:</strong> {{ hoaDon.idlichhen.trangthaica ? 'Đã xác nhận' : 'Chưa xác nhận' }}</p>
          <p><strong>Số lần thay đổi:</strong> {{ hoaDon.idlichhen.solanthaydoi }}</p>
          <p><strong>Số lần nhắc nhở:</strong> {{ hoaDon.idlichhen.solannhacnho }}</p>
        </div>
        <div class="col-md-6 info-section">
          <h3 class="section-title">Thông tin thú cưng</h3>
          <p><strong>ID:</strong> {{ hoaDon.idlichhen.thucung.id }}</p>
          <p><strong>Tên:</strong> {{ hoaDon.idlichhen.thucung.ten }}</p>
          <p><strong>Cân nặng:</strong> {{ hoaDon.idlichhen.thucung.cannang }} kg</p>
          <p><strong>Tuổi:</strong> {{ hoaDon.idlichhen.thucung.tuoi }} tuổi</p>
          <p><strong>Giống:</strong> {{ hoaDon.idlichhen.thucung.giong }}</p>
          <p><strong>Giới tính:</strong> {{ hoaDon.idlichhen.thucung.gioitinh ? 'Đực' : 'Cái' }}</p>
          <p><strong>Loại:</strong> {{ hoaDon.idlichhen.thucung.cophaimeokhong ? 'Mèo' : 'Chó' }}</p>
          <p><strong>Tình trạng sức khỏe:</strong> {{ hoaDon.idlichhen.thucung.tinhtrangsuckhoe }}</p>
          <p><strong>Mô tả:</strong> {{ hoaDon.idlichhen.thucung.mota }}</p>
        </div>
        <div class="col-md-6 info-section">
          <h3 class="section-title">Thông tin tùy chọn cân nặng</h3>
          <p><strong>ID:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.id }}</p>
          <p><strong>Cân nặng tối thiểu:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.cannangmin }} kg</p>
          <p><strong>Cân nặng tối đa:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.cannangmax ? `${hoaDon.idlichhen.tuyChonCanNang.cannangmax} kg` : 'Không giới hạn' }}</p>
          <p><strong>Giá tiền:</strong> {{ formatCurrency(hoaDon.idlichhen.tuyChonCanNang.giatien) }}</p>
          <p><strong>Trạng thái:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.trangthai ? 'Hoạt động' : 'Không hoạt động' }}</p>
        </div>
      </div>

      <div class="row mt-4">
        <div class="col-md-6">
          <button @click="inHoaDon(hoaDon.id)" class="btn btn-primary w-100">In hóa đơn</button>
        </div>
      </div>
    </div>
    <div v-else class="text-center">
      <p>Không tìm thấy thông tin hóa đơn.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuanLyHoaDonStore } from '~/stores/QuanLyHoaDon';
import Hoadon from "~/models/Hoadon";

const route = useRoute();
const router = useRouter();
const store = useQuanLyHoaDonStore();
const hoaDon = ref<Hoadon | null>(null);
const loading = ref(true);

const getTrangThaiLichHen = (trangthai: number) => {
  switch (trangthai) {
    case 0:
      return 'Chờ xác nhận';
    case 1:
      return 'Đã xác nhận';
    case 2:
      return 'Đã hủy';
    default:
      return 'Không xác định';
  }
};

const fetchHoaDon = async (id: number) => {
  try {
    loading.value = true;
    await store.fetchListHoaDon();
    hoaDon.value = store.ListHoaDon.find((item: Hoadon) => item.idlichhen.id === id) || null;
    if (!hoaDon.value) {
      console.error('Không tìm thấy hóa đơn với ID:', id);
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin hóa đơn:', error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${day}/${month}/${year} ${hours}:${minutes}`;
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

const getTrangThai = (trangthai: number) => {
  switch (trangthai) {
    case 1:
      return 'Chờ thanh toán';
    case 2:
      return 'Thành công';
    case 3:
      return 'Thất bại';
    default:
      return 'Không xác định';
  }
};


onMounted(() => {
  const id = Number(route.params.id);
  fetchHoaDon(id);
});

function inHoaDon(id: string) {
  store.inHoaDon(id);
}
</script>


<style scoped>

</style>