<template>
  <div>
    <h1>支付二维码</h1>
    <img v-if="qrCode" :src="qrCode" alt="支付二维码"/>
    <p v-else>二维码生成中，请稍候...</p>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

const hostip = ref();
const token = ref();
const qrCode = ref(""); // 二维码数据
const paymentStatus = ref(""); // 支付状态
let previousStatus = ""; // 记录上一次的支付状态
let socket; // WebSocket 实例

// 获取二维码数据
const fetchQRCode = async () => {
  try {
    const response = await axios.post("/api/pay/generate_qr", {
      orderId: token.value, // 订单号
    });
    qrCode.value = response.data.qrCode;
  } catch (error) {
    console.error("二维码生成失败", error);
    alert("生成二维码失败，请稍后重试");
  }
};

async function getHostIP() {
  try {
    const response = await axios.get("/api/other/getHostIp");
    if (response.data !== null) {
      hostip.value = response.data;
    } else
      hostip.value = "192.168.1.20";
  } catch (error) {
    ElMessage.error("获取服务器 IP 失败，请稍后重试");
  }
}

// 连接 WebSocket
const connectWebSocket = async () => {
  await getHostIP();
  socket = new WebSocket("ws://" + hostip.value + ":8888/channel/payment/" + token.value);

  socket.onmessage = (event) => {
    console.log("WebSocket 收到消息：", event.data);
    const message = JSON.parse(event.data);
    const newStatus = message.status;

    if (newStatus !== previousStatus) {
      paymentStatus.value = newStatus;
      previousStatus = newStatus;
      handleMySpace(newStatus);
    }
    console.log("WebSocket 收到消息：", message);
  };
  socket.onopen = () => {
    console.log("WebSocket 连接已建立");
  };

  socket.onclose = () => {
    console.log("WebSocket 连接已关闭");
  };
};

async function handleMySpace(newStatus) {
  if (newStatus !== "已扫码") {
    if (newStatus === "已支付") {
      try {
        const response = await axios.post("/api/pay/changOrderStatus", {
          token: token.value,
          status: newStatus,
        })
        if (response.data.code === 200)
          ElMessage.success("支付成功！");
        else
          ElMessage.error("订单状态更新失败，请稍后重试");
      } catch (error) {
        ElMessage.error("订单状态更新失败，请稍后重试");
      }
    } else if (newStatus === "未支付") {
      ElMessage.error("未支付订单");
    }
    setTimeout(() => {
      router.replace("/mySpace")
    }, 1500)
  }
}

// 销毁 WebSocket 连接
const disconnectWebSocket = () => {
  if (socket) {
    socket.close();
  }
};

// 生命周期管理
onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search);
  token.value = urlParams.get("token");
  await fetchQRCode();
  await connectWebSocket();
});

onUnmounted(() => {
  disconnectWebSocket();
});
</script>

<style lang="less" scoped>
.container {
  text-align: center;
  margin: 20px;
}

.title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.qr-image {
  width: 200px;
  height: 200px;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.loading-text {
  font-size: 18px;
  color: #666;
}
</style>