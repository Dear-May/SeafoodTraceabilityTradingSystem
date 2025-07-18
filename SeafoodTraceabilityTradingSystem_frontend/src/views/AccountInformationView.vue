<template>
  <div class="account-management-page">
    <HeaderComponent/>
    <div class="container-fluid">
      <div class="row">
        <div class="d-flex flex-column flex-shrink-0 p-3 bg-body-tertiary sidebar"
             style="width: 280px; height: 115vh; background-color: #d3d3d3;">
          <AvatarChangComponent/>
          <hr>
          <el-menu
              active-text-color="#e02020"
              background-color="#d3d3d3"
              class="el-menu-vertical-demo"
              default-active="1-1"
              text-color="#303133"
              @open="handleOpen"
              @close="handleClose"
              style="border-radius: 10px;"
          >
            <el-sub-menu index="1">
              <template #title>
                <el-icon>
                  <user/>
                </el-icon>
                <span style="font-weight: bold; font-size: 18px;">账号管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="1-1" style="font-size: 16px; font-weight: bold;">
                  安全设置
                </el-menu-item>
                <el-menu-item index="1-2" style="font-size: 16px; font-weight: bold;" @click="handlePersonalSetting">
                  个性设置
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="2" @click="handleAddressManagement">
              <el-icon>
                <Location/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;">售后地址管理</span>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon>
                <document/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;">帮助中心</span>
            </el-menu-item>
            <el-menu-item index="4">
              <el-icon>
                <setting/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;" @click="handleForgetPassword">忘记密码</span>
            </el-menu-item>
          </el-menu>
          <template>
            <footer>
              <hr>
            </footer>
          </template>
        </div>
        <div class="col-9 main-content">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="账号信息" name="account-info">
              <div class="account-info">
                <h4 style="text-align: left; font-weight: bold;">登录账号</h4>
                <el-divider></el-divider>
                <div class="form-group mt-3" style="text-align: left">
                  <label for="name">
                    <a class="d-flex align-items-left link-body-emphasis text-decoration-none"
                       style="font-weight: bold;">用户名：<span>{{
                        UserInfoForm.name
                      }}</span></a>
                  </label>
                </div>
                <div class="form-group mt-3" style="text-align: left">
                  <label for="phone" class="mb-3">
                    <a style="font-weight: bold;">登录手机号</a>&nbsp;&nbsp;
                    <el-tag type="success" class="ml-3" v-if="UserInfoForm.phone">已验证</el-tag>
                    <el-tag type="danger" class="ml-3" v-else>未验证</el-tag>
                    &nbsp;&nbsp;
                    <el-tooltip
                        class="box-item"
                        effect="dark"
                        content="根据国家相关法律法规的要求，帐号最少要求绑定有一个手机号码。"
                        placement="right-start"
                    >
                      <i class="bi bi-question-circle"></i>
                    </el-tooltip>
                  </label>
                  <div class="d-flex align-items-center">
                    <input type="text" id="phone" class="form-control" style="width: 20%" disabled
                           v-model="UserInfoForm.changPhone">
                    <el-button type="primary" size="default" class="ms-3"
                               style="background-color: #e02020; border-color: #e02020;"
                               @click="showChangePhoneDialogVisible = true">
                      换绑手机号
                    </el-button>

                    <el-dialog
                        v-model="showChangePhoneDialogVisible"
                        title="账号安全验证"
                        width="600px"
                        :visible="showChangePhoneDialogVisible"
                    >
                      <div>
                        <a style="font-weight: bold">您正在进行敏感操作，继续操作前请验证您的身份</a>
                        <el-alert
                            title="更换号码后，您将无法通过【原手机号码+密码】登录"
                            type="warning"
                            :closable="false"
                            class="mt-3 mb-3"
                            style="font-size: 18px"
                        />
                      </div>
                      <div class="form-group mt-2">
                        <label for="password" class="mb-3"><a style="font-weight: bold">密码验证</a></label><br>
                        <el-input
                            type="password"
                            placeholder="请输入当前账号密码"
                            v-model="password_sms"
                            style="width: 100%; height: 50%"
                            show-password
                        />
                      </div>
                      <div class="form-group mt-3">
                        <label for="sms-code"><a style="font-weight: bold">短信验证</a></label>
                        <el-input v-model="smsForm.phone" id="phone" placeholder="手机号"
                                  class="custom-input mb-2"></el-input>
                        <div class="d-flex align-items-center">
                          <el-input
                              type="text"
                              placeholder="请输入短信验证码"
                              v-model="smsForm.code"
                              style="width: 70%;height: 50%"
                          />
                          <el-button
                              id="captcha-button"
                              type="primary"
                              :disabled="isButtonDisabled_sms"
                              @click="getMsgNum_sms"
                              class="sms-code-button"
                              style="width: 30%"
                          >
                            {{ buttonText_sms }}
                          </el-button>
                        </div>
                      </div>
                      <template #footer>
                        <div class="dialog-footer">
                          <el-button @click=clearForm_sms>取消</el-button>
                          <el-button type="primary" @click="validateNum_sms">下一步</el-button>
                        </div>
                      </template>
                    </el-dialog>
                  </div>
                </div>
                <div class="form-group mt-3" style="text-align: left">
                  <label for="phone" class="mb-3">
                    <a style="font-weight: bold;">登录邮箱</a>&nbsp;&nbsp;
                    <el-tag type="success" class="ml-3" v-if="UserInfoForm.email">已验证</el-tag>
                    <el-tag type="danger" class="ml-3" v-else>未验证</el-tag>
                  </label>
                  <div class="d-flex align-items-center">
                    <input type="text" id="email" class="form-control" style="width: 20%" disabled
                           v-model="UserInfoForm.email">
                    <el-button type="primary" size="default" class="ms-3"
                               style="background-color: #e02020; border-color: #e02020;"
                               @click="showChangeEmailDialogVisible = true">
                      换绑邮箱
                    </el-button>

                    <el-dialog
                        v-model="showChangeEmailDialogVisible"
                        title="账号安全验证"
                        width="600px"
                        :visible="showChangeEmailDialogVisible"
                    >
                      <div>
                        <a style="font-weight: bold">您正在进行敏感操作，继续操作前请验证您的身份</a>
                        <el-alert
                            title="更换号码后，您将无法通过【原邮箱+密码】登录"
                            type="warning"
                            :closable="false"
                            class="mt-3 mb-3"
                            style="font-size: 18px"
                        />
                      </div>
                      <div class="form-group mt-2">
                        <label for="password" class="mb-3"><a style="font-weight: bold">密码验证</a></label><br>
                        <el-input
                            type="password"
                            placeholder="请输入当前账号密码"
                            v-model="password_email"
                            style="width: 100%; height: 50%"
                            show-password
                        />
                      </div>
                      <div class="form-group mt-3">
                        <label for="sms-code"><a style="font-weight: bold">邮箱验证</a></label>
                        <el-input v-model="emailForm.email" id="email" placeholder="邮箱"
                                  class="custom-input mb-2"></el-input>
                        <div class="d-flex align-items-center">
                          <el-input
                              type="text"
                              placeholder="请输入邮箱验证码"
                              v-model="emailForm.code"
                              style="width: 70%;height: 50%"
                          />
                          <el-button
                              id="captcha-button"
                              type="primary"
                              :disabled="isButtonDisabled_email"
                              @click="getMsgNum_email"
                              class="sms-code-button"
                              style="width: 30%"
                          >
                            {{ buttonText_email }}
                          </el-button>
                        </div>
                      </div>
                      <template #footer>
                        <div class="dialog-footer">
                          <el-button @click="clearForm_email">取消</el-button>
                          <el-button type="primary" @click="validateNum_email">下一步</el-button>
                        </div>
                      </template>
                    </el-dialog>
                  </div>
                </div>
                <div class="form-group mt-3 mb-5" style="text-align: left">
                  <label for="phone" class="mb-3">
                    <a style="font-weight: bold;">登录密码</a>
                  </label>
                  <div class="d-flex align-items-center">
                    <input type="password" id="password" class="form-control" style="width: 20%" disabled
                           v-model="UserInfoForm.password">
                    <el-button type="primary" size="default" class="ms-3"
                               style="background-color: #e02020; border-color: #e02020;"
                               @click="showChangePasswordDialogVisible = true">更换密码
                    </el-button>
                    <el-dialog
                        v-model="showChangePasswordDialogVisible"
                        title="修改密码"
                        width="600px"
                        :visible="showChangePasswordDialogVisible"
                    >
                      <div>
                        <a style="font-weight: bold">修改密码后，您将无法通过【原密码】登录</a>
                      </div>
                      <div class="form-group mt-2">
                        <label for="old-password" class="mb-3"><a style="font-weight: bold">旧密码</a></label><br>
                        <el-input
                            type="password"
                            placeholder="请输入当前账号密码"
                            v-model="oldPassword"
                            style="width: 100%; height: 50%"
                            show-password
                        />
                      </div>
                      <div class="form-group mt-3">
                        <el-input v-model="NewPassword" type="password" placeholder="请输入新密码"
                                  @input="checkPasswordStrength" show-password class="custom-input"></el-input>
                        <div class="password-strength-wrapper">
                          <div class="password-strength-bar">
                            <div class="strength-segment"
                                 :class="{ 'low-strength-segment': passwordStrength === '弱' ,'medium-strength-segment': passwordStrength === '中' ,'high-strength-segment': passwordStrength === '高' }"></div>
                            <div class="strength-segment"
                                 :class="{ 'medium-strength-segment': passwordStrength === '中','high-strength-segment': passwordStrength === '高' }"></div>
                            <div class="strength-segment"
                                 :class="{ 'high-strength-segment': passwordStrength === '高' }"></div>
                          </div>
                          <span :class="passwordStrengthClass">{{ passwordStrength }}</span>
                        </div>
                        <el-input v-model="confirmPassword" type="password" placeholder="确认密码" show-password
                                  class="custom-input" @paste.prevent=""></el-input>
                      </div>
                      <template #footer>
                        <div class="dialog-footer">
                          <el-button @click="clearForm_password">取消</el-button>
                          <el-button type="primary" :disabled="!canCompleteRegistration"
                                     :class="{ 'disabled-button': !canCompleteRegistration }"
                                     @click="completeForgetPassword" block
                                     class="register-button">修改密码
                          </el-button>
                        </div>
                      </template>
                    </el-dialog>
                  </div>
                </div>
                <h4 style="text-align: left; font-weight: bold;">第三方账号绑定</h4>
                <el-divider></el-divider>
                <a style="text-align: center; font-weight: bold;">使用以下任一方式都可以登录到您的 海潮
                  帐号，避免由于某个帐号失效导致无法登录</a>
                <div class="binding-table">
                  <el-table :data="bindingInfo" style="width: 100%" class="mb-4">
                    <el-table-column prop="platform" label="平台" width="100"></el-table-column>
                    <el-table-column prop="id" label="绑定账号ID"></el-table-column>
                    <el-table-column prop="name" label="绑定账号名称"></el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                      <template #default="scope">
                        <el-tag type="success" v-if="scope.row.status === '使用中'">使用中</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150">
                      <template #default="scope">
                        <el-button type="danger" @click="unbindAccount(scope.row)" size="small"
                                   style="background-color: #e02020; border-color: #e02020;">解除绑定
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div
                      v-if="UserInfoForm.giteeid=== null || UserInfoForm.githubid === null || UserInfoForm.googleid=== null || UserInfoForm.wechatid=== null">
                    <a style="text-align: left; font-weight: bold;">你还可以绑定以下第三方帐号</a>
                    <div class="d-flex justify-content-between">
                      <div v-if="UserInfoForm.giteeid === null" class="align-items-center">
                        <GiteeLoginComponent/>
                        <br>
                        <span class="ms-3">Gitee</span>
                      </div>
                      <div v-if="UserInfoForm.githubid === null" class="align-items-center">
                        <GitHubLoginComponent/>
                        <br>
                        <span class="ms-3">GitHub</span>
                      </div>
                      <div v-if="UserInfoForm.googleid === null" class="align-items-center">
                        <GoogleLoginComponent/>
                        <br>
                        <span class="ms-3">Google</span>
                      </div>
                      <div v-if="UserInfoForm.wechatid === null" class="align-items-center">
                        <i class="bi bi-wechat" style="font-size: 30px; color: #e02020;"
                           @click="showChangeWechatDialogVisible = true"></i>
                        <el-dialog
                            v-model="showChangeWechatDialogVisible"
                            title="绑定微信"
                            width="600px"
                            :visible="showChangeWechatDialogVisible"
                        >
                          <div>
                            <a style="font-weight: bold">绑定微信后，您将可以通过微信登录海潮帐号</a>
                          </div>
                          <WechatLoginComponent/>
                          <template #footer>
                            <div class="dialog-footer">
                              <el-button @click="showChangeWechatDialogVisible = false">取消</el-button>
                            </div>
                          </template>
                        </el-dialog>
                        <br>
                        <span class="ms-3">微信</span>
                      </div>
                    </div>
                  </div>
                  <h4 style="text-align: left; font-weight: bold;" class="mt-5">注销账号</h4>
                  <el-divider></el-divider>
                  <div class="container">
                    <div class="row">
                      <div class="col-10">
                        <a style="text-align: left; font-weight: bold;">
                          注销 海潮 帐号是不可恢复的操作，你应自行备份 海潮
                          帐号相关的信息和数据。注销帐号后你将丢失该帐号自注册以来产生的数据和记录，注销后相关数据将不可恢复。</a>
                      </div>
                      <div class="col-2">
                        <el-button type="danger" @click="deleteUser()" size="large"
                                   style="background-color: #e02020; border-color: #e02020;">注销 海潮 帐号
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
  <RightWidgetComponent/>
  <FooterComponent/>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import router from "@/router";
