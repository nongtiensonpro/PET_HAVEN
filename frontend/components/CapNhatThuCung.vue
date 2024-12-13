<script setup lang="ts">
import {computed, ref} from 'vue';
import ThuCungKhachHang from '../models/ThuCungKhachHang';
import { useStore } from '~/stores/UserStores';
import { useToast } from 'vue-toastification';

const toast = useToast();
const userInfo = computed(() => useStore);

const props = defineProps<{
  thuCung: ThuCungKhachHang | null;
}>();

const ten = ref(props.thuCung ? props.thuCung.ten : '');
const cannang = ref<number | null>(props.thuCung ? props.thuCung.cannang : null);
const tuoi = ref<number | null>(props.thuCung ? props.thuCung.tuoi : null);
const giong = ref(props.thuCung ? props.thuCung.giong : '');
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
  if (!giong) {
    errors.value.push('Giống không được để trống.');
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
          props.thuCung.idtaikhoan
      );
      console.log('Thú cưng đã được cập nhật:', thuCung.value);
      useStore().updatePet(thuCung.value);
      toast.success('Thú cưng đã được cập nhật thành công.');
      const modalElement = document.getElementById('exampleModal1');
      if (modalElement) {
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        modalInstance.hide();
      }
    }catch (error) {
      toast.error('Lỗi khi cập nhật thú cưng. Vui lòng thử lại.');
    }
  }
};
</script>

<template>
  <div class="container">
    <button type="button" class="btn btn-sm btn-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal1">
      Cập nhật thú cưng
    </button>

    <div class="modal fade" id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel" >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="exampleModalLabel">Cập nhật thú cưng</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
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
          </div>
          <div class="modal-footer">
            <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
            <button type="button" class="custom-button" @click="saveChanges">Lưu</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>