<template>
  <div class="my-haichao-page">
    <HeaderComponent/>
    <SidebarComponent ref="sidebarComponent"/>
    <div class="main-content">
      <el-tabs class="demo-tabs" v-model="activeTab">
        <el-tab-pane name="all" label="全部订单">
          <template #label>
        <span class="custom-tabs-label" @click="filterOrdersStatus('全部')">
          <span :class="{isActive: activeTab==='all'}"><i
              class="bi bi-list order-icon"></i>所有订单&nbsp;({{ orderCount.all }})</span>
        </span>
          </template>
          <div class="search-container">
            <el-input type="text" v-model="searchQuery" placeholder="搜索商品或者订单号..." class="search-input"
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                    <div v-if="order.status === '未支付'">
                      <el-button type="primary" size="small" @click="handleToPay(order.orderId)">去支付</el-button>
                      <el-button type="danger" size="small" @click="handleToCancel(order.orderId)">取消订单</el-button>
                    </div>
                    <div
                        v-else-if="order.status === '已支付' || order.status === '已发货' || order.status === '已评价'">
                      <el-button type="success" size="small"
                                 class="evaluate-button"
                                 style="background-color: transparent; border: none; color: #333333;">查看物流
                      </el-button>
                      <el-button type="danger" size="small" @click="openReturnDialog(order.orderId)">申请退款
                      </el-button>
                    </div>
                    <div v-else-if="order.status === '待评价'">
                      <el-button type="success" size="small" @click="CommentOrder(order)" plain
                                 style="background-color: transparent; border: none; color: #333333;">评价
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="unpaid" label="待付款">
          <template #label>
        <span class="custom-tabs-label" @click="() => {filterOrdersStatus('未支付'); activeTab = 'unpaid';}">
          <span :class="{isActive: activeTab === 'unpaid'}"><i
              class="bi bi-cash order-icon"></i>待付款&nbsp;({{ orderCount.unpaid }})</span>
        </span>
          </template>
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                    <div v-if="order.status === '未支付'">
                      <el-button type="primary" size="small" @click="handleToPay(order.orderId)">去支付</el-button>
                      <el-button type="danger" size="small" @click="handleToCancel(order.orderId)">取消订单</el-button>
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                      <el-button type="success" size="small"
                                 class="evaluate-button"
                                 style="background-color: transparent; border: none; color: #333333;">查看物流
                      </el-button>
                      <el-button type="danger" size="small" @click="openReturnDialog(order.orderId)">申请退款
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                      <el-button type="success" size="small"
                                 class="evaluate-button"
                                 style="background-color: transparent; border: none; color: #333333;">查看物流
                      </el-button>
                      <el-button type="danger" size="small" @click="openReturnDialog(order.orderId)">申请退款
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="evaluating" label="待评价">
          <template #label>
        <span class="custom-tabs-label" @click="() => {filterOrdersStatus('待评价'); activeTab = 'evaluating';}">
          <span :class="{isActive: activeTab === 'evaluating'}"><i class="bi bi-pencil-square order-icon"></i>待评价&nbsp;({{
              orderCount.evaluating
            }})</span>
        </span>
          </template>
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                      style="font-size: 16px; font-weight: bold; ">总计：￥{{
                      order.totalPrice
                    }}</span>
                  </div>
                  <div class="col">
                    <div v-if="order.status === '待评价'">
                      <el-button type="success" size="small" @click="CommentOrder(order)" plain
                                 style="background-color: transparent; border: none; color: #333333;">评价
                      </el-button>
                      <el-button type="danger" size="small" @click="openReturnDialog(order.orderId)">申请退款
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="refunding" label="售后">
          <template #label>
        <span class="custom-tabs-label" @click="()=>{filterOrdersStatus('退款'); activeTab = 'refunding';}">
          <span :class="{isActive: activeTab === 'paid'}"><i
              class="bi bi-truck order-icon"></i>售后&nbsp;({{ orderCount.refunding }})</span>
        </span>
          </template>
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
                  <span style="font-weight: bold; font-size: 16px;"><a class="bi bi-house"></a></span>&nbsp;&nbsp;
                  <span style="font-size: 14px;" class="ellipsis"><a @click="handleShop(order.shopId)">{{
                      order.shopName
                    }}</a>
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
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!--退货弹窗--->
      <el-dialog
          title="提交退货申请"
          v-model="dialogReturnVisible"
          width="50%"
          :append-to-body="true"
          :before-close="closeReturnDialog"
      >
        <el-form
            ref="returnForm"
            :model="returnForm"
            label-width="120px"
            class="trace-form"
        >
          <el-form-item label="申请描述">
            <el-input
                v-model="ReturnInfo"
                type="textarea"
                rows="5"
                placeholder="请输入溯源详细描述"
                maxlength="200"
                show-word-limit
            ></el-input>
          </el-form-item>

          <el-form-item label="上传图片">
            <el-upload
                list-type="picture-card"
                :auto-upload="false"
                :limit="3"
                accept="image/*"
                :on-change="handleReturnFileChange"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleReturnRemove"
                :file-list="returnForm.images"
            >
              <el-icon>
                <Plus/>
              </el-icon>
            </el-upload>
            <br>
            <div class="el-upload__tip">最多上传 3 张图片，支持 JPG/PNG 格式。</div>
          </el-form-item>

          <el-dialog v-model="dialogVisible">
            <img :src="dialogImageUrl" alt="Preview Image"/>
          </el-dialog>
        </el-form>
        <template #footer>
          <el-button @click="closeReturnDialog">取消</el-button>
          <el-button type="primary" @click="submitReturnInfo(selectReturnOrderID)">提交</el-button>
        </template>
      </el-dialog>

      <!--评价弹窗--->
      <el-dialog v-model="CommentDialogVisible" width="50%" height="500px" draggable @close="chanceToCancel">
        <div class="dialog-header row"
             style="border-bottom: 2px solid #e02020; padding-left: 10px; padding-right: 10px;">
          <div class="col-6" style="text-align: left;">
                      <span
                          style="background-color: #e02020; color: white;padding: 6px; font-size: 20px; font-weight: bold; height: 100%;">商品评价</span>
          </div>
          <div class="col-4" style="text-align: right; margin-top: 5px;">
            卖家：{{ commentForm.selectShopName }}&nbsp;&nbsp;
            <a class="bi bi-chat shake-animation" style="margin-left: 10px; color: #ff5000;"
               @click="handleToChat( commentForm.shopId)"></a>
          </div>
          <div class="col" style="text-align: right; padding-right: 10px; ">
            <el-tooltip
                class="box-item"
                effect="dark"
                content=""
                placement="bottom"
            >
              <el-button style="border: none; background-color: transparent; color: #333333; font-size: 14px;">
                评论须知
              </el-button>
            </el-tooltip>
          </div>
        </div>
        <div class="dialog-body row">
          <div class="col-4">
            <div class="dialog-image"
                 style="text-align: center; align-items: center; justify-content: center; display: flex; height: 100%; padding-top: 20px;">
              <img :src=" commentForm.selectShowImage" alt="" style="width: 200px; height: 200px;">
            </div>
            <span style="font-size: 14px; color: #555555;">{{ commentForm.selectGoodName }}</span>
          </div>
          <div class="col-8" style="text-align: left;">
            <div style="margin-bottom: 10px;">
              商品评分：
              <el-rate
                  size="large"
                  v-model="commentForm.SelectRate"
                  :texts="['非常不满意', '不满意', '一般', '满意', '非常满意']"
                  show-text
              />
            </div>
            <div>
              <span>商品评价：</span>
              <el-input type="textarea" placeholder="来分享一下你的感受吧..." v-model="commentForm.comment"
                        maxlength="200" height="200px" resize="none"></el-input>
              <el-upload action="#" list-type="picture-card" :auto-upload="false" :limit="3" accept="image/*"
                         :on-change="handleFileChange" :file-list="commentForm.files">
                <el-icon>
                  <Plus/>
                </el-icon>
                <template #file="{ file }">
                  <div>
                    <img class="el-upload-list__item-thumbnail" :src="file.url" alt=""/>
                    <span class="el-upload-list__item-actions">
                      <span
                          class="el-upload-list__item-preview"
                          @click="handlePictureCardPreview(file)">
                        <el-icon><zoom-in/></el-icon>
                      </span>
                      <span
                          v-if="!disabled"
                          class="el-upload-list__item-delete"
                          @click="handleRemove(file)">
                        <el-icon><Delete/></el-icon>
                      </span>
                    </span>
                  </div>
                </template>
              </el-upload>

              <el-dialog v-model="dialogVisible">
                <img :src="dialogImageUrl" alt="Preview Image"/>
              </el-dialog>
            </div>
          </div>
        </div>
        <el-divider/>
        <template #footer>
          <div class="dialog-footer" style="text-align: center; padding-right: 10px;">
            <el-button type="primary" size="large" @click="handleSubmitComment"
                       style="background-color: #e02020; color: white; border: none;">
              提交评论
            </el-button>
            <el-button @click="chanceToCancel" size="large">取消</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
  <RightWidgetComponent/>
  <FooterComponent/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";
