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
const saveChanges = async () => {
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

const fileInput = ref<HTMLInputElement | null>(null);

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
      </div>

      <div class="mb-3">
        <label for="mota" class="form-label">{{ t('serviceDescription') }}</label>
        <textarea
            class="form-control"
            id="mota"
            v-model="editedService.mota"
            rows="3"
        ></textarea>
      </div>

      <div class="mb-3">
        <label for="fileUpdate" class="form-label">{{ t('image') }}</label>
        <input
            type="file"
            class="form-control"
            id="fileUpdate"
            accept="image/*"
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