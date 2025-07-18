<template>
  <div class="my-haichao-page">
    <HeaderComponent/>
    <div class="live-stream-container" v-loading="loading">
      <!-- 头部信息 -->
      <div class="header bg-light d-flex align-items-center p-3 shadow-sm row">
        <div class="col-7 row">
          <div class="col-3 row">
            <div class="col-3">
              <img :src="shopForm.avatar" class="shop-avatar" alt="shop-avatar" width="50" height="50"/>
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
        <div class="col-1 row" style="text-align: start;">
        </div>
        <div class="col-4" style="margin-top: 15px; text-align: end;">
          <el-button @click="()=>{router.push('/talkToStore?id=' + LiveId)}">
            <a class="bi bi-chat"></a>&nbsp;
            联系客服
          </el-button>
          <el-button @click="()=>{router.push('/shop?shopId=' + LiveId)}">
            <a class="bi bi-house"></a>
            进入店铺
          </el-button>
          <el-button v-if="!followed" @click="followShop()">
            <a class="bi bi-share-fill"></a>
            关注店铺
          </el-button>
          <el-button v-else @click="unFollowShop()">
            <a class="bi bi-share-fill"></a>
            取消关注
          </el-button>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="content-container d-flex">
        <!-- 视频播放区 -->
        <div class="video-section flex-grow-1">
          <video id="video2" ref="video2" autoplay
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
                  <p class="mb-1">
                    <strong>{{ message.sender }}</strong>
                    <el-tag type="danger" v-if="message.type === 'shop' && message.id === LiveId.value">
                      商家
                    </el-tag>
                  </p>
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
  <FooterComponent/>
  <RightWidgetComponent></RightWidgetComponent>
</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import useUser from "@/composables/useUser";
import {nextTick, onMounted, ref} from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import axios from "axios";
import 'emoji-picker-element';

const {UserInfoForm, initUserSession} = useUser()

const LiveId = ref();
const loading = ref(false);

function getLiveId() {
  const urlParams = new URLSearchParams(window.location.search);
  const liveId = urlParams.get("id");
  if (liveId) {
    LiveId.value = liveId;
  } else {
    ElMessage.error("直播间不存在")
    router.push({name: "index"});
  }
}

async function play() {
  const httpURL = "http://localhost:1985/rtc/v1/play/";
  const webRTCURL = `webRTC://localhost/live/${LiveId.value}`;
  console.log(httpURL, webRTCURL);
  const localPc = new RTCPeerConnection();
  const stream = new MediaStream();
  const videoElement2 = document.querySelector("#video2");

  localPc.ontrack = (event) => {
    stream.addTrack(event.track);
    console.log(event.track);
    videoElement2.srcObject = stream;
  };

  localPc.addTransceiver("audio", {direction: "recvonly"});
  localPc.addTransceiver("video", {direction: "recvonly"});

  const offer = await localPc.createOffer();
  await localPc.setLocalDescription(offer);
  const data = {
    "api": httpURL,
    "streamurl": webRTCURL,
    "sdp": offer.sdp
  };

  httpApi(httpURL, data).then(async (data) => {
    console.log("answer", data);
    await localPc.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: data.sdp}));
  }).catch((data) => {
    if (data.code === 400) {
      console.log("SDP交换失败");
    }
  });
}

function httpApi(httpURL, data) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', httpURL, true);
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(JSON.stringify(data));
    xhr.onload = () => {
      if (xhr.readyState !== xhr.DONE) reject(xhr);
      if (xhr.status !== 200 && xhr.status !== 201) reject(xhr);
      const responseData = JSON.parse(xhr.responseText);
      if (responseData.code === 0) {
        resolve(responseData);
      } else {
        reject(responseData);
      }
    };
  });
}

let socket;
const initWebSocket = () => {
  const shopId = LiveId.value;
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
  else if (message.message === "liveStart")
    ElMessage.success("主播回来了，快来围观！");
  else if (message.message === "liveClose")
    ElMessage.error("主播下线了，感谢观看！");
};

const onlineUsers = ref([]);
const shopForm = ref({});
const value = ref(4.8);

async function initLiveInfo() {
  try {
    const response = await axios.post("/api/live/getLiveInfo", {
      roomId: LiveId.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data !== null) {
      shopForm.value = response.data;
    } else
      ElMessage.error("直播间不存在")
  } catch (err) {
    ElMessage.error("获取直播间信息失败，请稍后再试")
  }
}

async function initOnlineUsers() {
  try {
    const response = await axios.post('/api/live/getOnlineUserList', {
      roomId: LiveId.value,
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
      roomId: LiveId.value,
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

async function insertOnlineUser() {
  try {
    const response = await axios.post('/api/live/insertOnlineUser', {
      roomId: LiveId.value,
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code !== 200)
      ElMessage.error("插入在线用户失败，请稍后再试");
  } catch (err) {
    ElMessage.error("插入在线用户失败，请稍后再试");
  }
}

async function deleteOnlineUser() {
  try {
    const response = await axios.post('/api/live/deleteOnlineUser', {
      roomId: LiveId.value,
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code !== 200)
      ElMessage.error("删除在线用户失败，请稍后再试");
  } catch (err) {
    ElMessage.error("删除在线用户失败，请稍后再试");
  }
}

async function init() {
  await initLiveInfo();
  await initFollowShop();
  await insertOnlineUser();
  await initOnlineUsers();
  await initChatMessages();
}

const followed = ref(false);

async function initFollowShop() {
  try {
    const response = await axios.post('/api/shop/findFollowShop', {
      userId: UserInfoForm.value.id,
      shopId: LiveId.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    followed.value = response.data.code === 200;
  } catch (err) {
    ElMessage.error('获取关注状态失败，请稍后再试');
  }
}

async function followShop() {
  try {
    const response = await axios.post('/api/shop/followShop', {
      userId: UserInfoForm.value.id,
      shopId: LiveId.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code === 200) {
      followed.value = true;
      ElMessage.success('关注成功');
    } else {
      ElMessage.error('关注失败，请稍后再试');
    }
  } catch (err) {
    ElMessage.error('关注失败，请稍后再试');
  }
}

async function unFollowShop() {
  try {
    const response = await axios.post('/api/shop/unfollowShop', {
      userId: UserInfoForm.value.id,
      shopId: LiveId.value,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true,
    });
    if (response.data.code === 200) {
      followed.value = false;
      ElMessage.success('取消关注成功');
    } else {
      ElMessage.error('取消关注失败，请稍后再试');
    }
  } catch (err) {
    ElMessage.error('取消关注失败，请稍后再试');
  }
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
      roomId: LiveId.value,
      sendId: UserInfoForm.value.id,
      message: chatInput.value,
      sendType: "user"
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
    await deleteOnlineUser()
    socket.close();
    socket = null; // 清空socket变量
  }
};

onMounted(async () => {
  loading.value = true
  initUserSession()
  getLiveId()
  if (LiveId.value !== undefined) {
    initWebSocket()
    await init()
    await play()
    loading.value = false
  }
})

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
  margin-top: 80px;
  margin-bottom: 10px;
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
  min-height: 45vh;
  max-height: 45vh;
  overflow-y: auto;
}

.chat-section .input-group {
  display: flex;
  max-height: 50px;
}
</style>