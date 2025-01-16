<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ThuCungKhachHang from '../../../models/ThuCungKhachHang';
import { useToast } from 'vue-toastification';
import {useUserStore} from '~/stores/user';
import {useStore} from "~/stores/UserStores";
import { useI18n } from 'vue-i18n';

const userStores = useStore();
const route = useRoute();
const router = useRouter();
const store = useUserStore();
const toast = useToast();
const { t } = useI18n();

const userInfo = computed(() => store.userInfo);
const thuCung = ref<ThuCungKhachHang | null>(null);

const ten = ref('');
const cannang = ref<number | null>(null);
const tuoi = ref<number | null>(null);
const giong = ref('');
const gioitinh = ref(true);
const cophaimeokhong = ref(true);
const tinhtrangsuckhoe = ref('');
const mota = ref('');
const errors = ref<string[]>([]);

onMounted(async () => {
  const id = route.params.id as string;
  if (id && userInfo.value) {
    // Chuyển đổi ID từ route và từ userInfo về cùng kiểu dữ liệu để so sánh
    const petId = parseInt(id, 10);
    thuCung.value = userInfo.value.listThuCung.find(pet => pet.id === petId) || null;
    if (thuCung.value) {
      initializeForm(thuCung.value);
      console.log('Form initialized with:', thuCung.value);
    } else {
      toast.error(t('pet_not_found'));
      await router.push('/user/infouser');
    }
  } else {
    toast.error(t('user_info_not_found'));
    await router.push('/user/infouser');
  }
});

const initializeForm = (pet: ThuCungKhachHang) => {
  ten.value = pet.ten;
  cannang.value = pet.cannang;
  tuoi.value = pet.tuoi;
  giong.value = pet.giong;
  gioitinh.value = pet.gioitinh;
  cophaimeokhong.value = pet.cophaimeokhong;
  tinhtrangsuckhoe.value = pet.tinhtrangsuckhoe;
  mota.value = pet.mota;
};

const isNumeric = (value: any): boolean => {
  return !isNaN(parseFloat(value)) && isFinite(value);
};

const isPositiveNumber = (value: any): boolean => {
  return isNumeric(value) && parseFloat(value) > 0;
};

const validateForm = () => {
  const allErrors: string[] = [];
  // Validate name
  if (!ten.value.trim()) {
    allErrors.push(t('pet_name_empty'));
  } else if (ten.value.trim().length < 2) {
    allErrors.push(t('pet_name_min_length'));
  } else if (ten.value.trim().length > 50) {
    allErrors.push(t('pet_name_max_length'));
  }

  // Validate weight
  if (cannang.value === null) {
    allErrors.push(t('pet_weight_empty'));
  } else if (!isNumeric(cannang.value)) {
    allErrors.push(t('pet_weight_numeric'));
  } else if (!isPositiveNumber(cannang.value)) {
    allErrors.push(t('pet_weight_positive'));
  } else if (cannang.value > 100) {
    allErrors.push(t('pet_weight_max'));
  } else if (cannang.value < 0.1) {
    allErrors.push(t('pet_weight_min'));
  }

  // Validate age
  if (tuoi.value === null) {
    allErrors.push(t('pet_age_empty'));
  } else if (!isNumeric(tuoi.value)) {
    allErrors.push(t('pet_age_numeric'));
  } else if (!Number.isInteger(tuoi.value)) {
    allErrors.push(t('pet_age_integer'));
  } else if (tuoi.value < 0) {
    allErrors.push(t('pet_age_negative'));
  } else if (tuoi.value > 30) {
    allErrors.push(t('pet_age_max'));
  }

  // Validate breed
  if (!giong.value.trim()) {
    allErrors.push(t('pet_breed_empty'));
  } else if (giong.value.trim().length < 2) {
    allErrors.push(t('pet_breed_min_length'));
  } else if (giong.value.trim().length > 50) {
    allErrors.push(t('pet_breed_max_length'));
  }

  // Validate health status
  if (!tinhtrangsuckhoe.value.trim()) {
    allErrors.push(t('pet_health_status_empty'));
  } else if (tinhtrangsuckhoe.value.trim().length < 5) {
    allErrors.push(t('pet_health_status_min_length'));
  } else if (tinhtrangsuckhoe.value.trim().length > 200) {
    allErrors.push(t('pet_health_status_max_length'));
  }

  // Hiển thị lỗi nếu có
  if (allErrors.length > 0) {
    allErrors.forEach(error => {
      toast.error(error);
    });
    return false;
  }

  return true;
};

