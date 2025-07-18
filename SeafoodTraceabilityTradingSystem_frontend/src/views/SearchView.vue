<template>
  <div class="homepage" :class="{ 'night-mode': isNightMode }">
    <header class="p-3 mb-3 border-bottom header">
      <div class="container-fluid">
        <div class="row">
          <div class="col-2 d-flex align-items-center justify-content-start">
            <a href="/" class="d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none">
              <img src="../images/logo.png" alt="Bootstrap" class="bi me-2" width="50" height="50" role="img"
                   aria-label="Bootstrap">
            </a>
            <nav class="nav">
              <a href="/" class="nav-link px-2 link-secondary">主页</a>
              <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
            </nav>
          </div>

          <div class="col-6 d-flex align-items-center justify-content-center" v-if="!isScrolled">
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

          <div class="col d-flex align-items-center justify-content-center" v-if="isScrolled">
            <div class="search-bar">
              <el-dropdown>
                <span class="el-dropdown-link search-select">
                  {{ searchCategory === 'product' ? '商品' : '商家' }}
                  <el-icon class="el-icon-right">
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
                  placeholder="京东搜索 商品/店铺/品牌"
                  v-model="searchQuery"
                  @keyup.enter="searchProduct(searchQuery)"
                  clearable
                  class="search-input"
              />
              <el-button type="primary" icon="el-icon-search" class="search-button" @click="searchProduct(searchQuery)">
                搜索
              </el-button>
            </div>
          </div>

          <div class="col-4 d-flex align-items-center justify-content-end" style="margin-left: auto;">
            <template v-if="isScrolled">
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
            </template>
            <template v-if="UserInfoForm.id">
              <nav class="nav">
                <a href="#" class="nav-link px-2 link-secondary">主页</a>
                <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
                <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
                <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
                <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
                <a href="#" class="nav-link px-2 link-body-emphasis" @click="handleTalk">讨论</a>
              </nav>
              &nbsp;&nbsp;&nbsp;&nbsp;
              <div class="dropdown text-end margin-right-10">
                <a href="#" class="d-block link-body-emphasis text-decoration-none dropdown-toggle"
                   data-bs-toggle="dropdown" aria-expanded="false">
                  <img :src="UserInfoForm.avatar" alt="mdo" width="40" height="40" class="rounded-circle">
                </a>
                <ul class="dropdown-menu text-small">
                  <li><a class="dropdown-item" @click="handleMySpace">我的海潮</a></li>
                  <el-badge :value="0" class="item">
                    <li><a class="dropdown-item" @click="handleMyMessage">我的消息</a></li>
                  </el-badge>
                  <li><a class="dropdown-item" href="#">设置</a></li>
                  <li>
                    <hr class="dropdown-divider">
                  </li>
                  <li><a class="dropdown-item" @click="logout">登出账号</a></li>
                </ul>
              </div>
            </template>
            <template v-else>
              <el-button type="primary" class="me-2 login-button" @click="handleLogin">登录</el-button>
              <el-button type="primary" class="register-button" @click="handleRegister">免费注册</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>

    <div class="search-bar-section" style="margin-top: 80px;">
      <div class="container">
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
              placeholder="京东搜索 商品/店铺/品牌"
              v-model="searchQuery"
              @keyup.enter="searchProduct(searchQuery)"
              clearable
              class="search-input"
          >
            <template #append>
              <el-button @click="showImageSearchModal = true" style="background-color: transparent;">
                <i class="bi bi-camera-fill" style="font-size: 20px"></i>
              </el-button>
              <el-dialog v-model="showImageSearchModal" title="按图片搜索" width="30%">
                <div class="upload-container">
                  <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeUpload"
                  >
                    <el-image v-if="filedata.url" :src="filedata.url" class="avatar" style="border-radius: 5px;"
                              :preview-src-list="[filedata.url]"/>
                    <el-icon v-else class="avatar-uploader-icon">
                      <Plus/>
                    </el-icon>
                  </el-upload>
                  <el-button type="primary" @click="handleImageSearch">搜索</el-button>
                </div>
              </el-dialog>
            </template>

          </el-input>
          <el-button type="primary" icon="el-icon-search" class="search-button" @click="searchProduct(searchQuery)">
            搜索
          </el-button>
        </div>
      </div>
    </div>

    <el-container>
      <el-main>
        <div class="filter-section">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="价格区间">
              <el-input-number v-model="minPrice" placeholder="最小价格" @change="filterForm.minPrice = minPrice"/>
              <el-input-number v-model="maxPrice" placeholder="最大价格" @change="filterForm.maxPrice = maxPrice"/>
            </el-form-item>

            <el-form-item label="发货地">
              <el-select v-model="filterForm.location" placeholder="选择发货地" style="width: 150px;">
                <el-option
                    v-for="loc in locations"
                    :key="loc"
                    :label="loc"
                    :value="loc"
                    @click="filterForm.location = loc"
                />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="applyFilters">筛选</el-button>
            </el-form-item>
          </el-form>
        </div>

        <section class="recommendation-section">
          <div class="tab-content">
            <div>
              <el-row :gutter="20">
                <el-col :span="4" v-for="product in recommendedProducts" :key="product.id">
                  <el-tooltip
                      class="box-item"
                      effect="dark"
                      :content="product.name"
                      placement="right"
                  >
                    <el-card shadow="hover" class="product-card" @click="handleGoodDetail(product.id)">
                      <img :src="product.image" class="product-image" alt="Product Image">
                      <div class="product-info">
                        <p v-if="product.name.length < 10">{{ product.name }}</p>
                        <p v-else>{{ product.name.slice(0, 10) }}...</p>
                        <h4 class="product-price">￥{{ product.price }}</h4>
                        <div class="row">
                          <div class="col-6">
                            <p style="color: #999; font-size: 14px;" v-if="product.commentCount>=100">
                              {{ product.commentCount }}+人评价</p>
                            <p style="color: #999; font-size: 14px;" v-else>{{
                                product.commentCount
                              }}人评价</p>
                          </div>
                          <div class="col-6">
                            <p style="color: #999; font-size: 14px;">{{ product.location }}</p>
                          </div>
                        </div>
                      </div>
                    </el-card>
                  </el-tooltip>
                </el-col>
              </el-row>
            </div>
          </div>

        </section>
      </el-main>
    </el-container>
    <RightWidgetComponent/>
    <FooterComponent/>

  </div>
