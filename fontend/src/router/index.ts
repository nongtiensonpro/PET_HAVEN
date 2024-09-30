import { createRouter, createWebHistory } from 'vue-router';
import AnimalList from '../components/AnimalList.vue';

// Định nghĩa các route
const routes = [
    {
        path: '/',
        name: 'Home',
        component: AnimalList,
    },
    // Bạn có thể thêm các route khác ở đây
];

// Tạo router với lịch sử web
const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