const saveChanges = async () => {
  if (validateForm() && thuCung.value) {
    try {
      const updatedThuCung = new ThuCungKhachHang(
        thuCung.value.id,
        ten.value,
        cannang.value!,
        tuoi.value!,
        giong.value,
        thuCung.value.idtaikhoan,
        gioitinh.value,
        cophaimeokhong.value,
        tinhtrangsuckhoe.value,
        mota.value
      );
      await userStores.updatePet(updatedThuCung);
      toast.success(t('pet_update_success'));
      router.push('/user/infouser');
    } catch (error) {
      console.error('Lỗi khi cập nhật thú cưng:', error);
      toast.error(t('pet_update_error'));
    }
  }
};
</script>

<template>
  <div class="container mt-4">
    <h2 class="mb-4">{{ t('pet_update_title') }}</h2>
    <div v-if="errors.length" class="alert alert-danger">
      <ul>
        <li v-for="error in errors" :key="error">{{ error }}</li>
      </ul>
    </div>
    <form @submit.prevent="saveChanges" v-if="thuCung">
      <div class="mb-3">
        <label for="ten" class="form-label">{{ t('petName') }}</label>
        <input type="text" class="form-control" id="ten" v-model="ten" required>
      </div>
      <div class="mb-3">
        <label for="cannang" class="form-label">{{ t('weight') }}</label>
        <input type="number" class="form-control" id="cannang" v-model.number="cannang" step="0.1" required>
      </div>
      <div class="mb-3">
        <label for="tuoi" class="form-label">{{ t('age') }}</label>
        <input type="number" class="form-control" id="tuoi" v-model.number="tuoi" required>
      </div>
      <div class="mb-3">
        <label for="giong" class="form-label">{{ t('breed') }}</label>
        <input type="text" class="form-control" id="giong" v-model="giong" required>
      </div>
      <div class="mb-3">
        <label class="form-label d-block">{{ t('gender') }}</label>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="male" :value="true" v-model="gioitinh">
          <label class="form-check-label" for="male">{{ t('male') }}</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="female" :value="false" v-model="gioitinh">
          <label class="form-check-label" for="female">{{ t('female') }}</label>
        </div>
      </div>
      <div class="mb-3">
        <label class="form-label d-block">{{ t('petType') }}</label>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="cat" :value="true" v-model="cophaimeokhong">
          <label class="form-check-label" for="cat">{{ t('cat') }}</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="dog" :value="false" v-model="cophaimeokhong">
          <label class="form-check-label" for="dog">{{ t('dog') }}</label>
        </div>
      </div>
      <div class="mb-3">
        <label for="tinhtrangsuckhoe" class="form-label">{{ t('healthStatus') }}</label>
        <textarea class="form-control" id="tinhtrangsuckhoe" v-model="tinhtrangsuckhoe" required></textarea>
      </div>
      <div class="mb-3">
        <label for="mota" class="form-label">{{ t('description') }}</label>
        <textarea class="form-control" id="mota" v-model="mota"></textarea>
      </div>
      <div class="mb-3">
        <button type="submit" class="custom-button">{{ t('saveInfo') }}</button>
        <button type="button" class="custom-button" @click="router.push('/user/infouser')">{{ t('cancel') }}</button>
      </div>
    </form>
    <div v-else class="alert alert-warning">
      {{ t('loading_pet_info') }}
    </div>
  </div>
</template>

<style scoped>

</style>