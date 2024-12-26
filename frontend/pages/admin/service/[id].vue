<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useServiceStore} from '~/stores/DichVuStores';
import DichVu from '~/models/DichVu';
import { useToast } from 'vue-toastification';


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

const saveChanges = async () => {
  if (editedService.value) {
    try{
      await serviceStore.updateService(editedService.value);
      toast.success('Cập nhật dịch vụ thành công!');
      navigateTo('/admin/service/servicelist');
    }catch (error) {
      toast.error('Lỗi cập nhật dịch vụ!');
    }
  }
};

const addTuyChon = () => {
  if (editedService.value) {
    editedService.value.tuyChonDichVus.push({
      id: Date.now(), // Temporary ID
      tenTuyChon: '',
      moTa: '',
      trangThai: true,
      tuyChonCanNangs: []
    });
  }
};

const addCanNang = (tuyChon: any) => {
  tuyChon.tuyChonCanNangs.push({
    id: Date.now(), // Temporary ID
    canNangMin: 0,
    canNangMax: null,
    giaTien: 0,
    trangThai: true
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

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files?.length) return;

  const file = target.files[0];
  const formData = new FormData();
  formData.append('file', file);

  try {
    // Assume we have an API endpoint for file upload
    const response = await fetch('/api/upload', {
      method: 'POST',
      body: formData
    });
    const result = await response.json();
    if (result.url && editedService.value) {
      editedService.value.anh = result.url;
    }
  } catch (error) {
    console.error('Error uploading file:', error);
    // Handle error (e.g., show error message to user)
  }
};
</script>

<template>
  <div class="container mt-4">
    <h1 class="mb-4">Chi tiết dịch vụ {{ id }}</h1>
    <div v-if="editedService" class="card">
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">Tên dịch vụ:</label>
          <input v-model="editedService.tendichvu" class="form-control">
        </div>
        <div class="mb-3">
          <label class="form-label">Mô tả:</label>
          <textarea v-model="editedService.mota" class="form-control"></textarea>
        </div>
        <div class="mb-3 form-check">
          <input v-model="editedService.trangthai" type="checkbox" class="form-check-input" id="trangThai">
          <label class="form-check-label" for="trangThai">Hoạt động</label>
        </div>
        <div class="mb-3 form-check">
          <input v-model="editedService.hien" type="checkbox" class="form-check-input" id="hienThi">
          <label class="form-check-label" for="hienThi">Hiển thị</label>
        </div>
        <div class="mb-3">
          <label class="form-label">Ảnh:</label>
          <div class="d-flex align-items-center">
            <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept="image/*"
                class="form-control"
                style="max-width: 300px;"
            >
            <button @click="fileInput?.click()" class="btn btn-outline-secondary ms-2">Chọn file</button>
          </div>
          <img
              v-if="editedService?.anh"
              :src="editedService.anh"
              alt="Ảnh dịch vụ"
              class="img-fluid mt-2"
              style="max-width: 200px;"
          >
        </div>

        <h3 class="mt-4">Tùy chọn dịch vụ:</h3>
        <div v-for="(tuyChon, index) in editedService.tuyChonDichVus" :key="tuyChon.id" class="card mt-3">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h5 class="mb-0">Tùy chọn {{ index + 1 }}</h5>
              <button @click="removeTuyChon(index)" class="btn btn-danger btn-sm">Xóa</button>
            </div>
            <input v-model="tuyChon.tentuychon" class="form-control mb-2" placeholder="Tên tùy chọn">
            <textarea v-model="tuyChon.mota" class="form-control mb-2" placeholder="Mô tả"></textarea>
            <div class="form-check mb-2">
              <input v-model="tuyChon.trangthai" type="checkbox" class="form-check-input"
                     :id="'trangThai' + tuyChon.id">
              <label class="form-check-label" :for="'trangThai' + tuyChon.id">Hoạt động</label>
            </div>

            <h6 class="mt-3">Tùy chọn cân nặng:</h6>
            <ul class="list-group">
              <li v-for="(canNang, canNangIndex) in tuyChon.tuyChonCanNangs" :key="canNang.id" class="list-group-item">
                <div class="row align-items-center">
                  <div class="col">
                    <input v-model="canNang.cannangmin" type="number" class="form-control" placeholder="Cân nặng min">
                  </div>
                  <div class="col">
                    <input v-model="canNang.cannangmax" type="number" class="form-control" placeholder="Cân nặng max">
                  </div>
                  <div class="col">
                    <input v-model="canNang.giatien" type="number" class="form-control" placeholder="Giá tiền">
                  </div>
                  <div class="col">
                    <div class="form-check">
                      <input v-model="canNang.trangthai" type="checkbox" class="form-check-input"
                             :id="'trangThaiCanNang' + canNang.id">
                      <label class="form-check-label" :for="'trangThaiCanNang' + canNang.id">Hoạt động</label>
                    </div>
                  </div>
                  <div class="col-auto">
                    <button @click="removeCanNang(tuyChon, canNangIndex)" class="btn btn-danger btn-sm">Xóa</button>
                  </div>
                </div>
              </li>
            </ul>
            <button @click="addCanNang(tuyChon)" class="btn btn-secondary mt-2">Thêm cân nặng</button>
          </div>
        </div>
        <button @click="addTuyChon" class="btn btn-secondary mt-3">Thêm tùy chọn dịch vụ</button>

        <div class="mt-4">
          <button @click="saveChanges" class="btn btn-success">Lưu thay đổi</button>
        </div>
      </div>
    </div>
    <p v-else class="alert alert-warning">Không tìm thấy thông tin dịch vụ</p>
  </div>
</template>

<style scoped>

</style>