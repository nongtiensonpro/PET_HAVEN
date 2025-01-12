import {GoogleGenerativeAI} from '@google/generative-ai';
import {computed, onMounted, ref, watch} from 'vue';
import {useQuanLyLichHenKhachHang} from '~/stores/QuanLyLichHenKhachHang';


export const useAIThongKeStore = defineStore('ai', () => {
    const lichHenStore = useQuanLyLichHenKhachHang();
    const chatHistory = ref([]);

    const apiKey = 'AIzaSyClcxWPh0hpZrh9Cf996fL3X0dUPjwYuOQ';
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({model: 'gemini-2.0-flash-exp'});

    const generationConfig = {
        temperature: 1,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const detailedStats = ref({
        doanhThuTheoDichVu: [],
        thuCungTheoLoai: {},
        tyLeHuyLich: {},
        gioDatLichPhoBien: []
    });


    const context = computed(() => {

        return `
            Bạn là một chuyên gia phân tích dữ liệu cho cửa hàng thú cưng PetHaven. 
        `;
    });

    const loadChatHistory = () => {
        const storedHistory = sessionStorage.getItem('aiChatHistory');
        if (storedHistory) {
            chatHistory.value = JSON.parse(storedHistory);
        } else {
            chatHistory.value = [
                {role: "user", parts: [{text: context.value}]},
                {role: "model", parts: [{text: "Xin chào Ông chủ ! Tôi là nhân viên tôi sẽ giúp bạn thống kê! 🐶🐱"}]},
            ];
        }
    };

    watch(chatHistory, (newHistory) => {
        sessionStorage.setItem('aiChatHistory', JSON.stringify(newHistory));
    }, {deep: true});

    const sendMessage = async (prompt: string) => {
        try {
            chatHistory.value.push({role: "user", parts: [{text: context.value + "\n\n" + prompt}]});

            const chatSession = model.startChat({
                generationConfig,
                history: chatHistory.value,
            });

            const result = await chatSession.sendMessage(prompt);
            const responseText = result.response.text();

            chatHistory.value.push({role: "model", parts: [{text: responseText}]});

            return responseText;
        } catch (error) {
            console.error("Error sending message to AI:", error);
            throw new Error("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn. Vui lòng thử lại sau nhé! 🙏");
        }
    };

    const fetchData = async () => {
        await lichHenStore.fetchAppointments();
    };



    onMounted(async () => {
        loadChatHistory();
        await fetchData();
        console.log("Dữ liệu AI sẽ nhận:", context.value);
    });

    return {
        chatHistory,
        sendMessage,
        context,
        fetchData,
        detailedStats
    };
});