</template>

<script setup>
import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue';
import {ArrowDown, Plus} from "@element-plus/icons-vue";
import router from "@/router";
import axios from "axios";
import {useThemeStore} from "@/store/themeStore";
import {ElMessage} from "element-plus";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const themeStore = useThemeStore();
const isNightMode = computed(() => themeStore.isNightMode);
const searchQuery = ref('');
const searchCategory = ref('product');
const showImageSearchModal = ref(false);
const isScrolled = ref(false);
const city = localStorage.getItem('city');
const weather = localStorage.getItem('weather');
const temperature = localStorage.getItem('temperature');
const handleScroll = () => {
  isScrolled.value = window.scrollY > 200; // 当滚动超过 200px 时，改变搜索栏位置

  // 判断是否到达底部并加载更多商品
  if (loading_recommend.value) return; // 如果正在加载中，则不触发新的请求
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = window.scrollY;
  const windowHeight = window.innerHeight;

  // 判断是否到达底部
  if (scrollTop + windowHeight >= scrollHeight - 200) {
    if (recommendedProducts.value.length < total_recommend.value) {
      initSearchProducts();
    }
  }
};

watch(isNightMode, (newVal) => {
  if (newVal) {
    document.body.classList.add('night-mode');
  } else {
    document.body.classList.remove('night-mode');
  }
});

