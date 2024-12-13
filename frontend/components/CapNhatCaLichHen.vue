<script setup lang="ts">
import { ref, watch } from 'vue'
import { useCaLichHenStore } from '~/stores/QuanLyCaLichHen'
import type CaLichHens from '~/models/CaHen'
import Swal from "sweetalert2";
import { useToast } from 'vue-toastification'

const props = defineProps<{
  ca: CaLichHens
}>()

const emit = defineEmits(['cap-nhat'])
const toast = useToast();
const caLichHenStore = useCaLichHenStore()

const localCa = ref<CaLichHens>({ ...props.ca })

watch(() => props.ca, (newCa) => {
  localCa.value = { ...newCa }
}, { deep: true })

async function capNhatCa() {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: `Bạn có chắc chắn cập nhật ca chứ?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    try {
      await caLichHenStore.capNhatCaLichHen(localCa.value)
      emit('cap-nhat')
      toast.success('Cập nhật ca lịch hẹn thành công!')
    } catch (e) {
      toast.error('Cập nhật ca lịch hẹn thất bại!')
    }
  }
}
</script>

<template>
  <button type="button" class="btn btn-sm btn-outline-success m-1" data-bs-toggle="modal" :data-bs-target="`#exampleModal${localCa.id}`">
    Cập nhật
  </button>

  <div class="modal fade" :id="`exampleModal${localCa.id}`" tabindex="-1" :aria-labelledby="`exampleModal${localCa.id}`">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Cập nhật ca lịch hẹn</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="capNhatCa">
            <div class="mb-3">
              <label for="thoigianca" class="form-label">Thời gian ca</label>
              <input type="time" class="form-control" id="thoigianca" v-model="localCa.thoigianca" required>
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