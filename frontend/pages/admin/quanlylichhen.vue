<template>
  <div class="container">
    <div class="card">
      <div class="card-header text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0 text fs-4">Tất cả các lịch hẹn</h5>
        <button @click="fetchHoaDon" class="btn btn-sm btn-outline-success">
           Làm mới
        </button>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <input
              v-model="searchQuery"
              @input="handleSearch"
              type="text"
              class="form-control"
              placeholder="Tìm kiếm theo email, tên thú cưng hoặc dịch vụ..."
          >
        </div>
        <table class="table">
          <thead>
          <tr>
            <th>ID Lịch Hẹn</th>
            <th>Email KH</th>
            <th>Tên Thú Cưng</th>
            <th>Dịch Vụ</th>
            <th>Thời gian</th>
            <th>Ngày Hẹn</th>
            <th>Trạng Thái</th>
            <th>Thao tác</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="lichhen in paginatedHoaDon" :key="lichhen.id">
            <td>{{ lichhen.id }}</td>
            <td>{{ lichhen.emailNguoiDat }}</td>
            <td>{{ lichhen.thucung.ten }}</td>
            <td>{{ lichhen.dichvu.tendichvu }}</td>
            <td>{{ lichhen.idcalichhen.thoigianca }}</td>
            <td>{{ formatDate(lichhen.date) }}</td>
            <td><span class="badge bg-success">{{ getTrangThai(lichhen.trangthai) }}</span></td>
            <td>

              <div class="row">
              <div class="col">
                <div v-if="lichhen.trangthai === 3 || lichhen.trangthai === 4 || lichhen.trangthai === 6">
                  <!-- Button trigger modal -->
                  <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#exampleModal2"
                          @click="selectLichHen(lichhen)">

                    Thay đổi thời gian lịch hẹn
                  </button>
                  <div class="modal fade" id="exampleModal2" tabindex="-1" aria-labelledby="exampleModalLabel2">
                    <div class="modal-dialog">
                      <div class="modal-content">
                        <div class="modal-header">
                          <h1 class="modal-title fs-5" id="exampleModalLabel2">Thay đổi lịch hẹn</h1>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                          <CalendarAdmin :id="String(selectedLichHen?.id)"/>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="custom-button" data-bs-dismiss="modal">Close</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="col">
                <button type="button" class="btn btn-sm btn-outline-success" data-bs-toggle="modal" data-bs-target="#exampleModal1"
                        @click="selectLichHen(lichhen)">
                  Thay đổi trạng thái
                </button>

                <!-- Modal -->
                <div class="modal fade" id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1"
                >
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel1">Thay đổi trạng thái</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                        <div class="form-floating">
                          <select class="form-select" id="floatingSelect" v-model="selectedTrangThai"
                                  :aria-label="'Trạng thái bây giờ là ' + getTrangThai(selectedLichHen?.trangthai)">
                            <option selected>Lựa chọn trạng thái</option>
                            <option>{{ selectedLichHen?.id }}</option>
                            <option value="0">Thành công</option>
                            <option value="1">Thất bại</option>
                            <option value="2">Đã hủy</option>
                            <option value="3">Chờ thanh toán</option>
                            <option value="4">Chờ xác nhận</option>
                            <option value="5">Rỗng</option>
                            <option value="6">Thanh toán thành công</option>
                          </select>
                          <label for="floatingSelect">Trạng thái bây giờ là {{
                              getTrangThai(selectedLichHen?.trangthai)
                            }}</label>
                        </div>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="custom-button" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="custom-button"
                                @click="saveTrangThai(selectedLichHen?.id, selectedTrangThai)">Lưu trạng thái
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col">
                <button type="button" class="btn btn-sm btn-outline-info" @click="viewHoaDon(lichhen.idcalichhen.id)">Chi tiết</button>
              <!--Chi tiet -->
