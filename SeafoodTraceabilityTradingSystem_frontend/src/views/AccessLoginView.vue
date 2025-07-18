<template xmlns="http://www.w3.org/1999/xhtml">
  <el-container class="band-container" :class="{ 'night-mode': isNightMode }">
    <el-header class="band-logo header-left">
      <img src="../images/logo.png" alt="Logo" class="band-img" @click="handleHome"/>
      <span class="header-title">第三方登录</span>
    </el-header>
    <el-main class="band-content">
      <div class="band-section" style="display: flex; width: 100%; align-items: center;">
        <p class="description">检测到您的第三方账号尚未绑定系统，请选择以下操作：</p>
        <div class="band-methods tab-buttons">
          <el-button :class="{'active-tab': showBindForm===true}"
                     @click=showLogin
                     class="band-method-button"><p style="font-size:24px">绑定现有账号</p>
          </el-button>
          <span class="separator">|</span>
          <el-button :class="{'active-tab': showRegisterForm === true}"
                     @click=showRegister
                     class="band-method-button"><p style="font-size:24px">注册新账号</p>
          </el-button>

        </div>
        <div class="login-form" style="display: flex; width: 100%; height: 100%; flex-direction: column;">
          <div class="login-options-container" style="display: flex; height: 100%;" v-if=
              "showBindForm === true">
            <div class="login-options" style="flex: 1; display: flex; flex-direction: column; padding: 20px;">
              <br><br>
              <el-button type="primary" @click=passwordLogin style="margin-bottom: 10px;"
                         class="animated-button">密码登录
              </el-button>
              <br>
              <el-button type="primary" @click=smsLogin class="animated-button">手机号登录</el-button>
            </div>
            <div class="form-content" style="flex: 3; padding: 20px;">
              <div v-if="loginMethod === 'password'" class="password-login">
                <el-form :model="bindForm" status-icon ref="loginFormRef">
                  <el-input v-model="bindForm.username" placeholder="账号名/邮箱/手机号"
                            class="custom-input"></el-input>
                  <div class="password-input-container">
                    <el-input v-model="bindForm.password" type="password" placeholder="请输入登录密码"
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
              <div v-if="loginMethod ==='sms'" class="sms-login">
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
            </div>
          </div>
          <div v-if="showRegisterForm === true">
            <div class="form-content" style="padding: 20px;">
              <el-form :model="smsForm" status-icon ref="smsFormRef">
                <el-input v-model="smsForm.phone" id="phone" placeholder="手机号" class="custom-input"
                          :disabled="isEditingPhone"></el-input>
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
                  <el-button type="primary" @click="validateNum" block class="login-button">验证</el-button>
                </el-form-item>
              </el-form>
            </div>
            <div v-if="verify===true">
              <el-input v-model="password" type="password" placeholder="请输入密码" @input="checkPasswordStrength"
                        show-password class="custom-input"></el-input>
              <div class="password-strength-wrapper">
                <div class="password-strength-bar">
                  <div class="strength-segment"
                       :class="{ 'low-strength-segment': passwordStrength === '弱' ,'medium-strength-segment': passwordStrength === '中' ,'high-strength-segment': passwordStrength === '高' }"></div>
                  <div class="strength-segment"
                       :class="{ 'medium-strength-segment': passwordStrength === '中','high-strength-segment': passwordStrength === '高' }"></div>
                  <div class="strength-segment" :class="{ 'high-strength-segment': passwordStrength === '高' }"></div>
                </div>
                <span :class="passwordStrengthClass">{{ passwordStrength }}</span>
              </div>
              <el-input v-model="confirmPassword" type="password" placeholder="确认密码" show-password
                        class="custom-input" @paste.prevent=""></el-input>
              <el-button type="primary" :disabled="!canCompleteRegistration"
                         :class="{ 'disabled-button': !canCompleteRegistration }" @click="completeRegistration" block
                         class="register-button" style="margin-top: 10px;">注册并绑定
              </el-button>
            </div>
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
import {computed, onMounted, ref, watch} from 'vue';
import router from "@/router";
import {useThemeStore} from '@/store/themeStore';
import {ElMessage} from 'element-plus';
import axios from 'axios';
import Vcode from "vue3-puzzle-vcode";
import FooterLoginComponent from "@/components/FooterLoginComponent.vue";

const themeStore = useThemeStore();
const isNightMode = computed(() => themeStore.isNightMode);
const showBindForm = ref(true);
const showRegisterForm = ref(false);
const loginMethod = ref('password');
const bindForm = ref({username: '', password: '', source: ' ', thirdID: '', thirdName: ''});
const smsForm = ref({phone: '', code: ''});
const registerForm = ref({
  username: '',
  phone: '',
  email: '',
  password: ''
});
const userInfo = ref(null);
const loginFormRef = ref(null);
const smsFormRef = ref(null);
const isEditingPhone = ref(false);
const password = ref('');
const passwordStrength = ref('弱');
const confirmPassword = ref('');
const verify = ref(false);
const canCompleteRegistration = computed(() => {
  return password.value === confirmPassword.value && passwordStrength.value !== '弱';
});

