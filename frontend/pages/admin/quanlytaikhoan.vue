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
              <button class="btn btn-sm btn-outline-warning m-1" @click="editUser(user.idtaikhoan)">Edit</button>
              <button class="btn btn-sm btn-outline-danger m-1" @click="deleteUser(user.idtaikhoan)">Chặn</button>
              <button class="btn btn-sm btn-outline-danger m-1" @click="chitietUser(user.idtaikhoan)">Chi tiết</button>
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
  try {
    const fetchedUsers = await userStore.fetchUsers();
    User.value = fetchedUsers.valueOf();
  }catch (error) {
    toast.error('Làm mới tài khoản thất bại')
  }
};

onMounted(() => {
  loadUsers();
});

function lamMoi() {
  loadUsers();
}

const editUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
};

const deleteUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' +id, {});
};

const chitietUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
}
</script>

<style scoped>

</style>