<template>
  <div class="my-haichao-page">
    <HeaderComponent/>
    <SidebarComponent ref="sidebarComponent"/>

    <div class="main-content row">
      <div class="col-9">
        <h3 class="title">购物车</h3>
        <div class="cart-summary">
          <div class="cart-summary-info">
            <span class="cart-summary-title">全部商品（{{ totalQuantity() }}）</span>
          </div>
          <div class="cart-summary-actions">
            <el-checkbox style="margin-right: 10px;" v-model="isAllSelected" @change="toggleSelectAll"></el-checkbox>
            <span class="select-all-text">全选</span>
            <button class="move-to-favorite-btn" @click="moveToFavoriteList">移入收藏</button>
            <button class="delete-selected-btn" @click="deleteSelected">删除</button>
          </div>
        </div>
        <div v-for="(store) in storeData" :key="store.shopName" class="cart-item">
          <div class="store-info">
            <el-checkbox size="large" v-model="store.shopSelect" @change="toggleSelectShop(store)">
              <span class="store-name" @click="handleShop(store.shopId)">{{ store.shopName }}
                <a class="bi bi-chat shake-animation" style="margin-left: 10px; color: #ff5000;"
                   @click="handleToChat(store.shopId)"></a>
              </span>
            </el-checkbox>
          </div>

          <div class="product-list">
            <div v-for="(product) in store.goods" :key="product.specId" class="product-item">
              <el-checkbox style="margin-right: 10px;" v-model="product.goodSelected"
                           @change="toggleSelect(product)"></el-checkbox>
              <div class="product">
                <img class="product-image" :src="product.specImage" alt="商品图片"/>
                <div class="large-image">
                  <img :src="product.specImage" alt="放大商品图片"/>
                </div>
              </div>
              <div class="product-details">
                <div class="product-name-and-attributes">
                  <div class="product-name" @click="handleToProductDetail(product.goodId)">
                    {{ product.goodName }}
                  </div>
                </div>
              </div>
              <div class="product-spec">
                <div class="product-attributes" :class="{active: product.showDetails}"
                     @click="handleProductSpecDetail(product)">
                  <div class="edit-label">修改</div>
                  规格: {{ product.specName }}
                </div>
                <div class="product-spec-details" v-if="product.showDetails">
                  <div class="row">
                    <div class="col-4">
                      <img :src="selectedSpecImage" alt="商品图片" width="150px" height="150px"/>
                    </div>
                    <div class="col-8">
                      <div class="product-name" style="text-align: start; padding: 10px;">
                        已选：{{ selectedSpecName }}
                      </div>
                      <div
                          style="text-align: left; padding-left: 10px; font-size: 20px; color: #ff5000; font-weight: bold;">
                        价格: ¥ {{ selectedSpecPrice }}
                      </div>
                      <el-divider style="padding: 0 30px;"></el-divider>
                      <div class="row">
                        <div class="col-3">
                          <span style="text-align: right;">规格分类</span>
                        </div>
                        <div class="col-9" style="text-align: left;">
                          <el-button v-for="spec in specList" :key="spec.specificationID"
                                     :class="{'active': spec.specificationID === selectedSpecId}"
                                     style="padding: 0 5px; margin-right: 5px;"
                                     @click="selectSpecification(spec)">
                            <img :src="spec.showurl" width="30px" height="30px" alt="商品图片"
                                 style="margin-right: 5px">
                            <span>{{ spec.specName }}</span>
                          </el-button>
                        </div>
                      </div>
                    </div>
                    <el-divider style="padding: 0 30px;"></el-divider>
                  </div>
                  <el-button @click="updateProductSpec(product)" style="background-color: #ff5000; color: #fff;"
                             size="large">确定
                  </el-button>
                  <el-button @click=" product.showDetails=false" style="background-color: #fff; color: #666;"
                             size="large">取消
                  </el-button>
                </div>
              </div>
              <div class="product-price">
                <span class="price-sale">¥{{ product.specPrice }}</span>
              </div>
              <div class="product-quantity">
                <el-input-number v-model="product.specNumber" :min="1" :max="10"
                                 style="width: 100px;"
                                 @change="handleQuantityChange(product.goodId, product.specId,product.specNumber)"></el-input-number>
              </div>
              <div class="product-actions">
                <el-button class="move-to-favorite" @click="moveToFavorite(product)">移入收藏</el-button>
                <el-button class="delete-item" @click="deleteItem(product)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <a style="color: #666; text-align: center; font-size: 16px;">没有更多了</a>
      </div>
      <div class="col-3">
        <div class="container cart-checkout">
          <div class="title" style="margin-bottom: 40px;">结算明细</div>
          <div v-if="selectedList.length === 0">
            <img class="image" src="@/images/bucket_none.png" alt="购物车图标">
            <div class="message">选择商品查看实际支付价格</div>
          </div>
          <div class="image-container" v-else>
            <div v-for="(item, index) in selectedList" :key="index" class="image-item">
              <img :src="item.specImage" alt="商品图片" @click="deselectionItem(item)">
              <span class="image-caption">取消选择</span>
            </div>
          </div>
          <div class="total">合计：</div>
          <div class="price">¥ {{ totalPrice() }}</div>
          <button class="button" @click="Checkout">结算</button>
        </div>
      </div>

    </div>
  </div>
  <FooterComponent/>
  <RightWidgetComponent ref="rightWidget"></RightWidgetComponent>
