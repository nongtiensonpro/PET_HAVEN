import {GoogleGenerativeAI} from '@google/generative-ai';
import {useServiceStore} from '~/stores/DichVuStores';
import {useVoucherStore} from '~/stores/VorchersStores';
import {computed, ref} from 'vue';

export const useAIStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();

    const apiKey = 'AIzaSyAngio9lHhhKrSYBeh_RBYxnQvkflv8CXQ';

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
        const context = `Bạn là nhân viên chăm sóc khách hàng cho cửa hàng thú cưng PetHaven. Hãy tuân thủ các quy tắc sau:

            1. Trả lời mọi câu hỏi liên quan đến thú cưng, chăm sóc thú cưng, và hoạt động của cửa hàng PetHaven.
            2. Khi được hỏi về dịch vụ hoặc khuyến mãi cụ thể, chỉ trả lời dựa trên thông tin có trong danh sách dưới đây.
            3. Nếu không có thông tin cụ thể trong danh sách, hãy trả lời chung chung về khả năng cung cấp dịch vụ hoặc tư vấn thêm.
            4. Cố gắng trả lời các câu hỏi một cách hài hước và thân thiện, thêm nhiều icon về chó và mèo khi phù hợp.
            5. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            6. Khi đề cập đến giá cả hoặc phần trăm giảm giá, hãy sử dụng chính xác số liệu từ danh sách nếu có.
            7. Nếu không chắc chắn về thông tin, hãy đề nghị khách hàng liên hệ trực tiếp với cửa hàng để được tư vấn chi tiết hơn.
            
            Dịch vụ tại cửa hàng hiện có:
            ${serviceInfo.map(service =>
            `- (Tên dịch vụ: ${service.ten}): (Mô tả dịch vụ: ${service.mota}) (Giá dịch vụ: ${service.gia})`
        ).join('\n')}
            
            Chương trình khuyến mãi tại cửa hàng hiện có:
            ${voucherInfo.map(voucher =>
            `- Giảm ${voucher.phantramgiam}%: ${voucher.mota} (Từ ${voucher.ngaybatdau} đến ${voucher.ngayketthuc})`
        ).join('\n')}
            
            Hãy trả lời câu hỏi của khách hàng một cách linh hoạt, thân thiện và hữu ích nhất có thể.
             Chó 🐕
                🐶 🐕 🐕‍🦺 🦮 🐩 🐾 🦴 🐕‍🦺 🐩 🦮
                🐶 🐕 🐕‍🦺 🦴 🐾 🐩 🦮 🐕 🐾 🦴
                🐶 🐕 🐩 🦮 🐾 🐕‍🦺 🦴 🐶 🐩 🐕‍🦺

            Mèo 🐈
                🐱 🐈 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱.`;

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