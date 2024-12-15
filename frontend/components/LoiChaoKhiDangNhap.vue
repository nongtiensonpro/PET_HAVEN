<script setup lang="ts">
import { useUserStore } from '~/stores/user'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed } from 'vue'

const aiStore = useAIStore()
const userStore = useUserStore()

const greeting = ref('')
const isLoading = ref(true)

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

onMounted(async () => {
  try {
    let prompt
    if (userInfo.value) {
      prompt = `Chào mừng ${userInfo.value.name} ${userInfo.value.petInfo} đến với PetHaven. Hãy tạo một lời chào ngắn gọn, thân thiện và dễ thương, đề cập đến cả thú cưng nếu có.`
    } else {
      prompt = 'Hãy tạo một lời chào chung chung, thân thiện và dễ thương cho khách hàng mới đến PetHaven.'
    }
    greeting.value = await aiStore.sendMessage(prompt)
  } catch (error) {
    console.error('Error fetching AI greeting:', error)
    if (userInfo.value) {
      greeting.value = `Xin chào ${userInfo.value.name}${userInfo.value.petInfo ? ` và ${userInfo.value.petInfo}` : ''}! Chào mừng bạn đến với PetHaven. 🐾`
    } else {
      greeting.value = 'Chào mừng quý khách đến với PetHaven! Chúng tôi rất vui được phục vụ bạn và thú cưng của bạn. 🐾'
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
      Thưa cậu chủ em đang suy nghĩ 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="row">
        <div class="col-10">
          {{ greeting }}
        </div>
        <div class="col-2 custom-button" @click="tiepTucChat">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chat-dots-fill" viewBox="0 0 16 16">
            <path d="M16 8c0 3.866-3.582 7-8 7a9 9 0 0 1-2.347-.306c-.584.296-1.925.864-4.181 1.234-.2.032-.352-.176-.273-.362.354-.836.674-1.95.77-2.966C.744 11.37 0 9.76 0 8c0-3.866 3.582-7 8-7s8 3.134 8 7M5 8a1 1 0 1 0-2 0 1 1 0 0 0 2 0m4 0a1 1 0 1 0-2 0 1 1 0 0 0 2 0m3 1a1 1 0 1 0 0-2 1 1 0 0 0 0 2"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.greeting-container {
  padding: 20px;
  background-color: #f0f8ff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.loading {
  font-style: italic;
  color: #666;
}

.greeting {
  font-size: 1.2em;
  color: #333;
}
</style>