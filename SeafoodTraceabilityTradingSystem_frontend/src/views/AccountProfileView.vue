<template>
  <div class="account-management-page">
    <HeaderComponent/>
    <div class="container-fluid">
      <div class="row">
        <div class="d-flex flex-column flex-shrink-0 p-3 bg-body-tertiary sidebar"
             style="width: 280px; height: 75vh; background-color: #d3d3d3;">
          <AvatarChangComponent/>
          <hr>
          <el-menu
              active-text-color="#e02020"
              background-color="#d3d3d3"
              class="el-menu-vertical-demo"
              default-active="1-2"
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
                <el-menu-item index="1-1" style="font-size: 16px; font-weight: bold;" @click="handleAccountInfo">
                  安全设置
                </el-menu-item>
                <el-menu-item index="1-2" style="font-size: 16px; font-weight: bold;">
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
          <el-tabs
              v-model="activeName"
              type="card"
              class="demo-tabs"
              @tab-click="handleClick"
          >
            <el-tab-pane label="基本设置" name="account_information" @click="avatarUrl = ''">
              <div class="account-info">
                <h6 style="text-align: left; font-weight: bold; padding: 20px;">亲爱的 {{
                    UserInfoForm.name
                  }}，填写真实的资料，有助于好友找到你哦。</h6>
                <div class="form-group mt-4 row ml-4 mr-4" style=" padding-left: 20px;">
                  <div class="col-4 mt-5">
                    <label for="avatar" class="mb-3" style="font-weight: bold;">当前头像</label>
                  </div>
                  <div class="col-8 card-wrapper" style="text-align: left;" @click="activeName = 'avatar_image'">
                    <img :src="UserInfoForm.avatar" alt="User Avatar" class="profile-avatar"
                         style="width: 100px; height: 100px; border-radius: 8px; object-fit: cover;"
                         @mouseover="isHovering = true"
                         @mouseout="isHovering = false">
                    <div v-if="isHovering" class="edit-profile">更换头像</div>
                  </div>
                </div>
                <div class="form-group mt-4 row ml-4 mr-4" style="padding-left: 20px;">
                  <div class="col-4 mt-5">
                    <label for="nickname" class="mb-2" style="font-weight: bold;">昵称</label>
                  </div>
                  <div class="col-8" style="text-align: left;">
                    <input type="text" id="nickname" v-model="UserInfoForm.nickname" class="form-control mt-5"
                           style="width: 30%;">
                  </div>
                </div>
                <div class="row">
                  <div class="col-4 mt-5"></div>
                  <div class="col-8" style="text-align: left;">
                    <el-alert
                        class="mt-2"
                        title="1. 昵称设置成功后7天内不可修改；2. 不能填写违法、色情、赌博、暴力等不良信息。"
                        type="info"
                        :closable="false"
                        style="width: 70%;"
                        show-icon>
                    </el-alert>
                  </div>
                </div>
                <div class="form-group mt-4 row ml-4 mr-4" style="padding-left: 20px;">
                  <div class="col-4 mt-5">
                    <label for="gender" class="mb-3" style="font-weight: bold;">性别</label>
                  </div>
                  <div class="col-8 mt-5" style="text-align: left; ">
                    <el-radio-group v-model="UserInfoForm.gender">
                      <el-radio label="male">男</el-radio>
                      <el-radio label="female">女</el-radio>
                      <el-radio label="null">保密</el-radio>
                    </el-radio-group>
                  </div>
                </div>
                <div class="form-group mt-4" style="padding-left: 20px;">
                  <el-button type="primary" size="default" class="mt-3"
                             style="background-color: #e02020; border-color: #e02020; width: 30%; height: 40px;"
                             @click="handleSave">保存
                  </el-button>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="头像图片" name="avatar_image">
              <div class="avatar-upload-container">
                <div class="avatar-preview">
                  <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview" class="avatar-preview-image"/>
                  <div v-else class="avatar-placeholder">
                    <span>头像预览</span>
                  </div>
                </div>
                <div class="avatar-actions">
                  <div class="preview-sizes">
                    <h4>预览</h4>
                    <div class="preview-size">
                      <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview 100x100" width="100" height="100"
                           style="border-radius: 50%"/>
                      <div v-else class="avatar-placeholder-small"></div>
                      <span>100 x 100</span>
                    </div>
                    <div class="preview-size">
                      <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview 50x50" width="50" height="50"
                           style="border-radius: 50%"/>
                      <div v-else class="avatar-placeholder-small"></div>
                      <span>50 x 50</span>
                    </div>
                  </div>
                  <div class="avatar-edit-buttons">
                    <el-button type="primary" @click="triggerFileInput">
                      <i class="bi bi-upload"></i>&nbsp;上传
                    </el-button>
                    <el-button type="danger" @click="resetAvatar">
                      <i class="bi bi-x-circle"></i>&nbsp;重置
                    </el-button>
                    <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*"
                           style="display: none"/>
                  </div>
                </div>
              </div>
              <div class="dialog-footer">
                <el-button @click="resetAvatar" style="width: 10%;">取消
                </el-button>
                <el-button type="primary" @click=uploadAvatar
                           style="width: 10%; background-color: #e02020; border-color: #e02020; color: #fff;">
                  确认
                </el-button>
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
import {ref} from 'vue';
import router from "@/router";
import axios from "axios";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import AvatarChangComponent from "@/components/AvatarChangComponent.vue";
import {Document, Location, Setting, User} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const UserInfoForm = ref({
  id: '',
  name: '',
  avatar: '',
  nickname: '',
  changTime: '',
  gender: '',
})

