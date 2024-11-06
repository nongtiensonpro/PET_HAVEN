<template>
  <div class="calendar-container">
    <h2 class="calendar-title">Chọn ngày và giờ hẹn</h2>
    <FullCalendar
        :options="calendarOptions"
        class="calendar"
    />
    <div class="card m-4">
      <div class="card-body">
        <div class="row">
          <div>
            Bạn đã chọn ngày {{ new Date().toDateString()}}
          </div>
          <div class="col-6">
            <label for="">Chủ cần :</label>
            <select class="form-control" name="" id="">
              <option v-for="service in services" :key="service.id">
                {{ service.tendichvu }}
              </option>
            </select>
          </div>
          <div class="col-6">
            <div class="form-group">
              <label for="">Khi nào :</label>
              <select class="form-control" >
                <option v-for="lichhen in lichhens" :key="lichhen.id">
                  {{lichhen.tenca}} : {{lichhen.thoigianca}}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import viLocale from '@fullcalendar/core/locales/vi'
import { useToast } from 'vue-toastification';
import DichVu from "~/models/DichVu";
import CaHen from "~/models/CaHen";

import {useDatLichStore} from '~/stores/DatLichStores';

const datLichStore = useDatLichStore();

const today = new Date();

const fetchCaHen = datLichStore.fetchCaHen;



const services = computed((): DichVu[] => {
  return datLichStore.DichVu;
});

const lichhens = computed((): CaHen[] => {
  return datLichStore.CaLichHen;
})

onMounted(() => {
  fetchCaHen(new Date());

});
const toast = useToast();




const endDate = new Date(today);
endDate.setDate(today.getDate() + 6);

const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridWeek',
  headerToolbar: {
    left: 'prev,next',
    center: 'title',
    right: 'dayGridWeek,today'
  },
  events: events.value,
  eventClick: handleEventClick,
  dateClick: handleDateClick,
  locale: viLocale,
  buttonText: {
    today: 'Hôm nay',
    week: '7 ngày',
    dayGridWeek: 'Tuần'
  },
  validRange: {
    start: today,
    end: endDate
  },
  visibleRange: {
    start: today,
    end: endDate
  },
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

function handleEventClick(info) {
  toast.success(`Đã chọn: ${info.event.title}`, {})
}


const events = ref([
  { title: '', start: new Date(), color: '#3788d8' }
])

function handleDateClick(info) {
  const specificDate = info.dateStr;
  datLichStore.updateDatLichInfo(specificDate);
  events.value = events.value.filter(event => event.title !== 'Chọn');
  events.value.push({ title: 'Chọn', start: specificDate, color: '#3788d8' });
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
}

:deep(.fc) {
  --fc-border-color: #e0e0e0;
  --fc-button-bg-color: #3788d8;
  --fc-button-border-color: #3788d8;
  --fc-button-hover-bg-color: #2c6aa0;
  --fc-button-hover-border-color: #2c6aa0;
  --fc-button-active-bg-color: #2c6aa0;
  --fc-button-active-border-color: #2c6aa0;
  --fc-event-bg-color: #3788d8;
  --fc-event-border-color: #3788d8;
  --fc-today-bg-color: #ebf5ff;
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