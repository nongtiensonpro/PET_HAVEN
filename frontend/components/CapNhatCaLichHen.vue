<script setup lang="ts">
import { useCaLichHenStore } from '~/stores/QuanLyCaLichHen'
import type CaLichHens from '~/models/CaHen'

const props = defineProps<{
  ca: CaLichHens
}>()

const emit = defineEmits(['cap-nhat'])

const caLichHenStore = useCaLichHenStore()


async function capNhatCa(ca :CaLichHens) {

  await caLichHenStore.capNhatCaLichHen(ca)

  emit('cap-nhat')
}
</script>

<template>
  <button type="button" class="btn btn-sm btn-outline-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal123">
    Cập nhật
  </button>

  <div class="modal fade" id="exampleModal123" tabindex="-1" aria-labelledby="exampleModal123" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Cập nhật ca lịch hẹn</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="capNhatCa(ca)">
            <div class="mb-3">
              <label for="tenca" class="form-label">Tên ca</label>
              <input type="text" class="form-control" id="tenca" v-model="ca.tenca" required>
            </div>
            <div class="mb-3">
              <label for="thoigianca" class="form-label">Thời gian ca</label>
              <input type="time" class="form-control" id="thoigianca" v-model="ca.thoigianca" required>
            </div>
            <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="trangthai" v-model="ca.trangthai">
              <label class="form-check-label" for="trangthai">Trạng thái</label>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
              <button type="submit" class="btn btn-primary" data-bs-dismiss="modal">Lưu thay đổi</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>