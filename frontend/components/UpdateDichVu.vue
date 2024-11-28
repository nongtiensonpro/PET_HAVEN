<script setup lang="ts">
import { ref, computed } from 'vue';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import type  DichVu  from "~/models/DichVu";
import { useServiceStore } from '~/stores/DichVuStores';
import { useNotificationStore } from "~/stores/useNotificationStore";
import { useToast } from 'vue-toastification';

const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const toast = useToast();

const props = defineProps<{
  service: DichVu
}>();

const emit = defineEmits(['updateService']);

const schema = yup.object({
  tendichvu: yup.string().required('Tên dịch vụ là bắt buộc').min(3, 'Tên dịch vụ phải có ít nhất 3 ký tự'),
  giatien: yup.number().required('Giá tiền là bắt buộc').min(0, 'Giá tiền không thể âm'),
  mota: yup.string().required('Mô tả là bắt buộc').min(10, 'Mô tả phải có ít nhất 10 ký tự'),
});

const { handleSubmit, errors } = useForm({
  validationSchema: schema,
  initialValues: { ...props.service },
});

const { value: tendichvu } = useField('tendichvu');
const { value: giatien } = useField('giatien');
const { value: mota } = useField('mota');

const updatedService = computed(() => ({
  ...props.service,
  tendichvu: tendichvu.value,
  giatien: giatien.value,
  mota: mota.value,
}));

const selectedFile = ref<File | null>(null);
const anh = ref<string | null>(null);

const isActive = computed(() => updatedService.value.trangthai);

const updateService = () => {
  emit('updateService', updatedService.value);
};

const saveService = handleSubmit(async (values) => {
  try {
    const dataToUpdate = { ...updatedService.value, ...values };
    const result = await serviceStore.updateDichVu(dataToUpdate);

    if (result.success) {
      notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", "success");
      toast.success('Dịch vụ đã được lưu thành công!');
      await serviceStore.fetchServices();
      resetForm();
    } else {
      toast.error('Cập nhật dịch vụ thất bại!');
    }
  } catch (error) {
    toast.error('Cập nhật dịch vụ thất bại!');
  }
});

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    anh.value = URL.createObjectURL(selectedFile.value);
  }
};

const updateTTService = async () => {
  try {
    await serviceStore.updateTTDV(updatedService.value.id);
    notificationStore.addNotification("Trạng thái dịch vụ đã được cập nhật thành công!", "success");
    toast.success('Trạng thái dịch vụ đã được cập nhật thành công!');
    updatedService.value.trangthai = !updatedService.value.trangthai;
    await serviceStore.fetchServices();
  } catch (error) {
    handleError('Cập nhật trạng thái dịch vụ thất bại', error);
  }
};

const resetForm = () => {
  selectedFile.value = null;
  anh.value = null;
};

const handleError = (message: string, error?: any) => {
  notificationStore.addNotification(message, "error");
  toast.error(message);
  if (error) console.error(message, error);
};
</script>

<template>
  <div>
    <button type="button" class="nav-link" data-bs-toggle="modal" :data-bs-target="'#modal' + updatedService.id">
      Chi tiết
    </button>
    <div class="modal fade" :id="'modal' + updatedService.id" tabindex="-1"
         :aria-labelledby="'modalLabel' + updatedService.id"
         aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" :id="'modalLabel' + updatedService.id">
              Thông tin chi tiết dịch vụ: {{ updatedService.tendichvu }}
            </h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveService">
              <div class="row">
                <div class="col-6">
                  <img :src="anh || updatedService.anh" class="card-img-top p-1" alt="Service Image">
                  <input type="file" id="fileUpdate" accept="image/png, image/jpeg, image/gif"
                         @change="handleFileChange"/>
                </div>
                <div class="col-6">
                  <div class="form-group">
                    <label for="tendichvu">Tên dịch vụ</label>
                    <input type="text" id="tendichvu" class="form-control" v-model="tendichvu">
                    <span class="text-danger">{{ errors.tendichvu }}</span>
                  </div>
                  <div class="form-group">
                    <label for="giatien">Giá tiền</label>
                    <input type="number" id="giatien" class="form-control" v-model="giatien">
                    <span class="text-danger">{{ errors.giatien }}</span>
                  </div>
                  <div class="form-group">
                    <label>Trạng thái</label>
                    <p>{{ isActive ? 'Hoạt động' : 'Không hoạt động' }}</p>
                  </div>
                </div>
                <div class="col-12">
                  <label for="mota">Mô tả</label>
                  <textarea id="mota" class="form-control" v-model="mota"></textarea>
                  <span class="text-danger">{{ errors.mota }}</span>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button type="submit" class="btn btn-primary" @click="saveService">
              Lưu thay đổi
            </button>
            <button type="button" class="btn btn-warning" @click="updateTTService">
              {{ isActive ? 'Ẩn dịch vụ' : 'Hiện dịch vụ' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-group {
  margin-bottom: 1rem;
}
.text-danger {
  color: #dc3545;
  font-size: 0.875em;
}
</style>