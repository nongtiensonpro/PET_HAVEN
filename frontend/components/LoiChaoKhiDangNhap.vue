<script setup lang="ts">
import { useUserStore } from '~/stores/user'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed } from 'vue'
import { useServiceStore } from '~/stores/DichVuStores'
import { useI18n } from "vue-i18n";

const serviceStore = useServiceStore()
const aiStore = useAIStore()
const userStore = useUserStore()

const greeting = ref('')
const isLoading = ref(true)
const { t } = useI18n();
const userInfo = computed(() => {
  if (!userStore.userInfo) return null
  const pets = userStore.userInfo.listThuCung || []
  let petInfo = ''
  if (pets.length === 1) {
    petInfo = `và thú cưng ${pets[0].ten} (${pets[0].giong})`
  } else if (pets.length > 1) {
    petInfo = `và các thú cưng: ${pets.map(pet => `${pet.ten} (${pet.giong})`).join(', ')}`
  }
  return {
    name: userStore.userInfo.name,
    petInfo
  }
})
const dichVuJson = JSON.stringify(serviceStore.services);
onMounted(async () => {
  try {
    let prompt
    if (userInfo.value) {
      prompt = `Chào mừng chủ nhân ${userInfo.value.name} có thú cưng ${userInfo.value.petInfo} đến với PetHaven. Mình xin phép gọi bạn bằng một biệt danh kèm icon  dựa trên thông tin  của bạn  để phù hợp với thú cưng một tên thật dễ thương đáng yêu nha.
        Mình tên là Yellow Cat. Nhân viên cửa hàng PetHaven. Mình rất vui được phục vụ bạn và thú cưng của bạn. 🐾
        Đây là dịch vụ cửa hàng mình ${dichVuJson} và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu..
        Hãy trả lời thật ngắn gọn nhưng đầy đủ thông tin nha bạn yêu.Tối thiểu 100 từ. Hãy trả lời dưới dạng HTML với các thẻ div được style màu sắc và font chữ phù hợp. Sử dụng màu pastel và font chữ dễ thương. Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62);
      `
    } else {
      prompt = `Chào mừng chủ nhân đến với PetHaven! Mình tên là Yellow Cat. Mình rất vui được phục vụ bạn và thú cưng của bạn. 🐾
      Đây là dịch vụ cửa hàng mình ${dichVuJson} và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu..
      Hãy trả lời thật ngắn gọn nhưng đầy đủ thông tin nha bạn yêu. Hãy trả lời dưới dạng HTML với các thẻ div được style màu sắc và font chữ phù hợp. Sử dụng màu pastel và font chữ dễ thương.`
    }
    let response = await aiStore.sendMessage(prompt)
    // Loại bỏ ```html và ``` trong câu trả lời của AI
    greeting.value = response.replace(/```html/g, '').replace(/```/g, '')
  } catch (error) {
    console.error('Error fetching AI greeting:', error)
    if (userInfo.value) {
      greeting.value = `<div>
        Chào mừng chủ nhân ${userInfo.value.name} có thú cưng ${userInfo.value.petInfo} đến với PetHaven! 🌟
        <div style="color: #87CEEB; margin-top: 10px;">
          Mình là Yellow Cat, rất vui được phục vụ bạn! 🐾
        </div>
        <div style="color: #98FB98; margin-top: 10px;">
          Hãy khám phá các dịch vụ tuyệt vời của chúng mình nhé! 🎀
        </div>
      </div>`
    } else {
      greeting.value = `<div>
        Chào mừng chủ nhân đến với PetHaven! 🌟
        <div style="color: #87CEEB; margin-top: 10px;">
          Mình là Yellow Cat, rất vui được phục vụ bạn! 🐾
        </div>
        <div style="color: #98FB98; margin-top: 10px;">
          Hãy khám phá các dịch vụ tuyệt vời của chúng mình nhé! 🎀
        </div>
      </div>`
    }
  } finally {
    isLoading.value = false
  }
})

function tiepTucChat() {
  return navigateTo('/chat');
}
</script>

<template>
  <div class="greeting-container">
    <div v-if="isLoading" class="loading">
      <i class="fas fa-spinner fa-spin me-2"></i> {{ t('wait_a_minute_master_I_m_thinking') }} 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="text fs-6 justify-content-around" v-html="greeting">
      </div>
      <button class="custom-button" @click="tiepTucChat">
        <span class="button-text">{{ t('continute') }} Chat</span>
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
          class="bi bi-chat-right-dots-fill" viewBox="0 0 16 16">
          <path
            d="M16 2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h9.586a1 1 0 0 1 .707.293l2.853 2.853a.5.5 0 0 0 .854-.353zM4.5 5.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.greeting-container {
  display: flex;
  align-items: center;
  padding: 25px;
  font-family: 'Comic Sans MS', cursive;
}

.greeting {
  width: 100%;
}

.text {
  padding: 15px;
  margin-bottom: 20px;
  line-height: 1.8;
  letter-spacing: 0.5px;
}

.text :deep(div) {
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.text :deep(div:hover) {
  transform: scale(1.02);
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.1);
  }

  100% {
    transform: scale(1);
  }
}

.loading {
  font-style: italic;
  color: #666;
  padding: 10px;
}

.greeting-text strong {
  color: #28a745;
}

.custom-button {
  background-color: transparent;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.bi {
  margin-left: 5px;
}

.button-text {
  margin-right: 5px;
  font-size: 14px;
}
</style>