<template>
  <div class="order-items">
    <div class="order-item-header row">
      <div class="col-4 text-left">
        <span class="order-date">{{ formatDate(order.createTime) }}</span>&nbsp;&nbsp;
        <span class="order-id-text">订单号：{{ order.orderId }}</span>
      </div>
      <div class="col-5 text-left">
        <span class="fw-bold fs-6"><i class="bi bi-house"></i></span>&nbsp;&nbsp;
        <span class="ellipsis order-shop-name">
          <a @click="$emit('shop', order.shopId)">{{ order.shopName }}</a>
          <i class="bi bi-chat shake-animation order-chat-icon" @click="$emit('chat', order.shopId)"></i>
        </span>
      </div>
      <div class="col-3">
        <span class="order-status-text">状态：{{ order.status }}</span>
      </div>
    </div>

    <div class="order-item-body row">
      <div class="col-8">
        <div class="row order-item-body-card text-left" v-for="(item, index) in order.orderItems" :key="item.itemId">
          <div class="col-2">
            <img :src="item.specimg" alt="" class="order-item-img">
          </div>
          <div class="col-4 row">
            <span class="order-good-name">{{ item.goodname }}</span>
            <span class="order-spec-name">规格：{{ item.specname }}</span>
          </div>
          <div class="col-3 order-price-col">
            <span class="order-price-text">￥{{ item.specprice }}</span>
          </div>
          <div class="col-3 order-number-col">
            <span class="order-number-text">{{ item.specnumber }}</span>
          </div>
          <el-divider v-if="index < order.orderItems.length - 1"></el-divider>
        </div>
      </div>

      <div class="col-4 row order-total-section">
        <div class="col order-total-col">
          <span class="order-total-text">总计：￥{{ order.totalPrice }}</span>
        </div>
        <div class="col order-actions-col">
          <div v-if="order.status === '未支付'">
            <el-button type="primary" size="small" @click="$emit('pay', order.orderId)">去支付</el-button>
            <el-button type="danger" size="small" @click="$emit('cancel', order.orderId)">取消订单</el-button>
          </div>
          <div v-else-if="order.status === '已支付' || order.status === '已发货' || order.status === '已评价'">
            <el-button type="success" size="small" class="order-action-plain-btn" @click="$emit('logistics', order)">查看物流</el-button>
            <el-button type="danger" size="small" @click="$emit('refund', order.orderId)">申请退款</el-button>
          </div>
          <div v-else-if="order.status === '待评价'">
            <el-button type="success" size="small" class="order-action-plain-btn" @click="$emit('comment', order)">评价</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  order: { type: Object, required: true }
});

const emit = defineEmits(['pay', 'cancel', 'chat', 'shop', 'comment', 'refund', 'logistics']);

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return dateStr;
};
</script>

<style scoped>
.order-items {
  margin-bottom: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
}
.order-item-header {
  background-color: #fafafa;
  padding: 10px 20px;
  border-bottom: 1px solid #e8e8e8;
  align-items: center;
}
.order-item-body {
  padding: 16px 0;
}
.order-item-body-card {
  padding: 8px 20px;
}
.text-left { text-align: left; }
.order-date { font-weight: bold; font-size: 16px; }
.order-id-text { font-size: 14px; color: #666; }
.order-shop-name { font-size: 14px; }
.order-chat-icon { margin-left: 10px; color: #ff5000; cursor: pointer; }
.order-status-text { font-size: 14px; font-weight: bold; color: #e02020; }
.order-item-img { width: 100px; height: 100px; object-fit: cover; border-radius: 4px; }
.order-good-name { font-size: 16px; font-weight: bold; text-align: left; }
.order-spec-name { font-size: 12px; text-align: left; color: #555555; }
.order-price-col {
  border-left: 1px solid #e0e0e0;
  border-right: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-price-text { font-size: 16px; font-weight: bold; }
.order-number-col {
  border-left: 1px solid #e0e0e0;
  border-right: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-number-text { font-size: 16px; font-weight: bold; }
.order-total-section {
  text-align: center;
  align-items: center;
  justify-content: center;
  display: flex;
}
.order-total-col {
  border-right: 1px solid #e0e0e0;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-total-text { font-size: 16px; font-weight: bold; color: #e02020; }
.order-actions-col {
  display: flex;
  align-items: center;
  justify-content: center;
}
.order-action-plain-btn {
  background-color: transparent !important;
  border: none !important;
  color: #333333 !important;
}
</style>