async function fetchWeather() {
  try {
    const response = await axios.post('/api/other/weather1', {
      city: localStorage.getItem('city'),
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.weather !== null) {
      localStorage.setItem('weather', response.data.weather);
      localStorage.setItem('temperature', response.data.temperature);
    } else {
      localStorage.setItem('weather', "晴");
      localStorage.setItem('temperature', "25");
    }
  } catch (err) {
    localStorage.setItem('weather', "晴");
    localStorage.setItem('temperature', "25");
  }
}

const searchText = ref('');
const recommendedProducts = ref([]);
const originalProducts = ref([]);
const page_recommend = ref(1);
const pageSize = ref(40);
const total_recommend = ref(0);
const loading_recommend = ref(false); // 用于控制加载状态

async function getCount() {
  try {
    const response = await axios.post('/api/good/getCount', {}, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      total_recommend.value = response.data;
    }
  } catch (error) {
    ElMessage.error('获取商品总数失败');
  }
}

function getKetWord() {
  const urlParams = new URLSearchParams(window.location.search);
  searchText.value = urlParams.get('searchText');
}

const minPrice = ref(null);
const maxPrice = ref(null);
const filterForm = {
  minPrice: 0,
  maxPrice: 0,
  location: '',
};
const locations = ref([]);

function applyFilters() {
  if (filterForm.minPrice === null && filterForm.maxPrice === null && filterForm.location === null)
    recommendedProducts.value = [...originalProducts.value];
  else {
    if (filterForm.minPrice !== null && filterForm.maxPrice !== null) {
      recommendedProducts.value = originalProducts.value.filter(good => {
        return good.price >= filterForm.minPrice && good.price <= filterForm.maxPrice;
      });
    } else {
      ElMessage.error('价格区间不能为空');
      return;
    }
    if (filterForm.location !== null) {
      recommendedProducts.value = originalProducts.value.filter(good => {
        return good.location === filterForm.location;
      });
    }
  }
}

async function initSearchProducts() {
  if (loading_recommend.value) return; // 防止重复加载
  loading_recommend.value = true; // 设置加载状态为 true，表示正在加载

  try {
    const response = await axios.post('/api/good/searchGood', {
      keyword: searchText.value,
      page: page_recommend.value,
      pageSize: pageSize.value,
    }, {
      headers: {'Content-Type': 'application/json'},
      withCredentials: true
    });

    if (response.data.length > 0) {
      response.data.forEach(good => {
        recommendedProducts.value.push({
          id: good.goodId,
          name: good.goodName,
          price: good.price,
          image: good.showUrl,
          commentCount: good.comments,
          location: good.location
        });
        originalProducts.value = [...recommendedProducts.value];
        if (!locations.value.includes(good.location)) {
          locations.value.push(good.location);
        }
      });
      page_recommend.value++;
    }
  } catch (error) {
    ElMessage.error('获取推荐商品失败');
  } finally {
    loading_recommend.value = false; // 请求完成后重置加载状态
  }
}

function searchProduct(searchText) {
  window.location.href = '/search?searchText=' + searchText;
}

const setSearchCategory = (category) => {
  searchCategory.value = category;
};

function handleTalk() {
  router.push('/talk');
}

function handleLogin() {
  router.push('/login');
}

function handleRegister() {
  router.push('/register');
}

function handleMySpace() {
  router.push('/mySpace');
}

const handleMyMessage = () => {
  router.push('/talkToStore');
}

const handleGoodDetail = (id) => {
  router.push('/goodDetail?id=' + id);
}

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
    window.location.reload();
  }, 1500);
}

const filedata = ref({
  file: null,
  url: null,
})
const beforeUpload = async (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('请选择图片文件！');
    return;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;  // 限制文件大小为 2MB
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！');
    return;
  }
  filedata.value.file = file;
  filedata.value.url = URL.createObjectURL(file);
};

async function handleImageSearch() {
  if (filedata.value.file === null) {
    ElMessage.error('请先上传图片！');
    return;
  }
  const formData = new FormData();
  formData.append('image', filedata.value.file);
  try {
    const response = await axios.post('/api/good/image-search', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await router.push('/searchByImage?token=' + response.data.token)
    } else
      ElMessage.error("图片搜索失败");
  } catch (error) {
    ElMessage.error("图片搜索失败");
  }
}

onMounted(async () => {
  //刷新界面
  window.addEventListener('scroll', handleScroll);
  getKetWord();
  document.title = '海潮商城 - ' + searchText.value;
  initUserSession();
  await initSearchProducts();
  await getCount();
  await fetchWeather();
});


onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style lang="less" scoped>
.homepage {
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
}

.header {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1001;
  background-color: #ffffff; // 设置背景色确保内容可见
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); // 添加阴影使其更明显
  transition: all 0.3s ease; // 添加过渡效果
}

.weather-info {
  margin-right: 20px; // 给天气信息留些间距
}

.p-3 {
  padding: 1rem;
}

.mb-3 {
  margin-bottom: 1rem;
}

.border-bottom {
  border-bottom: 1px solid #eaeaea;
}

