<script setup lang="ts">
import {computed, ref} from 'vue';
import ThuCungKhachHang from '../../../models/ThuCungKhachHang';
import {useStore} from '~/stores/UserStores';
import {useToast} from 'vue-toastification';

const toast = useToast();
const userInfo = computed(() => useStore);

const props = defineProps<{
  thuCung: ThuCungKhachHang | null;
}>();

const ten = ref(props.thuCung ? props.thuCung.ten : '');
const cannang = ref<number | null>(props.thuCung ? props.thuCung.cannang : null);
const tuoi = ref<number | null>(props.thuCung ? props.thuCung.tuoi : null);
const giong = ref(props.thuCung ? props.thuCung.giong : '');
const gioitinh = ref(props.thuCung ? props.thuCung.gioitinh : true);
const cophaimeokhong = ref(props.thuCung ? props.thuCung.cophaimeokhong : true);
const tinhtrangsuckhoe = ref(props.thuCung ? props.thuCung.tinhtrangsuckhoe : '');
const mota = ref(props.thuCung ? props.thuCung.mota : '');
const errors = ref<string[]>([]);
const thuCung = ref<ThuCungKhachHang | null>(props.thuCung);

const validateForm = () => {
  errors.value = [];
  if (!ten.value) {
    errors.value.push('Tên không được để trống.');
  }
  if (cannang.value === null || cannang.value <= 0) {
    errors.value.push('Cân nặng phải lớn hơn 0.');
  }
  if (tuoi.value === null || tuoi.value < 0) {
    errors.value.push('Tuổi không hợp lệ.');
  }
  if (!giong.value) {
    errors.value.push('Giống không được để trống.');
  }
  if (!tinhtrangsuckhoe.value) {
    errors.value.push('Tình trạng sức khỏe không được để trống.');
  }
  return errors.value.length === 0;
};

const saveChanges = () => {
  if (validateForm()) {
    try {
      thuCung.value = new ThuCungKhachHang(
          props.thuCung.id,
          ten.value,
          cannang.value,
          tuoi.value,
          giong.value,
          props.thuCung.idtaikhoan,
          gioitinh.value,
          cophaimeokhong.value,
          tinhtrangsuckhoe.value,
          mota.value
      );
      console.log('Thú cưng đã được cập nhật:', thuCung.value);
      useStore().updatePet(thuCung.value);
      toast.success('Thú cưng đã được cập nhật thành công.');
      return navigateTo('/user/infouser');
    } catch (error) {
      toast.error('Lỗi khi cập nhật thú cưng. Vui lòng thử lại.');
    }
  }
};
</script>

<template>
  <div class="container">
    <div v-if="errors.length" class="alert alert-danger">
      <ul>
        <li v-for="error in errors" :key="error">{{ error }}</li>
      </ul>
    </div>
    <div class="mb-3">
      <label for="ten" class="form-label">Tên</label>
      <input type="text" class="form-control" id="ten" v-model="ten">
    </div>
    <div class="mb-3">
      <label for="cannang" class="form-label">Cân nặng</label>
      <input type="number" class="form-control" id="cannang" v-model.number="cannang">
    </div>
    <div class="mb-3">
      <label for="tuoi" class="form-label">Tuổi</label>
      <input type="number" class="form-control" id="tuoi" v-model.number="tuoi">
    </div>
    <div class="mb-3">
      <label for="giong" class="form-label">Giống</label>
      <input type="text" class="form-control" id="giong" v-model="giong">
    </div>
    <div class="mb-3">
      <label class="form-label">Giới tính</label>
      <div>
        <input type="radio" id="male" value="true" v-model="gioitinh">
        <label for="male">Đực</label>
        <input type="radio" id="female" value="false" v-model="gioitinh">
        <label for="female">Cái</label>
      </div>
    </div>
    <div class="mb-3">
      <label class="form-label">Loại thú cưng</label>
      <div>
        <input type="radio" id="cat" value="true" v-model="cophaimeokhong">
        <label for="cat">Mèo</label>
        <input type="radio" id="dog" value="false" v-model="cophaimeokhong">
        <label for="dog">Chó</label>
      </div>
    </div>
    <div class="mb-3">
      <label for="tinhtrangsuckhoe" class="form-label">Tình trạng sức khỏe</label>
      <textarea class="form-control" id="tinhtrangsuckhoe" v-model="tinhtrangsuckhoe"></textarea>
    </div>
    <div class="mb-3">
      <label for="mota" class="form-label">Mô tả</label>
      <textarea class="form-control" id="mota" v-model="mota"></textarea>
    </div>

    <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
    <button type="button" class="custom-button" @click="saveChanges">Lưu</button>
  </div>
</template>

<style scoped></style>