</template>

<script setup>

import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import {onMounted, ref} from "vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import axios from "axios";
import useUser from "@/composables/useUser";

const sidebarComponent = ref(null);

const {UserInfoForm, initUserSession} = useUser()

function initSidebar() {
  sidebarComponent.value.setActive(2);
}

const storeData = ref([]);
const isAllSelected = ref(false);

async function initData() {
  try {
    const response = await axios.post('/api/good/getCartList', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });

    if (response.data !== null) {
      storeData.value = response.data; // 结构为数组
    }
  } catch (error) {
    ElMessage.error("获取购物车数据失败");
  }
}

function totalQuantity() {
  let uniqueProducts = new Set();
  storeData.value.forEach(store => {
    store.goods.forEach(product => {
      uniqueProducts.add(product.specId); // 使用商品ID来计算种类
    });
  });
  return uniqueProducts.size; // 返回不同商品的数量
}

function handleToChat(shopId) {
  router.push('/talkToStore?id=' + shopId);
}

function handleShop(id) {
  router.push('/shop?shopId=' + id);
}

function handleToProductDetail(goodId) {
  router.push('/goodDetail?id=' + goodId);
}

async function handleQuantityChange(goodId, specId, specNumber) {
  try {
    const response = await axios.post('/api/good/updateGoodCart', {
      userId: UserInfoForm.value.id,
      goodId: goodId,
      specId: specId,
      number: specNumber
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });

    if (response.data.code === 200) {
      await initData(); // 更新购物车数据
      selectedList.value = selectedList.value.filter(item => item.specId !== specId);
    } else if (response.data.code === 400) {
      ElMessage.error("修改购物车数据失败");
    }
  } catch (error) {
    ElMessage.error("修改购物车数据失败");
  }
}

const selectedList = ref([]);

function toggleSelectAll() {
  storeData.value.forEach(store => {
    store.goods.forEach(product => {
      product.goodSelected = isAllSelected.value; // 指向正确的属性
      if (product.goodSelected) {
        addToSelectedList(product); // 添加到选中列表
      } else {
        removeFromSelectedList(product); // 从选中列表中移除
      }
    });
    store.shopSelect = isAllSelected.value; // 更新商店的选中状态
  });
}

function toggleSelectShop(store) {
  store.goods.forEach(product => {
    product.goodSelected = store.shopSelect;
    if (product.goodSelected) {
      addToSelectedList(product); // 添加到选中列表
    } else {
      removeFromSelectedList(product); // 从选中列表中移除
    }
  });
  updateSelectStatus();
}

