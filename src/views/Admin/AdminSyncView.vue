<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <aside class="bg-dark text-light" style="width: 200px; padding-top: 20px;">
      <div class="text-center mb-4">
        <a style="font-size: 16px; font-weight: bold">后台管理</a>
      </div>
      <el-menu
          :default-openeds="['1']"
          :default-active="'1-2'"
          class="border-0"
          text-color="#fff"
          active-text-color="#ffd04b"
          background-color="#333"
      >
        <el-sub-menu index="1">
          <template #title>首页</template>
          <el-menu-item index="1-1" @click="router.push('/admin/index?id='+id)">首页</el-menu-item>
          <el-menu-item index="1-2">搜索引擎商品数据同步</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <nav class="navbar navbar-light bg-white border-bottom shadow-sm">
        <div class="container-fluid">
          <span class="navbar-brand mb-0 h1">数据同步</span>
        </div>
      </nav>

      <div class="p-4">
        <div class="text-center mb-4">
          <h2>搜索引擎商品数据同步</h2>
          <el-button type="primary" @click="syncData"> 同步数据</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import router from "@/router";
import {onMounted} from "vue";
import {ElMessage} from "element-plus";
import axios from "axios";

let id = null;

async function checkUserPermission() {
  id = new URLSearchParams(window.location.search).get('id');
  if (!id) {
    ElMessage.error('权限校验失败');
    await router.replace('/')
  } else {
    try {
      const response = await axios.post('/api/admin/checkUserPermission', {
        id: id,
      }, {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      });
      if (response.data.code !== 200) {
        ElMessage.error('无权限访问')
        await router.replace('/')
      }
    } catch (error) {
      ElMessage.error('获取权限失败')
      await router.replace('/')
    }
  }
}

async function syncData() {
  try {
    const response = await axios.post('/api/admin/syncGoodData', {
      token: id,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('同步成功')
    } else {
      ElMessage.error('同步失败1')
    }
  } catch (error) {
    ElMessage.error('同步失败')
  }
}

onMounted(async () => {
  await checkUserPermission();
});
</script>

<style scoped lang="less">

</style>