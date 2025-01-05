<script setup lang="ts">
import { useUserStore } from '~/stores/user'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const { t, locale } = useI18n();
const router = useRouter()
const aiStore = useAIStore()
const userStore = useUserStore()

const greeting = ref('')
const isLoading = ref(true)

const userInfo = computed(() => {
  if (!userStore.userInfo) return null
  const pets = userStore.userInfo.listThuCung || []
  let petInfo = ''
  
  if (pets.length === 1) {
    petInfo = locale.value === 'en' 
      ? `and pet ${pets[0].ten} (${pets[0].giong})`
      : `và thú cưng ${pets[0].ten} (${pets[0].giong})`
  } else if (pets.length > 1) {
    petInfo = locale.value === 'en'
      ? `and pets: ${pets.map(pet => `${pet.ten} (${pet.giong})`).join(', ')}`
      : `và các thú cưng: ${pets.map(pet => `${pet.ten} (${pet.giong})`).join(', ')}`
  }

  return {
    name: userStore.userInfo.name,
    petInfo
  }
})

const generatePrompt = () => {
  const language = locale.value === 'en' ? 'English' : 'Vietnamese';
  if (userInfo.value) {
    return `Generate a short, friendly, and cute greeting for ${userInfo.value.name} ${userInfo.value.petInfo} at PetHaven. The greeting should be warm and mention their pets if available. Please respond in ${language} language only.`
  }
  return `Generate a general, friendly, and cute greeting for new customers at PetHaven. The greeting should be warm and welcoming. Please respond in ${language} language only.`
}

const generateGreeting = async () => {
  try {
    const prompt = generatePrompt()
    greeting.value = await aiStore.sendMessage(prompt)
  } catch (error) {
    console.error('Error generating greeting:', error)
    if (userInfo.value) {
      greeting.value = locale.value === 'en'
        ? `Hello ${userInfo.value.name}${userInfo.value.petInfo ? ` ${userInfo.value.petInfo}` : ''}! Welcome to PetHaven. 🐾`
        : `Xin chào ${userInfo.value.name}${userInfo.value.petInfo ? ` ${userInfo.value.petInfo}` : ''}! Chào mừng bạn đến với PetHaven. 🐾`
    } else {
      greeting.value = locale.value === 'en'
        ? 'Welcome to PetHaven! We are delighted to serve you and your pets. 🐾'
        : 'Chào mừng quý khách đến với PetHaven! Chúng tôi rất vui được phục vụ bạn và thú cưng của bạn. 🐾'
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  generateGreeting()
})

// Tự động cập nhật lời chào khi thay đổi ngôn ngữ
watch(() => locale.value, () => {
  isLoading.value = true
  generateGreeting()
})

function tiepTucChat() {
  return router.push('/chat');
}
</script>

<template>
  <div class="greeting-container">
    <div v-if="isLoading" class="loading">
      <i class="fas fa-spinner fa-spin me-2"></i> {{ t('thinking') }} 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="text fs-4 justify-content-around">
        {{ greeting }}
      </div>
      <button class="custom-button" @click="tiepTucChat">
        <span class="button-text">{{ t('continueChat') }}</span>
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-chat-right-dots-fill" viewBox="0 0 16 16">
          <path d="M16 2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h9.586a1 1 0 0 1 .707.293l2.853 2.853a.5.5 0 0 0 .854-.353zM4.5 5.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5"/>
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
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.loading {
  font-style: italic;
  color: #666;
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