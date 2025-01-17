<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useServiceStore } from '~/stores/DichVuStores';
import DichVu from '~/models/DichVu';
import { useToast } from 'vue-toastification';
import { useI18n } from 'vue-i18n';
import autoExpandTextarea from '~/directives/autoExpandTextarea';

const { t } = useI18n();
const toast = useToast();
const route = useRoute();
const serviceStore = useServiceStore();
const id = ref<number | null>(null);
const editedService = ref<DichVu | null>(null);

const { updateService } = serviceStore;

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

const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) >= 0;
};

const saveChanges = async () => {
  if (!editedService.value) return;

  const allErrors: string[] = [];

  // Validate service name
  if (!editedService.value.tendichvu || editedService.value.tendichvu.trim().length < 3) {
    allErrors.push(t('service_name_invalid'));
  }

  // Validate service options
  if (editedService.value.tuyChonDichVus.length === 0) {
    allErrors.push(t('at_least_one_option_required'));
  }

  // Kiểm tra trùng lặp tên tùy chọn
  const optionNames = new Set();

  editedService.value.tuyChonDichVus.forEach((tuyChon, index) => {
    if (!tuyChon.tentuychon || tuyChon.tentuychon.trim().length < 2) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('option_name_invalid')}`);
    }

    // Kiểm tra trùng lặp tên
    if (optionNames.has(tuyChon.tentuychon.trim().toLowerCase())) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('duplicate_option_name')}`);
    }
    optionNames.add(tuyChon.tentuychon.trim().toLowerCase());

    // Validate mô tả
    if (!tuyChon.mota || tuyChon.mota.trim().length < 10) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('description_min_length')}`);
    }

    if (tuyChon.tuyChonCanNangs.length === 0) {
      allErrors.push(`${t('option')} ${index + 1}: ${t('at_least_one_weight_option_required')}`);
    }

    // Kiểm tra trùng lặp và hợp lệ của khoảng cân nặng
    const weightRanges: Array<{ min: number, max: number }> = [];

    tuyChon.tuyChonCanNangs.forEach((canNang, canNangIndex) => {
      const min = parseFloat(canNang.cannangmin);
      const max = parseFloat(canNang.cannangmax);

      if (!isNumeric(canNang.cannangmin) || !Number.isInteger(min)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_must_be_integer')}`);
      }

      if (!isNumeric(canNang.cannangmax) || !Number.isInteger(max)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_must_be_integer')}`);
      }

      if (!isPositiveNumber(canNang.cannangmin)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('minimum_weight_negative')}`);
      }

      if (!isPositiveNumber(canNang.cannangmax)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('maximum_weight_negative')}`);
      }

      if (min >= max) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('weight_range_invalid')}`);
      }

      // Kiểm tra trùng lặp hoặc giao nhau của khoảng cân nặng
      for (const range of weightRanges) {
        if ((min >= range.min && min <= range.max) || (max >= range.min && max <= range.max)) {
          allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('overlapping_weight_ranges')}`);
          break;
        }
      }
      weightRanges.push({ min, max });

      if (!isNumeric(canNang.giatien) || !isPositiveNumber(canNang.giatien)) {
        allErrors.push(`${t('option')} ${index + 1}, ${t('weight_option')} ${canNangIndex + 1}: ${t('price_invalid')}`);
      }
    });
  });

  if (allErrors.length > 0) {
    allErrors.forEach(error => {
      toast.error(error);
    });
    return;
  }

  try {
    isUpdating.value = true;
    updateCounter.value = 10;

    updateInterval.value = setInterval(() => {
      updateCounter.value--;
    }, 1000);

    await updateService(editedService.value);

    if (updateInterval.value) {
      clearInterval(updateInterval.value);
    }
    isUpdating.value = false;
    toast.success(t('service_updated_successfully'));
    navigateTo('/admin/service/servicelist');
  } catch (error) {
    if (updateInterval.value) {
      clearInterval(updateInterval.value);
    }
    isUpdating.value = false;
    toast.error(t('error_updating_service'));
  }
};

