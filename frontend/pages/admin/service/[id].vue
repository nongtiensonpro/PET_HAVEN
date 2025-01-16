<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useServiceStore} from '~/stores/DichVuStores';
import DichVu from '~/models/DichVu';
import {useToast} from 'vue-toastification';
import {useI18n} from 'vue-i18n';

const {t} = useI18n();
const toast = useToast();
const route = useRoute();
const serviceStore = useServiceStore();
const id = ref<number | null>(null);
const editedService = ref<DichVu | null>(null);

const {updateService} = serviceStore;

const service = computed(() => {
  if (id.value === null) return null;
  return serviceStore.services.find(s => s.id === id.value) || null;
});

onMounted(async () => {
  const routeId = route.params.id;
  if (routeId && typeof routeId === 'string') {
    id.value = Number(routeId);
    if (serviceStore.services.length === 0) {
      await serviceStore.fetchServices();
    }
    if (service.value) {
      editedService.value = JSON.parse(JSON.stringify(service.value));
    }
  }
});
const isUpdating = ref(false);
const updateCounter = ref(0);
const updateInterval = ref<NodeJS.Timeout | null>(null);
const validationErrors = ref({
  tendichvu: '',
  mota: '',
  tuyChonDichVus: [] as string[],
  tuyChonCanNangs: [] as string[]
});

const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) >= 0;
};

const isFormValid = computed(() => {
  // Reset errors
  validationErrors.value = {
    tendichvu: '',
    mota: '',
    tuyChonDichVus: [],
    tuyChonCanNangs: []
  };

  // Validate service name
  if (!editedService.value?.tendichvu || editedService.value.tendichvu.trim().length < 3) {
    validationErrors.value.tendichvu = t('service_name_invalid');
  }

  // Validate service options
  editedService.value?.tuyChonDichVus.forEach((tuyChon, index) => {
    if (!tuyChon.tentuychon || tuyChon.tentuychon.trim().length < 2) {
      validationErrors.value.tuyChonDichVus[index] = t('option_name_invalid');
    }

    // Validate weight options
    tuyChon.tuyChonCanNangs.forEach((canNang, canNangIndex) => {
      // Validate cannangmin and cannangmax are numeric
      if (!isNumeric(canNang.cannangmin)) {
        validationErrors.value.tuyChonCanNangs[index] = t('minimum_weight_invalid');
      }

      if (!isNumeric(canNang.cannangmax)) {
        validationErrors.value.tuyChonCanNangs[index] = t('maximum_weight_invalid');
      }

      // Validate cannangmin and cannangmax are non-negative
      if (!isPositiveNumber(canNang.cannangmin)) {
        validationErrors.value.tuyChonCanNangs[index] = t('minimum_weight_negative');
      }

      if (!isPositiveNumber(canNang.cannangmax)) {
        validationErrors.value.tuyChonCanNangs[index] = t('maximum_weight_negative');
      }

      // Validate maximum weight is greater than minimum weight
      if (parseFloat(canNang.cannangmin) >= parseFloat(canNang.cannangmax)) {
        validationErrors.value.tuyChonCanNangs[index] = t('weight_range_invalid');
      }

      // Validate price is numeric and non-negative
      if (!isNumeric(canNang.giatien)) {
        validationErrors.value.tuyChonCanNangs[index] = t('price_invalid');
      }

      if (!isPositiveNumber(canNang.giatien)) {
        validationErrors.value.tuyChonCanNangs[index] = t('price_negative');
      }
    });
  });

  // Check if there are any errors
  return Object.values(validationErrors.value).every(errors => 
    Array.isArray(errors) ? errors.length === 0 : !errors
  );
});

const fileInput = ref<HTMLInputElement | null>(null);

const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const validImageTypes = ['image/jpeg', 'image/png', 'image/gif'];
    const maxSizeMB = 5;

    if (!validImageTypes.includes(file.type)) {
      toast.error(t('invalid_image_type'));
      input.value = ''; // Reset input
      return;
    }

    if (file.size > maxSizeMB * 1024 * 1024) {
      toast.error(t('image_too_large', { maxSize: maxSizeMB }));
      input.value = ''; // Reset input
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      if (editedService.value) {
        editedService.value.anh = e.target?.result as string;
      }
    };
    reader.readAsDataURL(file);
  }
};

