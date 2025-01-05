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
    text: `Bạn có chắc chắn cập nhật thời gian chứ?`,
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
  <div class="container">
    <form @submit.prevent="capNhatCa">
      <div class="mb-3">
        <label for="thoigianca" class="form-label">Thời gian hẹn</label>
        <input type="time" class="form-control" id="thoigianca" v-model="localCa.thoigianca" required>
      </div>
      <div class="modal-footer">
        <div class="row">
          <div class="col">
            <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
          </div>
          <div class="col">
            <button type="submit" class="custom-button" data-bs-dismiss="modal">Lưu thay đổi</button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>