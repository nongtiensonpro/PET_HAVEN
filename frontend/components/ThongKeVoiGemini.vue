<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {useQuanLyLichHenAdminStore} from '~/stores/QuanLyLichHenAdmin';
import {useAIThongKeStore} from '~/stores/AiThongKe';
import DichVu from "~/models/DichVu";
import {useServiceStore} from "~/stores/DichVuStores";
import {useVoucherStore} from "~/stores/VorchersStores";
import {useThongKeStore} from '~/stores/ThongKeStores'
import {useI18n} from 'vue-i18n';

const { t } = useI18n();

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
  lichhen.value = await useQuanLyAdmin.fetchHoaDonKhachHangs();
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
const thongTinKinhDoanh = thongKeData.value;
const doanhThuTheoNgay = thongKe.thongKeItems;

const dataForAnalysis = JSON.stringify(lichhen.value);
const doanhThuTheoDichVu = ref(null);


await thongKe.getUserThongKeTheoNgay(startDate, endDate);
doanhThuTheoDichVu.value = await thongKe.getDoanhThuTheoDichVu(startDate, endDate);
const getInitialSummary = async () => {
  isLoading.value = true;
  isThinking.value = true;
  chatHistory.value.push({ role: 'thinking', content: t('wait_a_minute_master_I_m_thinking') });

  try {
    // Lấy dữ liệu từ các nguồn khác nhau
    console.log(doanhThuTheoDichVu.value + 'Miu miu miu')
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
    Hãy trả lời dưới dạng HTML sẽ hiển thị trong thẻ V-HTML trong NUXTJS với các thẻ div được style màu sắc và font chữ phù hợp . Sử dụng màu pastel và font chữ dễ thương.
    Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62); và font-size: 1rem
    `;

    const response = await aiStore.sendMessage(prompt);
    const cleanedResponse = response.trim().replace(/```html|```/g, '');
    chatHistory.value.pop();
    chatHistory.value.push({
      role: 'ai',
      content: cleanedResponse,
      fullMessage: cleanedResponse
    });
  } catch (error) {
    console.error("Error getting initial summary:", error);
    chatHistory.value.pop();
    chatHistory.value.push({ role: 'system', content: t('error_occurred') });
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
  chatHistory.value.push({ role: 'user', content: userMessage });
  userInput.value = '';
  isLoading.value = true;
  isThinking.value = true;

  try {
    chatHistory.value.push({ role: 'thinking', content: t('analyzing_data') });


    const prompt = `
    Dựa trên dữ liệu sau đây:
    
    1. Dữ liệu lịch hẹn và hóa đơn: 
    ${dataForAnalysis}
    
    2. Dữ liệu thống kê:
      - Doanh thu theo dịch vụ :  ${doanhThuTheoDichVu.value} theo quy tắc (tên dịch vụ , số lần sử dụng , giá tiền) :: Dịch vụ chi tiết  ${JSON.stringify(serviceStore.services)} Đơn vị tiền trong hóa đơn và dịch vụ chi tiết là USD ( Đô la Mỹ )
      - Doanh thu theo ngày : ${JSON.stringify(doanhThuTheoNgay, null, 2)} hãy  tự tổng hợp và phân tích ra tháng và năm tương ứng
      - Thông tin kinh doanh : ${JSON.stringify(thongTinKinhDoanh, null, 2)}
    
    Câu hỏi của người dùng: ${userMessage}

    Hãy trả lời câu hỏi trên dựa vào dữ liệu được cung cấp. Nếu câu hỏi liên quan đến doanh thu hoặc hiệu quả kinh doanh, hãy sử dụng số liệu cụ thể để minh họa.
    Hãy trả lời dưới dạng HTML sẽ hiển thị trong thẻ V-HTML trong NUXTJS với các thẻ div được style màu sắc và font chữ phù hợp . Sử dụng màu pastel và font chữ dễ thương. Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62); và font-size: 1rem
    `;

    const response = await aiStore.sendMessage(prompt);
    const cleanedResponse = response.trim().replace(/```html|```/g, '');
    chatHistory.value.pop();
    chatHistory.value.push({
      role: 'assistant',
      content: cleanedResponse,
      fullMessage: cleanedResponse
    });
  } catch (error) {
    console.error("Error:", error);
    chatHistory.value.pop();
    chatHistory.value.push({ role: 'system', content: t('error_occurred') });
  } finally {
    isLoading.value = false;
    isThinking.value = false;
  }
};
</script>

<template>
  <div class="thongke-container">
    <div class="header">
      <h2 class="title">{{ t('analyzing_store_data_with_AI') }}</h2>
    </div>
    <div class="chat-container">
      <div class="chat-history" ref="chatHistoryRef">
        <div v-for="(message, index) in chatHistory" 
             :key="index"
             :class="['message', message.role === 'user' ? 'user-message' : 'ai-message']">
          <div class="message-content" v-html="message.fullMessage || message.content"></div>
          <div v-if="message.role === 'thinking'" class="thinking-animation">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
      <div class="input-section">
        <div class="input-wrapper">
          <input 
            v-model="userInput" 
            @keyup.enter="sendMessageToAI" 
            :placeholder="t('enter_your_message')"
            :disabled="isLoading"
          />
          <button 
            @click="sendMessageToAI" 
            :disabled="isLoading"
            class="send-button"
          >
            <span v-if="!isLoading">{{ t('send') }}</span>
            <span v-else class="loading-text">{{ t('sending') }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.thongke-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header {
  margin-bottom: 2rem;
  text-align: center;
}

.title {
  font-size: 1.8rem;
  color: #2c3e50;
  font-weight: 600;
  margin: 0;
}

.chat-container {
  background: rgba(246, 246, 234, 0.62);
  border-radius: 12px;
  overflow: hidden;
  height: calc(200vh - 250px);
  display: flex;
  flex-direction: column;
}

.chat-history {
  flex-grow: 1;
  overflow-y: auto;
  padding: 1.5rem;
  scroll-behavior: smooth;
}

.message {
  margin-bottom: 1.5rem;
  max-width: 85%;
  padding: 1rem;
  border-radius: 12px;
  animation: fadeIn 0.3s ease-in-out;
}

.user-message {
  margin-left: auto;
  background: #007bff;
  color: white;
}

.ai-message {
  margin-right: auto;
  background: white;
  color: #2c3e50;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-content {
  font-size: 1rem;
  line-height: 1.5;
}

.input-section {
  padding: 1.5rem;
  background: white;
  border-top: 1px solid #eee;
}

.input-wrapper {
  display: flex;
  gap: 1rem;
  align-items: center;
}

input {
  flex-grow: 1;
  padding: 0.8rem 1.2rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #007bff;
}

.send-button {
  padding: 0.8rem 1.5rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.send-button:hover:not(:disabled) {
  background: #0056b3;
}

.send-button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.thinking-animation {
  display: flex;
  gap: 0.3rem;
  margin-top: 0.5rem;
}

.thinking-animation span {
  width: 8px;
  height: 8px;
  background: #007bff;
  border-radius: 50%;
  animation: bounce 0.8s infinite;
}

.thinking-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.thinking-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.loading-text {
  display: inline-block;
  position: relative;
}

/* Custom scrollbar */
.chat-history::-webkit-scrollbar {
  width: 6px;
}

.chat-history::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-history::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-history::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>