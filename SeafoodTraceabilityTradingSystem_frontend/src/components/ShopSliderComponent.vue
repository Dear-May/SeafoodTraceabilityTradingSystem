<template>
  <aside class="bg-dark text-light" style="width: 200px; padding-top: 20px;">
    <div class="text-center mb-4">
      <a style="font-size: 16px; font-weight: bold" @click="()=> router.push('/store/index')">{{
          shopForm.shopName
        }}</a>
    </div>
    <el-menu
        :default-openeds="openedList"
        :default-active="activeIndex"
        class="border-0"
        text-color="#fff"
        active-text-color="#ffd04b"
        background-color="#333"
    >
      <el-sub-menu index="1">
        <template #title>首页</template>
        <el-menu-item index="1-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=> router.push('/store/index')">首页
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="2" v-if="UserForm.role === 'merchant'">
        <template #title>商品管理</template>
        <el-menu-item index="2-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=> {router.push('/store/mangerGood')}">
          商品列表
        </el-menu-item>
        <el-menu-item index="2-2"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=> {router.push('/store/traceabilityManage')}">
          溯源管理
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="3">
        <template #title>订单管理</template>
        <el-menu-item index="3-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=> {router.push('/store/orderList')}">
          订单列表
        </el-menu-item>
        <el-menu-item index="3-2"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=>{router.push('/store/orderReturns')}">
          退换货处理
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="4" v-if="UserForm.role === 'merchant'">
        <template #title>店员管理</template>
        <el-menu-item index="4-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=> {router.push('/store/staffManage')}">
          店员列表
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="5">
        <template #title>客服</template>
        <el-menu-item index="5-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=>{router.push(('/store/talk'))}">
          在线客服
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="6">
        <template #title>直播</template>
        <el-menu-item index="6-1"
                      :disabled="shopForm.status === '未审核' || shopForm.status === '审核中' || shopForm.status === '审核失败'"
                      @click="()=>{router.push('/store/live')}">
          直播间
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

  </aside>
</template>

<script setup>
import {useUserShop} from "@/composables/useShopUser";
import {defineEmits, defineExpose, onMounted, ref} from "vue";
import router from "@/router";
import {ElNotification} from "element-plus";
import {useRoute} from "vue-router";

const {shopForm, initUserSession, initShopInfo, UserForm} = useUserShop();
const activeIndex = ref("1");
const openedList = ref(['1', '3', '5']);

function setActiveIndex(index) {
  activeIndex.value = index;
}

async function setOpenedList(role) {
  if (role === "merchant") {
    openedList.value.push('2', '4')
    openedList.value.sort((a, b) => {
      const indexA = parseInt(a.split('-')[0]);
      const indexB = parseInt(b.split('-')[0]);
      return indexA - indexB;
    });
  }
}

let socket;
const route = useRoute();
const initWebSocket = () => {
// 模拟商家端的用户 ID
  const shopId = shopForm.value.shopID;
  socket = new WebSocket(`ws://localhost:8888/channel/merchant/${shopId}`);

  socket.onopen = () => {
    console.log("WebSocket 连接已建立");
  };

  socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    handleNotification(message);
  };

  socket.onclose = () => {
    console.log("WebSocket 连接已关闭");
  };

  socket.onerror = (error) => {
    console.error("WebSocket 连接出错:", error);
  };
};
const emit = defineEmits(['initApplications', 'initChat']);
// 处理通知
const handleNotification = (message) => {
  if (message.type === "order") {
    if (route.path === "/store/orderList") {
      emit('initApplications')
    } else {
      // 显示订单通知
      ElNotification({
        title: '新订单通知',
        message: message.message,
        type: 'info',
        onClick: () => {
          router.push('/store/orderList')
        }
      });
    }
  } else if (message.type === "return") {
    // 显示退换货通知
    if (route.path === "/store/orderReturns") {
      emit("initApplications");
    } else {
      ElNotification({
        title: '退换货通知',
        message: message.message,
        type: 'warning',
        onClick: () => {
          router.push('/store/orderReturns')
        }
      });
    }
  } else if (message.type === "new_employee") {
    // 显示新增店员通知
    if (UserForm.value.role === "merchant") {
      if (route.path === "/store/staffManage") {
        emit('initApplications')
      } else {
        ElNotification({
          title: '新增店员通知',
          message: message.message,
          type: 'info',
          onClick: () => {
            router.push('/store/staffManage')
          }
        });
      }
    }
  } else if (message.type === "chat") {
    const idMatch = message.message.match(/id=(\d+)\s+(.*)/);
    const id = idMatch[1];
    if (route.path === "/store/talk") {
      emit('initChat', id)
    } else {
      // 显示私信通知
      ElNotification({
        title: '私信通知',
        message: idMatch[2],
        type: 'info',
        onClick: () => {
          router.push('/store/talk?id=' + id);
        }
      });
    }
  }
};

defineExpose({setActiveIndex})

const closeWebSocket = () => {
  if (socket) {
    socket.close();
    socket = null; // 清空socket变量
  }
};

window.addEventListener('beforeunload', closeWebSocket);

router.beforeEach((to, from, next) => {
  closeWebSocket(); // 在路由跳转时关闭WebSocket连接
  next(); // 继续导航
});

onMounted(async () => {
  initUserSession();
  await setOpenedList(UserForm.value.role);
  await initShopInfo();
  initWebSocket();
});
</script>
<style scoped lang="less">

</style>