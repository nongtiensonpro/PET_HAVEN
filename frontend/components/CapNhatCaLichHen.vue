<script setup lang="ts">
import type CaLichHens from '~/models/CaHen'

const props = defineProps<{
  ca: CaLichHens
}>()

const emit = defineEmits(['cap-nhat'])

// Logic cập nhật ở đây
</script>

<template>
  <button type="button" class="btn btn-sm btn-outline-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal123">
    Cập nhật Ne
  </button>

  <div class="modal fade" id="exampleModal123" tabindex="-1" aria-labelledby="exampleModal123" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          {{ca}}
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary">Save changes</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>