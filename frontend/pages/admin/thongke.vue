<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-4">Thống Kê Dữ Liệu</h2>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <div class="mb-4">
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

        <div class="mb-4">
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

      <div>
        <p v-if="dateRange[0] && dateRange[1]" class="text-sm text-gray-600 mb-4">
          Thời gian đã chọn: {{ formatDisplayDate(dateRange[0]) }} - {{ formatDisplayDate(dateRange[1]) }}
        </p>
        <button @click="fetchData" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
          Lấy Dữ Liệu
        </button>
      </div>
    </div>

    <div v-if="loading" class="mt-4 text-center">
      <p class="text-gray-600">Đang tải dữ liệu...</p>
    </div>

    <div v-else-if="error" class="mt-4 text-center text-red-500">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="hasData" class="mt-4">
      <h3 class="text-lg font-semibold mb-2">Dữ liệu nhận được:</h3>
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số lượng</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="(item, index) in store.thongKeItems" :key="index">
              <td class="px-6 py-4 whitespace-nowrap">{{ formatDisplayDate(item.date) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ item.amount }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div ref="chartRef" class="border rounded-lg shadow-lg mt-4" style="width: 100%; height: 400px;"></div>
    </div>

    <div v-else class="mt-4 text-center">
      <p class="text-gray-600">Không có dữ liệu để hiển thị.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed, nextTick, onUnmounted } from 'vue';
import { useThongKeStore } from '~/stores/ThongKeStores';
import DatePicker from 'vue-datepicker-next';
import * as echarts from 'echarts';

const store = useThongKeStore();
const chartRef = ref<HTMLElement | null>(null);
const chart = ref<echarts.ECharts | null>(null);
const dateRange = ref<[string | null, string | null]>([null, null]);
const statisticType = ref('day');
const loading = ref(false);
const error = ref<string | null>(null);

const hasData = computed(() => store.thongKeItems.length > 0);

const updateDateRange = (dates: [string, string]) => {
  dateRange.value = dates;
};

const formatDisplayDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
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

    if (typeof fetchFunction !== 'function') {
      throw new Error('Invalid statistic type');
    }

    await fetchFunction(startDate, endDate);

    if (hasData.value) {
      await nextTick();
      renderChart();
    }
  } catch (err) {
    console.error('Error fetching data:', err);
    error.value = 'Có lỗi xảy ra khi lấy dữ liệu.';
  } finally {
    loading.value = false;
  }
};

const renderChart = () => {
  if (!chartRef.value) {
    console.error('Chart reference is null');
    return;
  }

  nextTick(() => {
    if (chart.value) {
      chart.value.dispose();
    }

    chart.value = echarts.init(chartRef.value);

    const option = {
      title: { text: 'Thống Kê Số Lượng' },
      tooltip: {},
      xAxis: {
        type: 'category',
        data: store.thongKeItems.map(item => formatDisplayDate(item.date.toString())),
      },
      yAxis: { type: 'value' },
      series: [{
        name: 'Số Lượng',
        type: 'bar',
        data: store.thongKeItems.map(item => item.amount),
      }],
    };

    chart.value.setOption(option);
  });
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

onUnmounted(() => {
  if (chart.value) {
    chart.value.dispose();
  }
});
</script>

<style>
@import 'vue-datepicker-next/index.css';

.custom-button {
  @apply bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded;
}
</style>