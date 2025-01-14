<script setup lang="ts">
import {onMounted, ref, computed} from 'vue';
import {useQuanLyLichHenAdminStore} from '~/stores/QuanLyLichHenAdmin';
import {useAIThongKeStore} from '~/stores/AiThongKe';
import DichVu from "~/models/DichVu";
import {useServiceStore} from "~/stores/DichVuStores";
import {useVoucherStore} from "~/stores/VorchersStores";
import {useThongKeStore} from '~/stores/ThongKeStores'
import {useI18n} from 'vue-i18n';

const {t} = useI18n();

const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const aiStore = useAIThongKeStore();

const thongKe = useThongKeStore();
const lichhen = ref([]);
const userInput = ref('');
const chatHistory = ref([]);
const isLoading = ref(false);
const isThinking = ref(false);
const thongKeData = ref([]);

const loadData = async () => {
  const data = await useQuanLyAdmin.fetchHoaDonKhachHangs();
  lichhen.value = data;
  await getInitialSummary();
};
const serviceStore = useServiceStore();
const voucherStore = useVoucherStore();

const services = computed(() =>
    serviceStore.services.filter((service: DichVu) => service.trangthai && service.hien)
);

const vouchers = computed(() =>
    voucherStore.ListVoucher.filter(voucher => voucher.trangthai)
);

const startDate = '2000-01-01';
const endDate = new Date().toISOString().split('T')[0];
const top10Users = thongKe.topUsers;
const thongTinKinhDoanh = thongKeData.value;
const doanhThuTheoNgay = thongKe.thongKeItems;

const dataForAnalysis = JSON.stringify(lichhen.value);
const doanhThuTheoDichVu = ref(null);


await thongKe.getUserThongKeTheoNgay(startDate, endDate);
doanhThuTheoDichVu.value = await thongKe.getDoanhThuTheoDichVu(startDate, endDate);
const getInitialSummary = async () => {
  isLoading.value = true;
  isThinking.value = true;
  chatHistory.value.push({role: 'thinking', content: t('wait_a_minute_master_I_m_thinking')});

  try {
    // Lấy dữ liệu từ các nguồn khác nhau
    console.log(doanhThuTheoDichVu.value+'Miu miu miu')
    const dataForAnalysis = JSON.stringify(lichhen.value);
    const dichVuForAnalysis = JSON.stringify(services.value);
    const khuyenMaiForAnalysis = JSON.stringify(vouchers.value);
    // Lấy thêm dữ liệu từ ThongKeStore

    const prompt = `
    Bạn là một chuyên gia phân tích dữ liệu cho cửa hàng thú cưng PetHaven.
    Phân tích các dữ liệu sau đây và cung cấp bản về các số liệu kinh doanh chính:
    
    1. Dữ liệu lịch hẹn và hóa đơn: 
    ${dataForAnalysis}

   2. Dữ liệu thống kê:
      - Doanh thu theo dịch vụ :  ${doanhThuTheoDichVu.value} theo quy tắc (tendichvu,idTuyChonDichVu,giaTien) :: Dịch vụ chi tiết  ${JSON.stringify(serviceStore.services)}
      - Doanh thu theo ngày : ${JSON.stringify(doanhThuTheoNgay, null, 2)} hãy  tự tổng hợp và phân tích ra tháng và năm tương ứng
      - Thông tin kinh doanh : ${JSON.stringify(thongTinKinhDoanh, null, 2)}
    
    3. Dịch vụ có trong cửa hàng:
    ${dichVuForAnalysis}
    
    4. Các voucher có trong cửa hàng:
    ${khuyenMaiForAnalysis}

    Chú giải trạng thái:
    """
    0: Trạng thái thành công
    1: Trạng thái đã thay đổi
    2: Trạng thái đã hủy
    3: Trạng thái đang chờ thanh toán
    4: Trạng thái đang chờ xác nhận
    5: Trạng thái trống
    6: Trạng thái thanh toán thành công
    7: Trạng thái đã hoàn tiền
    8: Trạng thái đang chờ dịch vụ
    """

    Hãy tuân thủ các quy tắc sau:
    1. Phân tích và tổng hợp tất cả các nguồn dữ liệu trên.
    2. Đưa ra các nhận xét về:
       - Doanh thu theo từng dịch vụ và xu hướng
       - Top khách hàng và hành vi của họ
       - Hiệu quả của các chương trình khuyến mãi
       - Tỷ lệ hủy/thay đổi lịch hẹn
    3. Đề xuất các chiến lược cụ thể để:
       - Tăng doanh thu cho các dịch vụ hiệu quả thấp
       - Giữ chân top khách hàng
       - Cải thiện tỷ lệ hoàn thành lịch hẹn
    4. Sử dụng số liệu cụ thể để hỗ trợ các nhận định
    5. Nếu phát hiện điểm bất thường trong dữ liệu, hãy nêu rõ

    Hãy trả lời một cách ngắn gọn, súc tích nhưng đầy đủ thông tin quan trọng.
    `;

    const response = await aiStore.sendMessage(prompt);
    chatHistory.value.pop();
    chatHistory.value.push({role: 'ai', content: response});
  } catch (error) {
    console.error("Error getting initial summary:", error);
    chatHistory.value.pop();
    chatHistory.value.push({role: 'system', content: 'Xin lỗi, có lỗi xảy ra khi tạo tóm tắt ban đầu.'});
  } finally {
    isLoading.value = false;
    isThinking.value = false;
  }
};


