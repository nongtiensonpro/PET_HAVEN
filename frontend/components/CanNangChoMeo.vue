<template>
  <!-- Button trigger modal -->
  <button type="button" class="nav-link" data-bs-toggle="modal" data-bs-target="#exampleModalCanNangNe">
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-info-circle" viewBox="0 0 16 16">
      <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
      <path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533zM9 4.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
    </svg>
  </button>

  <!-- Modal -->
  <div class="modal fade" id="exampleModalCanNangNe" tabindex="-1" aria-labelledby="exampleModalLabelCanNangNe" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabelCanNangNe">Thông tin thú vị về cân nặng</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <h5>Cân nặng của chó:</h5>
          <ul>
            <li>Trung bình: 10-30 kg</li>
            <li>Giống nhỏ nhất: Chihuahua (1-4 kg)</li>
            <li>Giống lớn nhất: Irish Wolfhound (50-90 kg)</li>
          </ul>
          <h5>Cân nặng của mèo:</h5>
          <ul>
            <li>Trung bình: 4-6 kg</li>
            <li>Giống nhỏ nhất: Singapura (1,5-3 kg)</li>
            <li>Giống lớn nhất: Maine Coon (10-15 kg)</li>
          </ul>
          <h5>Lưu ý:</h5>
          <ul>
            <li>Cân nặng khác nhau tùy thuộc giống và cá thể.</li>
            <li>Kiểm tra cân nặng thường xuyên để đảm bảo sức khỏe.</li>
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
.modal-body ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.modal-body li {
  margin-bottom: 10px;
}

.modal-body h5 {
  margin-top: 20px;
}
</style>