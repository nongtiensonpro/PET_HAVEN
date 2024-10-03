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
  ],

  buildModules: [
          '@nuxt/typescript-build',
      ],

  devtools: {
    enabled: true,

    timeline: {
      enabled: true
    }
  },

  compatibilityDate: '2024-10-03',
    modules: ['@pinia/nuxt'],
    alias: {
        pinia: '/node_modules/@pinia/nuxt/node_modules/pinia/dist/pinia.mjs',
    }
};