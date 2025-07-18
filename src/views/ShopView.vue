<template>
  <HeaderComponent/>
  <el-container>
    <div class="shop-container row">
      <div class="col-7 row">
        <div class="col-3 row">
          <div class="col-3">
            <div class="live-indicator" v-if="isLive" @click="()=>{router.push('/live/show?id='+shopid)}">
              <img :src="shopForm.shopAvatar" class="shop-avatar" alt="shop-avatar">
              <div class="live-label">直播中</div>
            </div>
            <img v-else :src="shopForm.shopAvatar" class="shop-avatar" alt="shop-avatar">
          </div>
          <div class="col-9">
            <p class="shop-name" style="font-size: 16px; ">{{ shopForm.shopName }}</p>
            <p class="shop-desc" style="font-size: 14px;">{{ shopForm.shopDesc }}...</p>
          </div>
        </div>
        <div class="col-7" style="margin-top: 15px;">
          <el-tag type="danger">
            <el-rate
                v-model="value"
                disabled
                show-score
                text-color="#e23030"
                score-template="|&nbsp;&nbsp;{value}"
            />
          </el-tag>
          &nbsp;
          <el-tag type="info">
            客服满意度96%
          </el-tag>
          &nbsp;
          <el-tag type="info">
            平均13小时发货
          </el-tag>
          &nbsp;
          <el-tag type="info">
            物流体验优秀
          </el-tag>
        </div>
      </div>
      <div class="col-3 row" style="text-align: start;">
      </div>
      <div class="col-2" style="margin-top: 15px; text-align: end;">
        <el-button>
          <a class="bi bi-chat" @click="handleChat()"></a>&nbsp;
          联系客服
        </el-button>
        <el-button v-if="!followed" @click="followShop()">
          <a class="bi bi-share-fill"></a>
          关注店铺
        </el-button>
        <el-button v-else @click="unFollowShop()">
          <a class="bi bi-share-fill"></a>
          取消关注
        </el-button>
      </div>
    </div>
  </el-container>
  <el-container style="min-height: 65vh;">
    <el-row>
      <el-col :span="4" v-for="product in Products" :key="product.id">
        <el-tooltip
            class="box-item"
            effect="dark"
            :content="product.goodName"
            placement="right"
        >
          <el-card shadow="hover" class="product-card" @click="handleGoodDetail(product.goodID)">
            <img :src="product.showURL" class="product-image" alt="Product Image">
            <div class="product-info">
              <p v-if="product.goodName.length < 10">{{ product.name }}</p>
              <p v-else>{{ product.goodName.slice(0, 10) }}...</p>
              <h4 class="product-price">￥{{ product.price }}</h4>
              <div class="row">
                <p class="col-6" style="color: #999; font-size: 14px;" v-if="product.comments>=100">
                  {{ product.comments }}+人评价</p>
                <p class="col-6" style="color: #999; font-size: 14px;" v-else>{{
                    product.comments
                  }}人评价</p>
              </div>
            </div>
          </el-card>
        </el-tooltip>
      </el-col>
    </el-row>
  </el-container>
  <FooterComponent/>
  <RightWidgetComponent/>
</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import {onMounted, ref} from "vue";
import router from "@/router";
import axios from "axios";
import {ElMessage} from "element-plus";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const shopid = ref();
const value = 4.8;
const shopForm = ref({});
const followed = ref(false);

async function initShopInfo() {
  try {
    const response = await axios.post('/api/shop/findShopById', {
      shopId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data !== null) {
      shopForm.value = response.data;
    } else
      ElMessage.error('获取店铺信息失败，请稍后再试');
  } catch (err) {
    ElMessage.error('获取店铺信息失败，请稍后再试');
  }
}

async function initFollowShop() {
  try {
    const response = await axios.post('/api/shop/findFollowShop', {
      userId: UserInfoForm.value.id,
      shopId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    followed.value = response.data.code === 200;
  } catch (err) {
    ElMessage.error('获取关注状态失败，请稍后再试');
  }
}

async function followShop() {
  try {
    const response = await axios.post('/api/shop/followShop', {
      userId: UserInfoForm.value.id,
      shopId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code === 200) {
      followed.value = true;
      ElMessage.success('关注成功');
    } else {
      ElMessage.error('关注失败，请稍后再试');
    }
  } catch (err) {
    ElMessage.error('关注失败，请稍后再试');
  }
}

async function unFollowShop() {
  try {
    const response = await axios.post('/api/shop/unfollowShop', {
      userId: UserInfoForm.value.id,
      shopId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code === 200) {
      followed.value = false;
      ElMessage.success('取消关注成功');
    } else {
      ElMessage.error('取消关注失败，请稍后再试');
    }
  } catch (err) {
    ElMessage.error('取消关注失败，请稍后再试');
  }
}

const Products = ref([]);

async function initGoodInfo() {
  try {
    const response = await axios.post('/api/good/getGoodByShopId', {
      shopId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data !== null) {
      Products.value = response.data;
    } else
      ElMessage.error('获取商品信息失败，请稍后再试');
  } catch (err) {
    ElMessage.error('获取商品信息失败，请稍后再试');
  }
}

function handleChat() {
  router.push('/talkToStore?id=' + shopForm.value.id);
}

const handleGoodDetail = (id) => {
  router.push('/goodDetail?id=' + id);
}

const isLive = ref(false);

async function checkLive() {
  try {
    const response = await axios.post('/api/live/isLiveVisible', {
      roomId: shopid.value,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      isLive.value = true;
    }
  } catch (error) {
    ElMessage.error('获取直播状态失败')
  }
}

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search);
  shopid.value = urlParams.get('shopId');
  initUserSession();
  await initShopInfo();
  await initFollowShop()
  await initGoodInfo();
  await checkLive();
});
</script>

<style scoped lang="less">
.shop-container {
  margin: 120px 20px 20px;
  width: 100%;
  height: 10vh;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
  text-align: left;
}

.shop-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.shop-name {
  font-weight: bold;
}

.product-card {
  cursor: pointer;
  margin-bottom: 30px;
  border-radius: 10px;
  height: 350px;
}

.product-card:hover {
  border-color: #e02020;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-info {
  margin-top: 10px;
  text-align: center;
}

.product-price {
  color: #e02020;
  font-weight: bold;
}

.live-indicator {
  position: relative;
  display: inline-block;
}

.live-label {
  position: absolute;
  bottom: -10px; /* 调整位置 */
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(255, 0, 0, 0.8);
  color: white;
  padding: 5px 1px;
  border-radius: 10px;
  font-size: 6px;
  font-weight: bold;
}

@keyframes ripple {
  0% {
    transform: scale(1);
    opacity: 1.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 2;
  }
  100% {
    transform: scale(1);
    opacity: 1.5;
  }
}

.live-indicator::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  border: 2px solid rgba(255, 0, 0, 0.5); /* 外圈颜色 */
  animation: ripple 1.5s infinite; /* 动画周期 */
  z-index: 1; /* 位于图片下面 */
}
</style>
