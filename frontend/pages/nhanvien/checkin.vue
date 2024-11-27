<template>
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-12 mb-4">
        <div class="card shadow">
          <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Lịch hẹn hôm nay</h5>
            <button @click="fetchHoaDon" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> Làm mới
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>Thời gian</th>
                  <th>ID Lịch Hẹn</th>
                  <th>Email KH</th>
                  <th>Tên Thú Cưng</th>
                  <th>Dịch Vụ</th>
                  <th>Ngày Hẹn</th>
                  <th>Số Tiền</th>
                  <th>Trạng Thái</th>
                  <th>Thao Tác</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hoaDon in hoaDonList" :key="hoaDon.id">
                  <td>{{ hoaDon.idlichhen.idcalichhen.thoigianca }}</td>
                  <td>{{ hoaDon.idlichhen.id }}</td>
                  <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                  <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                  <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                  <td>{{ formatDate(hoaDon.date) }}</td>
                  <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                  <td><span class="badge bg-warning">{{ getTrangThai(hoaDon.idlichhen.trangthai) }}</span></td>
                  <td>

                    <button
                        v-if="hoaDon.idlichhen.trangthai === 3 || hoaDon.idlichhen.trangthai === 4 "
                        @click="thanhToanHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Thanh toán
                    </button>

                    <button
                        v-if="hoaDon.idlichhen.trangthai === 6 "
                        @click="thanhToanHoaDon(hoaDon.idlichhen.id)"
                        class="btn btn-sm btn-outline-primary m-1">
                      Checkin
                    </button>

                    <button type="button" class="btn btn-sm btn-outline-primary m-1" data-bs-toggle="modal"
                            data-bs-target="#exampleModal">
                      Chi tiết
                    </button>

                    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                      <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabel">Thông tin chi tiết hóa đơn #{{ hoaDon.id }}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            <div class="row">
                              <div class="col-md-6">
                                <h6 class="border-bottom pb-2 mb-3">Thông tin hóa đơn</h6>
                                <div class="mb-3">
                                  <strong>Ngày hẹn:</strong> {{ formatDate(hoaDon.date) }}
                                </div>
                                <div class="mb-3">
                                  <strong>Ngày thanh toán:</strong> {{ hoaDon.ngaythanhtoan ? formatDate(hoaDon.ngaythanhtoan) : 'Chưa thanh toán' }}
                                </div>
                                <div class="mb-3">
                                  <strong>Phương thức thanh toán:</strong> {{ hoaDon.phuongthucthanhtoan || 'Chưa xác định' }}
                                </div>
                                <div class="mb-3">
                                  <strong>Người thanh toán:</strong> {{ hoaDon.nguoithanhtoan || 'Chưa thanh toán' }}
                                </div>
                                <div class="mb-3">
                                  <strong>Số tiền:</strong> {{ formatCurrency(hoaDon.sotien) }}
                                </div>
                                <div class="mb-3">
                                  <strong>Trạng thái:</strong>
                                  <span :class="['badge', hoaDon.trangthai === 2 ? 'bg-success' : 'bg-warning']">
                                    {{ getTrangThai(hoaDon.idlichhen.trangthai) }}
                                  </span>
                                </div>
                              </div>
                              <div class="col-md-6">
                                <h6 class="border-bottom pb-2 mb-3">Thông tin khách hàng & thú cưng</h6>
                                <div class="mb-3">
                                  <strong>Email:</strong> {{ hoaDon.idlichhen.emailNguoiDat }}
                                </div>
                                <div class="mb-3">
                                  <strong>Tên thú cưng:</strong> {{ hoaDon.idlichhen.thucung.ten }}
                                </div>
                                <div class="mb-3">
                                  <strong>Giống:</strong> {{ hoaDon.idlichhen.thucung.giong }}
                                </div>
                                <div class="mb-3">
                                  <strong>Cân nặng:</strong> {{ hoaDon.idlichhen.thucung.cannang }} kg
                                </div>
                                <div class="mb-3">
                                  <strong>Tuổi:</strong> {{ hoaDon.idlichhen.thucung.tuoi }} tuổi
                                </div>
                              </div>
                            </div>
                            <div class="row mt-4">
                              <div class="col-md-6">
                                <h6 class="border-bottom pb-2 mb-3">Thông tin dịch vụ</h6>
                                <div class="mb-3">
                                  <strong>ID Lịch hẹn:</strong> {{ hoaDon.idlichhen.id }}
                                </div>
                                <div class="mb-3">
                                  <strong>Dịch vụ:</strong> {{ hoaDon.idlichhen.dichvu.tendichvu }}
                                </div>
                              </div>
                              <div class="col-md-6">
                                <h6 class="border-bottom pb-2 mb-3">Thông tin khuyến mãi</h6>
                                <div v-if="hoaDon.idgiamgia">
                                  <div class="mb-3">
                                    <strong>Mô tả:</strong> {{ hoaDon.idgiamgia.mota }}
                                  </div>
                                  <div class="mb-3">
                                    <strong>Thời gian:</strong> {{ formatDate(hoaDon.idgiamgia.ngaybatdau) }} - {{ formatDate(hoaDon.idgiamgia.ngayketthuc) }}
                                  </div>
                                  <div class="mb-3">
                                    <strong>Phần trăm giảm:</strong> {{ hoaDon.idgiamgia.phantramgiam }}%
                                  </div>
                                  <div class="mb-3">
                                    <strong>Trạng thái:</strong> {{ hoaDon.idgiamgia.trangthai }}
                                  </div>
                                </div>
                                <div v-else>Không có khuyến mãi áp dụng</div>
                              </div>
                            </div>
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            <button type="button" class="btn btn-primary" @click="thanhToanHoaDon(hoaDon.idlichhen.id)">Thanh toán</button>
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
      </div>

      <div class="col-12">
        <div class="card shadow">
          <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Hóa đơn đã thanh toán</h5>
            <button @click="fetchHoaDonThanhToan" class="btn btn-light btn-sm">
              <i class="fas fa-sync-alt me-1"></i> Làm mới
            </button>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>ID Hóa Đơn</th>
                  <th>ID Lịch Hẹn</th>
                  <th>Email KH</th>
                  <th>Tên Thú Cưng</th>
                  <th>Dịch Vụ</th>
                  <th>Ngày Hẹn</th>
                  <th>Số Tiền</th>
                  <th>Trạng Thái</th>
                  <th>Thao Tác</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hoaDon in hoaDonThanhToanList" :key="hoaDon.id">
                  <td>{{ hoaDon.id }}</td>
                  <td>{{ hoaDon.idlichhen.id }}</td>
                  <td>{{ hoaDon.idlichhen.emailNguoiDat }}</td>
                  <td>{{ hoaDon.idlichhen.thucung.ten }}</td>
                  <td>{{ hoaDon.idlichhen.dichvu.tendichvu }}</td>
                  <td>{{ formatDate(hoaDon.date) }}</td>
                  <td>{{ formatCurrency(hoaDon.sotien) }}</td>
                  <td><span class="badge bg-success">{{ getTrangThai(hoaDon.trangthai) }}</span></td>
                  <td>

                    <button @click="taiHoaDon(hoaDon.id)" class="btn btn-sm btn-outline-success">
                      <i class="fas fa-check-circle me-1"></i> Tải hóa đơn
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useCheckInStore} from '~/stores/CheckInStores'
import {ref, onMounted} from "vue";
import {useQuanLyHoaDonStore} from '~/stores/QuanLyHoaDon';
import Swal from 'sweetalert2';
import {type Lichhen, Thucung} from "~/models/LichSuDatLich";
import HoaDonKhachHang from "~/models/HoaDonKhachHang";
const useQuanLyHoaDon = useQuanLyHoaDonStore();
const checkInStore = useCheckInStore()

