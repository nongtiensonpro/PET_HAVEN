<template>
  <div class="container mt-4">
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải thông tin hóa đơn...</span>
      </div>
    </div>
    <div v-else>
      <h1 class="mb-4 text-center">Chi tiết hóa đơn #{{ hoaDon.id }}</h1>

      <div class="row g-4">
        <div class="col-md-6">
          <section class="info-section">
            <h3 class="section-title">Thông tin khách hàng</h3>
            <p><strong>Email:</strong> {{ hoaDon.idlichhen.emailNguoiDat }}</p>
            <p><strong>ID Khách hàng:</strong> {{ hoaDon.idlichhen.idkhachhang }}</p>
          </section>

          <section class="info-section">
            <h3 class="section-title">Thông tin hóa đơn</h3>
            <p><strong>Ngày đặt:</strong> {{ formatDate(hoaDon.date) }}</p>
            <p><strong>Số tiền đầu:</strong> {{ hoaDon.sotienbandau }} USD</p>
            <p><strong>Số tiền cuối:</strong> {{ hoaDon.sotien }} USD</p>
            <p><strong>Ngày thanh toán:</strong> {{ formatDate(hoaDon.ngaythanhtoan) }}</p>
            <p><strong>Phương thức thanh toán:</strong> {{ hoaDon.phuongthucthanhtoan }}</p>
            <p><strong>Trạng thái:</strong> {{ getTrangThai(hoaDon.trangthai) }}</p>
            <p><strong>Người thanh toán:</strong> {{ hoaDon.nguoithanhtoan }}</p>
            <p><strong>Mã giao dịch:</strong> {{ hoaDon.magiaodich }}</p>
          </section>

          <section class="info-section">
            <h3 class="section-title">Thông tin lịch hẹn</h3>
            <p><strong>ID Lịch hẹn:</strong> {{ hoaDon.idlichhen.id }}</p>
            <p><strong>Ngày hẹn:</strong> {{ formatDate(hoaDon.idlichhen.date) }}</p>
            <p><strong>Trạng thái lịch hẹn:</strong> {{ getTrangThaiLichHen(hoaDon.idlichhen.trangthai) }}</p>
            <p><strong>Trạng thái ca:</strong> {{ hoaDon.idlichhen.trangthaica ? 'Đã đặt' : 'Chưa đặt' }}</p>
            <p><strong>Số lần thay đổi:</strong> {{ hoaDon.idlichhen.solanthaydoi }}</p>
            <p><strong>Số lần nhắc nhở:</strong> {{ hoaDon.idlichhen.solannhacnho }}</p>
          </section>
        </div>

        <div class="col-md-6">
          <section class="info-section">
            <h3 class="section-title">Thông tin thú cưng</h3>
            <p><strong>Tên:</strong> {{ hoaDon.idlichhen.thucung.ten }}</p>
            <p><strong>Giống:</strong> {{ hoaDon.idlichhen.thucung.giong }}</p>
            <p><strong>Cân nặng:</strong> {{ hoaDon.idlichhen.thucung.cannang }} kg</p>
            <p><strong>Tuổi:</strong> {{ hoaDon.idlichhen.thucung.tuoi }} năm</p>
            <p><strong>ID:</strong> {{ hoaDon.idlichhen.thucung.id }}</p>
          </section>

          <section class="info-section">
            <h3 class="section-title">Thông tin dịch vụ</h3>
            <p><strong>Tên dịch vụ:</strong> {{ hoaDon.idlichhen.dichvu.tendichvu }}</p>
            <p><strong>Mô tả:</strong> {{ hoaDon.idlichhen.dichvu.mota }}</p>
            <p><strong>Giá tiền:</strong> {{ hoaDon.idlichhen.dichvu.giatien }} VND</p>
            <p><strong>Trạng thái dịch vụ:</strong> {{ hoaDon.idlichhen.dichvu.trangthai ? 'Hoạt động' : 'Không hoạt động' }}</p>
          </section>

          <section class="info-section">
            <h3 class="section-title">Thông tin ca lịch hẹn</h3>
            <p><strong>Tên ca:</strong> {{ hoaDon.idlichhen.idcalichhen.tenca }}</p>
            <p><strong>Thời gian ca:</strong> {{ hoaDon.idlichhen.idcalichhen.thoigianca }}</p>
            <p><strong>Trạng thái ca:</strong> {{ hoaDon.idlichhen.idcalichhen.trangthai ? 'Hoạt động' : 'Không hoạt động' }}</p>
          </section>

          <section class="info-section">
            <h3 class="section-title">Thông tin giảm giá</h3>
            <p><strong>Phần trăm giảm:</strong> {{ hoaDon.idgiamgia.phantramgiam }}%</p>
            <p><strong>Ngày bắt đầu:</strong> {{ formatDate(hoaDon.idgiamgia.ngaybatdau) }}</p>
            <p><strong>Ngày kết thúc:</strong> {{ formatDate(hoaDon.idgiamgia.ngayketthuc) }}</p>
            <p><strong>Mô tả:</strong> {{ hoaDon.idgiamgia.mota }}</p>
            <p><strong>Trạng thái:</strong> {{ hoaDon.idgiamgia.trangthai ? 'Đang áp dụng' : 'Không áp dụng' }}</p>
          </section>
        </div>
      </div>

      <div class="row mt-4">
        <div class="col-md-6">
          <button @click="inHoaDon(hoaDon.id)" class="btn btn-primary w-100">In hóa đơn</button>
        </div>
        <div class="col-md-6">
          <button @click="goBack" class="btn btn-secondary w-100">Quay lại</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useQuanLyHoaDonStore } from '~/stores/QuanLyHoaDon';

const route = useRoute();
const router = useRouter();
const store = useQuanLyHoaDonStore();
const hoaDon = ref(null);
const loading = ref(true);
const getTrangThaiLichHen = (trangthai: number) => {
  switch (trangthai) {
    case 0: return 'Chờ xác nhận';
    case 1: return 'Đã xác nhận';
    case 2: return 'Đã hủy';
    default: return 'Không xác định';
  }
};
const fetchHoaDon = async (id: number) => {
  await store.fetchListHoaDon();
  hoaDon.value = store.ListHoaDon.find(item => item.id === id);
  loading.value = false;
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

const getTrangThai = (trangthai: number) => {
  switch (trangthai) {
    case 1: return 'Chờ thanh toán';
    case 2: return 'Thành công';
    case 3: return 'Thất bại';
    default: return 'Không xác định';
  }
};

const goBack = () => {
  router.push('/admin/quanlyhoadon');
};

onMounted(() => {
  const id = Number(route.params.id);
  fetchHoaDon(id);
});

function inHoaDon(id : String) {
  store.inHoaDon(id);
}
</script>

<style scoped>
.container {
  max-width: 1000px;
  margin: 0 auto;
}

.info-section {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.section-title {
  color: #007bff;
  border-bottom: 2px solid #007bff;
  padding-bottom: 10px;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.btn {
  padding: 10px 20px;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .col-md-6 {
    margin-bottom: 20px;
  }
}
</style>