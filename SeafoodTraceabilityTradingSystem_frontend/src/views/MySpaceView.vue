<template>
  <div class="my-haichao-page" :class="{ 'night-mode': isNightMode }">
    <HeaderComponent/>
    <SidebarComponent ref="sidebarComponent"/>

    <div class="main-content">
      <div class="profile-section d-flex">
        <div class="profile-card">
          <div class="profile-header card-wrapper" @click="handleEditProfile">
            <img :src="UserInfoForm.avatar" alt="User Avatar" class="profile-avatar" @mouseover="isHovering = true"
                 @mouseout="isHovering = false">
            <div v-if="isHovering" class="edit-profile">编辑资料</div>
          </div>
          <h2>{{ UserInfoForm.nickname }}</h2>
          <p>用户名 | {{ UserInfoForm.name }}</p>
          <el-button type="primary" size="small" class="open-plus-btn" @click="handleAddress">收货地址</el-button>
          <div class="profile-footer">
            <p>欢迎使用海潮商城！</p>
          </div>
        </div>

        <div class="order-section flex-grow-1 ms-4">
          <h2 style="text-align: left;">我的订单</h2>
          <el-divider></el-divider>
          <div class="order-details p-3">
            <div class="order-item">
              <span class="order-number">{{ orderCount.unpaid }}</span>
              <br>
              <i class="bi bi-cash order-icon"></i>
              <br>
              <span style="font-size: 20px;">待付款</span>
            </div>
            <div class="order-item">
              <span class="order-number">{{ orderCount.paid }}</span>
              <br>
              <i class="bi bi-truck order-icon"></i>
              <br>
              <span style="font-size: 20px;">待发货</span>
            </div>
            <div class="order-item">
              <span class="order-number">{{ orderCount.delivered }}</span>
              <br>
              <i class="bi bi-box-seam order-icon"></i>
              <br>
              <span style="font-size: 20px;">待收货</span>
            </div>
            <div class="order-item">
              <span class="order-number">{{ orderCount.evaluating }}</span>
              <br>
              <i class="bi bi-pencil-square order-icon"></i>
              <br>
              <span style="font-size: 20px;">待评价</span>
            </div>
            <div class="order-item">
              <span class="order-number">{{ orderCount.refunding }}</span>
              <br>
              <i class="bi bi-arrow-counterclockwise order-icon"></i>
              <br>
              <span style="font-size: 20px;">退款/售后</span>
            </div>
          </div>
        </div>
      </div>

      <div class="logistics-section">
        <h2 style="text-align: left;">我的物流</h2>
        <el-divider></el-divider>
        <div class="logistics-list">
          <div class="logistics-item" v-for="logistics in logisticsInfo" :key="logistics.id">
            <div class="logistics-img col-1">
              <img :src="logistics.image" alt="物流产品图片">
            </div>
            <div class="logistics-details col-9" style="text-align: left;">
              <p class="logistics-status">{{ logistics.status }}</p>
              <p class="logistics-provider">{{ logistics.provider }} | {{ logistics.date }}</p>
            </div>
            <div class="logistics-actions col-2">
              <a href="#" class="logistics-action">查看详情</a>
              <span>|</span>
              <a href="#" class="logistics-action">查看发票</a>
            </div>
          </div>
        </div>
      </div>

      <div class="logistics-section">
        <h4 style="text-align: left;">常买常逛&nbsp;&nbsp;<el-icon>
          <DishDot/>
        </el-icon>
        </h4>
        <el-divider></el-divider>
        <div class="promotion-section">
          <div class="promotion-card" v-for="promotion in promotions" :key="promotion.id"
               @click="handlePromotion(promotion)">
            <img :src="promotion.showURL" alt="Promotion" class="promotion-image">
            <div class="promotion-info">
              <h3>{{
                  promotion.goodName.substring(0, 10) + '...'
                }}</h3>
              <span class="promotion-price">￥{{ promotion.price }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="logistics-section">
        <h4 style="text-align: left;">猜你喜欢&nbsp;&nbsp;<i class="bi bi-balloon-heart"></i>
        </h4>
        <el-divider></el-divider>
        <div class="promotion-section">
          <div class="promotion-card" v-for="product in recommendedProducts" :key="product.id">
            <img :src="product.image" alt="Promotion" class="promotion-image">
            <div class="promotion-info">
              <h3>{{ product.name.slice(0, 10) + '...' }}</h3>
              <span class="promotion-price">￥{{ product.price }}</span>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
  <RightWidgetComponent/>
  <FooterComponent/>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import router from "@/router";
import axios from "axios";
import {DishDot} from "@element-plus/icons-vue";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import {ElMessage} from "element-plus";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()
const isHovering = ref(false);

const orderCount = ref({
  all: 0,
  unpaid: 0,
  paid: 0,
  delivered: 0,
  evaluating: 0,
  refunding: 0,
  cancelled: 0,
});

async function initOrderCount() {
  try {
    const response = await axios.post('/api/pay/order/getOrderCount', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      orderCount.value.all = response.data.all;
      orderCount.value.unpaid = response.data.unpaid;
      orderCount.value.paid = response.data.paid;
      orderCount.value.delivered = response.data.delivered;
      orderCount.value.evaluating = response.data.evaluating;
      orderCount.value.refunding = response.data.refunding;
      orderCount.value.cancelled = response.data.cancelled;
    } else
      ElMessage.error("获取订单数量失败，请稍后再试。")
  } catch (err) {
    ElMessage.error("获取订单数量失败，请稍后再试。")
  }
}

const logisticsInfo = ref([
  {
    id: 1,
    image: 'https://oss.yy0313.fit/GoodFirstShow/04620002.jpg',
    status: '订单已送达，感谢您选择海潮购物，期待再为您服务。',
    date: '2024-10-27 21:21:08',
    provider: '京东快递'
  },
  {
    id: 2,
    image: 'https://oss.yy0313.fit/GoodFirstShow/04620003.jpg',
    status: '您的订单由本人签收，如有疑问可以联系配送员（高建伟）',
    date: '2024-10-26 09:10:29',
    provider: '京东快递'
  }
]);

const promotions = ref([]);

async function initPromotions() {
  try {
    const response = await axios.post('/api/good/getUserLikeInFootMark', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      promotions.value = response.data;
    }
  } catch (err) {
    ElMessage.error("获取推荐商品失败，请稍后再试。")
  }
}

const handleScroll = () => {
  // 判断是否到达底部并加载更多商品
  if (loading_recommend.value) return; // 如果正在加载中，则不触发新的请求
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = window.scrollY;
  const windowHeight = window.innerHeight;

  // 判断是否到达底部
  if (scrollTop + windowHeight >= scrollHeight - 200) {
    if (recommendedProducts.value.length < total_recommend.value) {
      initRecommendedProducts();
    }
  }
};
const recommendedProducts = ref([]);
const page_recommend = ref(1);
const pageSize = ref(14);
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

async function initRecommendedProducts() {
  if (loading_recommend.value) return; // 防止重复加载
  loading_recommend.value = true; // 设置加载状态为 true，表示正在加载

  try {
    const response = await axios.post('/api/good/getGoods', {
      userId: UserInfoForm.value.id,
      page: page_recommend.value,
      pageSize: pageSize.value,
    }, {
      headers: {'Content-Type': 'application/json'},
      withCredentials: true
    });

    if (response.data.length > 0) {
      response.data.forEach(good => {
        recommendedProducts.value.push({
          id: good.goodID,
          name: good.goodName,
          price: good.price.toString(),
          image: good.showURL,
          commentCount: good.comments
        });
      });
      page_recommend.value++;
    }
  } catch (error) {
    ElMessage.error('获取推荐商品失败');
  } finally {
    loading_recommend.value = false; // 请求完成后重置加载状态
  }
}

const sidebarComponent = ref(null);

function initSidebar() {
  sidebarComponent.value.setActive(1);
}

function handleAddress() {
  router.push('/profile/address');
}

function handleEditProfile() {
  router.push('/profile/account_information');
}

function handlePromotion(promotion) {
  router.push('/goodDetail?id=' + promotion.goodID);
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll);
  initUserSession();
  initSidebar();
  await initOrderCount();
  await initPromotions();
  await getCount();
  await initRecommendedProducts();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
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
}

