<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCaLichHenStore } from '~/stores/QuanLyCaLichHen'
import type CaLichHens from '~/models/CaHen'
import Swal from "sweetalert2";
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const caLichHenStore = useCaLichHenStore()

const id = route.params.id as string
const caLichHen = ref<CaLichHens | null>(null)

// Lấy dữ liệu ca lịch hẹn từ store
async function fetchCaLichHen() {
  try {
    await caLichHenStore.fethcCaLichHen()
    caLichHen.value = caLichHenStore.calichhen.find(ca => ca.id === Number(id)) || null
    if (!caLichHen.value) {
      toast.error('Không tìm thấy ca lịch hẹn!')
      router.push('/admin/quanlyca')
    }
  } catch (e) {
    toast.error('Lỗi khi tải dữ liệu!')
    router.push('/admin/quanlyca')
  }
}

onMounted(() => {
  fetchCaLichHen()
})

async function capNhatCa() {
  if (!caLichHen.value) {
    toast.error('Không có dữ liệu ca lịch hẹn!')
    return
  }

  const result = await Swal.fire({
    title: 'Xác nhận',
    text: `Bạn có chắc chắn muốn cập nhật thời gian này thành ${caLichHen.value.thoigianca}?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });

  if (result.isConfirmed) {
    try {
      await caLichHenStore.capNhatCaLichHen(caLichHen.value)
      toast.success('Cập nhật ca lịch hẹn thành công!')
      router.push('/admin/quanlyca')
    } catch (e) {
      toast.error('Cập nhật ca lịch hẹn thất bại!')
    }
  }
}

function quayLai() {
  router.push('/admin/quanlyca')
}
</script>

<template>
  <div class="container mt-4" v-if="caLichHen">
    <div class="card p-4" style="border-radius: 15px">
      <h2 class="mb-4">Cập nhật thời gian ca số {{ id }}</h2>
      <form @submit.prevent="capNhatCa">
        <div class="mb-3">
          <label for="thoigianca" class="form-label">Thời gian hẹn</label>
          <input type="time" class="form-control" id="thoigianca" v-model="caLichHen.thoigianca" required>
        </div>
        <div class="d-flex justify-content-between mt-4">
          <button type="button" class="custom-button" @click="quayLai">
            <i class="bi bi-arrow-left"></i> Quay lại
          </button>
          <button type="submit" class="custom-button">
            <i class="bi bi-save"></i> Lưu thay đổi
          </button>
        </div>
      </form>
    </div>
  </div>
</template>