<template>
  <div class="card">
    <div class="card-header">
      <h2 class="mb-3">Chọn Dịch Vụ</h2>
      <div class="service-buttons-container">
        <button v-for="dichVu in listDichVu"
                :key="dichVu.id"
                type="button"
                class="custom-button"
                :class="{ 'selected': isServiceSelected(dichVu) }"
                @click="selectService(dichVu)">
          <img v-if="dichVu.anh" :src="dichVu.anh" :alt="dichVu.tendichvu" class="service-image">
          <img v-else="dichVu.anh" src="../assets/image/LogoPetHaven.png" :alt="dichVu.tendichvu" class="service-image">
          <span>{{ dichVu.tendichvu }}</span>
        </button>
      </div>
    </div>
    <div v-if="selectedService" class="card-body">
      <h3>{{ selectedService.tendichvu }}</h3>
      <p>{{ selectedService.mota }}</p>
      <template v-if="hasTuyChonDichVus">
        <div class="mt-3">
          <h4>Tùy chọn dịch vụ:</h4>
          <div class="d-flex flex-wrap">
            <button v-for="option in selectedService.tuyChonDichVus"
                    :key="option.id"
                    class="custom-button me-2 mb-2"
                    :class="{ 'selected': isTuyChonDichVuSelected(option) }"
                    @click="selectTuyChonDichVu(option)">
              {{ option.tentuychon }}
            </button>
          </div>
        </div>
      </template>
      <template v-if="hasTuyChonCanNangs">
        <div class="mt-3">
          <h4>Tùy chọn cân nặng:</h4>
          <div class="d-flex flex-wrap">
            <button v-for="option in selectedTuyChonDichVu.tuyChonCanNangs"
                    :key="option.id"
                    class="custom-button me-2 mb-2"
                    :class="{ 'selected': isTuyChonCanNangSelected(option) }"
                    @click="selectTuyChonCanNang(option)">
              {{ formatCanNang(option) }} - {{ option.giatien }}USD
            </button>
          </div>
        </div>
      </template>
      <div v-if="selectedTuyChonDichVu" class="mt-3">
        <h4>Chi tiết dịch vụ:</h4>
        <div v-html="selectedTuyChonDichVu.mota"></div>
      </div>
      <button @click="confirmSelection" class="btn btn-primary mt-3" :disabled="!canConfirm">
        Xác nhận
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import DichVu from "~/models/DichVu";
import { useServiceStore } from "~/stores/DichVuStores"
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu'

const serviceStore = useServiceStore();
const mauKhachDatDichVu = useMauKhachDatDichVu();
const listDichVu = ref<DichVu[]>(serviceStore.services);
const selectedService = ref<DichVu | null>(null);
const selectedTuyChonDichVu = ref<any>(null);
const selectedTuyChonCanNang = ref<any>(null);
const emit = defineEmits(['serviceSelected']);
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
  if (option.cannangmin === 0) return `Dưới ${option.cannangmax}kg`;
  if (option.cannangmax === null) return `Trên ${option.cannangmin}kg`;
  return `${option.cannangmin}-${option.cannangmax}kg`;
};

const confirmSelection = () => {
  if (canConfirm.value) {
    const currentData = mauKhachDatDichVu.getTempData() || {};
    const updatedData = {
      ...currentData,
      dichvu: {
        id: selectedService.value!.id,
        tendichvu: selectedService.value!.tendichvu,
        mota: selectedService.value!.mota,
        anh: selectedService.value!.anh,
        trangthai: selectedService.value!.trangthai,
        hien: selectedService.value!.hien,
      },
      tuyChonDichVu: selectedTuyChonDichVu.value as TuyChonDichVu,
      tuyChonCanNang: selectedTuyChonCanNang.value as TuyChonCanNang,
    };

    mauKhachDatDichVu.saveTempData(updatedData);
    emit('serviceSelected', updatedData);
    console.log('Selection confirmed:', {
      service: selectedService.value?.tendichvu,
      option: selectedTuyChonDichVu.value?.tentuychon,
      weight: formatCanNang(selectedTuyChonCanNang.value),
      price: selectedTuyChonCanNang.value?.giatien
    });
  }
};
</script>

<style scoped>
.card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-header {
  background-color: #f8f9fa;
  padding: 20px;
}

.card-body {
  padding: 20px;
}

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

.custom-button:hover, .custom-button.selected {
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

h2, h3, h4 {
  color: #400D01;
}

.btn-primary {
  background-color: #400D01;
  border-color: #400D01;
}

.btn-primary:hover {
  background-color: #2c0901;
  border-color: #2c0901;
}

.btn-primary:disabled {
  background-color: #8c6b63;
  border-color: #8c6b63;
}
</style>