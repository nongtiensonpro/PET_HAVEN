<script setup lang="ts">
const modalTitle = "Thông tin về tuổi thú cưng"

const petAgeInfo = {
  dog: {
    title: "Tuổi của chó",
    humanEquivalent: "1 năm tuổi của chó tương đương với khoảng 6-8 năm tuổi của người.",
    development: "Chó phát triển nhanh trong 1-2 năm đầu tiên, đạt đến 75% kích thước tối đa.",
    lifespan: "10-13 năm, tùy thuộc giống và điều kiện sống.",
    stages: [
      { name: "Cún con", age: "0-1 năm" },
      { name: "Thanh thiếu niên", age: "1-2 năm" },
      { name: "Người lớn", age: "2-5 năm" },
      { name: "Người lớn tuổi", age: "5-10 năm" },
      { name: "Già", age: "trên 10 năm" }
    ],
    agingSigns: "Rụng lông, giảm khả năng nghe, nhìn, giảm hoạt động.",
    ageCalculation: "(Tuổi của chó x 4) + 20"
  },
  cat: {
    title: "Tuổi của mèo",
    humanEquivalent: "1 năm tuổi của mèo tương đương với khoảng 4-6 năm tuổi của người.",
    development: "Mèo phát triển nhanh trong 6 tháng đầu tiên, đạt đến 75% kích thước tối đa.",
    lifespan: "12-17 năm, tùy thuộc giống và điều kiện sống.",
    stages: [
      { name: "Mèo con", age: "0-6 tháng" },
      { name: "Thanh thiếu niên", age: "6 tháng-2 năm" },
      { name: "Người lớn", age: "2-5 năm" },
      { name: "Người lớn tuổi", age: "5-10 năm" },
      { name: "Già", age: "trên 10 năm" }
    ],
    agingSigns: "Rụng lông, giảm khả năng nhảy, giảm hoạt động, thay đổi hành vi.",
    ageCalculation: "(Tuổi của mèo x 3) + 20"
  }
}

const lifespanFactors = [
  "Giống: Một số giống chó và mèo có tuổi thọ ngắn hơn hoặc dài hơn.",
  "Chế độ ăn uống: Dinh dưỡng cân đối và phù hợp.",
  "Tập thể dục: Hoạt động thường xuyên giúp duy trì sức khỏe.",
  "Sức khỏe: Các bệnh lý và tình trạng sức khỏe ảnh hưởng đến tuổi thọ.",
  "Chăm sóc: Sự quan tâm và chăm sóc từ chủ sở hữu."
]
</script>

<template>
  <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#petAgeModal">
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-info-circle" viewBox="0 0 16 16">
      <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
      <path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533zM9 4.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
    </svg>
  </button>

  <div class="modal fade" id="petAgeModal" tabindex="-1" aria-labelledby="petAgeModalLabel">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="petAgeModalLabel">{{ modalTitle }}</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div v-for="(pet, petType) in petAgeInfo" :key="petType" class="mb-4">
            <h2 class="h4">{{ pet.title }}</h2>
            <p><strong>Tuổi tương đương với người:</strong> {{ pet.humanEquivalent }}</p>
            <p><strong>Giai đoạn phát triển:</strong> {{ pet.development }}</p>
            <p><strong>Tuổi thọ trung bình:</strong> {{ pet.lifespan }}</p>

            <h3 class="h5 mt-3">Các giai đoạn tuổi:</h3>
            <ul>
              <li v-for="stage in pet.stages" :key="stage.name">
                <strong>{{ stage.name }}:</strong> {{ stage.age }}
              </li>
            </ul>

            <p><strong>Các dấu hiệu lão hóa:</strong> {{ pet.agingSigns }}</p>
            <p><strong>Cách tính tuổi tương đương với người:</strong> {{ pet.ageCalculation }}</p>
          </div>

          <h2 class="h4 mt-4">Các yếu tố ảnh hưởng đến tuổi thọ</h2>
          <ul>
            <li v-for="(factor, index) in lifespanFactors" :key="index">{{ factor }}</li>
          </ul>
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