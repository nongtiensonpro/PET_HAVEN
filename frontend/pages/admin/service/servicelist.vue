<script setup lang="ts">
import {ref, onMounted} from 'vue';
import {useServiceStore} from '~/stores/DichVuStores';
import {useNotificationStore} from '~/stores/useNotificationStore';
import DichVu from '~/models/DichVu'
import {useUserStore} from '~/stores/user'

const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const user = useUserStore();

// Define a reactive service object
const service = DichVu

onMounted(() => {
  serviceStore.fetchServices();
});
const cleanService = () => {
  service.value.tendichvu = '';
  service.value.giatien = '';
  service.value.mota = '';
};
const createService = async () => {
  try {
    const result = await serviceStore.createDichVu(service.value);

    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được tạo thành công', user.userInfo.name);
      alert('Dịch vụ đã được tạo thành công!');
      serviceStore.fetchServices();
      // Reset the service object after successful creation
      service.value = {tendichvu: '', giatien: '', mota: ''};
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`, user.userInfo.name);
      alert(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi tạo dịch vụ', user.userInfo.name);
    alert('Có lỗi xảy ra khi tạo dịch vụ.');
  }
};


const saveService = async (service) => {
  try {
    await serviceStore.updateDichVu(service);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    alert('Dịch vụ đã được lưu thành công!');
    serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    console.error('Lỗi khi lưu dịch vụ:', error);
    alert('Có lỗi xảy ra khi lưu dịch vụ.');
  }
};

const deleteService = async (serviceId) => {
  try {
    const result = await serviceStore.deleteDichVu(serviceId);

    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được xóa thành công', user.userInfo.name);
      alert('Dịch vụ đã được xóa thành công!');
      await serviceStore.fetchServices();
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`, user.userInfo.name);
      alert(`Có lỗi xảy ra khi xóa dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi xóa dịch vụ', user.userInfo.name);
    alert('Có lỗi xảy ra khi xóa dịch vụ.');
  }
};
</script>

<template>
  <div class="container p-4">
    <div class="container">
      <div class="row">
        <div class="col-10">
          <div class="form-group">
           <div class="row">
             <div class="col-10">
               <label for="">Tìm kiếm</label>
               <input type="text" class="form-control" name="" id="" aria-describedby="helpId" placeholder="">
             </div>
             <div class="col-2">
               <button type="button" class="custom-button">
                 Tìm kiếm
               </button>
             </div>
           </div>
          </div>
        </div>
        <div class="col-2">
          <!-- Button trigger modal -->
          <button type="button" class="custom-button" data-bs-toggle="modal" :data-bs-target="'#modal' + service.id">
            Thêm dịch vụ
          </button>
          <!-- Modal -->
          <div class="modal fade" :id="'modal' + service.id" tabindex="-1" :aria-labelledby="'modalLabel' + service.id"
               aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h1 class="modal-title fs-5" :id="'modalLabel' + service.id">Thêm dịch vụ:
                    {{ service.tendichvu }}</h1>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="row">
                    <div class="col-6">
                      <img src="~/assets/image/catservice.jpg" class="card-img-top p-1" alt="...">
                    </div>
                    <div class="col-6">
                          <label for="tendichvu" class="form-label">Tên dịch vụ</label>
                          <input type="text" class="form-control" id="tendichvu" v-model="service.tendichvu" placeholder="Tên dịch vụ">
                          <label for="giatien" class="form-label">Giá tiền</label>
                          <input type="text" class="form-control" id="giatien" v-model="service.giatien" placeholder="Giá tiền">
                          <label for="mota" class="form-label">Mô tả</label>
                    </div>
                    <div class="col-12">
                      <textarea class="form-control" id="mota" v-model="service.mota" rows="5" placeholder="Mô tả"></textarea>
                    </div>
                  </div>
                </div>
                <div class="p-4">
                  <div class="row">
                    <div class="col">
                      <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                    </div>
                    <div class="col">
                      <button type="button" class="custom-button" @click="createService(service)">Thêm dịch vụ</button>
                    </div>
                    <div class="col">
                      <button type="button" class="custom-button" @click="cleanService">Clean</button>
                    </div>
                  </div>
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
        <th scope="col">STT</th>
        <th scope="col">Tên dịch vụ</th>
        <th scope="col">Giá tiền</th>
        <th>Hành động</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(service, index) in serviceStore.services" :key="service.id">
        <th scope="row">{{ index + 1 }}</th>
        <td>{{ service.tendichvu }}</td>
        <td>{{ service.giatien.toLocaleString() }} VND</td>
        <td>
          <!-- Button trigger modal -->
          <div class="row">
            <div class="col">
              <button type="button" class="nav-link" @click="deleteService(service.id)" data-bs-dismiss="modal">Xóa</button>
            </div>
            <div class="col">
              <button type="button" class="nav-link" data-bs-toggle="modal" :data-bs-target="'#modal' + service.id">
                Chi tiết
              </button>

              <!-- Modal -->
              <div class="modal fade" :id="'modal' + service.id" tabindex="-1" :aria-labelledby="'modalLabel' + service.id"
                   aria-hidden="true">
                <div class="modal-dialog">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h1 class="modal-title fs-5" :id="'modalLabel' + service.id">Thông tin chi tiết dịch vụ:
                        {{ service.tendichvu }}</h1>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <div class="row">
                        <div class="col-6">
                          <img src="~/assets/image/catservice.jpg" class="card-img-top p-1" alt="...">
                        </div>
                        <div class="col-6">
                          <label for="tendichvu" class="form-label">Tên dịch vụ</label>
                          <input type="text" class="form-control" id="tendichvu" v-model="service.tendichvu"
                                 placeholder="Tên dịch vụ">
                          <label for="giatien" class="form-label">Giá tiền</label>
                          <input type="text" class="form-control" id="giatien" v-model="service.giatien"
                                 placeholder="Giá tiền">
                          <label for="mota" class="form-label">Mô tả</label>
                        </div>
                        <div class="col-12">
                          <textarea class="form-control" id="mota" v-model="service.mota" rows="5" placeholder="Mô tả"></textarea>
                        </div>
                      </div>
                    </div>
                    <div class="p-4">
                      <div class="row">
                        <div class="col">
                          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button" @click="saveService(service)" data-bs-dismiss="modal">
                            Lưu thay đổi
                          </button>
                        </div>
                        <div class="col">
                          <button type="button" class="custom-button">Ẩn dịch vụ</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.container {
  margin-top: 20px;
}

.table {
  width: 100%;
  margin: 0 auto;
}
</style>