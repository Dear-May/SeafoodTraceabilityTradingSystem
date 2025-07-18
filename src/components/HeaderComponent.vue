<template>
  <header class="p-3 mb-3 border-bottom header">
    <div class="container-fluid">
      <div class="row">
        <div class="col-3 d-flex align-items-center justify-content-start">
          <a href="/" class="d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none">
            <img src="../images/logo.png" alt="Haichao" class="bi me-2" width="50" height="50" role="img"
                 aria-label="Haichao">
          </a>
          <nav class="nav">
            <a @click="handleMyProfileClick" class="nav-link px-2 link-secondary">我的海潮</a>
            <a @click="handleIndex" class="nav-link px-2 link-emphasis">首页</a>
            <a href="#" class="nav-link px-2 link-body-emphasis">帮助中心</a>
          </nav>
        </div>

        <div class="col-6 d-flex align-items-center justify-content-center">
          <div class="search-bar">
            <el-dropdown>
                <span class="el-dropdown-link search-select">
                  {{ searchCategory === 'product' ? '商品' : '商家' }}
                  <el-icon class="el-icon--right">
                    <arrow-down/>
                  </el-icon>
                </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="setSearchCategory('product')">商品</el-dropdown-item>
                  <el-dropdown-item @click="setSearchCategory('store')">商家</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-input
                placeholder="商品/店铺/品牌"
                v-model="searchQuery"
                @keyup.enter="searchProduct(searchQuery)"
                clearable
                class="search-input"
            />
            <el-button type="primary" icon="el-icon-search" class="search-button" @click="searchProduct(searchQuery)">搜索
            </el-button>
          </div>
        </div>

        <div class="col-3 d-flex align-items-center justify-content-end" style="margin-left: auto;">
          <template v-if="UserInfoForm.id">
            <div class="weather-info d-flex align-items-center">
              <i class="bi bi-geo-alt-fill me-2" style="font-size: 20px;"></i>
              <span>{{ city }}</span>
              <i class="bi bi-cloud-sun-fill ms-4" style="font-size: 20px;" v-if="weather==='晴'"></i>
              <i class="bi bi-cloud-fog-fill ms-4" style="font-size: 20px;" v-if="weather==='阴'"></i>
              <i class="bi bi-cloud-rain-fill ms-4" style="font-size: 20px;" v-if="weather==='雨'"></i>
              <i class="bi bi-cloud-haze-fill ms-4" style="font-size: 20px;" v-if="weather==='雾'"></i>
              <i class="bi bi-cloud-sun-rain-fill ms-4" style="font-size: 20px;" v-if="weather==='多云'"></i>
              <i class="bi bi-cloud ms-4" style="font-size: 20px;" v-if="weather==='霾'"></i>
              <div class="d-flex flex-column ms-2">
                <span>{{ weather }}</span>
                <span>{{ temperature }}°C</span>
              </div>
            </div>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <nav class="nav">
              <a href="#" class="nav-link px-2 link-secondary" @click="handleIndex">主页</a>
              <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
            </nav>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <div class="dropdown text-end margin-right-10">
              <a href="#" class="d-block link-body-emphasis text-decoration-none dropdown-toggle"
                 data-bs-toggle="dropdown" aria-expanded="false">
                <img :src="UserInfoForm.avatar" alt="mdo" width="40" height="40" class="rounded-circle">
              </a>
              <ul class="dropdown-menu text-small">
                <li><a class="dropdown-item" @click="handleMyProfileClick">我的海潮</a></li>
                <li>
                  <el-badge :value="unreadMessageCount" class="item">
                    <a class="dropdown-item" @click="handleMyMessage">我的消息</a>
                  </el-badge>
                </li>
                <li><a class="dropdown-item" href="#">设置</a></li>
                <li>
                  <hr class="dropdown-divider">
                </li>
                <li><a class="dropdown-item" @click="logout">登出账号</a></li>
              </ul>
            </div>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import {ArrowDown} from "@element-plus/icons-vue";
import router from "@/router";
import {defineExpose, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import useUser from "@/composables/useUser";
import axios from "axios";

const {UserInfoForm, initUserSession} = useUser()

const searchCategory = ref('product');
const setSearchCategory = (category) => {
  searchCategory.value = category;
};
const searchQuery = ref('');

function searchProduct(searchText) {
  router.push('/search?searchText=' + searchText);
}

const city = localStorage.getItem('city');
const weather = localStorage.getItem('weather');
const temperature = localStorage.getItem('temperature');

const handleMyProfileClick = () => {
  router.push('/mySpace');
};

const handleMyMessage = () => {
  router.push('/talkToStore');
}

function handleIndex() {
  router.push('/');
}

function handleTalk() {
  router.push('/talk');
}

const unreadMessageCount = ref(0);

async function getUnreadMessageCount() {
  try {
    const response = await axios.post('/api/chat/getUnreadMessageCount', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.count !== 0)
      unreadMessageCount.value = response.data.count;
  } catch (err) {
    ElMessage.error('获取未读消息失败');
  }
}

defineExpose({getUnreadMessageCount});

function logout() {
  sessionStorage.removeItem('userBaseInfo');
  //清除cookie
  const cookies = document.cookie.split("; ");
  for (let cookie of cookies) {
    const eqPos = cookie.indexOf("=");
    const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
  }
  ElMessage.success('退出成功');
  setTimeout(() => {
    router.push('/');
  }, 1500);
}

onMounted(async () => {
  initUserSession();
  if (UserInfoForm.value.id) {
    await getUnreadMessageCount();
  }
});
</script>

<style scoped lang="less">
.header {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1001;
  background-color: #ffffff; // 设置背景色确保内容可见
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); // 添加阴影使其更明显
  transition: all 0.3s ease; // 添加过渡效果
}

.header.nav.a:hover {
  color: #e02020;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 800px;
  width: 100%;
  border: 2px solid #e02020;
  border-radius: 50px;
  overflow: hidden;
}

.search-select {
  cursor: pointer;
  padding: 0 15px;
  display: flex;
  align-items: center;
  border-right: 1px solid #e02020;
  background-color: transparent;
  font-size: 16px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  height: 50px;
  font-size: 16px;
  box-shadow: none;
  background-color: transparent;
}

.search-button {
  height: 50px;
  background-color: #e02020;
  color: #ffffff;
  border: none;
  border-radius: 0;
  padding: 0 30px;
  font-size: 16px;
}

.el-tooltip__trigger:focus-visible {
  outline: unset;
}
</style>
