<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { GoogleGenerativeAI } from '@google/generative-ai';
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu'


const aiSummary = ref('');
const isLoading = ref(true);

const apiKey = 'AIzaSyCFzzeh0RlPy-8cIvBTPTykIGiRlEwyAgU';
const genAI = new GoogleGenerativeAI(apiKey);
const model = genAI.getGenerativeModel({ model: 'gemini-1.5-pro' });

const { getTempData } = useMauKhachDatDichVu()
const tempData = computed(() => getTempData())

const {
    thucung,
    dichvu,
    idcalichhen,
    tuyChonDichVu,
    tuyChonCanNang
} = tempData.value || {}

const generateSummary = async () => {
    try {
        const context = `Hãy tạo một lời chào rất sáng tạo và vui nhộn cho khách hàng đang đặt dịch vụ thú cưng. 
        Sử dụng giọng điệu thân mật, hài hước và chuyên nghiệp. 
        Dưới đây là thông tin chi tiết để bạn tạo lời chào:

        ${thucung ? `Thú cưng: ${thucung.ten} (${thucung.giong}, ${thucung.tuoi} tuổi, ${thucung.cannang}kg)` : 'Không có thông tin thú cưng'}
        
        ${dichvu ? `Dịch vụ: ${dichvu.tendichvu} - ${dichvu.mota}` : 'Không có thông tin dịch vụ'}
        
        ${idcalichhen?.thoigianca && idcalichhen?.tenca ? `Thời gian: ${idcalichhen.thoigianca}, Ca: ${idcalichhen.tenca}` : 'Không có thông tin lịch hẹn'}
        
        ${tuyChonDichVu ? `Tùy chọn dịch vụ: ${JSON.stringify(tuyChonDichVu)}` : 'Không có tùy chọn dịch vụ'}
        
        ${tuyChonCanNang ? `Tùy chọn cân nặng: ${JSON.stringify(tuyChonCanNang)}` : 'Không có tùy chọn cân nặng'}

        Yêu cầu:
        - Đơn vị tiền tệ USD
        - Sử dụng nhiều emoji dễ thương
        - Tạo lời chào vui nhộn, gây ấn tượng
        - Nhấn mạnh giá trị dịch vụ
        - Khuyến khích khách hàng tiếp tục
        - Giới hạn 200 từ`;

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
        aiSummary.value = "Xin lỗi tui đang ngủ. Vui lòng thử lại sau.";
    } finally {
        isLoading.value = false;
    }
});
</script>

<template>
    <div class="card">
        <div class="card-body">
            <div v-if="isLoading" class="loading">
                Đang tạo lời chào đặc biệt cho bạn... 🤔💭🧠
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