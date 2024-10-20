<template>
  <NuxtLayout>
    <div class="container">
      <div class="row d-flex align-items-center">
        <!-- Logo và slogan -->

        <div class="col col-md-4 d-flex align-items-center">
          <img :src="logoImage" class="img-fluid rounded-top me-2" alt="">
          <div>
            <p class="m-0 text-logo">{{ logo }}</p>
            <p class="m-0 text-logo">{{ slogan }}</p>
          </div>
        </div>

        <!-- Thanh tìm kiếm và nút -->
        <div class="col col-md-8 d-flex align-items-center justify-content-end">
          <nav class="navbar bg-body-tertiary flex-grow-1 me-2">
            <form class="d-flex w-100" role="search">
              <input class="no-b w-100" type="search" :placeholder="searchPlaceholder">
              <button class="no-b" type="submit">{{ searchButton }}</button>
            </form>
          </nav>

          <div v-if="!isLoggedIn">
            <button type="button" class="custom-button" style="min-width: 60px " @click="login1">
              {{ login }}
            </button>
          </div>
          <div v-else>
            <button class="custom-button" style="min-width: 150px" type="button" data-bs-toggle="offcanvas"
                    data-bs-target="#offcanvasWithBothOptions" aria-controls="offcanvasWithBothOptions">
              Cài đặt
            </button>
            <div class="offcanvas offcanvas-start" data-bs-scroll="true" tabindex="-1" id="offcanvasWithBothOptions"
                 aria-labelledby="offcanvasWithBothOptionsLabel">
              <div class="offcanvas-header">
                <h5 class="offcanvas-title" id="offcanvasWithBothOptionsLabel">
                  Chào mừng {{
                    Array.isArray(userInfo.role) && userInfo.role.includes('admin') ? 'Admin' : 'Khách hàng'
                  }} {{ userInfo.name }}
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
              </div>
              <div class="offcanvas-body">
                <div v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin')">
                  <button type="button" class="custom-button" @click="changeRole" >
                    Khách hàng/ Nhân Viên
                  </button>
                </div>
                <div class="row">
                  <div class="col-12 p-4">
                    <!-- Button trigger modal -->
                    <button type="button" class="custom-button" data-bs-toggle="modal" data-bs-target="#staticBackdrop">
                      Cài đặt tài khoản
                    </button>

                    <!-- Modal -->
                    <div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                      <div class="modal-dialog">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5" id="staticBackdropLabel">Modal title</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            ...
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary">Understood</button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-12 p-4">
                    <button type="button" class="custom-button" @click="logout1">
                      Đăng xuất
                    </button>
                  </div>
                </div>

                <br>

              </div>
            </div>
          </div>
          <button type="button" style="max-width: 30px; text-align: center" class="custom-button" @click="changeLanguage">
            {{ currentLanguage === 'vi' ? switchToEnglish : switchToVietnamese }}
          </button>
          <div class="dropdown">
            <!-- Nút dropdown kết hợp với badge thông báo -->
            <button class="custom-button dropdown-toggle position-relative" style="min-width: 100px" type="button" data-bs-toggle="dropdown"
                    aria-expanded="false">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell-fill" viewBox="0 0 16 16">
                <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2m.995-14.901a1 1 0 1 0-1.99 0A5 5 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901"/>
              </svg>

              <!-- Badge hiển thị số lượng thông báo -->
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                  <span class="badge-pill">{{ notifications.length }}</span>
                </span>
            <span class="visually-hidden">unread messages</span>
            </button>
            <!-- Danh sách dropdown -->

            <ul class="dropdown-menu p-4 m-4" >
              <li v-if="notifications.length==0">
                Không có thông báo nào !
              </li>
              <li v-else>
                <button @click="removeAllNotifications" class="btn btn-link p-0 m-0 text-danger">
                  Xóa tất cả thông báo
                </button>
              </li>
              <li v-for="notification in notifications" :key="notification.id">
                <a v-if="notification.type='system'">Hệ thống</a>
                <a class="dropdown-item" href="#" >
                   {{ notification.message }}
                  <button @click="handleRemoveNotification(notification.id-1)" class="btn btn-link p-0 m-0 text-danger">
                    X
                  </button>
                </a>
                <hr>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Navbar -->
      <div class="container">
        <div v-if="viewRole==0">
          <nav class="navbar navbar-expand-lg bg-body-tertiary">
            <div class="container-fluid">
              <NuxtLink class="nav-link active" aria-current="page" to="/">{{ home }}</NuxtLink>
              <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                      aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="navbar">
                <ul class="navbar-nav">
                  <li class="nav-item" v-for="service in services" :key="service.id">
                    <div v-if="services.length==0">
                      {{ serviceNotAvailable }}
                    </div>
                    <div v-else>
                      <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page">
                        <div style="font-size: medium;">{{ service.tendichvu }}</div>
                      </NuxtLink>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </nav>
        </div>
        <div v-else>
          <nav class="navbar navbar-expand-lg bg-body-tertiary">
            <div class="container-fluid">
              <nuxt-link class="nav-link" :to="`/admin/adminhome`">Admin</nuxt-link>
              <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                      aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                  <li class="nav-item">
                    <div class="dropdown">
                      <a class="nav-link" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Dịch vụ
                      </a>

                      <ul class="dropdown-menu">
                        <li><nuxt-link class="dropdown-item" :to="`/admin/service/servicelist`">Tổng quan dịch vụ</nuxt-link></li>
                        <li><nuxt-link class="dropdown-item" :to="`/admin/service/addservice`">Thêm dịch vụ</nuxt-link></li>
                      </ul>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </nav>
        </div>
      </div>
      <div class="container h-100 w-100">
        <NuxtPage/>
      </div>
      <footer>
        <div v-if="viewRole==0">
          <div class="container">
            <div class="row">
              <div class="col">
                <div class="row">
                  <div class="col">
                    <img :src="logoImage" class="img-fluid rounded-top me-2" alt="">
                  </div>
                  <div class="col">
                    <div class="row">
                      <div class="col">
                        <h3>{{ logo }}</h3>
                        <p>{{ slogan }}</p>
                      </div>
                      <div class="col">
                        <a href="https://facebook.com/nongtiensonpro" role="button"><img
                            src="../assets/image/logo-fb.png"
                            width="32" height="32"
                            class="me-3" alt="Facebook"></a>
                        <a href="https://facebook.com/nongtiensonpro" role="button"><img
                            src="../assets/image/logo-youtobe.png" width="32" height="32" class="me-3"
                            alt="YouTube"></a>
                      </div>
                    </div>
                  </div>
                  <div>
                    <h3>{{ aboutUs }}</h3>
                    <p>
                      {{ introductionText }}
                      <a href="#" role="button">{{ moreInfo }}</a>
                    </p>
                  </div>
                </div>
              </div>
              <div class="col">
                <h3>{{ servicesTitle }}</h3>

                <div v-if="services.length==0">
                  {{ serviceNotAvailable }}
                </div>
                <div v-else>
                  <ul>
                    <li class="nav-item" v-for="service in services" :key="service.id">
                      <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page">
                        {{ service.tendichvu || serviceNotAvailable }}
                      </NuxtLink>
                    </li>
                  </ul>
                </div>


              </div>
              <div class="col">
                <div class="col col-md-auto">
                  <h3>{{ contact }}</h3>
                  <p>{{ phone }}</p>
                  <p>{{ email }}</p>
                  <p>{{ facebook }}</p>
                </div>
              </div>
              <div class="col">
                <iframe
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3413.541940628307!2d105.74466886584491!3d21.038129618650437!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313455e940879933%3A0xcf10b34e9f1a03df!2zVHLGsOG7nW5nIENhbyDEkeG6s25nIEZQVCBQb2x5dGVjaG5pYw!5e1!3m2!1svi!2s!4v1681224799580!5m2!1svi!2s"
                    width="100%" height="100%" style="border:0;" allowfullscreen="" loading="lazy"></iframe>
              </div>
            </div>
            <div>
              <p class="text-center">{{ footerCopyright }}</p>
            </div>
          </div>
        </div>
        <div v-else>
          Đây là footer dành cho nhân viên và quản lý
        </div>
      </footer>
    </div>
  </NuxtLayout>
