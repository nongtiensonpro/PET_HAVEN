import { createRouter, createWebHistory } from 'vue-router';
import App from '../App.vue';


const routes = [
    {
        path: '/',
        name: 'Home',
        component: App,
    },
    // Bạn có thể thêm các route khác ở đây
];

// Tạo router với lịch sử web
const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
