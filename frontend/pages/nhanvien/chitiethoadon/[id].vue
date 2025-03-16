<template>
  <div class="container bg-light mt-4 p-4">
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải thông tin hóa đơn...</span>
      </div>
    </div>
    <div v-else-if="hoaDon">
      <h2 class="mb-4">{{t('bill_details')}}</h2>
      <div class="row g-4">
        <!-- Thông tin hóa đơn -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{t('billing_information')}}</h3>
          <p><strong>{{t('invoice_code')}}:</strong> {{ hoaDon.id }}</p>
          <p><strong>{{t('date_of_establishment')}}:</strong> {{ formatDate(hoaDon.date) }}</p>
          <p><strong>{{t('status')}}:</strong> {{ getTrangThai(hoaDon.trangthai) }}</p>
          <p><strong>{{t('paymentMethod')}}:</strong> {{ hoaDon.phuongthucthanhtoan }}</p>
          <p><strong>{{t('transaction_code')}}:</strong> {{ hoaDon.magiaodich }}</p>
<!--          <p><strong>{{t('payer')}}:</strong> {{ hoaDon.nguoithanhtoan }}</p>-->
        </div>

        <!-- Thông tin thanh toán -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{t('billing_information')}}</h3>
          <p><strong>{{t('initial_amount')}}:</strong> {{ formatCurrency(hoaDon.sotienbandau) }}</p>
          <p><strong>{{t('payment_amount')}}:</strong> {{ formatCurrency(hoaDon.sotien) }}</p>
          <p><strong>{{t('payment_date')}}:</strong> {{ formatDate(hoaDon.ngaythanhtoan) }}</p>
        </div>

        <!-- Thông tin mã giảm giá -->
        <div v-if="hoaDon.idgiamgia" class="col-md-6 info-section">
          <h3 class="section-title">{{t('discount_code_information')}}</h3>
          <p><strong>{{t('discount_code')}}:</strong> {{ hoaDon.idgiamgia.id }}</p>
          <p><strong>{{t('percentage_off')}}:</strong> {{ hoaDon.idgiamgia.phantramgiam }}%</p>
          <p><strong>{{t('start_date')}}:</strong> {{ formatDate(hoaDon.idgiamgia.ngaybatdau) }}</p>
          <p><strong>{{t('end_date')}}:</strong> {{ formatDate(hoaDon.idgiamgia.ngayketthuc) }}</p>
          <p><strong>{{t('description')}}:</strong> {{ hoaDon.idgiamgia.mota }}</p>
          <p><strong>{{t('status')}}:</strong> {{ hoaDon.idgiamgia.trangthai ? 'Còn hiệu lực' : 'Hết hiệu lực' }}</p>
        </div>

        <!-- Thông tin lịch hẹn -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{t('appointment_information')}}</h3>
          <p><strong>{{t('appointment_code')}}:</strong> {{ hoaDon.idlichhen.id }}</p>
          <p><strong>{{t('customer_code')}}:</strong> {{ hoaDon.idlichhen.idkhachhang }}</p>
          <p><strong>{{t('appointment_date')}}:</strong> {{ formatDate(hoaDon.idlichhen.date) }}</p>
          <p><strong>{{t('appointment_status')}}:</strong> {{ getTrangThaiLichHen(hoaDon.idlichhen.trangthai) }}</p>
          <p><strong>{{t('email_of_the_booker')}}:</strong> {{ hoaDon.idlichhen.emailNguoiDat }}</p>
          <p><strong>{{t('timeLabel')}}:</strong> {{ hoaDon.idlichhen.idcalichhen.thoigianca }}</p>
          <p><strong>{{t('status')}}:</strong> {{ hoaDon.idlichhen.trangthaica ? 'Đã xác nhận' : 'Chưa xác nhận' }}</p>
