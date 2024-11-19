<script setup lang="ts">
import { onMounted, ref } from "vue";
import {useCaLichHenStore} from "~/stores/QuanLyCaLichHen"
import type CaLichHen from "~/models/CaHen";
import ThemCa from '~/components/ThemCa.vue'
const caLichHenStore = useCaLichHenStore();
const caLichHens = ref<CaLichHen[]>([]);
import CapNhatCaHen from '~/components/CapNhatCaLichHen.vue'

const fetchCaLichHens = async () => {
   caLichHens.value = await caLichHenStore.fethcCaLichHen();
}

onMounted(() => {
  fetchCaLichHens()
})

function capNhatTrangThai(ca: CaLichHen) {
  caLichHenStore.capNhatTrangThaiCa(ca);
  fetchCaLichHens();
}

function capNhat(ca: CaLichHen) {
  caLichHenStore.capNhatCaLichHen(ca);
  fetchCaLichHens();
}

</script>

<template>
  <div class="row">
    <div class="col-2">
        <ThemCa />
    </div>
  </div>
  <div class="container">
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên ca</th>
          <th>Thời gian ca</th>
          <th>Trạng thái</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>

        <tr v-for="ca in caLichHens" :key="ca.id">
          <td>{{ ca.id }}</td>
          <td>{{ ca.tenca }}</td>
          <td>{{ ca.thoigianca }}</td>
          <td>
            <span :class="ca.trangthai ? 'active' : 'inactive'">
              {{ ca.trangthai ? 'Hoạt động' : 'Không hoạt động' }}
            </span>
          </td>
          <td>
            <button type="button" @click="capNhatTrangThai(ca)" class="btn btn-sm btn-outline-warning m-1">Cập nhật
              trạng thái</button>
            <button type="button" class="btn btn-sm btn-outline-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal">
              Cập nhật Ne 123sdfsdfds
            </button>

            <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    {{ca}}
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary">Save changes</button>
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
.ca-lich-hen-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.ca-lich-hen-table th, .ca-lich-hen-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.ca-lich-hen-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.ca-lich-hen-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.ca-lich-hen-table tr:hover {
  background-color: #f5f5f5;
}

.active {
  color: green;
  font-weight: bold;
}

.inactive {
  color: red;
}
</style>