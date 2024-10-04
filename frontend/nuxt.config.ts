export default {
    // Global page headers
    ssr: true,
    head: {
        title: 'My Nuxt App',
        meta: [
            { charset: 'utf-8' },
            { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        ],
    },

    css: [
        './assets/css/bootstrap.min.css'
    ],

    js: [
        './assets/js/bootstrap.min.js'
    ],

    buildModules: [
        '@nuxt/typescript-build',
    ],

    devtools: {
        enabled: true,
        timeline: {
            enabled: true,
        },
    },

    server: {
        port: 3000,
    },

    compatibilityDate: '2024-10-03',

    modules: [
        '@nuxtjs/i18n',
        '@pinia/nuxt',
        '@nuxt/image',
    ],

    i18n: {
        locales: [
            { code: 'en', name: 'English', file: 'en.ts' },
            { code: 'vi', name: 'Tiếng Việt', file: 'vi.ts' }
        ],
        defaultLocale: 'vi',
        lazy: true,
        langDir: 'i18n/',
        debug: true,
    },

    alias: {
        pinia: '/node_modules/@pinia/nuxt/node_modules/pinia/dist/pinia.mjs',
    }
};
