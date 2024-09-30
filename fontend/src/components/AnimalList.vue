<template>
  <div>
    <h1>Danh sách động vật</h1>
<!--    <ul v-if="animals.length">-->
<!--      <li v-for="animal in animals" :key="animal.id">-->
<!--        <h3>{{ animal.name }} ({{ animal.type }})</h3>-->
<!--        <p><strong>Giống loài:</strong> {{ animal.breed || 'Chưa xác định' }}</p>-->
<!--        <p><strong>Tuổi:</strong> {{ animal.age }} năm</p>-->
<!--      </li>-->
<!--    </ul>-->


    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Type</th>
        <th>Age</th>
        <th>Breed</th>
        <th>OwnerID</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="al in animals">
        <td>{{al.id}}</td>
        <td>{{al.name}}</td>
        <td>{{al.type}}</td>
        <td>{{al.age}}</td>
        <td>{{al.breed}}</td>
        <td>{{al.ownerId}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Animal } from '../models/Animal'; // Import model Animal
import { AnimalService } from '../services/AnimalService'; // Import service

// Tạo biến animals với kiểu dữ liệu là Animal[]
const animals = ref<Animal[]>([]);

// Gọi API khi component được mounted
onMounted(async () => {
  try {
    const response = await AnimalService.getAll();
    animals.value = response;  // Gán dữ liệu vào biến animals
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu từ API:', error);
  }
});
</script>

