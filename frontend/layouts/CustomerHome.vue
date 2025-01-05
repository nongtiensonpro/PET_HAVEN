<template>
  <NuxtLayout>
    <div class="container p-4">
      <div class="row d-flex align-items-center " style=" margin: 5px">
        <!-- Logo và tiêu đề -->
        <div class="col-md-4 d-flex align-items-center p-1">
          <img :src="logoImage" class="img-fluid rounded-top me-2" alt="Logo">
          <div>
            <p class="m-0 text-logo">{{ logo }}</p>
            <p class="m-0 text-logo">{{ slogan }}</p>
          </div>
        </div>

        <!-- Thanh tìm kiếm, nút đăng nhập và ngôn ngữ -->
        <div class="col-md-8 d-flex align-items-center justify-content-end">
          <!-- Thanh tìm kiếm -->
          <nav class="navbar bg-body-tertiary flex-grow-1 me-2 " style="flex-basis: 50%;">
            <TimKiem />
          </nav>

          <!-- Nút đăng nhập và quản lý tài khoản -->
          <div>
            <div v-if="!isLoggedIn">
              <button type="button" class="custom-button btn-equal" @click="login1" style="border: none !important;">
                {{ login }}
              </button>
            </div>

            <div v-else>
              <button class="custom-button btn-equal" type="button" data-bs-toggle="offcanvas"
                data-bs-target="#offcanvasWithBothOptions" aria-controls="offcanvasWithBothOptions"
                style="border: none !important;">
                {{ account }}
              </button>

              <!-- Offcanvas quản lý tài khoản -->
              <div class="offcanvas offcanvas-start" data-bs-scroll="true" tabindex="-1" id="offcanvasWithBothOptions"
                aria-labelledby="offcanvasWithBothOptionsLabel"
                style="border-radius: 0 25px 25px 0; background: rgba(246, 246, 234, 0.75); backdrop-filter: blur(5px); box-shadow: 0 0 10px 5px rgba(246, 246, 234, 0.3);">
                <div class="offcanvas-header">
                  <h5 class="offcanvas-title" id="offcanvasWithBothOptionsLabel">
                    <template v-if="Array.isArray(userInfo.role)">
                      <div v-if="userInfo.role.includes('admin')">
                        {{ wellcome }} {{ admin }} {{ userInfo?.name }}
                      </div>
                      <div v-else-if="userInfo.role.includes('manager')">
                        {{ wellcome }} {{ staff }} {{ userInfo?.name }}
                      </div>
                      <div v-else>
                        {{ wellcome }} {{ owner }} {{ userInfo?.name }} !
                      </div>
                    </template>
                  </h5>
                  <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>

                <div class="offcanvas-body">
                  <div class="row">
                    <template
                      v-if="Array.isArray(userInfo.role) && (userInfo.role.includes('admin') || userInfo.role.includes('manager'))">
                      <div class="col-12 p-4">
                        <button type="button" class="custom-button btn-menu" @click="changeRole">
                          {{ customer_employee }}
                        </button>
                      </div>
                    </template>

                    <div class="col-12 p-4">
                      <nuxt-link class="nav-link" :to="`/user/infouser`">
                        <button type="button" class="custom-button btn-menu">
                          {{ infoacc }}
                        </button>
                      </nuxt-link>
                    </div>

                    <div class="col-12 p-4">
                      <nuxt-link class="nav-link" :to="`/user/appointment`">
                        <button type="button" class="custom-button btn-menu">
                          {{ appointment_management }}
                        </button>
                      </nuxt-link>
                    </div>

                    <div class="col-12 p-4">
                      <nuxt-link class="nav-link" :to="`/chat`">
                        <button type="button" class="custom-button btn-menu">
                          {{ chat_with_ai }}
                        </button>
                      </nuxt-link>
                    </div>

                    <div class="col-12 p-4">
                      <button type="button" class="custom-button btn-menu" @click="logout1">
                        {{ log_out }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Nút thay đổi ngôn ngữ -->
          <button type="button" class="custom-button btn-equal m-2 text-center" @click="changeLanguage"
            style="border: none !important;">
            {{ currentLanguage === 'vi' ? switchToEnglish : switchToVietnamese }}
          </button>

          <!-- Nút thông báo -->
          <div class="dropdown">
            <button class="custom-button btn-equal dropdown-toggle position-relative" type="button"
              style="border: none !important;" data-bs-toggle="dropdown" aria-expanded="false">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell-fill"
                viewBox="0 0 16 16">
                <path
                  d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2m.995-14.901a1 1 0 1 0-1.99 0A5 5 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901" />
              </svg>

              <!-- Badge hiển thị số lượng thông báo -->
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                {{ notifications.length }}
              </span>
            </button>

            <!-- Danh sách dropdown thông báo -->
            <ul class="dropdown-menu p-4 m-4">
              <li v-if="notifications.length === 0">
                {{ notifications_leng_null }} !
              </li>
              <li v-else>
                <button @click="removeAllNotifications" class="btn btn-link p-0 m-0 text-danger">
                  {{ remove_all }}
                </button>
              </li>
              <li v-for="notification in notifications" :key="notification.id">
                <a>{{ notification.type }}</a>
                <a class="dropdown-item">
                  {{ notification.message }}
                  <button @click="handleRemoveNotification(notification.id - 1)"
                    class="btn btn-link p-0 m-0 text-danger">
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
      <div class="text fs-5">
        <div v-if="viewRole === 0">
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
                    <div v-if="services.length == 0">
                      {{ serviceNotAvailable }}
                    </div>
                    <div v-else>
                      <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page">
                        <div v-if="service.hien && service.trangthai" style="font-size: medium;">{{
                          service.tendichvu
                        }}
                        </div>
                      </NuxtLink>
                    </div>
                  </li>
                  <li class="nav-item">
                    <div v-if="services.length > 0">
                      <Nuxt-link class="nav-link" :to="`/customer/calendar/newuser`" aria-current="page">
                        <div style="font-size: medium;">{{ book }}</div>
                      </Nuxt-link>
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
              <nuxt-link v-if="userInfo?.role && userInfo.role.includes('admin')" class="nav-link"
                :to="`/admin/adminhome`">
                {{ home }}
              </nuxt-link>
              <div v-else>
                Nhân viên
              </div>
              <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/service/servicelist`">{{ service }}</NuxtLink>
                  </li>
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/quanlytaikhoan`">{{ account }}</NuxtLink>
                  </li>
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/quanlyca`">{{ time_management }}</NuxtLink>
                  </li>
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/thongke`">{{ statistical }}</NuxtLink>
                  </li>
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/vouchers`">{{ vouchers }}</NuxtLink>
                  </li>
                  <li class="nav-item" v-if="userInfo?.role && userInfo.role.includes('admin')">
                    <NuxtLink class="nav-link" :to="`/admin/quanlylichhen`">{{ appointment_management }}</NuxtLink>
                  </li>
                  <li class="nav-item"
                    v-if="userInfo?.role && (userInfo.role.includes('admin') || userInfo.role.includes('manager'))">
                    <NuxtLink class="nav-link" :to="`/nhanvien/checkin`">{{ pay_at_the_counter }}</NuxtLink>
                  </li>
                </ul>
              </div>
            </div>
          </nav>
        </div>
      </div>

      <div class="container h-100 w-100 p-4 bg-opacity-75"
        style="border-radius: 25px; margin-top: 5px; background: rgba(246, 246, 234, 0.62); backdrop-filter: blur(5px); box-shadow: 0 0 10px 5px rgba(246, 246, 234, 0.3);">
        <NuxtPage />
      </div>

      <footer>
        <div v-if="viewRole == 0">
          <div class="container h-100 w-100 p-4 bg-opacity-75"
            style="border-radius: 25px; margin-top: 5px; background: rgba(246, 246, 234, 0.62); backdrop-filter: blur(5px); box-shadow: 0 0 10px 5px rgba(246, 246, 234, 0.3);">
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
                            src="../assets/image/logo-fb.png" width="32" height="32" class="me-3" alt="Facebook"></a>
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
                    </p>
                  </div>
                </div>
              </div>
              <div class="col">
                <h3>{{ servicesTitle }}</h3>
                <div v-if="services.length == 0">
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
                  width="100%" height="100%" style="border:0;" loading="lazy"></iframe>
              </div>
            </div>
            <div>
              <p class="text-center">{{ footerCopyright }}
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                  class="bi bi-heart-fill" viewBox="0 0 16 16">
                  <path fill-rule="evenodd"
                    d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" />
                </svg>
              </p>
            </div>
          </div>
        </div>
        <div v-else>
          <!--          Đây là footer dành cho nhân viên và quản lý-->
        </div>
      </footer>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { computed, ref, onMounted, onUnmounted } from 'vue';
import DichVu from '~/models/DichVu';
import logoImage from '@/image/LogoPetHaven.png';
import { useI18n } from 'vue-i18n';
import { useUserStore } from '~/stores/user';
import { useNotificationStore } from '~/stores/useNotificationStore';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';

let refreshInterval: number | null = null;
const toast = useToast();
const route = useRoute();
const router = useRouter();

const notificationStore = useNotificationStore();
const { addNotification, removeNotification } = notificationStore;

const notifications = computed(() => notificationStore.notifications);

const removeAllNotifications = () => {
  notificationStore.removeAllNotifications();
};

const handleAddNotification = (message: string, type: string) => {
  addNotification(message, type);
};

const handleRemoveNotification = (index: number) => {
  removeNotification(index);
};

const { t, locale } = useI18n();
const serviceStore = useServiceStore();

const services = computed((): DichVu[] => {
  if (!serviceStore.services || serviceStore.services.length === 0) {
    // Nếu services là null hoặc rỗng, thực hiện làm mới
    refreshServices();
    return [];
  }
  return serviceStore.services.filter((service: DichVu) => service.trangthai);
});

const refreshServices = async () => {
  try {
    await serviceStore.fetchServices();
  } catch (error) {

  }
};

// Thêm một watcher để theo dõi sự thay đổi của services
watch(() => serviceStore.services, (newServices) => {
  if (!newServices || newServices.length === 0) {
    refreshServices();
  }
});

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);
const isLoggedIn = computed(() => userStore.isLoggedIn);

const currentLanguage = ref(locale.value);

const logo = computed(() => t('logo'));
const slogan = computed(() => t('slogan'));
const home = computed(() => t('home'));
const switchToEnglish = computed(() => t('switchToEnglish'));
const switchToVietnamese = computed(() => t('switchToVietnamese'));
const aboutUs = computed(() => t('aboutUs'));
const introductionText = computed(() => t('introductionText'));
const servicesTitle = computed(() => t('services'));
const contact = computed(() => t('contact'));
const phone = computed(() => t('phone'));
const email = computed(() => t('email'));
const facebook = computed(() => t('facebook'));
const footerCopyright = computed(() => t('footerCopyright'));
const serviceNotAvailable = computed(() => t('serviceNotAvailable'));
const login = computed(() => t('login'));
const account = computed(() => t('account'));
const book = computed(() => t('book'));
const customer_employee = computed(() => t('customer_employee'));
const infoacc = computed(() => t('infoacc'));
const appointment_management = computed(() => t('appointment_management'));
const chat_with_ai = computed(() => t('chat_with_ai'));
const log_out = computed(() => t('log_out'));
const wellcome = computed(() => t('wellcome'));
const admin = computed(() => t('admin'));
const staff = computed(() => t('staff'));
const owner = computed(() => t('owner'));
const notifications_leng_null = computed(() => t('notifications_leng_null'));
const remove_all = computed(() => t('remove_all'));
const service = computed(() => t('service'));
const time_management = computed(() => t('time_management'));
const statistical = computed(() => t('statistical'));
const vouchers = computed(() => t('vouchers'));
const pay_at_the_counter = computed(() => t('pay_at_the_counter'));
const changeViewCustomer = computed(() => t('changeViewCustomer'));
const changeViewAdmin = computed(() => t('changeViewAdmin'));


const changeLanguage = () => {
  currentLanguage.value = currentLanguage.value === 'vi' ? 'en' : 'vi';
  locale.value = currentLanguage.value;
};

const viewRole = ref(0);

const refreshUserInfo = async () => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken) {
    await fetchUserInfo(accessToken);
  }
};


onMounted(() => {
  const code = route.query.code as string;

  if (code) {
    // console.log('Authorization code:', code);
    exchangeAuthorizationCodeForToken(code);
  } else {
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
      fetchUserInfo(accessToken);
    }
  }

  // Set up the interval to refresh user info every 5 minutes
  refreshInterval = window.setInterval(refreshUserInfo, 5 * 60 * 1000);
});

onUnmounted(() => {
  // Clear the interval when the component is unmounted
  if (refreshInterval !== null) {
    clearInterval(refreshInterval);
  }
});

if (process.client) {
  const storedViewRole = localStorage.getItem('viewRole');
  if (storedViewRole !== null) {
    viewRole.value = parseInt(storedViewRole, 10);
  } else {
    localStorage.setItem('viewRole', '0');
  }
}

const changeRole = () => {
  if (process.client) {
    if (viewRole.value === 0) {
      viewRole.value = 1;
      localStorage.setItem('viewRole', '1');
      addNotification(changeViewAdmin, 'system');
      return navigateTo('/admin/adminhome');
    } else {
      viewRole.value = 0;
      localStorage.setItem('viewRole', '0');
      addNotification(changeViewCustomer, 'system');
      return navigateTo('/');
    }
  }
};

const login1 = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak'
};

const logout1 = () => {
  const logoutUrl = `http://localhost:9082/realms/spring/protocol/openid-connect/logout`;
  const clientId = 'PetHaven';
  const redirectUri = encodeURIComponent('http://localhost:3000/');
  window.location.href = `${logoutUrl}?client_id=${clientId}&post_logout_redirect_uri=${redirectUri}`;
  localStorage.setItem('viewRole', '0');
  logout();
};

