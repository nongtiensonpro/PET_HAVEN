<script setup lang="ts">
import { ref } from 'vue'
import { useAIStore } from '~/stores/Gemini'

const aiStore = useAIStore()
const userInput = ref('')
const chatHistory = ref([])
const isLoading = ref(false)

const sendMessageToAI = async () => {
  if (!userInput.value.trim()) return

  const userMessage = userInput.value
  chatHistory.value.push({ role: 'user', content: userMessage })
  userInput.value = ''
  isLoading.value = true

  try {
    const response = await aiStore.sendMessage(userMessage)
    chatHistory.value.push({ role: 'ai', content: response })
  } catch (error) {
    console.error("Error sending message to AI:", error)
    chatHistory.value.push({ role: 'system', content: 'Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn.' })
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="chat-container">
    <div class="chat-history">
      <div v-for="(message, index) in chatHistory" :key="index" class="message-wrapper">
        <div :class="['message', message.role]">
          {{ message.content }}
        </div>
      </div>
    </div>
    <div class="input-container">
      <input
        v-model="userInput"
        @keyup.enter="sendMessageToAI"
        placeholder="Nhập tin nhắn của bạn..."
        :disabled="isLoading"
      />
      <button @click="sendMessageToAI" :disabled="isLoading">
        {{ isLoading ? 'Đang gửi...' : 'Gửi' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
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

.input-container {
  display: flex;
  padding: 10px;
  background-color: #f8f8f8;
}

input {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  margin-left: 10px;
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>