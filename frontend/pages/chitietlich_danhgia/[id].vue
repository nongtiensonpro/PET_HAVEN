<template>
  <div class="container py-5">
    <div class="row">
      <div class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div v-if="thayDoiLichHenStore.lichHenDetails">
              <!-- Thông tin lịch hẹn -->
              <div class="mb-4">

                <h5 class="text-primary"><i class="fas fa-calendar-alt me-2"></i>{{t('appointment_information')}}</h5>
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>ID:</strong> {{ thayDoiLichHenStore.lichHenDetails.id }}</p>
                    <p><strong>{{t('appointment_date')}}:</strong> {{ formatDate(thayDoiLichHenStore.getDate) }}</p>
                    <p><strong>{{t('timeSlotLabel')}} </strong> {{ thayDoiLichHenStore.getCaLichHen.thoigianca }}</p>
                    <p><strong>{{t('email_of_the_booker')}}</strong> {{ thayDoiLichHenStore.lichHenDetails.emailNguoiDat }}</p>
                  </div>
                </div>
              </div>

              <!-- Thông tin dịch vụ -->
              <div class="mb-4">
                <h5 class="text-primary"><i class="fas fa-clipboard-list me-2"></i>{{t('selectedService')}}</h5>
                <div class="row">
                  <div class="col-md-12">

                    <p><strong>{{t('serviceName')}}:</strong> {{ selectedService?.tendichvu }}</p>
                    <p><strong>{{t('serviceDescription')}}:</strong> {{ selectedService?.mota }}</p>
                    <p><strong>{{t('serviceOption')}}:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.giatien }} USD</p>
                    <p><strong>{{t('minimum_weight')}}:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmin }} kg</p>
                    <p><strong>{{t('maximum_weight')}}:</strong> {{ thayDoiLichHenStore.lichHenDetails.tuyChonCanNang?.cannangmax || 'Không giới hạn' }} kg</p>
                    <div>
                      <button type="button" class="custom-button" @click="chiTietDichVu(selectedService?.id)">Chi tiết</button>
                    </div>
                  </div>
                </div>
              </div>
              <!-- Thông tin thú cưng -->
              <div class="mb-4">
                <h5 class="text-primary"><i class="fas fa-paw me-2"></i>Thông tin thú cưng</h5>
                <div class="row">
                  <div class="col-md-6">
                    <p><strong>{{t('name')}}:</strong> {{ thayDoiLichHenStore.getThuCung.ten }}</p>
                    <p><strong>{{t('age')}}:</strong> {{ thayDoiLichHenStore.getThuCung.tuoi }}</p>
                    <p><strong>{{t('breed')}}:</strong> {{ thayDoiLichHenStore.getThuCung.giong }}</p>
                    <p><strong>{{t('weight')}}:</strong> {{ thayDoiLichHenStore.getThuCung.cannang }} kg</p>
                  </div>
                  <div class="col-md-6">
                    <p><strong>{{t('gender')}}:</strong> {{ thayDoiLichHenStore.getThuCung.gioitinh ? t('male') : t('female') }}</p>
                    <p><strong>{{t('petType')}}:</strong> {{ thayDoiLichHenStore.getThuCung.cophaimeokhong ? t('cat') : t('dog') }}</p>
                    <p><strong>{{t('healthStatus')}}:</strong> {{ thayDoiLichHenStore.getThuCung.tinhtrangsuckhoe }}</p>
                    <p v-if="thayDoiLichHenStore.getThuCung.mota"><strong>{{t('description')}}:</strong> {{ thayDoiLichHenStore.getThuCung.mota }}</p>
                  </div>
                </div>
              </div>

              <!-- Thông tin khách hàng -->
              <div>
                <h5 class="text-primary"><i class="fas fa-user me-2"></i>{{t('customerInfo')}}</h5>
                <p><strong>ID:</strong> {{ thayDoiLichHenStore.lichHenDetails.idkhachhang }}</p>
                <p><strong>Email:</strong> {{ thayDoiLichHenStore.lichHenDetails.emailNguoiDat }}</p>
              </div>
            </div>
            <div v-else class="text-center">
              <img src="/assets/image/cat3.jpg" alt="Chọn dịch vụ" class="img-fluid rounded mb-3"
                   style="max-height: 200px;">
              <p class="lead">{{t('loading')}}...</p>
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
                  :danhGia="convertToDanhGia(chiTietDanhGia)"/>
            </div>
          </div>
          <div v-else>
            <ThemDanhGia :idLichHen="String(thayDoiLichHenStore.lichHenDetails.id)"/>
            <p class="text-center text fs-4">{{t('noReview')}}</p>
          </div>
        </div>
      </div>
      <div v-else class="col">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title text-center text fs-4">{{t('useService')}}</h4>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useRoute} from 'vue-router'
import {ref, onMounted, computed} from 'vue';
import {useThayDoiLichHenStore} from '~/stores/ThayDoiLichHen'
import {useDanhGiaStore} from '~/stores/DanhGiaStores';
import type {ChiTietDanhGia} from '~/models/ChiTietDanhGia';
import CapNhatDanhGia from "~/components/CapNhatDanhGia.vue";

import {useI18n} from 'vue-i18n';

const {t} = useI18n();

const thayDoiLichHenStore = useThayDoiLichHenStore()
const danhGiaStore = useDanhGiaStore()
const chiTietDanhGia = ref<ChiTietDanhGia | null>(null);
import type DanhGia from '~/models/DanhGia';
import type LichHenDetails from "~/models/LichHenDetails";
import DichVu from "~/models/DichVu";
import {useServiceStore} from "~/stores/DichVuStores";

const route = useRoute()
const id = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id;
definePageMeta({
  middleware: ['auth']
})
const listServices = ref<DichVu[]>([]);
const serviceStore = useServiceStore();
const lichHen = ref<LichHenDetails | null>(null);

onMounted(async () => {
  await thayDoiLichHenStore.fetchLichHenDetails(Number(id))
  lichHen.value = thayDoiLichHenStore.lichHenDetails || null;
  await hienThiDanhGia(id)
  await serviceStore.fetchServices();
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

const filteredService = computed(() => {
  if (!thayDoiLichHenStore.lichHenDetails) return null;
  const tuyChonCanNangId = thayDoiLichHenStore.lichHenDetails.id;
  if (!tuyChonCanNangId) return null;
  return listServices.value.find(dichVu =>
      dichVu.tuyChonDichVus.some(tuyChon =>
          tuyChon.tuyChonCanNangs.some(canNang => canNang.id === tuyChonCanNangId)
      )
  );
});

const selectedService = computed(() => {
  if (!thayDoiLichHenStore.lichHenDetails?.tuyChonCanNang) return null;
  return serviceStore.services.find(service => 
    service.tuyChonDichVus.some(option => 
      option.tuyChonCanNangs.some(weight => 
        weight.id === thayDoiLichHenStore.lichHenDetails?.tuyChonCanNang?.id
      )
    )
  );
});

const selectedServiceOption = computed(() => {
  if (!thayDoiLichHenStore.lichHenDetails?.tuyChonCanNang || !selectedService.value) return null;
  return selectedService.value.tuyChonDichVus.find(option => 
    option.tuyChonCanNangs.some(weight => 
      weight.id === thayDoiLichHenStore.lichHenDetails?.tuyChonCanNang?.id
    )
  );
});
</script>

<style scoped>

</style>