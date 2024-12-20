<script setup lang="ts">
import {computed, ref} from 'vue';
import { useStore } from '~/stores/UserStores';
import {useUserStore} from '~/stores/user';
const userStoreNe = useUserStore();
const userInfo = computed(() => userStoreNe.userInfo);
import { useToast } from 'vue-toastification';

const toast = useToast();
const userStore = useStore();
const petName = ref('');
const petWeight = ref('');
const petBreed = ref('');
const petAge = ref('');
const petGender = ref(true); // true for male, false for female
const isCat = ref(true);
const healthStatus = ref('');
const description = ref('');
const errors = ref({
  name: '',
  weight: '',
  breed: '',
  age: '',
  healthStatus: '',
  description: ''
});

function validateFields() {
  let isValid = true;
  errors.value = { name: '', weight: '', breed: '', age: '', healthStatus: '', description: '' };

  if (!petName.value) {
    errors.value.name = 'Tên thú cưng không được để trống';
    isValid = false;
  }
  if (!petWeight.value || isNaN(Number(petWeight.value))) {
    errors.value.weight = 'Cân nặng phải là một số';
    isValid = false;
  }
  if (!petBreed.value) {
    errors.value.breed = 'Giống không được để trống';
    isValid = false;
  }
  if (!petAge.value || isNaN(Number(petAge.value))) {
    errors.value.age = 'Tuổi phải là một số';
    isValid = false;
  }
  if (!healthStatus.value) {
    errors.value.healthStatus = 'Tình trạng sức khỏe không được để trống';
    isValid = false;
  }
  if (!description.value) {
    errors.value.description = 'Mô tả không được để trống';
    isValid = false;
  }

  return isValid;
}

function addPet() {
  if (!validateFields()) {
    return;
  }

  try {
    const newPet = {
      ten: petName.value,
      cannang: parseFloat(petWeight.value),
      giong: petBreed.value,
      tuoi: parseInt(petAge.value),
      idtaikhoan: userInfo.value.id,
      gioitinh: petGender.value,
      cophaimeokhong: isCat.value,
      tinhtrangsuckhoe: healthStatus.value,
      mota: description.value
    };
    userStore.addPet(newPet);

    // Reset form fields
    petName.value = '';
    petWeight.value = '';
    petBreed.value = '';
    petAge.value = '';
    petGender.value = true;
    isCat.value = true;
    healthStatus.value = '';
    description.value = '';

    toast.success('Thêm thú cưng thành công');
    const modalElement = document.getElementById('exampleModal');
    if (modalElement) {
      const modalInstance = bootstrap.Modal.getInstance(modalElement);
      modalInstance.hide();
    }
    return navigateTo('/user/infouser');
  } catch (error) {
    toast.error('Thêm thú cưng thất bại');
  }
}
</script>

<template>
  <button type="button" class="btn btn-sm btn-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal">
    Thêm thú cưng
  </button>

  <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Thêm thú cưng</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div>
            <label>Tên thú cưng:</label>
            <input type="text" v-model="petName" class="form-control" />
            <span class="text-danger">{{ errors.name }}</span><br>

            <label>Cân nặng:</label>
            <input type="text" v-model="petWeight" class="form-control" />
            <span class="text-danger">{{ errors.weight }}</span><br>

            <label>Giống:</label>
            <input type="text" v-model="petBreed" class="form-control" />
            <span class="text-danger">{{ errors.breed }}</span><br>

            <label>Tuổi:</label>
            <input type="text" v-model="petAge" class="form-control" />
            <span class="text-danger">{{ errors.age }}</span><br>

            <label>Giới tính:</label>
            <div>
              <input type="radio" id="male" v-model="petGender" :value="true">
              <label for="male">Đực</label>
              <input type="radio" id="female" v-model="petGender" :value="false">
              <label for="female">Cái</label>
            </div><br>

            <label>Loại thú cưng:</label>
            <div>
              <input type="radio" id="cat" v-model="isCat" :value="true">
              <label for="cat">Mèo</label>
              <input type="radio" id="dog" v-model="isCat" :value="false">
              <label for="dog">Chó</label>
            </div><br>

            <label>Tình trạng sức khỏe:</label>
            <textarea v-model="healthStatus" class="form-control"></textarea>
            <span class="text-danger">{{ errors.healthStatus }}</span><br>

            <label>Mô tả:</label>
            <textarea v-model="description" class="form-control"></textarea>
            <span class="text-danger">{{ errors.description }}</span><br>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
          <button type="button" class="custom-button" @click="addPet">Thêm</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-danger {
  color: red;
}
</style>