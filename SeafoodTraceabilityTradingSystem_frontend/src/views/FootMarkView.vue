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
      <div v-for="(item, index) in filteredHistoricalData" :key="index" class="history-section">
        <h3 style="text-align: start; font-weight: bold;">{{ formatTime(item.time) }}<a
            style="font-weight: normal; font-size: 20px; color: #999; margin-left: 10px;">{{
            item.data.length
          }}件宝贝</a></h3>
        <div class="product-list">
          <div v-for="(product, idx) in item.data" :key="idx" class="product-card"
               @click="handleProductDetail(product)">
            <img :src="product.imageUrl" alt="product image" class="product-image"/>
            <div class="product-info">
              <p class="product-title">{{ product.title }}</p>
              <p class="product-price">￥{{ product.price }}</p>
            </div>
            <div class="trash-icon" @click="(event) => { event.stopPropagation(); DeleteProduct(product); }">
              <img src="@/images/Trash_icon.png" alt="Trash Icon" class="trash-image"/>
            </div>
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
import SidebarComponent from "@/components/SidebarComponent.vue";
import {onMounted, ref} from 'vue';
import {ElMessage} from "element-plus";
import axios from 'axios';
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

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
  filteredHistoricalData.value = historicalData.value.map(item => ({
    ...item,
    data: item.data.filter(product => product.title.toLowerCase().includes(searchQuery.value))
  })).filter(item => item.data.length > 0);
}


function formatTime(time) {
  const currentDate = new Date();
  const itemDate = new Date(time);

  const isSameYear = itemDate.getFullYear() === currentDate.getFullYear();

  // 创建新的日期对象来判断昨天和前天
  const yesterday = new Date(currentDate);
  yesterday.setDate(currentDate.getDate() - 1);

  const beforeYesterday = new Date(currentDate);
  beforeYesterday.setDate(currentDate.getDate() - 2);

  const isToday = itemDate.toDateString() === currentDate.toDateString();
  const isYesterday = itemDate.toDateString() === yesterday.toDateString();
  const isBeforeYesterday = itemDate.toDateString() === beforeYesterday.toDateString();

  if (isYesterday) {
    return '昨天';
  } else if (isBeforeYesterday) {
    return '前天';
  } else if (isToday) {
    return '今天';
  } else {
    return isSameYear ? itemDate.toLocaleDateString('zh-CN', {
      month: 'numeric',
      day: 'numeric'
    }).replace('年', '').replace('月', '/').replace('日', '') : time;
  }
}

function transformData(backendData) {
  const transformedData = [];
  for (const [date, items] of Object.entries(backendData)) {
    const data = items.map(item => ({
      goodID: item.goodID,
      title: item.goodName,
      price: item.price,
      imageUrl: item.showURL
    }));
    transformedData.push({
      time: date,
      data: data
    });
  }
  return transformedData;
}

const sidebarComponent = ref(null);

function initSidebar() {
  sidebarComponent.value.setActive(9);
}

async function init() {
  try {
    const response = await axios.post('/api/good/findGoodFootMark', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      historicalData.value = transformData(response.data);
      filteredHistoricalData.value = historicalData.value;
    }
  } catch (error) {
    ElMessage.error('获取历史记录失败');
  }
}

async function DeleteProduct(product) {
  try {
    const response = await axios.post('/api/good/deleteGoodFootMark', {
      userId: UserInfoForm.value.id,
      goodId: product.goodID
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      await init();
    } else {
      ElMessage.error('删除失败');
    }
  } catch (error) {
    ElMessage.error('删除失败');
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

</style>
