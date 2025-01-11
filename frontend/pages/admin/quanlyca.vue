<script setup lang="ts">
import { onMounted, ref, onUnmounted, computed } from "vue";
import { useCaLichHenStore } from "~/stores/QuanLyCaLichHen"
import type NgayNghi from "~/models/NgayNghi";
import type CaLichHen from "~/models/CaHen";
import Swal from 'sweetalert2';
import { useToast } from 'vue-toastification';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const caLichHenStore = useCaLichHenStore();
const caLichHens = ref<CaLichHen[]>([]);
const listNgayNghi = ref<NgayNghi[]>([]);
const toast = useToast();

// Pagination
const itemsPerPage = 5;
const currentPageCaLichHen = ref(1);
const currentPageNgayNghi = ref(1);

const paginatedCaLichHens = computed(() => {
  const start = (currentPageCaLichHen.value - 1) * itemsPerPage;
  return caLichHens.value.slice(start, start + itemsPerPage);
});

const paginatedNgayNghi = computed(() => {
  const start = (currentPageNgayNghi.value - 1) * itemsPerPage;
  return listNgayNghi.value.slice(start, start + itemsPerPage);
});

const totalPagesCaLichHen = computed(() => Math.ceil(caLichHens.value.length / itemsPerPage));
const totalPagesNgayNghi = computed(() => Math.ceil(listNgayNghi.value.length / itemsPerPage));

const { data: caLichHensData } = await useAsyncData('caLichHens', () => caLichHenStore.fethcCaLichHen());
const { data: ngayNghiData } = await useAsyncData('ngayNghi', () => caLichHenStore.fetchNgayNghi());

watch(caLichHensData, (newData) => {
  if (newData) caLichHens.value = newData;
});

watch(ngayNghiData, (newData) => {
  if (newData && Array.isArray(newData)) {
    listNgayNghi.value = newData;
  } else {
    listNgayNghi.value = [];
  }
});

const fetchData = async () => {
  try {
    caLichHens.value = await caLichHenStore.fethcCaLichHen();
    const ngayNghiData = await caLichHenStore.fetchNgayNghi();
    if (Array.isArray(ngayNghiData)) {
      listNgayNghi.value = ngayNghiData;
    } else {
      listNgayNghi.value = [];
    }
    currentPageCaLichHen.value = 1;
    currentPageNgayNghi.value = 1;
  } catch (e) {
    toast.error(t('fetchDataFailed'));
  }
}

let intervalId: NodeJS.Timeout;

