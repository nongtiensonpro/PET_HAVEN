<template>
  <div class="container">
    <div class="card shadow">
      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Tất cả các lịch hẹn</h5>
        <button @click="fetchHoaDon" class="btn btn-light btn-sm">
          <i class="fas fa-sync-alt me-1"></i> Làm mới
        </button>
      </div>
      <div class="card-body">
        <table class="table">
          <thead>
          <tr>
            <th>ID Lịch Hẹn</th>
            <th>Email KH</th>
            <th>Tên Thú Cưng</th>
            <th>Dịch Vụ</th>
            <th>Thời gian</th>
            <th>Ngày Hẹn</th>
            <th>Số Tiền</th>
            <th>Trạng Thái</th>
            <th>Thao tác</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="hoaDon in hoaDonKhachHangs" :key="hoaDon.id">
            <td>{{ hoaDon.idlichhen.id }}</td>
            <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
            <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
            <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
            <td>{{ hoaDon.idlichhen.idcalichhen.thoigianca }}</td>
            <td>{{ formatDate(hoaDon.date) }}</td>
            <td>{{ formatCurrency(hoaDon.sotien) }}</td>
            <td><span class="badge bg-success">{{ getTrangThai(hoaDon.trangthai) }}</span></td>
            <td>
              <div v-if="hoaDon.trangthai === 3 || hoaDon.trangthai === 4 || hoaDon.trangthai === 6">
                <!-- Button trigger modal -->
                <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#exampleModal2">
                  Thay đổi lịch hẹn
                </button>
                <div class="modal fade" id="exampleModal2" tabindex="-1" aria-labelledby="exampleModalLabel2" aria-hidden="true">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel2">Thay đổi lịch hẹn</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                        <CalendarAdmin :id="String(hoaDon.idlichhen)"/>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary">Save changes</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="nav-link" v-if="hoaDon.trangthai === 3 || hoaDon.trangthai === 4 || hoaDon.trangthai === 6">
                <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#exampleModal1">
                  Thay đổi trạng thái
                </button>

                <!-- Modal -->
                <div class="modal fade" id="exampleModal1" tabindex="-1" aria-labelledby="exampleModalLabel1"
                     aria-hidden="true">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel1">Thay đổi trạng thái</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body">
                        <div class="form-floating">
                          <select class="form-select" id="floatingSelect" v-model="selectedTrangThai"
                                  :aria-label="'Trạng thái bây giờ là ' + getTrangThai(hoaDon.trangthai)">
                            <option selected>Lựa chọn trạng thái</option>
                            <option value="0" >Thành công</option>
                            <option value="1" >Thất bại</option>
                            <option value="2" >Đã hủy</option>
                            <option value="3" >Chờ thanh toán</option>
                            <option value="4" >Chờ xác nhận</option>
                            <option value="5" >Rỗng</option>
                            <option value="6">Thanh toán thành công</option>
                          </select>
                          <label for="floatingSelect">Trạng thái bây giờ là {{ getTrangThai(hoaDon.trangthai) }}</label>
                        </div>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary"
                                @click="saveTrangThai(hoaDon.idlichhen.id, selectedTrangThai)">Lưu trạng thái
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#exampleModal">
                Chi tiết
              </button>
              <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel"
                   aria-hidden="true">
                <div class="modal-dialog">
                  <div class="modal-content">
                    <div class="modal-header">
                      <h1 class="modal-title fs-5" id="exampleModalLabel">Chi tiết hóa đơn</h1>
                      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                      <table class="table table-bordered">
                        <tbody>
                        <tr>
                          <th>ID Hóa Đơn</th>
                          <td>{{ hoaDon.id }}</td>
                        </tr>
                        <tr>
                          <th>ID Lịch Hẹn</th>
                          <td>{{ hoaDon.idlichhen.id }}</td>
                        </tr>
                        <tr>
                          <th>ID Khách Hàng</th>
                          <td>{{ hoaDon.idlichhen.idkhachhang }}</td>
                        </tr>
                        <tr>
                          <th>Tên Thú Cưng</th>
                          <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                        </tr>
                        <tr>
                          <th>Cân Nặng Thú Cưng</th>
                          <td>{{ hoaDon.idlichhen.thucung.cannang }} kg</td>
                        </tr>
                        <tr>
                          <th>Tuổi Thú Cưng</th>
                          <td>{{ hoaDon.idlichhen.thucung.tuoi }} năm</td>
                        </tr>
                        <tr>
                          <th>Giống Thú Cưng</th>
                          <td>{{ hoaDon.idlichhen.thucung.giong }}</td>
                        </tr>
                        <tr>
                          <th>Dịch Vụ</th>
                          <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                        </tr>
                        <tr>
                          <th>Mô Tả Dịch Vụ</th>
                          <td>{{ hoaDon.idlichhen.dichvu.mota }}</td>
                        </tr>
                        <tr>
                          <th>Email Người Đặt</th>
                          <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                        </tr>
                        <tr>
                          <th>Ngày Hẹn</th>
                          <td>{{ formatDate(hoaDon.idlichhen.date) }}</td>
                        </tr>
                        <tr>
                          <th>Thời Gian Ca</th>
                          <td>{{ hoaDon.idlichhen.idcalichhen.thoigianca }}</td>
                        </tr>
                        <tr>
                          <th>Số Tiền</th>
                          <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                        </tr>
                        <tr>
                          <th>Ngày Thanh Toán</th>
                          <td>{{ formatDate(hoaDon.ngaythanhtoan) }}</td>
                        </tr>
                        <tr>
                          <th>Phương Thức Thanh Toán</th>
                          <td>{{ hoaDon.phuongthucthanhtoan }}</td>
                        </tr>
                        <tr>
                          <th>Mã Giao Dịch</th>
                          <td>{{ hoaDon.magiaodich }}</td>
                        </tr>
                        <tr>
                          <th>Giảm Giá</th>
                          <td>{{ hoaDon.idgiamgia ? hoaDon.idgiamgia.phantramgiam + '%' : 'Không có' }}</td>
                        </tr>
                        <tr>
                          <th>Mô Tả Giảm Giá</th>
                          <td>{{ hoaDon.idgiamgia ? hoaDon.idgiamgia.mota : 'Không có' }}</td>
                        </tr>
                        <tr>
                          <th>Trạng Thái</th>
                          <td>{{ getTrangThai(hoaDon.trangthai) }}</td>
                        </tr>
                        </tbody>
                      </table>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="custom-button" data-bs-dismiss="modal">Close</button>
                    </div>
                  </div>
                </div>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onUnmounted } from 'vue';
