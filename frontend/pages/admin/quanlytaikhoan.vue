<template>
  <div class="container">
    <div>
      <button type="button" @click="lamMoi()" class="btn btn-sm btn-success m-4">Làm mới bảng</button>
    </div>
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Tên người dùng</th>
        <th>Giới tính</th>
        <th>Email</th>
        <th>Vai trò</th>
        <th>Thao tác</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in User" :key="user.id">
        <td>{{ user.idtaikhoan }}</td>
        <td>{{ user.hoten }}</td>
        <td>{{ user.gioitinh }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.role }}</td>
        <td>
              <button class="btn btn-sm btn-outline-warning m-1" @click="editUser(user.ID)">Edit</button>
              <button class="btn btn-sm btn-outline-danger m-1" @click="deleteUser(user.ID)">Chặn</button>
              <button class="btn btn-sm btn-outline-danger m-1">Chi tiết</button>
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
  User.value = fetchedUsers.valueOf();
};

onMounted(() => {
  loadUsers();
});

function lamMoi() {
  loadUsers();
}

const editUser = (id: string) => {
  toast.success('Chức năng đang phát triển', {});
};

const deleteUser = (id: string) => {
  toast.success('Chức năng đang phát triển', {});
};
</script>

<style scoped>

</style>