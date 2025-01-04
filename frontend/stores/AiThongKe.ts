import { GoogleGenerativeAI } from '@google/generative-ai';
import { useServiceStore } from '~/stores/DichVuStores';
import { useVoucherStore } from '~/stores/VorchersStores';
import { useUserStore } from '~/stores/user';
import { computed, onMounted, ref, watch } from 'vue';
import { useQuanLyLichHenKhachHang } from '~/stores/QuanLyLichHenKhachHang';
import type BookingData  from './MauKhachDatDichVu';
import DichVu from '~/models/DichVu';

export const useAIThongKeStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();
    const lichHenStore = useQuanLyLichHenKhachHang();
    const chatHistory = ref([]);

    const apiKey = 'AIzaSyClcxWPh0hpZrh9Cf996fL3X0dUPjwYuOQ';
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });

    const generationConfig = {
        temperature: 1,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const services = computed(() =>
        serviceStore.services.filter((service: DichVu) => service.trangthai && service.hien)
    );

    const vouchers = computed(() =>
        voucherStore.ListVoucher.filter(voucher => voucher.trangthai)
    );

    const getLowestPrice = (service: DichVu): number => {
        return service.tuyChonDichVus
            .flatMap(tuyChon => tuyChon.tuyChonCanNangs)
            .reduce((min, canNang) => Math.min(min, canNang.giaTien), Infinity);
    };

    const context = computed(() => {
        const serviceInfo = services.value.map((service: DichVu) => ({
            id: service.id,
            ten: service.tendichvu,
            gia: getLowestPrice(service)
        }));

        const voucherInfo = vouchers.value.map(({ id, phantramgiam, ngaybatdau, ngayketthuc }) => 
            ({ id, phantramgiam, ngaybatdau, ngayketthuc })
        );

        const appointments = lichHenStore.appointments.value || [];

        const statusMap = {
            0: 'Thành công', 1: 'Thất bại', 2: 'Đã hủy', 3: 'Chờ thanh toán',
            4: 'Chờ xác nhận', 5: 'Rỗng', 6: 'Thanh toán thành công',
            7: 'Đã hoàn tiền', 8: 'Chờ sử dụng'
        };
        
        const countServices = appointments.reduce((acc, a: any) => {
            acc[a.dichvu.tendichvu] = (acc[a.dichvu.tendichvu] || 0) + 1;
            return acc;
        }, {} as Record<string, number>);

        const mostBookedService = Object.entries(countServices).length > 0
            ? Object.entries(countServices).reduce((a, b) => a[1] > b[1] ? a : b)
            : ['Không có dịch vụ nào', 0];

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
            ${serviceInfo.map(service => `- ${service.ten}: Từ ${service.gia} USD`).join('\n')}

            Thống kê khuyến mãi:
            ${voucherInfo.map(voucher => `- Giảm ${voucher.phantramgiam}%: Từ ${voucher.ngaybatdau} đến ${voucher.ngayketthuc}`).join('\n')}

            Thống kê lịch hẹn:
            Tổng số lịch hẹn: ${appointments.length}
            ${appointments.length > 0 ? `
            Phân loại theo trạng thái lịch hẹn:
           ${Object.entries(statusMap).map(([status, label]) => `- ${label}: ${appointments.filter((a: BookingData) => a.trangthai === parseInt(status)).length}`).join('\n')}

            Dịch vụ được đặt nhiều nhất: ${mostBookedService[0]} (${mostBookedService[1]} lần)
            Tổng doanh thu từ lịch hẹn đã hoàn thành: ${totalRevenue} USD
            ` : 'Chưa có dữ liệu lịch hẹn.'}

            Hãy phân tích dữ liệu trên và đưa ra các nhận xét, xu hướng, và đề xuất để cải thiện hiệu suất kinh doanh của PetHaven.
        `;
    });

    const loadChatHistory = () => {
        const storedHistory = sessionStorage.getItem('aiChatHistory');
        if (storedHistory) {
            chatHistory.value = JSON.parse(storedHistory);
        } else {
            chatHistory.value = [
                { role: "user", parts: [{ text: context.value }] },
                { role: "model", parts: [{ text: "Xin chào! Tôi là nhân viên tôi sẽ giúp bạn thống kê! 🐶🐱" }] },
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
        console.log("Dữ liệu AI sẽ nhận:", context.value);
    });

    return { sendMessage, chatHistory };
});