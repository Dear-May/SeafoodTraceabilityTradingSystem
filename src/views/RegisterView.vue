<template xmlns="http://www.w3.org/1999/xhtml">
  <el-container class="register-container" :class="{ 'night-mode': isNightMode }">
    <el-header class="register-logo header-left">
      <img src="../images/logo.png" alt="Logo" class="logo-img" @click="handleHome"/>
      <span class="header-title">用户注册</span>
    </el-header>
    <el-main class="register-content">
      <div class="register-section" style="display: flex; width: 100%; align-items: center;">
        <div class="registration-steps-section">
          <div class="step-indicator">
            <p>
              <span
                  :class="['step-box step-box-enhanced', { 'active-step': currentStep === 1, 'breathing-border': currentStep === 1 }]">
                输入用户名</span>
              <img src="../images/arrow.png" alt="箭头" class="arrow-icon"/>
              <span
                  :class="['step-box step-box-enhanced', { 'active-step': currentStep === 2, 'breathing-border': currentStep === 2 }]">
                验证手机号和邮箱</span>
              <img src="../images/arrow.png" alt="箭头" class="arrow-icon"/>
              <span
                  :class="['step-box step-box-enhanced', { 'active-step': currentStep === 3, 'breathing-border': currentStep === 3 }]">
                输入密码</span>
            </p>
          </div>

          <div v-if="currentStep === 1" class="step-content">
            <h3 class="step-title">步骤 1: 输入用户名</h3>
            <div class="username-check-container" style="display: flex; align-items: center; gap: 10px;">
              <el-input v-model="username" placeholder="请输入用户名" @input="handleUsernameChange" clearable
                        class="username-input"></el-input>
              <el-button type="primary" @click="checkUsername" class="check-username-button">检查用户名</el-button>
            </div>
            <p v-if="usernameCheckMessage" class="username-check-message">{{ usernameCheckMessage }}</p>
            <el-button type="primary" :disabled="!isUsernameValid" :class="{ 'disabled-button': !isUsernameValid }"
                       @click="nextStep" block class="register-button"
                       style="margin-top: 10px;">下一步
            </el-button>
          </div>
          <div v-if="currentStep === 2" class="step-content">
            <h3 class="step-title">步骤 2: 验证手机号和邮箱</h3>
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
                      :disabled="isButtonDisabledToSms"
                      @click="CheckPhoneNumberToSms"
                      class="sms-code-button">
                    {{ buttonTextToSms }}
                  </el-button>
                </el-col>
              </el-row>
              <Vcode :show="isShowToSms" @success="onSuccessToSms" @close="onCloseToSms"/>
              <el-form-item>
                <el-button type="primary" @click="validateNumToSms" block class="login-button">验证</el-button>
              </el-form-item>
            </el-form>
            <el-form :model="emailForm" status-icon ref="emailFormRef">
              <el-input v-model="emailForm.email" id="email" placeholder="邮箱(选填)" class="custom-input"
                        :disabled="isEditingEmail"></el-input>
              <el-row :gutter="10">
                <el-col :span="14">
                  <el-input v-model="emailForm.code" placeholder="验证码" class="custom-input"></el-input>
                </el-col>
                <el-col :span="10">
                  <el-button
                      id="captcha-button"
                      type="primary"
                      :disabled="isButtonDisabledToEmail"
                      @click="CheckPhoneNumberToEmail"
                      class="sms-code-button">
                    {{ buttonTextToEmail }}
                  </el-button>
                </el-col>
              </el-row>
              <Vcode :show="isShowToEmail" @success="onSuccessToEmail" @close="onCloseToEmail"/>
              <el-form-item>
                <el-button type="primary" @click="validateNumToEmail" block class="login-button">验证</el-button>
              </el-form-item>
            </el-form>
            <el-button type="primary" :disabled="!isPhoneVerified" :class="{ 'disabled-button': !isPhoneVerified }"
                       @click="nextStep" block class="register-button"
                       style="margin-top: 10px;">下一步
            </el-button>
          </div>
          <div v-if="currentStep === 3" class="step-content">
            <h3 class="step-title">步骤 3: 输入密码</h3>
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
                       class="register-button" style="margin-top: 10px;">完成注册
            </el-button>
          </div>
        </div>
      </div>
      <a href="/merchant" class="merchant-link" @click="handleMerchant">成为商家，点我跳转</a>
      <a @click="handleLogin" class="login-link">已有账号，返回登录</a>
    </el-main>
    <el-aside class="night-mode-toggle">
      <el-switch v-model="themeStore.isNightMode" active-text="夜间模式" inactive-text="白天模式"></el-switch>
    </el-aside>
    <FooterLoginComponent/>
  </el-container>