const saveChanges = async () => {
  // Kiểm tra và thu thập tất cả các lỗi
  const allErrors: string[] = [];

  // Validate service name
  if (!editedService.value?.tendichvu || editedService.value.tendichvu.trim().length < 3) {
    allErrors.push(t('service_name_invalid'));
  }

  // Validate service options
  editedService.value?.tuyChonDichVus.forEach((tuyChon, index) => {
    if (!tuyChon.tentuychon || tuyChon.tentuychon.trim().length < 2) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('option_name_invalid')}`);
    }

    // Validate weight options
    tuyChon.tuyChonCanNangs.forEach((canNang, canNangIndex) => {
      // Validate cannangmin and cannangmax are numeric
      if (!isNumeric(canNang.cannangmin)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_invalid')}`);
      }

      if (!isNumeric(canNang.cannangmax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_invalid')}`);
      }

      // Validate cannangmin and cannangmax are non-negative
      if (!isPositiveNumber(canNang.cannangmin)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_negative')}`);
      }

      if (!isPositiveNumber(canNang.cannangmax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_negative')}`);
      }

      // Validate maximum weight is greater than minimum weight
      if (parseFloat(canNang.cannangmin) >= parseFloat(canNang.cannangmax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('weight_range_invalid')}`);
      }

      // Validate price is numeric and non-negative
      if (!isNumeric(canNang.giatien)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('price_invalid')}`);
      }

      if (!isPositiveNumber(canNang.giatien)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('price_negative')}`);
      }
    });
  });

  // Nếu có lỗi, hiển thị toast và không submit
  if (allErrors.length > 0) {
    // Hiển thị từng lỗi riêng biệt
    allErrors.forEach(error => {
      toast.error(error);
    });
    return;
  }

  if (editedService.value) {
    try {
      isUpdating.value = true;
      updateCounter.value = 0;
      updateInterval.value = setInterval(() => {
        updateCounter.value++;
      }, 1000);

      await updateService(editedService.value);

      clearInterval(updateInterval.value!);
      isUpdating.value = false;
      toast.success(t('service_update_successfully'));
      navigateTo('/admin/service/servicelist');
    } catch (error) {
      clearInterval(updateInterval.value!);
      isUpdating.value = false;
      toast.error(t('service_update_error'));
      console.error('Error updating service:', error);
    }
  }
};

const addTuyChon = () => {
  if (editedService.value) {
    editedService.value.tuyChonDichVus.push({
      tentuychon: '',
      mota: '',
      trangthai: true,
      tuyChonCanNangs: []
    });
  }
};

const addCanNang = (tuyChon: any) => {
  tuyChon.tuyChonCanNangs.push({
    cannangmin: 0,
    cannangmax: 0,
    giatien: 0,
    trangthai: true
  });
};

const removeTuyChon = (index: number) => {
  if (editedService.value) {
    editedService.value.tuyChonDichVus.splice(index, 1);
  }
};

const removeCanNang = (tuyChon: any, index: number) => {
  tuyChon.tuyChonCanNangs.splice(index, 1);
};

async function doiTrangThaiTuyChonDichVu(tuyChonId: number) {
  try {
    await serviceStore.doiTrangThaiTuyChonDichVu(tuyChonId);
    toast.success(t('status_updated_successfully'));

    if (editedService.value) {
      const tuyChon = editedService.value.tuyChonDichVus.find(tc => tc.id === tuyChonId);
      if (tuyChon) {
        tuyChon.trangthai = !tuyChon.trangthai;
      }
    }
  } catch (error) {
    toast.error(t('status_update_error'));
  }
}

async function doiTrangThaiTuyChonCanNang(tuyChonId: number, canNangId: number) {
  try {
    await serviceStore.doiTrangThaiTuyChonCanNang(canNangId);
    toast.success(t('status_updated_successfully'));


    if (editedService.value) {
      const tuyChon = editedService.value.tuyChonDichVus.find(tc => tc.id === tuyChonId);
      if (tuyChon) {
        const canNang = tuyChon.tuyChonCanNangs.find(cn => cn.id === canNangId);
        if (canNang) {
          canNang.trangthai = !canNang.trangthai;
        }
      }
    }

    editedService.value = JSON.parse(JSON.stringify(editedService.value));
  } catch (error) {
    console.error('Error updating weight option status:', error);
    toast.error(t('status_update_error'));
  }
}

</script>

<template>
  <div class="container mt-4" v-if="editedService">
    <h1 class="mb-4">{{ t('service_update') }}</h1>
    <form @submit.prevent="saveChanges" class="needs-validation" novalidate>
      <div class="mb-3">
        <label for="tendichvu" class="form-label">{{ t('serviceName') }}</label>
        <input
            type="text"
            class="form-control"
            id="tendichvu"
            v-model="editedService.tendichvu"
            required
        />
        <div v-if="validationErrors.tendichvu" class="invalid-feedback">
          {{ validationErrors.tendichvu }}
        </div>
      </div>

      <div class="mb-3">
        <label for="mota" class="form-label">{{ t('serviceDescription') }}</label>
        <textarea
            class="form-control"
            id="mota"
            v-model="editedService.mota"
            rows="3"
        ></textarea>
        <div v-if="validationErrors.mota" class="invalid-feedback">
          {{ validationErrors.mota }}
        </div>
      </div>

      <div class="mb-3">
        <label for="fileUpdate" class="form-label">{{ t('image') }}</label>
        <input
            type="file"
            class="form-control"
            id="fileUpdate"
            accept="image/*"
            @change="handleFileUpload"
        />
        <img v-if="editedService.anh" :src="editedService.anh" alt="Service Image">
      </div>

      <div class="mb-3">
        <label class="form-label">{{ t('status') }}</label>
        <div class="form-check">
          <input
              class="form-check-input"
              type="checkbox"
              id="trangthai"
              v-model="editedService.trangthai"
          />
          <label class="form-check-label" for="trangthai">
            {{ t('active') }}
          </label>
        </div>
      </div>

      <div class="mb-3">
        <h3>{{ t('serviceOptions') }}</h3>
        <button type="button" class="btn btn-success mb-3" @click="addTuyChon">
          {{ t('add_options') }}
        </button>

        <div v-for="(tuyChon, index) in editedService.tuyChonDichVus" :key="index" class="card mb-3">

          <div class="row text fs-3">
            <div class="col">
              {{ t('status') }} : {{ tuyChon.trangthai ? t('active') : t('inactive') }}
            </div>
            <div class="col">
              <button type="button" class="custom-button" @click="doiTrangThaiTuyChonDichVu(tuyChon.id)">Đổi trạng thái</button>
            </div>
          </div>
          <div class="card-body">
            <div class="mb-3">
              <label :for="'tentuychon-' + index" class="form-label">{{ t('options_name') }}</label>

              <input
                  type="text"
                  class="form-control"
                  :id="'tentuychon-' + index"
                  v-model="tuyChon.tentuychon"
                  required
              />
              <div v-if="validationErrors.tuyChonDichVus[index]" class="invalid-feedback">
                {{ validationErrors.tuyChonDichVus[index] }}
              </div>
            </div>

            <div class="mb-3">
              <label :for="'mota-tuychon-' + index" class="form-label">{{ t('option_description') }}</label>
              <textarea
                  class="form-control"
                  :id="'mota-tuychon-' + index"
                  v-model="tuyChon.mota"
                  rows="2"
              ></textarea>
            </div>

            <div class="mb-3">
              <h4>{{ t('weightOptions') }}</h4>
              <button type="button" class="btn btn-info mb-2" @click="addCanNang(tuyChon)">
                {{ t('add_weight') }}
              </button>

              <div v-for="(canNang, canNangIndex) in tuyChon.tuyChonCanNangs" :key="canNangIndex" class="card mb-2">
                <div class="row text fs-3">
                  <div class="col">
                    {{ t('status') }} : {{ canNang.trangthai ? t('active') : t('inactive') }}
                  </div>
                  <div class="col">
                    <button type="button" class="custom-button" @click="doiTrangThaiTuyChonCanNang(tuyChon.id, canNang.id)">Đổi trạng thái</button>
                  </div>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col">
                      <label :for="'cannangmin-' + index + '-' + canNangIndex"
                             class="form-label">{{ t('minimum_weight') }}</label>

                      <input
                          type="number"
                          class="form-control"
                          :id="'cannangmin-' + index + '-' + canNangIndex"
                          v-model="canNang.cannangmin"
                          required
                      />
                    </div>
                    <div class="col">
                      <label :for="'cannangmax-' + index + '-' + canNangIndex"
                             class="form-label">{{ t('maximum_weight') }}</label>
                      <input
                          type="number"
                          class="form-control"
                          :id="'cannangmax-' + index + '-' + canNangIndex"
                          v-model="canNang.cannangmax"
                          required
                      />
                    </div>
                    <div class="col">
                      <label :for="'giatien-' + index + '-' + canNangIndex" class="form-label">{{ t('price') }}</label>
                      <input
                          type="number"
                          class="form-control"
                          :id="'giatien-' + index + '-' + canNangIndex"
                          v-model="canNang.giatien"
                          required
                      />
                    </div>
                  </div>
                  <div v-if="validationErrors.tuyChonCanNangs[index]" class="invalid-feedback">
                    {{ validationErrors.tuyChonCanNangs[index] }}
                  </div>
                  <button type="button" class="btn btn-danger mt-2" @click="removeCanNang(tuyChon, canNangIndex)">
                    {{ t('lose_weight') }}
                  </button>
                </div>
              </div>
            </div>

            <button type="button" class="btn btn-danger" @click="removeTuyChon(index)">
              {{ t('clear_option') }}
            </button>
          </div>
        </div>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="isUpdating">
        <span v-if="!isUpdating">{{ t('save_service') }}</span>
        <span v-else>{{ t('loading') }} ({{ updateCounter }} s)</span>
      </button>
    </form>
  </div>
  <div v-else class="container mt-4">
    <p>{{ t('loading') }}</p>
  </div>
</template>

<style scoped>

</style>