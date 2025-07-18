<template>
  <el-header>
    <OceanWaveLogo ref="logoComponent"></OceanWaveLogo>
  </el-header>
  <div class="login-container">
    <div class="steps-wrapper">
      <el-steps :active="active" align-center class="full-width-steps">
        <el-step title="Step 1" description="确认身份"/>
        <el-step title="Step 2" description="验证手机号"/>
        <el-step title="Step 3" description="填写商店信息" v-if="registerForm.role === 'merchant'"/>
        <el-step title="Step 3" description="填写加入信息" v-else-if="registerForm.role === 'staff'"/>
        <el-step title="Step 3" description="请选择身份" v-else/>
      </el-steps>
    </div>

    <!-- 步骤一 -->
    <transition name="fade" mode="out-in" v-if="active === 0">
      <div class="role-selection" key="step-0">
        <div class="role-card" @click="selectRole('merchant')" :class="{'active': registerForm.role === 'merchant'}">
          <div class="role-icon merchant-icon"></div>
          <h3>我是商家</h3>
          <p>开设您的店铺，管理商品和订单。</p>
        </div>
        <div class="role-card" @click="selectRole('staff')" :class="{'active': registerForm.role === 'staff'}">
          <div class="role-icon staff-icon"></div>
          <h3>我是店员</h3>
          <p>协助商家管理店铺，处理日常工作。</p>
        </div>
      </div>
    </transition>

    <!-- 步骤二 -->
    <transition name="fade" mode="out-in" v-else-if="active === 1">
      <div class="phone-verification" key="step-1">
        <el-form :model="registerForm" label-width="100px" class="form-content">
          <el-form-item label="手机号">
            <el-input v-model="registerForm.phone" placeholder="请输入您的手机号"></el-input>
          </el-form-item>
          <el-form-item label="验证码">
            <div class="verification-wrapper">
              <el-input v-model="registerForm.code" placeholder="请输入验证码" class="code-input"></el-input>
              <el-button type="primary" @click="CheckPhoneNumber" class="send-code-btn" :disabled="isButtonDisabled">
                {{ buttonText }}
              </el-button>
            </div>
          </el-form-item>
          <Vcode :show="isShow" @success="onSuccess" @close="onClose"/>
          <el-form-item>
            <el-button type="primary" @click="validateNum" v-if="!isAccessed" size="large">验证手机号</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>

    <!-- 步骤三 -->
    <transition name="fade" mode="out-in" v-else-if="active === 2">
      <div class="shop-info" key="step-2">
        <el-form :model="registerForm" label-width="100px" class="form-content">
          <!-- 商家填写信息 -->
          <div v-if="registerForm.role === 'merchant'">
            <el-form-item label="店铺名称">
              <el-input v-model="registerForm.shopName" placeholder="请输入店铺名称"></el-input>
            </el-form-item>
            <el-form-item label="用户名称">
              <el-input v-model="registerForm.userName" placeholder="请输入用户名称"></el-input>
            </el-form-item>
            <el-form-item label="店铺LOGO">
              <el-upload
                  class="avatar-uploader"
                  accept="image/*"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload">
                <img v-if="registerForm.avatarUrl" :src="registerForm.avatarUrl" class="avatar" alt=""/>
                <el-icon v-else class="avatar-uploader-icon">
                  <plus/>
                </el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="registerForm.description" type="textarea" placeholder="请输入简介"
                        resize="none"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
          </div>

          <!-- 店员填写信息 -->
          <div v-else-if="registerForm.role === 'staff'">
            <el-form-item label="用户名称">
              <el-input v-model="registerForm.userName" placeholder="请输入用户名称"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="所属商店">
              <el-input v-model="registerForm.shopId" placeholder="请输入所属商店id"></el-input>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </transition>

    <!-- 按钮区域 -->
    <div class="form-wrapper">
      <el-button class="step-btn" v-if="active===0" size="large" @click="()=> {router.push('/store/login')}">返回登录
      </el-button>
      <el-button class="step-btn" @click="lastStep" v-if="active > 0" size="large">重新选择身份</el-button>
      <el-button class="step-btn" @click="next" v-if="active < 2" size="large">下一步</el-button>
      <el-button class="step-btn" v-else-if="active === 2" @click="finishRegister" size="large">完成注册</el-button>
    </div>
  </div>
  <!--  <FooterComponent/>-->
</template>
<script setup>
import OceanWaveLogo from "@/components/OceanWaveLogoComponent.vue";
import {onMounted, ref, watch} from "vue";
import FooterComponent from "@/components/FooterComponent.vue";
import {ElMessage} from "element-plus";
import {Plus} from "@element-plus/icons-vue";
import Vcode from "vue3-puzzle-vcode";
import axios from "axios";
import router from "@/router";