const addTuyChon = () => {
  if (!editedService.value) return;
  editedService.value.tuyChonDichVus.push({
    id: Date.now(),
    idDichVu: editedService.value.id,
    tentuychon: '',
    mota: '',  // Thay đổi từ null thành string rỗng
    trangthai: true,
    tuyChonCanNangs: []
  });
};

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
        <input type="text" class="form-control" id="tendichvu" v-model="editedService.tendichvu" required />
      </div>

      <div class="mb-3">
        <label for="mota" class="form-label">{{ t('serviceDescription') }}</label>
        <textarea class="form-control" id="mota" v-model="editedService.mota" rows="3"></textarea>
      </div>

      <div class="mb-3">
        <label for="fileUpdate" class="form-label">{{ t('image') }}</label>
        <input type="file" class="form-control" id="fileUpdate" accept="image/*" @change="handleFileUpload" />
        <img v-if="editedService.anh" :src="editedService.anh" alt="Service Image" class="mt-2" style="max-width: 200px;">
      </div>

      <div class="mb-3">
        <label class="form-label">{{ t('status') }}</label>
        <div class="form-check">
          <input class="form-check-input" type="checkbox" id="trangthai" v-model="editedService.trangthai" />
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

        <div v-for="(tuyChon, index) in editedService.tuyChonDichVus" :key="tuyChon.id" class="card mb-3">
          <div class="card-header d-flex justify-content-between align-items-center">
            <div>
              {{ t('status') }} : {{ tuyChon.trangthai ? t('active') : t('inactive') }}
            </div>
            <div>
              <button type="button" class="btn btn-secondary me-2" @click="doiTrangThaiTuyChonDichVu(tuyChon.id)">
                {{ t('change_status') }}
              </button>
              <button type="button" class="btn btn-danger" @click="removeTuyChon(index)">
                {{ t('delete') }}
              </button>
            </div>
          </div>
          <div class="card-body">
            <div class="mb-3">
              <label :for="'tentuychon-' + index" class="form-label">{{ t('options_name') }}</label>
              <input type="text" class="form-control" :id="'tentuychon-' + index" v-model="tuyChon.tentuychon" required />
            </div>

            <div class="mb-3">
              <label :for="'mota-tuychon-' + index" class="form-label">{{ t('option_description') }}</label>
              <textarea class="form-control" :id="'mota-tuychon-' + index" v-model="tuyChon.mota"
                v-auto-expand-textarea></textarea>
            </div>

            <div class="mb-3">
              <div class="d-flex justify-content-between align-items-center">
                <h6>{{ t('weightOptions') }}</h6>
                <button type="button" class="btn btn-secondary" @click="addCanNang(tuyChon)">
                  {{ t('add_weight_option') }}
                </button>
              </div>

              <div v-for="(canNang, canNangIndex) in tuyChon.tuyChonCanNangs" :key="canNang.id" class="card mt-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <div>
                    {{ t('status') }} : {{ canNang.trangthai ? t('active') : t('inactive') }}
                  </div>
                  <div>
                    <button type="button" class="btn btn-secondary me-2"
                      @click="doiTrangThaiTuyChonCanNang(tuyChon.id, canNang.id)">
                      {{ t('change_status') }}
                    </button>
                    <button type="button" class="btn btn-danger" @click="removeCanNang(tuyChon, canNangIndex)">
                      {{ t('delete') }}
                    </button>
                  </div>
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <div class="col">
                      <label :for="'cannangmin-' + index + '-' + canNangIndex" class="form-label">
                        {{ t('minimum_weight') }}
                      </label>
                      <input type="number" class="form-control" :id="'cannangmin-' + index + '-' + canNangIndex"
                        v-model="canNang.cannangmin" required />
                    </div>
                    <div class="col">
                      <label :for="'cannangmax-' + index + '-' + canNangIndex" class="form-label">
                        {{ t('maximum_weight') }}
                      </label>
                      <input type="number" class="form-control" :id="'cannangmax-' + index + '-' + canNangIndex"
                        v-model="canNang.cannangmax" required />
                    </div>
                    <div class="col">
                      <label :for="'giatien-' + index + '-' + canNangIndex" class="form-label">{{ t('price') }}</label>
                      <input type="number" class="form-control" :id="'giatien-' + index + '-' + canNangIndex"
                        v-model="canNang.giatien" required />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <button type="submit" class="btn btn-primary" :disabled="isUpdating">
        <span v-if="isUpdating">
          {{ t('updating') }} ({{ updateCounter }})
        </span>
        <span v-else>
          {{ t('save_changes') }}
        </span>
      </button>
    </form>
  </div>
  <div v-else class="container mt-4">
    <p>{{ t('loading') }}</p>
  </div>
</template>

<style scoped>
textarea {
  width: 100%;
  min-height: 100px;
  resize: vertical;
}
</style>