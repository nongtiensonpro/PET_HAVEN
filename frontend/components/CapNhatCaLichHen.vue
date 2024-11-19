<script setup lang="ts">
import type CaLichHen from '~/models/CaHen'

const props = defineProps<{
  ca: CaLichHen
}>()

const emit = defineEmits(['cap-nhat'])

// Logic cập nhật ở đây
</script>

<template>
  <button type="button" class="btn btn-sm btn-outline-warning m-1" data-bs-toggle="modal" data-bs-target="#exampleModal">
    Cập nhật Ne
  </button>

  <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
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