// 检测是否存在session，如果存在，填写用户信息
const userBaseInfoFromSession = JSON.parse(sessionStorage.getItem('userBaseInfo'));
const nickName = ref();
const Gender = ref();
if (userBaseInfoFromSession) {
  UserInfoForm.value.id = userBaseInfoFromSession.id;
  UserInfoForm.value.name = userBaseInfoFromSession.username;
  UserInfoForm.value.avatar = userBaseInfoFromSession.avatar;
  UserInfoForm.value.nickname = userBaseInfoFromSession.nickname;
  nickName.value = userBaseInfoFromSession.nickname;
} else {
  router.push('/');
}

async function getUserInfo() {
  try {
    const response = await axios.post('/api/user/getUserOtherInfo', {
      id: userBaseInfoFromSession.id,
      name: userBaseInfoFromSession.username,
    });
    if (response.data.changTime !== null) {
      UserInfoForm.value.changTime = response.data.changTime;
      UserInfoForm.value.gender = response.data.gender;
      Gender.value = response.data.gender;
    } else {
      ElMessage.error("获取用户信息失败");
    }
  } catch (error) {
    ElMessage.error("获取用户信息失败");
  }
}

getUserInfo();

const isHovering = ref(false);
const activeName = ref('account_information');

function handleClick(tab, event) {
  console.log(tab, event);
  activeName.value = tab.name;
}

async function handleSave() {
  if (UserInfoForm.value.changTime !== null && !((new Date().getTime() - new Date(UserInfoForm.value.changTime).getTime()) > 7 * 24 * 60 * 60 * 1000) && UserInfoForm.value.nickname !== nickName.value
  ) {
    const time1 = new Date(UserInfoForm.value.changTime).getTime() - new Date(UserInfoForm.value.changTime).getTime();
    ElMessage.error("距离上次修改时间过短，请等待" + Math.floor((7 * 24 * 60 * 60 * 1000 - time1) / (24 * 60 * 60 * 1000)) + "天后再修改")
  } else {
    try {
      const response = await axios.post('/api/user/updateUserOtherInfo', {
        id: UserInfoForm.value.id,
        name: UserInfoForm.value.name,
        nickname: UserInfoForm.value.nickname,
        gender: UserInfoForm.value.gender,
      }, {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      });
      if (response.data.code === 200) {
        ElMessage.success("修改成功");
        // 更新session
        sessionStorage.setItem('userBaseInfo', JSON.stringify({
          id: userBaseInfoFromSession.id,
          username: userBaseInfoFromSession.username,
          avatar: userBaseInfoFromSession.avatar,
          nickname: UserInfoForm.value.nickname,
        }));
        window.location.reload();
      } else if (response.data.code === 401) {
        ElMessage.error("昵称带有违禁词，请重新修改!!!");
      } else if (response.data.code === 402) {
        ElMessage.error("修改时间过短，请等待");
      } else {
        ElMessage.error("修改失败");
      }
    } catch (error) {
      ElMessage.error("修改失败");
    }
  }
}

