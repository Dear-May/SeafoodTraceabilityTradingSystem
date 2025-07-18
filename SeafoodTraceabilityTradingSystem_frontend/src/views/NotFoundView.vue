<template>
  <div class="box">
    <div ref="vantaRef" class="vanta-background"></div>
    <div class="banner">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在。</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref} from 'vue';
import * as THREE from "three";
import WAVES from "vanta/src/vanta.waves";
import {useRouter} from 'vue-router';

const router = useRouter();
const vantaRef = ref(null);
let vantaEffect = null;

const goHome = () => {
  router.push('/');
};

onMounted(() => {
  vantaEffect = WAVES({
    el: vantaRef.value,
    THREE: THREE,
  });
});

onBeforeUnmount(() => {
  if (vantaEffect) {
    vantaEffect.destroy();
  }
});
</script>

<style lang="less" scoped>
.box {
  position: relative;
  width: 100%;
  height: 100vh; /* 设置为全屏高度 */

  .vanta-background {
    position: absolute; /* 使背景绝对定位 */
    top: 0;
    left: 0;
    width: 100%; /* 使背景宽度为100% */
    height: 100%; /* 使背景高度为100% */
  }

  .banner {
    z-index: 999;
    position: absolute;
    top: 30%;
    left: 10%;
    color: #fff;
    text-align: center;
    width: 80%;
  }
}

h1 {
  font-size: 100px;
  font-weight: bold;
  margin-bottom: 20px;
}

p {
  font-size: 24px;
  margin-bottom: 40px;
}

.el-button {
  font-size: 16px;
  padding: 12px 24px;
}
</style>