import {Delete, Plus, ZoomIn} from "@element-plus/icons-vue";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()
const sidebarComponent = ref(null);

function initSidebar() {
  sidebarComponent.value.setActive(3);
}

const activeTab = ref('all');

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

const searchQuery = ref("");
const orderList = ref([]);
const filteredOrders = ref([]);

async function initOrdersInfo() {
  try {
    const response = await axios.post('/api/pay/order/getOrders', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      orderList.value = response.data;
      filteredOrders.value = orderList.value;
    } else
      ElMessage.error("获取订单信息失败，请稍后再试。")
  } catch (err) {
    ElMessage.error("获取订单信息失败，请稍后再试。")
  }
}

function filterOrders() {
  // 检查搜索查询是否为空
  if (!searchQuery.value.trim()) {
    // 如果为空，直接展示所有历史数据
    filteredOrders.value = orderList.value;
    return;
  }

  // 根据关键词过滤历史数据
  filteredOrders.value = orderList.value
      .filter(item =>
          item.orderId.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          item.orderItems.some(orderItem =>
              orderItem.goodname.toLowerCase().includes(searchQuery.value.toLowerCase())
          )
      )
      .map(item => ({
        status: item.status,
        createTime: item.createTime,
        address: item.address,
        shopId: item.shopId,
        shopName: item.shopName,
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
  filteredOrders.value = orderList.value.filter(item => item.status.includes(status));
  console.log(filteredOrders.value);
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

async function handleToPay(orderId) {
  try {
    const response = await axios.post('/api/pay/submitOrderByView', {
      userId: UserInfoForm.value.id,
      orderId: orderId,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data === "fail") {
      ElMessage.error('提交订单失败');
    } else {
      await router.replace('/payGoods/submitOrder?token=' + response.data);
    }
  } catch (err) {
    ElMessage.error('提交订单失败');
  }
}

async function handleToCancel(orderId) {
  try {
    const response = await axios.post('/api/pay/changOrderStatusView', {
      userId: UserInfoForm.value.id,
      orderId: orderId,
      status: '已取消'
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data.code === 400)
      ElMessage.error('取消订单失败');
    else if (response.data.code === 200) {
      await initOrdersInfo();
    }
  } catch (err) {
    ElMessage.error('取消订单失败');
  }
}

// 退货申请部分
const dialogReturnVisible = ref(false);
const selectReturnOrderID = ref(null);
const ReturnInfo = ref("");
const returnForm = ref({
  info: "",
  images: []
})

const openReturnDialog = (id) => {
  dialogReturnVisible.value = true;
  selectReturnOrderID.value = id;
};

const closeReturnDialog = () => {
  dialogReturnVisible.value = false;
  returnForm.value = {info: "", images: []};
  ReturnInfo.value = "";
}

const handleReturnFileChange = (file) => {
  if (file.size > MAX_FILE_SIZE) {
    ElMessage({
      type: 'error',
      message: '文件大小不能超过 3MB',
    });
    return;
  }
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = (e) => {
    if (!returnForm.value.images)
      returnForm.value.images = [];
    returnForm.value.images.push({
      name: file.name,
      url: e.target.result,
      file: file.raw
    });
  };
};

const handleReturnRemove = (file) => {
  returnForm.value.images = returnForm.value.images.filter(item => item.url !== file.url);
};

async function submitReturnImg(returnId) {
  const formData = new FormData();
  for (const file of returnForm.value.images) {
    formData.append('images', file.file);
  }
  formData.append('returnId', returnId);
  try {
    const response = await axios.post('/api/pay/return/uploadImg', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200)
      returnForm.value.images = [];
    else if (response.data.code === 400)
      ElMessage.error("上传图片失败");
  } catch (err) {
    ElMessage.error("上传图片失败");
  }
}

async function submitReturnInfo(orderId) {
  returnForm.value.info = ReturnInfo.value;
  if (returnForm.value.info.trim() === "")
    ElMessage.error("请填写退货申请描述");
  try {
    const response = await axios.post('/api/pay/return/addReturnInfo', {
      orderId: orderId,
      info: returnForm.value.info
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data.result.code === 200) {
      if (returnForm.value.images) {
        await submitReturnImg(response.data.returnId);
      }
      await initOrdersInfo();
      ElMessage.success("提交退货申请成功");
      closeReturnDialog();
    } else
      ElMessage.error("提交退货申请失败，请稍后再试。");
  } catch (err) {
    ElMessage.error("提交退货申请失败，请稍后再试。");
  }
}

// 评论部分
const CommentDialogVisible = ref(false);
const commentForm = ref({
  selectOrderId: '',
  selectShopName: '',
  selectShowImage: '',
  selectGoodName: '',
  shopId: '',
  SelectRate: 0,
  comment: '',
  files: [],
});

function CommentOrder(order) {
  CommentDialogVisible.value = true;
  commentForm.value.selectOrderId = order.orderId;
  commentForm.value.selectShopName = order.shopName;
  commentForm.value.shopId = order.shopId;
  commentForm.value.selectShowImage = order.orderItems[0].specimg;
  commentForm.value.selectGoodName = order.orderItems[0].goodname;
}

const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const disabled = ref(false)
const MAX_FILE_SIZE = 3 * 1024 * 1024;

const handleFileChange = (file) => {
  if (file.size > MAX_FILE_SIZE) {
    ElMessage({
      type: 'error',
      message: '文件大小不能超过 3MB',
    });
    return;
  }
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = (e) => {
    commentForm.value.files.push({
      name: file.name,
      url: e.url || URL.createObjectURL(file.raw),
      file: file.raw
    });
  };
};

const handleRemove = (file) => {
  commentForm.value.files = commentForm.value.files.filter(item => item.url !== file.url);
};

function handleShop(id) {
  router.push('/shop?shopId=' + id);
}

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url;
  dialogVisible.value = true;
};

const chanceToCancel = () => {
  CommentDialogVisible.value = false;
  commentForm.value.selectOrderId = '';
  commentForm.value.selectShopName = '';
  commentForm.value.shopId = '';
  commentForm.value.selectShowImage = '';
  commentForm.value.selectGoodName = '';
  commentForm.value.SelectRate = 0;
  commentForm.value.comment = '';
  commentForm.value.files = [];
}

async function handleSubmitComment() {
  try {
    const formData = new FormData();
    formData.append('userId', UserInfoForm.value.id);
    formData.append('orderId', commentForm.value.selectOrderId);
    formData.append('content', commentForm.value.comment);
    formData.append('rate', commentForm.value.SelectRate);
    if (commentForm.value.files.length > 0) {
      commentForm.value.files.forEach((item) => {
        formData.append('files', item.file);
      });
    } else {
      formData.append('files', new Blob([]));
    }
    const response = await axios.post('/api/pay/comment/addComment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('评论成功');
      chanceToCancel();
      await initOrdersInfo();
      await initOrderCount();

    } else {
      ElMessage.error('评论失败');
      chanceToCancel();
    }
  } catch (err) {
    ElMessage.error('评论失败');
    chanceToCancel();
  }
}

onMounted(async () => {
  initUserSession();
  initSidebar();
  await initOrderCount();
  await initOrdersInfo();
});
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