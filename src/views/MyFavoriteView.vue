<template>
  <div class="my-haichao-page">
    <HeaderComponent/>
    <SidebarComponent ref="sidebarComponent"/>

    <div class="main-content">
      <div class="search-container">
        <el-input type="text" v-model="searchQuery" placeholder="搜索宝贝..." class="search-input"
                  @keyup.enter="filterProducts"/>
        <button @click="filterProducts" class="search-button">
          <a class="bi bi-search" style="color:whitesmoke;"></a>
        </button>
      </div>
      <div class="product-list">
        <div class="product-card" @click="handleProductDetail(item)" v-for="item in filteredHistoricalData"
             :key="item.goodID">
          <img :src="item.showURL" alt="product image" class="product-image"/>
          <div class="product-info">
            <p class="product-title">{{ item.goodName }}</p>
            <p class="product-price" v-if="item.status">￥{{ item.price }}</p>
            <p class="product-price" style="color: #4c4c4c;" v-else>
              <a class="bi bi-x-circle-fill" style="color: #4c4c4c;"></a>已下架
            </p>
          </div>
          <div class="button-container">
            <button @click.stop="handleProductDetail(item)">进入店铺</button>
            <button @click.stop="findSimilar(item)">按图找相似</button>
          </div>
          <div class="trash-icon" @click="(event) => { event.stopPropagation(); DeleteProduct(item.goodID); }">
            <img src="@/images/Trash_icon.png" alt="Trash Icon" class="trash-image"/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <FooterComponent/>
  <RightWidgetComponent/>
</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import router from "@/router";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import {onMounted, ref} from 'vue';
import {ElMessage} from "element-plus";
import axios from 'axios';
import SidebarComponent from "@/components/SidebarComponent.vue";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const sidebarComponent = ref(null);

function initSidebar() {
  sidebarComponent.value.setActive(7);
}

function handleProductDetail(product) {
  router.push('/goodDetail?id=' + product.goodID);
}

const historicalData = ref([]);
const searchQuery = ref('');
const filteredHistoricalData = ref([]);

function filterProducts() {
  // 检查搜索查询是否为空
  if (!searchQuery.value.trim()) {
    // 如果为空，直接展示所有历史数据
    filteredHistoricalData.value = historicalData.value;
    return;
  }

  // 根据关键词过滤历史数据
  filteredHistoricalData.value = historicalData.value
      .filter(item => item.goodName.toLowerCase().includes(searchQuery.value.toLowerCase()))
      .map(item => ({
        status: item.status,
        uplodatime: item.uplodatime,
        location: item.location,
        shopID: item.shopID,
        goodName: item.goodName,
        comments: item.comments,
        price: item.price,
        goodID: item.goodID,
        showURL: item.showURL
      }));
}

async function init() {
  try {
    const response = await axios.post('/api/good/findFavourite', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data !== null) {
      historicalData.value = response.data;
      filteredHistoricalData.value = historicalData.value;
    }
  } catch (err) {
    ElMessage.error("获取收藏数据失败")
  }
}

async function DeleteProduct(goodID) {
  try {
    const response = await axios.post('/api/good/deleteFavourite', {
      userId: UserInfoForm.value.id,
      goodId: goodID
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      await init();
    } else if (response.data.code === 400) {
      ElMessage.error("取消收藏失败1")
    }
  } catch (error) {
    ElMessage.error("取消收藏失败")
  }
}

onMounted(async () => {
  initUserSession();
  await init();
  initSidebar();
});
</script>

<style lang="less" scoped>
.my-haichao-page {
  display: flex;
  flex-direction: row;
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #ffffff;
  margin-top: 90px;
  min-height: calc(90vh - 100px);
}

.search-box {
  margin-bottom: 20px;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
}

.history-section {
  margin-bottom: 30px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.product-list > div {
  flex-basis: calc(20% - 20px); /* 每个子元素占据 25% 的宽度，减去间距 */
}

.product-card {
  width: 200px;
  background-color: #fff;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  border: 1px solid #e02020;
}

.product-image {
  width: 100%;
  height: 75%;
  object-fit: cover;
  border-radius: 8px;
}

.trash-icon {
  position: absolute;
  top: 0;
  right: 0;
  display: none; /* 默认隐藏 */
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 0 0 0 8px;
}

.product-card:hover .trash-icon {
  display: block; /* 悬浮时显示 */
}

.trash-image {
  width: 30px;
  height: 30px;
  padding: 5px;
  /* 其他样式 */
}

.product-info {
  padding: 10px;
}

.product-title {
  font-size: 14px;
  color: #333;
  margin: 5px 0;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff0000;
}

.search-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  padding-right: 20px;
}

.search-input {
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ccc;
  border-radius: 8px 0 0 8px;
  outline: none;
  width: 200px;
  border-right: none;
}

.search-input:hover {
  border-color: #e02020;
}

.search-button {
  padding: 10px 20px;
  background-color: #e02020;
  color: white;
  font-size: 16px;
  border: 2px solid #e02020;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  font-weight: bold;
}

.search-button:hover {
  background-color: #c21a1a;
  border-color: #c21a1a;
}

.night-mode {
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

.night-mode .product-card {
  background-color: #3c3c3c;
}

.night-mode .search-input {
  background-color: #3c3c3c;
  color: #e0e0e0;
}

.button-container {
  position: absolute;
  width: 100%;
  top: 66%;
  display: none; /* 默认隐藏 */
  background-color: rgba(0, 0, 0, 0.6);
  justify-content: space-between;
  padding: 5px 0;
}

.product-card:hover .button-container {
  display: block; /* 悬浮时显示 */
}

.button-container button {
  background-color: transparent;
  color: white;
  border: none;
  padding: 10px 10px;
  cursor: pointer;
  border-radius: 5px;
  font-size: 18px;
}

</style>
