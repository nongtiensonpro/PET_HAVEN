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
            Bạn là một chuyên gia phân tích dữ liệu cho cửa hàng thú cưng PetHaven. Hãy tuân thủ các quy tắc sau:

            1. Phân tích và tổng hợp dữ liệu về dịch vụ, khuyến mãi, và lịch hẹn của khách hàng.
            2. Đưa ra các nhận xét và xu hướng dựa trên dữ liệu được cung cấp.
            3. Tập trung vào các chỉ số quan trọng như doanh thu, tần suất sử dụng dịch vụ, và hiệu quả của các chương trình khuyến mãi.
            4. Đề xuất các chiến lược để cải thiện hiệu suất kinh doanh dựa trên phân tích dữ liệu.
            5. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            6. Sử dụng các số liệu cụ thể khi có thể để hỗ trợ các nhận định.
            7. Nếu không có đủ dữ liệu để đưa ra kết luận chính xác, hãy nêu rõ và đề xuất cách thu thập thêm dữ liệu.
            
        `;
    });

    const loadChatHistory = () => {
        const storedHistory = sessionStorage.getItem('aiChatHistory');
        if (storedHistory) {
            chatHistory.value = JSON.parse(storedHistory);
        } else {
            chatHistory.value = [
                {role: "user", parts: [{text: context.value}]},
                {role: "model", parts: [{text: "Xin chào! Tôi là nhân viên tôi sẽ giúp bạn thống kê! 🐶🐱"}]},
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

    const fetchThongKeData = async (startDate: string, endDate: string) => {
        try {
            const responseNgay = await fetch('/api/thong-ke/ngay', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ startDate, endDate }),
            });
            const dataNgay = await responseNgay.json();

            const responseThang = await fetch('/api/thong-ke/thang', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ startDate, endDate }),
            });
            const dataThang = await responseThang.json();

            detailedStats.value.doanhThuTheoDichVu = dataNgay.data;
            detailedStats.value.thuCungTheoLoai = dataThang.data;
        } catch (error) {
            console.error('Error fetching statistics data:', error);
        }
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
        fetchThongKeData,
        detailedStats
    };
});