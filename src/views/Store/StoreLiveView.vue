<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider"></ShopSliderComponent>
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="p-4" v-loading="loading">
        <div class="live-stream-container">
          <!-- 头部信息 -->
          <div class="header bg-light d-flex align-items-center p-3 shadow-sm row">
            <div class="col-8">
              <el-button
                  ref="button_one"
                  @click="publish"
                  type="primary"
                  size="large"
                  :disabled="isPublishing"
              >
                开始直播
              </el-button>
              <el-button
                  ref="button_two"
                  @click="close"
                  type="danger"
                  size="large"
                  :disabled="!isPublishing"
              >
                停止直播
              </el-button>
            </div>
          </div>

          <!-- 主体内容 -->
          <div class="content-container d-flex">
            <!-- 视频播放区 -->
            <div class="video-section flex-grow-1">
              <video id="video" autoplay playsinline muted class="video-element"
                     style="width: 100%; height: 100%; object-fit: cover;"></video>
            </div>

            <!-- 在线用户和聊天区域 -->
            <div class="side-panel bg-light p-3">
              <!-- 在线用户列表 -->
              <div class="online-users mb-4">
                <h6 class="text-danger">在线人员 ({{ onlineUsers.length }})</h6>
                <ul class="list-unstyled">
                  <li
                      v-for="(user, index) in onlineUsers"
                      :key="index"
                      class="d-flex align-items-center mb-2"
                  >
                    <img
                        class="user-avatar-sm rounded-circle me-2"
                        :src="user.avatar"
                        alt="用户头像"
                    />
                    <span>{{ user.name }}</span>
                  </li>
                </ul>
              </div>

              <!-- 聊天区 -->
              <div class="chat-section">
                <h6 class="text-primary">聊天区</h6>
                <div class="chat-messages border rounded p-2 mb-3" ref="chatWindowRef">
                  <div
                      v-for="(message, index) in chatMessages"
                      :key="index"
                      class="d-flex align-items-start mb-2"
                  >
                    <img
                        class="user-avatar-sm rounded-circle me-2"
                        :src="message.avatar"
                        alt="用户头像"
                        width="35"
                        height="35"
                    />
                    <div>
                      <p class="mb-1"><strong>{{ message.sender }}</strong></p>
                      <p class="text-muted mb-0" v-html="message.content"></p>
                    </div>
                  </div>
                </div>

                <!-- 聊天输入框 -->
                <div style="text-align: start;">
                  <transition name="fade">
                    <emoji-picker
                        v-if="showEmojiPicker"
                        @emoji-click="addEmoji"
                        style="position: absolute; bottom:200px; right: 100px;"
                    ></emoji-picker>
                  </transition>
                  <i class="bi bi-emoji-smile me-3" style="font-size: 1.5rem; cursor: pointer;"
                     @click="toggleEmojiPicker"></i>
                </div>
                <div class="input-group">
                  <input
                      type="text"
                      v-model="chatInput"
                      class="form-control"
                      placeholder="输入50字以内聊天内容~"
                      maxlength="50"
                      @keyup.enter="sendMessage"
                  />
                  <button class="btn btn-danger" @click="sendMessage">发送</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import StoreHeaderView from "@/components/StoreHeaderView.vue";
import {ref, onMounted, nextTick} from "vue";
import "video.js/dist/video-js.css";
import {useUserShop} from "@/composables/useShopUser";
import {ElMessage} from "element-plus";
import router from "@/router";
import axios from "axios";
import 'emoji-picker-element';

const shopSlider = ref(null);
const storeHeader = ref(null);
const {shopForm, initUserSession, initShopInfo, UserForm} = useUserShop();
const loading = ref(false);
const isPublishing = ref(false); // 是否正在推流
const button_one = ref(null);
const button_two = ref(null);

const videoStream = ref(null);
const pc = ref(null);