import axios from 'axios';
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import AvatarChangComponent from "@/components/AvatarChangComponent.vue";
import {Document, Location, Setting, User} from "@element-plus/icons-vue";
import GiteeLoginComponent from "@/components/GiteeLoginComponent.vue";
import GitHubLoginComponent from "@/components/GitHubLoginComponent.vue";
import GoogleLoginComponent from "@/components/GoogleLoginComponent.vue";
import WechatLoginComponent from "@/components/WechatLoginComponent.vue";

const activeTab = ref('account-info');
const UserInfoForm = ref({
  id: '',
  name: '',
  avatar: '',
  nickname: '',
  email: '',
  phone: '',
  changPhone: '',
  giteeid: '',
  giteename: '',
  githubid: '',
  githubname: '',
  googleid: '',
  googlename: '',
  wechatid: '',
  wechatname: '',
  password: ''
})
const bindingInfo = ref([]);
const showChangePhoneDialogVisible = ref(false)

// 检测是否存在session，如果存在，填写用户信息
const userBaseInfoFromSession = JSON.parse(sessionStorage.getItem('userBaseInfo'));
if (userBaseInfoFromSession) {
  UserInfoForm.value.id = userBaseInfoFromSession.id;
  UserInfoForm.value.name = userBaseInfoFromSession.username;
  UserInfoForm.value.avatar = userBaseInfoFromSession.avatar;
  UserInfoForm.value.nickname = userBaseInfoFromSession.nickname;
} else {
  router.push('/');
}

