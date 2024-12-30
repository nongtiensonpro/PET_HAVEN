<script setup lang="ts">
import Swal from "sweetalert2";
import {useQuanLyLichHenAdminStore} from "~/stores/QuanLyLichHenAdmin";
import {ref, onMounted, computed} from "vue";
import type {Lichhen} from "~/models/LichSuDatLich";
import {useToast} from 'vue-toastification';
import {useRoute} from 'vue-router';

const route = useRoute();
const toast = useToast();
const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const selectedLichHen = ref<Lichhen | null>(null);
const selectedTrangThai = ref<number | null>(null);
const listLichHen = ref<Lichhen[]>([]);

onMounted(async () => {
  const id = Number(route.params.id);
  listLichHen.value = await useQuanLyAdmin.fetchHoaDonKhachHangs();
  if (listLichHen.value.length > 0) {
    selectedLichHen.value = listLichHen.value[0];
    selectedTrangThai.value = selectedLichHen.value.trangthai;
  }
});

const getTrangThai = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    0: 'Thành công',
    1: 'Đã đổi',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công',
    7: 'Đã hoàn tiền',
    8: 'Chờ sử dụng'
  };
  return trangThaiMap[status] || 'Không xác định';
};

async function saveTrangThai() {
  if (!selectedLichHen.value || selectedTrangThai.value === null) return;

  const result = await Swal.fire({
    title: 'Xác nhận',
    text: 'Bạn có muốn thay đổi trạng thái không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không',
  });
  if (result.isConfirmed) {
    try {
      await useQuanLyAdmin.thayDoiTrangThai(selectedLichHen.value.id, selectedTrangThai.value);
      toast.success('Thay đổi trạng thái thành công', {timeout: 3000});
      listLichHen.value = await useQuanLyAdmin.fetchHoaDonKhachHangs();
      return navigateTo('/admin/quanlylichhen')
    } catch (error) {
      toast.error(`Không thể thay đổi trạng thái. Vui lòng thử lại. ${error}`, {timeout: 3000});
    }
  }
}

const formattedDate = computed(() => {
  if (selectedLichHen.value) {
    const date = new Date(selectedLichHen.value.date);
    return date.toLocaleDateString('vi-VN');
  }
  return '';
});

</script>

<template>
  <div v-if="selectedLichHen">
    <div class="row">
      <div class="col">
        <h2>Thông tin lịch hẹn</h2>
        <p><strong>ID:</strong> {{ selectedLichHen.id }}</p>
        <p><strong>Email người đặt:</strong> {{ selectedLichHen.emailNguoiDat }}</p>
        <p><strong>Ngày hẹn:</strong> {{ formattedDate }}</p>
        <p><strong>Trạng thái hiện tại:</strong> {{ getTrangThai(selectedLichHen.trangthai) }}</p>

        <h3>Thông tin thú cưng</h3>
        <p><strong>Tên:</strong> {{ selectedLichHen.thucung.ten }}</p>
        <p><strong>Cân nặng:</strong> {{ selectedLichHen.thucung.cannang }} kg</p>
        <p><strong>Tuổi:</strong> {{ selectedLichHen.thucung.tuoi }} tuổi</p>
        <p><strong>Giống:</strong> {{ selectedLichHen.thucung.giong }}</p>
      </div>
      <div class="col">
        <div class="form-floating mt-3">
          <select class="form-select" id="floatingSelect" v-model="selectedTrangThai"
                  :aria-label="'Trạng thái bây giờ là ' + getTrangThai(selectedLichHen.trangthai)">
            <option value="0">Thành công</option>
            <option value="1">Đã đổi</option>
            <option value="2">Đã hủy</option>
            <option value="3">Chờ thanh toán</option>
            <option value="4">Chờ xác nhận</option>
            <option value="5">Rỗng</option>
            <option value="6">Thanh toán thành công</option>
            <option value="7">Đã hoàn tiền</option>
            <option value="8">Chờ sử dụng</option>
          </select>
          <label for="floatingSelect">Chọn trạng thái mới</label>
        </div>
        <div class="row">
          <div class="col">
            <button type="button" class="custom-button" @click="saveTrangThai">Lưu trạng thái</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    Đang tải dữ liệu...
  </div>
</template>

<style scoped>

</style>