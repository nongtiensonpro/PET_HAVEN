<template>
  <div class="container card bg-light p-4" style="border-radius: 25px">
    <div class="text fs-1">
      Quản lý tài khoản
    </div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="row">
        <div class="col">
          <input v-model="searchTerm" type="text" class="custom-button" placeholder="Nhập tên tài khoản cần tìm kiếm ?">
        </div>
        <div class="col">
          <button @click="search" class="custom-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
              <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
            </svg>
          </button>
        </div>
        <div class="col">
          <button type="button" @click="lamMoi()" class="custom-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-clockwise" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
              <path d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
            </svg>
          </button>
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