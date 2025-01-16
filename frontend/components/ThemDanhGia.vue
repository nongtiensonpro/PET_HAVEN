<script setup lang="ts">
import { ref } from 'vue';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';
import { useDanhGiaStore } from "~/stores/DanhGiaStores";
import { useToast } from "vue-toastification";
import {useI18n} from 'vue-i18n';

const {t} = useI18n();

const toast = useToast();
const DanhGiaStore = useDanhGiaStore();

const props = defineProps({
  idLichHen: {
    type: Number,
    required: true
  }
});

const schema = yup.object({
  moTa: yup.string().required(t('description_is_required')),
  star: yup.number().required(t('number_of_stars_is_required')).min(1).max(5),
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
    toast.success(t('review_added_successfully'));

    resetForm();
    return navigateTo('/user/appointment');
  } catch (error) {
    toast.error(t('error_adding_review'));
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
    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label for="moTa" class="form-label">{{t('description')}}</label>
        <textarea class="form-control" id="moTa" rows="3" v-model="moTa.value.value"></textarea>
        <div class="text-danger" v-if="errors.moTa">{{ errors.moTa }}</div>
      </div>
      <div class="mb-3">
        <label for="rating" class="form-label">{{t('rating')}} ( 1-5 ):</label>
        <div class="star-rating">
                  <span v-for="starValue in 5" :key="starValue" @click="setRating(starValue)" :class="{ 'active': starValue <= rating }">
                    &#9733;
                  </span>
        </div>
        <div class="text-danger" v-if="errors.star">{{ errors.star }}</div>
      </div>
      <div class="modal-footer">
        <button type="submit" class="custom-button">{{t('saveReview')}}</button>
      </div>
    </form>
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