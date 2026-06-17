<template>
  <div class="my-haichao-page">
    <HeaderComponent />
    <SidebarComponent ref="sidebarComponent" />
    <div class="main-content">
      <el-tabs class="demo-tabs" v-model="activeTab">
        <el-tab-pane name="all" label="全部订单">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('全部')">
              <span :class="{isActive: activeTab==='all'}"><i class="bi bi-list order-icon"></i>所有订单&nbsp;({{ orderCount.all }})</span>
            </span>
          </template>
          <div class="search-container">
            <el-input type="text" v-model="searchQuery" placeholder="搜索商品或者订单号..." class="search-input" @keyup.enter="filterOrders" />
            <button @click="filterOrders" class="search-button"><i class="bi bi-search" style="color:whitesmoke;"></i></button>
          </div>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="unpaid" label="待付款">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('未支付'); activeTab = 'unpaid';">
              <span :class="{isActive: activeTab === 'unpaid'}"><i class="bi bi-credit-card order-icon"></i>待付款&nbsp;({{ orderCount.unpaid }})</span>
            </span>
          </template>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="paid" label="待发货">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('已支付'); activeTab = 'paid';">
              <span :class="{isActive: activeTab === 'paid'}"><i class="bi bi-truck order-icon"></i>待发货&nbsp;({{ orderCount.paid }})</span>
            </span>
          </template>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="delivered" label="待收货">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('已发货'); activeTab = 'delivered';">
              <span :class="{isActive: activeTab === 'delivered'}"><i class="bi bi-box-seam order-icon"></i>待收货&nbsp;({{ orderCount.delivered }})</span>
            </span>
          </template>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="evaluating" label="待评价">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('待评价'); activeTab = 'evaluating';">
              <span :class="{isActive: activeTab === 'evaluating'}"><i class="bi bi-chat-dots order-icon"></i>待评价&nbsp;({{ orderCount.evaluating }})</span>
            </span>
          </template>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="refunding" label="售后">
          <template #label>
            <span class="custom-tabs-label" @click="filterOrdersStatus('退款'); activeTab = 'refunding';">
              <span :class="{isActive: activeTab === 'refunding'}"><i class="bi bi-arrow-repeat order-icon"></i>售后&nbsp;({{ orderCount.refunding }})</span>
            </span>
          </template>
          <div class="order-table-header row">
            <span class="col-4">商品</span>
            <span class="col-1">单价</span>
            <span class="col-2">数量</span>
            <span class="col-3">总计</span>
            <span class="col-2">操作</span>
          </div>
          <div class="order-list">
            <OrderCard v-for="order in filteredOrders" :key="order.orderId" :order="order"
              @pay="handleToPay" @cancel="handleToCancel" @chat="handleToChat"
              @shop="handleShop" @comment="CommentOrder" @refund="openReturnDialog" />
          </div>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="dialogVisible_Cancel" title="确认取消" width="30%">
        <span>确定要取消该订单吗？</span>
        <template #footer>
          <el-button @click="dialogVisible_Cancel = false">取消</el-button>
          <el-button type="primary" @click="confirmCancel">确定</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="dialogVisible_Return" title="确认退款" width="30%">
        <span>确定要申请退款吗？</span>
        <template #footer>
          <el-button @click="dialogVisible_Return = false">取消</el-button>
          <el-button type="primary" @click="confirmReturn">确定</el-button>
        </template>
      </el-dialog>
    </div>
    <RightWidgetComponent />
    <FooterComponent />
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import OrderCard from "@/components/OrderCard.vue";
import { getOrders, cancelOrder, returnOrder } from "@/api/order";
import { ElMessage } from "element-plus";
import router from "@/router";
import { Delete, Plus, ZoomIn } from "@element-plus/icons-vue";
import useUser from "@/composables/useUser";

const { UserInfoForm, initUserSession } = useUser();
const router_ = router;

