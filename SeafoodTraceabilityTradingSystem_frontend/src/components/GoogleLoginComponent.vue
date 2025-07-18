<template>
  <el-button circle @click="redirectToGiteeAuth()">
    <img src="../images/google.png" alt="google登录" style="width: 24px; height: 24px;"/>
  </el-button>
</template>

<script setup>
import axios from 'axios';

async function redirectToGiteeAuth() {
  try {
    const response = await axios.post('/api/callback/google-auth', {}, {
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