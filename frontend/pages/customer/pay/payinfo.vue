<script setup lang="ts">
import {ref, computed} from 'vue';
import {useMauKhachDatDichVu} from "~/stores/MauKhachDatDichVu";
import Swal from 'sweetalert2';

const {getTempData} = useMauKhachDatDichVu()
const token = localStorage.getItem('access_token');
const isLoading = ref(false);
const countdown = ref(10);
const showOverlay = ref(false);

definePageMeta({
  middleware: ['auth']
})

function thanhToanTaiQuay() {
  return navigateTo('/customer/success');
}

function startCountdown() {
  showOverlay.value = true;
  isLoading.value = true;
  countdown.value = 10;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      isLoading.value = false;
      showOverlay.value = false;
    }
  }, 1000);
}
const dichVuVaThuCungKhachHangDat = getTempData();
async function thanhThayDoiLich() {
  return navigateTo('/chi-tiet-lich/' + dichVuVaThuCungKhachHangDat?.id);
}

async function thanhToanOnline() {
  const result = await Swal.fire({
    title: 'Xác nhận',
    text: "Bạn có chắc chắn có muốn thanh toán chứ ?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Có',
    cancelButtonText: 'Không'
  });
  if (result.isConfirmed) {
    const {getTempData} = useMauKhachDatDichVu()
    const tempData = computed(() => getTempData())

    startCountdown(); // Start the countdown

    fetch('http://localhost:8080/api/payPal/payment/create', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'idLichHen': tempData.value.id
      }
    })
        .then(response => response.text())
        .then(url => {
          if (url) {
            window.location.href = url;
          } else {
            console.error("Không thể tạo thanh toán");
            isLoading.value = false;
            showOverlay.value = false;
          }
        })
        .catch(error => {
          console.error("Lỗi:", error);
          isLoading.value = false;
          showOverlay.value = false;
        });
  }
}
</script>

<template>
  <div class="container">

    <div class="card no-border">
      <div class="col-12">
        <div class="text text-center">
          <div>Chúc mừng bạn đã đặt lịch hẹn thành công</div>
          <p>Bạn có 20 phút để thanh toán và đổi hoặc hủy lịch</p>
          <p>Sau 20 phút mà không thanh toán thì lịch này sẽ tự hủy</p>
        </div>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="row">
            <div class="col-md-6">
              <div class="card mb-4">
                <div class="card-body">
                  <h5 class="card-title text-center">Thanh toán với PayPal</h5>
                  <img src="~/assets/image/PayPal-logo.png" alt="PayPal logo" class="img-fluid mb-3">
                  <div class="row">
                    <div class="col">
                      <button @click="thanhToanOnline" class="custom-button">
                        Thanh toán ngay
                      </button>
                    </div>
                    <div class="col">
                      <button @click="thanhThayDoiLich" class="custom-button">
                        Hủy hoặc thay đổi thời gian
                      </button>
                    </div>
               </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title mb-4">Thông tin về PayPal</h5>
                  <div class="accordion" id="paypalInfo">
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseOne">
                          PayPal là gì và hoạt động như thế nào?
                        </button>
                      </h2>
                      <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <p>PayPal là dịch vụ thanh toán trực tuyến cho phép:</p>
                          <ul>
                            <li>Gửi và nhận tiền điện tử</li>
                            <li>Bảo vệ thông tin tài khoản và thẻ tín dụng</li>
                            <li>Hoạt động như trung gian giữa người mua và người bán</li>
                          </ul>
                          <p>Để sử dụng:</p>
                          <ol>
                            <li>Tạo tài khoản PayPal</li>
                            <li>Liên kết với thẻ tín dụng, ghi nợ hoặc tài khoản ngân hàng</li>
                            <li>Sử dụng email và mật khẩu PayPal để giao dịch</li>
                          </ol>
                        </div>
                      </div>
                    </div>
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseTwo">
                          Cách đăng ký và xác minh tài khoản PayPal
                        </button>
                      </h2>
                      <div id="collapseTwo" class="accordion-collapse collapse" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <h6>Đăng ký:</h6>
                          <ol>
                            <li>Truy cập PayPal.com và chọn "Đăng ký"</li>
                            <li>Chọn loại tài khoản (cá nhân/doanh nghiệp)</li>
                            <li>Nhập thông tin cá nhân</li>
                            <li>Liên kết phương thức thanh toán</li>
                            <li>Xác minh email</li>
                          </ol>
                          <h6>Xác minh tài khoản:</h6>
                          <ol>
                            <li>Nhận mã xác minh qua email/SMS</li>
                            <li>Nhập mã vào trang web PayPal</li>
                            <li>Thiết lập mật khẩu và câu hỏi bảo mật</li>
                            <li>Cung cấp thông tin xác minh danh tính (nếu cần)</li>
                          </ol>
                        </div>
                      </div>
                    </div>
                    <div class="accordion-item">
                      <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapseThree">
                          Phí giao dịch của PayPal
                        </button>
                      </h2>
                      <div id="collapseThree" class="accordion-collapse collapse" data-bs-parent="#paypalInfo">
                        <div class="accordion-body">
                          <p>Phí giao dịch phụ thuộc vào loại giao dịch và vị trí:</p>
                          <table class="table table-striped">
                            <thead>
                            <tr>
                              <th>Loại giao dịch</th>
                              <th>Phí</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                              <td>Giao dịch trong nước</td>
                              <td>2,5% + 1.000 VND</td>
                            </tr>
                            <tr>
                              <td>Giao dịch quốc tế</td>
                              <td>4,4% + 1.000 VND + phí chuyển đổi ngoại tệ</td>
                            </tr>
                            <tr>
                              <td>Rút tiền</td>
                              <td>1,5% + 1.000 VND</td>
                            </tr>
                            <tr>
                              <td>Chuyển tiền</td>
                              <td>0,5% + 1.000 VND</td>
                            </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Overlay and countdown -->
    <div v-if="showOverlay" class="overlay">
      <div class="overlay-content">
        <p>Đang chuyển hướng đến PayPal...</p>
        <p>Vui lòng chờ {{ countdown }} giây</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.no-border {
  border: none !important;
  box-shadow: none !important;
}

.card-img-top {
  width: 100%;
  height: auto;
  max-height: 350px;
  object-fit: contain;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.overlay-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

.text {
  font-size: 1.5rem;
  padding-top: 10px;
}
</style>