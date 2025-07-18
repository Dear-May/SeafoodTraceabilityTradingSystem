<template>
  <div class="right-widget">
    <el-tooltip content="购物车" placement="left">
      <el-badge :value="cartCount" class="item">
        <el-button type="text" icon="el-icon-shopping-cart" @click="handleCartClick"><i
            class="bi bi-cart-fill " style="font-size: 20px"></i>
        </el-button>
      </el-badge>
    </el-tooltip>
    <el-tooltip content="我的" placement="left">
      <el-button type="text" icon="el-icon-user" @click="handleMyProfileClick"><i class="bi bi-person-circle"
                                                                                  style="font-size: 20px"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip content="客服" placement="left">
      <el-button type="text" icon="el-icon-headset" @click="handleCustomerServiceClick">
        <i class="bi bi-telephone-fill" style="font-size: 20px"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip content="反馈" placement="left">
      <el-button type="text" icon="el-icon-edit" @click="handleFeedbackClick">
        <i class="bi bi-pencil-fill" style="font-size: 20px"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip content="夜晚模式" placement="left" v-if="!isNightMode">
      <el-button type="text" icon="el-icon-moon" @click="toggleNightMode">
        <i class="bi bi-moon-fill" style="font-size: 20px; color: black"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip content="白天模式" placement="left" v-if="isNightMode">
      <el-button type="text" icon="el-icon-sun" @click="toggleNightMode">
        <i class="bi bi-sun-fill" style="font-size: 20px; color: white"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip content="回到顶部" placement="left" v-if="isScrolled">
      <el-button type="text" icon="el-icon-caret-up" @click="handleScrollToTop">
        <i class="bi bi-arrow-up-circle-fill" style="font-size: 20px"></i>
      </el-button>
    </el-tooltip>
  </div>
</template>

<script setup>
import {useThemeStore} from "@/store/themeStore";
import {computed, defineExpose, onBeforeUnmount, onMounted, ref, watch} from "vue";
import router from "@/router";
import axios from "axios";
import {ElMessage} from "element-plus";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const themeStore = useThemeStore();
const isNightMode = computed(() => themeStore.isNightMode);
watch(isNightMode, (newVal) => {
  if (newVal) {
    document.body.classList.add('night-mode');
  } else {
    document.body.classList.remove('night-mode');
  }
});
const isScrolled = ref(false);
const handleScroll = () => {
  isScrolled.value = window.scrollY > 200; // 当滚动超过 200px 时，改变搜索栏位置
};
const toggleNightMode = () => {
  themeStore.toggleNightMode();
};

const handleCartClick = () => {
  router.push('/mySpace/BucketList')
};

const handleMyProfileClick = () => {
  router.push('/mySpace')
};

const handleCustomerServiceClick = () => {
  console.log('客服按钮点击');
  // Implement customer service functionality
};

const handleFeedbackClick = () => {
  console.log('反馈按钮点击');
  // Implement feedback functionality
};

const handleScrollToTop = () => {
  window.scrollTo({top: 0, behavior: 'smooth'});
};

const cartCount = ref(0);

async function getCartCount() {
  const response = await axios.post('/api/good/getCartCount', {
    userId: UserInfoForm.value.id,
  }, {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    withCredentials: true
  })
  if (response.data !== null)
    cartCount.value = response.data;
  else
    ElMessage.error('获取购物车数量失败，请稍后再试');
}

defineExpose({getCartCount});

onMounted(async () => {
  window.addEventListener('scroll', handleScroll);
  initUserSession();
  await getCartCount();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped lang="less">
.right-widget {
  position: fixed;
  right: 0;
  top: 30%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  padding: 10px;
  border-radius: 10px 0 0 10px;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.right-widget .el-button {
  margin-bottom: 15px;
  color: #333;
}

.right-widget .el-button:hover {
  color: #e02020;
}
</style>