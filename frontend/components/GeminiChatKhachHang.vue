<script setup lang="ts">
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useAIStore} from '~/stores/Gemini'
import {useQuanLyLichHenKhachHang} from "~/stores/QuanLyLichHenKhachHang";
import {useUserStore} from "~/stores/user";
import {useServiceStore} from "~/stores/DichVuStores";
import {useVoucherStore} from "~/stores/VorchersStores";
import {useI18n} from 'vue-i18n';

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
  chatHistory.value.push({role: 'thinking', content: t('wait_a_minute_master_I_m_thinking')})

  try {
    const welcomeMessage = `
          Bạn là 'Yellow Cat', nhân viên chăm sóc khách hàng của PetHaven. Hãy:
          1. Gọi khách bằng biệt danh dễ thương kèm icon phù hợp với thú cưng.
          2. Trả lời về thú cưng, chăm sóc và hoạt động của PetHaven.
          3. Chỉ dùng thông tin từ danh sách dịch vụ/khuyến mãi khi được hỏi.
          4. Trả lời hài hước, thân thiện, ngắn gọn, đầy đủ, thêm icon chó mèo.
          5. Dùng số liệu chính xác về giá/giảm giá nếu có.
          6. Đề nghị liên hệ trực tiếp nếu không chắc chắn.

          Dịch vụ: ${dichVuForAnalysis}
          Khuyến mãi: ${khuyenMaiForAnalysis}
          Thông tin khách hàng: ${userForAnalysis}
          Lịch sử hẹn: ${history}

          🐶🐕🐩🐾 | 🐱🐈🐈‍⬛🐾
          `
    const response = await aiStore.sendMessage(welcomeMessage)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({role: 'ai', content: response})
  } catch (error) {
    console.error("Error sending welcome message:", error)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({role: 'system', content: t('sleepdeep')})
  } finally {
    isThinking.value = false
  }
}

const sendMessageToAI = async () => {
  if (!userInput.value.trim()) return

  const userMessage = userInput.value
  chatHistory.value.push({role: 'user', content: userMessage})
  userInput.value = ''
  isLoading.value = true
  isThinking.value = true

  try {
    chatHistory.value.push({role: 'thinking', content: t('wait_a_minute_master_I_m_thinking')})
    const response = await aiStore.sendMessage(userMessage)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({role: 'ai', content: response})
  } catch (error) {
    console.error("Error sending message to AI:", error)
    chatHistory.value.pop() // Remove the thinking message
    chatHistory.value.push({role: 'system', content: t('sleepdeep')})
  } finally {
    isLoading.value = false
    isThinking.value = false
  }
}
</script>


<template>
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
      <input class="col-10"
             v-model="userInput"
             @keyup.enter="sendMessageToAI"
             :placeholder="t('enter_your_message')"
             :disabled="isLoading"
      />
      <button @click="sendMessageToAI" :disabled="isLoading" class="custom-button col-2">
        {{ isLoading ? t('sending') : t('send') }}
      </button>
    </div>
  </div>
  <div class="text-center p-4 text fs-6">
    {{ t('chatBotsGod') }}
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


input {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}


button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
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
</style>