</template>

<script setup>
import {computed, ref, watch} from 'vue';
import router from "@/router";
import axios from "axios";
import Vcode from "vue3-puzzle-vcode";
import {ElMessage} from "element-plus";
import {useThemeStore} from '@/store/themeStore';
import FooterLoginComponent from "@/components/FooterLoginComponent.vue";

const currentStep = ref(1);
const username = ref('');
const isUsernameValid = ref(false);
const phoneNumber = ref('');
const email = ref('');
const isEditingPhone = ref(false);
const isEditingEmail = ref(false);
const isPhoneVerified = ref(false);
const password = ref('');
const confirmPassword = ref('');
const passwordStrength = ref('弱');
const themeStore = useThemeStore();
const isNightMode = computed(() => themeStore.isNightMode);
const usernameCheckMessage = ref('');
const usernameCheckMessageColor = ref('');

const registerForm = ref({
  username: '',
  phone: '',
  email: '',
  password: ''
});

watch(isNightMode, (newVal) => {
  if (newVal) {
    console.log(themeStore.isNightMode);
    document.body.classList.add('night-mode');
  } else {
    console.log(themeStore.isNightMode);
    document.body.classList.remove('night-mode');
  }
});

const canCompleteRegistration = computed(() => {
  return password.value === confirmPassword.value && passwordStrength.value !== '弱';
});

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

function handleUsernameChange() {
  isUsernameValid.value = false; // 用户名修改后需要重新检查
  usernameCheckMessage.value = ''; // 清除之前的检查消息
}

function isValidUsername(username) {
  const usernameRegex = /^(?!\d)[a-zA-Z0-9_]{3,15}$/;

  return usernameRegex.test(username);
}

async function checkUsername() {
  // 模拟检查用户名逻辑
  if (isValidUsername(username.value)) {
    try {
      const response = await axios.post('/api/user/register/check-username', {
        username: username.value,
      }, {
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        withCredentials: true
      });
      if (response.data.code === 200) {
        isUsernameValid.value = true;
        usernameCheckMessage.value = '用户名可用';
        usernameCheckMessageColor.value = '#67c23a';
      } else {
        isUsernameValid.value = false;
        if (response.data.code === 400) {
          usernameCheckMessage.value = '用户名已被占用';
          usernameCheckMessageColor.value = '#f56c6c';
        } else if (response.data.code === 402) {
          usernameCheckMessage.value = '用户名格式错误';
          usernameCheckMessageColor.value = '#f56c6c';
        }
      }
    } catch (error) {
      console.error('Error during redirect:', error.message);
      isUsernameValid.value = false;
      usernameCheckMessage.value = '服务器错误';
      usernameCheckMessageColor.value = '#808080';
    }
  } else {
    isUsernameValid.value = false;
    usernameCheckMessage.value = '用户名无效，应符合3到15个字符，只能包含字母、数字和下划线，且不能以数字开头';
    usernameCheckMessageColor.value = '#f56c6c';
  }
}

// 手机验证码部分
const waitToSms = ref(120);
const isButtonDisabledToSms = ref(false);
const buttonTextToSms = ref('获取验证码');
const isShowToSms = ref(false);
const smsForm = ref({
  phone: '',
  code: ''
});
const smsFormRef = ref(null);

watch(waitToSms, (newValueToSms) => {
  if (newValueToSms === 0) {
    isButtonDisabledToSms.value = false;
    buttonTextToSms.value = '获取验证码';
    waitToSms.value = 60; // 重置计时器
  } else {
    buttonTextToSms.value = `${newValueToSms}秒后可以重新发送`;
  }
});

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

