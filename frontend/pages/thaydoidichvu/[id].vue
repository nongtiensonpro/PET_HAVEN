<template>
  <div class="card">
    <div class="card-body">
      <h2 class="card-header">{{ t('services_in_use') }}</h2>
      <div class="card-body">
        <div class="card-body" v-if="currentHoaDon && filteredService">
          <div class="row">
            <div class="col">
              <h3 class="mt-4">{{ t('selectedService') }} :</h3>
              <p>{{ t('serviceName') }}: {{ filteredService.tendichvu }}</p>
              <p>{{ t('serviceDescription') }}: {{ filteredService.mota }}</p>
            </div>
            <div class="col">
              <h3 class="mt-4">{{ t('serviceOptions') }}</h3>
              <p>{{ t('serviceOption') }}: {{ filteredOption?.tentuychon }}</p>
              <div v-html="filteredOption?.mota"></div>
            </div>
            <div class="col">
              <h3 class="mt-4">{{ t('weightOptions') }}</h3>
              <p>ID: {{ currentHoaDon.idlichhen.tuyChonCanNang.id }}</p>
              <p>{{ t('weight') }}: {{ currentHoaDon.idlichhen.tuyChonCanNang.cannangmin }} kg -
                {{ currentHoaDon.idlichhen.tuyChonCanNang.cannangmax ? currentHoaDon.idlichhen.tuyChonCanNang.cannangmax
                  + ' kg' : 'Không giới hạn' }}</p>
              <p>Giá tiền: {{ currentHoaDon.idlichhen.tuyChonCanNang.giatien }} USD</p>
            </div>
          </div>

        </div>
      </div>
    </div>
    <div class="card-body">
      <h2 class="card-header mb-3">{{ t('chooseService') }}</h2>
      <div v-if="isLoading" class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>
      <div v-else class="service-buttons-container">
        <button v-for="dichVu in listDichVu" :key="dichVu.id" type="button" class="custom-button"
          :class="{ 'selected': isServiceSelected(dichVu) }" @click="selectService(dichVu)">
          <img v-if="dichVu.anh" :src="dichVu.anh" :alt="dichVu.tendichvu" class="service-image">
          <img v-else="dichVu.anh" src="~/assets/image/LogoPetHaven.png" :alt="dichVu.tendichvu" class="service-image">
          <span>{{ dichVu.tendichvu }}</span>
        </button>
      </div>
    </div>
    <div v-if="selectedService" class="card-body">
      <h3>{{ selectedService.tendichvu }}</h3>
      <p>{{ selectedService.mota }}</p>
      <template v-if="hasTuyChonDichVus">
        <div class="mt-3">
          <h4>{{ t('serviceOptions') }}</h4>
          <div class="d-flex flex-wrap">
            <button v-for="option in selectedService.tuyChonDichVus" :key="option.id" class="custom-button me-2 mb-2"
              :class="{ 'selected': isTuyChonDichVuSelected(option) }" @click="selectTuyChonDichVu(option)">
              {{ option.tentuychon }}
            </button>
          </div>
        </div>
      </template>
      <template v-if="hasTuyChonCanNangs">
        <div class="mt-3">
          <h4>{{ t('weightOptions') }}</h4>
          <div class="d-flex flex-wrap">
            <button v-for="option in selectedTuyChonDichVu.tuyChonCanNangs" :key="option.id"
              class="custom-button me-2 mb-2" :class="{ 'selected': isTuyChonCanNangSelected(option) }"
              @click="selectTuyChonCanNang(option)">
              {{ formatCanNang(option) }} - {{ option.giatien }}USD
            </button>
          </div>
        </div>
      </template>
      <div v-if="selectedTuyChonDichVu" class="mt-3">
        <h4>{{ t('serviceDetails') }}</h4>
        <div v-html="selectedTuyChonDichVu.mota"></div>
      </div>
      <div>
        <h4>{{ t('note') }}</h4>
        <textarea v-model="ghichu" cols="30" rows="10" :placeholder="t('enter_note')"></textarea>
      </div>

      <div class="card mt-4" :disabled="!canConfirm"
        v-if="currentHoaDon?.idlichhen?.tuyChonCanNang?.giatien && selectedTuyChonCanNang?.giatien">
        <div class="card-header text fs-4">
          {{ t('price_change_info') }}
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-4">
              <p class="mb-2">{{ t('initial_price') }}: {{ formatPrice(currentHoaDon.idlichhen.tuyChonCanNang.giatien)
                }}</p>
            </div>
            <div class="col-md-4">
              <p class="mb-2">{{ t('new_price') }}: {{ formatPrice(selectedTuyChonCanNang.giatien) }}</p>
            </div>
            <div class="col-md-4">
              <p class="mb-2" :class="priceChangeClass">
                {{ t('price_difference') }}: {{ formatPriceDifference(currentHoaDon.idlichhen.tuyChonCanNang.giatien,
                selectedTuyChonCanNang.giatien) }}
              </p>
            </div>
            <div class="col-12 mt-3">
              <div v-if="priceDifference === 0" class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>
                {{ t('price_no_change_message') }}
                <button @click="doiDichVu" class="custom-button mt-3" :disabled="!canConfirm || isLoading">
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  {{ t('confirm') }}
                </button>
              </div>
              <div v-else-if="priceDifference > 0" class="alert alert-success">
                <i class="fas fa-check-circle me-2"></i>
                {{ t('price_decrease_message') }}
                <button @click="doiDichVu" class="custom-button mt-3" :disabled="!canConfirm || isLoading">
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  {{ t('confirm') }}
                </button>
              </div>
              <div v-else class="alert alert-warning">
                <i class="fas fa-exclamation-triangle me-2"></i>
                {{ t('price_increase_message') }}
                <button @click="doiDichVu" class="custom-button mt-3" :disabled="!canConfirm || isLoading">
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  {{ t('confirm') }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import DichVu from "~/models/DichVu";
import { useServiceStore } from "~/stores/DichVuStores"
import { useI18n } from 'vue-i18n'
import HoaDonKhachHang from "~/models/HoaDonKhachHang";
import { useCheckInStore } from '~/stores/CheckInStores'
import Swal from "sweetalert2";
import { useDoiDichVuStores } from "~/stores/DoiDichVuStores";

const isLoading = ref(false);
const error = ref('');

const doiDichVuKH = useDoiDichVuStores();
const checkInStore = useCheckInStore();
const hoaDonList = ref<HoaDonKhachHang[]>([]);
const toast = useToast();
const { t } = useI18n()
const serviceStore = useServiceStore();
const listDichVu = ref<DichVu[]>(serviceStore.services);
const selectedService = ref<DichVu | null>(null);
const selectedTuyChonDichVu = ref<any>(null);
const selectedTuyChonCanNang = ref<any>(null);

onMounted(async () => {
  try {
    isLoading.value = true;
    await checkInStore.fetchHoaDon();
    await checkInStore.fetchHoaDonDaThanhToan();
    hoaDonList.value = checkInStore.ListHoaDon;
  } catch (err) {
    error.value = t('error_loading_data');
    console.error('Error loading data:', err);
  } finally {
    isLoading.value = false;
  }
});

const currentHoaDon = computed(() => {
  if (!hoaDonList.value || !id) return null;
  return hoaDonList.value.find(hoaDon => hoaDon.idlichhen.id.toString() === id.toString());
});

const filteredService = computed(() => {
  const tuyChonCanNangId = currentHoaDon.value?.idlichhen.tuyChonCanNang.id;
  return listDichVu.value.find(dichVu =>
    dichVu.tuyChonDichVus.some(tuyChon =>
      tuyChon.tuyChonCanNangs.some(canNang => canNang.id === tuyChonCanNangId)
    )
  );
});

const filteredOption = computed(() => {
  const tuyChonCanNangId = currentHoaDon.value?.idlichhen.tuyChonCanNang.id;
  return filteredService.value?.tuyChonDichVus.find(tuyChon =>
    tuyChon.tuyChonCanNangs.some(canNang => canNang.id === tuyChonCanNangId)
  );
});

const hasTuyChonDichVus = computed(() =>
  selectedService.value?.tuyChonDichVus && selectedService.value.tuyChonDichVus.length > 0
);

const hasTuyChonCanNangs = computed(() =>
  selectedTuyChonDichVu.value && selectedTuyChonDichVu.value.tuyChonCanNangs
);

const canConfirm = computed(() =>
  selectedService.value && selectedTuyChonDichVu.value && selectedTuyChonCanNang.value
);

const isServiceSelected = (dichVu: DichVu) =>
  selectedService.value && selectedService.value.id === dichVu.id;

const isTuyChonDichVuSelected = (option: any) =>
  selectedTuyChonDichVu.value && selectedTuyChonDichVu.value.id === option.id;

const isTuyChonCanNangSelected = (option: any) =>
  selectedTuyChonCanNang.value && selectedTuyChonCanNang.value.id === option.id;

const selectService = (dichVu: DichVu) => {
  selectedService.value = dichVu;
  selectedTuyChonDichVu.value = null;
  selectedTuyChonCanNang.value = null;
};

const selectTuyChonDichVu = (option: any) => {
  selectedTuyChonDichVu.value = option;
  selectedTuyChonCanNang.value = null;
};

const selectTuyChonCanNang = (option: any) => {
  selectedTuyChonCanNang.value = option;
};

const formatCanNang = (option: any) => {
  if (option.cannangmin === 0) return `${t('under')} ${option.cannangmax}${t('kg')}`;
  if (option.cannangmax === null) return `${t('over')} ${option.cannangmin}${t('kg')}`;
  return `${option.cannangmin}-${option.cannangmax}${t('kg')}`;
};

const formatPrice = (price: number | null): string => {
  if (price === null || price === undefined) return '0 USD';
  return `${price} USD`;
};

const formatPriceDifference = (oldPrice: number | null, newPrice: number | null): string => {
  if (oldPrice === null || newPrice === null) return '0 USD';
  const difference = newPrice - oldPrice;
  const sign = difference > 0 ? '+' : '';
  return `${sign}${difference} USD`;
};

const priceChangeClass = computed(() => {
  if (!currentHoaDon.value?.idlichhen?.tuyChonCanNang?.giatien || !selectedTuyChonCanNang.value?.giatien) {
    return '';
  }
  const difference = selectedTuyChonCanNang.value.giatien - currentHoaDon.value.idlichhen.tuyChonCanNang.giatien;
  return {
    'text-success': difference < 0,
    'text-danger': difference > 0
  };
});

const priceDifference = computed(() => {
  if (!currentHoaDon.value?.idlichhen?.tuyChonCanNang?.giatien || !selectedTuyChonCanNang.value?.giatien) {
    return 0;
  }
  return currentHoaDon.value.idlichhen.tuyChonCanNang.giatien - selectedTuyChonCanNang.value.giatien;
});

const route = useRoute();
const id = route.params.id;
const ghichu = ref('');

const doiDichVu = async () => {
  if (!selectedService.value || !selectedTuyChonCanNang.value || !id || !currentHoaDon.value) {
    toast.error(t('please_select_all_required_fields'));
    return;
  }

  try {
    const result = await Swal.fire({
      title: t('are_you_sure_you_want_to_change'),
      text: `${filteredService.value?.tendichvu || ''} ----> ${selectedService.value?.tendichvu || ''}`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: t('yes'),
      cancelButtonText: t('no')
    });

    if (result.isConfirmed) {
      isLoading.value = true;

      try {
        await doiDichVuKhachHang();
        if (priceDifference.value > 0) {
          // Giá mới thấp hơn giá cũ
          await doiDichVuKH.thanhToanDichVuKhiSoTienThayDoiBangHoacNhoHon(currentHoaDon.value.idlichhen.id);
        } else if (priceDifference.value < 0) {
          // Giá mới cao hơn giá cũ
          await doiDichVuKH.thanhToanDichVuKhiSoTienThayDoiLonHon(currentHoaDon.value.idlichhen.id);
        }

      } catch (error) {
        console.error('Error:', error);
        toast.error(t('connection_error'));
      }
    }
  } catch (error) {
    console.error('Error:', error);
    toast.error(t('error_processing_request'));
  } finally {
    isLoading.value = false;
  }
};

async function doiDichVuKhachHang() {
  if (!selectedTuyChonCanNang.value?.id || !id) {
    toast.error(t('invalid_data'));
    return;
  }

  const data = {
    idLichHen: id,
    idTuyChonCanNang: selectedTuyChonCanNang.value.id,
    ghiChu: ghichu.value.trim()
  };

  const token = localStorage.getItem('access_token');
  if (!token) {
    toast.error(t('please_login'));
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/api/hoa-don/doi-dich-vu", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });

    if (response.ok) {
      const result = await response.json();
      console.log("Dữ liệu trả về từ server:", result);
      toast.success(t('service_changed_successfully'));
      return navigateTo('/nhanvien/checkin');
    } else {
      const errorData = await response.json().catch(() => null);
      toast.error(errorData?.message || t('error_changing_service'));
    }
  } catch (error) {
    console.error('Error:', error);
    toast.error(t('connection_error'));
    throw error;
  }
}
</script>

<style scoped>
.service-buttons-container {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  padding-bottom: 10px;
}

.custom-button {
  flex: 0 0 auto;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #400D01;
  background-color: white;
  color: #400D01;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  margin-right: 10px;
}

.custom-button:hover,
.custom-button.selected {
  background-color: #400D01;
  color: white;
}

.service-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 5px;
}

.custom-button:disabled {
  background-color: #8c6b63;
  border-color: #8c6b63;
}
</style>