<template>
    <div class="container">
        <h1>Quản lý hóa đơn</h1>
        <button @click="refreshData" class="btn btn-sm btn-success m-4">Làm mới dữ liệu</button>
        <table class="table table-striped mt-3">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ngày</th>
                    <th>Phương thức thanh toán</th>
                    <th>Số tiền</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="hoaDon in paginatedHoaDonList" :key="hoaDon.id">
                    <td>{{ hoaDon.idlichhen.id }}</td>
                    <td>{{ formatDate(hoaDon.date) }}</td>
                    <td>{{ hoaDon.phuongthucthanhtoan }}</td>
                    <td>{{ hoaDon.sotien }} USD</td>
                    <td>{{ getTrangThai(hoaDon.trangthai) }}</td>
                    <td>
                        <button @click="viewHoaDon(hoaDon.id)" class="btn btn-primary">Chi tiết</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="pagination">
            <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-secondary">Trước</button>
            <span>Trang {{ currentPage }} / {{ totalPages }}</span>
            <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-secondary">Sau</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useQuanLyHoaDonStore } from '~/stores/QuanLyHoaDon';
import HoaDonKhachHang from "~/models/HoaDonKhachHang";

const store = useQuanLyHoaDonStore();
const hoaDonList = ref<HoaDonKhachHang[]>([]);
const currentPage = ref(1);
const itemsPerPage = 5;

definePageMeta({
  middleware: ['auth']
})

const fetchHoaDon = async () => {
    await store.fetchListHoaDon();
    hoaDonList.value = store.ListHoaDon;
};

const { data } = await useAsyncData('hoaDonList', () => store.fetchListHoaDon());

watch(data, () => {
    hoaDonList.value = store.ListHoaDon;
});

const refreshData = () => {
    fetchHoaDon();
    currentPage.value = 1;
};

let intervalId: NodeJS.Timeout;

onMounted(() => {
    refreshData()
    intervalId = setInterval(refreshData,  60 * 1000);
});

onUnmounted(() => {
    if (intervalId) clearInterval(intervalId);
});

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} ${hours}:${minutes}`;
};

const getTrangThai = (trangthai: number) => {
    switch (trangthai) {
        case 1: return 'Chờ thanh toán';
        case 2: return 'Thành công';
        case 3: return 'Thất bại';
        default: return 'Không xác định';
    }
};

const viewHoaDon = (id: number) => {
    navigateTo(`/admin/chitiethoadon/${id}`);
};

const paginatedHoaDonList = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return hoaDonList.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(hoaDonList.value.length / itemsPerPage));

const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
    }
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;
    }
};
</script>

<style scoped>
.table {
    width: 100%;
    margin-top: 20px;
}
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
}
.pagination button {
    margin: 0 10px;
}
</style>