async function CheckPhoneNumberToSms() {
  if (smsForm.value.phone.length === 0) {
    ElMessage.error('请输入手机号码！');
    return;
  } else if (!/^1[3-9]\d{9}$/.test(smsForm.value.phone)) {
    ElMessage.error('请输入正确的手机号码！');
    return;
  }
  if (await SelectPhone(smsForm.value.phone) === true) {
    onShowToSms();
  } else {
    ElMessage.error('手机号已被占用，请重新输入！');
  }
}

// 人机验证部分
const onShowToSms = () => {
  isShowToSms.value = true;
};

const onCloseToSms = () => {
  isShowToSms.value = false;
};

const onSuccessToSms = () => {
  onCloseToSms(); // 验证成功，需要手动关闭模态框
  getMsgNumToSms(); // 获取验证码
};

// 获取验证码
async function getMsgNumToSms() {
  setButtonStatusToSms(); // 设置按钮状态
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
function setButtonStatusToSms() {
  isButtonDisabledToSms.value = true;
  waitToSms.value = waitToSms.value > 0 ? waitToSms.value : 120; // 确保初始值有效
  countdownToSms();
}

// 计时功能
function countdownToSms() {
  if (waitToSms.value > 0) {
    setTimeout(() => {
      waitToSms.value -= 1;
      countdownToSms();
    }, 1000);
  }
}

async function validateNumToSms() {
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
    } else {
      ElMessage.success('验证成功！');
      isPhoneVerified.value = true;
      phoneNumber.value = smsForm.value.phone;
      isEditingPhone.value = true;
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
}

// 邮箱验证部分
const waitToEmail = ref(120);
const isButtonDisabledToEmail = ref(false);
const buttonTextToEmail = ref('获取验证码');
const isShowToEmail = ref(false);
const emailForm = ref({
  email: '',
  code: ''
});
const emailFormRef = ref(null);

watch(waitToEmail, (newValueToEmail) => {
  if (newValueToEmail === 0) {
    isButtonDisabledToEmail.value = false;
    buttonTextToEmail.value = '获取验证码';
    waitToEmail.value = 30; // 重置计时器
  } else {
    buttonTextToEmail.value = `${newValueToEmail}秒后可以重新发送`;
  }
});

async function SelectEmail(email) {
  try {
    const response = await axios.post('/api/user/register/selectEmail', {
      email: email
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

async function CheckPhoneNumberToEmail() {
  console.log(emailForm.value.email);
  if (emailForm.value.email.length === 0) {
    ElMessage.error('请输入邮箱！');
    return;
  } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(emailForm.value.email)) {
    ElMessage.error('请输入正确的邮箱地址！');
    return;
  }
  if (await SelectEmail(emailForm.value.email) === true) {
    onShowToEmail();
  } else {
    ElMessage.error('邮箱已被占用，请重新输入！');
  }
}

// 人机验证部分
const onShowToEmail = () => {
  isShowToEmail.value = true;
};

const onCloseToEmail = () => {
  isShowToEmail.value = false;
};

const onSuccessToEmail = () => {
  onCloseToEmail(); // 验证成功，需要手动关闭模态框
  getMsgNumToEmail(); // 获取验证码
};

// 获取验证码
async function getMsgNumToEmail() {
  setButtonStatusToEmail(); // 设置按钮状态
  try {
    const response = await axios.post('/api/email/send', {
      email: emailForm.value.email,
    }, {}, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });

    if (response.data.code === 200) {
      ElMessage.success('验证码已发送到您的邮箱，请注意查收！');
    } else {
      ElMessage.error('验证码发送失败，请稍后再试！');
    }
  } catch (error) {
    console.error("发送验证码错误:", error);
  }
}

// 设置按钮状态
function setButtonStatusToEmail() {
  isButtonDisabledToEmail.value = true;
  waitToEmail.value = waitToEmail.value > 0 ? waitToEmail.value : 30; // 确保初始值有效
  countdownToEmail();
}

// 计时功能
function countdownToEmail() {
  if (waitToEmail.value > 0) {
    setTimeout(() => {
      waitToEmail.value -= 1;
      countdownToEmail();
    }, 1000);
  }
}

async function validateNumToEmail() {
  if (emailForm.value.email.length === 0 || emailForm.value.code.length === 0) {
    ElMessage.error('请输入邮箱和验证码！');
    return;
  }
  const data = {
    verCode: emailForm.value.code,
    email: emailForm.value.email,
  };

  try {
    await emailFormRef.value.validate();
    const response = await axios.post('/api/email/validateNum', data, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    console.log(response.data);
    if (response.data.code === 400) {
      ElMessage.error('过期验证码已失效!');
    } else if (response.data.code === 500) {
      ElMessage.error('验证码错误，请重新输入！');
    } else {
      ElMessage.success('验证成功！');
      registerForm.value.email = emailForm.value.email;
      email.value = emailForm.value.email;
      isEditingEmail.value = true;
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
}

function nextStep() {
  registerForm.value.username = username.value;
  registerForm.value.phone = phoneNumber.value;
  registerForm.value.email = email.value;
  registerForm.value.password = password.value;
  console.log(registerForm.value);
  currentStep.value++;
}

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

function completeRegistration() {
  if (password.value === confirmPassword.value && passwordStrength.value !== '弱') {
    registerForm.value.password = password.value;
    console.log(registerForm.value);
    axios.post('/api/user/register', registerForm.value, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    }).then(response => {
      if (response.data.code === 200) {
        ElMessage.success('注册成功，即将跳转登录页面！');
        setTimeout(() => {
          handleHome();
        }, 1500);
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

function handleLogin() {
  router.push('/login');
}


function handleMerchant() {
  router.push('/store/login');
}

async function handleHome() {
  await router.push('/');
}
</script>

<style lang="less" scoped>
.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  background-color: #f7f7f7;
  height: 100vh;
  justify-content: space-between;
}

.register-logo {
  display: flex;
  align-items: center;
  margin-top: 20px;
  position: absolute;
  top: 20px;
  left: 20px;
}

.logo-img {
  height: 80px;
}

.header-title {
  margin-left: 15px;
  font-size: 28px;
  color: #668B8B;
  font-weight: bold;
  line-height: 1;
}

.register-content {
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

.register-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.step-indicator {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.arrow-icon {
  width: 50px;
  height: 50px;
  margin: 0 20px;
  vertical-align: middle;
}

.step-box {
  padding: 15px 25px;
  border: 2px solid transparent;
  border-radius: 10px;
  transition: border-color 0.5s ease;
}

.step-box-enhanced {
  background-color: #B6D0E2;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.step-box-enhanced:hover {
  transform: scale(1.05);
}

.step-box.active-step {
  font-weight: bold;
  color: #668B8B;
  border-bottom: 2px solid #668B8B;
}

.step-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
}

.custom-input {
  width: 70%;
  border-radius: 10px;
  padding: 5px;
  font-size: 18px;
  display: flex;
  align-items: center;
  margin-bottom: 5px;
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
  width: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.username-check-container {
  gap: 20px;
  margin-bottom: 20px;
}

.username-input {
  height: 50px;
  font-size: 20px;
}

.check-username-button {
  flex-shrink: 0;
}

.username-check-message {
  color: v-bind(usernameCheckMessageColor);
}

.register-button {
  margin-bottom: 45px;
  font-size: 20px;
  padding: 14px;
  width: 200px;
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

.disabled-button {
  background-color: #c0c0c0 !important;
  border-color: #c0c0c0 !important;
  cursor: not-allowed;
}

.verification-button {
  margin-top: 15px;
  height: 30px;
  padding: 0 20px;
  font-size: 16px;
  border-radius: 8px;
  line-height: 48px;
  display: flex;
}

.password-strength {
  margin-top: 20px;
  font-size: 18px;
}

.high-strength {
  color: #67c23a;
  font-weight: bold;
}

.medium-strength {
  color: #e6a23c;
  font-weight: bold;
}

.low-strength {
  color: #f56c6c;
  font-weight: bold;
}

.merchant-link {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: #808080;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s ease;
}

.merchant-link:hover {
  color: #668B8B;
}

.login-link {
  position: absolute;
  bottom: 20px;
  right: 20px;
  color: #808080;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s ease;
}

.login-link:hover {
  color: #668B8B;
}

.night-mode-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
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
  background-color: #1f1f1f;
}

body.night-mode .register-content {
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


</style>
