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
          <button class="btn btn-sm btn-outline-danger m-1" @click="chitietUser(user.idtaikhoan)">Chi tiết</button>
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
import { onMounted, ref, computed } from "vue";
import UserModel from '~/models/User';
import { useToast } from 'vue-toastification'

const User = ref<UserModel[]>([]);
const toast = useToast();
const userStore = useStore();

// Pagination
const currentPage = ref(1);
const itemsPerPage = 5;

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return User.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(User.value.length / itemsPerPage);
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
  } catch (error) {
    toast.error('Làm mới tài khoản thất bại')
  }
};

onMounted(() => {
  loadUsers();
});

function lamMoi() {
  loadUsers();
  currentPage.value = 1; // Reset to first page after refreshing
}

const editUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
};

const deleteUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
};

const chitietUser = (id: string) => {
  toast.success('Chức năng đang phát triển : ' + id, {});
}
</script>

<style scoped>

</style>