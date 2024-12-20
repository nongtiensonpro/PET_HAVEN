<template>
  <div class="container bg-light p-4">
    <div class="text fs-1">
      Quản lý tài khoản
    </div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="row">
        <div class="col">
          <button type="button" @click="lamMoi()" class="custom-button">Làm mới bảng</button>
        </div>
        <div class="col">
          <input v-model="searchTerm" type="text" class="custom-button" placeholder="Tìm kiếm...">
        </div>
        <div class="col">
          <button @click="search" class="custom-button">Tìm kiếm</button>
        </div>
      </div>
    </div>
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Tên người dùng</th>
        <th>Email</th>
        <th>Vai trò</th>
        <th>Thao tác</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in paginatedUsers" :key="user.id">
        <td>{{ user.idtaikhoan }}</td>
        <td>{{ user.hoten }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.role }}</td>
        <td>
          <button class="btn btn-sm btn-outline-warning m-1" @click="editUser(user.idtaikhoan)">Edit</button>
          <button class="btn btn-sm btn-outline-danger m-1" @click="deleteUser(user.idtaikhoan)">Chặn</button>
          <button class="btn btn-sm btn-outline-info m-1" @click="chitietUser(user.idtaikhoan)">Chi tiết</button>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Pagination controls -->
    <div class="pagination d-flex justify-content-center align-items-center mt-3">
      <div class="row">
        <div class="col">
          <button @click="prevPage" :disabled="currentPage === 1" class="custom-button">Trước</button>
        </div>
        <div class="col">
          <span class="text fs-5">Trang {{ currentPage }} / {{ totalPages }}</span>
        </div>
        <div class="col">
          <button @click="nextPage" :disabled="currentPage === totalPages" class="custom-button">Sau</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useStore } from '~/stores/UserStores';
import { onMounted, ref, computed, watch } from "vue";
import UserModel from '~/models/User';
import { useToast } from 'vue-toastification'

const User = ref<UserModel[]>([]);
const filteredUsers = ref<UserModel[]>([]);
const toast = useToast();
const userStore = useStore();
const searchTerm = ref('');

// Pagination
const currentPage = ref(1);
const itemsPerPage = 5;

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredUsers.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredUsers.value.length / itemsPerPage);
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

const loadUsers = async () => {
  try {
    const fetchedUsers = await userStore.fetchUsers();
    User.value = fetchedUsers.valueOf();
    filteredUsers.value = User.value;
  } catch (error) {
    toast.error('Làm mới tài khoản thất bại')
  }
};

onMounted(() => {
  loadUsers();
});

function lamMoi() {
  loadUsers();
  currentPage.value = 1;
  searchTerm.value = '';
}

const search = () => {
  if (searchTerm.value) {
    filteredUsers.value = User.value.filter(user =>
        user.idtaikhoan.toString().includes(searchTerm.value) ||
        (user.hoten?.toLowerCase() || '').includes(searchTerm.value.toLowerCase()) ||
        (user.email?.toLowerCase() || '').includes(searchTerm.value.toLowerCase()) ||
        (user.role?.toLowerCase() || '').includes(searchTerm.value.toLowerCase())
    );
  } else {
    filteredUsers.value = User.value;
  }
  currentPage.value = 1;
};

const editUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
};

const deleteUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
};

const chitietUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
}

watch(searchTerm, () => {
  search();
});
</script>

<style scoped>

</style>