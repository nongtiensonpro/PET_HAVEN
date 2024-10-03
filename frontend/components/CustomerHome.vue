<script setup lang="ts">

</script>

<template>
  <div class="container">
    <div class="row d-flex align-items-center">
      <div class="col col-md-4 d-flex align-items-center">
        <img src="../assets/image/LogoPetHaven.png" class="img-fluid rounded-top me-2" alt="">
        <div>
          <p class="m-0 text-logo">Pet Haven</p>
          <p class="m-0 text-logo">Pets are best friends.</p>
        </div>
      </div>
      <div class="col col-md-auto d-flex">
        <nav class="navbar bg-body-tertiary flex-grow-1">
          <form class="d-flex" role="search">
            <input class="noboder" type="search" style="width: 500px ; max-height: 35px" placeholder="Tìm kiếm..." >
            <button class="noboder " type="submit" >Search</button>

          </form>
        </nav>
        <button  type="button" class="custom-button ">Login</button>
        <button type="button" class="custom-button ">VN</button>
      </div>
    </div>
    <!--  -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#-->
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
      <div class="container-fluid">
        <NuxtLink class="nav-link active" aria-current="page" style="font-size: " to="/">Nhà Pet Haven</NuxtLink>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbar">
          <ul class="navbar-nav">
            <li class="nav-item" v-for="service in services" :key="service.id">
              <div v-if="services.length===0">
                Dịch vụ không khả dụng vui lòng thử lại sau !
              </div>
              <div v-else>
                <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page"><div style="font-size: medium ; ">{{ service.tendichvu || 'Dịch vụ không khả dụng' }}</div></NuxtLink>
              </div>


            </li>
          </ul>
        </div>
      </div>
    </nav>
  </div>
  <nuxt-page/>
  <footer>
    <div class="container">
      <div class="row">
        <div class="col">
          <div class="row">
            <div class="col">
              <img src="../assets/image/LogoPetHaven.png" class="img-fluid rounded-top me-2" alt="">
            </div>
            <div class="col">
              <h3>Pet Haven</h3>
              <p>Pets are best friends</p>
              <a name="" id=""  href="https://facebook.com/nongtiensonpro" role="button"><img src="../assets/image/logo-fb.png" width="32" height="32" class="me-3" alt="Facebook"></a>
              <a name="" id=""  href="https://facebook.com/nongtiensonpro" role="button"><img src="../assets/image/logo-youtobe.png" width="32" height="32" class="me-3" alt="Youtobe"></a>
              <a name="" id=""  href="https://facebook.com/nongtiensonpro" role="button"><img src="../assets/image/logo-tw.png" width="32" height="32" class="me-3" alt="X"></a>
            </div>
            <div>
              <h3>Giới thiệu</h3>
              <p>Pet Haven là đơn vị tư vấn và giúp bạn tìm kiếm và đăng ký nhà đã có dịch vụ chăm sóc cho thú cưng <a  id=""  href="#" role="button">Xem thêm</a></p>

            </div>
          </div>

        </div>
        <div class="col">
          <h3>Dịch vụ</h3>
          <ul>
            <div v-if="services.length===0">Dịch vụ không khả dụng vui lòng thử lại sau !</div>
            <div v-else>
              <li class="nav-item" v-for="service in services" :key="service.id">
              <NuxtLink class="nav-link" :to="`/services/${service.id}`" aria-current="page">{{ service.tendichvu || 'Dịch vụ không khả dụng' }}</NuxtLink>
              <div v-if="!service.tendichvu">Dịch vụ không khả dụng</div>
            </li>
            </div>
          </ul>
        </div>
        <div class="col">
          <div class="row">
            <div class="col">
              <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3413.541940628307!2d105.74466886584491!3d21.038129618650437!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313455e940879933%3A0xcf10b34e9f1a03df!2zVHLGsOG7nW5nIENhbyDEkeG6s25nIEZQVCBQb2x5dGVjaG5pYw!5e1!3m2!1svi!2s!4v1727962840908!5m2!1svi!2s" width="200" height="200" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
            </div>
            <div class="col">
              <h3>Liên hệ</h3>
              <p>0987654321</p>
              <p>Email:  pet-haven@example.com</p>
              <p>Facebook: pet-haven-facebook</p>
            </div>
          </div>

        </div>
      </div>
      <div class="col text-center">
        <h4>© All Copyright 2024 by Pet Haven</h4>
      </div>
    </div>
  </footer>
</template>

<style scoped>

.navbar {
  background: #F8F8F1 !important;
  border-radius: 25px;
  border: 0.6px solid #400D01;

}
.noboder{
  border: none;
  outline: none;
  cursor: pointer;
  background: none;
}
.text-logo{
  color: #400D01;
  font-family: Pacifico;
  font-size: 40px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
.custom-button {
  background-color: transparent; /* Màu nền */
  border: 0.6px solid #400D01;
  color: #400D01; /* Màu chữ */
  padding: 15px 32px; /* Khoảng cách bên trong */
  text-align: center; /* Căn giữa chữ */
  text-decoration: none; /* Bỏ gạch chân */
  display: inline-block; /* Hiển thị theo dòng */
  font-size: 16px; /* Kích thước chữ */
  margin: 4px 2px; /* Khoảng cách ngoài */
  cursor: pointer; /* Con trỏ chuột */
  border-radius: 12px; /* Bo tròn góc */
}
</style>


<script lang="ts">
import { useServiceStore } from '~/stores/DichVuStores';
import { onMounted, computed } from 'vue';
import DichVu from '~/models/DichVu';

export default {
  setup() {
    const serviceStore = useServiceStore();

    onMounted(() => {
      serviceStore.fetchServices();
    });

    const services = computed((): DichVu[] => serviceStore.services);

    return {
      services,
    };
  },
};
</script>