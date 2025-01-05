<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

interface DanhGia {
  id: number;
  noiDung: string;
  sao: number;
  ngayDanhGia: string;
  tenNguoiDung: string;
}

const props = defineProps<{
  danhGias: DanhGia[];
}>();

const { t, locale } = useI18n();
const router = useRouter()

const danhGias = ref(props.danhGias);
const aiSummary = ref('');
const isLoading = ref(true);

const apiKey = 'AIzaSyClcxWPh0hpZrh9Cf996fL3X0dUPjwYuOQ';
const genAI = new GoogleGenerativeAI(apiKey);
const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });

const generatePrompt = () => {
  const language = locale.value === 'en' ? 'English' : 'Vietnamese';
  const commentSummary = props.danhGias.map(comment => {
    return `- ${comment.noiDung} (Rating: ${comment.sao}/5)`
  }).join('\n');

  if (props.danhGias.length === 0) {
    return `There are no reviews yet. Please generate a friendly message encouraging users to be the first to leave a review. Please respond in ${language} language only.`
  }

  return `Here are some reviews from our customers:
${commentSummary}

Please analyze these reviews and create a friendly summary that includes:
1. Overall sentiment
2. Common positive points
3. Areas that customers appreciate most
4. A warm invitation for new customers to try our services

Please respond in ${language} language only and keep the tone friendly and engaging.`
}

const generateSummary = async () => {
  try {
    const prompt = generatePrompt()
    aiSummary.value = await model.generateContent(prompt);
  } catch (error) {
    console.error("Error generating AI summary:", error);
    if (props.danhGias.length === 0) {
      aiSummary.value = locale.value === 'en'
        ? "We're excited to hear about your experience! Be the first to share your thoughts and help others discover our amazing pet care services. 🐾"
        : "Chúng tôi rất mong được nghe về trải nghiệm của bạn! Hãy là người đầu tiên chia sẻ cảm nhận và giúp những người khác khám phá dịch vụ chăm sóc thú cưng tuyệt vời của chúng tôi. 🐾"
    } else {
      aiSummary.value = locale.value === 'en'
        ? `Thank you for all the wonderful reviews! Our customers appreciate our dedicated service and professional care. We're committed to maintaining these high standards and welcome your feedback. 🐾`
        : `Cảm ơn tất cả các đánh giá tuyệt vời! Khách hàng của chúng tôi đánh giá cao dịch vụ tận tâm và sự chăm sóc chuyên nghiệp. Chúng tôi cam kết duy trì các tiêu chuẩn cao này và luôn chào đón phản hồi của bạn. 🐾`
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  generateSummary()
})

// Tự động cập nhật tổng hợp khi thay đổi ngôn ngữ hoặc comments
watch([() => locale.value, () => props.danhGias], () => {
  isLoading.value = true
  generateSummary()
})

function tiepTucChat() {
  return router.push('/chat');
}
</script>

<template>
  <div class="card">
    <div class="card-body">
      <h2>{{ t('summary') }}</h2>
      <div v-if="isLoading" class="loading">
        <i class="fas fa-spinner fa-spin me-2"></i> {{ t('thinking') }} 🤔💭🧠
      </div>
      <div v-else class="ai-summary" v-html="aiSummary"></div>
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
.ai-summary p {
  margin-bottom: 1rem;
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