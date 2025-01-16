<script setup lang="ts">
import type Service from "~/models/DichVu";
import { useUserStore } from '~/stores/user'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n';
const props = defineProps<{
  service: Service
}>();

const { t } = useI18n();
const aiStore = useAIStore()
const userStore = useUserStore()

const greeting = ref('')
const isLoading = ref(true)
const relatedServices = ref([])

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
    petInfo,
    pets
  }
})

onMounted(async () => {
  try {
    let prompt
    if (userInfo.value) {
      prompt = `Chủ nhân ${userInfo.value.name} có thú  ${userInfo.value.petInfo} đến với PetHaven. Mình là Yellow Cat nhân viên cửa hàng . Mình xin phép gọi bạn bằng một biệt danh kèm icon  dựa trên thông tin  của bạn  để phù hợp với thú cưng một tên thật dễ thương đáng yêu nha.
      Đang xem dịch vụ "${props.service.tendichvu}".
      Hãy giúp chủ nhân lựa chọn dịch vụ phù hợp với thú cưng, thân thiện và dễ thương, đề cập đến cả thú cưng và dịch vụ đang xem.
      Đề xuất 1 dịch vụ liên quan có thể phù hợp và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu. Hãy trả lời dưới dạng HTML sẽ hiển thị trong thẻ V-HTML trong NUXTJS với các thẻ div được style màu sắc và font chữ phù hợp.Không sử dụng margin. Sử dụng màu pastel và font chữ dễ thương. Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62); Tối thiểu 100 từ và tối đa 150 từ và font-size: 14px`
    } else {
      prompt = `Chào mừng chủ nhân với thú cưng đến với PetHaven. Mình là Yellow Cat nhân viên cửa hàng . Mình xin phép gọi bạn bằng một biệt danh kèm icon  dựa trên thông tin dịch vụ của cửa hàng của mình  một tên thật dễ thương đáng yêu nha.
      Bạn đang xem dịch vụ "${props.service.tendichvu}".
      Hãy tạo một lời chào thân thiện, dễ thương cho chủ nhân  mới, đề cập đến dịch vụ đang xem và gợi 1 dịch vụ liên quan và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu. Hãy trả lời dưới dạng HTML với các thẻ div được style màu sắc và font chữ phù hợp.Không sử dụng margin. Sử dụng màu pastel và font chữ dễ thương. Được biết màu nền đang hiển thị là background: rgba(246, 246, 234, 0.62); Tối thiểu 100 từ và tối đa 150 từ và font-size: 14px.
      `
    }
    const response = await aiStore.sendMessage(prompt)
    greeting.value = response.trim().replace('```html', '').replace('```', '')
  } catch (error) {
    console.error('Error fetching AI greeting:', error)
    setDefaultGreeting()
  } finally {
    isLoading.value = false
  }
})

function setDefaultGreeting() {
  if (userInfo.value) {
    greeting.value = `<div style="color: #FF69B4;">
      Xin chào <span style="color: #87CEEB;">${userInfo.value.name}</span>${userInfo.value.petInfo ? ` và ${userInfo.value.petInfo}` : ''}! 🌟
      <div style="color: #98FB98; margin-top: 10px;">
        Chào mừng bạn đến với PetHaven. Chúng tôi hy vọng bạn sẽ thích dịch vụ "${props.service.tendichvu}" của chúng tôi.🐾
      </div>
    </div>`
  } else {
    greeting.value = `<div style="color: #FF69B4;">
      Chào mừng quý khách đến với PetHaven! 🌟
      <div style="color: #87CEEB; margin-top: 10px;">
        Chúng tôi rất vui được phục vụ bạn và thú cưng của bạn.
      </div>
      <div style="color: #98FB98; margin-top: 10px;">
        Dịch vụ "${props.service.tendichvu}" của chúng tôi đang chờ đón bạn .🐾
      </div>
    </div>`
  }
}

function tiepTucChat() {
  return navigateTo('/chat');
}

function datLichNgay() {
  return navigateTo(`/customer/calendar/newuser`);
}
</script>

<template>
  <div class="greeting-container">
    <div v-if="isLoading" class="loading">
      <i class="fas fa-spinner fa-spin me-2"></i> {{ t('wait_a_minute_master_I_m_thinking') }} 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="row mb-3">
        <div class="col-10 text" v-html="greeting">
        </div>
        <div class="col-2 row">
          <div class="col-12">
            <button class="custom-button" @click="datLichNgay">
              <span class="button-text">Đặt lịch ngay</span>
              <i class="bi bi-calendar-check"></i>
            </button>
          </div>
          <div class="col-12">
            <button class="custom-button" @click="tiepTucChat">
              <span class="button-text">Tiếp tục Chat</span>
              <i class="bi bi-chat-right-dots-fill"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.greeting-container {
  padding: 20px;
  background-color: rgba(246, 246, 234, 0.62);
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: 'Comic Sans MS', cursive;
}

.loading {
  font-style: italic;
  color: #666;
  padding: 10px;
}

.greeting {
  font-size: 1.2em;
  color: #333;
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

.custom-button {
  width: 100%;
  margin-top: 10px;
  background-color: transparent;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.custom-button:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.button-text {
  margin-right: 8px;
  font-size: 14px;
}

.bi {
  font-size: 16px;
}
</style>