import { createApp } from 'vue'
import App from './App.vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import router from './router/index.ts'


createApp(App)
    .use(router)   // Sử dụng Vue Router cho điều hướng
    // .use(store)    // Sử dụng Vuex hoặc Pinia cho quản lý trạng thái
    .mount('#app') // Gắn ứng dụng Vue vào thẻ HTML có id 'app'