const exchangeAuthorizationCodeForToken = async (code: string) => {
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

    }

    const data = await response.json();
    if (data.error) {

    } else {
      localStorage.setItem('access_token', data.access_token);
      localStorage.setItem('refresh_token', data.refresh_token);

      router.push('/');
      await fetchUserInfo(data.access_token);


      userStore.setLoggedIn(true);
    }
  } catch (error) {
    localStorage.setItem('viewRole', '0');

  }
};

const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem('refresh_token');
  if (!refreshToken) {

    userStore.setLoggedIn(false);
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

    }

    const data = await response.json();
    localStorage.setItem('access_token', data.access_token);
    localStorage.setItem('refresh_token', data.refresh_token);

    return data.access_token;
  } catch (error) {
    localStorage.setItem('viewRole', '0');

    return null;
  }
};

const fetchUserInfo = async (accessToken: string) => {
  const url = 'http://localhost:8080/api/user';
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.status === 401) {

      const newAccessToken = await refreshAccessToken();
      if (newAccessToken) {
        return fetchUserInfo(newAccessToken);
      } else {

        userStore.setLoggedIn(false);
        return;
      }
    }

    if (!response.ok) {
      const errorData = await response.json();

      return;
    }

    const userInfoData = await response.json();


    const roles = userInfoData.roles || [];
    userStore.setUserInfo({
      name: userInfoData.username || '',
      role: roles || '',
      listThuCung: userInfoData.listThuCung || ''
    });

    userStore.setLoggedIn(true);

    if (userInfoData.username) {

    }
  } catch (error) {
    localStorage.setItem('viewRole', '0');
    userStore.setLoggedIn(false);
  }
};

const logout = () => {
  localStorage.setItem('viewRole', '0');
  userStore.clearUserInfo();
  localStorage.clear();
};

onMounted(() => {
  const code = route.query.code as string;

  if (code) {
    exchangeAuthorizationCodeForToken(code);
  } else {
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
      fetchUserInfo(accessToken);
    }
  }
});
</script>

<style scoped>
.btn-menu {
  min-width: 300px !important;
  text-align: left;
  padding-left: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: none;
}
</style>