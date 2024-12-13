<script setup lang="ts">
const modalTitle = "Hướng dẫn đặt tên thú cưng"
const guidelines = [
  "Cá tính hóa: Đặt tên dựa trên tính cách, ngoại hình hoặc hành vi đặc trưng của thú cưng.",
  "Sự sáng tạo: Kết hợp các từ, sử dụng ngôn ngữ khác nhau hoặc tạo ra từ mới.",
  "Cá nhân hóa: Đặt tên dựa trên sở thích, bộ phim, sách hoặc nhân vật yêu thích.",
  "Đơn giản và dễ nhớ: Chọn tên ngắn gọn, dễ phát âm và nhớ."
]
const petNames = {
  dog: {
    common: ["Max", "Luna", "Charlie", "Bella", "Rocky"],
    themed: ["Coco (chocolate)", "Mocha (cà phê)", "Sữa (trắng)"],
    unique: ["Aurora", "Loki", "Zorro", "Nala", "Orion"]
  },
  cat: {
    common: ["Whiskas", "Muffin", "Kitty", "Lion", "Ginger"],
    themed: ["Cappuccino", "Trà Xanh", "Đá Quý"],
    unique: ["Lyra", "Felix", "Cleo", "Zephyr", "Onyx"]
  }
}
</script>

<template>
  <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#petNameModal">
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-info-circle" viewBox="0 0 16 16">
      <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
      <path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533zM9 4.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
    </svg>
  </button>

  <div class="modal fade" id="petNameModal" tabindex="-1" aria-labelledby="petNameModalLabel">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="petNameModalLabel">{{ modalTitle }}</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <h2 class="h5 mb-3">Hướng dẫn</h2>
          <ul class="list-group mb-4">
            <li v-for="(guideline, index) in guidelines" :key="index" class="list-group-item">
              {{ guideline }}
            </li>
          </ul>

          <h2 class="h5 mb-3">Gợi ý tên thú cưng</h2>
          <div class="row">
            <div class="col-md-6 mb-3">
              <h3 class="h6">Tên cho chó</h3>
              <ul class="list-unstyled">
                <li><strong>Phổ biến:</strong> {{ petNames.dog.common.join(', ') }}</li>
                <li><strong>Theo chủ đề:</strong> {{ petNames.dog.themed.join(', ') }}</li>
                <li><strong>Độc đáo:</strong> {{ petNames.dog.unique.join(', ') }}</li>
              </ul>
            </div>
            <div class="col-md-6 mb-3">
              <h3 class="h6">Tên cho mèo</h3>
              <ul class="list-unstyled">
                <li><strong>Phổ biến:</strong> {{ petNames.cat.common.join(', ') }}</li>
                <li><strong>Theo chủ đề:</strong> {{ petNames.cat.themed.join(', ') }}</li>
                <li><strong>Độc đáo:</strong> {{ petNames.cat.unique.join(', ') }}</li>
              </ul>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-body {
  max-height: 70vh;
  overflow-y: auto;
}
</style>