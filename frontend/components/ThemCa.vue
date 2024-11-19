<script setup lang="ts">
import { ref } from "vue";
import { useCaLichHenStore } from "~/stores/QuanLyCaLichHen";
import  CaLichHen from "~/models/CaHen";

const caLichHenStore = useCaLichHenStore();
const tenca = ref("");
const thoigianca = ref("");
const trangthai = ref(false);

const themCaHen = async () => {
    
    try {
        const [hours, minutes] = thoigianca.value.split(':');
        const date = new Date();
        date.setHours(parseInt(hours), parseInt(minutes), 0, 0);

        const newCaHen = new CaLichHen(0, tenca.value, date, trangthai.value);
        await caLichHenStore.themCaLichHen(newCaHen);
        tenca.value = "";
        thoigianca.value = "";
        trangthai.value = false;
    } catch (e) {
        
    }
}
</script>

<template>
    <div class="container">
        <!-- Button trigger modal -->
        <button type="button" class="custom-button" data-bs-toggle="modal" data-bs-target="#exampleModal">
            Thêm Ca Hẹn
        </button>

        <!-- Modal -->
        <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form @submit.prevent="themCaHen" class="form-container">
                            <div class="form-group">
                                <label for="tenca">Tên ca:</label>
                                <input id="tenca" v-model="tenca" type="text" required class="form-input" />
                            </div>
                            <div class="form-group">
                                <label for="thoigianca">Thời gian ca:</label>
                                <input id="thoigianca" v-model="thoigianca" type="time" required class="form-input" />
                            </div>
                            <div class="form-group">
                                <label for="trangthai">Trạng thái:</label>
                                <input id="trangthai" type="checkbox" v-model="trangthai" class="form-checkbox" />
                            </div>
                            <button type="submit" class="form-button">Thêm</button>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.form-container {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
    margin-bottom: 15px;
}

.form-input,
.form-checkbox {
    width: 100%;
    padding: 10px;
    margin-top: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.form-button {
    width: 100%;
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.form-button:hover {
    background-color: #45a049;
}
</style>