function toggleSelect(product) {
  if (!product.goodSelected && isAllSelected.value) {
    isAllSelected.value = false; // 取消全选状态
  }

  if (product.goodSelected) {
    addToSelectedList(product); // 添加到选中列表
  } else {
    removeFromSelectedList(product); // 从选中列表中移除
  }
  updateSelectStatus();
  updateShopSelectStatus(product);
}

function addToSelectedList(product) {
  if (!selectedList.value.includes(product)) {
    selectedList.value.push(product);
  }
}

function removeFromSelectedList(product) {
  const index = selectedList.value.indexOf(product);
  if (index > -1) {
    selectedList.value.splice(index, 1);
  }
}

function updateSelectStatus() {
  isAllSelected.value = storeData.value.every(store =>
      store.goods.every(product => product.goodSelected)
  );
}

function updateShopSelectStatus(product) {
  // 找到该商品所属的商店
  const store = storeData.value.find(shop =>
      shop.goods.some(p => p.specId === product.specId)
  );

  // 检查该商店的所有商品选中状态
  if (store) {
    store.shopSelect = store.goods.every(p => p.goodSelected); // 根据商品状态更新商店的选中状态
  }
}

const specList = ref([]);
const selectedSpecImage = ref()
const selectedSpecName = ref()
const selectedSpecPrice = ref()
const selectedSpecId = ref()

