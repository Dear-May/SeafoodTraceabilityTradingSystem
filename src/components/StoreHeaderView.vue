<template>
  <nav class="navbar navbar-light bg-white border-bottom shadow-sm">
    <div class="container-fluid">
      <span class="navbar-brand mb-0 h1">{{ headerTitle }}</span>
      <div>
        <el-dropdown>
          <el-avatar size="medium">
            <img :src="shopForm.shopAvatar" alt="avatar"/>
          </el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Action 1</el-dropdown-item>
              <el-dropdown-item @click="logout" divided>登出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </nav>
</template>

<script setup>
import {useUserShop} from "@/composables/useShopUser";
import {defineExpose, onMounted, ref} from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";

const {shopForm, initUserSession, initShopInfo} = useUserShop();
const headerTitle = ref()

function setTitle(title) {
  headerTitle.value = title;
}

function logout() {
  sessionStorage.removeItem('ShopUserBaseInfoSession');
  document.cookie = "ShopUserBaseInfo=;expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
  ElMessage.success("登出成功")
  setTimeout(() => {
    router.replace("/store/login")
  }, 1500);
}

defineExpose({setTitle})

onMounted(async () => {
  initUserSession();
  await initShopInfo();

});
</script>