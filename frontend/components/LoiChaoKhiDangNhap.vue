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
      <i class="fas fa-spinner fa-spin me-2"></i> Thưa cậu chủ em đang suy nghĩ 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="text fs-4 justify-content-around">
        {{ greeting }}
      </div>
      <button class="custom-button" @click="tiepTucChat">
        <span class="button-text">Tiếp tục Chat</span>
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
  //background-color: rgba(246, 246, 234, 0.62);
  //border-radius: 15px;
  //box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
}



.greeting-text strong {
  color: #28a745; /* Màu thương hiệu PetHaven (ví dụ) */
}

.custom-button {
  background-color: transparent; /* Màu nền trong suốt */
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  display: flex; /* Sử dụng flexbox */
  align-items: center; /* Căn giữa theo chiều dọc */
  white-space: nowrap;
}

.custom-button:hover {
  background-color: rgba(40, 167, 69, 0.2); /* Hiệu ứng hover */
}

.bi {
  margin-left: 5px;
}
.button-text {
  margin-right: 5px; /* Khoảng cách giữa chữ và biểu tượng */
  font-size: 14px;
}
</style>