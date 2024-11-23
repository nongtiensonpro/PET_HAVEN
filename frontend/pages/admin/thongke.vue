<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-4">Thống Kê Dữ Liệu</h2>
    <div class="row">
      <div class="col">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Chọn khoảng thời gian:</label>
          <date-picker
              v-model="dateRange"
              range
              @change="updateDateRange"
              value-type="format"
              format="YYYY-MM-DD"
              :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
              class="w-full border rounded p-2"
          />
        </div>

        <div>
          <label for="statisticType" class="block text-sm font-medium text-gray-700 mb-2">Loại thống kê:</label>
          <select
              v-model="statisticType"
              id="statisticType"
              class="w-full border rounded p-2"
          >
            <option value="day">Theo Ngày</option>
            <option value="month">Theo Tháng</option>
            <option value="year">Theo Năm</option>
          </select>
        </div>
      </div>

      <div class="col">
        <p v-if="dateRange[0] && dateRange[1]" class="text-sm text-gray-600">
          Thời gian đã chọn: {{ formatDisplayDate(dateRange[0]) }} - {{ formatDisplayDate(dateRange[1]) }}
        </p>
        <div class="col">
          <button @click="fetchData" class="custom-button">
            Lấy Dữ Liệu
          </button>
        </div>
      </div>
      <div v-if="error" class="mt-4 text-center col-12">
        <p class="text fs-4 ">{{ error }}</p>
      </div>
      </div>



      <div ref="chart" class="border rounded-lg shadow-lg" style="width: 100%; height: 400px;"></div>
      <div v-if="loading" class="mt-4 text-center">
        <p class="text-gray-600">Đang tải dữ liệu...</p>
      </div>

  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, watch} from 'vue';
import {useThongKeStore} from '~/stores/ThongKeStores';
import DatePicker from 'vue-datepicker-next';
import * as echarts from 'echarts';

const store = useThongKeStore();
const chartRef = ref<HTMLDivElement | null>(null);
const dateRange = ref<[string | null, string | null]>([null, null]);
const statisticType = ref('day');
const loading = ref(false);
const error = ref<string | null>(null);

const updateDateRange = (dates: [string, string]) => {
  dateRange.value = dates;
};

const formatDisplayDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {day: '2-digit', month: '2-digit', year: 'numeric'});
};

const fetchData = async () => {
  if (!dateRange.value[0] || !dateRange.value[1]) {
    alert('Vui lòng chọn ngày bắt đầu và ngày kết thúc.');
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    const [startDate, endDate] = dateRange.value;
    const fetchFunction = {
      day: store.getUserThongKeTheoNgay,
      month: store.getUserThongKeTheoThang,
      year: store.getUserThongKeTheoNam
    }[statisticType.value];

    await fetchFunction(startDate, endDate);
    renderChart();
  } catch (err) {
    console.error('Error fetching data:', err);
    error.value = 'Có lỗi xảy ra khi lấy dữ liệu.';
  } finally {
    loading.value = false;
  }
};

const renderChart = () => {
  if (!chartRef.value) return;

  const myChart = echarts.init(chartRef.value);
  const option = {
    title: {text: 'Thống Kê Số Lượng'},
    tooltip: {},
    xAxis: {
      type: 'category',
      data: store.thongKeItems.map(item => new Date(item.date).toLocaleDateString('vi-VN')),
    },
    yAxis: {type: 'value'},
    series: [{
      name: 'Số Lượng',
      type: 'bar',
      data: store.thongKeItems.map(item => item.amount),
    }],
  };
  myChart.setOption(option);
};

watch(statisticType, () => {
  if (dateRange.value[0] && dateRange.value[1]) {
    fetchData();
  }
});

onMounted(async () => {
  try {
    const today = new Date();
    const oneYearAgo = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());
    const formatDate = (date: Date) => date.toISOString().split('T')[0];

    dateRange.value = [formatDate(oneYearAgo), formatDate(today)];
    await fetchData();
  } catch (err) {
    console.error('Error in onMounted:', err);
    error.value = 'Có lỗi xảy ra khi khởi tạo dữ liệu.';
  }
});
</script>

<style>
@import 'vue-datepicker-next/index.css';
</style>