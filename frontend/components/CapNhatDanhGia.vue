<template>
  <div>
    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label for="rating" class="form-label">{{t('rating')}} ( 1-5 ):</label>
        <div class="star-rating">
          <span
              v-for="star in 5"
              :key="star"
              @click="setRating(star)"
              :class="{ 'active': star <= localDanhGia.sosao }"
          >
            &#9733;
          </span>
        </div>
        <div class="text-danger" v-if="errors.sosao">{{ errors.sosao }}</div>
      </div>
      <div class="mb-3">
        <label for="review" class="form-label">{{t('description')}}:</label>
        <textarea
            v-model="mota.value.value"
            class="form-control"
            id="review"
            rows="3"
        ></textarea>
        <div class="text-danger" v-if="errors.mota">{{ errors.mota }}</div>
      </div>
      <div class="modal-footer">
        <button type="submit" class="custom-button">{{t('saveReview')}}</button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useToast } from 'vue-toastification';
import { useDanhGiaStore } from '~/stores/DanhGiaStores';
import Swal from 'sweetalert2';
import type DanhGiaS from "~/models/DanhGia";
import {useI18n} from 'vue-i18n';
import { useField, useForm } from 'vee-validate';
import * as yup from 'yup';

const {t} = useI18n();
const props = defineProps<{
  danhGia: DanhGiaS
}>();

const emit = defineEmits(['cap-nhat']);
const toast = useToast();
const danhGiaStore = useDanhGiaStore();

const schema = yup.object({
  mota: yup.string().required(t('description_is_required')),
  sosao: yup.number().required(t('number_of_stars_is_required')).min(1).max(5),
});

const { handleSubmit, errors } = useForm({
  validationSchema: schema,
  initialValues: {
    mota: props.danhGia.mota,
    sosao: props.danhGia.sosao
  }
});

const mota = useField<string>('mota');
const sosao = useField<number>('sosao');

const localDanhGia = ref<DanhGiaS>({ ...props.danhGia });

watch(() => props.danhGia, (newDanhGia) => {
  localDanhGia.value = { ...newDanhGia };
  mota.value.value = newDanhGia.mota;
  sosao.value.value = newDanhGia.sosao;
}, { deep: true });

const setRating = (value: number) => {
  localDanhGia.value.sosao = value;
  sosao.value.value = value;
};

const submitForm = handleSubmit(async (values) => {
  try {
    const result = await Swal.fire({
      title: t('confirm'),
      text: t('cofimUpdateReview'),
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: t('yes'),
      cancelButtonText: t('no')
    });

    if (result.isConfirmed) {
      await danhGiaStore.capNhatDanhGia(
        localDanhGia.value.id,
        values.mota,
        Number(localDanhGia.value.idhoadon.idlichhen.id),
        values.sosao
      );
      emit('cap-nhat');
      toast.success(t('updateReviewSecc'));

      return navigateTo('/user/appointment');
    }
  } catch (error) {
    toast.error(t('updateReviewFall'));
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