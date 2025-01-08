<template>
  <div class="voucher-list card container mt-5 p-4" style="border-radius: 25px">
    <h1 class="title mb-4">{{ t('voucherManagement') }}</h1>
    <div class="row mb-4">
      <div class="col-md-4 d-flex align-items-center">
        <button type="button" class="custom-button me-2" @click="themVoucher">
          <span class="me-2">{{ t('add') }}</span>
        </button>
      </div>
      <div class="col-md-4">
        <input
            v-model="searchTerm"
            type="text"
            class="form-control"
            :placeholder="t('searchVoucherPlaceholder')"
            @input="handleSearch"
        >
      </div>
      <div class="col-md-4 d-flex align-items-center justify-content-end">
        <button class="custom-button" type="button" @click="handleSearch" :title="t('search')">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
            <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
          </svg>
        </button>
        <button @click="refreshVouchers" class="custom-button me-2" :title="t('refresh')">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
            <path d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-hover">
        <thead class="table-light">
        <tr>
          <th scope="col">ID</th>
          <th scope="col">{{ t('description') }}</th>
          <th scope="col">{{ t('discount') }}</th>
          <th scope="col">{{ t('startDate') }}</th>
          <th scope="col">{{ t('endDate') }}</th>
          <th scope="col">{{ t('status') }}</th>
          <th scope="col">{{ t('actions') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="voucher in paginatedVouchers" :key="voucher.id">
          <td>{{ voucher.id }}</td>
          <td>{{ voucher.mota }}</td>
          <td>
              <span class="badge rounded-pill" :class="voucher.phantramgiam > 50 ? 'bg-danger' : 'bg-success'">
                {{ voucher.phantramgiam }}%
              </span>
          </td>
          <td>{{ formatDate(voucher.ngaybatdau) }}</td>
          <td>{{ formatDate(voucher.ngayketthuc) }}</td>
          <td>
              <span :class="['badge', voucher.trangthai ? 'bg-success' : 'bg-warning text-dark']">
                {{ voucher.trangthai ? t('active') : t('inactive') }}
              </span>
          </td>
          <td>
            <div class="d-flex">
              <button type="button" class="btn btn-sm btn-outline-primary me-2" @click="capNhatVoucher(voucher)">
                  {{ t('update') }}
              </button>
              <button
                  @click="updateTrangThaiVoucher(voucher.id)"
                  type="button"
                  class="btn btn-sm"
                  :class="voucher.trangthai ? 'btn-outline-danger' : 'btn-outline-success'"
              >
                  {{ t('changeStatus') }}
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-center align-items-center mt-3">
      <nav aria-label="Page navigation">
        <ul class="pagination">
          <li class="page-item">
            <button
                @click="prevPage"
                :disabled="currentPage === 1"
                class="page-link custom-button"
            >
              {{ t('previous') }}
            </button>
          </li>
          <li class="page-item container disabled">
            {{ t('page') }} {{ currentPage }} / {{ totalPages }}
          </li>
          <li class="page-item">
            <button
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="page-link custom-button"
            >
              {{ t('next') }}
            </button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, computed, watch } from 'vue';
import { useVoucherStore } from '~/stores/VorchersStores';
import type Voucher from "~/models/Voucher";
import { useToast } from 'vue-toastification';
import Swal from "sweetalert2";
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const toast = useToast();
const voucherStore = useVoucherStore();

const { data: voucherData, refresh } = await useAsyncData<Voucher[]>(
  'vouchers',
  () => voucherStore.fetchVoucher()
);

// Search functionality
const searchTerm = ref('');
const filteredVouchers = ref<Voucher[]>([]);

// Pagination
const itemsPerPage = 5;
const currentPage = ref(1);

const paginatedVouchers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredVouchers.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredVouchers.value.length / itemsPerPage);
});

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

let intervalId: NodeJS.Timeout;

onMounted(() => {
  intervalId = setInterval(() => {
    refresh();
  }, 5 * 60 * 1000); // Refresh every 5 minutes
  handleSearch(); // Initial search to populate filteredVouchers
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function refreshVouchers() {
  try {
    await refresh();
    currentPage.value = 1; // Reset to first page after refresh
    handleSearch(); // Re-apply search after refresh
    toast.success(t('refreshVouchersSuccess'));
  } catch (e) {
    toast.error(t('refreshVouchersFailed'));
  }
}

function formatDate(date: Date | string) {
  if (typeof date === 'string') {
    return new Date(date).toLocaleDateString('vi-VN');
  }
  return date.toLocaleDateString('vi-VN');
}

async function updateTrangThaiVoucher(id: number) {
  const result = await Swal.fire({
    title: t('confirmTitle'),
    text: t('confirmUpdateVoucherStatus'),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: t('yes'),
    cancelButtonText: t('no')
  });
  if (result.isConfirmed) {
    try {
      await voucherStore.updateTrangThaiVoucher(id);
      await refresh();
      handleSearch(); // Re-apply search after update
      toast.success(t('updateVoucherStatusSuccess'));
    } catch (e) {
      toast.error(t('updateVoucherStatusFailed'));
    }
  }
}

function handleSearch() {
  if (!voucherData.value) {
    filteredVouchers.value = [];
    return;
  }

  const searchTermLower = searchTerm.value.toLowerCase();
  filteredVouchers.value = voucherData.value.filter(voucher =>
    voucher.mota.toLowerCase().includes(searchTermLower) ||
    voucher.id.toString().includes(searchTermLower)
  );
  currentPage.value = 1; // Reset to first page when searching
}

watch([voucherData, searchTerm], () => {
  handleSearch();
}, { immediate: true });

function themVoucher() {
  return navigateTo('/admin/themvoucher');
}

function capNhatVoucher(voucher: Voucher) {
  return navigateTo(`/admin/chitietvoucher/${voucher.id}`);
}
</script>

<style scoped>
.chart {
  height: 400px;
}
</style>