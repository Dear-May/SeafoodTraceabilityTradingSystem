<template>
  <div class="store-order-list-page">
    <StoreHeaderView />
    <div class="main-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane name="all" label="全部">
          <template #label>
            <span @click="filterOrdersStatus('全部')">
              <span :class="{isActive: activeTab==='all'}">全部({{ orderCount.all }})</span>
            </span>
          </template>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @confirm="handleConfirmOrder" @cancel="handleCancelOrder" />
          </div>
        </el-tab-pane>
        <el-tab-pane name="working" label="进行中">
          <template #label>
            <span @click="filterOrdersStatus('已支付'); activeTab = 'working';">
              <span :class="{isActive: activeTab==='working'}">进行中({{ orderCount.working }})</span>
            </span>
          </template>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @confirm="handleConfirmOrder" @cancel="handleCancelOrder" />
          </div>
        </el-tab-pane>
        <el-tab-pane name="completed" label="已完成">
          <template #label>
            <span @click="filterOrdersStatus('已完成'); activeTab = 'completed';">
              <span :class="{isActive: activeTab==='completed'}">已完成({{ orderCount.completed }})</span>
            </span>
          </template>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @confirm="handleConfirmOrder" @cancel="handleCancelOrder" />
          </div>
        </el-tab-pane>
        <el-tab-pane name="cancelled" label="已取消">
          <template #label>
            <span @click="filterOrdersStatus('已取消'); activeTab = 'cancelled';">
              <span :class="{isActive: activeTab==='cancelled'}">已取消({{ orderCount.cancelled }})</span>
            </span>
          </template>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @confirm="handleConfirmOrder" @cancel="handleCancelOrder" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import OrderCard from "@/components/OrderCard.vue";
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import { computed, useUserShop} from "@/composables/useShopUser";
import {onMounted, ref} from "vue";
import request from "@/api/request";
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
    const response = await request.post('/api/shop/getShopOrderCount', {
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
    const response = await request.post('/api/shop/getShopOrderInfo', {
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
    const response = await request.post('/api/shop/updateOrderStatus', {
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