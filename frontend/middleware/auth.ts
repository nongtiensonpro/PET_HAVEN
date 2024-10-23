import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';
import { useRouter } from 'vue-router';

export default defineNuxtRouteMiddleware(async (to) => {
    if (process.client) {
        const accessToken = sessionStorage.getItem('access_token');
        const viewRole = sessionStorage.getItem('viewRole');

        const userStore = useUserStore();
        const toast = useToast();
        const router = useRouter();

        if (!accessToken || !viewRole) {
            toast.error('Vui lòng đăng nhập để tiếp tục.');
            return navigateTo('/login');
        }


        if (!userStore.userInfo) {
            toast.error('Đã hết hạn đăng nhập. Đang cố gắng đăng nhập lại!');
            const currentRoute = to.fullPath;

            try {

                window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak';

            } catch (error) {
                console.error('Đăng nhập lại thất bại !:', error);
                sessionStorage.setItem('viewRole', '0');
                sessionStorage.removeItem('access_token');
                return navigateTo('/login');
            }
        }
    }
});
