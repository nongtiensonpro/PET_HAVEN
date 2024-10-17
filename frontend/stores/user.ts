import { defineStore } from 'pinia'

interface UserInfo {
  sub: string
  name: string
  preferred_username: string
  given_name: string
  family_name: string
  email: string
  roles: string[]
}

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as UserInfo | null,
    isLoggedIn: false,
  }),
  actions: {
    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
    },
    clearUserInfo() {
      this.userInfo = null
      this.isLoggedIn = false
    },
  },
})