<template>
  <el-header class="register-logo header-left">
    <img src="../images/logo.png" alt="Logo" class="logo-img" @click="handleHome" width="80" height="80"/>
    <span class="header-title">{{ setTitle(title) }}</span>
  </el-header>
</template>

<script setup>
import {useRouter} from 'vue-router'
import {defineExpose, ref} from 'vue';

const title = ref('Ocean Wave')

function setTitle(Atitle) {
  title.value = Atitle;
  return title.value;
}

defineExpose({setTitle})

const router = useRouter();

function handleHome() {
  router.push('/')
}
</script>

<style lang="less" scoped>
.register-logo {
  displaY: flex;
  text-align: left;
  align-items: center;
  height: 120px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 999;
  padding-left: 30px;
  background-color: rgba(255, 255, 255, 0.3); //白色背景
}

.logo-img {
  cursor: pointer;
}

.header-title {
  margin-left: 15px;
  font-size: 28px;
  color: #668B8B;
  font-weight: bold;
  line-height: 1;
}
</style>