async function getSpecList(goodId) {
  try {
    const response = await axios.post('/api/good/getSpecList', {
      goodId: goodId
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data !== null) {
      return response.data;
    } else
      ElMessage.error("获取商品规格失败");
  } catch (error) {
    ElMessage.error("获取商品规格失败");
  }
}

async function handleProductSpecDetail(product) {
  product.showDetails = !product.showDetails;
  if (product.showDetails) {
    selectedSpecImage.value = product.specImage;
    selectedSpecId.value = product.specId;
    selectedSpecName.value = product.specName;
    selectedSpecPrice.value = product.specPrice;
    specList.value = await getSpecList(product.goodId);
  }
}

function selectSpecification(spec) {
  selectedSpecName.value = spec.specName;
  selectedSpecPrice.value = spec.price;
  selectedSpecId.value = spec.specificationID;
  selectedSpecImage.value = spec.showurl;
}

const rightWidget = ref(null);

async function updateProductSpec(product) {
  try {
    const response = await axios.post('/api/good/updateGoodCartSpec', {
      userId: UserInfoForm.value.id,
      goodId: product.goodId,
      oldSpecId: product.specId,
      newSpecId: selectedSpecId.value,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 400)
      ElMessage.error("修改购物车数据失败");
    else {
      await initData();
      await rightWidget.value.getCartCount();
      selectedList.value = selectedList.value.filter(item => item.specId !== product.specId);
    }
  } catch (error) {
    ElMessage.error("修改购物车数据失败");
  }
}

async function moveToFavorite(product) {
  try {
    const response = await axios.post('/api/good/moveToFavorite', {
      userId: UserInfoForm.value.id,
      goodId: product.goodId,
      specId: product.specId,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await initData();
      await rightWidget.value.getCartCount();
      selectedList.value = selectedList.value.filter(item => item.specId !== product.specId);
    } else if (response.data.code === 400) {
      ElMessage.error("移入收藏失败");
    }
  } catch (error) {
    ElMessage.error("移入收藏失败");
  }
}

async function deleteItem(product) {
  try {
    const response = await axios.post('/api/good/deleteGoodCart', {
      userId: UserInfoForm.value.id,
      goodId: product.goodId,
      specId: product.specId,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await initData();
      await rightWidget.value.getCartCount();
      selectedList.value = selectedList.value.filter(item => item.specId !== product.specId);
    } else if (response.data.code === 400) {
      ElMessage.error("删除失败");
    }
  } catch (error) {
    ElMessage.error("删除失败");
  }
}

async function moveToFavoriteList() {
  for (const item of selectedList.value) {
    await moveToFavorite(item);
  }
  selectedList.value = [];
}

async function deleteSelected() {
  for (const item of selectedList.value) {
    await deleteItem(item);
  }
  selectedList.value = [];
}

function deselectionItem(item) {
  item.goodSelected = false;
  removeFromSelectedList(item);
  updateSelectStatus();
  updateShopSelectStatus(item);
}

function totalPrice() {
  let total = 0;
  selectedList.value.forEach(product => {
    total += product.specPrice * product.specNumber;
  });
  return total.toFixed(2);
}

async function storePayGoods() {
  try {
    const response = await axios.post('/api/pay/storePayGoods', {
      bucket_buy: selectedList.value,
      user_id: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data === "fail") {
      ElMessage.error("提交订单失败")
    } else
      await router.push('/payGoods/ready?token=' + response.data)
  } catch (error) {
    ElMessage.error("提交订单失败")
  }
}

async function Checkout() {
  if (selectedList.value.length === 0) {
    ElMessage.error("请选择商品");
  } else {
    await storePayGoods();
  }
}

onMounted(async () => {
  initUserSession();
  initSidebar();
  await initData();
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
  min-height: calc(100vh - 100px);
}

.title {
  text-align: start;
  color: #ff5000;
  font-size: 28px;
  font-weight: bold;
  padding: 20px;
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  background-color: #f8f8f8;
  padding: 10px;
  border-radius: 8px;

  .cart-summary-info {
    display: flex;
    align-items: center;

    .cart-summary-title {
      font-size: 16px;
      font-weight: bold;
      color: #ff5000;
      margin-right: 20px;
    }

    .discount-info {
      font-size: 14px;
      color: #ff0000;
    }
  }

  .cart-summary-actions {
    display: flex;
    align-items: center;

    .select-all-checkbox {
      margin-right: 10px;
    }

    .select-all-text {
      font-size: 14px;
      margin-right: 20px;
    }

    .move-to-favorite-btn,
    .delete-selected-btn {
      background: none;
      border: 1px solid #ccc;
      padding: 6px 12px;
      margin-right: 10px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      color: #333;

      &:hover {
        background-color: #f0f0f0;
      }
    }
  }
}

.cart-item {
  font-family: Arial, sans-serif;
  padding: 15px;
  border-top: 1px solid #ebebeb;

  .store-info {
    display: flex;
    align-items: center;
    margin-bottom: 15px;

    .select-checkbox {
      margin-right: 10px;
    }

    .store-name {
      font-weight: bold;
      color: #ff5000;
      font-size: 18px;
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

  .product-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  .product-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px;
    border: none;
    background-color: #fff;

    .select-checkbox {
      margin-right: 10px;
    }

    .product {
      position: relative;
      display: inline-block;
    }

    .large-image {
      display: none;
      position: absolute;
      top: -50px;
      left: 100%;
      width: 200px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
      border-radius: 10px;
      background-color: white;
      padding: 5px;
    }

    .product:hover .product-image {
      filter: brightness(60%);
    }

    .product:hover .large-image {
      display: block;
    }

    .product-image {
      width: 80px;
      height: 80px;
      object-fit: cover;
      margin-right: 15px;
      border-radius: 5px;
    }

    .product-details {
      flex: 3;
      display: flex;
      flex-direction: column;

      .product-name-and-attributes {
        display: flex;
        flex-direction: column;

        .product-name {
          font-size: 16px;
          color: #333;
          font-weight: 500;
          margin-bottom: 5px;
          text-align: start;
        }

        .product-name:hover {
          color: #e02020;
        }
      }
    }

    .product-spec {
      position: relative;
      display: inline-block;

      .product-attributes {
        position: relative;
        margin-left: 40px;
        padding: 20px;
        width: 150px;
        text-align: center;
        border-radius: 5px;
        color: #999;
      }

      .product-spec-details {
        position: absolute;
        top: 100%;
        left: 0;
        width: 75vh;
        min-height: 30vh;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        border-radius: 10px;
        background-color: white;
        padding: 5px;
        z-index: 100;

        .el-button:hover,
        .el-button.active {
          border-color: #e02020;
          color: #e02020;
          background-color: transparent;
        }

      }
    }

    .edit-label {
      display: none;
      position: absolute;
      top: 0;
      right: 0;
      background-color: #ff5000;
      color: white;
      padding: 5px;
      font-size: 12px;
      border-radius: 0 0 0 5px;
    }

    .product-attributes:hover, .product-attributes.active {
      border: 1px dashed #ff5000;
    }

    .product-attributes:hover .edit-label,
    .product-attributes.active .edit-label {
      display: block;
    }

    .product-price {
      flex: 1;
      text-align: right;

      .price-sale {
        font-size: 16px;
        color: #ff5000;
        font-weight: bold;
      }

      .price-original {
        font-size: 14px;
        color: #999;
        text-decoration: line-through;
        margin-left: 8px;
      }
    }

    .product-quantity {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;

      .quantity-btn {
        background-color: #f5f5f5;
        border: 1px solid #ccc;
        width: 30px;
        height: 30px;
        text-align: center;
        line-height: 28px;
        cursor: pointer;
      }

      .quantity-input {
        width: 40px;
        height: 30px;
        text-align: center;
        border: 1px solid #ccc;
        margin: 0 5px;
      }
    }

    .product-actions {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: flex-end;

      button {
        background: none;
        border: none;
        color: #007bff;
        cursor: pointer;
        margin-bottom: 5px;

        &:hover {
          text-decoration: underline;
        }
      }

      .move-to-favorite {
        color: #999;
        text-decoration: none;
      }

      .delete-item {
        color: #ff5000;
        text-decoration: none;
      }
    }
  }
}

@container-width: 340px;
@container-height: 350px;
@border-radius: 10px;
@font-family: Arial, sans-serif;

.container {
  width: @container-width;
  height: @container-height;
  border: 1px solid #eee;
  border-radius: @border-radius;
  position: fixed;
  margin: 20px auto;
  text-align: center;
  font-family: @font-family;
  overflow: visible;

  .title {
    position: absolute;
    top: 10px;
    left: 15px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #e02020;
  }

  .image {
    position: absolute;
    top: 60px;
    left: 50%;
    transform: translateX(-50%);
    opacity: 0.5;
  }

  .message {
    position: absolute;
    top: 170px;
    left: 50%;
    transform: translateX(-50%);
    color: #e02020;
    font-weight: bold;
  }

  .image-container {
    top: 80px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    width: 100%;
    position: relative;
    z-index: 10;

    .image-item {
      position: relative;
      display: inline-block;

      img {
        width: 75px;
        height: 75px;
        border-radius: 10px;
        position: relative;
        cursor: pointer;
        margin-bottom: 10px;
      }

      .image-caption {
        display: none;
        font-size: 12px;
        position: absolute;
        top: 0;
        right: 0;
        background-color: rgba(0, 0, 0, 0.5); /* 半透明背景 */
        color: white; /* 文字颜色 */
        padding: 5px; /* 内边距 */
        border-radius: 5px; /* 圆角边框 */
      }

      &:hover .image-caption {
        display: block;
      }
    }
  }

  .total {
    position: absolute;
    bottom: 80px;
    left: 30px;
    font-weight: bold;
  }

  .price {
    position: absolute;
    bottom: 80px;
    right: 30px;
    color: #e02020;
    font-weight: bold;
  }

  .button {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    background-color: #e02020;
    color: white;
    width: 80%;
    padding: 10px;
    border: none;
    border-radius: 5px;
    font-weight: bold;
    cursor: pointer;
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

.night-mode .product-card {
  background-color: #3c3c3c;
}

.night-mode .search-input {
  background-color: #3c3c3c;
  color: #e0e0e0;
}
</style>