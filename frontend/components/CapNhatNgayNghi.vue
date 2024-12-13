<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Modal } from 'bootstrap';
import { useToast } from 'vue-toastification';

const caLichHenStore = useCaLichHenStore();
const toast = useToast();
const ngayNghi = ref('');
let modal: Modal | null = null;

onMounted(() => {
  const modalElement = document.getElementById('capNhatNgayNghiModal');
  if (modalElement) {
    modal = new Modal(modalElement);
  }
});

const formattedNgayNghi = computed({
  get() {
    return ngayNghi.value ? new Date(ngayNghi.value).toISOString().split('T')[0] : '';
  },
  set(value) {
    ngayNghi.value = value;
  }
});

async function themNgayNghi() {
  try {
    await caLichHenStore.themNgayNghi(formattedNgayNghi.value);
    toast.success('Cập nhật ngày nghỉ thành công');
    ngayNghi.value = ''; // Reset form
    if (modal) {
      modal.hide(); // Đóng modal
    }
  } catch (error) {
    toast.error('Cập nhật ngày nghỉ thất bại');
  }
}
</script>

<template>
  <button type="button" class="custom-button" data-bs-toggle="modal" data-bs-target="#capNhatNgayNghiModal">
    Cập nhật ngày nghỉ
  </button>

  <div class="modal fade" id="capNhatNgayNghiModal" tabindex="-1" aria-labelledby="capNhatNgayNghiModalLabel" >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="capNhatNgayNghiModalLabel">Cập nhật ngày nghỉ</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="themNgayNghi" class="form-container">
            <div class="form-group">
              <label for="thoigianca">Ngày nghỉ:</label>
              <input id="thoigianca" v-model="formattedNgayNghi" type="date" required class="form-input" />
            </div>
            <button type="submit" class="custom-button">Thêm</button>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.form-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-input,
.form-checkbox {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form-button {
  width: 100%;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.form-button:hover {
  background-color: #45a049;
}
</style>