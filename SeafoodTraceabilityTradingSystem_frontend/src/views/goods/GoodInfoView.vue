<template>
  <HeaderComponent/>
  <el-container>
    <div class="shop-container row">
      <div class="col-7 row">
        <div class="col-3 row">
          <div class="col-3">
            <div class="live-indicator" v-if="isLive" @click="()=>{router.push('/live/show?id='+shopForm.id)}">
              <img :src="shopForm.avatar" class="shop-avatar" alt="shop-avatar">
              <div class="live-label">鐩存挱涓?/div>
            </div>
            <img v-else :src="shopForm.avatar" class="shop-avatar" alt="shop-avatar">
          </div>
          <div class="col-9">
            <p class="shop-name" class="fs-16">{{ shopForm.name }}</p>
            <p class="shop-desc" class="fs-14">{{ shopForm.description }}</p>
          </div>
        </div>
        <div class="col-7" class="mt-15">
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
            瀹㈡湇婊℃剰搴?6%
          </el-tag>
          &nbsp;
          <el-tag type="info">
            骞冲潎13灏忔椂鍙戣揣
          </el-tag>
          &nbsp;
          <el-tag type="info">
            鐗╂祦浣撻獙浼樼
          </el-tag>
        </div>
      </div>
      <div class="col-3 row" class="text-start">
      </div>
      <div class="col-2" class="mt-15 text-end">
        <el-button @click="handleChat">
          <a class="bi bi-chat"></a>&nbsp;
          鑱旂郴瀹㈡湇
        </el-button>
        <el-button @click="handleShop">
          <a class="bi bi-house"></a>
          杩涘叆搴楅摵
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
            <img :src="goods.image" alt="good-image" class="w-100-h-100">
          </div>
        </div>
        <div class="col-4">
          <div>
            <div class="left">
              <img :src="good_big_show" alt="good-image"
                   style="width: 100%; height: 100%; border-radius: 10px; display: inline-block;">
              <!-- 榧犳爣灞傜僵 -->
              <div v-show="topShow" class="top" :style="topStyle"></div>
              <!-- 鏈€椤跺眰瑕嗙洊浜嗘暣涓師鍥剧┖闂寸殑閫忔槑灞傜僵 -->
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
              <h1 class="good-title" class="fw-bold">{{ goodForm.name }}</h1>
              <h3 class="good-title" class="price-text">锟{ showPrice }}
                <span class="good-price" class="section-desc-sm">宸插敭鍑? 10浠?/span>
                <span>
                    <el-button text @click="openDrawer"
                    >婧簮淇℃伅鏌ョ湅</el-button
                    >
                </span>
              </h3>
              <div class="pos-relative">
                <p class="good-price"
                   class="section-desc">閰?nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;閫?&nbsp;&nbsp;&nbsp;&nbsp;
                  <span
                      class="c-000">{{
                      goodForm.location
                    }}&nbsp;&nbsp;&nbsp;&nbsp;鑷?nbsp;&nbsp;&nbsp;&nbsp;</span>
                  <span class="c-000 fs-18" ref="boxRef">
                  {{ showAddress }}
                  <el-icon class="el-icon--right" @click="toggleAddressList">
                    <arrow-down v-if="!showAddressModal" @click="showAddressModal = true"></arrow-down>
                    <arrow-up v-else @click="showAddressModal = false"></arrow-up>
                  </el-icon>
                </span>
                  <span style="font-size: 18px; color: #999;">
                <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;蹇€? 鍏嶈繍璐?48灏忔椂鍐呭彂璐?              </span>
                </p>

                <div v-if="showAddressModal" class="address-list"
                     :style="{ top: addressListTop + 'px', left: addressListLeft + 'px' }">
                  <h4 class="address-title">閫夋嫨鏀惰揣鍦板潃</h4><br>
                  <div class="address-item" v-for="(item, index) in addresses.value" :key="index"
                       @click="changeAddress(item)">
                    <div class="header">
                      <span>{{ item.name }}</span>
                      <span v-if="item.isDefault" class="default">榛樿</span>
                    </div>
                    <p>{{ item.phone }}</p>
                    <p>{{ item.address }}</p>
                  </div>
                </div>
              </div>
              <p class="good-price"
                 class="section-desc">淇?nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;闅?&nbsp;&nbsp;&nbsp;&nbsp;
                <span class="c-000">閫€璐у疂 7澶╂棤鐞嗙敱閫€璐?鏋侀€熼€€娆?/span>
              </p>
              <div class="good-price"
                   class="section-desc">
                瑙勬牸鍒嗙被锛?nbsp;&nbsp;
                <div v-for="(spec, index) in goodForm.spec" :key="index"
                     class="d-inline-block mr-10">
                  <el-button :class="{ active: activeSpec === spec }" color="#e23030" :dark="isDark" plain
                             @click="changPrice(spec)">{{
                      spec.name
                    }}
                  </el-button>
                </div>
              </div>
              <div class="good-price"
                   class="section-desc">
                鏁?nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;閲?&nbsp;&nbsp;&nbsp;&nbsp;
                <el-input-number v-model="num" :min="1" :max="10"/>
                <span style="font-size: 18px; color: #999; margin-left: 10px;">鏈夎揣</span>
              </div>
            </div>
            <el-footer style="bottom: 35vh;width: 100%; ">
              <el-divider></el-divider>
              <div class="d-flex-align">
                <el-button-group
                    class="w-40"
                >
                  <el-button type="primary" size="large" @click="checkOut" class="good-buy">
                    绔嬪嵆璐拱
                  </el-button>
                  <el-button type="primary" size="large" @click="addToCart" class="good-buy"
                             style="background-color: #e6a23c; border-radius: 0 10px 10px 0;">
                    鍔犲叆璐墿杞?                  </el-button>
                </el-button-group>
                <a class="good-star">
                  <i class="bi bi-star" v-if="!isFavorite" @click="changeFavorite(true)"></i>
                  <i class="bi bi-star-fill" class="c-red" v-else @click="changeFavorite(false)"></i>
                  <span>鏀惰棌</span>
                </a>
              </div>
            </el-footer>
          </el-container>
        </div>
      </div>
      <el-anchor :offset="70" direction="horizontal">
        <el-anchor-link :href="`#鐢ㄦ埛璇勪环`">
          <a class="link-item">{{ locale['Basic Usage'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#鍙傛暟淇℃伅`">
          <a class="link-item">{{ locale['Horizontal Mode'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#鍥炬枃璇︾粏`">
          <a class="link-item">{{ locale['Scroll Container'] }}</a>
        </el-anchor-link>
        <el-anchor-link :href="`#鐪嬩簡鍙堢湅`">
          <a class="link-item">{{ locale['Looks good'] }}</a>
        </el-anchor-link>
      </el-anchor>
      <el-container>
        <div class="good-comment">
          <h1 class="section-title" id="鐢ㄦ埛璇勪环">
            鐢ㄦ埛璇勪环</h1>
          <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleTabClick">
            <el-tab-pane label="鍏ㄩ儴" name="first">
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
                    瑙勬牸: <span>{{ comment.specname }}</span>
                  </p>
                  <p class="good-comment-time">
                    鏃堕棿: <span>{{ formatDate(comment.time) }}</span>
                  </p>
                  <div class="good-comment-images" v-if="comment.images!== null">
                    <div v-for="(image, index) in comment.images" :key="index">
                      <el-image :src="image" fit="contain" width="50px" height="50px"></el-image>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="鏈夊浘" name="second">
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
                    瑙勬牸: <span>{{ comment.specname }}</span>
                  </p>
                  <p class="good-comment-time">
                    鏃堕棿: <span>{{ formatDate(comment.time) }}</span>
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
            <h1 class="section-title" id="鍙傛暟淇℃伅">
              鍙傛暟淇℃伅</h1>
            <el-descriptions
                class="margin-top"
                :column="3"
                border
            >
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    鍟嗗搧鍚嶇О
                  </div>
                </template>
                {{ goodForm.name }}
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <div class="cell-item">
                    涓婃灦鏃堕棿
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
                    浜у湴
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
                    绉嶇被
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
                    鍦板潃
                  </div>
                </template>
                {{ goodForm.location }}
              </el-descriptions-item>
            </el-descriptions>
            <el-divider/>
          </div>
          <div class="good-desc">
            <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="鐪嬩簡鍙堢湅">
              鍥炬枃璇︾粏</h1>
            <div class="good-desc-content">
              <p>
                鍟嗗搧璇︽儏椤电殑鍥炬枃璇︽儏锛屽彲浠ユ槸鍟嗗搧鐨勪粙缁嶃€佽鏍笺€佸寘瑁呫€佸敭鍚庝繚闅滅瓑鍐呭锛岃鐢ㄦ埛鏇寸洿瑙傜殑浜嗚В鍟嗗搧銆?              </p>
              <p>
                鍥炬枃璇︽儏椤电殑甯冨眬鍙互鏍规嵁浜у搧鐨勭被鍨嬨€佸唴瀹广€侀槄璇讳範鎯瓑鍥犵礌杩涜璋冩暣锛屼互鎻愬崌鐢ㄦ埛鐨勯槄璇讳綋楠屻€?              </p>
              <p>
                鍥炬枃璇︽儏椤电殑璁捐瑕佺獊鍑哄晢鍝佺殑涓昏鐗圭偣锛岀獊鍑哄晢鍝佺殑鍥剧墖銆佹枃瀛楋紝骞堕厤鍚堥厤鑹层€佸瓧浣撱€佹帓鐗堢瓑鍏冪礌锛岃鐢ㄦ埛蹇€熺悊瑙ｅ晢鍝佺殑浠峰€笺€?              </p>
            </div>
            <el-divider/>
            <div class="good-desc">
              <h1 style="font-size: 24px; font-weight: bold; margin-bottom: 20px;" id="${locale['looks-good']}">
                鐪嬩簡鍙堢湅</h1>
              <div class="promotion-section">
                <div class="promotion-card" v-for="product in recommendedProducts" :key="product.id">
                  <img :src="product.image" alt="Promotion" class="promotion-image">
                  <div class="promotion-info">
                    <h3>{{ product.name.slice(0, 10) + '...' }}</h3>
                    <span class="promotion-price">锟{ product.price }}</span>
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
        title="婧簮淇℃伅"
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
          <!-- 鏃堕棿绾垮睍绀?-->
          <el-timeline>
            <el-timeline-item
                v-for="(entry, index) in trace.entries"
                :key="index"
                :timestamp="formatDate(entry.timestamp)"
                placement="top"
            >
              <div class="timeline-entry">
                <p class="timeline-content">
                  <strong>鎿嶄綔浜?</strong> {{ entry.operator }}
                  <br/>
                  <MarkdownRender :content="entry.content" />
                </p>
                <!-- 鍥剧墖灞曠ず -->
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
import request from "@/api/request";
import router from "@/router";
import useUser from "@/composables/useUser";
import MarkdownRender from '@/components/MarkdownRender.vue';

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
    const response = await request.post('/api/trace/getGoodTrace', {
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
      ElMessage.error("鑾峰彇婧簮淇℃伅澶辫触")
  } catch (e) {
    ElMessage.error("鑾峰彇婧簮淇℃伅澶辫触")
  }
}

const openDrawer = async () => {
  await loadTraceData();
  activeTab.value = traceData.value[0]?.id; // 榛樿婵€娲荤涓€涓爣绛?  table.value = true;
};

async function getAddressList() {
  try {
    const response = await request.post('/api/addresses/getAllAddresses', {
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
      ElMessage.error('鑾峰彇鍦板潃鍒楄〃澶辫触');
    }
  } catch (err) {
    ElMessage.error('鑾峰彇鍦板潃鍒楄〃澶辫触');
  }
}

async function getFavoriteStatus(goodsId) {
  try {
    const response = await request.post('/api/good/isFavourite', {
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
    ElMessage.error('鑾峰彇鏀惰棌鐘舵€佸け璐?)
  }
}

async function init() {
  try {
    const response = await request.post('/api/good/getGoodAllInfo', {
      goodId: goodID.value
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.shopAvatar !== null) {
      // 鍟嗗淇℃伅
      shopForm.value.id = response.data.shopID;
      shopForm.value.name = response.data.shopName;
      shopForm.value.avatar = response.data.shopAvatar;
      shopForm.value.description = response.data.shopDesc.substring(0, 10) + "...";
      // 涓诲晢鍝佷俊鎭?      goodForm.value.name = response.data.goodName;
      document.title = response.data.goodName;
      goodForm.value.location = response.data.goodLocation;
      goodForm.value.uploadTime = response.data.goodUploadTime;
      // 瑙勫垯淇℃伅
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
    ElMessage.error("鑾峰彇鍟嗗搧淇℃伅澶辫触");
  }
}

async function addFootMark() {
  try {
    const response = await request.post('/api/good/addGoodFootMark', {
      goodId: goodID.value,
      userId: UserInfoForm.value.id
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 400)
      ElMessage.error("鍘嗗彶璁板綍娣诲姞澶辫触")
  } catch (error) {
    ElMessage.error("鍘嗗彶璁板綍娣诲姞澶辫触")
  }
}

document.title = "Good Info";

const value = 4.8;

// 鏁版嵁瀹氫箟
const topStyle = ref({transform: ""});
const r_img = ref({});
const topShow = ref(false);
const rShow = ref(false);

// 榧犳爣杩涘叆鍘熷浘绌洪棿鍑芥暟
const enterHandler = () => {
  topShow.value = true;
  rShow.value = true;
};

// 榧犳爣绉诲姩鍑芥暟
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

// 榧犳爣绉诲嚭鍑芥暟
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
    addressListTop.value = boxRef.value.offsetTop + 30; // 涓婃柟浣嶇疆
    addressListLeft.value = boxRef.value.offsetLeft - 100; // 宸︿晶浣嶇疆
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
    const response = await request.post('/api/pay/storePayGoods', {
      buy_now: data,
      user_id: UserInfoForm.value.id,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === "fail")
      ElMessage.error("涓嬪崟澶辫触")
    else
      await router.push('/payGoods/ready?token=' + response.data)
  } catch (error) {
    ElMessage.error("涓嬪崟澶辫触")
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
    const response = await request.post('/api/good/addGoodCart', data, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success("鍔犲叆璐墿杞︽垚鍔?)
      await rightWidget.value.getCartCount();
    } else if (response.data.code === 400) {
      ElMessage.error("鍔犲叆璐墿杞﹀け璐?)
    }
  } catch (error) {
    ElMessage.error("鍔犲叆璐墿杞﹀け璐?)
  }
}

async function changeFavorite(status) {
  if (status) {
    try {
      const response = await request.post('/api/good/addFavourite', {
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
        ElMessage.error("鏀惰棌澶辫触")
      }
    } catch (error) {
      ElMessage.error("鏀惰棌澶辫触")
    }
  } else {
    try {
      const response = await request.post('/api/good/deleteFavourite', {
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
        ElMessage.error("鍙栨秷鏀惰棌澶辫触")
      }
    } catch (error) {
      ElMessage.error("鍙栨秷鏀惰棌澶辫触")
    }
  }
}

const locale = {
  "Basic Usage": "鐢ㄦ埛璇勪环",
  "Horizontal Mode": "鍙傛暟淇℃伅",
  "Scroll Container": "鍥炬枃璇︾粏",
  "Looks good": "鐪嬩簡鍙堢湅",
}
const activeName = ref("first");
const CommentList = ref([]);
const filterComment = ref([]);

async function initComment() {
  try {
    const response = await request.post('/api/pay/comment/getCommentByGoodId', {
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
    ElMessage.error('鑾峰彇璇勮澶辫触')
  }
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 鏈堜唤闇€鍔?锛屽苟琛ラ浂
  const day = String(date.getDate()).padStart(2, '0'); // 琛ラ浂

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
  // 鍒ゆ柇鏄惁鍒拌揪搴曢儴骞跺姞杞芥洿澶氬晢鍝?  if (loading_recommend.value) return; // 濡傛灉姝ｅ湪鍔犺浇涓紝鍒欎笉瑙﹀彂鏂扮殑璇锋眰
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = window.scrollY;
  const windowHeight = window.innerHeight;

  // 鍒ゆ柇鏄惁鍒拌揪搴曢儴
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
const loading_recommend = ref(false); // 鐢ㄤ簬鎺у埗鍔犺浇鐘舵€?async function getCount() {
  try {
    const response = await request.post('/api/good/getCount', {}, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data !== null) {
      total_recommend.value = response.data;
    }
  } catch (error) {
    ElMessage.error('鑾峰彇鍟嗗搧鎬绘暟澶辫触');
  }
}

async function initRecommendedProducts() {
  if (loading_recommend.value) return; // 闃叉閲嶅鍔犺浇
  loading_recommend.value = true; // 璁剧疆鍔犺浇鐘舵€佷负 true锛岃〃绀烘鍦ㄥ姞杞?
  try {
    const response = await request.post('/api/good/getGoods', {
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
    ElMessage.error('鑾峰彇鎺ㄨ崘鍟嗗搧澶辫触');
  } finally {
    loading_recommend.value = false; // 璇锋眰瀹屾垚鍚庨噸缃姞杞界姸鎬?  }
}

const isLive = ref(false);

async function checkLive() {
  try {
    const response = await request.post('/api/live/isLiveVisible', {
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
    ElMessage.error('鑾峰彇鐩存挱鐘舵€佸け璐?)
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

/* 鏀惧ぇ鐨勫浘鐗囷紝閫氳繃瀹氫綅灏嗗乏涓婅瀹氫綅鍒?0,0) */
.rightImg {
  display: inline-block;
  width: 800px;
  height: 800px;
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 10px;
}

/* 鍙宠竟鐨勫尯鍩熷浘鐗囨斁澶х┖闂?*/
.right {
  margin-left: 500px;
  width: 400px;
  height: 400px;
  position: absolute;
  overflow: hidden;
  z-index: 500;
  border-radius: 0 10px 10px 0;
}

/* 涓€涓渶楂樺眰灞傜僵 */
.maskTop {
  width: 500px;
  height: 500px;
  position: absolute;
  z-index: 1;
  top: 0;
  left: 0;
}

/* 灞傜僵锛岄€氳繃瀹氫綅灏嗗乏涓婅瀹氫綅鍒?0,0) */
.top {
  width: 200px;
  height: 200px;
  background-color: lightcoral;
  opacity: 0.4;
  position: absolute;
  top: 0;
  left: 0;
}

/* 鍘熷浘鐨勫鍣?*/
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
  position: absolute; /* 浣垮湴鍧€鍒楄〃缁濆瀹氫綅 */
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  z-index: 1000; /* 纭繚鏄剧ず鍦ㄥ叾浠栧厓绱犱箣涓?*/
  background-color: white; /* 鑳屾櫙鑹诧紝浠ヤ究浜庡尯鍒?*/
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 娣诲姞闃村奖鏁堟灉 */
  padding: 10px;
  border-radius: 8px; /* 鍦嗚璁捐 */
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
  font-size: 24px; /* 璋冩暣鍥炬爣澶у皬 */
}

.good-star span {
  font-size: 14px; /* 璋冩暣鏂囨湰澶у皬 */
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
  flex-wrap: wrap; /* 濡傛灉鍥剧墖杩囧锛屽彲浠ユ崲琛?*/
  gap: 10px; /* 鏍规嵁闇€瑕佽皟鏁村浘鐗囦箣闂寸殑闂磋窛 */
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
  bottom: -10px; /* 璋冩暣浣嶇疆 */
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
  border: 2px solid rgba(255, 0, 0, 0.5); /* 澶栧湀棰滆壊 */
  animation: ripple 1.5s infinite; /* 鍔ㄧ敾鍛ㄦ湡 */
  z-index: 1; /* 浣嶄簬鍥剧墖涓嬮潰 */
}

</style>
