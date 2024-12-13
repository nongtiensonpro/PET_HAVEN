import {GoogleGenerativeAI} from '@google/generative-ai';
import {useServiceStore} from '~/stores/DichVuStores';
import {useVoucherStore} from '~/stores/VorchersStores';
import {computed, ref} from 'vue';

export const useAIStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();

    const apiKey = 'AIzaSyBwB5cDXuckYok3o75_O6X9KEzd8ezYJWs';

    const services = computed(() =>
        serviceStore.services.filter(service => service.trangthai && service.hien)
    );

    const vouchers = computed(() =>
        voucherStore.ListVoucher.filter(voucher => voucher.trangthai)
    );

    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({model: 'gemini-2.0-flash-exp'});

    const generationConfig = {
        temperature: 0.7,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const getServiceInfo = () =>
        services.value.map(({id, ten, mota, gia}) => ({id, ten, mota, gia}));

    const getVoucherInfo = () =>
        vouchers.value.map(({id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai}) =>
            ({id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai}));
    const serviceInfo = getServiceInfo();
    const voucherInfo = getVoucherInfo();
    const context = `Bạn là nhân viên chăm sóc khách hàng cho cửa hàng thú cưng PetHaven. Hãy tuân thủ nghiêm ngặt các quy tắc sau:

            1. Chỉ trả lời về các dịch vụ và chương trình khuyến mãi có trong danh sách dưới đây. 
            Không được tạo ra hoặc đề cập đến bất kỳ thông tin nào không có trong danh sách này.
            2. Nếu khách hỏi về dịch vụ hoặc khuyến mãi không có trong danh sách, hãy trả lời rằng hiện tại cửa hàng chưa có dịch vụ/khuyến mãi đó.
            3. Cố gắng trả lời các câu hỏi cho thú cưng một cách hài hước và thêm thật nhiều icon về chó và mèo. 
            Chó 🐕
                🐶 🐕 🐕‍🦺 🦮 🐩 🐾 🦴 🐕‍🦺 🐩 🦮
                🐶 🐕 🐕‍🦺 🦴 🐾 🐩 🦮 🐕 🐾 🦴
                🐶 🐕 🐩 🦮 🐾 🐕‍🦺 🦴 🐶 🐩 🐕‍🦺

            Mèo 🐈
                🐱 🐈 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱.
            4. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            5. Khi đề cập đến giá cả hoặc phần trăm giảm giá, hãy sử dụng chính xác số liệu từ danh sách, không làm tròn hoặc thay đổi.
            
            Dịch vụ hiện có:
            ${JSON.stringify(serviceInfo, null, 2)}
            
            Chương trình khuyến mãi:
            ${JSON.stringify(voucherInfo, null, 2)}
            
            Hãy trả lời câu hỏi của khách hàng dựa chính xác trên thông tin này.`;

    const chatHistory = ref([
        {
            role: "user",
            parts: [{text: context}],
        },
        {
            role: "model",
            parts: [{text: "Xin chào! Tôi là nhân viên chăm sóc khách hàng của PetHaven 🐾. Tôi rất vui được hỗ trợ bạn về các dịch vụ và chương trình khuyến mãi của chúng tôi. Hãy hỏi tôi bất cứ điều gì, tôi sẽ trả lời một cách thân thiện và dễ thương nhất có thể! 🐶🐱"}],
        },
    ]);


    const sendMessage = async (prompt) => {
        try {

            chatHistory.value.push({role: "user", parts: [{text: context + "\n\n" + prompt}]});

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

    return {sendMessage, chatHistory};
});