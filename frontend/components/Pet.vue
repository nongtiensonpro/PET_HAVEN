<template>
  <div class="container py-5">
    <form @submit.prevent="saveData">
      <label for="pet-select" class="form-label" v-if="listThuCung?.length > 0">{{ t('chooseSavedPet') }}</label>
      <label for="pet-select" class="form-label" v-else>{{ t('enterFirstPet') }}</label>
      <div class="col-md-6">
        <div class="form-group">
          <label for="pet-select" class="form-label">{{ t('savedPetsCount') }} {{listThuCung?.length}}</label>
          <select v-model="selectedPet" id="pet-select" class="form-select" @change="handlePetSelection">
            <option value="new">{{ t('newPet') }}</option>
            <option v-for="pet in listThuCung" :key="pet.id" :value="pet.id">
              {{ pet.ten }} ({{ pet.cophaimeokhong ? t('cat') : t('dog') }})
            </option>
          </select>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-ten" class="form-label">{{ t('petName') }}</label>
              </div>
              <div class="col-4">
                <TenThuCungLaGi/>
              </div>
            </div>
            <input v-model="petForm.ten" type="text" id="pet-ten" class="form-control" :class="{ 'is-invalid': errors.ten }" :placeholder="t('enterPetName')">
            <div v-if="errors.ten" class="invalid-feedback">{{ t('pleaseEnterPetName') }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-tuoi" class="form-label">{{ t('age') }}</label>
              </div>
              <div class="col-4">
                <ThongTinVeTuoi/>
              </div>
            </div>
            <input v-model="petForm.tuoi" type="number" id="pet-tuoi" class="form-control" :class="{ 'is-invalid': errors.tuoi }" :placeholder="t('enterAge')">
            <div v-if="errors.tuoi" class="invalid-feedback">{{ errors.tuoi === 'Vui lòng nhập tuổi' ? t('pleaseEnterAge') : t('ageMustBePositive') }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-giong" class="form-label">{{ t('breed') }}</label>
              </div>
              <div class="col-4">
                <ThongTinVeGiongCho/>
              </div>
            </div>
            <input v-model="petForm.giong" type="text" id="pet-giong" class="form-control" :class="{ 'is-invalid': errors.giong }" :placeholder="t('enterBreed')">
            <div v-if="errors.giong" class="invalid-feedback">{{ t('pleaseEnterBreed') }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-cannang" class="form-label">{{ t('weight') }}</label>
              </div>
              <div class="col-4">
                <CanNangChoMeo/>
              </div>
            </div>
            <input v-model="petForm.cannang" type="number" id="pet-cannang" class="form-control" :class="{ 'is-invalid': errors.cannang }" :placeholder="t('enterWeight')">
            <div v-if="errors.cannang" class="invalid-feedback">{{ errors.cannang === 'Vui lòng nhập cân nặng' ? t('pleaseEnterWeight') : t('weightMustBePositive') }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-gioitinh" class="form-label">{{ t('gender') }}</label>
            <select v-model="petForm.gioitinh" id="pet-gioitinh" class="form-select" :class="{ 'is-invalid': errors.gioitinh }">
              <option :value="true">{{ t('male') }}</option>
              <option :value="false">{{ t('female') }}</option>
            </select>
            <div v-if="errors.gioitinh" class="invalid-feedback">{{ t('pleaseSelectGender') }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-loai" class="form-label">{{ t('petType') }}</label>
            <select v-model="petForm.cophaimeokhong" id="pet-loai" class="form-select" :class="{ 'is-invalid': errors.cophaimeokhong }">
              <option :value="true">{{ t('cat') }}</option>
              <option :value="false">{{ t('dog') }}</option>
            </select>
            <div v-if="errors.cophaimeokhong" class="invalid-feedback">{{ t('pleaseSelectPetType') }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-tinhtrangsuckhoe" class="form-label">{{ t('healthStatus') }}</label>
            <input v-model="petForm.tinhtrangsuckhoe" type="text" id="pet-tinhtrangsuckhoe" class="form-control" :placeholder="t('enterHealthStatus')">
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-mota" class="form-label">{{ t('description') }}</label>
            <textarea v-model="petForm.mota" id="pet-mota" rows="3" class="form-control" :placeholder="t('enterDescription')"></textarea>
          </div>
        </div>
      </div>

      <div class="text-end">
        <button type="submit" class="custom-button">{{ t('saveInfo') }}</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu';
import { useDatLichStore } from '~/stores/DatLichStores';
import { useI18n } from 'vue-i18n';
import ThuCungKhachHang from "~/models/ThuCungKhachHang";
import ThongTinVeTuoi from "~/components/ThongTinVeTuoi.vue";
import ThongTinVeGiongCho from "~/components/ThongTinVeGiongCho.vue";
import CanNangChoMeo from "~/components/CanNangChoMeo.vue";
import TenThuCungLaGi from "~/components/TenThuCungLaGi.vue";

const { t } = useI18n();

const datLichStore = useDatLichStore()
const { saveTempData, getTempData, updateDataAfterBooking } = useMauKhachDatDichVu();
const listThuCung = computed((): ThuCungKhachHang[] => datLichStore.ListThuCung || []);

const selectedPet = ref('new');
const selectedExistingPet = ref(null);

onMounted(() => {
  if (listThuCung.value.length > 0) {
    selectedPet.value = listThuCung.value[0].id;
    loadExistingPet(selectedPet.value);
  }
});

const petForm = reactive({
  id: null,
  ten: '',
  tuoi: '',
  giong: '',
  cannang: '',
  idtaikhoan: '',
  gioitinh: true,
  cophaimeokhong: true,
  tinhtrangsuckhoe: '',
  mota: ''
});

const errors = ref({});

const validateForm = () => {
  errors.value = {};
  if (!petForm.ten) errors.value.ten = t('pleaseEnterPetName');
  if (!petForm.tuoi) {
    errors.value.tuoi = t('pleaseEnterAge');
  } else if (isNaN(petForm.tuoi) || Number(petForm.tuoi) <= 0) {
    errors.value.tuoi = t('ageMustBePositive');
  }
  if (!petForm.giong) errors.value.giong = t('pleaseEnterBreed');
  if (!petForm.cannang) {
    errors.value.cannang = t('pleaseEnterWeight');
  } else if (isNaN(petForm.cannang) || Number(petForm.cannang) <= 0) {
    errors.value.cannang = t('weightMustBePositive');
  }
  if (petForm.gioitinh === undefined) errors.value.gioitinh = t('pleaseSelectGender');
  if (petForm.cophaimeokhong === undefined) errors.value.cophaimeokhong = t('pleaseSelectPetType');
  return Object.keys(errors.value).length === 0;
};

const saveData = () => {
  if (validateForm()) {
    const currentData = getTempData();
    const updatedData = {
      ...currentData,
      thucung: {
        ...currentData.thucung,
        ...petForm,
        id: selectedPet.value === 'new' ? null : selectedPet.value
      }
    };
    updateDataAfterBooking(updatedData);
  }
};

const loadExistingPet = (petId) => {
  const pet = listThuCung.value.find(p => p.id === petId);
  if (pet) {
    Object.assign(petForm, pet);
  }
};

watch(selectedPet, (newValue) => {
  if (newValue === 'new') {
    selectedExistingPet.value = null;
    Object.keys(petForm).forEach(key => petForm[key] = '');
    petForm.gioitinh = true;
    petForm.cophaimeokhong = true;
  } else if (newValue === 'existing' && listThuCung.value.length > 0) {
    selectedExistingPet.value = listThuCung.value[0].id;
    loadExistingPet();
  }
});

watch(selectedExistingPet, loadExistingPet);

const handlePetSelection = () => {
  if (selectedPet.value === 'new') {
    Object.keys(petForm).forEach(key => petForm[key] = '');
    petForm.gioitinh = true;
    petForm.cophaimeokhong = true;
  } else {
    loadExistingPet(selectedPet.value);
  }
};
</script>

<style scoped>
/* Existing styles */
</style>