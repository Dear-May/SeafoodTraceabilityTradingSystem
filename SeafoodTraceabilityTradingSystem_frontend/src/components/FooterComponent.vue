<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#">关于我们</a> |
          <a href="#">联系我们</a> |
          <a href="#">帮助中心</a> |
          <a href="#">隐私政策</a>
        </div>
        <div class="footer-copyright">
          &copy; 2024 海潮商城 版权所有. 保留所有权利.
        </div>
        <div class="footer-wechat">
          <el-popover
              placement="top-start"
              title="微信公众号"
              :width="200"
              trigger="hover"
          >
            <img src="@/images/qrcode_for_gh_6708f3c423f2_258.jpg" alt="微信公众号" style="width: 100%;">
            <template #reference>
              <i class="bi bi-wechat" style="font-size: 20px"></i>
            </template>
          </el-popover>
        </div>
      </div>
    </div>
  </footer>
  <HeaderComponent ref="header" style="display: none"></HeaderComponent>
</template>

<script setup>
import useUser from "@/composables/useUser";
import {onMounted, defineEmits, ref} from "vue";
import {ElNotification} from "element-plus";
import router from "@/router";
import {useRoute} from "vue-router";
import HeaderComponent from "@/components/HeaderComponent.vue";

const {UserInfoForm, initUserSession} = useUser()
const header = ref(null)
let socket;
const initWebSocket = () => {
// 模拟商家端的用户 ID
  const userId = UserInfoForm.value.id;
  socket = new WebSocket(`ws://localhost:8888/channel/user/${userId}`);

  socket.onopen = () => {
    console.log("WebSocket 连接已建立");
  };

  socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log("WebSocket 收到消息:", message);
    handleNotification(message);
  };

  socket.onclose = () => {
    console.log("WebSocket 连接已关闭");
  };

  socket.onerror = (error) => {
    console.error("WebSocket 连接出错:", error);
  };
};

const route = useRoute();
const emit = defineEmits(['initUserChat'])
const handleNotification = (message) => {
  if (message.type === "chat") {
    const idMatch = message.message.match(/id=(\d+)\s+(.*)/);
    const id = idMatch[1];
    if (route.path === "/talkToStore") {
      emit('initUserChat', id)
    } else {
      ElNotification({
        title: '有新的消息通知',
        message: idMatch[2],
        type: 'info',
        onClick: () => {
          router.push('/talkToStore?id=' + id)
        }
      });
    }
    if (header.value && (router.push !== '/store/login' && router.push !== '/store/register'))
      header.value.getUnreadMessageCount()
  }
}

onMounted(() => {
  initUserSession()
  initWebSocket()
})
</script>

<style scoped lang="less">
.footer {
  padding: 20px 0;
  background-color: #ffffff;
  border-top: 1px solid #eaeaea;
  text-align: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.footer-links {
  margin-bottom: 10px;
}

.footer-links a {
  color: #333;
  margin: 0 10px;
  text-decoration: none;
}

.footer-links a:hover {
  color: #e02020;
}

.footer-wechat {
  position: absolute;
  right: 20px;
  bottom: 20px;
  z-index: 1000;
}

.bi-wechat {
  font-size: 24px;
  color: #333;
}

.footer-wechat:hover .bi-wechat {
  color: #00c800;
}

.footer-copyright {
  color: #888;
}
</style>