const logoComponent = ref(null);
const active = ref(0);

function lastStep() {
  registerForm.value = {
    shopId: "",
    role: "",
    phone: "",
    userName: "",
    shopName: "",
    email: "",
    description: "",
    code: "",
    avatarUrl: "",
  };
  active.value = 0;
}

const next = () => {
  if (active.value === 0) {
    if (registerForm.value.role === "") {
      ElMessage.error("请选择身份");
      return;
    }
  } else if (active.value === 1) {
    if (!isAccessed.value) {
      ElMessage.error("请先验证手机号！");
      return;
    }
  }
  if (active.value++ > 2) active.value = 0;
};

const registerForm = ref({
  shopId: "",
  role: "",
  phone: "",
  userName: "",
  shopName: "",
  email: "",
  description: "",
  code: "",
  avatarUrl: "",
});

const selectRole = (role) => {
  registerForm.value.role = role;
};

function initLogo() {
  logoComponent.value.setTitle("商家注册");
}

const isShow = ref(false);
const wait = ref(60);
const isButtonDisabled = ref(false);
const buttonText = ref("获取验证码");
const isAccessed = ref(false);
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
  if (!registerForm.value.phone) {
    ElMessage.error("请输入手机号码！");
  } else if (!/^1[3-9]\d{9}$/.test(registerForm.value.phone)) {
    ElMessage.error("请输入正确的手机号码！");
  }
  isAccessed.value = false;
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
          phoneNumber: registerForm.value.phone,
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

// 验证验证码
const validateNum = async () => {
  if (!registerForm.value.phone || !registerForm.value.code) {
    ElMessage.error("请输入手机号码和验证码！");
    return;
  }
  const data = {
    verifyCode: registerForm.value.code,
    phone: registerForm.value.phone,
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
      ElMessage.error("用户不存在，请注册账号！");
    } else {
      ElMessage.success("验证成功！");
      isAccessed.value = true;
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
};

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    return false;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    registerForm.value.avatarUrl = e.target.result;
  };
  reader.readAsDataURL(file);

  return false; // 阻止默认上传行为
};

async function finishRegister() {
  if (registerForm.value.role === "") {
    ElMessage.error("请选择身份！");
    return;
  } else if (registerForm.value.phone === "") {
    ElMessage.error("请输入手机号码！");
    return;
  } else {
    if (registerForm.value.role === "merchant") {
      if (registerForm.value.shopName === "") {
        ElMessage.error("请输入店铺名称！");
        return;
      }
      if (registerForm.value.userName === "") {
        ElMessage.error("请输入用户名称！");
        return;
      }
      if (registerForm.value.email === "") {
        ElMessage.error("请输入邮箱！");
        return;
      }
      if (registerForm.value.avatarUrl === "") {
        ElMessage.error("请上传店铺LOGO！");
        return;
      }
    } else if (registerForm.value.role === "staff") {
      if (registerForm.value.userName === "") {
        ElMessage.error("请输入用户名称！");
        return;
      }
      if (registerForm.value.email === "") {
        ElMessage.error("请输入邮箱！");
        return;
      }
      if (registerForm.value.shopId === "") {
        ElMessage.error("请输入所属商店id！");
        return;
      }
    }
  }
  const re = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (!re.test(registerForm.value.email)) {
    ElMessage.error("请输入正确的邮箱格式！")
    return;
  }
  if (registerForm.value.role === "staff") {
    try {
      const response = await axios.post("/api/shop/user/isAccessShop", {
        shopId: registerForm.value.shopId
      }, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      if (response.data.code === 404) {
        ElMessage.error("该商店不存在或未通过审核！")
        return;
      }
    } catch (err) {
      ElMessage.error("获取商店信息失败！")
      return;
    }
  }
  try {
    const response = await axios.post("/api/shop/user/register", registerForm.value, {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });
    if (response.data.code === 200) {
      if (registerForm.value.role === "merchant") {
        ElMessage.success("注册成功，请登录账号，并完善店铺信息！");
        setTimeout(() => {
          window.location.href = "/store/login";
        }, 1500);
      } else if (registerForm.value.role === "staff") {
        ElMessage.success("注册成功，信息已提交，请等待店主审核！")
        setTimeout(() => {
          window.location.href = "/store/login";
        }, 1500);
      }
    } else if (response.data.code === 400) {
      ElMessage.error("注册失败，请检查输入信息！");
    } else if (response.data.code === 403) {
      ElMessage.error("注册失败，该手机号已被注册！");
    }
  } catch (error) {
    console.error("注册失败:", error);
  }
}

onMounted(() => {
  initLogo();
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 90vh;
  width: 100%;
}

.steps-wrapper {
  position: absolute;
  top: 20%;
  width: 75vw;
  display: flex;
  justify-content: center;
  align-items: center;
}

.full-width-steps {
  width: 100%;
}

.role-selection {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 3rem;
}

.role-card {
  width: 280px;
  height: 30vh;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  padding: 2rem;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
}

.role-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
}

.role-card.active {
  border: 2px solid #0077b6;
}

.role-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1rem;
  background-size: cover;
}

