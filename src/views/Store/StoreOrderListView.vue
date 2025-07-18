<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider" @initApplications="initOrdersInfo"></ShopSliderComponent>
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="p-4">
        <el-tabs class="demo-tabs" v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane name="all" label="全部订单">
            <template #label>
        <span class="custom-tabs-label" @click="filterOrdersStatus('全部')">
          <span :class="{isActive: activeTab==='all'}"><i
              class="bi bi-list order-icon"></i>所有订单&nbsp;({{ orderCount.all }})</span>
        </span>
            </template>
            <div class="search-container">
              <el-input type="text" v-model="searchQuery" placeholder="搜索用户名或者订单号..." class="search-input"
                        @keyup.enter="filterOrders"/>
              <button @click="filterOrders" class="search-button">
                <a class="bi bi-search" style="color:whitesmoke;"></a>
              </button>
            </div>
            <div class="row"
                 style="background-color: #f5f5f5; padding: 10px; margin-bottom: 10px; margin-left: 40px; margin-right: 40px; font-weight: bold;">
              <span class="col-4">商品</span>
              <span class="col-1">单价</span>
              <span class="col-2">数量</span>
              <span class="col-3">总计</span>
              <span class="col-2">操作</span>
            </div>
            <div class="order-list">
              <div v-for="(order) in filteredOrders" :key="order.orderId" class="order-items">
                <div class="order-item-header row">
                  <div class="col-4" style="text-align: left;">
                    <span style="font-weight: bold; font-size: 16px;">{{ formatDate(order.createTime) }}</span>&nbsp;&nbsp;
                    <span style="font-size: 14px;">订单号：{{ order.orderId }}</span>
                  </div>
                  <div class="col-5" style="text-align: left;">
                    <span style="font-size: 14px;" class="ellipsis">{{ order.userName }}
                    <a class="bi bi-chat shake-animation" style="margin-left: 10px; color: #ff5000;"
                       @click="handleToChat(order.shopId)"></a></span>
                  </div>
                  <div class="col-3">
                    <span style="font-size: 14px; font-weight: bold;">状态：{{ order.status }}</span>
                  </div>
                </div>
                <div class="order-item-body row">
                  <div class="col-8">
                    <div class="row order-item-body-card" v-for="(item,index) in order.orderItems" :key="item.itemId"
                         style="text-align: left;">
                      <div class="col-2">
                        <img :src="item.specimg" alt="" style="width: 100px; height: 100px;">
                      </div>
                      <div class="col-4 row">
                        <span style="font-size: 16px; font-weight: bold; text-align: left;">{{ item.goodname }}</span>
                        <span style="font-size: 12px; text-align: left; color: #555555;">规格：{{ item.specname }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">￥{{ item.specprice }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">{{ item.specnumber }}</span>
                      </div>
                      <el-divider v-if="index < order.orderItems.length - 1"></el-divider>
                    </div>
                  </div>
                  <div class="col-4 row"
                       style="text-align: center; align-items: center; justify-content: center; display: flex;">
                    <div
                        style="border-right: 1px solid #e0e0e0; height: 100%; align-items: center; justify-content: center; display: flex;"
                        class="col">
                  <span
                      style="font-size: 16px; font-weight: bold;">总计：￥{{
                      order.totalPrice
                    }}</span>
                    </div>
                    <div class="col">
                      <div v-if="order.status === '已支付'">
                        <el-button type="success" size="small" @click="handleDeliver(order)"
                                   class="evaluate-button"
                                   style="background-color: transparent; border: none; color: #333333;">寄送商品
                        </el-button>
                      </div>
                      <div v-else-if="order.status === '已发货'">
                        <el-button type="success" size="small" @click="handleToEvaluate(order.orderId)"
                                   class="evaluate-button"
                                   style="background-color: transparent; border: none; color: #333333;">查看物流
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane name="paid" label="待发货">
            <template #label>
              <span class="custom-tabs-label" @click="()=>{filterOrdersStatus('已支付'); activeTab = 'paid';}">
                <span :class="{isActive: activeTab === 'paid'}"><i
                    class="bi bi-truck order-icon"></i>待发货&nbsp;({{ orderCount.paid }})</span>
              </span>
            </template>
            <div class="search-container">
              <el-input type="text" v-model="searchQuery" placeholder="搜索用户名或者订单号..." class="search-input"
                        @keyup.enter="filterOrders"/>
              <button @click="filterOrders" class="search-button">
                <a class="bi bi-search" style="color:whitesmoke;"></a>
              </button>
            </div>
            <div class="row"
                 style="background-color: #f5f5f5; padding: 10px; margin-bottom: 10px; margin-left: 40px; margin-right: 40px;">
              <span class="col-4">商品</span>
              <span class="col-1">单价</span>
              <span class="col-2">数量</span>
              <span class="col-3">总计</span>
              <span class="col-2">操作</span>
            </div>
            <div class="order-list">
              <div v-for="(order) in filteredOrders" :key="order.orderId" class="order-items">
                <div class="order-item-header row">
                  <div class="col-4" style="text-align: left;">
                    <span style="font-weight: bold; font-size: 16px;">{{ formatDate(order.createTime) }}</span>&nbsp;&nbsp;
                    <span style="font-size: 14px;">订单号：{{ order.orderId }}</span>
                  </div>
                  <div class="col-5" style="text-align: left;">
                    <span style="font-size: 14px;" class="ellipsis">{{ order.userName }}
                    <a class="bi bi-chat shake-animation" style="margin-left: 10px; color: #ff5000;"
                       @click="handleToChat(order.shopId)"></a></span>
                  </div>
                  <div class="col-3">
                    <span style="font-size: 14px; font-weight: bold;">状态：{{ order.status }}</span>
                  </div>
                </div>
                <div class="order-item-body row">
                  <div class="col-8">
                    <div class="row order-item-body-card" v-for="(item,index) in order.orderItems" :key="item.itemId"
                         style="text-align: left;">
                      <div class="col-2">
                        <img :src="item.specimg" alt="" style="width: 100px; height: 100px;">
                      </div>
                      <div class="col-4 row">
                        <span style="font-size: 16px; font-weight: bold; text-align: left;">{{ item.goodname }}</span>
                        <span style="font-size: 12px; text-align: left; color: #555555;">规格：{{ item.specname }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">￥{{ item.specprice }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">{{ item.specnumber }}</span>
                      </div>
                      <el-divider v-if="index < order.orderItems.length - 1"></el-divider>
                    </div>
                  </div>
                  <div class="col-4 row"
                       style="text-align: center; align-items: center; justify-content: center; display: flex;">
                    <div
                        style="border-right: 1px solid #e0e0e0; height: 100%; align-items: center; justify-content: center; display: flex;"
                        class="col">
                  <span
                      style="font-size: 16px; font-weight: bold;">总计：￥{{
                      order.totalPrice
                    }}</span>
                    </div>
                    <div class="col">
                      <div v-if="order.status === '已支付'">
                        <el-button type="success" size="small" @click="handleDeliver(order)"
                                   class="evaluate-button"
                                   style="background-color: transparent; border: none; color: #333333;">寄送商品
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane name="delivered" label="待收货">
            <template #label>
              <span class="custom-tabs-label" @click="() => {filterOrdersStatus('已发货'); activeTab = 'delivered';}">
                <span :class="{isActive: activeTab === 'delivered'}"><i
                    class="bi bi-cash order-icon"></i>待收货&nbsp;({{ orderCount.delivered }})</span>
              </span>
            </template>
            <div class="search-container">
              <el-input type="text" v-model="searchQuery" placeholder="搜索用户名或者订单号..." class="search-input"
                        @keyup.enter="filterOrders"/>
              <button @click="filterOrders" class="search-button">
                <a class="bi bi-search" style="color:whitesmoke;"></a>
              </button>
            </div>
            <div class="row"
                 style="background-color: #f5f5f5; padding: 10px; margin-bottom: 10px; margin-left: 40px; margin-right: 40px;">
              <span class="col-4">商品</span>
              <span class="col-1">单价</span>
              <span class="col-2">数量</span>
              <span class="col-3">总计</span>
              <span class="col-2">操作</span>
            </div>
            <div class="order-list">
              <div v-for="(order) in filteredOrders" :key="order.orderId" class="order-items">
                <div class="order-item-header row">
                  <div class="col-4" style="text-align: left;">
                    <span style="font-weight: bold; font-size: 16px;">{{ formatDate(order.createTime) }}</span>&nbsp;&nbsp;
                    <span style="font-size: 14px;">订单号：{{ order.orderId }}</span>
                  </div>
                  <div class="col-5" style="text-align: left;">
                    <span style="font-size: 14px;" class="ellipsis">{{ order.userName }}
                    <a class="bi bi-chat shake-animation" style="margin-left: 10px; color: #ff5000;"
                       @click="handleToChat(order.shopId)"></a></span>
                  </div>
                  <div class="col-3">
                    <span style="font-size: 14px; font-weight: bold;">状态：{{ order.status }}</span>
                  </div>
                </div>
                <div class="order-item-body row">
                  <div class="col-8">
                    <div class="row order-item-body-card" v-for="(item,index) in order.orderItems" :key="item.itemId"
                         style="text-align: left;">
                      <div class="col-2">
                        <img :src="item.specimg" alt="" style="width: 100px; height: 100px;">
                      </div>
                      <div class="col-4 row">
                        <span style="font-size: 16px; font-weight: bold; text-align: left;">{{ item.goodname }}</span>
                        <span style="font-size: 12px; text-align: left; color: #555555;">规格：{{ item.specname }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">￥{{ item.specprice }}</span>
                      </div>
                      <div class="col-3"
                           style="border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; align-items: center; justify-content: center; display: flex;">
                        <span style="font-size: 16px; font-weight: bold;">{{ item.specnumber }}</span>
                      </div>
                      <el-divider v-if="index < order.orderItems.length - 1"></el-divider>
                    </div>
                  </div>
                  <div class="col-4 row"
                       style="text-align: center; align-items: center; justify-content: center; display: flex;">
                    <div
                        style="border-right: 1px solid #e0e0e0; height: 100%; align-items: center; justify-content: center; display: flex;"
                        class="col">
                  <span
                      style="font-size: 16px; font-weight: bold;">总计：￥{{
                      order.totalPrice
                    }}</span>
                    </div>
                    <div class="col">
                      <div
                          v-if="order.status === '已支付' || order.status === '已发货' || order.status === '已评价'">
                        <el-button type="success" size="small" @click="handleToEvaluate(order.orderId)"
                                   class="evaluate-button"
                                   style="background-color: transparent; border: none; color: #333333;">查看物流
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane name="refunding" label="退款中">
            <template #label>
              <span class="custom-tabs-label" @click="() => {router.push('/store/orderReturns')}">
                <span :class="{isActive: activeTab === 'refunding'}"><i
                    class="bi bi-cash order-icon"></i>待退款&nbsp;({{ orderCount.refunding }})</span>
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import {useUserShop} from "@/composables/useShopUser";
import {onMounted, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";
import StoreHeaderView from "@/components/StoreHeaderView.vue";

const {shopForm, initUserSession, initShopInfo} = useUserShop();

const shopSlider = ref(null);
const storeHeader = ref(null);
const activeTab = ref('all');
const handleTabClick = () => {
  if (activeTab.value === 'refunding') {
    ElMessage.error('暂未开通');
  }
}
const orderList = ref([]);
const filteredOrders = ref([]);
const orderCount = ref({
  all: 0,
  paid: 0,
  delivered: 0,
  refunding: 0,
});

async function initOrderCount() {
  try {
    const response = await axios.post('/api/shop/getShopOrderCount', {
      shopID: shopForm.value.shopID
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      orderCount.value = response.data;
    } else
      ElMessage.error("获取订单数量失败，请稍后再试。")
  } catch (err) {
    ElMessage.error("获取订单数量失败，请稍后再试。")
  }
}

async function initOrdersInfo() {
  try {
    const response = await axios.post('/api/shop/getShopOrderInfo', {
      shopID: shopForm.value.shopID
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      orderList.value = response.data;
      filteredOrders.value = orderList.value;
      await initOrderCount();
    } else
      ElMessage.error("获取订单信息失败，请稍后再试。")
  } catch (err) {
    ElMessage.error("获取订单信息失败，请稍后再试。")
  }
}

const searchQuery = ref("");

function filterOrders() {
  // 检查搜索查询是否为空
  if (!searchQuery.value.trim() && activeTab.value === 'all') {
    // 如果为空，直接展示所有历史数据
    filteredOrders.value = orderList.value;
    return;
  }

  // 根据关键词过滤历史数据
  filteredOrders.value = filteredOrders.value
      .filter(item =>
          item.orderId.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          item.userName.toLowerCase().includes(searchQuery.value.toLowerCase())
      )
      .map(item => ({
        status: item.status,
        createTime: item.createTime,
        address: item.address,
        shopId: item.shopId,
        userName: item.userName,
        totalPrice: item.totalPrice,
        orderId: item.orderId,
        orderItems: item.orderItems // 可以根据需要决定是否包含订单项
      }));
}

function filterOrdersStatus(status) {
  if (status === '全部') {
    filteredOrders.value = orderList.value;
    return;
  }
  filteredOrders.value = orderList.value.filter(item => item.status === status);
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份需加1，并补零
  const day = String(date.getDate()).padStart(2, '0'); // 补零

  return `${year}-${month}-${day}`;
}

function handleToChat(shopId) {
  router.push('/talkToStore?id=' + shopId);
}

async function handleDeliver(order) {
  try {
    const response = await axios.post('/api/shop/updateOrderStatus', {
      shopId: shopForm.value.shopID,
      userId: order.userId,
      orderId: order.orderId,
      status: '已发货'
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success("订单状态已更新为已发货")
      await initOrdersInfo();
    } else
      ElMessage.error("发货失败，请稍后再试。")
  } catch (err) {
    ElMessage.error("发货失败，请稍后再试。")
  }
}

onMounted(async () => {
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex('3-1')
  storeHeader.value.setTitle('订单管理');
  await initOrdersInfo();
});
</script>

<style scoped lang="less">
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #ff5000;
  font-size: 32px;
  font-weight: 600;
  background-color: #f5f7fa; /* 添加背景色 */
  border-radius: 8px; /* 添加圆角效果 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 添加阴影 */
}


.demo-tabs .order-icon {
  transition: transform 0.3s ease-in-out;
  margin: 10px;
}

.active {
  .span {
    color: #e02020;
    font-weight: bold;
  }

  .order-icon {
    transform: rotate(180deg);
  }
}

.demo-tabs .custom-tabs-label span {
  vertical-align: middle;
  margin-left: 4px;
  font-size: 20px;
  color: #4c4c4c; /* 改变字体颜色 */
  font-weight: 500; /* 修改字体粗细 */

  &.isActive {
    color: #e02020;
  }

  &:hover {
    color: #e02020;
  }
}

.search-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  padding-right: 20px;

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
}

.order-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 18px;
  color: #4c4c4c; /* 改变字体颜色 */
  font-weight: 500; /* 修改字体粗细 */
}

.order-list {
  margin-top: 20px;

  .order-items {
    margin-bottom: 20px;
    margin-left: 40px;
    margin-right: 40px;
    padding: 10px;
    background-color: #e0e0e0; /* 添加背景色 */
    border-radius: 8px; /* 添加圆角效果 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 添加阴影 */

    .order-item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      font-size: 18px;
      color: #4c4c4c; /* 改变字体颜色 */
      font-weight: 500; /* 修改字体粗细 */

      .ellipsis:hover {
        color: #e02020;
      }

      @keyframes shake {
        0% {
          transform: translateY(0);
        }
        25% {
          transform: translateY(-5px);
        }
        50% {
          transform: translateY(0);
        }
        75% {
          transform: translateY(5px);
        }
        100% {
          transform: translateY(0);
        }
      }

      .shake-animation {
        display: inline-block; /* 保持元素的行内块布局 */
        animation: shake 1s infinite; /* 动画持续1秒，无限循环 */
      }
    }

    .order-item-body {
      background-color: #f5f7fa;
    }

    .order-item-body-card {
      padding: 5px;

      img {
        width: 100px;
        height: 100px;
        border-radius: 8px;
      }
    }
  }

  .el-button.evaluate-button {
    font-size: 14px;

    &:hover {
      color: #e02020;
    }
  }
}
</style>