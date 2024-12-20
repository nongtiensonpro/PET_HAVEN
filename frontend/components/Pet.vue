<template>
  <div class="container py-5">
    <form @submit.prevent="saveData">
      <label for="pet-select" class="form-label" v-if="listThuCung?.length > 0">Lựa chọn thú cưng đã lưu hoặc thêm thú cưng mới</label>
      <label for="pet-select" class="form-label" v-else>Hãy nhập thông tin thú cưng đầu tiên của bạn</label>
      <div class="col-md-6">
        <div class="form-group">
          <label for="pet-select" class="form-label">Thông tin thú cưng đã được lưu {{listThuCung?.length}}</label>
          <select v-model="selectedPet" id="pet-select" class="form-select">
            <option value="new">Thú cưng mới</option>
            <option v-if="listThuCung?.length > 0" value="existing">Thú cưng hiện có</option>
          </select>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-ten" class="form-label">Tên thú cưng</label>
              </div>
              <div class="col-4">
                <TenThuCungLaGi/>
              </div>
            </div>
            <input v-model="petForm.ten" type="text" id="pet-ten" class="form-control" :class="{ 'is-invalid': errors.ten }" placeholder="Nhập tên thú cưng">
            <div v-if="errors.ten" class="invalid-feedback">{{ errors.ten }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-tuoi" class="form-label">Tuổi</label>
              </div>
              <div class="col-4">
                <ThongTinVeTuoi/>
              </div>
            </div>
            <input v-model="petForm.tuoi" type="number" id="pet-tuoi" class="form-control" :class="{ 'is-invalid': errors.tuoi }" placeholder="Nhập tuổi">
            <div v-if="errors.tuoi" class="invalid-feedback">{{ errors.tuoi }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-giong" class="form-label">Giống</label>
              </div>
              <div class="col-4">
                <ThongTinVeGiongCho/>
              </div>
            </div>
            <input v-model="petForm.giong" type="text" id="pet-giong" class="form-control" :class="{ 'is-invalid': errors.giong }" placeholder="Nhập giống">
            <div v-if="errors.giong" class="invalid-feedback">{{ errors.giong }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <label for="pet-cannang" class="form-label">Cân nặng (kg)</label>
              </div>
              <div class="col-4">
                <CanNangChoMeo/>
              </div>
            </div>
            <input v-model="petForm.cannang" type="number" id="pet-cannang" class="form-control" :class="{ 'is-invalid': errors.cannang }" placeholder="Nhập cân nặng">
            <div v-if="errors.cannang" class="invalid-feedback">{{ errors.cannang }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-gioitinh" class="form-label">Giới tính</label>
            <select v-model="petForm.gioitinh" id="pet-gioitinh" class="form-select" :class="{ 'is-invalid': errors.gioitinh }">
              <option :value="true">Đực</option>
              <option :value="false">Cái</option>
            </select>
            <div v-if="errors.gioitinh" class="invalid-feedback">{{ errors.gioitinh }}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-loai" class="form-label">Loại thú cưng</label>
            <select v-model="petForm.cophaimeokhong" id="pet-loai" class="form-select" :class="{ 'is-invalid': errors.cophaimeokhong }">
              <option :value="true">Mèo</option>
              <option :value="false">Chó</option>
            </select>
            <div v-if="errors.cophaimeokhong" class="invalid-feedback">{{ errors.cophaimeokhong }}</div>
          </div>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-tinhtrangsuckhoe" class="form-label">Tình trạng sức khỏe</label>
            <input v-model="petForm.tinhtrangsuckhoe" type="text" id="pet-tinhtrangsuckhoe" class="form-control" placeholder="Nhập tình trạng sức khỏe">
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-group">
            <label for="pet-mota" class="form-label">Mô tả</label>
            <textarea v-model="petForm.mota" id="pet-mota" rows="3" class="form-control" placeholder="Nhập mô tả"></textarea>
          </div>
        </div>
      </div>

      <div class="text-end">
        <button type="submit" class="custom-button">Lưu thông tin</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu';
import { useDatLichStore } from '~/stores/DatLichStores'
import ThuCungKhachHang from "~/models/ThuCungKhachHang";
import ThongTinVeTuoi from "~/components/ThongTinVeTuoi.vue";

const datLichStore = useDatLichStore()
const { saveTempData, getTempData, updateDataAfterBooking } = useMauKhachDatDichVu();
const listThuCung = computed((): ThuCungKhachHang[] => datLichStore.ListThuCung || []);

const selectedPet = ref('new');
const selectedExistingPet = ref(null);

onMounted(() => {
  if (listThuCung.value.length > 0) {
    selectedPet.value = 'existing';
    selectedExistingPet.value = listThuCung.value[0].id;
    loadExistingPet();
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
  if (!petForm.ten) errors.value.ten = 'Vui lòng nhập tên thú cưng';
  if (!petForm.tuoi) {
    errors.value.tuoi = 'Vui lòng nhập tuổi';
  } else if (isNaN(petForm.tuoi) || Number(petForm.tuoi) <= 0) {
    errors.value.tuoi = 'Tuổi phải là số dương';
  }
  if (!petForm.giong) errors.value.giong = 'Vui lòng nhập giống';
  if (!petForm.cannang) {
    errors.value.cannang = 'Vui lòng nhập cân nặng';
  } else if (isNaN(petForm.cannang) || Number(petForm.cannang) <= 0) {
    errors.value.cannang = 'Cân nặng phải là số dương';
  }
  if (petForm.gioitinh === undefined) errors.value.gioitinh = 'Vui lòng chọn giới tính';
  if (petForm.cophaimeokhong === undefined) errors.value.cophaimeokhong = 'Vui lòng chọn loại thú cưng';
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
        id: selectedPet.value === 'existing' ? selectedExistingPet.value : null
      }
    };
    updateDataAfterBooking(updatedData);
  }
};

const loadExistingPet = () => {
  if (selectedPet.value === 'existing' && selectedExistingPet.value) {
    const pet = listThuCung.value.find(p => p.id === selectedExistingPet.value);
    if (pet) {
      Object.assign(petForm, pet);
    }
  } else {
    Object.keys(petForm).forEach(key => petForm[key] = '');
    petForm.gioitinh = true;
    petForm.cophaimeokhong = true;
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
</script>

<style scoped>
/* Existing styles */
</style>