<!--              <button-->
<!--                  type="button"-->
<!--                  class="btn btn-sm btn-outline-info"-->
<!--                  data-bs-toggle="modal"-->
<!--                  data-bs-target="#exampleModal"-->
<!--                  @click="selectLichHen(lichhen)">-->
<!--                Chi tiết-->
<!--              </button>-->
<!--              <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"-->
<!--              >-->
<!--                <div class="modal-dialog">-->
<!--                  <div class="modal-content">-->
<!--                    <div class="modal-header">-->
<!--                      <h1 class="modal-title fs-5" id="exampleModalLabel">Chi tiết lịch hẹn</h1>-->
<!--                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
<!--                    </div>-->
<!--                    <div class="modal-body">-->
<!--                      <table class="table table-bordered">-->
<!--                        <tbody>-->
<!--                        <tr>-->
<!--                          <th>ID Lịch Hẹn</th>-->
<!--                          <td>{{ selectedLichHen?.id }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>ID Khách Hàng</th>-->
<!--                          <td>{{ selectedLichHen?.idkhachhang }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Tên Thú Cưng</th>-->
<!--                          <td>{{ selectedLichHen?.thucung.ten }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Cân Nặng Thú Cưng</th>-->
<!--                          <td>{{ selectedLichHen?.thucung.cannang }} kg</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Tuổi Thú Cưng</th>-->
<!--                          <td>{{ selectedLichHen?.thucung.tuoi }} năm</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Giống Thú Cưng</th>-->
<!--                          <td>{{ selectedLichHen?.thucung.giong }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Dịch Vụ</th>-->
<!--                          <td>{{ selectedLichHen?.dichvu.tendichvu }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Mô Tả Dịch Vụ</th>-->
<!--                          <td>{{ selectedLichHen?.dichvu.mota }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Email Người Đặt</th>-->
<!--                          <td>{{ selectedLichHen?.emailNguoiDat }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Ngày Hẹn</th>-->
<!--                          <td>{{ formatDate(selectedLichHen?.date) }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Thời Gian Ca</th>-->
<!--                          <td>{{ selectedLichHen?.idcalichhen.thoigianca }}</td>-->
<!--                        </tr>-->
<!--                        <tr>-->
<!--                          <th>Trạng Thái</th>-->
<!--                          <td>{{ getTrangThai(selectedLichHen?.trangthai) }}</td>-->
<!--                        </tr>-->
<!--                        </tbody>-->
<!--                      </table>-->
<!--                    </div>-->
<!--                    <div class="modal-footer">-->
<!--                      <button type="button" class="custom-button" data-bs-dismiss="modal">Close</button>-->
<!--                    </div>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
              </div>
              </div>
              <!--Chi tiet -->
            </td>
          </tr>
          </tbody>

        </table>
        <div class="d-flex justify-content-center align-items-center mt-4">
          <div class="d-flex align-items-center">
            <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
                class="custom-button me-3"
            >
              Trước
            </button>
            <span class="fs-5 mx-3">Trang {{ currentPage }} / {{ totalPages }}</span>
            <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="custom-button ms-3"
            >
              Sau
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, onUnmounted, computed} from 'vue';
import HoaDonKhachHang from '~/models/HoaDonKhachHang';
import {useQuanLyLichHenAdminStore} from '~/stores/QuanLyLichHenAdmin';
import Swal from 'sweetalert2';
import {useToast} from 'vue-toastification';
import CalendarAdmin from "~/components/CalendarChangeAdmin.vue";
import type {Lichhen} from "~/models/LichSuDatLich";

const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const lichhen = ref<Lichhen[]>([]);
const filteredHoaDon = ref<Lichhen[]>([]);
const selectedTrangThai = ref<number>(0);
const toast = useToast();
let refreshInterval: NodeJS.Timeout;
const itemsPerPage = 5;
const currentPage = ref(1);
const searchQuery = ref('');
const selectedLichHen = ref<Lichhen | null>(null); // Lịch hẹn được chọn

function selectLichHen(lichHenItem: Lichhen) {
  selectedLichHen.value = lichHenItem; // Cập nhật thông tin lịch hẹn được chọn
}

onMounted(() => {
  fetchHoaDon();
  refreshInterval = setInterval(fetchHoaDon, 5*60 * 1000);
});

const totalPages = computed(() => Math.ceil(filteredHoaDon.value.length / itemsPerPage));

const paginatedHoaDon = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredHoaDon.value.slice(start, end);
});

const handleSearch = () => {
  filteredHoaDon.value = lichhen.value.filter(hoaDon =>
      hoaDon.emailNguoiDat.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      hoaDon.thucung.ten.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      hoaDon.dichvu.tendichvu.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
  currentPage.value = 1;
};

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval);
});

const fetchHoaDon = async () => {
  try {
    await useQuanLyAdmin.fetchHoaDonKhachHangs();
    lichhen.value = useQuanLyAdmin.hoaDonKhachHangs;
    filteredHoaDon.value = lichhen.value;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu:', error);
    toast.error('Không thể tải dữ liệu. Vui lòng thử lại sau.');
  }
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'USD'}).format(amount);
};

const getTrangThai = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    0: 'Thành công',
    1: 'Đã đổi',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công',
    7: 'Đã hoàn tiền',
    8: 'Chờ sử dụng'
  };
  return trangThaiMap[status] || 'Không xác định';
};

async function saveTrangThai(id: number, idTrangThai: number) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: 'Bạn có muốn thay đổi trạng thái không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không',
  });
  if (result.isConfirmed) {
    try {
      await useQuanLyAdmin.thayDoiTrangThai(id, idTrangThai);
      toast.success('Thay đổi trạng thái thành công', {timeout: 3000});
      await fetchHoaDon(); // Refresh the data
    } catch (error) {
      toast.error(`Không thể thay đổi trạng thái. Vui lòng thử lại. ${error}`, {timeout: 3000});
    }
  }
}

async function doiNgayHen(ngayHen: string, idcalichhen: number) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: 'Bạn có muốn thay đổi ngày hẹn không?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không',
  });
  if (result.isConfirmed) {
    try {
      await useQuanLyAdmin.doiThoiGian(ngayHen, idcalichhen);
      toast.success('Thay đổi ngày hẹn thành công', {timeout: 3000});
      await fetchHoaDon();
    } catch (error) {
      toast.error(`Không thể thay đổi ngày hẹn. Vui lòng thử lại. ${error}`, {timeout: 3000});
    }
  }
}
const viewHoaDon = (id: number) => {
  navigateTo(`/admin/chitiethoadon/${id}`);
};

</script>

<style scoped>
</style>