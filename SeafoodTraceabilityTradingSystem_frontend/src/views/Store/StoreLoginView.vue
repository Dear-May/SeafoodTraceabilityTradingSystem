<template>
  <div id="box" class="background-video"></div>
  <el-header>
    <OceanWaveLogo ref="logoComponent"></OceanWaveLogo>
  </el-header>
  <div class="login-container">
    <div class="login-card">
      <div class="column">
        <h1>登录</h1>
        <p>欢迎来到商家登录平台，请输入账号密码！</p>
        <el-form :model="loginForm">
          <el-form-item>
            <el-input v-model="loginForm.phone" placeholder="手机号" class="form-element"/>
          </el-form-item>
          <el-form-item>
            <el-input v-model="loginForm.code" type="text" placeholder="验证码" class="form-element"
                      style="width: 50%"/>
            <el-button type="text" @click="CheckPhoneNumber" style="margin-left: 18px;" :disabled="isButtonDisabled">
              {{ buttonText }}
            </el-button>
          </el-form-item>
          <Vcode :show="isShow" @success="onSuccess" @close="onClose"/>
          <el-form-item class="form-checkbox-item">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          </el-form-item>
          <div class="flex">
            <el-button type="primary" @click="validateNum">登录</el-button>
            <a @click="() => router.push('/login')" class="text-link">普通用户登录</a>
          </div>
        </el-form>
      </div>
      <div class="column">
        <h2>海潮商城欢迎您的加入！</h2>
        <p>如果您没有账号，您想要现在注册一个吗？</p>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </div>
    </div>
  </div>
  <FooterComponent/>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue';
import OceanWaveLogo from "@/components/OceanWaveLogoComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import router from "@/router";
import {AbstractShapeBg} from '@/js/AbstractShapeBg.module';
import {ElMessage} from "element-plus";
import axios from "axios";
import Vcode from "vue3-puzzle-vcode";
import {useUserShop} from "@/composables/useShopUser";

const logoComponent = ref(null);

function initLogo() {
  logoComponent.value.setTitle('商家登录')
}

const {initUserSession} = useUserShop();

const loginForm = ref({
  phone: '',
  code: '',
  rememberMe: false,
});

const isShow = ref(false);
const wait = ref(60);
const isButtonDisabled = ref(false);
const buttonText = ref("获取验证码");
// 监听验证码倒计时
watch(wait, (newValue) => {
  if (newValue === 0) {
    isButtonDisabled.value = false;
    buttonText.value = "获取验证码";
    wait.value = 60; // 重置计时器
  } else {
    buttonText.value = `${newValue}秒后可以重新发送`;
  }
});

// 检查手机号输入
const CheckPhoneNumber = () => {
  if (!loginForm.value.phone) {
    ElMessage.error("请输入手机号码！");
  } else if (!/^1[3-9]\d{9}$/.test(loginForm.value.phone)) {
    ElMessage.error("请输入正确的手机号码！");
  }
  onShow();
};

// 人机验证部分
const onShow = () => {
  isShow.value = true;
};

const onClose = () => {
  isShow.value = false;
};

const onSuccess = () => {
  onClose();
  getMsgNum();
};

