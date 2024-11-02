<template>
  <div class="calendar-container">
    <h2 class="calendar-title">Chọn ngày và giờ hẹn</h2>
    <FullCalendar
      :options="calendarOptions"
      class="calendar"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import viLocale from '@fullcalendar/core/locales/vi'
import {useToast} from 'vue-toastification';
const toast = useToast();
const events = ref([
  { title: 'Sự kiện 1', start: '2024-11-01', color: '#400D01' },
  { title: 'Sự kiện 2', start: '2024-11-15', color: '#400D01' },
])

const calendarOptions = {
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek,timeGridDay'
  },
  events: events.value,
  eventClick: handleEventClick,
  dateClick: handleDateClick,
  locale: viLocale,
  buttonText: {
    today: 'Hôm nay',
    month: 'Tháng',
    week: 'Tuần',
    day: 'Ngày'
  }
}

function handleEventClick(info) {
  toast.success(`Sự kiện: ${info.event.title}`, {})
}

function handleDateClick(info) {
  toast.success(`Ngày được chọn: ${info.dateStr}`)
}
</script>

<style scoped>
.calendar-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  border-radius: 8px;
}

.calendar-title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.calendar {
  font-family: Arial, sans-serif;
  background: #F2F0D8;
  color: #400D01;
}

:deep(.fc-button-primary) {
  background-color: #400D01;
  border-color: #400D01;
  margin: 1%;
}

:deep(.fc-button-primary:hover) {
  background-color: #400D01;
  border-color: #400D01;
  margin: 1%;
}

:deep(.fc-day-today) {
  background-color: #e8f5e9 !important;
}

:deep(.fc-event) {
  border: none;
  padding: 2px 5px;
  font-size: 0.85em;
}
</style>