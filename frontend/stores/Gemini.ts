import { GoogleGenerativeAI } from '@google/generative-ai'
import { useServiceStore } from '~/stores/DichVuStores';
import DichVu from '~/models/DichVu';
import { computed } from "vue";

export const useAIStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const apiKey = process.env.GEMINI_API_KEY
    const services = computed((): DichVu[] => {
        return serviceStore.services.filter((service: DichVu) => service.trangthai && service.hien);
    });

    const genAI = new GoogleGenerativeAI('AIzaSyBwB5cDXuckYok3o75_O6X9KEzd8ezYJWs')
    const model = genAI.getGenerativeModel({
        model: 'gemini-2.0-flash-exp'
    })

    const generationConfig = {
        temperature: 0.7,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    }

    const getServiceInfo = () => {
        return services.value.map(service => ({
            id: service.id,
            ten: service.ten,
            mota: service.mota,
            gia: service.gia
        }));
    }

    const sendMessage = async (prompt) => {
        try {
            const serviceInfo = getServiceInfo();
            const context = `Bạn là một trợ lý AI cho cửa hàng thú cưng. Dưới đây là thông tin về các dịch vụ hiện có:
            ${JSON.stringify(serviceInfo, null, 2)}
            Hãy sử dụng thông tin này để trả lời câu hỏi của khách hàng. Nếu khách hỏi về dịch vụ không có trong danh sách, hãy nói rằng cửa hàng hiện không cung cấp dịch vụ đó.`;

            const chatSession = model.startChat({
                generationConfig,
                history: [
                    {
                        role: "user",
                        parts: [{ text: context }],
                    },
                    {
                        role: "model",
                        parts: [{ text: "Tôi đã hiểu. Tôi sẽ sử dụng thông tin này để trả lời câu hỏi của khách hàng về các dịch vụ của cửa hàng thú cưng. Tôi sẵn sàng hỗ trợ." }],
                    },
                ],
            })

            const result = await chatSession.sendMessage(prompt)
            return result.response.text()
        } catch (error) {
            console.error("Error sending message to AI:", error)
            throw error
        }
    }

    return { sendMessage }
})