async function bindAccountForm() {
  if (UserInfoForm.value.giteeid !== null) {
    const newAccount = {
      platform: 'Gitee',
      id: UserInfoForm.value.giteeid,
      name: UserInfoForm.value.giteename,
      status: '使用中',
      username: UserInfoForm.value.name,
    };
    bindingInfo.value.push(newAccount);
  }
  if (UserInfoForm.value.githubid !== null) {
    const newAccount = {
      platform: 'GitHub',
      id: UserInfoForm.value.githubid.toString(),
      name: UserInfoForm.value.githubname,
      status: '使用中',
      username: UserInfoForm.value.name,
    };
    bindingInfo.value.push(newAccount);
  }
  if (UserInfoForm.value.googleid !== null) {
    const newAccount = {
      platform: 'Google',
      id: UserInfoForm.value.googleid,
      name: UserInfoForm.value.googlename,
      status: '使用中',
      username: UserInfoForm.value.name,
    };
    bindingInfo.value.push(newAccount);
  }
  if (UserInfoForm.value.wechatid !== null) {
    const newAccount = {
      platform: '微信',
      id: UserInfoForm.value.wechatid,
      name: UserInfoForm.value.wechatname,
      status: '使用中',
      username: UserInfoForm.value.name,
    };
    bindingInfo.value.push(newAccount);
  }
}

