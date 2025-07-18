<template>
  <div class="qr-container" v-loading="isLoading">
    <!-- 根据状态显示二维码或新的图片 -->
    <div v-if="showQRCode" class="qr-wrapper">
      <img :src="qrCodeUrl" alt="二维码" class="qr-image"/>
      <div class="status-text">{{ statusMessage }}</div>
    </div>
    <div v-else class="new-status-wrapper">
      <div v-if="!firstTime">
        <!-- 新的图片或提示，替换二维码 -->
        <img src="../images/Wechatlogin.png" alt="微信登录" class="new-status-image"/>
        <div class="new-status-text">{{ statusMessage }}</div>
      </div>
    </div>
    <el-button @click="generateQRCode" class="login-button" type="primary" block>生成二维码</el-button>
  </div>
</template>

<script>
import {ElMessage} from 'element-plus';
import axios from 'axios';
import {ref} from 'vue';
import router from "@/router";

export default {
  setup() {
    const qrCodeUrl = ref(null); // 初始二维码 URL
    const statusMessage = ref('请扫描二维码'); // 初始提示文字
    const showQRCode = ref(false); // 控制显示二维码或新元素
    const firstTime = ref(true); // 第一次加载页面
    const isLoading = ref(false); // 控制加载动画

    // 生成二维码
    const generateQRCode = async () => {
      ElMessage.info('正在生成二维码，请稍候');
      isLoading.value = true; // 启动加载动画

      try {
        const response = await axios.post('/api/wx/startQRCode');
        if (response.data.qrCodeUrl) {
          ElMessage.success('二维码生成成功');
          qrCodeUrl.value = response.data.qrCodeUrl;
          statusMessage.value = '请扫描二维码';
          showQRCode.value = true; // 显示二维码
          firstTime.value = false; // 不是第一次加载页面
          setTimeout(pollQRCodeStatus, 2000); // 开始轮询，等待2秒避免立即请求
        }
      } catch (error) {
        ElMessage.error('生成二维码失败，请稍后重试');
        console.error(error);
      } finally {
        isLoading.value = false; // 停止加载动画
      }
    };

    // 轮询二维码状态
    const pollQRCodeStatus = async () => {
      try {
        const response = await axios.post('/api/wx/pollQRCodeStatus');
        if (response.data.status.includes('登录成功')) {
          statusMessage.value = "登录成功，欢迎回来！";
          showQRCode.value = false; // 隐藏二维码，显示新的元素
          const openidMatch = response.data.status.match(/openid：(.+)/);
          let openid = null;
          if (openidMatch && openidMatch[1]) {
            openid = openidMatch[1].trim();
          }
          setTimeout(() => {
            router.replace("/accessResult?id=" + openid + "&source=wechat");
          }, 1500);
        } else if (response.data.status.includes('扫码成功')) {
          statusMessage.value = "扫码成功，请绑定账号";
          showQRCode.value = false;
          setTimeout(() => {
            const tokenMatch = response.data.status.match(/token：([^，]+)/);
            if (tokenMatch && tokenMatch[1]) {
              const token = tokenMatch[1];
              router.replace("/accessLogin?token=" + token)
            }
          }, 1500);
          // 等待 2 秒，避免立即请求
        } else if (response.data.status.includes('二维码已过期')) {
          statusMessage.value = '二维码已过期，请重新生成';
          showQRCode.value = false; // 隐藏二维码，显示新的元素
        } else if (response.data.status.includes('用户取消扫码')) {
          statusMessage.value = '用户取消扫码，请重新生成';
          showQRCode.value = false;
        } else {
          statusMessage.value = response.data.status;
          setTimeout(pollQRCodeStatus, 2000); // 每隔 2 秒轮询一次
        }
      } catch (error) {
        ElMessage.error('轮询二维码状态失败');
        console.error(error);
      }
    };

    return {
      qrCodeUrl,
      statusMessage,
      generateQRCode,
      showQRCode,
      firstTime,
      isLoading,
    };
  },
};
</script>


<style scoped>
.qr-container {
  display: flex;
  flex-direction: column; /* 垂直排列 */
  align-items: center; /* 居中对齐 */
  margin-top: 20px;
}

.qr-wrapper, .new-status-wrapper {
  display: inline-block;
  position: relative;
}

.qr-image, .new-status-image {
  width: 200px;
  height: 200px;
}

.status-text, .new-status-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 5px 10px;
  border-radius: 5px;
}

.login-button {
  background-color: #668B8B;
  border: none;
  font-size: 18px;
  padding: 12px;
  border-radius: 8px;
  width: 100%;
}
</style>
