<script setup lang="ts">

</script>

<template>
  <div class="container">
    <div class="row d-flex align-items-center">
      <div class="col-md-4 d-flex align-items-center">
        <img src="../assets/image/LogoPetHaven.png" class="img-fluid rounded-top me-2" alt="">
        <div>
          <p class="m-0 text-logo">Pet Haven</p>
          <p class="m-0 text-logo">Pets are best friends.</p>
        </div>
      </div>
      <div class="col-md-auto d-flex">
        <nav class="navbar bg-body-tertiary flex-grow-1">
          <form class="d-flex" role="search">
            <input class="noboder" type="search" placeholder="Search" aria-label="Tìm kiếm...">
            <button class="noboder" type="submit">Search</button>
          </form>
        </nav>
        <button  type="button" class="btn btn-primary m-2">Login</button>
        <button type="button" class="btn btn-primary m-2">VN</button>
      </div>
    </div>
    <!--  -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#-->
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">Navbar</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbar">
          <ul class="navbar-nav">
            <li class="nav-item">
<!--              <a class="nav-link active" aria-current="page" href="#">Home</a>-->
              <NuxtLink class="nav-link active" aria-current="page" to="/">Home</NuxtLink>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Features</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Pricing</a>
            </li>
            <li class="nav-item">
              <a class="nav-link disabled" aria-disabled="true">Disabled</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </div>


</template>

<style scoped>

.navbar {
  background-color: #343A40;
  border-radius: 25px;
  border: 0.6px solid #400D01;
}
.noboder{
  border: none;
  outline: none;
  cursor: pointer;
  background: none;
}
.text-logo{
  color: #400D01;
  font-family: Pacifico;
  font-size: 40px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
</style>