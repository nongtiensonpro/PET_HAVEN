<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})
</script>

<template>
  <button type="button" class="custom-button">dadsadasdas</button>
  <div class="text fs-1">DSDSDS</div>


  <button type="button" class="btn btn-sm btn-outline-primary m-1" data-bs-toggle="modal"
          data-bs-target="#exampleModal">
    Chi tiết
  </button>

  <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">Thông tin chi tiết hóa đơn #{{
              hoaDon.id
            }}</h5>
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
                <strong>Ngày thanh toán:</strong>
                {{ hoaDon.ngaythanhtoan ? formatDate(hoaDon.ngaythanhtoan) : 'Chưa thanh toán' }}
              </div>
              <div class="mb-3">
                <strong>Phương thức thanh toán:</strong>
                {{ hoaDon.phuongthucthanhtoan || 'Chưa xác định' }}
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
                <strong>ID:</strong> {{ hoaDon.idlichhen.thucung.id }}
              </div>
              <div class="mb-3">
                <strong>Tên thú cưng:</strong> {{ hoaDon.idlichhen.thucung.ten }}
              </div>
              <div class="mb-3">
                <strong>Cân nặng:</strong> {{ hoaDon.idlichhen.thucung.cannang }} kg
              </div>
              <div class="mb-3">
                <strong>Tuổi:</strong> {{ hoaDon.idlichhen.thucung.tuoi }} tuổi
              </div>
              <div class="mb-3">
                <strong>Giống:</strong> {{ hoaDon.idlichhen.thucung.giong }}
              </div>
              <div class="mb-3">
                <strong>ID Tài khoản:</strong> {{ hoaDon.idlichhen.thucung.idtaikhoan }}
              </div>
              <div class="mb-3">
                <strong>Giới tính:</strong> {{ hoaDon.idlichhen.thucung.gioitinh ? 'Đực' : 'Cái' }}
              </div>
              <div class="mb-3">
                <strong>Loại:</strong> {{ hoaDon.idlichhen.thucung.cophaimeokhong ? 'Mèo' : 'Chó' }}
              </div>
              <div class="mb-3">
                <strong>Tình trạng sức khỏe:</strong>
                {{ hoaDon.idlichhen.thucung.tinhtrangsuckhoe || 'Không có thông tin' }}
              </div>
              <div class="mb-3">
                <strong>Mô tả:</strong> {{ hoaDon.idlichhen.thucung.mota || 'Không có mô tả' }}
              </div>
            </div>
          </div>
          <div class="row mt-4">
            <div class="col-md-6">
              <h6 class="border-bottom pb-2 mb-3">Thông tin dịch vụ</h6>
              <div class="mb-3">
                <strong>ID Lịch hẹn:</strong> {{ hoaDon.idlichhen.id }}
              </div>
            </div>
            <div class="col-md-6">
              <h6 class="border-bottom pb-2 mb-3">Thông tin khuyến mãi</h6>
              <div v-if="hoaDon.idgiamgia">
                <div class="mb-3">
                  <strong>Mô tả:</strong> {{ hoaDon.idgiamgia.mota }}
                </div>
                <div class="mb-3">
                  <strong>Thời gian:</strong> {{ formatDate(hoaDon.idgiamgia.ngaybatdau) }} -
                  {{ formatDate(hoaDon.idgiamgia.ngayketthuc) }}
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
          <button type="button" v-if="hoaDon.idlichhen.trangthai === 3" class="btn btn-primary"
                  @click="thanhToanHoaDon(hoaDon.idlichhen.id)">Thanh toán
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>