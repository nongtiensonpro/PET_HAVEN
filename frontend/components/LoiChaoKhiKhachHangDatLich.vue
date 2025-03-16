<script setup lang="ts">
import { useMauKhachDatDichVu } from '~/stores/MauKhachDatDichVu'
import { useAIStore } from '~/stores/Gemini'
import { ref, onMounted, computed } from 'vue'

const { getTempData } = useMauKhachDatDichVu()
const aiStore = useAIStore()
const tempData = computed(() => getTempData())

const greeting = ref('')
const isLoading = ref(true)

const petEmojis = [
    '🐶', '🐱', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', 
    '🦁', '🐮', '🐷', '🐸', '🐙', '🦄', '🐴', '🦮', '🐕', '🐩'
]

const serviceEmojis = [
    '💕', '🐾', '🏥', '💉', '🛁', '✂️', '💆', '🚿', '🐈', '🐕‍🦺', 
    '🧼', '💖', '🌈', '🌟', '🤗', '🐾', '💓', '🌞', '🌸', '🎉'
]

onMounted(async () => {
    if (tempData.value) {
        const { 
            thucung, 
            dichvu, 
            idcalichhen, 
            tuyChonDichVu, 
            tuyChonCanNang 
        } = tempData.value

        let prompt = `Hãy tạo một lời chào rất sáng tạo và vui nhộn cho khách hàng đang đặt dịch vụ thú cưng. 
        Sử dụng giọng điệu thân mật, hài hước và chuyên nghiệp. 
        Dưới đây là thông tin chi tiết để bạn tạo lời chào:

        ${thucung ? `Thú cưng: ${thucung.ten} (${thucung.giong}, ${thucung.tuoi} tuổi, ${thucung.cannang}kg)` : 'Không có thông tin thú cưng'}
        
        ${dichvu ? `Dịch vụ: ${dichvu.tendichvu} - ${dichvu.mota}` : 'Không có thông tin dịch vụ'}
        
        ${idcalichhen ? `Thời gian: ${idcalichhen.thoigianca}, Ca: ${idcalichhen.tenca}` : 'Không có thông tin lịch hẹn'}
        
        ${tuyChonDichVu ? `Tùy chọn dịch vụ: ${JSON.stringify(tuyChonDichVu)}` : 'Không có tùy chọn dịch vụ'}
        
        ${tuyChonCanNang ? `Tùy chọn cân nặng: ${JSON.stringify(tuyChonCanNang)}` : 'Không có tùy chọn cân nặng'}

        Yêu cầu:
        - Sử dụng nhiều emoji dễ thương
        - Tạo lời chào vui nhộn, gây ấn tượng
        - Nhấn mạnh giá trị dịch vụ
        - Khuyến khích khách hàng tiếp tục
        - Giới hạn 300 từ`

        try {
            isLoading.value = true
            const aiGreeting = await aiStore.sendMessage(prompt)
            
            // Thêm emoji ngẫu nhiên
            const randomPetEmoji = petEmojis[Math.floor(Math.random() * petEmojis.length)]
            const randomServiceEmoji = serviceEmojis[Math.floor(Math.random() * serviceEmojis.length)]
            
            greeting.value = `${randomPetEmoji} ${randomServiceEmoji}\n\n${aiGreeting}\n\n${randomServiceEmoji} ${randomPetEmoji}`
            isLoading.value = false
        } catch (error) {
            console.error('Lỗi khi tạo lời chào AI:', error)
            greeting.value = `Xin chào! Chúng tôi rất vui được phục vụ bạn tại PetHaven! 🐾`
            isLoading.value = false
        }
    }
})
</script>

<template>
    <div class="greeting-container">
        <div v-if="isLoading" class="loading">
            Đang tạo lời chào đặc biệt cho bạn... 🤔💭🧠
        </div>
        <pre v-else class="greeting">{{ greeting }}</pre>
    </div>
</template>

<style scoped>
.greeting-container {
    background-color: #f9f5f0;
    border-radius: 15px;
    padding: 20px;
    font-family: 'Comic Sans MS', cursive;
}
.loading {
    font-style: italic;
    color: #666;
    text-align: center;
}
.greeting {
    white-space: pre-wrap;
    font-size: 1em;
    color: #333;
    line-height: 1.6;
}
</style>