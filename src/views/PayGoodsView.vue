<template>
  <HeaderComponent/>
  <div class="main-content row" style="min-height: 90vh;">
    <div class="col-9">
      <div class="address-container">
        <div class="header-container row">
          <div class="col-2">
            <a style="font-size: 18px; font-weight: bold; text-align: left;">确认收货地址</a>
          </div>
          <div style="text-align: right;" class="col-10">
            <AddAddressComponent></AddAddressComponent>
            <el-button @click="router.push('/profile/address')"
                       class="link-text">管理地址
            </el-button>
          </div>
        </div>

        <div class="address-list">
          <el-card v-for="(address, index) in addresses"
                   :key="index"
                   class="address-card"
                   :class="{selected: address.isSelected}"
                   @click="selectAddress(address)">
            <div class="row">
              <div class="icon col-2">
                <el-icon>
                  <Location :class="{selected: address.isSelected}"></Location>
                </el-icon>
              </div>
              <div class="col-10 details">
                <div class="address">
                  <el-tag v-if="address.isDefault"
                          style="background-color: rgba(240, 63, 46, 0.5); color: #ff5000; font-weight: bold;">
                    默认
                  </el-tag>
                  {{ address.region.substring(0, 10) }}
                </div>
                <div class="place">{{ address.address }}</div>
                <div class="contact">{{ address.receiver }} {{ address.phone }}</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      <div class="order-container">
        <div class="order-content">
          <div class="order-header">
            <span class="store-name">店铺宝贝</span>
            <span class="product-info">商品属性</span>
            <span class="price-info">原价</span>
            <span class="quantity-info">数量</span>
            <span class="subtotal-info">小计</span>
          </div>
          <div>
            <div v-for="(shop,index) in orderItems" :key="shop.shopId" class="shop-item">
              <div class="shop-name">{{ shop.shopName }}</div>
              <div v-for="item in shop.goods" :key="item.specId" class="order-item">
                <div class="product-details">
                  <img :src="item.specImage" alt="商品图片" class="product-image"/>
                  <div class="product-description">
                    {{ item.goodName }}
                    <div class="sku-info">规格: {{ item.specName }}</div>
                  </div>
                </div>
                <div class="price">¥{{ item.specPrice }}</div>
                <div class="quantity">
                  <el-input-number v-model="item.specNumber" :min="1" :max="10"
                                   @change="changeQuantity(item)"></el-input-number>
                </div>
                <div class="subtotal">¥{{ item.specPrice * item.specNumber }}</div>
              </div>
              <div class="order-footer">
                <div class="remark">
                  <span>订单备注</span>
                  <el-input type="textarea" placeholder="请填写，传达给商家可见，建议和商家协商一致" v-model="shop.remark"
                            maxlength="200" resize="none"></el-input>
                </div>
                <div class="delivery-service">
                  <span>配送服务 快递 包邮</span>
                </div>
                <div class="total-price">
                  合计(含运费) <span class="total-amount">¥{{ orderTotalPrice(shop.goods) }}</span>
                </div>
              </div>
              <el-divider style="border-width: 2px; " v-if="index < orderItems.length - 1"></el-divider>
            </div>
          </div>

        </div>
      </div>
    </div>
    <div class="col-3">
      <div class="container cart-checkout">
        <div class="title" style="margin-bottom: 40px;">付款详细&nbsp;&nbsp;<span style="font-size: 12px;">共<a
            style="color: #4c4c4c; font-weight: bold; font-size: 20px">{{  }}</a>件商品</span></div>
        <div class="message">
          <a style="text-align: start">商品总价：</a>
          <a style="text-align: end">¥ {{ orderTotalPriceAll() }}</a>
        </div>
        <div class="cut-price">
          <a style="text-align: start">共减免：</a>
          <a style="text-align: end">¥ {{ orderCutPrice }}</a>
        </div>
        <div class="total">合计：</div>
        <div class="price">¥ {{ orderTotalPriceAll() - orderCutPrice }}</div>
        <button class="button" @click="submitOrder">提交订单</button>
      </div>
    </div>
  </div>
  <FooterComponent/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import router from "@/router";
import HeaderComponent from "@/components/HeaderComponent.vue";
import AddAddressComponent from "@/components/AddAddressComponent.vue";
import {Location} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import FooterComponent from "@/components/FooterComponent.vue";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const Token = ref();
const addresses = ref([]);
const orderItems = ref([]);
const orderCutPrice = 0;

