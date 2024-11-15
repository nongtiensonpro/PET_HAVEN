import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';
import { useAsyncData } from '#imports';

export default defineNuxtRouteMiddleware(async (to) => {
    if (process.client) {
        const userStore = useUserStore();
        const toast = useToast();

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        const { data: userInfo, error } = await useAsyncData('userInfo', async () => {
            const accessToken = localStorage.getItem('access_token');
            const viewRole = localStorage.getItem('viewRole');

            if (!accessToken || !viewRole) {
                toast.error('Vui lòng đăng nhập để tiếp tục.');
                return navigateTo('/');
            }

            // Kiểm tra xem token đã hết hạn hay chưa
            if (!userStore.userInfo) {
                toast.error('Đã hết hạn đăng nhập. Đang cố gắng đăng nhập lại!');

                const refreshToken = localStorage.getItem('refresh_token');
                if (!refreshToken) {
                    toast.error('Không tìm thấy refresh token. Vui lòng đăng nhập lại.');
                    return navigateTo('/');
                }

                const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/token';
                const params = new URLSearchParams({
                    grant_type: 'refresh_token',
                    refresh_token: refreshToken,
                    client_id: 'PetHaven',
                    client_secret: 'GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL',
                });

                try {
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: params.toString()
                    });

                    if (!response.ok) {
                        toast.error("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.");
                        return navigateTo('/');
                    }

                    const data = await response.json();
                    localStorage.setItem('access_token', data.access_token);
                    localStorage.setItem('refresh_token', data.refresh_token);
                    console.log('New access token:', data.access_token);

                    return data;
                } catch (error) {
                    console.error('Lỗi khi làm mới token:', error);
                    toast.error('Đăng nhập lại thất bại!');
                    return navigateTo('/');
                }
            }

            return userStore.userInfo;
        });

        // Kiểm tra xem có lỗi xảy ra khi fetch token hay không
        if (error.value) {
            toast.error('Đăng nhập lại thất bại!');
            return navigateTo('/');
        }

        
    }
});