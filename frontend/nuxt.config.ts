export default {
    // Global page headers
    head: {
        title: 'My Nuxt App',
        meta: [
            {charset: 'utf-8'},
            {name: 'viewport', content: 'width=device-width, initial-scale=1'},
        ],
    },
    css: [
        './assets/css/bootstrap.min.css'
    ],
    js: [
        './assets/js/bootstrap.min.js'
    ]
    ,buildModules: [
        '@nuxt/typescript-build',
    ],
    axios: {
        baseURL: 'http://localhost:8080/api' // Thay đổi URL này thành URL của API của bạn
    },
    compatibilityDate: '2024-10-03'
};