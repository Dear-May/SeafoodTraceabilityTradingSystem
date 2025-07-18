<template>
  <HeaderComponent/>
  <el-container>
    <div class="shop-container row">
      <div class="col-7 row">
        <div class="col-3 row">
          <div class="col-3">
            <div class="live-indicator" v-if="isLive" @click="()=>{router.push('/live/show?id='+shopForm.id)}">
              <img :src="shopForm.avatar" class="shop-avatar" alt="shop-avatar">
              <div class="live-label">直播中</div>
            </div>
            <img v-else :src="shopForm.avatar" class="shop-avatar" alt="shop-avatar">
          </div>
          <div class="col-9">
            <p class="shop-name" style="font-size: 16px; ">{{ shopForm.name }}</p>
            <p class="shop-desc" style="font-size: 14px;">{{ shopForm.description }}</p>
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
        <el-button @click="handleChat">
          <a class="bi bi-chat"></a>&nbsp;
          联系客服
        </el-button>
        <el-button @click="handleShop">
          <a class="bi bi-house"></a>
          进入店铺
        </el-button>
      </div>
    </div>
  </el-container>
  <el-container>
    <div class="good-container row">
      <div class="good-show row">
        <div class="col-1 ">
          <div v-for="(goods, index) in goodForm.images" :key="index" class="good-item"
               @mouseover="good_big_show = goods.image">
            <img :src="goods.image" alt="good-image" style="width: 100%; height: 100%;">
          </div>
        </div>
        <div class="col-4">
          <div>
            <div class="left">
              <img :src="good_big_show" alt="good-image"
                   style="width: 100%; height: 100%; border-radius: 10px; display: inline-block;">
              <!-- 鼠标层罩 -->
              <div v-show="topShow" class="top" :style="topStyle"></div>
              <!-- 最顶层覆盖了整个原图空间的透明层罩 -->
              <div
                  class="maskTop"
                  @mouseenter="enterHandler"
                  @mousemove="moveHandler"
                  @mouseout="outHandler"
              ></div>
            </div>
            <div v-show="rShow" class="right">
              <img
                  :style="r_img"
                  class="rightImg"
                  :src="good_big_show"
                  alt=""
              />
            </div>
          </div>
        </div>
        <div class="col-7 ">
          <el-container>
            <div class="good-item-desc">
              <h1 class="good-title" style="font-weight: bold; ">{{ goodForm.name }}</h1>
              <h3 class="good-title" style="font-weight: bold; color: #e02020; margin-bottom: 20px;">￥{{ showPrice }}
                <span class="good-price" style="font-size: 14px; color: #999; font-weight: normal; margin-left: 10px;">已售出: 10件</span>
                <span>
                    <el-button text @click="openDrawer"
                    >溯源信息查看</el-button
                    >
                </span>
              </h3>
              <div style="position: relative;">
                <p class="good-price"
                   style="font-size: 18px; color: #999; font-weight: normal; margin-left: 10px; margin-bottom: 20px;">配&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;送:&nbsp;&nbsp;&nbsp;&nbsp;
                  <span
                      style="color: #000000;">{{
                      goodForm.location
                    }}&nbsp;&nbsp;&nbsp;&nbsp;至&nbsp;&nbsp;&nbsp;&nbsp;</span>
                  <span style="color: #000000; font-size: 18px;" ref="boxRef">
                  {{ showAddress }}
                  <el-icon class="el-icon--right" @click="toggleAddressList">
                    <arrow-down v-if="!showAddressModal" @click="showAddressModal = true"></arrow-down>
                    <arrow-up v-else @click="showAddressModal = false"></arrow-up>
                  </el-icon>
                </span>
                  <span style="font-size: 18px; color: #999;">
                <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快递: 免运费 48小时内发货
              </span>
                </p>

                <div v-if="showAddressModal" class="address-list"
                     :style="{ top: addressListTop + 'px', left: addressListLeft + 'px' }">
                  <h4 class="address-title">选择收货地址</h4><br>
                  <div class="address-item" v-for="(item, index) in addresses.value" :key="index"
                       @click="changeAddress(item)">
                    <div class="header">
                      <span>{{ item.name }}</span>
                      <span v-if="item.isDefault" class="default">默认</span>
                    </div>
                    <p>{{ item.phone }}</p>
                    <p>{{ item.address }}</p>
                  </div>
                </div>
              </div>
              <p class="good-price"
                 style="font-size: 18px; color: #999; font-weight: normal; margin-left: 10px; margin-bottom: 20px;">保&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;障:&nbsp;&nbsp;&nbsp;&nbsp;
                <span style="color: #000000;">退货宝 7天无理由退货 极速退款</span>
              </p>
              <div class="good-price"
                   style="font-size: 18px; color: #999; font-weight: normal; margin-left: 10px; margin-bottom: 20px;">
                规格分类：&nbsp;&nbsp;
                <div v-for="(spec, index) in goodForm.spec" :key="index"
                     style="display: inline-block; margin-right: 10px;">
                  <el-button :class="{ active: activeSpec === spec }" color="#e23030" :dark="isDark" plain
                             @click="changPrice(spec)">{{
                      spec.name
                    }}
                  </el-button>
                </div>
              </div>
              <div class="good-price"
                   style="font-size: 18px; color: #999; font-weight: normal; margin-left: 10px; margin-bottom: 20px;">
                数&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;量:&nbsp;&nbsp;&nbsp;&nbsp;
                <el-input-number v-model="num" :min="1" :max="10"/>
                <span style="font-size: 18px; color: #999; margin-left: 10px;">有货</span>
              </div>
            </div>
            <el-footer style="bottom: 35vh;width: 100%; ">
              <el-divider></el-divider>
              <div style="display: flex; align-items: center;">
                <el-button-group
                    style="width: 40%;"
                >
                  <el-button type="primary" size="large" @click="checkOut" class="good-buy">
                    立即购买
                  </el-button>
                  <el-button type="primary" size="large" @click="addToCart" class="good-buy"
                             style="background-color: #e6a23c; border-radius: 0 10px 10px 0;">
                    加入购物车
                  </el-button>
                </el-button-group>
                <a class="good-star">
                  <i class="bi bi-star" v-if="!isFavorite" @click="changeFavorite(true)"></i>
                  <i class="bi bi-star-fill" style="color: #e02020;" v-else @click="changeFavorite(false)"></i>
                  <span>收藏</span>
                </a>
              </div>
            </el-footer>
          </el-container>
        </div>
      </div>
      <el-anchor :offset="70" direction="horizontal">
        <el-anchor-link :href="`#用户评价`">
          <a class="link-item">{{ locale['Basic Usage'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#参数信息`">
          <a class="link-item">{{ locale['Horizontal Mode'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#图文详细`">
          <a class="link-item">{{ locale['Scroll Container'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#看了又看`">
          <a class="link-item">{{ locale['Looks good'] }}</a>
        </el-anchor-link>
      </el-anchor>
      <el-container>
        <div class="good-comment">
          <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="用户评价">
            用户评价</h1>
          <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleTabClick">
            <el-tab-pane label="全部" name="first">
              <div class="good-comment-item" v-for="(comment, index) in filterComment" :key="index">
                <div class="good-comment-avatar">
                  <img :src="comment.userAvatar" alt="good-avatar">
                </div>
                <div class="good-comment-content">
                  <h3 class="good-comment-title">{{ comment.userName }}</h3>
                  <el-rate
                      v-model="comment.rate"
                      disabled
                      text-color="#ff9900"
                  />
                  <p class="good-comment-desc">
                    {{ comment.commentText }}
                  </p>
                  <p class="good-comment-spec">
                    规格: <span>{{ comment.specname }}</span>
                  </p>
                  <p class="good-comment-time">
                    时间: <span>{{ formatDate(comment.time) }}</span>
                  </p>
                  <div class="good-comment-images" v-if="comment.images!== null">
                    <div v-for="(image, index) in comment.images" :key="index">
                      <el-image :src="image" fit="contain" width="50px" height="50px"></el-image>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="有图" name="second">
              <div class="good-comment-item" v-for="(comment, index) in filterComment" :key="index">
                <div class="good-comment-avatar">
                  <img :src="comment.userAvatar" alt="good-avatar">
                </div>
                <div class="good-comment-content">
                  <h3 class="good-comment-title">{{ comment.userName }}</h3>
                  <el-rate
                      v-model="comment.rate"
                      disabled
                      text-color="#ff9900"
                  />
                  <p class="good-comment-desc">
                    {{ comment.commentText }}
                  </p>
                  <p class="good-comment-spec">
                    规格: <span>{{ comment.specname }}</span>
                  </p>
                  <p class="good-comment-time">
                    时间: <span>{{ formatDate(comment.time) }}</span>
                  </p>
                  <div class="good-comment-images">
                    <div v-for="(image, index) in comment.images" :key="index">
                      <el-image :src="image" fit="contain" width="50px" height="50px"></el-image>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
          <div class="good-desc">
            <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="参数信息">
              参数信息</h1>
            <el-descriptions
                class="margin-top"
                :column="3"
                border
            >
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    商品名称
                  </div>
                </template>
                {{ goodForm.name }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    上架时间
                  </div>
                </template>
                {{ goodForm.uploadTime }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    <el-icon :style="iconStyle">
                      <location/>
                    </el-icon>
                    产地
                  </div>
                </template>
                {{ goodForm.location }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    <el-icon :style="iconStyle">
                      <tickets/>
                    </el-icon>
                    种类
                  </div>
                </template>
                <el-tag size="small">School</el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    <el-icon :style="iconStyle">
                      <office-building/>
                    </el-icon>
                    地址
                  </div>
                </template>
                {{ goodForm.location }}
              </el-descriptions-item>
            </el-descriptions>
            <el-divider/>
          </div>
          <div class="good-desc">
            <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="看了又看">
              图文详细</h1>
            <div class="good-desc-content">
              <p>
                商品详情页的图文详情，可以是商品的介绍、规格、包装、售后保障等内容，让用户更直观的了解商品。
              </p>
              <p>
                图文详情页的布局可以根据产品的类型、内容、阅读习惯等因素进行调整，以提升用户的阅读体验。
              </p>
              <p>
                图文详情页的设计要突出商品的主要特点，突出商品的图片、文字，并配合配色、字体、排版等元素，让用户快速理解商品的价值。
              </p>
            </div>
            <el-divider/>
            <div class="good-desc">
              <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="${locale['looks-good']}">
                看了又看</h1>
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
      </el-container>
    </div>
    <el-drawer
        v-model="table"
        title="溯源信息"
        direction="rtl"
        size="25%"
    >
      <el-tabs v-model="activeTab" class="trace-tabs">
        <el-tab-pane
            v-for="trace in traceData"
            :key="trace.id"
            :label="formatDate(trace.date)"
            :name="trace.id"
        >
          <!-- 时间线展示 -->
          <el-timeline>
            <el-timeline-item
                v-for="(entry, index) in trace.entries"
                :key="index"
                :timestamp="formatDate(entry.timestamp)"
                placement="top"
            >
              <div class="timeline-entry">
                <p class="timeline-content">
                  <strong>操作人:</strong> {{ entry.operator }}
                  <br/>
                  {{ entry.content }}
                </p>
                <!-- 图片展示 -->
                <div class="timeline-images" v-if="entry.images && entry.images.length">
                  <el-image
                      v-for="(image, imgIndex) in entry.images"
                      :key="imgIndex"
                      :src="image"
                      class="timeline-image"
                  ></el-image>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>

  </el-container>
  <FooterComponent/>
  <RightWidgetComponent ref="rightWidget"/>
</template>

<script setup>

import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import {ArrowDown, ArrowUp, Location, OfficeBuilding, Tickets} from "@element-plus/icons-vue";
import {onBeforeUnmount, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import router from "@/router";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const goodID = ref();
const shopForm = ref({
  id: "",
  name: "",
  description: "",
  avatar: "",
});

const goodForm = ref({
  name: "",
  location: "",
  uploadTime: "",
  spec: [
    {
      name: "",
      value: "",
    }
  ],
  images: [
    {
      image: "",
    }
  ],
})

const good_big_show = ref();
const showPrice = ref();
const selectSpecId = ref();
const addresses = []
const showAddress = ref();
const selectAddressId = ref();
const isFavorite = ref(false);
const traceData = ref([]);
const table = ref(false);
const activeTab = ref("");

async function loadTraceData() {
  try {
    const response = await axios.post('/api/trace/getGoodTrace', {
      GoodID: goodID.value
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data != null)
      traceData.value = response.data;
    else
      ElMessage.error("获取溯源信息失败")
  } catch (e) {
    ElMessage.error("获取溯源信息失败")
  }
}

const openDrawer = async () => {
  await loadTraceData();
  activeTab.value = traceData.value[0]?.id; // 默认激活第一个标签
  table.value = true;
};

async function getAddressList() {
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
        name: address.consignee,
        phone: address.phone,
        address: address.area,
        isDefault: address.status === 'default'
      }));
      showAddress.value = addresses.value[0].address;
      selectAddressId.value = addresses.value[0].id;
    } else {
      ElMessage.error('获取地址列表失败');
    }
  } catch (err) {
    ElMessage.error('获取地址列表失败');
  }
}

async function getFavoriteStatus(goodsId) {
  try {
    const response = await axios.post('/api/good/isFavourite', {
      userId: UserInfoForm.value.id,
      goodId: goodsId
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      isFavorite.value = true;
    }
  } catch (err) {
    ElMessage.error('获取收藏状态失败')
  }
}

async function init() {
  try {
    const response = await axios.post('/api/good/getGoodAllInfo', {
      goodId: goodID.value
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.shopAvatar !== null) {
      // 商家信息
      shopForm.value.id = response.data.shopID;
      shopForm.value.name = response.data.shopName;
      shopForm.value.avatar = response.data.shopAvatar;
      shopForm.value.description = response.data.shopDesc.substring(0, 10) + "...";
      // 主商品信息
      goodForm.value.name = response.data.goodName;
      document.title = response.data.goodName;
      goodForm.value.location = response.data.goodLocation;
      goodForm.value.uploadTime = response.data.goodUploadTime;
      // 规则信息
      const showUrls = response.data.specificationEntities.map(item => item.Showurl);
      goodForm.value.images = [];
      showUrls.forEach(image => {
        goodForm.value.images.push({
          image: image
        })
      })
      goodForm.value.spec = response.data.specificationEntities.map(item => {
        return {
          id: item.specificationID,
          name: item.specName,
          value: item.price
        }
      })
      good_big_show.value = goodForm.value.images[0].image;
      showPrice.value = goodForm.value.spec[0].value;
      selectSpecId.value = goodForm.value.spec[0].id;
      await getAddressList();
      await addFootMark();
      await getFavoriteStatus(goodID.value);
    }
  } catch (error) {
    ElMessage.error("获取商品信息失败");
  }
}

async function addFootMark() {
  try {
    const response = await axios.post('/api/good/addGoodFootMark', {
      goodId: goodID.value,
      userId: UserInfoForm.value.id
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 400)
      ElMessage.error("历史记录添加失败")
  } catch (error) {
    ElMessage.error("历史记录添加失败")
  }
}

document.title = "Good Info";

const value = 4.8;

// 数据定义
const topStyle = ref({transform: ""});
const r_img = ref({});
const topShow = ref(false);
const rShow = ref(false);

// 鼠标进入原图空间函数
const enterHandler = () => {
  topShow.value = true;
  rShow.value = true;
};

// 鼠标移动函数
const moveHandler = (event) => {
  let x = event.offsetX;
  let y = event.offsetY;
  let topX = x - 100 < 0 ? 0 : x - 100;
  let topY = y - 100 < 0 ? 0 : y - 100;

  if (topX > 300) {
    topX = 300;
  }
  if (topY > 300) {
    topY = 300;
  }

  topStyle.value.transform = `translate(${topX}px,${topY}px)`;
  r_img.value.transform = `translate(-${2 * topX}px,-${2 * topY}px)`;
};

// 鼠标移出函数
const outHandler = () => {
  topShow.value = false;
  rShow.value = false;
};

function handleChat() {
  router.push('/talkToStore?id=' + shopForm.value.id);
}

function handleShop() {
  router.push('/shop?shopId=' + shopForm.value.id);
}

const showAddressModal = ref(false);
const activeSpec = ref();

function changPrice(spec) {
  activeSpec.value = spec;
  showPrice.value = spec.value;
  selectSpecId.value = spec.id;
  num.value = 1;
}

const boxRef = ref(null);

const addressListTop = ref(0);
const addressListLeft = ref(0);
const toggleAddressList = () => {
  if (showAddressModal.value) {
    addressListTop.value = boxRef.value.offsetTop + 30; // 上方位置
    addressListLeft.value = boxRef.value.offsetLeft - 100; // 左侧位置
  }
};

function changeAddress(item) {
  showAddress.value = item.address;
  selectAddressId.value = item.id;
}

const num = ref(1);
const isDark = false;

async function checkOut() {
  const data = {
    specId: selectSpecId.value,
    goodId: goodID.value,
    goodName: goodForm.value.name,
    specName: goodForm.value.spec.find(item => item.id === selectSpecId.value).name,
    specPrice: goodForm.value.spec.find(item => item.id === selectSpecId.value).value,
    specImage: goodForm.value.images[0].image,
    specNumber: num.value,
  }
  try {
    const response = await axios.post('/api/pay/storePayGoods', {
      buy_now: data,
      user_id: UserInfoForm.value.id,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === "fail")
      ElMessage.error("下单失败")
    else
      await router.push('/payGoods/ready?token=' + response.data)
  } catch (error) {
    ElMessage.error("下单失败")
  }
}

const rightWidget = ref(null);

async function addToCart() {
  const data = {
    userId: UserInfoForm.value.id,
    goodId: goodID.value,
    specId: selectSpecId.value,
    number: num.value,
  }
  try {
    const response = await axios.post('/api/good/addGoodCart', data, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success("加入购物车成功")
      await rightWidget.value.getCartCount();
    } else if (response.data.code === 400) {
      ElMessage.error("加入购物车失败")
    }
  } catch (error) {
    ElMessage.error("加入购物车失败")
  }
}

async function changeFavorite(status) {
  if (status) {
    try {
      const response = await axios.post('/api/good/addFavourite', {
        userId: UserInfoForm.value.id,
        goodId: goodID.value
      }, {
        headers: {
          "Content-Type": "application/json"
        },
        withCredentials: true
      })
      if (response.data.code === 200) {
        isFavorite.value = true;
      } else if (response.data.code === 400) {
        ElMessage.error("收藏失败")
      }
    } catch (error) {
      ElMessage.error("收藏失败")
    }
  } else {
    try {
      const response = await axios.post('/api/good/deleteFavourite', {
        userId: UserInfoForm.value.id,
        goodId: goodID.value
      }, {
        headers: {
          "Content-Type": "application/json"
        },
        withCredentials: true
      })
      if (response.data.code === 200) {
        isFavorite.value = false;
      } else if (response.data.code === 400) {
        ElMessage.error("取消收藏失败")
      }
    } catch (error) {
      ElMessage.error("取消收藏失败")
    }
  }
}

const locale = {
  "Basic Usage": "用户评价",
  "Horizontal Mode": "参数信息",
  "Scroll Container": "图文详细",
  "Looks good": "看了又看",
}
const activeName = ref("first");
const CommentList = ref([]);
const filterComment = ref([]);

async function initComment() {
  try {
    const response = await axios.post('/api/pay/comment/getCommentByGoodId', {
      goodId: goodID.value,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      CommentList.value = response.data;
      filterComment.value = CommentList.value;
    }
  } catch (error) {
    ElMessage.error('获取评论失败')
  }
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份需加1，并补零
  const day = String(date.getDate()).padStart(2, '0'); // 补零

  return `${year}-${month}-${day}`;
}

function handleTabClick() {
  if (activeName.value === "first") {
    filterComment.value = CommentList.value.filter(item => item.images !== null)
  } else {
    filterComment.value = CommentList.value
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

const isLive = ref(false);

async function checkLive() {
  try {
    const response = await axios.post('/api/live/isLiveVisible', {
      roomId: shopForm.value.id,
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
  goodID.value = new URLSearchParams(window.location.search).get("id");
  window.addEventListener('scroll', handleScroll);
  initUserSession();
  await init();
  await getCount();
  await initComment();
  await initRecommendedProducts();
  await checkLive();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
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

.good-container {
  margin: 0 20px 20px;
  width: 100%;
  height: auto;
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: relative;
  text-align: left;
}

.good-show {
  height: 65vh;
}

/* 放大的图片，通过定位将左上角定位到(0,0) */
.rightImg {
  display: inline-block;
  width: 800px;
  height: 800px;
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 10px;
}

/* 右边的区域图片放大空间 */
.right {
  margin-left: 500px;
  width: 400px;
  height: 400px;
  position: absolute;
  overflow: hidden;
  z-index: 500;
  border-radius: 0 10px 10px 0;
}

/* 一个最高层层罩 */
.maskTop {
  width: 500px;
  height: 500px;
  position: absolute;
  z-index: 1;
  top: 0;
  left: 0;
}

/* 层罩，通过定位将左上角定位到(0,0) */
.top {
  width: 200px;
  height: 200px;
  background-color: lightcoral;
  opacity: 0.4;
  position: absolute;
  top: 0;
  left: 0;
}

/* 原图的容器 */
.left {
  width: 500px;
  height: 500px;
  border: none;
  float: left;
  position: relative;
}

.active {
  border: 2px solid #e02020;
}

.address-list {
  position: absolute; /* 使地址列表绝对定位 */
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  z-index: 1000; /* 确保显示在其他元素之上 */
  background-color: white; /* 背景色，以便于区分 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  padding: 10px;
  border-radius: 8px; /* 圆角设计 */
  max-height: 40vh;
}

.address-item {
  border: 1px solid #cccccc;
  border-radius: 8px;
  padding: 10px;
  background-color: #f5f5f5;
}

.address-item:hover {
  background-color: #e0e0e0;
  border-color: #e02020;
}

.header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.default {
  color: #ff8800;
  border: 1px solid #ff8800;
  border-radius: 4px;
  padding: 2px 4px;
}

.good-item {
  margin: 10px;
  border: 2px solid #e0e0e0;
  border-radius: 5px;
}

.good-item:hover {
  border-color: #e02020;
}

.good-buy {
  background-color: #e02020;
  color: white;
  border: none;
  font-size: 20px;
  border-radius: 10px 0 0 10px;
  width: 50%;
}

.good-star {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #121212;
  margin-left: 30px;
  text-decoration: none;
}

.good-star:hover {
  color: #e02020;
}

.good-star i {
  font-size: 24px; /* 调整图标大小 */
}

.good-star span {
  font-size: 14px; /* 调整文本大小 */
}

.el-anchor-link {
  color: #e02020;
  font-size: 18px;
}

.link-item {
  font-size: 18px;
  color: #4c4c4c;
  text-decoration: none;
  font-weight: bold;
}

.link-item:hover {
  color: #e02020;
}

.good-comment {
  width: 100%;
  margin: 20px 0;
  padding: 10px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.good-comment-item {
  display: flex;
  margin-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}

.good-comment-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
}

.good-comment-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.good-comment-content {
  flex: 1;
}

.good-comment-title {
  font-size: 16px;
  font-weight: bold;
}

.good-comment-desc {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.good-comment-spec {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.good-comment-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.good-comment-images {
  display: flex;
  flex-wrap: wrap; /* 如果图片过多，可以换行 */
  gap: 10px; /* 根据需要调整图片之间的间距 */
}

.el-tabs {
  margin-bottom: 20px;
}

.el-tabs__header {
  border-bottom: 2px solid #e0e0e0;
}

.el-tabs__item {
  font-size: 16px;
  color: #e02020;
}

.el-tabs__item {
  font-size: 20px !important;
  font-weight: bold;
}

.el-tabs__item.is-active {
  color: #e02020;
  font-weight: bold;
}

.el-image {
  margin-top: 10px;
  width: 100%;
  max-width: 300px;
}

.el-tooltip__trigger:focus-visible {
  outline: unset;
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

.trace-tabs {
  margin-top: 10px;
}

.timeline-entry {
  padding: 12px 0;
}

.timeline-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.timeline-images {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.timeline-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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