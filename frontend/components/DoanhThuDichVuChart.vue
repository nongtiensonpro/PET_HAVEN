<template>
  <div class="chart-container">
    <div class="chart-header">
      <h3>Doanh Thu Theo Dịch Vụ</h3>
      <div class="date-picker-container">
        <v-date-picker v-model="startDate" />
        <v-date-picker v-model="endDate" />
        <v-btn @click="fetchData" color="primary">Xem thống kê</v-btn>
      </div>
    </div>
    <div class="chart-content">
      <Bar
        v-if="chartData.datasets[0].data.length > 0"
        :data="chartData"
        :options="chartOptions"
      />
      <div v-else class="no-data">
        Không có dữ liệu trong khoảng thời gian này
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Bar } from 'vue-chartjs'
import { useThongKeStore } from '@/stores/ThongKeStores'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
)

const thongKeStore = useThongKeStore()
const startDate = ref(new Date())
const endDate = ref(new Date())

const chartData = computed(() => ({
  labels: thongKeStore.doanhThuDichVu.map(item => item.tenDichVu),
  datasets: [
    {
      label: 'Doanh Thu (VNĐ)',
      data: thongKeStore.doanhThuDichVu.map(item => item.doanhThu),
      backgroundColor: [
        'rgba(255, 99, 132, 0.5)',
        'rgba(54, 162, 235, 0.5)',
        'rgba(255, 206, 86, 0.5)',
        'rgba(75, 192, 192, 0.5)',
        'rgba(153, 102, 255, 0.5)',
      ],
      borderColor: [
        'rgba(255, 99, 132, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
      ],
      borderWidth: 1
    }
  ]
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value: any) {
          return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
          }).format(value)
        }
      }
    }
  },
  plugins: {
    legend: {
      display: true,
      position: 'top' as const
    },
    tooltip: {
      callbacks: {
        label: function(context: any) {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          label += new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
          }).format(context.raw);
          return label;
        }
      }
    }
  }
}

const fetchData = async () => {
  try {
    await thongKeStore.getDoanhThuTheoDichVu(
      startDate.value.toISOString().split('T')[0],
      endDate.value.toISOString().split('T')[0]
    )
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

// Fetch initial data
fetchData()
</script>

<style scoped>
.chart-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.date-picker-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.chart-content {
  height: 400px;
}

.no-data {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
  font-style: italic;
}
</style>
