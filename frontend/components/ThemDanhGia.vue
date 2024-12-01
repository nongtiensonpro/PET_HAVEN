<script setup lang="ts">
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import { useDanhGiaStore } from "~/stores/DanhGiaStores";
import {useToast} from "vue-toastification";
const toast = useToast();

const DanhGiaStore = useDanhGiaStore();

const props = defineProps({
  idLichHen: {
    type: Number,
    required: true
  }
});

const schema = yup.object({
  moTa: yup.string().required('Mô tả là bắt buộc'),
  star: yup.number().required('Số sao là bắt buộc').min(1).max(5),
});

const { handleSubmit, errors } = useForm({
  validationSchema: schema,
});

const moTa = useField<string>('moTa');
const star = useField<number>('star');


const submitForm = handleSubmit(async (values) => {
  try {
    const newDanhGia = {
      moTa: values.moTa,
      idLichHen: props.idLichHen,
      star: values.star
    };
    await DanhGiaStore.addDanhGia(newDanhGia.moTa, newDanhGia.idLichHen, newDanhGia.star);
    toast.success('Đã thêm đánh giá thành công');

    // Close the modal
    const modal = document.getElementById('exampleModal');
    if (modal) {
      const bootstrapModal = bootstrap.Modal.getInstance(modal);
      bootstrapModal?.hide();
    }

    // Reset the form
    resetForm();
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi thêm đánh giá');
  }
});


const resetForm = () => {
  moTa.value.value = '';
  star.value.value = undefined;
};
</script>

<template>
  <div class="container">
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
      Thêm đánh giá
    </button>

    <!-- Modal -->
    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="exampleModalLabel">Thêm đánh giá</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitForm">
              <div class="mb-3">
                <label for="moTa" class="form-label">Mô tả</label>
                <textarea class="form-control" id="moTa" rows="3" v-model="moTa.value.value"></textarea>
                <div class="text-danger" v-if="errors.moTa">{{ errors.moTa }}</div>
              </div>
              <div class="mb-3">
                <label for="star" class="form-label">Số sao (1-5)</label>
                <input type="number" class="form-control" id="star" v-model="star.value.value" min="1" max="5">
                <div class="text-danger" v-if="errors.star">{{ errors.star }}</div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="resetForm">Đóng</button>
                <button type="submit" class="btn btn-primary">Lưu đánh giá</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>