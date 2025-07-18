<template xmlns="http://www.w3.org/1999/xhtml">
  <el-container class="login-container" :class="{ 'night-mode': isNightMode }">
    <el-header class="login-logo header-left">
      <img src="../images/logo.png" alt="Logo" class="logo-img" @click="handleHome"/>
      <span class="header-title">海潮&nbsp;&nbsp;&nbsp;&nbsp;海鲜食品溯源交易平台</span>
    </el-header>
    <el-main class="login-content">
      <div class="login-section" style="display: flex; width: 100%; align-items: center;">
        <div class="qr-login-section" style="margin-top: 50px; border-right: 1px solid #ddd; padding-right: 40px;">
          <p class="qr-login-text" style="margin-bottom: 20px; margin-top: 0; align-self: center; font-size: 24px;">
            手机扫码登录</p>
          <div class="qr-login">
            <wxLogin/>
            <p class="qr-login-text">打开微信APP，点击左上角扫一扫</p>
          </div>
        </div>

        <div class="credentials-login-section">
          <div class="login-methods tab-buttons">
            <el-button :class="{'active-tab': activeTab === 'password'}" @click="activeTab = 'password'"
                       class="login-method-button"><p style="font-size:24px">密码登录</p>
            </el-button>
            <span class="separator">|</span>
            <el-button :class="{'active-tab': activeTab === 'sms'}" @click="activeTab = 'sms'"
                       class="login-method-button"><p style="font-size:24px">短信登录</p>
            </el-button>

          </div>
          <div v-if="activeTab === 'password'" class="password-login">
            <el-form :model="loginForm" status-icon ref="loginFormRef">
              <el-input v-model="loginForm.username" placeholder="账号名/邮箱/手机号" class="custom-input"></el-input>
              <div class="password-input-container">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入登录密码"
                          class="custom-input password-input">
                  <template #append>
                    <el-link type="info" class="forgot-password-link no-unecoration" @click="handleForgotPassword">
                      忘记密码
                    </el-link>
                  </template>
                </el-input>
              </div>
              <el-form-item>
                <el-button type="primary" @click="handleLogin" block class="login-button">登录</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div v-if="activeTab === 'sms'" class="sms-login">
            <el-form :model="smsForm" status-icon ref="smsFormRef">
              <el-input v-model="smsForm.phone" id="phone" placeholder="手机号" class="custom-input"></el-input>
              <el-row :gutter="10">
                <el-col :span="14">
                  <el-input v-model="smsForm.code" placeholder="验证码" class="custom-input"></el-input>
                </el-col>
                <el-col :span="10">
                  <el-button
                      id="captcha-button"
                      type="primary"
                      :disabled="isButtonDisabled"
                      @click="CheckPhoneNumber"
                      class="sms-code-button">
                    {{ buttonText }}
                  </el-button>
                </el-col>
              </el-row>
              <Vcode :show="isShow" @success="onSuccess" @close="onClose"/>
              <el-form-item>
                <el-button type="primary" @click="validateNum" block class="login-button">登录</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div class="third-party-login">
            <span>第三方登录：</span>
            <GiteeLogin/>
            <GitHubLogin/>
            <GoogleLogin/>
            <span class="separator">|</span>
            <el-button type="text" class="register-button" @click="handleRegister">注册账号</el-button>
          </div>

        </div>
      </div>
    </el-main>
    <el-aside class="night-mode-toggle">
      <el-switch v-model="themeStore.isNightMode" active-text="夜间模式" inactive-text="白天模式"></el-switch>
    </el-aside>
    <FooterLoginComponent/>
  </el-container>
</template>