onMounted(async () => {
  await loadData();
});

const sendMessageToAI = async () => {
  if (!userInput.value.trim()) return;
  const userMessage = userInput.value;
  chatHistory.value.push({role: 'user', content: userMessage});
  userInput.value = '';
  isLoading.value = true;
  isThinking.value = true;

  try {
    chatHistory.value.push({role: 'thinking', content: t('analyzing_data')});


    const prompt = `
    Dựa trên dữ liệu sau đây:
    
    1. Dữ liệu lịch hẹn và hóa đơn: 
    ${dataForAnalysis}
    
    2. Dữ liệu thống kê:
      - Doanh thu theo dịch vụ :  ${doanhThuTheoDichVu.value} theo quy tắc (tên dịch vụ , số lần sử dụng , giá tiền) :: Dịch vụ chi tiết  ${JSON.stringify(serviceStore.services)}
      - Doanh thu theo ngày : ${JSON.stringify(doanhThuTheoNgay, null, 2)} hãy  tự tổng hợp và phân tích ra tháng và năm tương ứng
      - Thông tin kinh doanh : ${JSON.stringify(thongTinKinhDoanh, null, 2)}
      - Top 10 khách hàng : ${JSON.stringify(top10Users, null, 2)}
    
    Câu hỏi của người dùng: ${userMessage}
    
    Hãy trả lời câu hỏi trên dựa vào dữ liệu được cung cấp. Nếu câu hỏi liên quan đến doanh thu hoặc hiệu quả kinh doanh, hãy sử dụng số liệu cụ thể để minh họa.
    `;

    const response = await aiStore.sendMessage(prompt);
    chatHistory.value.pop();
    chatHistory.value.push({role: 'ai', content: response});
  } catch (error) {
    console.error("Error:", error);
    chatHistory.value.pop();
    chatHistory.value.push({role: 'system', content: 'Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn.'});
  } finally {
    isLoading.value = false;
    isThinking.value = false;
  }
};
</script>

<template>
  <div class="container p-4">
    <h2 class="mb-4">{{ t('analyzing_store_data_with_AI') }}</h2>
    <div class="chat-container">
      <div class="chat-history">
        <div v-for="(message, index) in chatHistory" :key="index" class="message-wrapper">
          <div :class="['message', message.role]">
            {{ message.content }}
            <span v-if="message.role === 'thinking'" class="thinking-icons">
              🤔💭🧠
            </span>
          </div>
        </div>
      </div>
      <div class="input-container row p-3">
        <input class="col-10" v-model="userInput" @keyup.enter="sendMessageToAI" :placeholder="t('enter_your_message')"
               :disabled="isLoading"/>
        <button @click="sendMessageToAI" :disabled="isLoading" class="custom-button col-2">
          {{ isLoading ? t('sending') : t('send') }}
        </button>
      </div>
    </div>
    <div class="text-center p-4 text fs-6">
      {{ t('chatBotsGod') }}
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
}

.chat-history {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.message-wrapper {
  display: flex;
  margin-bottom: 10px;
  width: 100%;
}

.message {
  padding: 8px;
  border-radius: 8px;
  max-width: 70%;
  word-wrap: break-word;
  white-space: pre-wrap;
  overflow-wrap: break-word;
}

.user {
  background-color: #e6f2ff;
  margin-left: auto;
}

.ai {
  background-color: #f0f0f0;
  margin-right: auto;
}

.system {
  background-color: #ffe6e6;
  margin-left: auto;
  margin-right: auto;
}

.thinking {
  background-color: #fff3cd;
  margin-right: auto;
  font-style: italic;
}

.thinking-icons {
  margin-left: 5px;
  font-size: 1.2em;
}

.input-container {
  display: flex;
  padding: 10px;
}

input {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.custom-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>