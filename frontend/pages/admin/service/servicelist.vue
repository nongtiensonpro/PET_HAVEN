<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useServiceStore } from '~/stores/DichVuStores';
import { useNotificationStore } from '~/stores/useNotificationStore';
import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';

definePageMeta({
  middleware: ['auth']
})

const currentPage = ref(1);
const itemsPerPage = 5;
const name = ref('');

const toast = useToast();
const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const user = useUserStore();

const paginatedServices = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return serviceStore.services.slice(start, end);
});

const totalPages = computed(() => Math.ceil(serviceStore.services.length / itemsPerPage));

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

onMounted(serviceStore.fetchServices);

const findServiceByName = async () => {
  if (!name.value.trim()) {
    toast.error('Tên dịch vụ không được để trống.');
    setTimeout(serviceStore.fetchServices, 2000);
    return;
  }
  try {
    const result = await serviceStore.getDichVuByName(name.value);
    if (!result.status) toast.error(result.message);
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại sau.');
    setTimeout(serviceStore.fetchServices, 2000);
  }
};

const handleServiceAction = async (action, serviceId, successMessage, errorMessage) => {
  try {
    await action(serviceId);
    notificationStore.addNotification(successMessage, user.userInfo.name);
    toast.success(successMessage);
    await serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification(errorMessage, user.userInfo.name);
    toast.error(errorMessage);
  }
};

const deleteService = (serviceId) => handleServiceAction(
  serviceStore.deleteDichVu,
  serviceId,
  'Dịch vụ đã được xóa thành công!',
  'Có lỗi xảy ra khi xóa dịch vụ.'
);

const updateTTService = (serviceId) => handleServiceAction(
  serviceStore.updateTTDV,
  serviceId,
  'Dịch vụ đã được cập nhật thành công!',
  'Dịch vụ đã được cập nhật thất bại!'
);

const updateTTHienService = (serviceId) => handleServiceAction(
  serviceStore.updateTTHien,
  serviceId,
  'Dịch vụ đã được cập nhật thành công!',
  'Dịch vụ đã được cập nhật thất bại!'
);

const chiTietDV = (serviceId) => navigateTo(`/admin/service/${serviceId}`);

function themDichVu() {
  navigateTo('/admin/service/themdichvu');
}
</script>

<template>
  <div class="container card bg-light p-4" style="border-radius: 25px">
    <div class="container">
      <h1 class="fs-1">Tổng quan dịch vụ</h1>
      <div class="row p-4">
        <div class="col-10">
          <div class="form-group">
            <div class="row">
              <div class="col-6">
                <input v-model="name" type="text" class="custom-button" style="min-width: 100% !important;" placeholder="Nhập tên dịch vụ cần tìm kiếm ?">
              </div>
              <div class="col-2">
                <button @click="findServiceByName" type="button" class="custom-button">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
                    <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
                  </svg>
                </button>
              </div>
              <div class="col-2">
                <button type="button" class="custom-button" @click="themDichVu">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-plus" viewBox="0 0 16 16">
                    <path d="M8.5 6a.5.5 0 0 0-1 0v1.5H6a.5.5 0 0 0 0 1h1.5V10a.5.5 0 0 0 1 0V8.5H10a.5.5 0 0 0 0-1H8.5z"/>
                    <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2zm10-1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1"/>
                  </svg>
                </button>
              </div>
              <div class="col-2">
                <button @click="serviceStore.fetchServices" type="button" class="custom-button">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
                    <path d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
                  </svg>
                </button>
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
          <th class="text-center" scope="col">Trạng thái</th>
          <th class="text-center">Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(service, index) in paginatedServices" :key="service.id">
          <th class="text-center" scope="row">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</th>
          <td class="text-center">{{ service.tendichvu }}</td>
          <td class="text-center">{{ service.trangthai ? 'Hoạt động' : 'Không hoạt động' }}</td>
          <td>
            <div class="row">
              <div class="col">
                <button @click="deleteService(service.id)" type="button" class="btn btn-sm btn-outline-danger">Xóa</button>
              </div>
              <div class="col">
                <button @click="updateTTService(service.id)" type="button" class="btn btn-sm btn-outline-warning">
                  {{ service.trangthai ? 'Ẩn dịch vụ' : 'Hiện dịch vụ' }}
                </button>
              </div>
              <div class="col">
                <button @click="updateTTHienService(service.id)" type="button" class="btn btn-sm btn-outline-secondary">
                  {{ service.hien ? 'Ẩn khỏi màn hình chính' : 'Hiện ở màn hình chính' }}
                </button>
              </div>
              <div class="col">
                <button @click="chiTietDV(service.id)" type="button" class="btn btn-sm btn-outline-success m-1">Chi tiết</button>
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