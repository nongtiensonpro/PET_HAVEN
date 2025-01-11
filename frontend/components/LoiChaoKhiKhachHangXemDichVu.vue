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
      Đề xuất 1 dịch vụ liên quan có thể phù hợp và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu.`
    } else {
      prompt = `Chào mừng chủ nhân với thú cưng đến với PetHaven. Mình là Yellow Cat nhân viên cửa hàng . Mình xin phép gọi bạn bằng một biệt danh kèm icon  dựa trên thông tin dịch vụ của cửa hàng của mình  một tên thật dễ thương đáng yêu nha.
      Bạn đang xem dịch vụ "${props.service.tendichvu}".
      Hãy tạo một lời chào thân thiện, dễ thương cho chủ nhân  mới, đề cập đến dịch vụ đang xem và gợi 1 dịch vụ liên quan và giải thích vì sao nên sử dụng dịch vụ đó một cách dễ thương đáng yêu..`
    }
    const response = await aiStore.sendMessage(prompt)
    const [greetingText, servicesText] = response.split('Các dịch vụ liên quan:')
    greeting.value = greetingText.trim()
    relatedServices.value = servicesText ? servicesText.trim().split('\n').map(s => s.trim()) : []
  } catch (error) {
    console.error('Error fetching AI greeting:', error)
    setDefaultGreeting()
  } finally {
    isLoading.value = false
  }
})

function setDefaultGreeting() {
  if (userInfo.value) {
    greeting.value = `Xin chào ${userInfo.value.name}${userInfo.value.petInfo ? ` và ${userInfo.value.petInfo}` : ''}!
    Chào mừng bạn đến với PetHaven. Chúng tôi hy vọng bạn sẽ thích dịch vụ "${props.service.tendichvu}" của chúng tôi. ${t('always_reply_in_english')}.🐾`
  } else {
    greeting.value = `Chào mừng quý khách đến với PetHaven!
    Chúng tôi rất vui được phục vụ bạn và thú cưng của bạn.
    Dịch vụ "${props.service.tendichvu}" của chúng tôi đang chờ đón bạn. ${t('always_reply_in_english')}.🐾`
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
      {{t('wait_a_minute_master_I_m_thinking')}} 🤔💭🧠
    </div>
    <div v-else class="greeting">
      <div class="row mb-3">
        <div class="col-10">
          {{ greeting }}
        </div>
        <div class="col-2 row">
          <div class="col-12">
            <button class="custom-button" @click="datLichNgay">
              Đặt lịch ngay
              <i class="bi bi-calendar-check"></i>
            </button>
          </div>
          <div class="col-12">
            <button class="custom-button" @click="tiepTucChat">
              Tiếp tục Chat
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
  background-color: #f8f9fa;
  border-radius: 10px;
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

.btn {
  width: 100%;
  margin-top: 10px;
}
</style>