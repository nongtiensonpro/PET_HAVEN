<template>
  <div class="calendar-container">
    <FullCalendar
        :options="calendarOptions"
        class="calendar"
    />
    <div class="booking-details card m-4">
      <div class="card-body">
        <div class="row">
          <div class="col-12 mb-3">
            <p class="selected-date">Bạn đã chọn ngày: <strong>{{ formattedSelectedDate }}</strong></p>
          </div>
          <div class="col-12 mb-3">
            <label for="time" class="form-label">Thời gian mới:</label>
            <select id="time" v-model="selectedTime" class="form-select">
              <option value="" disabled>Chọn thời gian</option>
              <option v-for="lichhen in lichhens" :key="lichhen.id" :value="lichhen.id">
                 {{ lichhen.thoigianca }}
              </option>
            </select>
          </div>
        </div>
        <button @click="confirmBooking" class="custom-button" :disabled="!isFormValid">
          Xác nhận thay đổi lịch hẹn
        </button>
        <button @click="quayLai" class="custom-button">
          Quay lại
        </button>
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
import CaHen from "~/models/CaHen"
import { useDatLichStore } from '~/stores/DatLichStores'
import { useQuanLyLichHenAdminStore } from '~/stores/QuanLyLichHenAdmin'
import Swal from 'sweetalert2';
import { useRoute } from 'vue-router'

const route = useRoute()
const id = route.params.id

const quanLyAdmin = useQuanLyLichHenAdminStore()

const datLichStore = useDatLichStore()
const toast = useToast()

const today = new Date()
const endDate = new Date(today)
endDate.setDate(today.getDate() + 6)

const selectedDate = ref(new Date())
const selectedTime = ref('')

const formattedSelectedDate = computed(() => {
  return new Intl.DateTimeFormat('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }).format(selectedDate.value)
})

const fetchCaHen = datLichStore.fetchCaHen

const lichhens = computed((): CaHen[] => datLichStore.CaLichHen)


const isFormValid = computed(() => {
  return selectedDate.value &&  selectedTime.value
})
function formatDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}
onMounted(() => {
  const today = new Date();
  const formattedDate = formatDate(today);
  fetchCaHen(new Date(formattedDate));
  handleDateClick({ dateStr: formattedDate });
})

watch(selectedDate, (newDate) => {
  fetchCaHen(newDate)
})

const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'dayGridWeek',
  headerToolbar: {
    left: 'prev,next',
    center: 'title',
    right: 'dayGridWeek,today'
  },
  events: events.value,
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

const events = ref([
  {
    title: 'Chọn',
    start: new Date(),
    color: '#3788d8',
    allDay: true
  }
])
function handleDateClick(info: { dateStr: string }) {
  selectedDate.value = new Date(info.dateStr);
  datLichStore.updateDatLichInfo(new Date(info.dateStr));
  events.value = [{ title: 'Chọn', start: new Date(info.dateStr), color: '#3788d8', allDay: true }];
}

async function confirmBooking() {
  if (isFormValid.value) {
    const selectedCaLichHen = lichhens.value.find(ca => ca.id === Number(selectedTime.value));

    if (!selectedCaLichHen) {
      toast.error('Không tìm thấy ca lịch hẹn đã chọn', { timeout: 3000 });
      return;
    }

    const newCaLichHenId = selectedCaLichHen.id;
    const newDate = selectedDate.value.toISOString().split('T')[0];

    const result = await Swal.fire({
      title: 'Xác nhận',
      text: "Bạn có muốn thay đổi lịch hẹn không?",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Có',
      cancelButtonText: 'Không'
    });

    if (result.isConfirmed) {
      try {
        await quanLyAdmin.thayDoiLichHenAdmin(id, newDate, String(newCaLichHenId));
        toast.success('Đã thay đổi lịch hẹn thành công', { timeout: 3000 });
        navigateTo(`/admin/quanlylichhen`);
      } catch (error) {
        toast.error('Không thể thay đổi lịch hẹn. Vui lòng thử lại . ' + id+'Meo' + error, { timeout: 3000 });
      }
    }
  } else {
    toast.error('Vui lòng chọn ngày và thời gian mới', { timeout: 3000 });
  }
}

function quayLai(){
  navigateTo(`/admin/quanlylichhen`);
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

.calendar {
  font-family: 'Arial', sans-serif;
  margin-bottom: 30px;
}

.booking-details {
  background-color: rgba(246, 246, 234, 0.3);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.booking-details:hover {
  box-shadow: 0 5px 15px rgba(64, 13, 1, 0.1);
}

.selected-date {
  font-size: 1.1em;
  color: #400D01;
}

.form-select {
  border-radius: 12px;
  border: 1px solid #400D01;
  padding: 10px 15px;
  transition: all 0.3s ease;
  background-color: rgba(246, 246, 234, 0.62);
  color: #400D01;
}

.form-select:focus {
  border-color: #400D01;
  box-shadow: 0 0 0 0.2rem rgba(64, 13, 1, 0.25);
}


</style>