async function makePhone() {
  if (UserInfoForm.value.phone.length >= 7) {
    return UserInfoForm.value.phone.substring(0, 3) + '****' + UserInfoForm.value.phone.substring(7, UserInfoForm.value.phone.length);
  }
  return UserInfoForm.value.phone;
}

async function fetchUserInfo() {
  const response = await axios.post('/api/user/findUserById', {
    id: UserInfoForm.value.id,
    name: UserInfoForm.value.name
  }, {
    headers: {
      'Content-Type': 'application/json'
    },
    withCredentials: true
  });
  if (response.data.id !== null) {
    UserInfoForm.value.phone = response.data.phone;
    UserInfoForm.value.email = response.data.email;
    UserInfoForm.value.giteeid = response.data.giteeid;
    UserInfoForm.value.giteename = response.data.giteename;
    UserInfoForm.value.githubid = response.data.githubid;
    UserInfoForm.value.githubname = response.data.githubname;
    UserInfoForm.value.googleid = response.data.googleid;
    UserInfoForm.value.googlename = response.data.googlename;
    UserInfoForm.value.wechatid = response.data.wechatid;
    UserInfoForm.value.wechatname = response.data.wechatname;
    UserInfoForm.value.password = response.data.password.substring(0, 12);
    UserInfoForm.value.changPhone = await makePhone();
    registerForm.value.phone = response.data.phone;
    await bindAccountForm();
  } else {
    ElMessage.error('获取用户信息失败，请重新登录');
  }
}