async function initAddresses() {
  try {
    const response = await axios.post('/api/addresses/getAllAddresses', {
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data) {
      addresses.value = response.data.map(address => ({
        id: address.id,
        receiver: address.consignee,
        phone: address.phone,
        region: address.area,
        address: address.detailed_address,
        isDefault: address.status === 'default',
        isSelected: address.isDefault,
      }));
    } else {
      ElMessage.error('获取地址列表失败');
    }
  } catch (err) {
    ElMessage.error('获取地址列表失败');
  }
}

async function initGoods() {
  const urlParams = new URLSearchParams(window.location.search);
  Token.value = urlParams.get('token');
  try {
    const response = await axios.post('/api/pay/getPayGoods', {
      token: Token.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data) {
      orderItems.value = response.data;
    } else {
      ElMessage.error('获取商品信息失败');
    }
  } catch (error) {
    ElMessage.error('获取商品信息失败');
  }
}

function selectAddress(address) {
  addresses.value.forEach(item => {
    item.isSelected = false;
  });
  address.isSelected = true;
}

async function changeQuantity(item) {
  try {
    const response = await axios.post('/api/pay/changGoodNum', {
      token: Token.value,
      specId: item.specId,
      num: item.specNumber,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data.code === 200)
      await initGoods();
    else if (response.data.code === 404)
      ElMessage.error('购物车信息不存在，请重新提交');
    else if (response.data.code === 400)
      ElMessage.error('修改商品数量失败');
  } catch (e) {
    ElMessage.error('修改商品数量失败');
  }
}

const orderTotalPrice = (goods = []) => {
  let total = 0;
  goods.forEach(item => {
    total += item.specPrice * item.specNumber;
  });
  return total;
};

const orderTotalPriceAll = () => {
  let total = 0;
  orderItems.value.forEach(shop => {
    total += orderTotalPrice(shop.goods);
  });
  return total;
};

async function submitOrder() {
  let addressId;
  try {
    addressId = addresses.value.find(address => address.isSelected).id;
  } catch (e) {
    ElMessage.error('请选择收货地址');
    return;
  }
  const data = {
    userId: UserInfoForm.value.id,
    token: Token.value,
    addressId: addressId,
  }
  try {
    const response = await axios.post('/api/pay/submitOrder', data, {
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

onMounted(async () => {
  initUserSession();
  await initAddresses();
  await initGoods();
})
</script>

<style scoped lang="less">
.main-content {
  flex: 1;
  padding: 20px;
  background-color: #ffffff;
  margin-top: 90px;
  min-height: calc(80vh - 100px);
}

.address-container {
  margin: 10px 40px;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  min-height: 25vh;
  padding: 20px;
  background-color: #f5f5f5;

  .header-container {
    width: 100%;
    margin-bottom: 20px;

    .link-text {
      margin-left: 10px;
      margin-bottom: 20px;
      text-decoration: none;
      color: #4c4c4c;
      background-color: transparent;
      border: none;
      cursor: pointer;
    }

    .link-text:hover {
      color: #e02020;
    }
  }

  .address-list {
    display: flex;
    gap: 15px;
    overflow-x: auto;
    padding-bottom: 10px;

    .address-card {
      width: calc(25% - 10px);
      height: 120px;
      padding: 3px;
      border-radius: 10px;
      border: 1px solid #e0e0e0;
      cursor: pointer;
      background-color: #ffffff;
      transition: all 0.3s ease;

      &.selected {
        border-color: #f03f2e;
        box-shadow: 0 0 10px rgba(240, 63, 46, 0.4);
        background-color: #ffecec;

        .status {
          color: #ffffff;
          background-color: #f03f2e;
          padding: 4px 8px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: bold;
          margin-top: 10px;
          display: inline-block;
        }
      }

      .icon {
        font-size: 24px;
        align-self: center;

        & .selected {
          color: #f03f2e;
        }
      }

      .details {
        text-align: left;

        .address, .contact {
          font-size: 14px;
          color: #4c4c4c;
          margin: 2px 0;
        }

        .place {
          font-weight: bold;
          font-size: 16px;
          color: #333;
          margin: 2px 0;
        }
      }
    }
  }
}

.shop-name {
  font-weight: bold;
  text-align: start;
  padding: 10px 20px;
  color: #ff5000;
}

.order-container {
  margin: 20px 40px;
  padding: 20px;
  border-radius: 10px;
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);

  .order-content {
    .order-header {
      display: grid;
      grid-template-columns: 20% 40% 10% 15% 15%;
      padding: 10px;
      font-weight: bold;
      color: #555;
      border-bottom: 1px solid #ddd;
      text-align: center;
    }

    .order-item {
      width: 100%;
      display: grid;
      grid-template-columns: 60% 10% 15% 15%;
      align-items: center;
      padding: 15px 20px;
      border-bottom: 1px solid #e0e0e0;
      text-align: center;

      .store-info {
        text-align: center;
      }

      .product-details {
        display: flex;
        align-items: center;

        .product-image {
          width: 80px;
          height: 80px;
          margin-right: 40px;
          margin-left: 40px;
        }

        .product-description {
          text-align: left;
          font-size: 14px;
          color: #333;
          margin-left: 40px;

          .sku-info {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
          }
        }
      }

      .price {
        color: #f03f2e;
        font-weight: bold;
      }

      .quantity {
        text-align: center;
      }

      .subtotal {
        color: #f03f2e;
        font-weight: bold;
      }
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      padding: 20px 0;

      .remark {
        width: 60%;

        el-input {
          width: 100%;
        }
      }

      .delivery-service, .total-price {
        width: 20%;
        text-align: center;
        align-items: center;
        font-size: 16px;
        color: #555;
      }

      .total-amount {
        align-items: center;
        font-size: 20px;
        color: #f03f2e;
        font-weight: bold;
      }
    }
  }
}

@container-width: 340px;
@container-height: 270px;
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
    top: 70px;
    left: 30%;
    transform: translateX(-50%);
    color: #e02020;
    font-weight: bold;
  }

  .cut-price {
    position: absolute;
    top: 100px;
    left: 27%;
    transform: translateX(-50%);
    color: #e02020;
    font-weight: bold;
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
</style>