const hoaDonList = ref<HoaDonKhachHang[]>([]);
const hoaDonThanhToanList = ref([]);

const fetchHoaDon = async () => {
  await checkInStore.fetchHoaDon();
  hoaDonList.value = checkInStore.ListHoaDon;
  console.log(hoaDonList.value);
};

const fetchHoaDonThanhToan = async () => {
  await checkInStore.fetchHoaDonDaThanhToan();
  hoaDonThanhToanList.value = checkInStore.ListHoaDonDaThanhToan;

}

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(amount);
};

const getTrangThai = (status) => {
  switch (status) {
    case 0:
      return 'Thành công';
    case 1:
      return 'Thất bại';
    case 3:
      return 'Chờ thanh toán'
    case 6:
      return 'Thanh toán thành công'
    case 4 :
      return 'Chờ xác nhận'
    default:
      return 'Không xác định';
  }
};

function taiHoaDon(id: string) {
  useQuanLyHoaDon.inHoaDon(id)
  fetchHoaDon();
  fetchHoaDonThanhToan();
};


async function thanhToanHoaDon(id: string) {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: "Bạn có chắc chắn có muốn thanh toán cho hóa đơn này ?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    checkInStore.checkIn(id);
    fetchHoaDon();
    fetchHoaDonThanhToan();
  }
}

onMounted(() => {
  fetchHoaDon();
  fetchHoaDonThanhToan();
});
</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
</style>