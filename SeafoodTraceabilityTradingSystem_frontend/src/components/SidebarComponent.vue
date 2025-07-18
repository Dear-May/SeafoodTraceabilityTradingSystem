<template>
  <div class="sidebar">
    <el-menu :default-active="defaultActive">
      <el-menu-item index="1" @click="handleMyHaichao">我的海潮</el-menu-item>
      <el-menu-item index="2" @click="handleCart">我的购物车</el-menu-item>
      <el-menu-item index="3" @click="handleOrder">我的订单</el-menu-item>
      <el-menu-item index="7" @click="handleFavorite">宝贝收藏</el-menu-item>
      <el-menu-item index="8">关注店铺</el-menu-item>
      <el-menu-item index="9" @click="handleFootMark">购物足迹</el-menu-item>
      <el-menu-item index="10" @click="handleComment">评价管理</el-menu-item>
      <el-menu-item index="12" @click="handleEditProfile">账户设置</el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import {defineExpose, ref} from 'vue'
import router from "@/router";

const defaultActive = ref('1')

function setActive(active) {
  defaultActive.value = active
}

function handleMyHaichao() {
  router.push('/mySpace');
}

function handleCart() {
  router.push('/mySpace/BucketList');
}

function handleOrder() {
  router.push('/mySpace/myOrder');
}

function handleFavorite() {
  router.push('/mySpace/myFavorite')
}

function handleFootMark() {
  router.push('/mySpace/footMark');
}

function handleComment() {
  router.push('/profile/myComment');
}

function handleEditProfile() {
  router.push('/profile/account_information');
}

defineExpose({setActive})
</script>

<style scoped lang="less">
.sidebar {
  width: 200px;
  padding: 20px;
  margin-top: 90px;
  background-color: #ffffff;
  border-right: 1px solid #eaeaea;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.el-menu-item {
  font-size: 16px;
}

.is-active {
  color: #e02020;
  box-shadow: 3px 0 8px rgba(0, 0, 0, 0.1);
}
</style>