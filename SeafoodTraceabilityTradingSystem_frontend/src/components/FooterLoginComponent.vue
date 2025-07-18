<template>
  <el-footer class="border-top">
    <p class="col-md-4 mb-0 text-body-secondary" style="text-align: left; padding: 20px 0;">&copy; 2024&nbsp;-&nbsp;现在&nbsp;&nbsp;&nbsp;&nbsp;海潮&nbsp;&nbsp;海鲜食品溯源交易平台,
      Inc</p>
    <a href="/"
       class="col-md-4 d-flex align-items-center justify-content-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none"
       style="padding-top: 15px;">
      <img class="bi me-2" width="40" height="40" src="../images/logo.png" alt="Logo" style="margin-top: 10px;">
    </a>
    <ul class="nav col-md-4 justify-content-end" style="padding: 10px 0;">
      <li class="nav-item"><a @click="handleHome" class="nav-link px-2 text-body-secondary">主页</a></li>
      <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">Features</a></li>
      <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">Pricing</a></li>
      <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">FAQs</a></li>
      <li class="nav-item"><a href="/about" class="nav-link px-2 text-body-secondary">关于我们</a></li>
    </ul>
  </el-footer>
</template>

<script setup>
import router from "@/router";

function handleHome() {
  router.push('/index');
}
</script>

<style scoped lang="less">
.el-footer {
  display: flex;
  height: 4vh;
  width: 100%;
}
</style>