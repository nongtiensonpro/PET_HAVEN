<script setup lang="ts">
import { ref } from 'vue';
import { useServiceStore } from '~/stores/DichVuStores';
import DichVu from '~/models/DichVu';
import { useToast } from 'vue-toastification';
import {useI18n} from "vue-i18n";

const toast = useToast();
const serviceStore = useServiceStore();
const { t } = useI18n();
const newService = ref<DichVu>({
  id: 0,
  tendichvu: '',
  mota: '',
  trangthai: true,
  hien: true,
  anh: '',
  tuyChonDichVus: []
});
const addTuyChon = () => {
  newService.value.tuyChonDichVus.push({
    id: Date.now(),
    idDichVu: newService.value.id,
    tenTuyChon: '',
    moTa: null,
    trangThai: true,
    tuyChonCanNangs: []
  });
};

const addCanNang = (tuyChon: any) => {
  tuyChon.tuyChonCanNangs.push({
    canNangMin: 0,
    canNangMax: null,
    giaTien: 0,
    trangThai: true
  });
};
const { addDichVu } = serviceStore;

const selectedFile = ref<File | null>(null);

const isLoading = ref(false);
const countdown = ref(3);

const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) >= 0;
};

const saveNewService = async () => {
  // Kiểm tra và thu thập tất cả các lỗi
  const allErrors: string[] = [];

  // Validate service name
  if (!newService.value.tendichvu || newService.value.tendichvu.trim().length < 3) {
    allErrors.push(t('service_name_invalid'));
  }

  // Validate service options
  if (newService.value.tuyChonDichVus.length === 0) {
    allErrors.push(t('at_least_one_option_required'));
  }

  newService.value.tuyChonDichVus.forEach((tuyChon, index) => {
    // Validate option name
    if (!tuyChon.tenTuyChon || tuyChon.tenTuyChon.trim().length < 2) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('option_name_invalid')}`);
    }

    // Validate weight options
    if (tuyChon.tuyChonCanNangs.length === 0) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('at_least_one_weight_option_required')}`);
    }

    tuyChon.tuyChonCanNangs.forEach((canNang, canNangIndex) => {
      // Validate canNangMin and canNangMax are numeric
      if (!isNumeric(canNang.canNangMin)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_invalid')}`);
      }

      if (!isNumeric(canNang.canNangMax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_invalid')}`);
      }

      // Validate canNangMin and canNangMax are non-negative
      if (!isPositiveNumber(canNang.canNangMin)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_negative')}`);
      }

      if (!isPositiveNumber(canNang.canNangMax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_negative')}`);
      }

      // Validate maximum weight is greater than minimum weight
      if (parseFloat(canNang.canNangMin) >= parseFloat(canNang.canNangMax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('weight_range_invalid')}`);
      }

      // Validate price is numeric and non-negative
      if (!isNumeric(canNang.giaTien)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('price_invalid')}`);
      }

      if (!isPositiveNumber(canNang.giaTien)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('price_negative')}`);
      }
    });
  });

  // Validate file upload
  if (!selectedFile.value) {
    allErrors.push(t('image_required'));
  } else {
    const validImageTypes = ['image/jpeg', 'image/png', 'image/gif'];
    const maxSizeMB = 5;

    if (!validImageTypes.includes(selectedFile.value.type)) {
      allErrors.push(t('invalid_image_type'));
    }

    if (selectedFile.value.size > maxSizeMB * 1024 * 1024) {
      allErrors.push(t('image_too_large', { maxSize: maxSizeMB }));
    }
  }

  // Nếu có lỗi, hiển thị toast và không submit
  if (allErrors.length > 0) {
    // Hiển thị từng lỗi riêng biệt
    allErrors.forEach(error => {
      toast.error(error);
    });
    return;
  }

  try {
    isLoading.value = true;
    countdown.value = 10;

    const countdownInterval = setInterval(() => {
      countdown.value--;
    }, 1000);

    await addDichVu(newService.value, selectedFile.value || undefined);

    clearInterval(countdownInterval);
    isLoading.value = false;
    toast.success(t('new_service_added_successfully'));
    navigateTo('/admin/service/servicelist');
  } catch (error) {
    isLoading.value = false;
    toast.error(t('error_adding_new_service'));
  }
};



const removeTuyChon = (index: number) => {
  newService.value.tuyChonDichVus.splice(index, 1);
};

const removeCanNang = (tuyChon: any, index: number) => {
  tuyChon.tuyChonCanNangs.splice(index, 1);
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files?.length) return;
  selectedFile.value = target.files[0];
};

const fileInput = ref<HTMLInputElement | null>(null);

</script>

<template>
  <div class="container mt-4">
    <h1 class="mb-4">{{t('add_new_service')}}</h1>
    <div class="card">
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">{{t('serviceName')}}:</label>
          <input v-model="newService.tendichvu" class="form-control">
        </div>
        <div class="mb-3">
          <label class="form-label">{{t('serviceDescription')}}:</label>
          <textarea v-model="newService.mota" class="form-control"></textarea>
        </div>
        <div class="mb-3">
          <label class="form-label">{{t('image')}}:</label>
          <div class="d-flex align-items-center">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileUpload"
              accept="image/*"
              class="form-control"
              style="max-width: 300px;"
            >
            <button @click="fileInput?.click()" class="btn btn-outline-secondary ms-2">{{t('select')}} file</button>
          </div>
          <img
            v-if="newService.anh"
            :src="newService.anh"
            :alt="t('image')"
            class="img-fluid mt-2"
            style="max-width: 200px;"
          >
        </div>
        <h3 class="mt-4">{{t('serviceOptions')}}</h3>
        <div v-for="(tuyChon, index) in newService.tuyChonDichVus" :key="tuyChon.id" class="card mt-3">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h5 class="mb-0">{{t('options')}} {{ index + 1 }}</h5>
              <button @click="removeTuyChon(index)" class="btn btn-danger btn-sm">{{t('delete')}}</button>
            </div>
            <input v-model="tuyChon.tenTuyChon" class="form-control mb-2" :placeholder="t('options_name')">
            <textarea v-model="tuyChon.moTa" class="form-control mb-2" :placeholder="t('description')"></textarea>
            <h6 class="mt-3">{{t('weightOptions')}}</h6>
            <ul class="list-group">
              <li v-for="(canNang, canNangIndex) in tuyChon.tuyChonCanNangs" :key="canNang.id" class="list-group-item">
                <div class="row align-items-center">
                  <div class="col">
                    <input v-model="canNang.canNangMin" type="number" class="form-control" :placeholder="t('weight')+ 'min'">
                  </div>
                  <div class="col">
                    <input v-model="canNang.canNangMax" type="number" class="form-control" :placeholder="t('weight') + 'max'">
                  </div>
                  <div class="col">
                    <input v-model="canNang.giaTien" type="number" class="form-control" :placeholder="t('price')">
                  </div>
                  <div class="col">
                    <div class="form-check">
                      <input v-model="canNang.trangThai" type="checkbox" class="form-check-input" :id="'trangThaiCanNang' + canNang.id">
                      <label class="form-check-label" :for="'trangThaiCanNang' + canNang.id">{{t('active')}}</label>
                    </div>
                  </div>
                  <div class="col-auto">
                    <button @click="removeCanNang(tuyChon, canNangIndex)" class="btn btn-danger btn-sm">{{t('delete')}}</button>
                  </div>
                </div>
              </li>
            </ul>
            <button @click="addCanNang(tuyChon)" class="btn btn-secondary mt-2">{{t('addTimeSlot')}} {{t('weight')}}</button>
          </div>
        </div>
        <button @click="addTuyChon" class="btn btn-secondary mt-3">{{t('addTimeSlot')}} {{t('serviceOptions')}}</button>
        <div class="mt-4">
          <button @click="saveNewService" class="btn btn-success" :disabled="isLoading">
        <span v-if="isLoading">
          {{ t('processing') }} ({{ countdown }})
        </span>
            <span v-else>
          {{ t('save_new_service') }}
        </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>