watch(isNightMode, (newVal) => {
  if (newVal) {
    document.body.classList.add('night-mode');
  } else {
    document.body.classList.remove('night-mode');
  }
});

function showLogin() {
  showBindForm.value = true;
  showRegisterForm.value = false;
  isEditingPhone.value = false;
  bindForm.value.username = '';
  smsForm.value.phone = '';
  smsForm.value.code = '';
  loginMethod.value = 'password';
}

function showRegister() {
  showBindForm.value = false;
  showRegisterForm.value = true;
  bindForm.value.username = '';
  bindForm.value.password = '';
  smsForm.value.phone = '';
  smsForm.value.code = '';
  loginMethod.value = 'password';
}

function passwordLogin() {
  loginMethod.value = 'password'
  smsForm.value.phone = '';
  smsForm.value.code = '';
}

function smsLogin() {
  loginMethod.value = 'sms'
  bindForm.value.username = '';
  bindForm.value.password = '';
}

// 验证码部分
const wait = ref(120);
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
  wait.value = wait.value > 0 ? wait.value : 120; // 确保初始值有效
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

async function handleLogin() {
  try {
    await loginFormRef.value.validate();
    const response = await axios.post('/api/user/login', {
      username: bindForm.value.username,
      password: bindForm.value.password,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      console.log(bindForm.value);
      await bindAccount();
    } else if (response.data.code === 400) {
      ElMessage.error('用户名或密码错误！请重新登录！');
      bindForm.value.username = '';
      bindForm.value.password = '';
    } else if (response.data.code === 401) {
      ElMessage.error('用户名不存在！请重新登录！');
      bindForm.value.username = '';
      bindForm.value.password = '';
    }
  } catch (error) {
    console.error('Error during login:', error.message);
  }
}

async function SelectPhone(phone) {
  try {
    const response = await axios.post('/api/user/register/selectPhone', {
      phone: phone
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    return response.data.code === 200;
  } catch (error) {
    ElMessage.error('服务器错误，请稍后再试！');
    return false;
  }
}

async function CheckPhoneNumber() {
  if (smsForm.value.phone.length === 0) {
    ElMessage.error('请输入手机号码！');
    return;
  } else if (!/^1[3-9]\d{9}$/.test(smsForm.value.phone)) {
    ElMessage.error('请输入正确的手机号码！');
    return;
  }
  if (await SelectPhone(smsForm.value.phone) === true) {
    onShow();
  } else {
    ElMessage.error('手机号已被占用，请重新输入！');
  }
}

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
    console.log(response.data);
    if (response.data.result === 404) {
      ElMessage.error('过期验证码已失效!');
    } else if (response.data.result === 500) {
      ElMessage.error('验证码错误，请重新输入！');
    } else if (response.data.result === 403) {
      if (showBindForm.value === true) {
        ElMessage.error('用户不存在，请注册账号！');
      } else if (showRegisterForm.value === true) {
        ElMessage.success('验证码验证成功');
        registerForm.value.phone = smsForm.value.phone;
        registerForm.value.username = "user" + Math.floor(Math.random() * 10000);
        bindForm.value.username = smsForm.value.phone;
        isEditingPhone.value = true;
        verify.value = true;
      }
    } else {
      if (showBindForm.value === true) {
        bindForm.value.username = smsForm.value.phone;
        await bindAccount();
      }
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
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
      document.cookie = `userBaseInfo=${encodeURIComponent(JSON.stringify(response.data))}; path=/ max-age=604800`; // 存储用户信息到cookie
    }
  } catch (err) {
    ElMessage.error('获取用户信息失败，请稍后再试！');
  }
}

const bindAccount = async () => {
  try {
    const response = await axios.post('/api/user/bindAccount', bindForm.value, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('账号绑定成功');
      setTimeout(async () => {
        await getUserBaseInfo(bindForm.value.username);
        await router.push('/');
      }, 1500);
    } else {
      ElMessage.error('绑定失败，请检查您的信息');
    }
  } catch (error) {
    ElMessage.error('绑定账号时出现错误');
    console.error(error);
  }
};

function checkPasswordStrength() {
  const passwordValue = password.value;
  let strength = '弱';

  // 判断密码长度
  const lengthCriteria = passwordValue.length >= 8;
  const hasUpperCase = /[A-Z]/.test(passwordValue);
  const hasLowerCase = /[a-z]/.test(passwordValue);
  const hasNumbers = /\d/.test(passwordValue);
  const hasSpecialChars = /[!@#$%^&*(),.?":{}|<>]/.test(passwordValue);

  // 强度等级判断
  const criteriaMet = [lengthCriteria, hasUpperCase, hasLowerCase, hasNumbers, hasSpecialChars].filter(Boolean).length;

  if (criteriaMet >= 4) {
    strength = '高';
  } else if (criteriaMet >= 2) {
    strength = '中';
  }
  passwordStrength.value = strength;
}

const passwordStrengthClass = computed(() => {
  switch (passwordStrength.value) {
    case '高':
      return 'high-strength';
    case '中':
      return 'medium-strength';
    case '弱':
    default:
      return 'low-strength';
  }
});

function completeRegistration() {
  if (password.value === confirmPassword.value && passwordStrength.value !== '弱') {
    registerForm.value.password = password.value;
    console.log(registerForm.value);
    axios.post('/api/user/register', registerForm.value, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    }).then(async response => {
      if (response.data.code === 200) {
        ElMessage.success('注册成功');
        await bindAccount();
      } else {
        ElMessage.error('注册失败，请稍后再试！');
      }
    }).catch(error => {
      console.error('Error during registration:', error.message);
      ElMessage.error('注册失败，请稍后再试！');
    });
  } else {
    ElMessage.error('密码不符合要求，请重新输入！');
  }
}

onMounted(async () => {
  try {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      const response = await axios.post('/api/callback/ThirdLogin/userInfo', {}, {withCredentials: true});
      if (response.data && response.data.name) {
        userInfo.value = response.data;
        ElMessage.success(`欢迎回来, ${userInfo.value.name}`);
        bindForm.value.source = userInfo.value.source;
        bindForm.value.thirdID = userInfo.value.id;
        bindForm.value.thirdName = userInfo.value.name;
      }
      urlParams.delete('token');
      const newUrl = urlParams.toString() ? `${window.location.pathname}?${urlParams.toString()}` : window.location.pathname;
      window.history.replaceState(null, '', newUrl);
    } else {
      await router.push('/login');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
});

const handleHome = async () => {
  await router.push('/');
};

function handleForgotPassword() {
  router.push('/forgotPassword');
}

</script>

<style lang="less" scoped>
.band-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  background-color: #f7f7f7;
  height: 100vh;
  justify-content: space-between;
}

.band-logo {
  display: flex;
  align-items: center;
  margin-top: 20px;
  position: absolute;
  top: 20px;
  left: 20px;
}

.band-img {
  height: 80px;
}

.header-title {
  margin-left: 15px;
  font-size: 28px;
  color: #96CDCD;
  font-weight: bold;
  line-height: 1;
}

.band-content {
  max-height: 68vh;
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

.band-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.description {
  font-size: 25px;
  margin-bottom: 30px;
  font-weight: bold;
}

.band-method-button {
  color: #668B8B;
  font-size: 20px;
  padding: 10px;
}

.band-method-button {
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
  margin: 0 10px;
}

.login-form {
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: column;
}

.login-options-container {
  display: flex;
  height: 100%;
}

.login-options {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.form-content {
  flex: 3;
  padding: 20px;
  justify-content: center;
}

.login-options el-button {
  margin-bottom: 10px;
  width: 100%;
}

.animated-button {
  position: relative;
  overflow: hidden;
  font-size: 18px;
  padding: 12px;
  border-radius: 8px;
  height: 48px;
  transition: all 0.5s;
  font-weight: bold;
}

.animated-button::after {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 300%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  transform: skewX(-45deg);
  transition: all 0.5s;
}

.animated-button:hover::after {
  left: 100%;
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

.login-button {
  font-size: 18px;
  padding: 12px;
  border-radius: 8px;
  width: 100%;
}

.password-strength-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
  margin-bottom: 20px;
}

.password-strength-bar {
  display: flex;
  width: 70%;
  height: 10px;
  background-color: #e0e0e0;
  border-radius: 5px;
}

.strength-segment {
  flex: 1;
  height: 100%;
  margin-right: 2px;
  border-radius: 5px;
}

.low-strength-segment {
  background-color: #f56c6c;
}

.medium-strength-segment {
  background-color: #e6a23c;
}

.high-strength-segment {
  background-color: #67c23a;
}

.password-strength-text {
  font-size: 16px;
  font-weight: bold;
}

.night-mode-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
}

.el-button--primary {
  background-color: #96CDCD;
  border-color: #96CDCD;
}

.el-button--primary:hover {
  background-color: #668B8B;
  border-color: #668B8B;
}

body.night-mode, html.night-mode {
  background-color: #1f1f1f;
  color: #f7f7f7;
}

.night-mode .el-container {
  background-color: #1f1f1f;
}

body.night-mode .band-content {
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
  background-color: #668B8B;
  border-color: #668B8B;
}

</style>