const activeTab = ref("all");
const searchQuery = ref("");
const orderList = ref([]);
const filteredOrders = ref([]);
const orderCount = computed(() => {
  const all = orderList.value.length;
  const paid = orderList.value.filter(item => item.status === '已支付' || item.status === '已发货').length;
  const unpaid = orderList.value.filter(item => item.status === '未支付').length;
  const delivered = orderList.value.filter(item => item.status === '已收货').length;
  const evaluating = orderList.value.filter(item => item.status === '待评价' || item.status === '已评价').length;
  const refunding = orderList.value.filter(item => item.status === '退款中' || item.status === '已退款' || item.status === '申请退款').length;
  return { all, paid, unpaid, delivered, evaluating, refunding };
});

let cancelOrderDto = ref({
  orderId: "",
  userId: UserInfoForm.value.id
});

const dialogVisible_Cancel = ref(false);
const dialogVisible_Return = ref(false);
const returnOrderId = ref("");

onMounted(() => {
  initUserSession();
  initOrdersInfo();
});

async function initOrdersInfo() {
  try {
    const response = await getOrders({
      userId: UserInfoForm.value.id
    }, { headers: { "Content-Type": "application/json" } });
    if (response.data && response.data.length > 0) {
      orderList.value = response.data;
      filteredOrders.value = response.data;
    } else if (response.data && response.data.code === 400) {
      orderList.value = [];
      filteredOrders.value = [];
    } else {
      orderList.value = response.data || [];
      filteredOrders.value = response.data || [];
    }
  } catch (error) {
    ElMessage.error("获取订单信息失败!");
  }
}

function filterOrdersStatus(status) {
  if (status === '全部') { filteredOrders.value = orderList.value; return; }
  filteredOrders.value = orderList.value.filter(item => item.status.includes(status));
}

function filterOrders() {
  const query = searchQuery.value.trim().toLowerCase();
  if (!query) { filteredOrders.value = orderList.value; return; }
  filteredOrders.value = orderList.value.filter(item =>
    (item.orderId && item.orderId.toString().toLowerCase().includes(query)) ||
    (item.shopName && item.shopName.toLowerCase().includes(query)) ||
    (item.orderItems && item.orderItems.some(i => i.goodname && i.goodname.toLowerCase().includes(query)))
  );
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;
}

const handleOpen = (key, keyPath) => { };
const handleClose = (key, keyPath) => { };

function handleToPay(orderId) {
  router_.push({ name: 'submitOrderView', query: { orderId: orderId } });
}

function handleToCancel(orderId) {
  cancelOrderDto.value.orderId = orderId;
  cancelOrderDto.value.userId = UserInfoForm.value.id;
  dialogVisible_Cancel.value = true;
}

function handleToChat(shopId) {
  router_.push({ path: '/talkToStore', query: { shopId: shopId, storeUserId: UserInfoForm.value.id } });
}

function handleShop(shopId) {
  router_.push({ path: '/shop', query: { shopId: shopId } });
}

function CommentOrder(order) {
  router_.push({ path: '/mySpace/myComment', query: { orderId: order.orderId } });
}

function openReturnDialog(orderId) {
  returnOrderId.value = orderId;
  dialogVisible_Return.value = true;
}

async function confirmCancel() {
  try {
    await cancelOrder(cancelOrderDto.value, { headers: { "Content-Type": "application/json" } });
    ElMessage.success("取消成功");
    dialogVisible_Cancel.value = false;
    await initOrdersInfo();
  } catch (error) {
    ElMessage.error("取消失败!");
  }
}

async function confirmReturn() {
  try {
    await returnOrder({ orderId: returnOrderId.value }, { headers: { "Content-Type": "application/json" } });
    ElMessage.success("退款申请成功");
    dialogVisible_Return.value = false;
    await initOrdersInfo();
  } catch (error) {
    ElMessage.error("退款申请失败!");
  }
}
</script>

<style scoped lang="less">
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