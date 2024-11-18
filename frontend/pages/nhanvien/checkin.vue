<template>
  <div class="container">
    <div class="container">
      <div class="row">
        <div class="col-6">
          <div class="card">
            <div class="text-center text fs-3">
              Các hóa đơn chưa thanh toán
              <button @click="fetchHoaDon" class="btn btn-primary">Làm mới</button>
            </div>
            <div class="card-body">
              <div class="container">
                <div class="row">
                  <div class="col">
                    <table class="table">
                      <thead>
                      <tr>
                        <th>ID Hóa Đơn</th>
                        <th>ID Lịch Hẹn</th>
                        <th>Email KH</th>
                        <th>Tên Thú Cưng</th>
                        <th>Dịch Vụ</th>
                        <th>Ngày Hẹn</th>
                        <th>Số Tiền</th>
                        <th>Trạng Thái</th>
                        <th>Thao Tác</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr v-for="hoaDon in hoaDonList" :key="hoaDon.id">
                        <td>{{ hoaDon.id }}</td>
                        <td>{{ hoaDon.idlichhen.id }}</td>
                        <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                        <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                        <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                        <td>{{ formatDate(hoaDon.date) }}</td>
                        <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                        <td>{{ getTrangThai(hoaDon.trangthai) }}</td>
                        <td>
                          <button @click="handlePayment(hoaDon.id)" class="btn btn-sm btn-success">Check In và Thanh Toán</button>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
        <div class="col-6">
          <div class="card">
            <div class="text-center text fs-3">
              Các hóa đơn đã thanh toán
              <button @click="fetchHoaDon" class="btn btn-primary">Làm mới</button>
            </div>
            <div class="card-body">
              <h4 class="card-title">Title</h4>
              <p class="card-text">Text</p>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { useCheckInStore } from '~/stores/CheckInStores'
import { ref } from "vue";

const checkInStore = useCheckInStore()

const hoaDonList = ref([]);
const fetchHoaDon = async () => {
  await checkInStore.fetchHoaDon();
  hoaDonList.value = checkInStore.ListHoaDon;
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

const getTrangThai = (status) => {
  switch(status) {
    case 1: return 'Chưa thanh toán';
    case 2: return 'Đã thanh toán';
    default: return 'Không xác định';
  }
};

const handlePayment = (id : String) => {
  checkInStore.checkIn(id);
  fetchHoaDon();
};
</script>

<style scoped>
/* Add any necessary styles here */
</style>