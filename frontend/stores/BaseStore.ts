import { defineStore } from 'pinia';
import Pageable from '../models/Pageable';

interface FetchOptions {
    endpoint: string;
    model: any;
}

export const useApiService = defineStore('apiService', {
    state: () => ({
        data: [] as any[],
        pageable: {} as Pageable,
    }),
    actions: {
        async fetchData({ endpoint, model }: FetchOptions) {
            try {
                const response = await fetch(endpoint);

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                const data = await response.json();

                // Ghi lại dữ liệu để kiểm tra
                console.log(data);

                // Gán trực tiếp dữ liệu từ phản hồi API
                this.data = data.content.map((item: any) => new model(item)); // Chuyển đổi dữ liệu thành model
                this.pageable = data.page; // Cập nhật pageable
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        // Hàm loại bỏ hàm và các thuộc tính không cần thiết
        cleanPayload(payload: any) {
            return JSON.parse(JSON.stringify(payload, (key, value) => {
                if (typeof value === 'function') {
                    return undefined; // Bỏ qua hàm
                }
                return value;
            }));
        }
    },
});