onMounted(() => {
  fetchData()
  intervalId = setInterval(fetchData, 60 * 1000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function capNhatTrangThai(ca: CaLichHen) {
  const action = ca.trangthai ? t('hideTime') : t('showTime');
  const result = await Swal.fire({
    title: t('confirmTitle'),
    text: t('confirmHideTime', { action: action.toLowerCase(), time: ca.thoigianca }),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });

  if (result.isConfirmed) {
    try {
      await caLichHenStore.capNhatTrangThaiCa(ca);
      await fetchData();
      toast.success(t('updateTimeSlotSuccess'));
    } catch (e) {
      toast.error(t('updateTimeSlotFailed'));
    }
  }
}

function capNhat(ca: CaLichHen) {
  caLichHenStore.capNhatCaLichHen(ca);
  fetchData();
}

function lammoi() {
  fetchData();
}

async function huyNgayNghi(ngayNghi: NgayNghi) {
  const action = ngayNghi.trangthai ? t('deactivate') : t('activate');
  const result = await Swal.fire({
    title: t('confirmTitle'),
    text: t('confirmHoliday', { action: action.toLowerCase(), date: ngayNghi.ngaynghi }),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });
  if (result.isConfirmed) {
    try {
      await caLichHenStore.huyNgayNghi(ngayNghi);
      await fetchData();
      toast.success(t('holidayStatusChangeSuccess'));
    } catch (e) {
      toast.error(t('holidayStatusChangeFailed'));
    }
  }
}

function themThoiGian() {
  return navigateTo('/admin/themthoigian');
}
function capNhatNgayNghi() {
  return navigateTo('/admin/capnhatngaynghi');
}

function capNhatThoiGian(ca: CaLichHen) {
  return navigateTo(`/admin/capnhatcalichhen/`+ ca.id);
}

</script>

<template>
  <div class="card p-4" style="border-radius: 25px">
    <div class="text fs-1">
      {{ t('timeManagement') }}
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-2">
          <button type="button" class="custom-button" @click="themThoiGian">{{ t('addTime') }}</button>
        </div>
        <div class="col-3">
          <button type="button" class="custom-button" @click="capNhatNgayNghi">{{ t('updateDate') }}</button>
        </div>
        <div class="col-3">
          <button type="button" @click="lammoi()" class="custom-button" :title="t('refresh')">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
              class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z" />
              <path
                d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col container">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>{{ t('time') }}</th>
              <th>{{ t('status') }}</th>
              <th>{{ t('actions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ca in paginatedCaLichHens" :key="ca.id">
              <td>{{ ca.id }}</td>
              <td>{{ ca.thoigianca }}</td>
              <td>
                <span :class="ca.trangthai ? 'active' : 'inactive'">
                  {{ ca.trangthai ? t('active') : t('inactive') }}
                </span>
              </td>
              <td>
                <button type="button" @click="capNhatTrangThai(ca)" class="btn btn-sm btn-outline-warning m-1">
                  {{ ca.trangthai ? t('hideTime') : t('showTime') }}
                </button>
                <button type="button" class="btn btn-sm btn-outline-info m-1" @click="capNhatThoiGian(ca)">
                  {{ t('update') }}
                </button>

              </td>
            </tr>
          </tbody>
        </table>
        <!-- Pagination for Ca Lich Hen -->
        <div class="pagination d-flex justify-content-center align-items-center mt-3">
          <div class="row">
            <div class="col">
              <button @click="currentPageCaLichHen--" :disabled="currentPageCaLichHen === 1" class="custom-button">{{
                t('previous') }}</button>
            </div>
            <div class="col">
              <span class="text fs-5">{{ t('page') }} {{ currentPageCaLichHen }} / {{ totalPagesCaLichHen }}</span>
            </div>
            <div class="col">
              <button @click="currentPageCaLichHen++" :disabled="currentPageCaLichHen === totalPagesCaLichHen"
                class="custom-button">{{ t('next') }}</button>
            </div>
          </div>
        </div>
      </div>
      <div class="col container">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>{{ t('holidayDate') }}</th>
              <th>{{ t('status') }}</th>
              <th>{{ t('actions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ngay in paginatedNgayNghi" :key="ngay.id">
              <td>{{ ngay.id }}</td>
              <td>{{ ngay.ngaynghi }}</td>
              <td>{{ ngay.trangthai ? t('active') : t('inactive') }}</td>
              <td>
                <button type="button" class="btn btn-sm btn-outline-warning m-1" @click="huyNgayNghi(ngay)">
                  {{ ngay.trangthai ? t('deactivate') : t('activate') }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <!-- Pagination for Ngay Nghi -->
        <div class="pagination d-flex justify-content-center align-items-center mt-3">
          <div class="row">
            <div class="col">
              <button @click="currentPageNgayNghi--" :disabled="currentPageNgayNghi === 1" class="custom-button">{{
                t('previous') }}</button>
            </div>
            <div class="col">
              <span class="text fs-5">{{ t('page') }} {{ currentPageNgayNghi }} / {{ totalPagesNgayNghi }}</span>
            </div>
            <div class="col">
              <button @click="currentPageNgayNghi++" :disabled="currentPageNgayNghi === totalPagesNgayNghi"
                class="custom-button">{{ t('next') }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ca-lich-hen-table th,
.ca-lich-hen-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.ca-lich-hen-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.ca-lich-hen-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.ca-lich-hen-table tr:hover {
  background-color: #f5f5f5;
}
</style>