// 发送验证码
const getMsgNum = async () => {
  setButtonStatus();
  try {
    const response = await axios.post(
        "/api/sms/smsXxs",
        {
          phoneNumber: loginForm.value.phone,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
    );

    if (response.data.isOk === "OK") {
      ElMessage.success("验证码已发送到您的手机，请注意查收！");
    } else {
      ElMessage.error("验证码发送失败，请稍后再试！");
    }
  } catch (error) {
    console.error("发送验证码错误:", error);
  }
};

// 设置按钮状态
const setButtonStatus = () => {
  isButtonDisabled.value = true;
  wait.value = wait.value > 0 ? wait.value : 60;
  countdown();
};

// 倒计时功能
const countdown = () => {
  if (wait.value > 0) {
    setTimeout(() => {
      wait.value -= 1;
      countdown();
    }, 1000);
  }
};

async function getUserBaseInfo() {
  try {
    const response = await axios.post('/api/shop/user/getShopUser', {
      phone: loginForm.value.phone,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.id !== null) {
      if (response.data.status === "正常") {
        sessionStorage.setItem('ShopUserBaseInfoSession', JSON.stringify(response.data)); // 存储用户信息到sessionStorage
        if (loginForm.value.rememberMe) {
          document.cookie = `ShopUserBaseInfo=${encodeURIComponent(JSON.stringify(response.data))}; path=/; max-age=604800`; // 存储用户信息到cookie，有效期7天
        }
      } else {
        ElMessage.error("该用户未通过商家审核，请联系店长！")
      }
    }
  } catch (err) {
    ElMessage.error('获取用户信息失败，请稍后再试！');
  }
}

async function handleLogin() {
  try {
    const response = await axios.post('/api/shop/user/login', {
      phone: loginForm.value.phone,
    }, {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });

    if (response.data.code === 200) {
      ElMessage.success("登录成功！");
      await getUserBaseInfo();
      setTimeout(() => {
        router.push('/store/index');
      }, 1000);
    } else if (response.data.code === 403) {
      ElMessage.error("用户不存在，请注册账号！");
      setTimeout(() => {
        router.push('/store/register');
      }, 1500);
    } else {
      ElMessage.error("登录失败，请稍后再试！");
    }
  } catch (error) {
    ElMessage.error("登录失败，请稍后再试！");
  }
}

// 验证验证码
const validateNum = async () => {
  if (!loginForm.value.phone || !loginForm.value.code) {
    ElMessage.error("请输入手机号码和验证码！");
    return;
  }
  const data = {
    verifyCode: loginForm.value.code,
    phone: loginForm.value.phone,
  };

  try {
    const response = await axios.post("/api/sms/validateNum", data, {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });

    if (response.data.result === 404) {
      ElMessage.error("过期验证码已失效!");
    } else if (response.data.result === 500) {
      ElMessage.error("验证码错误，请重新输入！");
    } else if (response.data.result === 403) {
      ElMessage.error("用户不存在或已离职，请注册账号！");
    } else {
      await handleLogin();
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
};

function handleRegister() {
  router.push('/store/register');
}

onMounted(() => {
  initLogo();
  initUserSession();
  // 初始化 AbstractShapeBg
  const colorbg = new AbstractShapeBg({
    dom: 'box', // 对应需要挂载动画的 DOM 元素 ID
    colors: ['#0D98BA', '#0CA0B1', '#0BA9A8', '#0AB19F', '#09BA96', '#08C28D'], // 自定义颜色
    loop: true, // 是否循环动画
  });
});
</script>

<style scoped lang="less">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1.5rem;
  background-color: #e0f7fa; /* 海洋背景颜色 */
}

body:after {
  content: "";
  position: fixed;
  inset: 0;
  background-color: #004d61; /* 深海蓝色 */
  width: 60%;
  height: 100vh;
  clip-path: polygon(0 100%, 0 0, 100% 0, 70% 100%);
  z-index: -1;
}

.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 94vh;
  width: 100%;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保视频覆盖整个背景 */
  z-index: -1; /* 确保视频在内容下面 */
}

.login-card {
  background-color: white;
  border: 1px solid #ddd;
  box-shadow: 0 10px 50px -30px black;
  width: 1200px;
  border-radius: 30px;
  overflow: hidden;
  display: grid;
  grid-template-columns: auto auto;
}

.login-card a {
  text-decoration: none;
  color: #004d61; /* 主配色 */
}

.login-card .column {
  padding: 4rem;
}

.login-card .column:last-child {
  background: url("../../images/banner1.jpg") center;
  background-size: cover;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 40px;
}

.login-card .column:last-child:after {
  content: "";
  position: absolute;
  background: linear-gradient(90deg, white, rgba(255, 255, 255, 0.1333333333));
  inset: 0;
}

.login-card .column:last-child * {
  z-index: 1;
}


.login-card .column:last-child a:hover {
  background-color: #004d61;
  color: white;
}

.login-card h1 {
  margin-bottom: 15px;
}

.login-card .form-element {
  width: 100%;
  border: 0;
  background-color: white;
  padding: 20px 30px;
  font-size: 18px;
  border-radius: 50px;
  transition: box-shadow 0.2s;
  border: none !important;
  outline: none;
  box-shadow: 0 0 0 2px #004d61;
}

.el-input {
  :deep(.el-input__wrapper) {
    border: none !important;
    box-shadow: none !important;
  }

  :deep(.el-input__inner) {
    border: none !important;
    box-shadow: none !important;
  }
}

.login-card input[type=checkbox] {
  accent-color: #004d61;
  width: 16px;
  height: 16px;
}

.login-card form {
  margin-top: 3rem;
}

.login-card form > * {
  margin-top: 1rem;
}

.login-card button {
  background-color: #004d61;
  color: white;
  border: none;
  padding: 20px 40px;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 600;
  font-size: 18px;
  transition: all 0.2s;
}

.login-card form button:active {
  scale: 0.95;
  background-color: #003647;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.social-buttons {
  display: flex;
  gap: 1rem;
}

.social-buttons a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: 50px;
  font-size: 22px;
  color: white;
}

</style>
