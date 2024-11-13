<script setup lang="ts">
import {useUserStore} from '~/stores/user';
import {computed} from "vue";

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);
definePageMeta({
  middleware: 'auth',
});

function login1() {
  if (process.client) {
    window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak';
  }
}

if (userInfo.value === null) {
  login1();
}
function createPayment() {
  fetch('http://localhost:8080/payment/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
      .then(response => response.json())
      .then(data => {
        if (data) {
          // Redirect người dùng đến URL thanh toán của PayPal
          window.location.href = data; // `data` chứa URL `approval_url` từ API
        } else {
          console.error("Không thể tạo thanh toán");
        }
      })
      .catch(error => {
        console.error("Lỗi:", error);
      });
}

</script>

<template>
  <div class="container p-2">
    <div class="card">
      <div class="card-body">
        <div class="form-group">
          <div class="row">
            <div class="col-4">
              <img class="card-img-top" src="../../assets/image/cat3.jpg" alt="">
            </div>
            <div class="col-8">
              <label for="">Tên người dùng</label>
              <input type="text"
                     class="form-control" name="" id="name" aria-describedby="helpId" placeholder="" v-model="userInfo.name">
              <div v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin')">
                <label for="">Roles :</label>
               Quản trị viên
              </div>
              <button @click="createPayment"
              >Thanh toán</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>