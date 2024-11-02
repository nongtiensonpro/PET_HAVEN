<script setup lang="ts">
import Calendar from '~/components/Calendar.vue'
import {useServiceStore} from '~/stores/DichVuStores';
import {computed} from "vue";
import DichVu from "~/models/DichVu";

const serviceStore = useServiceStore();

const services = computed((): DichVu[] => {
  return serviceStore.services.filter((service: DichVu) => service.trangthai);
});

</script>

<template>
  <div class="container p-4">
    <div class="row">
      <h2>Nhà Haven \ Đặt lịch</h2>
      <div class="col-12">
        <div class="row">
          <div class="col-6">
            <div class="card">
              <img class="card-img-top" src="~/assets/image/cat3.jpg" alt="">
              <div class="card-body">
                <div class="row">
                  <div class="col-6">
                    <label for="">Chủ cần :</label>
                    <select class="form-control" name="" id="">
                      <option v-for="service in services" :key="service.id" v-if="services.length > 0">
                        {{ service.tendichvu }}
                      </option>
                    </select>
                  </div>
                  <div class="col-6">
                    <div class="form-group">
                      <label for="">Khi nào :</label>
                      <select class="form-control" name="" id="">
                        <option>Meo 1</option>
                        <option>Meo 2</option>
                        <option>Meo 3</option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-6">
            <Calendar/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>