<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu';

const { saveTempData, getTempData, clearTempData } = useMauKhachDatDichVu();

const selectedPet = ref('new');
const petForm = reactive({
  name: '',
  age: '',
  breed: '',
  weight: '',
  notes: ''
});

const errors = ref({});
const successMessage = ref('');

const validateForm = () => {
  errors.value = {};
  if (!petForm.name) errors.value.name = 'Tên thú cưng là bắt buộc';
  if (!petForm.age) errors.value.age = 'Tuổi thú cưng là bắt buộc';
  if (!petForm.breed) errors.value.breed = 'Giống thú cưng là bắt buộc';
  if (!petForm.weight) errors.value.weight = 'Cân nặng thú cưng là bắt buộc';
  return Object.keys(errors.value).length === 0;
};

const saveData = () => {
  if (validateForm()) {
    const currentData = getTempData();
    const updatedData = {
      ...currentData,
      thucung: {
        ...currentData.thucung,
        pet: selectedPet.value,
        ...petForm
      }
    };
    saveTempData(updatedData);
    console.log('Data saved successfully:', getTempData());
    // Clear the form after successful save
    // if (selectedPet.value === 'new') {
    //   Object.keys(petForm).forEach(key => petForm[key] = '');
    // }
  } else {
    console.log('Form validation failed');
  }
};

const loadExistingPet = () => {
  if (selectedPet.value === 'existing') {
    const existingData = getTempData();
    if (existingData && existingData.thucung) {
      Object.assign(petForm, existingData.thucung);
    }
  } else {
    Object.keys(petForm).forEach(key => petForm[key] = '');
  }
};
</script>

<!-- Rest of the template remains the same -->

<template>
  <div class="container py-5">
    <div class="card shadow">
      <div class="card-header bg-primary text-white">
        <h2 class="mb-0 text-center">Thông tin thú cưng</h2>
      </div>
      <div class="card-body">
        <form @submit.prevent="saveData">
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-group">
                <label for="pet-select" class="form-label">Chọn thú cưng</label>
                <select v-model="selectedPet" id="pet-select" class="form-select" @change="loadExistingPet">
                  <option value="new">Mới</option>
                  <option value="existing">Thú cưng hiện có</option>
                </select>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-group">
                <label for="pet-name" class="form-label">Tên thú cưng</label>
                <input v-model="petForm.name" type="text" id="pet-name" class="form-control" :class="{ 'is-invalid': errors.name }" placeholder="Nhập tên thú cưng">
                <div v-if="errors.name" class="invalid-feedback">{{ errors.name }}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <div class="form-group">
                <label for="pet-age" class="form-label">Tuổi</label>
                <input v-model="petForm.age" type="number" id="pet-age" class="form-control" :class="{ 'is-invalid': errors.age }" placeholder="Nhập tuổi">
                <div v-if="errors.age" class="invalid-feedback">{{ errors.age }}</div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="form-group">
                <label for="pet-breed" class="form-label">Giống</label>
                <input v-model="petForm.breed" type="text" id="pet-breed" class="form-control" :class="{ 'is-invalid': errors.breed }" placeholder="Nhập giống">
                <div v-if="errors.breed" class="invalid-feedback">{{ errors.breed }}</div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="form-group">
                <label for="pet-weight" class="form-label">Cân nặng (kg)</label>
                <input v-model="petForm.weight" type="number" id="pet-weight" class="form-control" :class="{ 'is-invalid': errors.weight }" placeholder="Nhập cân nặng">
                <div v-if="errors.weight" class="invalid-feedback">{{ errors.weight }}</div>
              </div>
            </div>
          </div>
          <div class="mb-3">
            <div class="form-group">
              <label for="pet-notes" class="form-label">Ghi chú</label>
              <textarea v-model="petForm.notes" id="pet-notes" rows="3" class="form-control" placeholder="Nhập ghi chú"></textarea>
            </div>
          </div>
          <div class="text-end">
            <button type="submit" class="btn btn-primary">Lưu thông tin</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  transition: box-shadow 0.3s ease-in-out;
}

.card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.form-control:focus, .form-select:focus {
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>