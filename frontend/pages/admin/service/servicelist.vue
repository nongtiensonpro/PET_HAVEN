<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useServiceStore} from '~/stores/DichVuStores';
import {useNotificationStore} from '~/stores/useNotificationStore';
import {useUserStore} from '~/stores/user';
import {useToast} from 'vue-toastification';
import InputField from '~/components/InputField.vue';
import {useForm} from 'vee-validate';
import * as yup from 'yup';
import UpdateDichVu from "~/components/UpdateDichVu.vue";

definePageMeta({
  middleware: ['auth']
})

// Add these new refs for pagination
const currentPage = ref(1);
const itemsPerPage = 5;

// Compute paginated services
const paginatedServices = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return serviceStore.services.slice(start, end);
});

// Compute total pages
const totalPages = computed(() => {
  return Math.ceil(serviceStore.services.length / itemsPerPage);
});

// Methods for pagination
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const toast = useToast();
const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const user = useUserStore();

const service = ref({
  tendichvu: '',
  giatien: '',
  mota: '',
  trangthai: false,
  anh: null as File | null,
});

const anh = ref(null);

const selectedFile = ref<File | null>(null);

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    anh.value = URL.createObjectURL(selectedFile.value);
  }
};


const tendichvuRules = 'required|min:1';
const giatienRules = 'required|numeric|min:0';
const motaRules = 'required';

const schema = yup.object().shape({
  tendichvu: yup.string().required('Vui lòng nhập tên dịch vụ'),
  giatien: yup.number()
      .typeError('Giá tiền phải là một số')
      .required('Vui lòng nhập giá tiền')
      .positive('Giá tiền phải lớn hơn 0'),
  mota: yup.string().required('Vui lòng nhập mô tả')
});

const {handleSubmit, resetForm} = useForm({
  validationSchema: schema,
  initialValues: {
    tendichvu: '',
    giatien: '',
    mota: '',
    trangthai: false,
    anh: null,
  }
});
const submitForm = handleSubmit(async (formValues) => {
  await createService(formValues);
});

onMounted(() => {
  serviceStore.fetchServices();
});

const cleanService = () => {
  service.value.tendichvu = '';
  service.value.giatien = '';
  service.value.mota = '';
  service.value.trangthai = false;
  anh.value = '';
  resetForm()
  toast.success("Các ô input đã được làm sạch.");
};

const createService = async (formValues) => {
  try {
    const result = await serviceStore.addDichVu(formValues);
    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được tạo thành công', user.userInfo.name);
      toast.success('Dịch vụ đã được tạo thành công!');
      await serviceStore.fetchServices();
      cleanService();
      resetForm();
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`, user.userInfo.name);
      toast.error(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi tạo dịch vụ', user.userInfo.name);
    toast.error('Có lỗi xảy ra khi tạo dịch vụ.');
  }
};


const name = ref('');

const findServiceByName = async (name: string) => {
  if (!name || name.trim() === '') {
    toast.error('Tên dịch vụ không được để trống.');
    setTimeout(() => {
      serviceStore.fetchServices();
    }, 2000);
    return;
  }
  try {
    const result = await serviceStore.getDichVuByName(name);
    if (!result.status) {
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại sau.');
    setTimeout(() => {
      serviceStore.fetchServices();
    }, 2000);
  }
};
const deleteService = async (serviceId: String) => {
  try {
    const result = await serviceStore.deleteDichVu(serviceId);

    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được xóa thành công', user.userInfo.name);
      toast.success('Dịch vụ đã được xóa thành công!');
      await serviceStore.fetchServices();
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`, user.userInfo.name);
      toast.error(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi xóa dịch vụ', user.userInfo.name);
    toast.error('Có lỗi xảy ra khi xóa dịch vụ.');
  }
};

const updateTTService = async (serviceId: String) => {
  try {
    await serviceStore.updateTTDV(serviceId);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    toast.success('Dịch vụ đã được cập nhật thành công!');
    await serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    toast.error('Dịch vụ đã được cập nhật thất bại!');
  }
};

const updateTTHienService = async (serviceId: String) => {
  try {
    await serviceStore.updateTTHien(serviceId);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    toast.success('Dịch vụ đã được cập nhật thành công!');
    await serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    toast.error('Dịch vụ đã được cập nhật thất bại!');
  }
};
</script>

