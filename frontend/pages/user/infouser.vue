<script setup lang="ts">
import {useUserStore} from '~/stores/user';
import {computed} from "vue";
import type {ThuCung} from "~/models/ChiTietDanhGia";
import {useI18n} from 'vue-i18n';

const {t} = useI18n();


const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);
definePageMeta({
  middleware: ['auth']
})

function login1() {
  if (process.client) {
    window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak';
  }
}

if (userInfo.value === null) {
  login1();
}
const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem('refresh_token');
  if (!refreshToken) {
    console.error('No refresh token available');
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
      console.error(`Error refreshing token: ${errorData.error_description}`);
    }

    const data = await response.json();
    localStorage.setItem('access_token', data.access_token);
    localStorage.setItem('refresh_token', data.refresh_token);
    console.log('New access token:', data.access_token);
    return data.access_token;
  } catch (error) {
    localStorage.setItem('viewRole', '0');
    console.error('Error refreshing token:', error);
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
      console.log('Access token expired, refreshing token...');
      const newAccessToken = await refreshAccessToken();
      if (newAccessToken) {
        return fetchUserInfo(newAccessToken);
      } else {
        console.error('Unable to refresh access token');
        userStore.setLoggedIn(false);
        return;
      }
    }

    if (!response.ok) {
      const errorData = await response.json();
      console.error(`Error fetching user info: ${errorData.error_description}`);
      return;
    }

    const userInfoData = await response.json();
    console.log('User Info:', userInfoData);

    const roles = userInfoData.roles || [];
    userStore.setUserInfo({
      name: userInfoData.username || '',
      role: roles || '',
      listThuCung: userInfoData.listThuCung || ''
    });

    userStore.setLoggedIn(true);

    if (userInfoData.username) {
      console.log('User info refreshed successfully');
    }
  } catch (error) {
    localStorage.setItem('viewRole', '0');
    console.error('Error fetching user info:', error);
    userStore.setLoggedIn(false);
  }
};

function restUser() {
  const accessToken = localStorage.getItem('access_token');
  if (!accessToken) {
    console.error('No access token available');
    return;
  }else {
    fetchUserInfo(accessToken);
    return navigateTo('/user/infouser');
  }
}

onMounted(() => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken) {
    fetchUserInfo(accessToken);
  }
})

function themThuCung() {
  return navigateTo('/user/themthucung');
}

function capNhatThuCung(thuCung : ThuCung) {
  console.log(thuCung);
  return navigateTo(`/user/capnhatthucung/${thuCung.id}`);
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
              <div
                  v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin') || userInfo.role.includes('manager')">
                <div class="p-4 text fs-4">
                  <div v-if="userInfo.role.includes('admin')">
                    {{t('role_admin_info')}}
                  </div>
                  <div v-else-if="userInfo.role.includes('manager')">
                   {{t('role_user_evo')}}
                  </div>
                  <div v-else>
                    {{t('role_user_info')}}
                  </div>
                </div>
              </div>
              <label for="">{{t('userName')}}</label>
              <input type="text" class="form-control" name="" id="name" aria-describedby="helpId" placeholder=""
                     v-model="userInfo.name">
            </div>
          </div>
          <div class="container p-4">
            <div class="row">
              <div class="col-2">
                <button type="button" class="custom-button" @click="themThuCung">{{t('add_pet')}}</button>
              </div>
            </div>
            <div v-if="userInfo?.listThuCung == 0">
             {{t('list_pet_null')}}
            </div>
            <div v-else>
              <div class="text fs-4">
               <div class="row">
                 <div class="col">
                   {{t('list_pet')}} :
                 </div>
                 <div class="col">
                   <button type="button" class="btn btn-sm btn-warning m-1" @click="restUser">{{t('refresh')}}</button>
                 </div>
               </div>
              </div>
              <div v-if="userInfo.listThuCung.length > 0" v-for="thuCung in userInfo?.listThuCung"
                   :key="userInfo?.listThuCung.id" class="card m-4">
                <div class="card-body">
                  <table class="table">
                    <thead>
                    <tr>
                      <th>{{t('name')}}</th>
                      <th>{{t('weight')}}</th>
                      <th>{{t('breed')}}</th>
                      <th>{{t('gender')}}</th>
                      <th>{{t('petType')}}</th>
                      <th>{{t('age')}}</th>
                      <th>{{t('actions')}}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                      <td>{{ thuCung.ten }}</td>
                      <td>{{ thuCung.cannang }}</td>
                      <td>{{ thuCung.giong }}</td>
                      <td>{{thuCung.gioitinh?t('female'):t('male')}}</td>
                      <td>{{thuCung.cophaimeokhong?t('cat'):t('dog')}}</td>
                      <td>{{ thuCung.tuoi }}</td>

                      <td>
                        <button type="button" class="btn btn-sm btn-warning m-1" @click="capNhatThuCung(thuCung)">{{t('updatePetTitle')}}</button>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>