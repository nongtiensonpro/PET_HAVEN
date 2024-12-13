<template>
  <button type="button" class="btn btn-sm btn-outline-warning m-1" data-bs-toggle="modal" :data-bs-target="`#updateReviewModal${danhGia.id}`">
    Cập nhật đánh giá
  </button>

  <div class="modal fade" :id="`updateReviewModal${danhGia.id}`" tabindex="-1" :aria-labelledby="`updateReviewModalLabel${danhGia.id}`" >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" :id="`updateReviewModalLabel${danhGia.id}`">Cập nhật đánh giá</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <div class="mb-3">
              <label for="rating" class="form-label">Đánh giá (1-5 sao):</label>
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
            </div>
            <div class="mb-3">
              <label for="review" class="form-label">Nhận xét:</label>
              <textarea
                v-model="localDanhGia.mota"
                class="form-control"
                id="review"
                rows="3"
              ></textarea>
            </div>
            <div class="modal-footer">
              <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
              <button type="submit" class="custom-button" data-bs-dismiss="modal">Lưu thay đổi</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useToast } from 'vue-toastification';
import { useDanhGiaStore } from '~/stores/DanhGiaStores';
import Swal from 'sweetalert2';
import type DanhGiaS from "~/models/DanhGia";

const props = defineProps<{
  danhGia: DanhGiaS
}>();

const emit = defineEmits(['cap-nhat']);
const toast = useToast();
const danhGiaStore = useDanhGiaStore();

const localDanhGia = ref<DanhGiaS>({ ...props.danhGia });

watch(() => props.danhGia, (newDanhGia) => {
  localDanhGia.value = { ...newDanhGia };
}, { deep: true });

const setRating = (value: number) => {
  localDanhGia.value.sosao = value;
};


async function submitForm() {
  try {
    const result = await Swal.fire({
      title: 'Xác nhận',
      text: "Bạn có muốn cập nhật đánh giá không?",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Có',
      cancelButtonText: 'Không'
    });

    if (result.isConfirmed) {
      await danhGiaStore.capNhatDanhGia(
        localDanhGia.value.id,
        localDanhGia.value.mota,
        Number(localDanhGia.value.idhoadon.idlichhen.id),
        localDanhGia.value.sosao
      );
      emit('cap-nhat');
      toast.success('Cập nhật đánh giá thành công!');

      return navigateTo('/services/'+props.danhGia.idhoadon.idlichhen.dichvu.id);
    }
  } catch (error) {
    toast.error('Cập nhật đánh giá thất bại!');
  }
}
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