<template>
  <div class="container card bg-light p-4" style="border-radius: 25px">
    <div class="container">
      <div class="text fs-1">
        Tổng quan dịch vụ
      </div>
      <div class="row p-4">
        <div class="col-10">
          <div class="form-group">
            <div class="row">
              <div class="col-8">
                <input type="text" class="custom-button" name="" id="" style="min-width: 100% !important;" aria-describedby="helpId" placeholder="Tìm kiếm ?"
                       v-model="name">
              </div>
              <div class="col-2">
                <button type="button" class="custom-button" @click="findServiceByName(name)">
                  Tìm kiếm
                </button>
              </div>
              <div class="col-2">
                <button type="button" class="custom-button" @click="serviceStore.fetchServices()">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                       class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
                    <path
                        d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="col-2">
          <button type="button" class="custom-button" data-bs-toggle="modal" data-bs-target="#modal">
            Thêm dịch vụ
          </button>
          <div class="modal fade" id="modal" tabindex="-1" aria-labelledby="modalLabel" >
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h1 class="modal-title fs-5" id="modalLabel">Thêm dịch vụ:
                    {{ service.tendichvu }}</h1>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <form v-on:submit.prevent="submitForm">
                    <div class="row">
                      <div class="col-6">
                        <div v-if="anh===null">
                          <img :src="service.anh" class="card-img-top p-1" alt="Không có ảnh rùi">
                        </div>
                        <div v-else>
                          <img :src="anh" class="card-img-top p-1" alt="Không có ảnh rùi">
                        </div>
                        <input type="file" id="fileInput" accept="image/png, image/jpeg, image/gif"
                               @change="handleFileChange"/>
                      </div>
                      <div class="col-6">
                        <InputField
                            type="text"
                            name="tendichvu"
                            class="form-control"
                            label="Tên dịch vụ"
                            v-model="service.tendichvu"
                            :rules="tendichvuRules"
                        />
                        <InputField
                            type="number"
                            name="giatien"
                            class="form-control"
                            label="Giá tiền"
                            v-model="service.giatien"
                            :rules="giatienRules"
                        />
                        <div class="form-check form-control form-check-inline">
                          <label class="form-check-label">
                            <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai"
                                   :value="true"> Hoạt động
                            <br>
                            <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai"
                                   :value="false"> Không hoạt động
                          </label>
                        </div>
                      </div>
                      <div class="col-12">
                        <InputField
                            type="text"
                            name="mota"
                            class="form-control"
                            label="Mô tả"
                            v-model="service.mota"
                            :rules="motaRules"
                        />
                      </div>
                    </div>
                    <div class="p-12 p-1">
                      <div class="row">
                        <div class="col">
                          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                        </div>
                        <div class="col">
                          <button type="submit" class="custom-button">Thêm dịch vụ</button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button" @click="cleanService">Clean</button>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
    <table class="table">
      <thead>
      <tr>
        <th class="text-center" scope="col">STT</th>
        <th class="text-center" scope="col">Tên dịch vụ</th>
        <th class="text-center" scope="col">Trạng thái</th>
        <th class="text-center">Hành động</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(service, index) in serviceStore.services" :key="service.id">
        <th class="text-center" scope="row">{{ index + 1 }}</th>
        <td class="text-center">{{ service.tendichvu }}</td>
        <td class="text-center">{{ service.trangthai == true ? 'Hoạt động' : 'Không hoạt động' }}</td>
        <td>
          <div  class="row">
            <div class="col">
              <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteService(service.id)" data-bs-dismiss="modal">Xóa
              </button>
            </div>
            <div class="col">
              <button type="button" class="btn btn-sm btn-outline-warning" @click="updateTTService(service.id)" data-bs-dismiss="modal"  >
                <span v-if="service.trangthai">
                  Ẩn dịch vụ
                </span>
                <span v-else>
                  Hiện dịch vụ
                </span>
              </button>
            </div>
            <div class="col">
              <button type="button" class="btn btn-sm btn-outline-secondary" @click="updateTTHienService(service.id)" data-bs-dismiss="modal" >
                <span v-if="service.hien">
                  Ẩn khỏi màn hình chính
                </span>
                <span v-else>
                  Hiện ở màn hình chính
                </span>
              </button>
            </div>
            <div class="col">
              <update-dich-vu :service="service"/>
            </div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
    <div class="pagination d-flex justify-content-center align-items-center mt-3">
      <div class="row">
        <div class="col">
          <button @click="prevPage" :disabled="currentPage === 1" class="custom-button">Trước</button>
        </div>
        <div class="col">
          <span class="text fs-5">Trang {{ currentPage }} / {{ totalPages }}</span>
        </div>
        <div class="col">
          <button @click="nextPage" :disabled="currentPage === totalPages" class="custom-button">Sau</button>
        </div>
      </div>



    </div>

  </div>
</template>

<style scoped>

</style>