let socket;
const initWebSocket = () => {
// 模拟商家端的用户 ID
  const shopId = shopForm.value.shopID;
  socket = new WebSocket(`ws://localhost:8888/channel/live/${shopId}`);

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

// 处理通知
const handleNotification = async (message) => {
  if (message.message === "updateUser")
    await initOnlineUsers();
  else if (message.message === "updateMessage")
    await initChatMessages();
};

const onlineUsers = ref([]);

async function initOnlineUsers() {
  try {
    const response = await axios.post('/api/live/getOnlineUserList', {
      roomId: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    onlineUsers.value = response.data;
  } catch (err) {
    ElMessage.error("获取在线用户列表失败，请稍后再试");
  }
}

const chatMessages = ref([]);
const chatWindowRef = ref(null);

async function initChatMessages() {
  try {
    const response = await axios.post('/api/live/getLiveMessageList', {
      roomId: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    chatMessages.value = response.data;
    await nextTick(() => {
      const chatWindow = chatWindowRef.value;
      if (chatWindow) {
        chatWindow.scrollTop = chatWindow.scrollHeight;
      }
    });
  } catch (error) {
    ElMessage.error("获取聊天记录失败，请稍后再试");
  }
}

async function init() {
  await initOnlineUsers();
  await initChatMessages();
}

async function updateLiveStatus(status) {
  try {
    const response = await axios.post('/api/live/updateLiveStatus', {
      roomId: shopForm.value.shopID,
      status: status,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    })
    if (response.data.code === 500)
      ElMessage.error("更新直播状态失败，请稍后再试")
  } catch (error) {
    ElMessage.error("更新直播状态失败，请稍后再试")
  }
}

async function publish() {
  if (isPublishing.value) return;

  const httpURL = "http://localhost:1985/rtc/v1/publish/";
  const webRTCURL = `webRTC://localhost/live/${shopForm.value.shopID}`;

  try {
    // 捕获本地视频流
    videoStream.value = await navigator.mediaDevices.getUserMedia({
      video: {frameRate: {ideal: 30}, width: 1280, height: 720},
      audio: true,
    });

    // 显示本地视频流
    const localVideo = document.querySelector("#video");
    localVideo.srcObject = videoStream.value;
    localVideo.play();

    // 创建 RTCPeerConnection
    pc.value = new RTCPeerConnection();

    // 添加轨道到 RTCPeerConnection
    videoStream.value.getTracks().forEach((track) => {
      pc.value.addTrack(track, videoStream.value);
    });

    // 创建 SDP offer
    const offer = await pc.value.createOffer();
    await pc.value.setLocalDescription(offer);

    const data = {
      api: httpURL,
      streamurl: webRTCURL,
      sdp: offer.sdp,
    };

    // 与 SRS 信令交互
    const response = await httpApi(httpURL, data);

    await pc.value.setRemoteDescription(
        new RTCSessionDescription({type: "answer", sdp: response.sdp})
    );
    isPublishing.value = true;
    ElMessage.success("开始直播");
    await updateLiveStatus("open");
  } catch (error) {
    ElMessage.error("推流失败，请稍后再试");
  }
}

async function close() {
  if (!isPublishing.value) return;

  if (pc.value) {
    pc.value.close();
    pc.value = null;
  }

  videoStream.value.getTracks().forEach((track) => track.stop());
  isPublishing.value = false;
  await updateLiveStatus("close");
  ElMessage.success("停止直播");
}

function httpApi(httpURL, data) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", httpURL, true);
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.send(JSON.stringify(data));
    xhr.onload = () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        reject(JSON.parse(xhr.responseText));
      }
    };
  });
}

const chatInput = ref("");
const showEmojiPicker = ref(false);

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value;
};

const addEmoji = (event) => {
  chatInput.value += event.detail.unicode;
};

async function sendMessage() {
  if (!chatInput.value) return;
  try {
    const response = await axios.post('/api/live/insertLiveMessage', {
      roomId: shopForm.value.shopID,
      sendId: UserForm.value.id,
      message: chatInput.value,
      sendType: "shop"
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code !== 200)
      ElMessage.error("发送失败，请稍后再试")
    else
      chatInput.value = "";
  } catch (err) {
    ElMessage.error("发送失败，请稍后再试")
  }
}

const closeWebSocket = async () => {
  if (socket) {
    await close();
    socket.close();
    socket = null; // 清空socket变量
  }
};

onMounted(async () => {
  loading.value = true;
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex("6-1");
  storeHeader.value.setTitle("直播间");
  loading.value = false;
  initWebSocket();
  await init();
});

window.addEventListener('beforeunload', closeWebSocket);

router.beforeEach((to, from, next) => {
  closeWebSocket(); // 在路由跳转时关闭WebSocket连接
  next(); // 继续导航
});
</script>

<style scoped>
.live-stream-container {
  display: flex;
  flex-direction: column;
  height: 80vh;
}

/* 头部信息样式 */
.header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ddd;
}

.user-avatar {
  width: 50px;
  height: 50px;
}

/* 主体内容布局 */
.content-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 视频区域 */
.video-section {
  flex-grow: 1;
  background: #000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-player {
  width: 100%;
  height: 100%;
}

/* 在线用户和聊天区域 */
.side-panel {
  width: 300px;
  border-left: 1px solid #ddd;
  overflow-y: auto;
}

.online-users img.user-avatar-sm {
  width: 30px;
  height: 30px;
}

.chat-section .chat-messages {
  min-height: 44vh;
  max-height: 44vh;
  overflow-y: auto;
}

.chat-section .input-group {
  display: flex;
}
</style>
