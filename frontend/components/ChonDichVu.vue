<template>
  <div class="">
    <div class="card-header">
      <div class="row">
        <div class="col">
          <button v-for="option in serviceOptions"
                  :key="option.id"
                  type="button"
                  class="custom-button me-2 mb-2"
                  :class="{ 'selected': selectedOption === option.id }"
                  @click="toggleOption(option.id)">
            {{ option.tentuychon }}
          </button>
        </div>
      </div>
    </div>
    <div class="card-body">
      <div v-for="option in serviceOptions" :key="option.id">
        <div v-if="selectedOption === option.id">
          <div class="d-flex flex-wrap">
            <button v-for="weight in option.tuyChonCanNangs"
                    :key="weight.id"
                    class="custom-button me-2 mb-2"
                    :class="{ 'selected': selectedWeight && selectedWeight.id === weight.id }"
                    @click="selectWeight(weight)">
              {{ formatWeightRange(weight) }}
            </button>
          </div>
          <div v-html="option.mota"></div>
          <p v-if="selectedWeight" class="mt-3 text fs-3">
            Giá tiền: {{ formatCurrency(selectedWeight.giatien) }} USD
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  serviceOptions: {
    type: Array,
    required: true
  }
});

const selectedOption = ref<number | null>(null);
const selectedWeight = ref<any>(null);

const toggleOption = (optionId: number) => {
  selectedOption.value = optionId;
  selectFirstWeight();
};

const selectFirstWeight = () => {
  const selectedOptionData = props.serviceOptions.find(option => option.id === selectedOption.value);
  if (selectedOptionData && selectedOptionData.tuyChonCanNangs.length > 0) {
    selectedWeight.value = selectedOptionData.tuyChonCanNangs[0];
  } else {
    selectedWeight.value = null;
  }
};

const formatWeightRange = (weight: { cannangmin: number, cannangmax: number | null }) => {
  if (weight.cannangmax === null) {
    return `Trên ${weight.cannangmin}kg`;
  }
  return `${weight.cannangmin}kg - ${weight.cannangmax}kg`;
};

const selectWeight = (weight) => {
  selectedWeight.value = weight;
};

const formatCurrency = (value: number): string => {
  return value.toLocaleString('vi-VN');
};

onMounted(() => {
  if (props.serviceOptions.length > 0) {
    selectedOption.value = props.serviceOptions[0].id;
    selectFirstWeight();
  }
});

watch(() => props.serviceOptions, (newOptions) => {
  if (newOptions.length > 0 && !selectedOption.value) {
    selectedOption.value = newOptions[0].id;
    selectFirstWeight();
  }
}, { immediate: true });
</script>

<style scoped>
.custom-button {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.custom-button.selected {
  font-weight: bold;
  background-color: #400D01;
  color: white;
}
</style>