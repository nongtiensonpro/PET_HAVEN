import { defineNuxtConfig } from 'nuxt';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineNuxtConfig({
    app: {
        head: {
            title: 'My Nuxt App',
            meta: [
                { name: 'viewport', content: 'width=device-width, initial-scale=1' },
                { charset: 'utf-8' },
            ],
        },
    },
    vite: {
        plugins: [
            vue(), // Đảm bảo plugin Vue được thêm vào đây
        ],
        resolve: {
            alias: {
                '@': path.resolve(__dirname, './src'),
            },
        },
        server: {
            port: 9200,
            proxy: {
                '/api': {
                    target: 'http://localhost:8081', // Địa chỉ của server Node.js
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ''),
                },
            },
        },
    },
    typescript: {
        strict: true, // Bật chế độ kiểm tra nghiêm ngặt của TypeScript
    },
});
