<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ThuCungKhachHang from '../../../models/ThuCungKhachHang';
import { useToast } from 'vue-toastification';
import {useUserStore} from '~/stores/user';
import {useStore} from "~/stores/UserStores";

const userStores = useStore();
const route = useRoute();
const router = useRouter();
const store = useUserStore();
const toast = useToast();

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
      toast.error('Không tìm thấy thông tin thú cưng.');
      router.push('/user/infouser');
    }
  } else {
    toast.error('Không có thông tin người dùng hoặc ID thú cưng.');
    router.push('/user/infouser');
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

const validateForm = () => {
  errors.value = [];
  if (!ten.value.trim()) errors.value.push('Tên không được để trống.');
  if (cannang.value === null || cannang.value <= 0) errors.value.push('Cân nặng phải lớn hơn 0.');
  if (tuoi.value === null || tuoi.value < 0) errors.value.push('Tuổi không hợp lệ.');
  if (!giong.value.trim()) errors.value.push('Giống không được để trống.');
  if (!tinhtrangsuckhoe.value.trim()) errors.value.push('Tình trạng sức khỏe không được để trống.');
  return errors.value.length === 0;
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
      toast.success('Thú cưng đã được cập nhật thành công.');
      router.push('/user/infouser');
    } catch (error) {
      console.error('Lỗi khi cập nhật thú cưng:', error);
      toast.error('Lỗi khi cập nhật thú cưng. Vui lòng thử lại.');
    }
  }
};
</script>

<template>
  <div class="container mt-4">
    <h2 class="mb-4">Cập nhật thông tin thú cưng</h2>
    <div v-if="errors.length" class="alert alert-danger">
      <ul>
        <li v-for="error in errors" :key="error">{{ error }}</li>
      </ul>
    </div>
    <form @submit.prevent="saveChanges" v-if="thuCung">
      <div class="mb-3">
        <label for="ten" class="form-label">Tên</label>
        <input type="text" class="form-control" id="ten" v-model="ten" required>
      </div>
      <div class="mb-3">
        <label for="cannang" class="form-label">Cân nặng (kg)</label>
        <input type="number" class="form-control" id="cannang" v-model.number="cannang" step="0.1" required>
      </div>
      <div class="mb-3">
        <label for="tuoi" class="form-label">Tuổi</label>
        <input type="number" class="form-control" id="tuoi" v-model.number="tuoi" required>
      </div>
      <div class="mb-3">
        <label for="giong" class="form-label">Giống</label>
        <input type="text" class="form-control" id="giong" v-model="giong" required>
      </div>
      <div class="mb-3">
        <label class="form-label d-block">Giới tính</label>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="male" :value="true" v-model="gioitinh">
          <label class="form-check-label" for="male">Đực</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="female" :value="false" v-model="gioitinh">
          <label class="form-check-label" for="female">Cái</label>
        </div>
      </div>
      <div class="mb-3">
        <label class="form-label d-block">Loại thú cưng</label>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="cat" :value="true" v-model="cophaimeokhong">
          <label class="form-check-label" for="cat">Mèo</label>
        </div>
        <div class="form-check form-check-inline">
          <input class="form-check-input" type="radio" id="dog" :value="false" v-model="cophaimeokhong">
          <label class="form-check-label" for="dog">Chó</label>
        </div>
      </div>
      <div class="mb-3">
        <label for="tinhtrangsuckhoe" class="form-label">Tình trạng sức khỏe</label>
        <textarea class="form-control" id="tinhtrangsuckhoe" v-model="tinhtrangsuckhoe" required></textarea>
      </div>
      <div class="mb-3">
        <label for="mota" class="form-label">Mô tả</label>
        <textarea class="form-control" id="mota" v-model="mota"></textarea>
      </div>
      <div class="mb-3">
        <button type="submit" class="btn btn-primary me-2">Lưu thay đổi</button>
        <button type="button" class="btn btn-secondary" @click="router.push('/user/infouser')">Hủy</button>
      </div>
    </form>
    <div v-else class="alert alert-warning">
      Đang tải thông tin thú cưng...
    </div>
  </div>
</template>

<style scoped>

</style>