import { defineNuxtPlugin } from '#app'
import autoExpandTextarea from '~/directives/autoExpandTextarea'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.directive('auto-expand-textarea', autoExpandTextarea)
})