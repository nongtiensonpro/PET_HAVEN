import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';

export default defineNuxtRouteMiddleware(() => {

    if (process.client) {
        const accessToken = sessionStorage.getItem('access_token');
        const viewRole = sessionStorage.getItem('viewRole');

        const userStore = useUserStore();
        const toast = useToast();


        if (!accessToken || !viewRole) {
            toast.error('Vui lòng đăng nhập để tiếp tục.');
            return navigateTo('/');
        }

        if (!userStore.userInfo) {
            toast.error('Đã hết hạn đăng nhập. Vui lòng đăng nhập lại!');
            return navigateTo('/');
        }


        // if (accessToken && viewRole === '1') {
        //     return navigateTo('/admin/adminhome');
        // }

        // return navigateTo('/');
    }
});
