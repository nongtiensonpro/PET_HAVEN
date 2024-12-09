<template>
  <div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <button type="button" @click="lamMoi()" class="btn btn-sm btn-success">Làm mới bảng</button>
      <div class="d-flex">
        <input v-model="searchTerm" type="text" class="form-control me-2" placeholder="Tìm kiếm...">
        <button @click="search" class="btn btn-primary">Tìm kiếm</button>
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
    <div class="d-flex justify-content-between align-items-center mt-3">
      <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-sm btn-secondary">Trước</button>
      <span>Trang {{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-sm btn-secondary">Sau</button>
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
      user.hoten.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      user.role.toLowerCase().includes(searchTerm.value.toLowerCase())
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

// Add a watch for searchTerm to trigger search on input change
watch(searchTerm, () => {
  search();
});
</script>

<style scoped>
.table {
  width: 100%;
  margin-top: 20px;
}
</style>