<script setup>
import {computed, ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import axios from 'axios';
import {ElMessage} from 'element-plus';
import Vcode from "vue3-puzzle-vcode";
import {useThemeStore} from '@/store/themeStore';
import GiteeLogin from "@/components/GiteeLoginComponent.vue";
import GitHubLogin from "@/components/GitHubLoginComponent.vue";
import wxLogin from "@/components/WechatLoginComponent.vue"
import GoogleLogin from "@/components/GoogleLoginComponent.vue";
import FooterLoginComponent from "@/components/FooterLoginComponent.vue";


const router = useRouter();
const activeTab = ref('password');
const loginForm = ref({
  username: '',
  password: ''
});
const smsForm = ref({
  phone: '',
  code: ''
});
const loginFormRef = ref(null);
const smsFormRef = ref(null);
const themeStore = useThemeStore();
const isNightMode = computed(() => themeStore.isNightMode);
const rememberMe = ref(false);
watch(isNightMode, (newVal) => {
  if (newVal) {
    document.body.classList.add('night-mode');
  } else {
    document.body.classList.remove('night-mode');
  }
});

// 检测是否存在cookie，如果存在，则填入session
try {
  const userBaseInfo = JSON.parse(decodeURIComponent(document.cookie.replace(/(?:^|.*;\s*)userBaseInfo\s*=\s*([^;]*).*$|^.*$/, "$1")));
  if (userBaseInfo) {
    try {
      sessionStorage.setItem('userBaseInfo', JSON.stringify(userBaseInfo));
    } catch (error) {
      console.log("无cookie")
    }
  }
} catch (error) {
  console.log("无cookie");
}
// 检测是否存在session，如果存在，则跳转到首页
const userBaseInfoFromSession = JSON.parse(sessionStorage.getItem('userBaseInfo'));
if (userBaseInfoFromSession) {
  router.replace('/');
}

async function getUserBaseInfo(username) {
  try {
    const response = await axios.post('/api/user/getUserBaseInfo', {
      username: username
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.id !== null) {
      sessionStorage.setItem('userBaseInfo', JSON.stringify(response.data)); // 存储用户信息到sessionStorage
      document.cookie = `userBaseInfo=${encodeURIComponent(JSON.stringify(response.data))}; path=/; max-age=604800`; // 存储用户信息到cookie，有效期7天
    }
  } catch (err) {
    ElMessage.error('获取用户信息失败，请稍后再试！');
  }
}

async function handleLogin() {
  try {
    await loginFormRef.value.validate();
    const response = await axios.post('/api/user/login', {
      username: loginForm.value.username,
      password: loginForm.value.password,
      rememberMe: rememberMe.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('登录成功！');
      await getUserBaseInfo(loginForm.value.username);
      setTimeout(() => {
        router.replace('/index');
      }, 1500);
    } else if (response.data.code === 400) {
      ElMessage.error('用户名或密码错误！请重新登录！');
      loginForm.value.username = '';
      loginForm.value.password = '';
    } else if (response.data.code === 401) {
      ElMessage.error('用户名不存在！请重新登录！');
      loginForm.value.username = '';
      loginForm.value.password = '';
    }
  } catch (error) {
    console.error('Error during login:', error.message);
  }
}

// 验证码部分
const wait = ref(60);
const isButtonDisabled = ref(false);
const buttonText = ref('获取验证码');
const isShow = ref(false);

watch(wait, (newValue) => {
  if (newValue === 0) {
    isButtonDisabled.value = false;
    buttonText.value = '获取验证码';
    wait.value = 60; // 重置计时器
  } else {
    buttonText.value = `${newValue}秒后可以重新发送`;
  }
});

async function CheckPhoneNumber() {
  if (smsForm.value.phone.length === 0) {
    ElMessage.error('请输入手机号码！');
    return;
  } else if (!/^1[3-9]\d{9}$/.test(smsForm.value.phone)) {
    ElMessage.error('请输入正确的手机号码！');
    return;
  }
  onShow();
}

// 人机验证部分
const onShow = () => {
  isShow.value = true;
};

const onClose = () => {
  isShow.value = false;
};

const onSuccess = () => {
  onClose(); // 验证成功，需要手动关闭模态框
  getMsgNum(); // 获取验证码
};

// 获取验证码
async function getMsgNum() {
  setButtonStatus(); // 设置按钮状态
  try {
    const response = await axios.post('/api/sms/smsXxs', {
      phoneNumber: smsForm.value.phone,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });

    if (response.data.isOk === "OK") {
      ElMessage.success('验证码已发送到您的手机，请注意查收！');
    } else {
      ElMessage.error('验证码发送失败，请稍后再试！');
    }
  } catch (error) {
    console.error("发送验证码错误:", error);
  }
}

// 设置按钮状态
function setButtonStatus() {
  isButtonDisabled.value = true;
  wait.value = wait.value > 0 ? wait.value : 60; // 确保初始值有效
  countdown();
}

// 计时功能
function countdown() {
  if (wait.value > 0) {
    setTimeout(() => {
      wait.value -= 1;
      countdown();
    }, 1000);
  }
}

// 登录按钮
async function validateNum() {
  if (smsForm.value.phone.length === 0 || smsForm.value.phone.length === 0) {
    ElMessage.error('请输入手机号码和验证码！');
    return;
  }
  const data = {
    verifyCode: smsForm.value.code,
    phone: smsForm.value.phone,
  };

  try {
    await smsFormRef.value.validate();
    const response = await axios.post('/api/sms/validateNum', data, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.result === 404) {
      ElMessage.error('过期验证码已失效!');
    } else if (response.data.result === 500) {
      ElMessage.error('验证码错误，请重新输入！');
    } else if (response.data.result === 403) {
      ElMessage.error('用户不存在，请注册账号！');
    } else {
      ElMessage.success('登录成功！');
      await getUserBaseInfo(smsForm.value.phone);
      setTimeout(() => {
        router.replace('/index');
      }, 1500);
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
}

function handleHome() {
  router.push('/index');
}

function handleForgotPassword() {
  router.push('/forgotPassword');
}

function handleRegister() {
  router.push('/register');
}


</script>

<style lang="less" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  background-color: #f7f7f7;
  min-height: 100vh;
  justify-content: space-between;
}

.login-logo {
  display: flex;
  align-items: center;
  margin-top: 50px;
  position: absolute;
  top: 20px;
  left: 20px;
}

.logo-img {
  width: 120px;
}

.login-logo img {
  width: 150px;
}

.header-title {
  margin-left: 15px;
  font-size: 28px;
  color: #668B8B;
  font-weight: bold;
  line-height: 1;
}


.login-content {
  max-height: 65vh;
  display: flex;
  width: 100%;
  max-width: 900px;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  position: relative;
  top: 20vh;
}

.login-section {
  display: flex;
  width: 100%;
}

.qr-login-section {
  flex: 0.33;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding-right: 50px;
  margin-left: 50px;
  border-right: 1px solid #ddd;
  margin-top: 40px;
}

.credentials-login-section {
  flex: 0.66;
  padding-left: 30px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-top: 0;
}

.login-method-button {
  color: #668B8B;
  font-size: 20px;
  padding: 10px;
}

.login-method-button {
  font-size: 16px;
  padding: 10px 20px;
  border: none;
  background-color: transparent;
  color: #666;
  cursor: pointer;
}

.active-tab {
  font-weight: bold;
  color: #668B8B;
  border-bottom: 2px solid #668B8B;
}

.separator {
  font-size: 20px;
  color: #ccc;
  margin: 0 20px;
  align-self: center;
}

.qr-login {
  text-align: center;
}

.qr-code {
  width: 180px;
  height: 180px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.qr-login-text {
  font-size: 14px;
  color: #666;
}

.password-login,
.sms-login {
  padding: 20px;
}

.custom-input {
  border-radius: 10px;
  padding: 16px;
  font-size: 18px;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.password-input {
  border-right: none;
}

.forgot-password-link {
  color: #666;
  font-size: 16px;
  cursor: pointer;
  border-left: none;
  padding-left: 8px;
  border-right: none;
}

.no-underline {
  text-decoration: none;
}

.forgot-password-input {
  text-align: right;
  cursor: pointer;
  color: #666;
  font-size: 16px;
}

.third-party-login {
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}

.third-party-login .el-button {
  margin: 0 10px;
  font-size: 20px;
}

.night-mode-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
}

.login-button {
  font-size: 18px;
  padding: 12px;
  border-radius: 8px;
  width: 100%;
}

.sms-code-button {
  margin-top: 15px;
  height: 30px; /* 与输入框高度保持一致 */
  padding: 0 20px; /* 左右内边距让按钮显得更舒展 */
  font-size: 16px;
  border-radius: 8px;
  line-height: 48px; /* 与按钮高度一致，保证文字垂直居中 */
  //居中显示
  display: flex;

}

.el-button--primary {
  background-color: #668B8B;
  border-color: #668B8B;
}

.el-button--primary:hover {
  background-color: #96CDCD;
  border-color: #96CDCD;
}

body.night-mode, html.night-mode {
  background-color: #1f1f1f;
  color: #f7f7f7;
}

.night-mode .el-container {
  background-color: #1f1f1f; /* 设置容器的深色背景 */
}

.el-footer.night-mode {
  color: #96CDCD;
}

.el-footer.night-mode.a {
  color: #96CDCD;
}

body.night-mode .login-content {
  background-color: #333;
  color: #f7f7f7;
}

body.night-mode .el-input,
body.night-mode .el-input__inner {
  background-color: #444;
  color: #f7f7f7;
  border-color: #555;
}

body.night-mode .el-button--primary {
  background-color: #96CDCD;
  border-color: #96CDCD;
}

</style>