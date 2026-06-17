<template>
  <el-button circle @click="redirectToGiteeAuth()">
    <img src="../assets/images/mayun.png" alt="gitee登录" style="width: 24px; height: 24px;"/>
  </el-button>
</template>

<script setup>
import request from "@/api/request";

async function redirectToGiteeAuth() {
  try {
    const response = await request.post('/api/callback/gitee-auth', {}, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    if (response.data && response.data.url) {
      window.location.href = response.data.url;
    }
  } catch (error) {
    console.error('Error during redirect:', error.message);
  }
}
</script>