import HoaDonKhachHang from '~/models/HoaDonKhachHang';
import { useQuanLyLichHenAdminStore } from '~/stores/QuanLyLichHenAdmin';
import Swal from 'sweetalert2';
import { useToast } from 'vue-toastification';
import CalendarAdmin from "~/components/CalendarChangeAdmin.vue";

const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const hoaDonKhachHangs = ref<HoaDonKhachHang[]>([]);
const selectedTrangThai = ref<number>(0);
const toast = useToast();
let refreshInterval: NodeJS.Timeout;

onMounted(() => {
  fetchHoaDon();
  refreshInterval = setInterval(fetchHoaDon,  60 * 1000);
});

onUnmounted(() => {
  if (refreshInterval) clearInterval(refreshInterval);
});

const fetchHoaDon = async () => {
  try {
    await useQuanLyAdmin.fetchHoaDonKhachHangs();
    hoaDonKhachHangs.value = useQuanLyAdmin.hoaDonKhachHangs;
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
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

const getTrangThai = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    0: 'Thành công',
    1: 'Thất bại',
    2: 'Đã hủy',
    3: 'Chờ thanh toán',
    4: 'Chờ xác nhận',
    5: 'Rỗng',
    6: 'Thanh toán thành công'
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
      toast.success('Thay đổi trạng thái thành công', { timeout: 3000 });
      await fetchHoaDon(); // Refresh the data
    } catch (error) {
      toast.error(`Không thể thay đổi trạng thái. Vui lòng thử lại. ${error}`, { timeout: 3000 });
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
      toast.success('Thay đổi ngày hẹn thành công', { timeout: 3000 });
      await fetchHoaDon();
    } catch (error) {
      toast.error(`Không thể thay đổi ngày hẹn. Vui lòng thử lại. ${error}`, { timeout: 3000 });
    }
  }
}
</script>