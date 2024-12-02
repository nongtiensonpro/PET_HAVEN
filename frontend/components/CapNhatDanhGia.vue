<template>
  <div class="mb-3 rounded">
    <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#updateReviewModal">
      Cập nhật đánh giá
    </button>

    <div class="modal fade" id="updateReviewModal" tabindex="-1" aria-labelledby="updateReviewModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="updateReviewModalLabel">Cập nhật đánh giá</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitForm">
              <div class="mb-3">
                <label for="rating" class="form-label">Đánh giá (1-5 sao):</label>
                <div class="star-rating">
                  <span v-for="star in 5" :key="star" @click="setRating(star)" :class="{ 'active': star <= currentRating }">
                    &#9733;
                  </span>
                </div>
                <div class="text-danger" v-if="errors.rating">{{ errors.rating }}</div>{{currentRating}}
              </div>
              <div class="mb-3">
                <label for="review" class="form-label">Nhận xét:</label>
                <textarea v-model="currentReview" class="form-control" id="review" rows="3" :class="{ 'is-invalid': errors.review }"></textarea>
                <div v-if="errors.review" class="invalid-feedback">{{ errors.review }}</div>
              </div>
              <button type="submit" class="btn btn-primary">Cập nhật đánh giá</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import { useToast } from 'vue-toastification';
import { useDanhGiaStore } from '~/stores/DanhGiaStores';

const danhGiaStore = useDanhGiaStore();
const toast = useToast();

const props = defineProps<{
  idDanhGia: string;
  idLichHen: string;
  initialRating: number;
  initialReview: string;
}>();

const emit = defineEmits(['submit']);

const schema = yup.object({
  review: yup.string().required('Vui lòng nhập nhận xét').min(3, 'Nhận xét phải có ít nhất 3 ký tự'),
  rating: yup.number().required('Vui lòng chọn số sao').min(1, 'Vui lòng chọn ít nhất 1 sao').max(5, 'Tối đa 5 sao'),
});

const { handleSubmit, errors, setFieldValue } = useForm({
  validationSchema: schema,
  initialValues: {
    review: props.initialReview,
    rating: props.initialRating,
  },
});

const currentReview = ref(props.initialReview);
const currentRating = ref(props.initialRating);

watch(() => props.initialReview, (newValue) => {
  currentReview.value = newValue;
});

watch(() => props.initialRating, (newValue) => {
  currentRating.value = newValue;
});

const setRating = (value: number) => {
  currentRating.value = value;
  setFieldValue('rating', value);
};

const closeModal = () => {
  const modal = document.getElementById('updateReviewModal');
  if (modal) {
    const bootstrapModal = (window as any).bootstrap.Modal.getInstance(modal);
    if (bootstrapModal) {
      bootstrapModal.hide();
    }
  }
};

const submitForm = handleSubmit(async (values) => {
  try {
    await danhGiaStore.capNhatDanhGia(props.idDanhGia, currentReview.value, currentRating.value, props.idLichHen);
    toast.success('Cập nhật đánh giá thành công!');
    closeModal();
  } catch (error) {
    toast.error('Cập nhật đánh giá thất bại!');
  }
});
</script>

<style scoped>
.star-rating {
  font-size: 24px;
  cursor: pointer;
}
.star-rating span {
  color: #ccc;
  transition: color 0.2s;
}
.star-rating span.active {
  color: #ffd700;
}
</style>