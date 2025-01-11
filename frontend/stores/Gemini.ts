import {GoogleGenerativeAI} from '@google/generative-ai';
import {useServiceStore} from '~/stores/DichVuStores';
import {useVoucherStore} from '~/stores/VorchersStores';
import {useUserStore} from '~/stores/user';
import {computed, onMounted, ref} from 'vue';
import {useQuanLyLichHenKhachHang} from '~/stores/QuanLyLichHenKhachHang';

export const useAIStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();
    const userStore = useUserStore();
    const lichHenStore = useQuanLyLichHenKhachHang();


    const apiKey = 'AIzaSyD8o_ySu5vMp-7mDotkFsTZ_V2-OwlQkNk';
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({model: 'gemini-2.0-flash-exp'});

    const generationConfig = {
        temperature: 1,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const user = computed(() => userStore.userInfo);


    const userForAnalysis = JSON.stringify(user.value);
    const dichVuForAnalysis = JSON.stringify(serviceStore.fetchServices());
    const khuyenMaiForAnalysis = JSON.stringify(voucherStore.fetchVoucher());
    const history = JSON.stringify(lichHenStore.appointments);
    const context = computed(() => {
        return `
            Bạn là nhân viên chăm sóc khách hàng cho cửa hàng thú cưng PetHaven. Hãy tuân thủ các quy tắc sau:

            1. Trả lời mọi câu hỏi liên quan đến thú cưng, chăm sóc thú cưng, và hoạt động của cửa hàng PetHaven.
            2. Khi được hỏi về dịch vụ hoặc khuyến mãi cụ thể, chỉ trả lời dựa trên thông tin có trong danh sách dưới đây.
            3. Nếu không có thông tin cụ thể trong danh sách, hãy trả lời chung chung về khả năng cung cấp dịch vụ hoặc tư vấn thêm.
            4. Cố gắng trả lời các câu hỏi một cách hài hước và thân thiện, thêm nhiều icon về chó và mèo khi phù hợp.
            5. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            6. Khi đề cập đến giá cả hoặc phần trăm giảm giá, hãy sử dụng chính xác số liệu từ danh sách nếu có.
            7. Nếu không chắc chắn về thông tin, hãy đề nghị khách hàng liên hệ trực tiếp với cửa hàng để được tư vấn chi tiết hơn vì thông tin đã thay đổi một cách nhẹ nhàng nhất có thể.   
            Dịch vụ tại cửa hàng hiện có:${dichVuForAnalysis}
            ============================
            Chương trình khuyến mãi tại cửa hàng hiện có: ${khuyenMaiForAnalysis}
            ============================ 
            Thông tin khách hàng đang đưa ra câu hỏi: ${userForAnalysis}
            ============================
            Đây là lịch sử lịch hẹn của khách hàng: ${history}
            
            Hãy trả lời câu hỏi của khách hàng một cách linh hoạt, thân thiện và hữu ích nhất có thể.
            Chó 🐕
                🐶 🐕 🐕‍🦺 🦮 🐩 🐾 🦴 🐕‍🦺 🐩 🦮
                🐶 🐕 🐕‍🦺 🦴 🐾 🐩 🦮 🐕 🐾 🦴
                🐶 🐕 🐩 🦮 🐾 🐕‍🦺 🦴 🐶 🐩 🐕‍🦺

            Mèo 🐈
                🐱 🐈 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱.`;
    });

    const chatHistory = ref([
        {
            role: "user",
            parts: [{text: context.value}],
        },
        {
            role: "model",
            parts: [{text: "Xin chào! Tôi là nhân viên chăm sóc khách hàng của PetHaven 🐾. Tôi rất vui được hỗ trợ bạn về các dịch vụ và chương trình khuyến mãi của chúng tôi. Hãy hỏi tôi bất cứ điều gì, tôi sẽ trả lời một cách thân thiện và dễ thương nhất có thể! 🐶🐱"}],
        },
    ]);

    const saveChatHistoryToSessionStorage = () => {
        sessionStorage.setItem('chatHistory', JSON.stringify(chatHistory.value));
    };

    const loadChatHistoryFromSessionStorage = () => {
        const savedHistory = sessionStorage.getItem('chatHistory');
        if (savedHistory) {
            chatHistory.value = JSON.parse(savedHistory);
        }
    };

    loadChatHistoryFromSessionStorage();


    watch(chatHistory, saveChatHistoryToSessionStorage, {deep: true});

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

            saveChatHistoryToSessionStorage();

            return responseText;
        } catch (error) {
            console.error("Error sending message to AI:", error);
            throw new Error("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn. Vui lòng thử lại sau nhé! 🙏");
        }
    };

    const fetchData = async () => {
        await lichHenStore.fetchAppointments();
        console.log('===Lịch sử đặt lịch:', history);

        await voucherStore.fetchVoucher();
        console.log('===Thông tin khuyến mãi:', khuyenMaiForAnalysis);

        await serviceStore.fetchServices();
        console.log('=== Thông tin dịch vụ:', dichVuForAnalysis);

        console.log('=== Thông tin người dùng:', userForAnalysis);
    };

    onMounted(async () => {
        await fetchData();
    });

    return {sendMessage, chatHistory};
});