</template>

<script lang="ts">
import {useServiceStore} from '~/stores/DichVuStores';
import {computed, ref} from 'vue';
import DichVu from '~/models/DichVu';
import logoImage from '~/assets/image/LogoPetHaven.png'; // Import logo
import {useI18n} from 'vue-i18n';
import {useUserStore} from '~/stores/user';
import {useNotificationStore} from '~/stores/useNotificationStore';

export default {
  mounted() {
    const code = this.$route.query.code;

    if (code) {
      console.log('Authorization code:', code);
      this.exchangeAuthorizationCodeForToken(code);
    } else {
      const accessToken = sessionStorage.getItem('access_token');
      if (accessToken) {
        this.fetchUserInfo(accessToken);
      }
    }
  },
  setup() {
    const notificationStore = useNotificationStore();
    const { addNotification, removeNotification  } = notificationStore;

    const notifications = notificationStore.notifications;

    const removeAllNotifications = () => {
      notificationStore.removeAllNotifications();
    };

    const handleAddNotification = (message: String,type: String) => {
      addNotification(message, type);
    };

    const handleRemoveNotification = (index: number) => {
      removeNotification(index);
    };
    const {t, locale} = useI18n();
    const serviceStore = useServiceStore();
    const services = computed((): DichVu[] => serviceStore.services);

    const userStore = useUserStore();

    const userInfo = computed(() => userStore.userInfo);
    const isLoggedIn = computed(() => userStore.isLoggedIn);

    const currentLanguage = ref(locale.value);

    const logo = computed(() => t('logo'));
    const slogan = computed(() => t('slogan'));
    const searchPlaceholder = computed(() => t('searchPlaceholder'));
    const searchButton = computed(() => t('searchButton'));
    const home = computed(() => t('home'));
    const switchToEnglish = computed(() => t('switchToEnglish'));
    const switchToVietnamese = computed(() => t('switchToVietnamese'));
    const aboutUs = computed(() => t('aboutUs'));
    const introductionText = computed(() => t('introductionText'));
    const moreInfo = computed(() => t('moreInfo'));
    const servicesTitle = computed(() => t('services'));
    const contact = computed(() => t('contact'));
    const phone = computed(() => t('phone'));
    const email = computed(() => t('email'));
    const facebook = computed(() => t('facebook'));
    const footerCopyright = computed(() => t('footerCopyright'));
    const serviceNotAvailable = computed(() => t('serviceNotAvailable'));
    const login = computed(() => t('login'));


    const changeLanguage = () => {
      currentLanguage.value = currentLanguage.value === 'vi' ? 'en' : 'vi';
      locale.value = currentLanguage.value;
    };
    const viewRole = ref(0);
    const changeRole = () => {
      if (viewRole.value === 0) {
        viewRole.value = 1;
        addNotification('Đã chuyển sang giao diện Nhân viên', 'system');
      } else {
        viewRole.value = 0;
        addNotification('Đã chuyển sang giao diện Khách hàng', 'system');
      }
    }
    return {
      services,
      logo,
      slogan,
      logoImage,
      searchPlaceholder,
      searchButton,
      home,
      switchToEnglish,
      switchToVietnamese,
      aboutUs,
      introductionText,
      moreInfo,
      servicesTitle,
      contact,
      phone,
      email,
      facebook,
      footerCopyright,
      serviceNotAvailable,
      changeLanguage,
      currentLanguage,
      login,
      userInfo,
      isLoggedIn,
      viewRole,
      notifications,
      handleRemoveNotification,
      changeRole,
      removeAllNotifications
    };
  },
  methods: {
    login1() {
      window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak'
    },
    logout1() {
      const logoutUrl = `http://localhost:9082/realms/spring/protocol/openid-connect/logout`;
      const clientId = 'PetHaven'; // Client ID của bạn
      const redirectUri = encodeURIComponent('http://localhost:3000/');
      window.location.href = `${logoutUrl}?client_id=${clientId}&post_logout_redirect_uri=${redirectUri}`;
      this.logout()
    },
    async exchangeAuthorizationCodeForToken(code: string) {
      const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/token';

      const params = new URLSearchParams({
        grant_type: 'authorization_code',
        code: code,
        client_id: 'PetHaven',
        client_secret: 'GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL',
        redirect_uri: 'http://localhost:3000'
      });

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: params.toString()
        });

        if (!response.ok) {
          const errorData = await response.json();
          console.error('Error exchanging authorization code for token:', errorData.error_description);
        }

        const data = await response.json();
        if (data.error) {
          console.error('Error fetching token:', data.error_description);
        } else {
          sessionStorage.setItem('access_token', data.access_token);
          sessionStorage.setItem('refresh_token', data.refresh_token);
          console.log('Access token:', data.access_token);
          this.$router.push('/'); // Chuyển hướng về trang chủ
          await this.fetchUserInfo(data.access_token);
          console.log("Lấy thông tin tài khoản sau khi lấy token", data.access_token);

          this.isLoggedIn = true;
        }
      } catch (error) {
        console.error('Error fetching token:', error);
      }
    },
    async refreshAccessToken() {
      const refreshToken = sessionStorage.getItem('refresh_token');
      if (!refreshToken) {
        console.error('No refresh token available');
        this.isLoggedIn = false;
        return;
      }

      const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/token';

      const params = new URLSearchParams({
        grant_type: 'refresh_token',
        refresh_token: refreshToken,
        client_id: 'PetHaven',
        client_secret: 'GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL',
      });

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: params.toString()
        });

        if (!response.ok) {
          const errorData = await response.json();
          console.error(`Error refreshing token: ${errorData.error_description}`);
        }

        const data = await response.json();
        sessionStorage.setItem('access_token', data.access_token);
        sessionStorage.setItem('refresh_token', data.refresh_token);
        console.log('New access token:', data.access_token);
        return data.access_token;
      } catch (error) {
        console.error('Error refreshing token:', error);
        return null;
      }
    },
    async fetchUserInfo(accessToken: string) {
      //const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/userinfo';
      const url = 'http://localhost:8080/api/user';
      const userStore = useUserStore();
      try {
        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.status === 401) {
          console.log('Access token expired, refreshing token...');
          const newAccessToken = await this.refreshAccessToken();
          if (newAccessToken) {
            return this.fetchUserInfo(newAccessToken);
          } else {
            console.error('Unable to refresh access token');
            return;
          }
        }

        if (!response.ok) {
          const errorData = await response.json();
          console.error(`Error fetching user info: ${errorData.error_description}`);
        }

        const userInfoData = await response.json();
        console.log('User Info:', userInfoData);

        const roles = userInfoData.roles || [];
        userStore.setUserInfo({
          name: userInfoData.username || '',
          role: roles || ''
        });

      } catch (error) {
        console.error('Error fetching user info:', error);
      }
    }
    ,
    logout() {
      const userStore = useUserStore();
      userStore.clearUserInfo();
      sessionStorage.clear();
    },

  }
};
</script>

<style scoped>
.navbar {
  background: #F8F8F1 !important;
  border-radius: 25px;
  border: 1px solid #400D01;
}

.no-b {
  border: none;
  outline: none;
  cursor: pointer;
  background: none;
}

.text-logo {
  color: #400D01;
  font-family: Pacifico, sans-serif;
  font-size: 25px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.custom-button {
  background-color: transparent;
  border: 1px solid #400D01;
  color: #400D01;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 12px;
  width: 80%;
}
</style>