<script setup lang="ts">
import { onMounted, ref, onUnmounted } from 'vue';
import { useQuanLyLichHenAdminStore } from '~/stores/QuanLyLichHenAdmin';
import { useAIThongKeStore } from '~/stores/AiThongKe';
import type { Lichhen } from "~/models/LichSuDatLich";

const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const aiStore = useAIThongKeStore();

const lichhen = ref<Lichhen[]>([]);
const userInput = ref('');
const chatHistory = ref([]);
const isLoading = ref(false);
const isThinking = ref(false);

const loadData = async () => {
  const data = await useQuanLyAdmin.fetchHoaDonKhachHangs();
  lichhen.value = data;
  await getInitialSummary();
};

const getInitialSummary = async () => {
  isLoading.value = true;
  isThinking.value = true;
  chatHistory.value.push({ role: 'thinking', content: 'Đang phân tích yêu cầu...' });

  try {
    const dataForAnalysis = JSON.stringify(lichhen.value);
    const prompt = `Phân tích dữ liệu sau đây và cung cấp bản tóm tắt ngắn gọn về các số liệu kinh doanh chính: ${dataForAnalysis}`;

    const response = await aiStore.sendMessage(prompt);
    chatHistory.value.pop(); // Remove the thinking message
    chatHistory.value.push({ role: 'ai', content: response });
  } catch (error) {
    console.error("Error getting initial summary:", error);
    chatHistory.value.pop(); // Remove the thinking message
    chatHistory.value.push({ role: 'system', content: 'Xin lỗi, có lỗi xảy ra khi tạo tóm tắt ban đầu.' });
  } finally {
    isLoading.value = false;
    isThinking.value = false;
  }
};

onMounted(loadData);

const sendMessageToAI = async () => {
  if (!userInput.value.trim()) return;
  const userMessage = userInput.value;
  chatHistory.value.push({ role: 'user', content: userMessage });
  userInput.value = '';
  isLoading.value = true;
  isThinking.value = true;

  try {
    chatHistory.value.push({ role: 'thinking', content: 'Đang phân tích dữ liệu...' });

    // Prepare the data for AI analysis
    const dataForAnalysis = JSON.stringify(lichhen.value);
    const prompt = `Phân tích dữ liệu sau đây và cung cấp số liệu kinh doanh: ${dataForAnalysis}\n\nUser question: ${userMessage}`;

    const response = await aiStore.sendMessage(prompt);
    chatHistory.value.pop(); // Remove the thinking message
    chatHistory.value.push({ role: 'ai', content: response });
  } catch (error) {
    console.error("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn:", error);
    chatHistory.value.pop(); // Remove the thinking message
    chatHistory.value.push({ role: 'system', content: 'Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn.' });
  } finally {
    isLoading.value = false;
    isThinking.value = false;
  }
};
</script>

<template>
  <div class="container p-4">
    <h2 class="mb-4">Phân tích dữ liệu cửa hàng với AI</h2>
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
        <input class="col-10" v-model="userInput" @keyup.enter="sendMessageToAI" placeholder="Nhập câu hỏi của bạn..."
          :disabled="isLoading" />
        <button @click="sendMessageToAI" :disabled="isLoading" class="custom-button col-2">
          {{ isLoading ? 'Đang xử lý...' : 'Gửi' }}
        </button>
      </div>
    </div>
    <div class="text-center p-4 text fs-6">
      AI có thể mắc lỗi. Hãy kiểm tra các thông tin quan trọng.
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