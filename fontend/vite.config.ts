import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
    plugins: [vue()], // Đảm bảo plugin Vue được thêm vào đây
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
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
});
