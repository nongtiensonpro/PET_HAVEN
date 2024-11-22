<script setup lang="ts">
import { onMounted, ref } from "vue";
import {useCaLichHenStore} from "~/stores/QuanLyCaLichHen"
import type NgayNghi from "~/models/NgayNghi";
import type CaLichHen from "~/models/CaHen";
import ThemCa from '~/components/ThemCa.vue'

const caLichHenStore = useCaLichHenStore();
const caLichHens = ref<CaLichHen[]>([]);

const listNgayNghi = ref<NgayNghi[]>([]);

import CapNhatCaHen from '~/components/CapNhatCaLichHen.vue'
import CapNhatNgayNghi from "~/components/CapNhatNgayNghi.vue";
import Swal from 'sweetalert2';
import { useToast } from 'vue-toastification'

const toast = useToast();
const fetchCaLichHens = async () => {
   caLichHens.value = await caLichHenStore.fethcCaLichHen();
}


const fetchNgayNghi = async () => {
  const ngayNghiData = await caLichHenStore.fetchNgayNghi();
  console.log(ngayNghiData+'MIU MIU');
  if (Array.isArray(ngayNghiData)) {
    listNgayNghi.value = ngayNghiData;
  } else {
    listNgayNghi.value = [];
  }
}
onMounted(() => {
  fetchCaLichHens()
  fetchNgayNghi()
})

async function capNhatTrangThai(ca: CaLichHen) {
  const result = await Swal.fire({
    title: 'Xác nhận',
   text: `Bạn có chắc chắn ${ca.trangthai ? 'ẩn' : 'hiện'} ca "${ca.tenca}" chứ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });

  if (result.isConfirmed) {
    try {
      caLichHenStore.capNhatTrangThaiCa(ca);
      fetchCaLichHens();
      setTimeout(() => {
        fetchCaLichHens();
      }, 100);
      toast.success('Cập nhật ca lịch hẹn thành công!')
    }catch (e) {
      toast.success('Cập nhật ca lịch hẹn thất bại!')

    }
  }
}

function capNhat(ca: CaLichHen) {
  caLichHenStore.capNhatCaLichHen(ca);
  fetchCaLichHens();
}

function lammoi() {
  fetchCaLichHens();
  fetchNgayNghi();
}

</script>

<template>

    <div class="">
      <div class="card-body">
        <div class="row">
        <div class="col-2">
          <ThemCa />
        </div>
        <div class="col-3">
          <CapNhatNgayNghi />
        </div>
          <div class="col-3">
            <button type="button" @click="lammoi()" class="btn btn-sm btn-success">Làm mới</button>
          </div>
        </div>
      </div>
  </div>
  <div class="row">


  <div class="col container">
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
            <button type="button" @click="capNhatTrangThai(ca)" class="btn btn-sm btn-outline-warning m-1">{{ca.trangthai?'Ẩn ca':'Hiện ca'}}</button>
            <CapNhatCaHen :ca="ca" @cap-nhat="capNhat">
            </CapNhatCaHen>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div class="col container">
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Ngày nghỉ</th>
        <th>Trạng thái</th>
        <th>Thao tác</th>
      </tr>
      </thead>
      <tbody>

      <tr v-for="ngay in listNgayNghi" :key="ngay.id">
        <td>{{ ngay.id }}</td>
        <td>{{ ngay.ngaynghi }}</td>
        <td>{{ ngay.trangthai?'Hoạt động':'Không hoạt động' }}</td>
        <td>
          <button type="button"  class="btn btn-sm btn-outline-warning m-1">Hủy ngày nghỉ</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
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