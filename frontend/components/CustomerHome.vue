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
            <input class="noboder w-100" type="search" :placeholder="searchPlaceholder">
            <button class="noboder" type="submit">{{ searchButton }}</button>
          </form>
        </nav>
        <button type="button" class="custom-button">
          {{ login }}
        </button>
        <button type="button" class="custom-button" @click="changeLanguage">
          {{ currentLanguage === 'vi' ? switchToEnglish : switchToVietnamese }}
        </button>
      </div>
    </div>

    <!-- Navbar -->
    <div class="container">
      <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
          <NuxtLink class="nav-link active" aria-current="page" to="/">{{ home }}</NuxtLink>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbar">
            <ul class="navbar-nav">
              <li class="nav-item" v-for="service in services" :key="service.id">
                <div v-if="services.length==0">
                  {{serviceNotAvailable}}
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

    <NuxtPage />

    <footer>
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
                    <a href="https://facebook.com/nongtiensonpro" role="button"><img src="../assets/image/logo-fb.png" width="32" height="32" class="me-3" alt="Facebook"></a>
                    <a href="https://facebook.com/nongtiensonpro" role="button"><img src="../assets/image/logo-youtobe.png" width="32" height="32" class="me-3" alt="YouTube"></a>
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
                {{serviceNotAvailable}}
              </div>
              <div v-else>
                <ul>
                <li class="nav-item" v-for="service in services" :key="service.id">
                  <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page">{{ service.tendichvu || serviceNotAvailable }}</NuxtLink>
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
            <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3413.541940628307!2d105.74466886584491!3d21.038129618650437!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313455e940879933%3A0xcf10b34e9f1a03df!2zVHLGsOG7nW5nIENhbyDEkeG6s25nIEZQVCBQb2x5dGVjaG5pYw!5e1!3m2!1svi!2s!4v1681224799580!5m2!1svi!2s" width="100%" height="100%" style="border:0;" allowfullscreen="" loading="lazy"></iframe>
          </div>
        </div>
        <div>
          <p class="text-center">{{ footerCopyright }}</p>
        </div>
      </div>
    </footer>
  </div>
  </NuxtLayout>
</template>

<script lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { computed, ref } from 'vue';
import DichVu from '~/models/DichVu';
import logoImage from '~/assets/image/LogoPetHaven.png';// Import logo

export default {
  async asyncData({ store }) {
    await store.dispatch('serviceStore/fetchServices'); // Gọi action để lấy dữ liệu từ API
  },
  setup() {
    const { t, locale } = useI18n();
    const serviceStore = useServiceStore();
    const services = computed((): DichVu[] => serviceStore.services);

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

      // // Cập nhật lại thông điệp để phản ánh sự thay đổi ngôn ngữ
      // console.log(`Ngôn ngữ đã được thay đổi sang: ${currentLanguage.value}`);
    };

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
      login
    };
  },
};
</script>

<style scoped>
.navbar {
  background: #F8F8F1 !important;
  border-radius: 25px;
  border: 0.6px solid #400D01;
}
.noboder {
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
  border: 0.6px solid #400D01;
  color: #400D01;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 12px;
}
</style>