const password_sms = ref('');
const smsForm = ref({
  phone: '',
  code: ''
});
const wait_sms = ref(60);
const isButtonDisabled_sms = ref(false);
const buttonText_sms = ref('获取验证码');

watch(wait_sms, (newValue) => {
  if (newValue === 0) {
    isButtonDisabled_sms.value = false;
    buttonText_sms.value = '获取验证码';
    wait_sms.value = 60; // 重置计时器
  } else {
    buttonText_sms.value = `${newValue}秒后可以重新发送`;
  }
});

async function handleLogin(password) {
  try {
    const response = await axios.post('/api/user/login', {
      username: UserInfoForm.value.name,
      password: password,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      return true;
    } else {
      ElMessage.error('密码错误！请重新输入！');
      password_sms.value = '';
      smsForm.value.phone = '';
      smsForm.value.code = '';
      return false;
    }
  } catch (error) {
    console.error('Error during login:', error.message);
    return false;
  }
}

async function getMsgNum_sms() {
  if (smsForm.value.phone === UserInfoForm.value.phone) {
    ElMessage.error('请勿绑定相同的手机号码！');
  }
  const password_1 = password_sms.value;
  if (await handleLogin(password_1)) {
    setButtonStatus_sms(); // 设置按钮状态
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
}

// 设置按钮状态
function setButtonStatus_sms() {
  isButtonDisabled_sms.value = true;
  wait_sms.value = wait_sms.value > 0 ? wait_sms.value : 60; // 确保初始值有效
  countdown_sms();
}

// 计时功能
function countdown_sms() {
  if (wait_sms.value > 0) {
    setTimeout(() => {
      wait_sms.value -= 1;
      countdown_sms();
    }, 1000);
  }
}

function clearForm_sms() {
  password_sms.value = '';
  smsForm.value.phone = '';
  smsForm.value.code = '';
  isButtonDisabled_sms.value = false;
  showChangePhoneDialogVisible.value = false;
}

async function validateNum_sms() {
  if (smsForm.value.phone.length === 0 || smsForm.value.phone.length === 0) {
    ElMessage.error('请输入手机号码和验证码！');
    return;
  }
  const data = {
    verifyCode: smsForm.value.code,
    phone: smsForm.value.phone,
  };

  try {
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
    } else {
      await changPhone(smsForm.value.phone);
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
}

async function changPhone(phone) {
  const data = {
    id: UserInfoForm.value.id,
    phone: phone,
    name: UserInfoForm.value.name,
  };
  try {
    const response = await axios.post('/api/user/updateUserPhone', data, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success('手机号码修改成功！');
      window.location.reload();
    } else {
      ElMessage.error('手机号码修改失败，请稍后再试！');
    }
  } catch (error) {
    console.error("修改手机号码错误:", error);
  }
}

const showChangeEmailDialogVisible = ref(false);
const emailForm = ref({
  email: '',
  code: ''
});
const password_email = ref('');
const wait_email = ref(60);
const isButtonDisabled_email = ref(false);
const buttonText_email = ref('获取验证码');
watch(wait_email, (newValue) => {
  if (newValue === 0) {
    isButtonDisabled_email.value = false;
    buttonText_email.value = '获取验证码';
    wait_email.value = 60; // 重置计时器
  } else {
    buttonText_email.value = `${newValue}秒后可以重新发送`;
  }
});

async function getMsgNum_email() {
  if (emailForm.value.email === UserInfoForm.value.email) {
    ElMessage.error('请勿绑定相同的邮箱！');
  }
  const password_1 = password_email.value;
  if (await handleLogin(password_1)) {
    setButtonStatus_email(); // 设置按钮状态
    try {
      const response = await axios.post('/api/email/send', {
        email: emailForm.value.email,
      }, {
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
}

// 设置按钮状态
function setButtonStatus_email() {
  isButtonDisabled_email.value = true;
  wait_email.value = wait_email.value > 0 ? wait_email.value : 60; // 确保初始值有效
  countdown_email();
}

// 计时功能
function countdown_email() {
  if (wait_email.value > 0) {
    setTimeout(() => {
      wait_email.value -= 1;
      countdown_email();
    }, 1000);
  }
}

function clearForm_email() {
  password_email.value = '';
  emailForm.value.email = '';
  emailForm.value.code = '';
  isButtonDisabled_email.value = false;
  showChangeEmailDialogVisible.value = false;
}

async function validateNum_email() {
  if (emailForm.value.email.length === 0 || emailForm.value.code.length === 0) {
    ElMessage.error('请输入邮箱和验证码！');
    return;
  }
  const data = {
    verCode: emailForm.value.code,
    email: emailForm.value.email,
  };
  try {
    const response = await axios.post('/api/email/validateNum', data, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 400) {
      ElMessage.error('过期验证码已失效!');
    } else if (response.data.code === 500) {
      ElMessage.error('验证码错误，请重新输入！');
    } else {
      await changEmail(emailForm.value.email);
    }
  } catch (error) {
    console.error("验证错误:", error);
  }
}

async function changEmail(email) {
  const data = {
    id: UserInfoForm.value.id,
    email: email,
    name: UserInfoForm.value.name,
  };
  try {
    const response = await axios.post('/api/user/updateUserEmail', data, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success('邮箱修改成功！');
      window.location.reload();
    } else {
      ElMessage.error('邮箱修改失败，请稍后再试！');
    }
  } catch (error) {
    console.error("修改邮箱错误:", error);
  }
}

const showChangePasswordDialogVisible = ref(false);
const oldPassword = ref('');
const NewPassword = ref('');
const passwordStrength = ref('弱');
const confirmPassword = ref('');
const registerForm = ref({
  username: UserInfoForm.value.name,
  phone: '',
  email: '',
  password: ''
});

function checkPasswordStrength() {
  const passwordValue = NewPassword.value;
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

const canCompleteRegistration = computed(() => {
  return NewPassword.value === confirmPassword.value && passwordStrength.value !== '弱';
});

function clearForm_password() {
  passwordStrength.value = '弱';
  oldPassword.value = '';
  NewPassword.value = '';
  confirmPassword.value = '';
  showChangePasswordDialogVisible.value = false;
}

async function completeForgetPassword() {
  if (NewPassword.value === confirmPassword.value && passwordStrength.value !== '弱') {
    registerForm.value.password = NewPassword.value;
    try {
      const response = await axios.post('/api/user/forgetPassword', registerForm.value, {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      })
      console.log(response.data.code);
      if (response.data.code === 200) {
        ElMessage.success('密码修改成功！');
        window.location.reload();
      } else {
        ElMessage.error('密码修改失败，请稍后再试！');
      }
    } catch (error) {
      ElMessage.error('密码修改失败，请稍后再试！');
    }
  } else {
    ElMessage.error('密码不符合要求，请重新输入！');
  }
}

function handlePersonalSetting() {
  setTimeout(() => {
    router.push('/profile/account_profile');
  }, 500);
}

function handleAddressManagement() {
  setTimeout(() => {
    router.push('/profile/address');
  }, 500);
}

function handleForgetPassword() {
  setTimeout(() => {
    router.replace('/forgotPassword');
  }, 500);
}

const showChangeWechatDialogVisible = ref(false);

async function unbindAccount(account) {
  const response = await axios.post('/api/user/unbindAccount', account, {
    headers: {
      'Content-Type': 'application/json'
    },
    withCredentials: true
  })
  if (response.data.code === 200) {
    ElMessage({
      type: 'warning',
      message: `已成功解除绑定: ${account.platform}`,
    });
    bindingInfo.value = bindingInfo.value.filter(a => a !== account);
    setTimeout(() => {
      window.location.reload();
    }, 1000);
  } else {
    ElMessage.error('解除绑定失败，请稍后再试！');
  }
}

function deleteUser() {
  ElMessageBox.confirm('确认删除该用户？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
}

onMounted(async () => {
  await fetchUserInfo();
});
</script>

<style lang="less" scoped>
.account-management-page {
  font-family: Arial, sans-serif;
  display: flex;
  padding: 20px;
}

.sidebar {
  margin-top: 90px;
  background-color: #ffffff;
  padding: 20px;
  border-right: 1px solid #eaeaea;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  height: 70%;
}

.nav {
  list-style: none;
  padding: 0;
}

.nav-item {
  margin-bottom: 15px;
}

.nav-link {
  font-weight: bold;
  color: #e02020;
  text-decoration: none;
}

.nav-link.active {
  background-color: #e02020;
  color: white !important;
}

.sub-nav {
  list-style: none;
  padding-left: 20px;
}

.sub-nav li {
  margin-bottom: 5px;
}

.main-content {
  margin-top: 90px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-section {
  margin-bottom: 20px;
}

.order-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.form-group {
  margin-bottom: 20px;
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

.binding-table {
  margin-top: 20px;
}

.night-mode {
  background-color: #2e2e2e;
  color: #e0e0e0;
}

.night-mode .account-management-page {
  background-color: #2e2e2e;
}

.night-mode .row {
  background-color: #2e2e2e;
  color: #e0e0e0;
}

.night-mode .body {
  background-color: #2e2e2e;
}

.night-mode .header {
  background-color: #3c3c3c;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.night-mode .sidebar {
  background-color: #3c3c3c;
  border-right: 1px solid #444;
}

.night-mode .sidebar .el-menu-item {
  background-color: #3c3c3c;
  color: #e0e0e0;
}

.night-mode .sidebar.el-menu-item:hover {
  background-color: #4c4c4c;
}

.night-mode .main-content,
.night-mode .profile-section,
.night-mode .order-section,
.night-mode .logistics-section,
.night-mode .logistics-item,
.night-mode .profile-card,
.night-mode .promotion-card {
  background-color: #3c3c3c;
  color: #e0e0e0;
}

.night-mode .order-icon,
.night-mode .logistics-action {
  color: #ffa500;
}

.night-mode .right-widget {
  background-color: #3c3c3c;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.5);
}

.night-mode .footer {
  background-color: #2e2e2e;
  border-top: 1px solid #333;
}

.night-mode .footer-links a {
  color: #ffffff;
}

.night-mode .footer-links a:hover {
  color: #e02020;
}
</style>
