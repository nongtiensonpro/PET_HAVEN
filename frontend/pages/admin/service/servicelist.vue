<script setup lang="ts">
import {useServiceStore} from '~/stores/DichVuStores';
import {onMounted} from 'vue';

const serviceStore = useServiceStore();

onMounted(() => {
  serviceStore.fetchServices();
});
</script>

<template>
  <div class="container">
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
          <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#exampleModal">
            Chi tiết
          </button>

          <!-- Modal -->
          <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
               aria-hidden="true">
            <div class="modal-dialog">
              <div class="modal-content">
                <div class="modal-header">
                  <h1 class="modal-title fs-5" id="exampleModalLabel">Thông tin chi tiết dịch vụ: {{service.tendichvu}}</h1>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                  <div class="row">
                    <div class="col-6">
                      <img src="~/assets/image/catservice.jpg" class="card-img-top p-4" alt="...">
                    </div>
                    <div class="col-6">
                      <form>
                        <label for="exampleFormControlInput1" class="form-label">Tên dịch vụ</label>
                        <input type="text" class="form-control" id="exampleFormControlInput1" v-model="service.tendichvu"
                               placeholder="Tên dịch vụ">
                        <label for="exampleFormControlInput2" class="form-label">Giá tiền</label>
                        <input type="text" class="form-control" id="exampleFormControlInput2" v-model="service.giatien"
                               placeholder="Nhập số điện thoại">
                        <label for="exampleFormControlInput2" class="form-label">Mô tả</label>
                        <input type="text" class="form-control" id="exampleFormControlInput2" v-model="service.mota"
                               placeholder="Nhập số điện thoại">
                      </form>
                    </div>
                  </div>
                </div>
                <div class="p-4">
                  <div class="row">
                    <div class="col">
                      <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                    </div>
                    <div class="col">
                      <button type="button" class="custom-button">Lưu thay đổi</button>
                    </div>
                    <div class="col">
                      <button type="button" class="custom-button">Ẩn dịch vụ</button>
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

.service-image {
  width: 100px;
  height: auto;
}
</style>
