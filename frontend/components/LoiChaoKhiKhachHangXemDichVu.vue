<script setup lang="ts">
import type Service from "~/models/DichVu";
import { useUserStore } from '~/stores/user'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed } from 'vue'

const props = defineProps<{
  service: Service
}>();

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
      prompt = `Chào mừng ${userInfo.value.name} ${userInfo.value.petInfo} đến với PetHaven.
      Bạn đang xem dịch vụ "${props.service.tendichvu}" với giá ${props.service.giatien} USD.
      Hãy tạo một lời chào ngắn gọn, thân thiện và dễ thương, đề cập đến cả thú cưng (nếu có) và dịch vụ đang xem.`
    } else {
      prompt = `Chào mừng quý khách đến với PetHaven.
      Bạn đang xem dịch vụ "${props.service.tendichvu}" với giá ${props.service.giatien} USD.
      Hãy tạo một lời chào chung chung, thân thiện và dễ thương cho khách hàng mới, đề cập đến dịch vụ đang xem.`
    }
    greeting.value = await aiStore.sendMessage(prompt)
  } catch (error) {
    console.error('Error fetching AI greeting:', error)
    if (userInfo.value) {
      greeting.value = `Xin chào ${userInfo.value.name}${userInfo.value.petInfo ? ` và ${userInfo.value.petInfo}` : ''}!
      Chào mừng bạn đến với PetHaven. Chúng tôi hy vọng bạn sẽ thích dịch vụ "${props.service.tendichvu}" của chúng tôi. 🐾`
    } else {
      greeting.value = `Chào mừng quý khách đến với PetHaven!
      Chúng tôi rất vui được phục vụ bạn và thú cưng của bạn.
      Dịch vụ "${props.service.tendichvu}" của chúng tôi đang chờ đón bạn. 🐾`
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
        <div class="col-2 custom-button" @click="tiepTucChat" style="border: none; background: none">
          Tiếp tục Chat
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-chat-right-dots-fill" viewBox="0 0 16 16">
            <path d="M16 2a2 2 0 0 0-2-2H2a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h9.586a1 1 0 0 1 .707.293l2.853 2.853a.5.5 0 0 0 .854-.353zM4.5 5.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 3a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.greeting-container {
  padding: 20px;
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