.merchant-icon {
  background-image: url("@/images/merchant-icon.png");
}

.staff-icon {
  background-image: url("@/images/staff-icon.png");
}

.dots {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.5rem;
}

.dots::before, .dots::after, .dots span {
  content: "";
  width: 8px;
  height: 8px;
  background-color: #d0d0d0;
  border-radius: 50%;
}

.step-btn {
  margin-top: 12px;
  padding: 12px 20px;
  background-color: #0077b6;
  color: #fff;
  font-weight: bold;
  border-radius: 8px;
  transition: background 0.3s;

  &:hover {
    background-color: #005f87;
  }
}

.phone-verification {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
  width: 100%;

  h2 {
    font-size: 1.8rem;
    color: #333;
    margin-bottom: 1.5rem;
    font-weight: bold;
    text-align: center;
  }

  .form-content {
    width: 450px;
    background: #ffffff;
    padding: 2.5rem;
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease-in-out;

    &:hover {
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
      transform: translateY(-5px);
    }

    .el-form-item {
      margin-bottom: 1.5rem;

      label {
        font-size: 1rem;
        color: #444;
        font-weight: 600;
      }

      .el-input {
        :deep(.el-input__wrapper) {
          border-radius: 12px;
          box-shadow: 0 0 0 2px #e0e0e0;
          transition: box-shadow 0.2s ease;

          &:hover,
          &:focus {
            box-shadow: 0 0 0 2px #0077b6;
          }
        }

        input {
          font-size: 1rem;
          color: #333;
        }
      }
    }

    .verification-wrapper {
      display: flex;
      align-items: center;
      gap: 1rem;

      .code-input {
        flex: 1;

        :deep(.el-input__wrapper) {
          border-radius: 12px;
        }
      }

      .send-code-btn {
        background-color: #0077b6;
        color: #fff;
        border-radius: 12px;
        font-size: 1rem;
        padding: 0.8rem 1.2rem;
        transition: background 0.3s, transform 0.2s;

        &:hover {
          background-color: #005f87;
          transform: scale(1.05);
        }
      }
    }

    .verify-btn {
      width: 100%;
      background-color: #0077b6;
      color: #ffffff;
      border-radius: 12px;
      font-size: 1.1rem;
      font-weight: 600;
      padding: 0.8rem 1.2rem;
      transition: background 0.3s, box-shadow 0.3s;

      &:hover {
        background-color: #005f87;
        box-shadow: 0 5px 15px rgba(0, 119, 182, 0.3);
      }
    }
  }
}

.shop-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 2rem;
  width: 100%;

  .form-content {
    width: 500px;
    background-color: #ffffff;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
    }

    .el-form-item {
      margin-bottom: 1.5rem;

      label {
        font-size: 1rem;
        color: #444;
        font-weight: 600;
      }

      .el-input {
        :deep(.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 2px #e0e0e0;
          transition: box-shadow 0.2s ease;

          &:hover,
          &:focus {
            box-shadow: 0 0 0 2px #0077b6;
          }
        }

        input {
          font-size: 1rem;
          color: #333;
        }
      }
    }

    .avatar-uploader {
      display: flex;
      justify-content: center;
      align-items: center;

      .avatar {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #d0d0d0;
      }

      .avatar-uploader-icon {
        font-size: 2rem;
        color: #ccc;
        border: 1px dashed #ccc;
        border-radius: 50%;
        width: 100px;
        height: 100px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: border 0.3s, color 0.3s;

        &:hover {
          border: 1px dashed #0077b6;
          color: #0077b6;
        }
      }
    }

    .el-textarea {
      :deep(.el-textarea__inner) {
        border-radius: 8px;
        box-shadow: 0 0 0 2px #e0e0e0;

        &:hover,
        &:focus {
          box-shadow: 0 0 0 2px #0077b6;
        }
      }
    }

    .el-button {
      background-color: #0077b6;
      color: #ffffff;
      font-size: 1rem;
      padding: 0.8rem 1.2rem;
      border-radius: 8px;
      transition: background 0.3s, box-shadow 0.3s;

      &:hover {
        background-color: #005f87;
        box-shadow: 0 5px 15px rgba(0, 119, 182, 0.3);
      }
    }
  }
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
