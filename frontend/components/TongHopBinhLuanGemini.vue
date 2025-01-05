<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { GoogleGenerativeAI } from '@google/generative-ai';

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

const danhGias = ref(props.danhGias);
const aiSummary = ref('');
const isLoading = ref(true);

const apiKey = 'AIzaSyClcxWPh0hpZrh9Cf996fL3X0dUPjwYuOQ';
const genAI = new GoogleGenerativeAI(apiKey);
const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });

const generateSummary = async () => {
  try {
    const context = `Bạn là AI chuyên phân tích đánh giá. Tóm tắt ngắn gọn các đánh giá sau, mục đích chính là cho biết dịch vụ này chất lượng ra sao:
    ${JSON.stringify(danhGias.value, null, 2)}
    Tạo bản tóm tắt ngắn gọn (khoảng 100 từ) bao gồm:
    1. Điểm trung bình
    2. Tỷ lệ đánh giá tích cực/tiêu cực
    3. Ưu điểm chính
    Hài hước, thêm 1-2 emoji thú cưng. Chia thành các đoạn ngắn để dễ đọc.`;

    const result = await model.generateContent(context);
    return result.response.text();
  } catch (error) {
    console.error("Error generating AI summary:", error);
    throw new Error("Xin lỗi, có lỗi xảy ra khi tạo tóm tắt. Vui lòng thử lại sau! 🙏");
  }
};

const formattedSummary = computed(() => {
  if (!aiSummary.value) return '';

  const sentences = aiSummary.value.split(/(?<=[.!?])\s+/);
  const paragraphs = [];
  let currentParagraph = '';

  for (const sentence of sentences) {
    if (currentParagraph.length + sentence.length > 100) {
      paragraphs.push(currentParagraph.trim());
      currentParagraph = '';
    }
    currentParagraph += sentence + ' ';
  }

  if (currentParagraph) {
    paragraphs.push(currentParagraph.trim());
  }
  return paragraphs.map(p => `<p>${p}</p>`).join('');
});

onMounted(async () => {
  try {
    aiSummary.value = await generateSummary();
  } catch (error) {
    console.error("Error getting AI summary:", error);
    aiSummary.value = "Xin lỗi, không thể tạo tóm tắt lúc này. Vui lòng thử lại sau.";
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="card">
    <div class="card-body">
      <h2>Tổng hợp đánh giá và nhận xét bởi AI</h2>
      <div v-if="isLoading" class="loading">
        Đang tải tóm tắt...
      </div>
      <div v-else class="ai-summary" v-html="formattedSummary"></div>
    </div>
  </div>
</template>

<style scoped>
.ai-summary p {
  margin-bottom: 1rem;
}
</style>