.login-button {
  background-color: #007bff; // 按钮背景颜色
  border-radius: 20px; // 边框圆角
  padding: 10px 20px; // 内边距
  font-weight: bold; // 字体加粗
  font-size: 16px;
  color: #ffffff; // 字体颜色
  transition: background-color 0.3s ease; // 背景颜色渐变效果
}

.login-button:hover {
  background-color: #0056b3; // 悬停时的背景颜色
}

.register-button {
  background-color: #96CDCD; // 按钮背景颜色
  border: 2px solid #96CDCD;
  border-radius: 20px; // 边框圆角
  padding: 10px 20px; // 内边距
  font-weight: bold; // 字体加粗
  font-size: 16px;
  color: #ffffff; // 字体颜色
  transition: background-color 0.3s ease; // 背景颜色渐变效果
}

.register-button:hover {
  border-color: #668B8B;
  background-color: #668B8B; // 悬停时的背景颜色
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar-section {
  padding: 30px 0;
  background-color: #ffffff;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.camera-icon {
  background-color: transparent;
  color: #e02020;
  border: none;
}

.vanta-carousel {
  position: relative;
  width: 100%;
  height: 400px;
}

.carousel {
  position: relative;
  z-index: 1;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.subcategory-popup {
  position: absolute;
  z-index: 1000;
  width: 35%;
  height: 60.3%;
  padding: 10px;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: opacity 0.3s ease;
  opacity: 0.9;
}

.subcategory-details {
  font-size: 14px;
  color: #555;
}

.subcategory-links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  max-width: 800px; // 限制最大宽度，每行最多放置 10 个子类别链接
}

.subcategory-links a {
  font-size: 16px;
  color: #808080;
  text-decoration: none;
  transition: color 0.3s ease;
}

.subcategory-links a:hover {
  color: #e02020 !important;
}

.recommended-section {
  margin-top: 20px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  border-bottom: 1px solid #eaeaea;
}

.tab-navigation-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}

.tab-navigation-item .tab-icon {
  font-size: 30px;
  color: #e02020;
  margin-right: 10px;
}

.tab-navigation-item .tab-label {
  color: #333;
  font-weight: normal;
}

.tab-navigation-item.active .tab-label {
  color: #e02020;
  font-weight: bold;
}

.tab-navigation-item:hover .tab-label {
  color: #e02020;
}

.tab-content {
  margin-top: 20px;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.product-card {
  cursor: pointer;
  margin-bottom: 30px;
  border-radius: 10px;
  height: 400px;
}

.product-card:hover {
  border-color: #e02020;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-info {
  margin-top: 10px;
  text-align: center;
}

.product-price {
  color: #e02020;
  font-weight: bold;
}

.right-widget {
  position: fixed;
  right: 0;
  top: 30%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  padding: 10px;
  border-radius: 10px 0 0 10px;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.right-widget .el-button {
  margin-bottom: 15px;
  color: #333;
}

.right-widget .el-button:hover {
  color: #e02020;
}

.night-mode {
  background-color: #121212;
  color: #ffffff;
}

.night-mode .header {
  background-color: #1f1f1f;
  border-color: #333;
}

.night-mode .search-bar-section {
  background-color: #1f1f1f;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
}

.night-mode .el-menu {
  background-color: #1f1f1f;
  border-color: #333;
  color: #d3d3d3;
}

.night-mode .category-sidebar {
  background-color: #1f1f1f;
  border-right: 1px solid #333;
}

.night-mode .subcategory-popup {
  background-color: #1f1f1f;
  color: #d3d3d3;
}

.night-mode .vanta-carousel {
  filter: brightness(0.8);
}

.night-mode .recommendation-section {
  background-color: #1f1f1f;
  color: #ffffff;
}

.night-mode .el-card {
  background-color: #1f1f1f;
  color: #ffffff;
}

.night-mode .right-widget {
  background-color: #1f1f1f;
  color: #ffffff;
}

.night-mode .right-widget.i {
  background-color: #1f1f1f;
  color: #ffffff;
}

.night-mode .footer {
  background-color: #1f1f1f;
  border-top: 1px solid #333;
}

.night-mode .footer-links a {
  color: #ffffff;
}

.night-mode .footer-links a:hover {
  color: #e02020;
}

.el-tooltip__trigger:focus-visible {
  outline: unset;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

</style>
