<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-4">Chọn Ngày Thống Kê</h2>
    <div class="mb-4">
      <div class="row">
        <div class="col">
          <date-picker v-model="dateRange" range @change="updateDateRange" value-type="format" format="DD-MM-YYYY"
                       :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']" class="border rounded p-2" />
        </div>
        <div class="col">
          <button @click="fetchData" class="custom-button">Lấy Dữ Liệu</button>
        </div>
      </div>


    </div>

    <div class="mb-4">
      <label for="statisticType" class="mr-2">Chọn loại thống kê:</label>
      <select v-model="statisticType" id="statisticType" class="border rounded p-2">
        <option value="day">Theo Ngày</option>
        <option value="month">Theo Tháng</option>
        <option value="year">Theo Năm</option>
      </select>
    </div>

    <div ref="chart" class="border rounded-lg shadow-lg" style="width: 100%; height: 400px;"></div>

    <div v-if="loading" class="mt-4 text-center">Đang tải dữ liệu...</div>
    <div v-if="error" class="mt-4 text-red-500">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useThongKeStore } from '~/stores/ThongKeStores';
import DatePicker from 'vue-datepicker-next';
import * as echarts from 'echarts';

const store = useThongKeStore();
const chartRef = ref<HTMLDivElement | null>(null);
const dateRange = ref<[Date | null, Date | null]>([null, null]);
const statisticType = ref('day'); // Mặc định là thống kê theo ngày
const loading = ref(false);
const error = ref<string | null>(null);

const updateDateRange = (dates: [Date, Date]) => {
  dateRange.value = dates;
};

const fetchData = async () => {
  loading.value = true;
  error.value = null;
  try {
    if (dateRange.value[0] && dateRange.value[1]) {
      if (statisticType.value === 'day') {
        await store.getUserThongKeTheoNgay(dateRange.value[0], dateRange.value[1]);
      } else if (statisticType.value === 'month') {
        await store.getUserThongKeTheoThang(dateRange.value[0], dateRange.value[1]);
      } else if (statisticType.value === 'year') {
        await store.getUserThongKeTheoNam(dateRange.value[0], dateRange.value[1]);
      }
      renderChart();
    } else {
      alert('Vui lòng chọn ngày bắt đầu và ngày kết thúc.');
    }
  } catch (err) {
    error.value = 'Có lỗi xảy ra khi lấy dữ liệu.';
  } finally {
    loading.value = false;
  }
};

const renderChart = () => {
  if (chartRef.value) {
    const myChart = echarts.init(chartRef.value);
    const option = {
      title: {
        text: 'Thống Kê Số Lượng',
      },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: store.thongKeItems.map(item => item.date.toLocaleDateString('vi-VN')),
      },
      yAxis: {
        type: 'value',
      },
      series: [{
        name: 'Số Lượng',
        type: 'bar',
        data: store.thongKeItems.map(item => item.amount),
      }],
    };
    myChart.setOption(option);
  }
};

onMounted(async () => {
  const today = new Date();
  const oneYearAgo = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());
  const formatDate = (date: Date) => date.toISOString().split('T')[0];

  await store.getUserThongKeTheoNgay(formatDate(oneYearAgo), formatDate(today));
  renderChart();
});
</script>

<style>
@import 'vue-datepicker-next/index.css';
</style>