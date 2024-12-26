<template>
  <div class="calendar-container">
    <FullCalendar :options="calendarOptions" class="calendar" />
    <div class="booking-details card m-4">
      <div class="card-body">
        <div class="row">
          <div class="col-12 mb-3">
            <p class="selected-date">Bạn đã chọn ngày: <strong>{{ formattedSelectedDate }}</strong></p>
          </div>
          <template v-if="hasAvailableSlots">
            <div class="col-md-6 mb-3">
              <label for="time" class="form-label">Thời gian:</label>
              <select id="time" v-model="selectedTime" class="form-select">
                <option value="" disabled>Chọn thời gian</option>
                <option v-for="lichhen in lichhens" :key="lichhen.id" :value="lichhen.id">
                  {{ lichhen.thoigianca }}
                </option>
              </select>
            </div>
            <div class="col-12">
              <button @click="confirmBooking" class="custom-button" :disabled="!isFormValid">
                Tiếp tục
              </button>
            </div>
          </template>
          <div v-else class="col-12">
            <p class="text-danger">Ngày bạn đã chọn không khả dụng. Vui lòng chọn lại ngày khác.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import viLocale from '@fullcalendar/core/locales/vi'
import { useToast } from 'vue-toastification'
import { useDatLichStore } from '~/stores/DatLichStores'
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu'
import DichVuKhachDat from "~/models/DichVuKhachDat"

const { saveTempData, getTempData } = useMauKhachDatDichVu()
const datLichStore = useDatLichStore()
const toast = useToast()

const today = new Date()
const endDate = new Date(today.getTime() + 6 * 24 * 60 * 60 * 1000)

const selectedDate = ref(today)
const selectedTime = ref('')

const formattedSelectedDate = computed(() =>
  new Intl.DateTimeFormat('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }).format(selectedDate.value)
)

const services = computed(() => datLichStore.DichVu)
const lichhens = computed(() => datLichStore.CaLichHen)

const hasAvailableSlots = computed(() => services.value.length > 0 && lichhens.value.length > 0)

const isFormValid = computed(() => selectedDate.value && selectedTime.value)

onMounted(() => {
  const formattedDate = formatDate(today)
  datLichStore.fetchCaHen(formattedDate)
  handleDateClick({ dateStr: formattedDate })
})

watch(selectedDate, (newDate) => datLichStore.fetchCaHen(formatDate(newDate)))

const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridWeek',
  headerToolbar: {
    left: 'prev,next',
    center: 'title',
    right: 'dayGridWeek,today'
  },
  events: [{ title: 'Chọn', start: selectedDate.value, color: '#400D01', allDay: true }],
  eventClick: () => {},
  dateClick: handleDateClick,
  locale: viLocale,
  buttonText: { today: 'Hôm nay', week: '7 ngày', dayGridWeek: 'Tuần' },
  validRange: { start: today, end: endDate },
  visibleRange: { start: today, end: endDate },
  duration: { days: 7 },
  dayCount: 7,
  height: 'auto',
  contentHeight: 'auto',
  aspectRatio: 1.8,
  expandRows: true,
  slotMinTime: '07:00:00',
  slotMaxTime: '19:00:00',
  allDaySlot: false,
  stickyHeaderDates: true,
  nowIndicator: true
}))

function formatDate(date: Date): string {
  return date.toISOString().split('T')[0]
}

function handleDateClick(info: { dateStr: string }) {
  selectedDate.value = new Date(info.dateStr)
  datLichStore.updateDatLichInfo(info.dateStr)
}

function confirmBooking() {
  if (isFormValid.value) {
    const selectedCaLichHen = lichhens.value.find(ca => ca.id === Number(selectedTime.value))

    if (!selectedCaLichHen) {
      toast.error('Không tìm thấy ca lịch hẹn đã chọn', { timeout: 3000 })
      return
    }

    const dichVuKhachDat = new DichVuKhachDat({
      calichhen: selectedCaLichHen,
      date: selectedDate.value
    })

    saveTempData(dichVuKhachDat)
    console.log('Dữ liệu đã lưu:', getTempData())
  } else {
    toast.error('Vui lòng điền đầy đủ thông tin đặt lịch', { timeout: 3000 })
  }
}
</script>


<style scoped>
.calendar-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  border-radius: 12px;
  background-color: #ffffff;
}

.calendar-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.calendar {
  font-family: 'Arial', sans-serif;
  margin-bottom: 30px;
  color: #400D01 !important;
}

.booking-details {
  background-color: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.booking-details:hover {
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.selected-date {
  font-size: 1.1em;
  color: #400D01;
}

.form-select {
  border-radius: 20px;
  border: 1px solid #ced4da;
  padding: 10px 15px;
  transition: all 0.3s ease;
}

.form-select:focus {
  border-color: #f6f6ea;
  box-shadow: 0 0 0 0.2rem rgb(64, 13, 1);
}

.btn-primary {
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgb(64, 13, 1);
}

:deep(.fc) {
  --fc-border-color: #e0e0e0;
  --fc-button-bg-color: #400D01;
  --fc-button-border-color: #400D01;
  --fc-button-hover-bg-color: #5a1301;
  --fc-button-hover-border-color: #5a1301;
  --fc-button-active-bg-color: #5a1301;
  --fc-button-active-border-color: #5a1301;
  --fc-event-bg-color: #400D01;
  --fc-event-border-color: #400D01;
  --fc-today-bg-color: rgba(64, 13, 1, 0.1);
}

:deep(.fc-button-primary) {
  border-radius: 20px;
  padding: 8px 16px;
  text-transform: uppercase;
  font-weight: bold;
  transition: all 0.3s ease;
}

:deep(.fc-button-primary:not(:disabled):hover) {
  background-color: var(--fc-button-hover-bg-color);
  border-color: var(--fc-button-hover-border-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(64, 13, 1, 0.3);
}
:deep(.fc-day-today) {
  background-color: var(--fc-today-bg-color) !important;
}

:deep(.fc-event) {
  border: none;
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 0.85em;
  font-weight: bold;
}

:deep(.fc-timegrid-slot) {
  height: 50px !important;
}

:deep(.fc-timegrid-axis) {
  padding-right: 10px;
}

:deep(.fc-col-header-cell) {
  padding: 10px 0;
  font-weight: bold;
}

:deep(.fc-day-grid-event) {
  margin: 2px 0;
}

:deep(.fc-toolbar-title) {
  font-size: 20px !important;
  font-weight: 600;
}
</style>