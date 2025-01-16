<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useAIStore } from '~/stores/Gemini'
import { useQuanLyLichHenKhachHang } from "~/stores/QuanLyLichHenKhachHang";
import { useUserStore } from "~/stores/user";
import { useServiceStore } from "~/stores/DichVuStores";
import { useVoucherStore } from "~/stores/VorchersStores";
import { useI18n } from 'vue-i18n';

const {t} = useI18n();


const lichHenStore = useQuanLyLichHenKhachHang();
const aiStore = useAIStore()
const userInput = ref('')
const chatHistory = ref([])
const isLoading = ref(false)
const isThinking = ref(false)
const userStore = useUserStore();
const serviceStore = useServiceStore();
const voucherStore = useVoucherStore();

const user = computed(() => userStore.userInfo);

onMounted(() => {
  sendWelcomeMessage()
})

onUnmounted(() => {
  lichHenStore.fetchAppointments();
})

const userForAnalysis = JSON.stringify(user.value);
const dichVuForAnalysis = JSON.stringify(serviceStore.fetchServices());
const khuyenMaiForAnalysis = JSON.stringify(voucherStore.fetchVoucher());
const history = JSON.stringify(lichHenStore.appointments);
const sendWelcomeMessage = async () => {
  isThinking.value = true
  chatHistory.value.push({ role: 'thinking', content: t('wait_a_minute_master_I_m_thinking') })

  try {
    const welcomeMessage = `
          Bạn là 'Yellow Cat', nhân viên chăm sóc khách hàng của PetHaven. Hãy:
          1. Gọi khách bằng biệt danh dễ thương kèm icon phù hợp với thú cưng.
          2. Trả lời về thú cưng, chăm sóc và hoạt động của PetHaven.
          3. Chỉ dùng thông tin từ danh sách dịch vụ/khuyến mãi khi được hỏi.
          4. Trả lời hài hước, thân thiện, ngắn gọn, đầy đủ, thêm icon chó mèo.
          5. Dùng số liệu chính xác về giá/giảm giá nếu có.
          6. Đề nghị liên hệ trực tiếp nếu không chắc chắn.

          - Dịch vụ: ${dichVuForAnalysis} (Đơn vị tiền USD)
          - Khuyến mãi: ${khuyenMaiForAnalysis}
          - Thông tin khách hàng: ${userForAnalysis}
          - Lịch sử hẹn: ${history}

          🐶🐕🐩🐾 | 🐱🐈🐈‍⬛🐾
          Hãy trả lời dưới dạng HTML sẽ hiển thị trong thẻ V-HTML trong NUXTJS với các thẻ div được style màu sắc và font chữ phù hợp . Sử dụng màu pastel và font chữ dễ thương.
           Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62); và font-size: 1rem
          `
    const response = await aiStore.sendMessage(welcomeMessage)
    chatHistory.value.pop() // Remove the thinking message
    const cleanedResponse = response.replace(/```html|```/g, '')
    chatHistory.value.push({ role: 'ai', content: cleanedResponse })
  } catch (error) {
    console.error("Error sending welcome message:", error)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({ role: 'system', content: t('sleepdeep') })
  } finally {
    isThinking.value = false
  }
}

const sendMessageToAI = async () => {
  if (!userInput.value.trim()) return

  const userMessage = userInput.value
  chatHistory.value.push({ role: 'user', content: userMessage })
  userInput.value = ''
  isLoading.value = true
  isThinking.value = true

  try {
    chatHistory.value.push({ role: 'thinking', content: t('wait_a_minute_master_I_m_thinking') })
    const response = await aiStore.sendMessage(userMessage)
    chatHistory.value.pop() // Remove the thinking message
    const cleanedResponse = response.replace(/```html|```/g, '')
    chatHistory.value.push({ role: 'ai', content: cleanedResponse })
  } catch (error) {
    console.error("Error sending message to AI:", error)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({ role: 'system', content: t('sleepdeep') })
  } finally {
    isLoading.value = false
    isThinking.value = false
  }
}
</script>

<template>
  <div class="chat-container-wrapper">
    <div class="chat-container">
      <div class="chat-history" ref="chatHistoryRef">
        <div v-for="(message, index) in chatHistory" :key="index" :class="['message', message.role]">
          <div v-if="message.role === 'ai'" class="message-content" v-html="message.content"></div>
          <div v-else class="message-content">{{ message.content }}</div>
          <div v-if="message.role === 'thinking'" class="thinking-animation">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
      <div class="input-section">
        <div class="input-wrapper">
          <input v-model="userInput" @keyup.enter="sendMessageToAI" :placeholder="t('enter_your_message')"
            :disabled="isLoading" />
          <button @click="sendMessageToAI" :disabled="isLoading" class="send-button">
            <span v-if="!isLoading">{{ t('send') }}</span>
            <span v-else class="loading-text">{{ t('sending') }}</span>
          </button>
        </div>
      </div>
    </div>
    <div class="chat-footer">
      {{ t('chatBotsGod') }}
    </div>
  </div>
</template>

<style scoped>
.chat-container-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.chat-container {
  background: rgba(246, 246, 234, 0.62);
  border-radius: 8px;
  overflow: hidden;
  height: calc(150vh - 250px);
  display: flex;
  flex-direction: column;
}

.chat-history {
  flex-grow: 1;
  overflow-y: auto;
  padding: 0.8rem;
  scroll-behavior: smooth;
}

.message {
  margin-bottom: 0.8rem;
  padding: 0.6rem;
  border-radius: 8px;
  max-width: 100%;
  animation: fadeIn 0.3s ease-in-out;
}

.message-content {
  font-size: 1rem;
  line-height: 1.3;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.user {
  margin-left: auto;
  background: #007bff;
  color: white;
}

.ai {
  margin-right: auto;
  background: white;
  color: #2c3e50;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  white-space: normal;
}

.system {
  margin: 0 auto;
  background: #ff6b6b;
  color: white;
}

.thinking {
  margin-right: auto;
  background: #ffd93d;
  color: #2c3e50;
}

.input-section {
  padding: 0.8rem;
  background: white;
  border-top: 1px solid #eee;
}

.input-wrapper {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

input {
  flex-grow: 1;
  padding: 0.5rem 0.8rem;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #007bff;
}

.send-button {
  padding: 0.5rem 1rem;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
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
  background: #ffd93d;
  border-radius: 50%;
  animation: bounce 0.8s infinite;
}

.thinking-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.thinking-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

.chat-footer {
  text-align: center;
  padding: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.message-content :deep(div) {
  margin: 0.2rem 0;
}

.message-content :deep(p) {
  margin: 0.2rem 0;
}

.message-content :deep(ul), 
.message-content :deep(ol) {
  margin: 0.2rem 0;
  padding-left: 1rem;
}

.message-content :deep(li) {
  margin: 0.1rem 0;
}

.message-content :deep(br) {
  display: none;
}

.message-content :deep(p:empty) {
  display: none;
}

.message.ai .message-content {
  white-space: normal;
}

.message-content :deep(strong) {
  font-weight: 600;
}

.message-content :deep(em) {
  font-style: italic;
}

.message-content :deep(code) {
  background: rgba(0, 0, 0, 0.05);
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: monospace;
}

.message-content :deep(pre) {
  background: rgba(0, 0, 0, 0.05);
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
}

.message-content :deep(a) {
  color: #007bff;
  text-decoration: none;
}

.message-content :deep(a:hover) {
  text-decoration: underline;
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

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-5px);
  }
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