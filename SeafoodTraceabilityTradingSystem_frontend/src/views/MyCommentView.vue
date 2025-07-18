<template>
  <div class="my-haichao-page">
    <HeaderComponent/>
    <SidebarComponent ref="sidebarComponent"/>
    <div class="main-content">
      <header class="header">
        <div class="comment-header">
          <span class="store-name">评分</span>
          <span class="product-info">评论</span>
          <span class="price-info">商家</span>
          <span class="quantity-info">商品信息</span>
          <span class="subtotal-info">操作</span>
        </div>
      </header>
      <div class="comment-list">
        <div v-for="(item, index) in commentInfo" :key="index" class="comment-item">
          <div class="comment-score">
            <el-rate
                v-model="item.rate"
                disabled
                show-score
                text-color="#ff9900"
            />
          </div>
          <div class="comment-info">
            <div class="comment-content">{{ item.content }}</div>
            <div class="comment-images" v-if="item.images!== null">
              <div v-for="(image, index) in item.images" :key="index">
                <img :src="image" alt="" width="50px" height="50px"/>
              </div>
            </div>
            <div class="comment-time">{{ formatDate(item.time) }}</div>
          </div>
          <div class="comment-store">
            <div class="store-avatar">
              <img :src="item.storeAvatar" alt="" width="50px" height="50px"/>
            </div>
            <div class="store-name" @click="handleShop(item.shopId)">{{ item.shopName }}</div>
          </div>
          <div class="comment-product">
            <div class="product-name" @click="routerToProduct(item.goodId)">{{ item.goodName }}</div>
            <div class="product-price">￥{{ item.totalPrice }}</div>
          </div>
          <div class="comment-work">
            <el-button type="text" @click="handleDeleteComment(item.commentId)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <FooterComponent/>
  <RightWidgetComponent/>
</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import {onMounted, ref} from "vue";
import router from "@/router";
import axios from "axios";
import {ElMessage} from "element-plus";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const sidebarComponent = ref(null);

function initSidebar() {
  sidebarComponent.value.setActive(10);
}

const commentInfo = ref([]);

async function initCommentInfo() {
  try {
    const response = await axios.post("/api/pay/comment/getComment", {
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data != null) {
      commentInfo.value = response.data;
    } else {
      ElMessage.error('获取评论失败，请稍后再试');
    }
  } catch (error) {
    ElMessage.error('获取评论失败，请稍后再试');
  }
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份需加1，并补零
  const day = String(date.getDate()).padStart(2, '0'); // 补零

  return `${year}-${month}-${day}`;
}

async function handleDeleteComment(commentId) {
  try {
    const response = await axios.post("/api/pay/comment/deleteComment", {
      userId: UserInfoForm.value.id,
      commentId: commentId,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      await initCommentInfo();
    } else {
      ElMessage.error('删除失败，请稍后再试');
    }
  } catch (error) {
    ElMessage.error('删除失败，请稍后再试');
  }
}

function handleShop(id) {
  router.push('/shop?shopId=' + id);
}

function routerToProduct(goodId) {
  router.push('/goodDetail?id=' + goodId);
}

onMounted(async () => {
  initUserSession();
  initSidebar();
  await initCommentInfo();
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

  .comment-header {
    display: grid;
    grid-template-columns: 10% 35% 15% 25% 15%;
    padding: 10px;
    font-weight: bold;
    color: #555;
    border-bottom: 1px solid #ddd;
    text-align: center;
  }

  .comment-item {
    width: 100%;
    display: grid;
    grid-template-columns: 10% 35% 15% 25% 15%;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #e0e0e0;
    text-align: center;

    .comment-score {
      margin-top: 10px;
      text-align: center;
    }

    .comment-info {
      margin-top: 10px;
      font-size: 14px;
      color: #333;

      .comment-content {
        margin-top: 10px;
        font-size: 20px;
        color: #333;
      }

      .comment-images {
        margin-top: 10px;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;

        img {
          margin: 5px;
          border-radius: 5px;
          box-shadow: 0 0 5px #ccc;
        }
      }

      .comment-time {
        margin-top: 10px;
        font-size: 14px;
        color: #999;
      }

    }

    .comment-store {
      margin-top: 10px;
      display: flex;
      align-items: center;

      .store-avatar {
        margin-right: 10px;
      }

      .store-name {
        font-size: 16px;
        color: #333;

        &:hover {
          color: #e02020;
          cursor: pointer;
        }
      }
    }

    .comment-product {
      margin-top: 10px;
      display: block;
      align-items: center;

      .product-name {
        font-size: 16px;
        color: #333;

        &:hover {
          color: #e02020;
        }
      }

      .product-price {
        margin-left: 10px;
        font-size: 16px;
        color: #e02020;
      }
    }

    .comment-work {
      margin-top: 10px;
      display: flex;
      justify-content: center;

      .el-button {
        margin-left: 10px;
        font-size: 18px;
      }
    }
  }
}

</style>