<!--          <p><strong>{{t('number_of_changes')}}:</strong> {{ hoaDon.idlichhen.solanthaydoi }}</p>-->
<!--          <p><strong>{{t('number_of_reminders')}}:</strong> {{ hoaDon.idlichhen.solannhacnho }}</p>-->
        </div>

        <!-- Thông tin thú cưng -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{t('petInfo')}}</h3>
          <p><strong>ID:</strong> {{ hoaDon.idlichhen.thucung.id }}</p>
          <p><strong>{{t('petName')}}:</strong> {{ hoaDon.idlichhen.thucung.ten }}</p>
          <p><strong>{{t('weight')}}:</strong> {{ hoaDon.idlichhen.thucung.cannang }} kg</p>
          <p><strong>{{t('age')}}:</strong> {{ hoaDon.idlichhen.thucung.tuoi }} tuổi</p>
          <p><strong>{{t('breed')}}:</strong> {{ hoaDon.idlichhen.thucung.giong }}</p>
          <p><strong>{{t('gender')}}:</strong> {{ hoaDon.idlichhen.thucung.gioitinh ? 'Đực' : 'Cái' }}</p>
          <p><strong>{{t('petType')}}:</strong> {{ hoaDon.idlichhen.thucung.cophaimeokhong ? 'Mèo' : 'Chó' }}</p>
          <p><strong>{{t('healthStatus')}}:</strong> {{ hoaDon.idlichhen.thucung.tinhtrangsuckhoe }}</p>
          <p><strong>{{t('description')}}:</strong> {{ hoaDon.idlichhen.thucung.mota.includes('')?t('no_value'):hoaDon.idlichhen.thucung.mota }}</p>
        </div>

        <!-- Thông tin tùy chọn cân nặng -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{t('weightOptions')}}</h3>
          <p><strong>ID:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.id }}</p>
          <p><strong>{{t('minimum_weight')}}:</strong> {{ hoaDon.idlichhen.tuyChonCanNang.cannangmin }} kg</p>
          <p><strong>{{t('maximum_weight')}}:</strong> {{
            hoaDon.idlichhen.tuyChonCanNang.cannangmax ? `${hoaDon.idlichhen.tuyChonCanNang.cannangmax} kg` : t('unlimited')
          }}</p>
          <p><strong>{{t('price')}}:</strong> {{ formatCurrency(hoaDon.idlichhen.tuyChonCanNang.giatien) }}</p>
          <p><strong>{{t('status')}}:</strong> {{
            hoaDon.idlichhen.tuyChonCanNang.trangthai ? t('active') : t('inactive')
          }}</p>
        </div>

        <!-- Thông tin dịch vụ và tùy chọn -->
        <div class="col-md-6 info-section">
          <h3 class="section-title">{{ t('selectedService') }}</h3>
          <p><strong>{{ t('serviceName') }}:</strong> {{ filteredService.tendichvu }}</p>
          <p><strong>{{ t('serviceDescription') }}:</strong> {{ filteredService.mota }}</p>

          <h4 class="mt-3">{{ t('serviceOptions') }}</h4>
          <p><strong>{{ t('serviceOption') }}:</strong> {{ filteredOption?.tentuychon }}</p>
          <div v-html="filteredOption?.mota"></div>
        </div>
      </div>

      <!-- Nút in hóa đơn -->
      <div class="row mt-4">
        <div class="col-md-6">
          <button @click="inHoaDon(hoaDon.id)" class="btn btn-primary w-100">{{t('print_invoice')}}</button>
        </div>
      </div>
    </div>
    <div v-else class="text-center">
      <p>{{t('invoice_information_not_found')}}.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useQuanLyHoaDonStore} from '~/stores/QuanLyHoaDon';
import Hoadon from "~/models/Hoadon";
import {useServiceStore} from '~/stores/DichVuStores';
import DichVu from "~/models/DichVu";
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const serviceStore = useServiceStore();
const route = useRoute();
const store = useQuanLyHoaDonStore();
const hoaDon = ref<Hoadon | null>(null);
const listServices = ref<DichVu[]>([]);
const loading = ref(true);


const filteredService = computed(() => {
  if (!hoaDon.value) return null;
  const tuyChonCanNangId = hoaDon.value.idlichhen.tuyChonCanNang?.id;
  if (!tuyChonCanNangId) return null;
  return listServices.value.find(dichVu =>
      dichVu.tuyChonDichVus.some(tuyChon =>
          tuyChon.tuyChonCanNangs.some(canNang => canNang.id === tuyChonCanNangId)
      )
  );
});

const filteredOption = computed(() => {
  if (!hoaDon.value) return null;
  const tuyChonCanNangId = hoaDon.value.idlichhen.tuyChonCanNang?.id;
  if (!tuyChonCanNangId) return null;
  return listServices.value.flatMap(dichVu =>
    dichVu.tuyChonDichVus.find(tuyChon =>
      tuyChon.tuyChonCanNangs.some(canNang => canNang.id === tuyChonCanNangId)
    )
  ).filter(Boolean)[0] || null;
});


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
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'USD'}).format(amount);
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
  serviceStore.fetchServices();
  listServices.value = serviceStore.services;
});

function inHoaDon(id: number) {
  store.inHoaDon(id);
}
</script>


<style scoped>

</style>