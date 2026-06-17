<template>
  <div class="social-login-btn" @click="handleOAuthLogin">
    <img :src="iconSrc" :alt="platform" class="social-icon" />
    <span>{{ displayName }}登录</span>
  </div>
</template>

<script setup>
import request from "@/api/request";
import { ElMessage } from 'element-plus';

const props = defineProps({
  platform: { type: String, required: true },
  displayName: { type: String, required: true },
  iconSrc: { type: String, required: true }
});

const apiEndpoints = {
  gitee: '/api/callback/gitee-auth',
  github: '/api/callback/github-auth',
  google: '/api/callback/google-auth'
};

async function handleOAuthLogin() {
  try {
    const response = await request.post(apiEndpoints[props.platform], {}, {
      headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' }
    });
    if (response.data && response.data.url) {
      window.location.href = response.data.url;
    } else {
      ElMessage.error('获取授权链接失败');
    }
  } catch (error) {
    ElMessage.error('授权服务异常');
  }
}
</script>

<style scoped>
.social-login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 8px 0;
  background: #fff;
}
.social-login-btn:hover {
  border-color: #e02020;
  box-shadow: 0 2px 8px rgba(224, 32, 32, 0.15);
  transform: translateY(-1px);
}
.social-icon { width: 24px; height: 24px; margin-right: 10px; }
</style>
