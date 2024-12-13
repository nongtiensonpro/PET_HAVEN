<script setup lang="ts">
import { ref } from 'vue';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import { useDanhGiaStore } from "~/stores/DanhGiaStores";
import { useToast } from "vue-toastification";

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
const rating = ref(0);

const setRating = (value: number) => {
  rating.value = value;
  star.value.value = value;
};

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
      const modalElement = modal as HTMLElement;
      const bootstrapModal = (window as any).bootstrap.Modal.getInstance(modalElement);
      bootstrapModal?.hide();
    }

    // Reset the form
    resetForm();
    return navigateTo('/chitietlich_danhgia/'+ props.idLichHen);
  } catch (error) {
    toast.error('Đã xảy ra lỗi khi thêm đánh giá');
  }
});

const resetForm = () => {
  moTa.value.value = '';
  star.value.value = 0;
  rating.value = 0;
};
</script>

<template>
  <div class="container">
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
      Thêm đánh giá
    </button>

    <!-- Modal -->
    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" >
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
                <label for="rating" class="form-label">Đánh giá (1-5 sao):</label>
                <div class="star-rating">
                  <span v-for="starValue in 5" :key="starValue" @click="setRating(starValue)" :class="{ 'active': starValue <= rating }">
                    &#9733;
                  </span>
                </div>
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
.star-rating span {
  cursor: pointer;
  font-size: 24px;
  color: #ccc;
}

.star-rating span.active {
  color: #ffd700;
}
</style>