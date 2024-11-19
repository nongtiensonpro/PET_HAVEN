<template>
  <div class="container">
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Tên người dùng</th>
        <th>Tên</th>
        <th>Họ</th>
        <th>Email</th>
        <th>Xác nhận Email</th>
        <th>Thời gian tạo</th>
        <th>Trạng thái</th>
        <th>Vai trò</th>
        <th>Thao tác</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in User" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>{{ user.firstName }}</td>
        <td>{{ user.lastName }}</td>
        <td>{{ user.email }}</td>
        <td>{{  user.emailVerified==true?'Kích hoạt':'Chưa kích hoạt' }}</td>
        <td>{{ new Date(user.createdTimestamp).toLocaleString() }}</td>
        <td>{{  user.enabled==true?'Hoạt động':'Không hoạt động' }}</td>
        <td> ? </td>
        <td>
              <button class="btn btn-sm btn-outline-warning m-1" @click="editUser(user.id)">Edit</button>
              <button class="btn btn-sm btn-outline-danger" @click="deleteUser(user.id)">Delete</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '~/stores/UserStores';
import { onMounted, ref } from "vue";
import UserModel from '~/models/User';
import {useToast} from 'vue-toastification'
const User = ref<UserModel[]>([]);
const toast = useToast();
const userStore = useStore();

const loadUsers = async () => {
  const fetchedUsers = await userStore.fetchUsers();
  User.value = fetchedUsers.map(user => new UserModel(
    user.id,
    user.username,
    user.firstName,
    user.lastName,
    user.email,
    user.emailVerified,
    user.createdTimestamp,
    user.enabled,
    user.access
  ));
};

onMounted(() => {
  loadUsers();
});



const editUser = (id: string) => {
  toast.success('Chức năng đang phát triển', {});
};

const deleteUser = (id: string) => {
  toast.success('Chức năng đang phát triển', {});
};
</script>

<style scoped>

</style>