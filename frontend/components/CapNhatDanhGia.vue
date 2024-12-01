<template>
  <div class="mb-3 p-3 border rounded">
    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label for="rating" class="form-label">Đánh giá:</label>
        <div class="star-rating">
          <span v-for="star in 5" :key="star" @click="setRating(star)" :class="{ 'active': star <= rating }">
            &#9733;
          </span>
        </div>
      </div>
      <div class="mb-3">
        <label for="review" class="form-label">Nhận xét:</label>
        <textarea v-model="review" class="form-control" id="review" rows="3" :class="{ 'is-invalid': errors.review }"></textarea>
        <div v-if="errors.review" class="invalid-feedback">{{ errors.review }}</div>
      </div>
      <button type="submit" class="btn btn-primary">Cập nhật đánh giá</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import { useToast } from 'vue-toastification';

const props = defineProps<{
  idLichHen: string;
  initialRating: number;
  initialReview: string;
}>();

const emit = defineEmits(['update:rating', 'update:review', 'submit']);

const toast = useToast();

const schema = yup.object({
  review: yup.string().required('Vui lòng nhập nhận xét').min(10, 'Nhận xét phải có ít nhất 10 ký tự'),
  rating: yup.number().required('Vui lòng chọn số sao').min(1, 'Vui lòng chọn ít nhất 1 sao').max(5, 'Tối đa 5 sao'),
});

const { handleSubmit, errors } = useForm({
  validationSchema: schema,
  initialValues: {
    review: props.initialReview,
    rating: props.initialRating,
  },
});

const { value: review } = useField('review');
const { value: rating } = useField('rating');

const setRating = (value: number) => {
  rating.value = value;
};

const submitForm = handleSubmit(async (values) => {
  try {
    emit('submit', {
      idLichHen: props.idLichHen,
      mota: values.review,
      sosao: values.rating,
    });
    toast.success('Cập nhật đánh giá thành công!');
  } catch (error) {
    toast.error('Cập nhật đánh giá thất bại!');
  }
});
</script>

<style scoped>
.star-rating {
  font-size: 24px;
  color: #ccc;
  cursor: pointer;
}
.star-rating span {
  transition: color 0.2s;
}
.star-rating span.active {
  color: #ffc107;
}
</style>