import { GoogleGenerativeAI } from '@google/generative-ai';
import { useServiceStore } from '~/stores/DichVuStores';
import { useVoucherStore } from '~/stores/VorchersStores';
import { useUserStore } from '~/stores/user';
import { computed, onMounted, ref, watch } from 'vue';
import { useQuanLyLichHenKhachHang } from '~/stores/QuanLyLichHenKhachHang';

export const useAIThongKeStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();
    const userStore = useUserStore();
    const lichHenStore = useQuanLyLichHenKhachHang();
    const refreshInterval = ref(null);

    const apiKey = 'AIzaSyAngio9lHhhKrSYBeh_RBYxnQvkflv8CXQ';
    const genAI = new GoogleGenerativeAI(apiKey);
    // const model = genAI.getGenerativeModel({ model: 'gemini-2.0-flash-exp' });
    const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });

    const generationConfig = {
        temperature: 1,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const services = computed(() =>
        serviceStore.services.filter(service => service.trangthai && service.hien)
    );

    const vouchers = computed(() =>
        voucherStore.ListVoucher.filter(voucher => voucher.trangthai)
    );

    const user = computed(() => userStore.userInfo);

    const getServiceInfo = () =>
        services.value.map(({ id, ten, mota, gia }) => ({ id, ten, mota, gia }));

    const getVoucherInfo = () =>
        vouchers.value.map(({ id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai }) =>
            ({ id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai }));

    const context = computed(() => {
        const serviceInfo = getServiceInfo();
        const voucherInfo = getVoucherInfo();
        return `
            Bạn là một chuyên gia phân tích dữ liệu cho cửa hàng thú cưng PetHaven. Hãy tuân thủ các quy tắc sau:

            1. Phân tích và tổng hợp dữ liệu về dịch vụ, khuyến mãi, và lịch hẹn của khách hàng.
            2. Đưa ra các nhận xét và xu hướng dựa trên dữ liệu được cung cấp.
            3. Tập trung vào các chỉ số quan trọng như doanh thu, tần suất sử dụng dịch vụ, và hiệu quả của các chương trình khuyến mãi.
            4. Đề xuất các chiến lược để cải thiện hiệu suất kinh doanh dựa trên phân tích dữ liệu.
            5. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            6. Sử dụng các số liệu cụ thể khi có thể để hỗ trợ các nhận định.
            7. Nếu không có đủ dữ liệu để đưa ra kết luận chính xác, hãy nêu rõ và đề xuất cách thu thập thêm dữ liệu.
            
            Thống kê dịch vụ:
            ${serviceInfo.map(service =>
            `-Tên dịch vụ :  ${service.ten}: Giá dịch vụ ${service.gia} USD`
        ).join('\n')}
            
            Thống kê khuyến mãi:
            ${voucherInfo.map(voucher =>
            `- Giảm ${voucher.phantramgiam}% phần trăm : ${voucher.mota} (Từ ngày ${voucher.ngaybatdau} đến ngày ${voucher.ngayketthuc})`
        ).join('\n')}
        
            Thống kê lịch hẹn:
            Tổng số lịch hẹn: ${lichHenStore.appointments.value?.length || 0}
            ${lichHenStore.appointments.value?.length > 0 ?
            `Phân loại theo trạng thái lịch hẹn:
            - Thành công: ${lichHenStore.appointments.value.filter(a => a.trangthai === 0).length}
            - Thất bại: ${lichHenStore.appointments.value.filter(a => a.trangthai === 1).length}
            - Đã hủy: ${lichHenStore.appointments.value.filter(a => a.trangthai === 2).length}
            - Chờ thanh toán: ${lichHenStore.appointments.value.filter(a => a.trangthai === 3).length}
            - Chờ xác nhận: ${lichHenStore.appointments.value.filter(a => a.trangthai === 4).length}
            - Rỗng: ${lichHenStore.appointments.value.filter(a => a.trangthai === 5).length}
            - Thanh toán thành công: ${lichHenStore.appointments.value.filter(a => a.trangthai === 6).length}
            - Đã hoàn tiền: ${lichHenStore.appointments.value.filter(a => a.trangthai === 7).length}
            - Chờ sử dụng: ${lichHenStore.appointments.value.filter(a => a.trangthai === 8).length}

            Dịch vụ được đặt nhiều nhất: ${(() => {
                const serviceCounts = lichHenStore.appointments.value.reduce((acc, appointment) => {
                    acc[appointment.dichvu.tendichvu] = (acc[appointment.dichvu.tendichvu] || 0) + 1;
                    return acc;
                }, {});
                const maxService = Object.entries(serviceCounts).reduce((a, b) => a[1] > b[1] ? a : b);
                return `${maxService[0]} (${maxService[1]} lần)`;
            })()}

            Tổng doanh thu từ lịch hẹn đã hoàn thành: ${
                lichHenStore.appointments.value
                    .filter(a => a.trangthai === 3)
                    .reduce((sum, a) => sum + a.dichvu.giatien, 0)
                    .toLocaleString()
            } USD`
            : 'Chưa có dữ liệu lịch hẹn.'}

            Hãy phân tích dữ liệu trên và đưa ra các nhận xét, xu hướng, và đề xuất để cải thiện hiệu suất kinh doanh của PetHaven.
        `;
    });

    const chatHistory = ref([]);

    const loadChatHistory = () => {
        const storedHistory = sessionStorage.getItem('aiChatHistory');
        if (storedHistory) {
            chatHistory.value = JSON.parse(storedHistory);
        } else {
            chatHistory.value = [
                {
                    role: "user",
                    parts: [{ text: context.value }],
                },
                {
                    role: "model",
                    parts: [{ text: "Xin chào! Tôi là nhân viên tôi sẽ giúp bạn thống kê! 🐶🐱" }],
                },
            ];
        }
    };

    watch(chatHistory, (newHistory) => {
        sessionStorage.setItem('aiChatHistory', JSON.stringify(newHistory));
    }, { deep: true });

    const sendMessage = async (prompt: string) => {
        try {
            chatHistory.value.push({ role: "user", parts: [{ text: context.value + "\n\n" + prompt }] });

            const chatSession = model.startChat({
                generationConfig,
                history: chatHistory.value,
            });

            const result = await chatSession.sendMessage(prompt);
            const responseText = result.response.text();

            chatHistory.value.push({ role: "model", parts: [{ text: responseText }] });

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
        refreshInterval.value = setInterval(fetchData, 60 * 1000);
    });

    return { sendMessage, chatHistory };
});