<template>
  <div class="container mx-auto p-4">
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">Thống Kê Dữ Liệu</h2>
    <div class="bg-white shadow-md rounded-lg overflow-hidden p-4">
      <div class="p-6 space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="row p-4">
            <div class="col">
              <label class="block text-sm font-medium text-gray-700 mb-2">Chọn khoảng thời gian:</label>
              <date-picker
                  v-model="dateRange"
                  range
                  @change="updateDateRange"
                  value-type="format"
                  format="YYYY-MM-DD"
                  :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
                  class="w-full border rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div class="col">
              <label for="statisticType" class="block text-sm font-medium text-gray-700 mb-2">Loại thống kê:</label>
              <select
                  v-model="statisticType"
                  id="statisticType"
                  class="w-full border rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="day">Theo Ngày</option>
                <option value="month">Theo Tháng</option>
                <option value="year">Theo Năm</option>
              </select>
            </div>
          </div>
        </div>
        <div class="flex justify-between items-center">
          <p v-if="dateRange[0] && dateRange[1]" class="text-sm text-gray-600">
            Thời gian đã chọn: {{ formatDisplayDate(dateRange[0]) }} - {{ formatDisplayDate(dateRange[1]) }}
          </p>
          <button @click="fetchData" class="custom-button">
            Lấy Dữ Liệu
          </button>
        </div>
      </div>
    </div>
    <div>
      <div class="card m-4">
        <div class="card-body">
          <div v-if="loading" class="mt-8 text-center">
            <p class="text-gray-600">Đang tải dữ liệu...</p>
          </div>

          <div v-else-if="error" class="mt-8 text-center text-red-500">
            <p>{{ error }}</p>
          </div>
          <div v-else-if="hasData" class="mt-8">
            <h3 class="text-xl font-semibold mb-4 text-gray-800">Dữ liệu thống kê:</h3>
            <div class="bg-white shadow-lg rounded-lg overflow-hidden">
              <table class="min-w-full divide-y divide-gray-200 m-4 p-4">
                <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Số tiền</th>
                </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
               <tr v-for="(item, index) in store.thongKeItems" :key="index" class="hover:bg-gray-50 transition-colors duration-200">
               <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ formatDisplayDate(item.date.toString()) }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.amount }} USD</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-else class="mt-8 text-center">
            <p class="text-gray-600">Không có dữ liệu để hiển thị.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import { useThongKeStore } from '~/stores/ThongKeStores';
import DatePicker from 'vue-datepicker-next';

const store = useThongKeStore();
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
  } catch (err) {
    console.error('Error fetching data:', err);
    error.value = 'Có lỗi xảy ra khi lấy dữ liệu.';
  } finally {
    loading.value = false;
  }
};

watch(statisticType, () => {
  if (dateRange.value[0] && dateRange.value[1]) {
    fetchData();
  }
});

onMounted(async () => {
  try {
    const today = new Date();
    const oneMonthAgo = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    const formatDate = (date: Date) => date.toISOString().split('T')[0];

    dateRange.value = [formatDate(oneMonthAgo), formatDate(today)];
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