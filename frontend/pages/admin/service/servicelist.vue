<script setup lang="ts">
import { onMounted} from 'vue';
import {useServiceStore} from '~/stores/DichVuStores';
import {useNotificationStore} from '~/stores/useNotificationStore';
import {useUserStore} from '~/stores/user'
import { useToast } from 'vue-toastification';


definePageMeta({
  middleware: 'auth',
});
const toast = useToast();
const serviceStore = useServiceStore();
const notificationStore = useNotificationStore();
const user = useUserStore();

const service = ref({
  tendichvu: '',
  giatien: '',
  mota: '',
  trangthai: false
});

onMounted(() => {
  serviceStore.fetchServices();
});

const cleanService = () => {
  service.value.tendichvu = '';
  service.value.giatien = '';
  service.value.mota = '';
  service.value.trangthai = false;
  toast.success("Các ô input đã được làm sạch.");
};

const name = '';


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
    if(result.status) {

    }else{
      toast.error(result.message);
    }
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại sau.');
    setTimeout(() => {
      serviceStore.fetchServices();
    }, 2000);
  }
};

const createService = async () => {
  try {
    const newService = {
      tendichvu: service.value.tendichvu,
      giatien: service.value.giatien,
      mota: service.value.mota
    }; // Tạo đối tượng mới từ service.value

    const result = await serviceStore.addDichVu(newService);

    if (result.success) {
      notificationStore.addNotification('Dịch vụ đã được tạo thành công', user.userInfo.name);
      toast.success('Dịch vụ đã được tạo thành công!');
      serviceStore.fetchServices();
      cleanService(); // Gọi hàm cleanService để reset lại form
    } else {
      notificationStore.addNotification(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`, user.userInfo.name);
      toast.error(`Có lỗi xảy ra khi tạo dịch vụ: ${result.message}`);
    }
  } catch (error) {
    notificationStore.addNotification('Có lỗi xảy ra khi tạo dịch vụ', user.userInfo.name);
    console.error('Lỗi khi tạo dịch vụ:', error);
    toast.error('Có lỗi xảy ra khi tạo dịch vụ.');
  }
};



const saveService = async (service) => {
  try {
    await serviceStore.updateDichVu(service);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    toast.success('Dịch vụ đã được lưu thành công!');
    serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    console.error('Lỗi khi lưu dịch vụ:', error);
    toast.error('Có lỗi xảy ra khi lưu dịch vụ.');
  }
};

const deleteService = async (serviceId) => {
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

const updateTTService = async (serviceId) => {
  try {
    await serviceStore.updateTTDV(serviceId);
    notificationStore.addNotification("Dịch vụ đã được cập nhật thành công!", user.userInfo.name);
    toast.success('Dịch vụ đã được cập nhật thành công!');
    serviceStore.fetchServices();
  } catch (error) {
    notificationStore.addNotification("Dịch vụ đã được cập nhật thất bại!", user.userInfo.name);
    toast.error('Dịch vụ đã được cập nhật thất bại!');
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
             <div class="col-8">
               <label for="">Tìm kiếm</label>
               <input type="text" class="form-control" name="" id="" aria-describedby="helpId" placeholder="" v-model="name">
             </div>
             <div class="col-2">
               <button type="button" class="custom-button" @click="findServiceByName(name)">
                 Tìm kiếm
               </button>
             </div>
             <div class="col-2">
               <button type="button" class="custom-button" @click="serviceStore.fetchServices()">
                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
                   <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
                   <path d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
                 </svg>
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
                      <div class="form-check form-check-inline">
                        <label class="form-check-label">
                          <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai" :value="true"> Hoạt động<br>
                          <input class="form-check-input" type="radio" name="trangthai" v-model="service.trangthai" :value="false"> Không hoạt động
                        </label>
                      </div>
                    </div>
                    <div class="col-12">
                      <label for="mota" class="form-label">Mô tả :</label>
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
        <th scope="col">Trạng thái</th>
        <th>Hành động</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(service, index) in serviceStore.services" :key="service.id">
        <th scope="row">{{ index + 1 }}</th>
        <td>{{ service.tendichvu }}</td>
        <td>{{ service.giatien.toLocaleString() }} VND</td>
        <td>{{ service.trangthai == true ? 'Hoạt động' : 'Không hoạt động' }}</td>
        <td>
          <!-- Button trigger modal -->
          <div class="row">
            <div class="col">
              <button type="button" class="nav-link" @click="deleteService(service.id)" data-bs-dismiss="modal">Xóa</button>
            </div>
            <div class="col">
              <button type="button" class="nav-link" @click="updateTTService(service.id)" data-bs-dismiss="modal">
                <div v-if="service.trangthai">
                  Ẩn dịch vụ
                </div>
                <div v-else>
                  Hiện dịch vụ
                </div>
              </button>
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
                          <label  class="form-label">Trạng thái : {{service.trangthai==true?'Hoạt động':'Không hoạt động'}}</label>
                        </div>
                        <div class="col-12">
                          <label for="mota" class="form-label">Mô tả</label>
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
                          <button type="button" class="custom-button" @click="updateTTService(service.id)">
                            <p v-if="service.trangthai">
                              Ẩn dịch vụ
                            </p>
                            <p v-else>
                              Hiện dịch vụ
                            </p>
                          </button>
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