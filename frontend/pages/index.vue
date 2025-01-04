  <template>
    <div class="container">
      <div v-if="services.length === 0">
        {{serviceNotAvailable}}
      </div>
      <div v-else>
        <div>
          <div class="row">
            <div class="col">
              <LoiChaoKhiDangNhap  />
            </div>
            <div class="col">
              <VoucherDisplay :vouchers="vouchers" />
            </div>
          </div>
        <h2 class="text text-center">{{wecomehome}}</h2>

        <div  class="row ">
          <div
              v-for="service in paginatedServices"
              :key="service.id"
              class="card col m-1"
              :style="{
              '--card-width': calculateCardWidth(),
              '--card-height': calculateCardHeight(service)
            }"
          >
            <nuxt-link class="nav-link" :to="`/services/${service.id}`">
              <div class="card-container">
                <div class="card-body">
                  <div class="row">
                    <div class="col">
                      <div class="card-image-wrapper">
                        <img
                            v-if="!service.anh"
                            src="~/assets/image/cat1.jpg"
                            class="card-img-top"
                            alt="Ảnh mặc định nè"
                        />

                        <img
                            v-else
                            :src="service.anh"
                            class="card-img-top"
                            alt="..."
                            :style="{ objectFit: calculateImageFit(service) }"
                        />
                      </div>
                    </div>
                    <div class="col">
                       <p class="text fs-5 text-lg-start">
                         {{service.mota}}
                       </p>
                    </div>
                  </div>
                  <div class="card-header">
                    <h2 class="mb-0 fs-4">{{ service.tendichvu }}</h2>
                  </div>
                </div>
              </div>
            </nuxt-link>
          </div>
        </div>

        <div class="pagination d-flex justify-content-center align-items-center mt-3">
          <div class="row">
            <div class="col">
              <button @click="prevPage" :disabled="currentPage === 1" class="custom-button" style="background: none;border: none">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-left-circle-fill" viewBox="0 0 16 16">
                  <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0m3.5 7.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5z"/>
                </svg>
              </button>
            </div>
            <div class="col text fs-4">
                 {{ currentPage }} / {{ totalPages }}
            </div>
            <div class="col">
              <button @click="nextPage" :disabled="currentPage === totalPages" class="custom-button" style="background: none;border: none">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right-circle-fill" viewBox="0 0 16 16">
                  <path d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0M4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </template>

  <script setup lang="ts">
  import { useServiceStore } from '~/stores/DichVuStores';
  import { onMounted, computed, ref, onUpdated } from 'vue';
  import DichVu from '~/models/DichVu';
  import { useVoucherStore } from '~/stores/VorchersStores';
  import Voucher from '~/models/Voucher';
  import VoucherDisplay from "~/components/VoucherDisplay.vue";
  import {useI18n} from 'vue-i18n';

  const {t, locale} = useI18n();
  const voucherStore = useVoucherStore();
  const serviceStore = useServiceStore();
  const itemsPerPage = 4;
  const currentPage = ref(1);
  const windowWidth = ref(window.innerWidth);
  const wecomehome = computed(() => t('wecomehome'));
  const serviceNotAvailable = computed(() => t('serviceNotAvailable'));
  onMounted(() => {
    serviceStore.fetchServices();
    voucherStore.fetchVoucher();
    window.addEventListener('resize', handleResize);
  });

  onUpdated(() => {
    // Cập nhật lại chiều rộng thẻ khi component được cập nhật (ví dụ: thay đổi kích thước cửa sổ)
  });

  const handleResize = () => {
    windowWidth.value = window.innerWidth;
  };

  const services = computed((): DichVu[] => {
    return serviceStore.services.filter((service: DichVu) => service.trangthai && service.hien);
  });

  const vouchers = computed(() : Voucher[] => {
    return voucherStore.ListVoucher.filter((voucher: Voucher) => voucher.trangthai);
  });

  const totalPages = computed(() => Math.ceil(services.value.length / itemsPerPage));

  // Dynamic card width based on number of columns and available space
  const calculateCardWidth = () => {
    const columns = windowWidth.value >= 1200 ? 3 : windowWidth.value >= 992 ? 2 : 1;
    const containerPadding = 32; // Adjust as needed based on your CSS
    const availableWidth = windowWidth.value - containerPadding;
    return `${availableWidth / columns}px`;
  };

  // Calculate card height based on image aspect ratio or a fixed value
  const calculateCardHeight = (service: DichVu) => {
    if (service.anh) {
      // You'll need to fetch the image dimensions to do this properly
      // This is a placeholder, replace with actual logic
      return 'auto'; // Or a calculated value based on aspect ratio
    }
    return '300px'; // Default height if no image
  };

  const calculateImageFit = (service:DichVu) =>{
    if(service.anh){
      return 'cover'
    }
    return 'contain'
  }

  const paginatedServices = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return services.value.slice(start, start + itemsPerPage);
  });

  const nextPage = () => {
    if (currentPage.value < totalPages.value) {
      currentPage.value++;
    }
  };

  const prevPage = () => {
    if (currentPage.value > 1) {
      currentPage.value--;
    }
  };
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
  });
  </script>

  <style scoped>
  .card-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); /* Responsive columns */
    grid-gap: 1rem;
  }
  .card {
    width: var(--card-width); /* Dynamic width */
    height: var(--card-height);
    display: flex; /* Ensure card content fills the height */
    flex-direction: column;
  }
  .card-body {
    flex-grow: 1; /* Allow body to take up available space */
    display: flex;
    flex-direction: column;
  }
  .card-image-wrapper {
    height: 200px; /* Or a percentage */
    overflow: hidden; /* Prevent image overflow */
    display: flex;
    align-items: center; /* Center vertically */
    justify-content: center; /* Center horizontally */
  }
  .card-img-top {
    width: 100%;
    height: 100%;
    object-fit: contain; /* Or 'cover' depending on desired behavior */
  }

  </style>