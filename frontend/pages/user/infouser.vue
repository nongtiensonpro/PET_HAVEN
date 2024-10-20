<script setup lang="ts">
import {useUserStore} from '~/stores/user';
import {computed} from "vue";

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);
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
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>