.profile-section {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.profile-card {
  width: 300px;
  background-color: #d32f2f;
  color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.profile-card::before {
  content: 'OceanWave';
  position: absolute;
  top: 10%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 55px; /* 根据需要调整字体大小 */
  color: rgba(255, 255, 255, 0.5); /* 白色文字，透明度可以调整 */
  pointer-events: none; /* 确保文字不会影响鼠标事件 */
  z-index: 1;
}

.profile-header {
  margin-bottom: 20px;
}

.card-wrapper {
  position: relative;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  position: relative;
  z-index: 2;
  cursor: pointer;
}

.edit-profile {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 18px;
  color: white;
  padding: 5px;
  background-color: rgba(0, 0, 0, 0.6);
  width: 39%;
  height: 100%;
  border-radius: 50%;
  display: flex; /* 用于文字垂直居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  pointer-events: none;
  z-index: 3;
  opacity: 0.8;
}

.open-plus-btn {
  background-color: #ffca28;
  color: #000000;
  border: none;
  margin-bottom: 10px;
}

.profile-footer {
  background-color: #424242;
  padding: 10px;
  border-radius: 8px;
}

.order-section {
  flex-grow: 1;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.order-details {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.order-item {
  text-align: center;
  color: #333;
}

.order-number {
  font-size: 35px;
  font-weight: bold;
  margin-bottom: 10px;
}

.order-icon {
  font-size: 36px;
  margin: 10px 0;
}

.logistics-section {
  margin-top: 20px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logistics-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.logistics-item {
  display: flex;
  padding: 15px;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  background-color: #f9f9f9;
  transition: background-color 0.3s ease;
}

.logistics-item:hover {
  background-color: #f1f1f1;
}

.logistics-img img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 20px;
}

.logistics-details {
  flex-grow: 1;
}

.logistics-status {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.logistics-provider {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
}

.logistics-actions {
  display: flex;
  align-items: center;
}

.logistics-action {
  color: #007bff;
  text-decoration: none;
  margin-right: 10px;
}

.logistics-action:hover {
  text-decoration: underline;
}

.promotion-section {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.promotion-card {
  width: 200px;
  padding: 10px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.promotion-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid #e02020;
  transform: translateY(-5px);
}

.promotion-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.promotion-info {
  text-align: center;
}

.promotion-price {
  color: #e02020;
  font-weight: bold;
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
</style>
