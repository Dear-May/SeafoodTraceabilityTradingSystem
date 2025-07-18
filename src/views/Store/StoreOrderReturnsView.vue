<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider" @initApplications="initReturnOrdersInfo"></ShopSliderComponent>
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
                    <div v-if="order.status !== '等待商家退款'">
                      <span style="font-size: 14px; font-weight: bold;">操作员：{{ order.workUserName }}</span>
                    </div>
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
                      <el-button type="success" size="small" @click="handleToEvaluate(order.orderId)"
                                 class="evaluate-button"
                                 style="background-color: transparent; border: none; color: #333333;">查看申请原因
                      </el-button>
                      <div v-if="order.status === '等待商家退款'">
                        <el-button type="success" size="small" @click="summitApply(order.orderId,'success')">通过申请
                        </el-button>
                        <el-button type="danger" size="small" @click="summitApply(order.orderId, 'fail')">拒绝申请
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane name="paid" label="未处理订单">
            <template #label>
              <span class="custom-tabs-label" @click="()=>{filterOrdersStatus('等待商家退款'); activeTab = 'paid';}">
                <span :class="{isActive: activeTab === 'paid'}"><i
                    class="bi bi-truck order-icon"></i>待处理订单&nbsp;({{ orderCount.working }})</span>
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
                      <el-button type="success" size="small" @click="handleToEvaluate(order.orderId)"
                                 class="evaluate-button"
                                 style="background-color: transparent; border: none; color: #333333;">查看申请原因
                      </el-button>
                      <div>
                        <el-button type="success" size="small" @click="summitApply(order.orderId,'success')">通过申请
                        </el-button>
                        <el-button type="danger" size="small" @click="summitApply(order.orderId, 'fail')">拒绝申请
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <el-dialog
          title="查看申请原因"
          v-model="dialogVisible"
          width="40%"
          :before-close="handleClose">
        <div style="text-align: center;">
          <p style="font-size: 16px; margin-top: 20px;">退换货申请原因：{{ ReturnInfo.text }}</p>
          <div v-for="(image,index) in ReturnInfo.images" :key="index">
            <el-image
                style="width: 100px; height: 100px"
                :src="image"
                :zoom-rate="1.2"
                :max-scale="7"
                :min-scale="0.2"
                :preview-src-list="[image]"
                :initial-index="4"
                fit="cover"
            />
          </div>
        </div>
        <template #footer>
          <el-button type="success" size="small" @click="summitApply(selectOrderID,'success')"
                     v-if="selectStatus === '等待商家退款'">通过申请
          </el-button>
          <el-button type="danger" size="small" @click="summitApply(selectOrderID, 'fail')"
                     v-if="selectStatus === '等待商家退款'">拒绝申请
          </el-button>
          <el-button type="primary" size="small" @click="handleClose">关闭</el-button>
        </template>
      </el-dialog>

    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import {useUserShop} from "@/composables/useShopUser";
import {onMounted, ref} from "vue";
import StoreHeaderView from "@/components/StoreHeaderView.vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

const {UserForm, shopForm, initUserSession, initShopInfo} = useUserShop();
const shopSlider = ref(null);
const storeHeader = ref(null);

const activeTab = ref('all');
const orderList = ref([]);
const filteredOrders = ref([]);
const orderCount = ref({
  all: 0,
  working: 0,
});

async function initReturnOrdersInfo() {
  try {
    const response = await axios.post('/api/shop/return/getShopReturnInfo', {
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
      console.log(orderList.value);
      orderCount.value.all = orderList.value.length;
      orderCount.value.working = orderList.value.filter(order => order.status === '等待商家退款').length;
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
        orderItems: item.orderItems,
        returnGood: item.returnGood,
        workUserName: item.workUserName,
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

const dialogVisible = ref(false);
const ReturnInfo = ref({});
const selectOrderID = ref();
const selectStatus = ref();

function handleToEvaluate(orderId) {
  dialogVisible.value = true;
  ReturnInfo.value = orderList.value.find(item => item.orderId === orderId);
  selectStatus.value = ReturnInfo.value.status;
  ReturnInfo.value = ReturnInfo.value.returnGood;
  selectOrderID.value = orderId;
}

function handleClose() {
  dialogVisible.value = false;
  ReturnInfo.value = {};
}

async function summitApply(orderId, status) {
  console.log(orderId, status);
  try {
    const response = await axios.post('/api/shop/return/changeReturnStatus', {
      workUserID: UserForm.value.id,
      orderId: orderId,
      status: status
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("操作成功");
      dialogVisible.value = false;
      ReturnInfo.value = {};
      selectOrderID.value = "";
      await initReturnOrdersInfo();
    } else {
      ElMessage.error("操作失败，请稍后再试。");
    }
  } catch (err) {
    ElMessage.error("操作失败，请稍后再试。");
  }
}

onMounted(async () => {
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex('3-2')
  storeHeader.value.setTitle("退换货管理")
  await initReturnOrdersInfo();
});
</script>

<style lang="less" scoped>
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