const avatarUrl = ref('');
const fileInput = ref(null);
const MAX_FILE_SIZE = 3 * 1024 * 1024;
const errorMessage = ref('');

const triggerFileInput = () => {
  fileInput.value.click();
};

const handleFileChange = (event) => {
  const file = event.target.files[0];

  if (file) {
    // 检测文件是否为图片
    if (!file.type.startsWith('image/')) {
      ElMessage({
        type: 'error',
        message: '请选择图片文件（如 jpg、png 格式）',
      });
      // 重置文件选择框
      resetAvatar();
      return;
    }

    // 检测文件大小是否超过 3MB
    if (file.size > MAX_FILE_SIZE) {
      ElMessage({
        type: 'error',
        message: '文件大小不能超过 3MB',
      });
      // 重置文件选择框
      resetAvatar();
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      avatarUrl.value = e.target.result;
    };
    reader.onerror = () => {
      ElMessage({
        type: 'error',
        message: '文件读取失败，请重试',
      });
      resetAvatar();
    };
    reader.readAsDataURL(file);
  }
};

const resetAvatar = () => {
  avatarUrl.value = '';
  fileInput.value.value = null;
};

async function uploadAvatar() {
  const file = fileInput.value.files[0];
  if (!file) {
    errorMessage.value = '请选择一个图片文件';
    return;
  }
  const formData = new FormData();
  formData.append('avatar', file);
  formData.append('username', UserInfoForm.value.name)
  const response = await axios.post('/api/user/upload-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    withCredentials: true
  })
  if (response.data.code === 200) {
    ElMessage.success('头像上传成功');
    sessionStorage.setItem('userBaseInfo', JSON.stringify({
      id: UserInfoForm.value.id,
      username: UserInfoForm.value.name,
      avatar: "https://oss.yy0313.fit/UserAvatar/" + UserInfoForm.value.name + ".jpg",
      nickname: UserInfoForm.value.nickname,
    }));
    setTimeout(() => {
      window.location.reload();
    }, 1000);
  } else {
    ElMessage.error('头像上传失败，请重试');
  }
}

function handleAccountInfo() {
  setTimeout(() => {
    router.push('/profile/account_information');
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

.card-wrapper {
  position: relative;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  position: relative;
  z-index: 2;
  cursor: pointer;
}

.edit-profile {
  position: absolute;
  top: 73%;
  width: 11%;
  font-size: 12px;
  color: white;
  padding: 5px;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex; /* 用于文字垂直居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  pointer-events: none;
  z-index: 3;
  opacity: 0.8;
}

.avatar-upload-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-preview {
  width: 250px;
  height: 250px;
  border-radius: 50%;
  border: 2px dashed #dcdcdc;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background-color: #f5f5f5;
  margin-top: 30px;
  margin-left: 30%;
  margin-bottom: 30px;
}

.avatar-placeholder {
  font-size: 18px;
  color: #888;
  text-align: center;
}

.avatar-preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-right: 30%;
}

.preview-sizes {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-size {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-placeholder-small {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #dcdcdc;
}

.avatar-edit-buttons {
  display: flex;
  gap: 10px;
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
