<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider" @initChat="handleInitChat"></ShopSliderComponent>

    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="chat-container">
        <!-- 左侧：用户列表 -->
        <div class="user-list">
          <div class="search-bar">
            <el-input
                v-model="searchQuery"
                placeholder="搜索联系人"
                prefix-icon="el-icon-search"
                size="small"
                clearable
            />
          </div>
          <div
              class="user-item"
              v-for="(user, index) in filteredUsers"
              :key="user.id"
              :class="{ selected: selectedUser && selectedUser.id === user.id }"
              @click="selectUser(user)"
          >
            <el-avatar :src="user.avatar" size="medium"/>
            <div class="user-info">
              <h6>{{ user.nickname }}</h6>
              <p v-html="user.lastMessage"></p>
            </div>
            <el-button
                icon="el-icon-delete"
                size="small"
                type="danger"
                class="delete-btn"
                @click.stop="deleteUser(index)"
            >
              <el-icon>
                <Delete/>
              </el-icon>
            </el-button>
          </div>
        </div>

        <!-- 右侧：聊天窗口 -->
        <div class="chat-interface">
          <!-- 聊天窗口顶部 -->
          <div class="chat-header">
            <div v-if="selectedUser" class="d-flex align-items-center">
              <el-avatar :src="selectedUser.avatar" size="medium"/>
              <h5>{{ selectedUser.nickname }}</h5>
            </div>
            <div v-else>
              <h5>选择联系人开始聊天</h5>
            </div>
          </div>

          <!-- 聊天记录 -->
          <div class="chat-body" v-if="selectedUser" ref="chatWindowRef">
            <div
                v-for="(message, index) in messages"
                :key="index"
                class="message"
                :class="{ self: message.isSelf }"
            >
              <!-- 左侧：用户头像 -->
              <el-avatar
                  v-if="!message.isSelf"
                  :src="selectedUser.avatar"
                  size="default"
                  class="avatar"
              ></el-avatar>
              <!-- 右侧：商家头像 -->
              <el-avatar
                  v-if="message.isSelf"
                  :src="shopForm.shopAvatar"
                  size="default"
                  class="avatar"
              ></el-avatar>
              <!-- 消息内容 -->
              <div class="message-content">
                <p v-html="message.content" v-if="message.contentType === 'text'"></p>
                <el-image :src="message.content" v-else-if="message.contentType === 'image'" class="img-fluid"
                          style="width: 100px; height: auto;"
                          :preview-src-list="[message.content]"
                          alt="Image"/>
                <div
                    v-else-if="message.contentType === 'link'"
                    class="product-item d-flex align-items-center mb-2"
                    style="background-color: #f5f5f5; padding: 10px; border-radius: 10px;"
                >
                  <img
                      :src="message.content.image"
                      alt="Product"
                      class="me-2"
                      style="width: 40px; height: 40px;"
                  >
                  <div>
                    <p class="mb-1">{{ message.content.name }}</p>
                    <span class="text-muted">{{ message.content.price }} 元</span>
                  </div>
                </div>
              </div>
              <div v-if="message.isSelf" class="message-status">
                <span v-if="message.isRead" class="read-status">已读</span>
                <span v-else class="unread-status">未读</span>
              </div>

            </div>
          </div>

          <div v-else style="text-align: center;">
            <p>请选择联系人开始聊天</p>
          </div>

          <!-- 输入框 -->
          <div class="input-toolbar d-flex align-items-center mb-2 p-2" v-if="selectedUser">
            <transition name="fade">
              <emoji-picker
                  v-if="showEmojiPicker"
                  @emoji-click="addEmoji"
                  style="position: absolute; bottom:215px; left: 500px;"
              ></emoji-picker>
            </transition>
            <i class="bi bi-emoji-smile me-3" style="font-size: 1.5rem; cursor: pointer;"
               @click="toggleEmojiPicker"></i>
            <el-upload
                :show-file-list="false"
                :before-upload="beforeUpload"
                accept="image/*"
            >
              <i class="bi bi-card-image me-3" style="font-size: 1.5rem; cursor: pointer;"></i>
            </el-upload>
            <i class="bi bi-scissors me-3" style="font-size: 1.5rem; cursor: pointer;"
               @click="takeScreenshot"></i>
            <el-dialog
                v-model="dialogVisible"
                width=60vw
                :before-close="handleClose"
            >
              <vue-cropper
                  v-if="imageSrc"
                  ref="cropper"
                  :src="imageSrc"
                  :aspect-ratio="16 / 9"
                  :view-mode="1"
                  :auto-crop-area="0.5"
                  :guides="true"
                  :auto-crop="true"
                  :background="true"
                  :drag-mode="move"
              ></vue-cropper>
              <div v-if="imageSrc">
                <el-button type="primary" @click="cropImage" style="border-radius: 20px;">裁剪并发送</el-button>
              </div>
            </el-dialog>
          </div>
          <div class="chat-input" v-if="selectedUser">
            <el-input
                v-model="inputMessage"
                type="textarea"
                placeholder="请输入消息..."
                clearable
                maxlength="500"
                resize="none"
                :autosize="{ minRows: 6, maxRows: 8 }"
                @keyup.enter="sendMessage"
            />
            <el-button type="primary" @click="sendMessage">发送</el-button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import StoreHeaderView from "@/components/StoreHeaderView.vue";
import {computed, nextTick, onMounted, ref} from "vue";
import {useUserShop} from "@/composables/useShopUser";
import {Delete} from "@element-plus/icons-vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import 'emoji-picker-element';
import VueCropper from "vue-cropperjs";
import html2canvas from "html2canvas";
import 'cropperjs/dist/cropper.css';

const {shopForm, initUserSession, initShopInfo} = useUserShop();
const shopSlider = ref(null);
const storeHeader = ref(null);

const searchQuery = ref("");
const users = ref([]);

async function initUserList() {
  try {
    const response = await axios.post('/api/shop/chat/getTalkList', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true,
    })
    if (response.data != null && Array.isArray(response.data))
      users.value = response.data;
  } catch (err) {
    ElMessage.error("获取联系人列表失败")
  }
}

const messages = ref([]);
const chatData = ref({});
const selectedUser = ref(null);

async function IsInChatSession() {
  const userId = new URLSearchParams(window.location.search).get('id');
  if (userId) {
    const user = users.value.find(user => user.id === parseInt(userId, 10));
    if (user) {
      await selectUser(user);
    }
  } else {
    selectedUser.value = null;
    messages.value = [];
  }
}

const inputMessage = ref("");

const filteredUsers = computed(() =>
    users.value.filter((user) => user.nickname.includes(searchQuery.value))
);

async function selectUser(user) {
  try {
    const response = await axios.post('/api/shop/chat/getTalkDetail', {
      shopID: shopForm.value.shopID,
      userId: user.id,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true,
    })
    if (response.data != null) {
      const chatId = Object.keys(response.data)[0];
      chatData.value[chatId] = response.data[chatId].map(msg => ({
        content: msg.content,
        isSelf: msg.isSelf,
        isRead: msg.isRead,
        contentType: msg.contentType
      }));
      selectedUser.value = user;
      messages.value = chatData.value[user.id] || [];
    }
  } catch (error) {
    ElMessage.error("获取聊天记录失败")
  }
}

async function handleInitChat(id) {
  const user = users.value.find(user => user.id === parseInt(id, 10));
  if (user) {
    if (selectedUser.value && selectedUser.value.id === user.id) {
      await selectUser(user);
    }
  }
  await initUserList();
}

async function deleteUser(index) {
  const contactId = users.value[index].id;
  try {
    const response = await axios.post('/api/shop/chat/deleteChatSession', {
      shopID: shopForm.value.shopID,
      userId: contactId,
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      delete chatData.value[contactId];
      if (selectedUser.value && selectedUser.value.id === index) {
        selectedUser.value = null;
        messages.value = [];
      }
      await initUserList();
    } else if (response.data.code === 400)
      ElMessage.error('删除联系人失败，请稍后再试')
  } catch (error) {
    ElMessage.error('删除联系人失败，请稍后再试')
  }
}

const chatWindowRef = ref(null);
const showEmojiPicker = ref(false);

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value;
};

const addEmoji = (event) => {
  inputMessage.value += event.detail.unicode;
};

async function uploadImage(file) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', selectedUser.value.id);
    formData.append('shopId', shopForm.value.shopID);
    const response = await axios.post('/api/shop/chat/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await initUserList();
      await selectUser(selectedUser.value);
      await nextTick(() => {
        const chatWindow = chatWindowRef.value;
        if (chatWindow) {
          chatWindow.scrollTop = chatWindow.scrollHeight;
        }
      });
    } else
      ElMessage.error('上传图片失败，请稍后再试')
  } catch (err) {
    ElMessage.error('上传图片失败，请稍后再试')
  }
}

const beforeUpload = async (file) => {
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('请选择图片文件！');
    return;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;  // 限制文件大小为 2MB
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！');
    return;
  }
  await uploadImage(file);
};

async function sendMessage() {
  if (inputMessage.value.trim() === "") {
    ElMessage.error("消息不能为空")
    return;
  }
  try {
    const response = await axios.post('/api/shop/chat/sendMessage', {
      shopID: shopForm.value.shopID,
      userId: selectedUser.value.id,
      message: inputMessage.value,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      await selectUser(selectedUser.value);
      await initUserList();
      inputMessage.value = "";
      await nextTick(() => {
        const chatWindow = chatWindowRef.value;
        if (chatWindow) {
          chatWindow.scrollTop = chatWindow.scrollHeight;
        }
      });
    } else if (response.data.code === 400) {
      ElMessage.error("发送失败，请稍后再试")
    }
  } catch (error) {
    ElMessage.error("发送失败，请稍后再试")
  }
}

const cropper = ref(null);
const imageSrc = ref(null);
const dialogVisible = ref(false);

const takeScreenshot = () => {
  dialogVisible.value = true;
  const element = document.body;
  html2canvas(element).then(canvas => {
    imageSrc.value = canvas.toDataURL('image/png');
  });
};

async function cropImage() {
  const canvas = cropper.value.getCroppedCanvas();
  const dataUrl = canvas.toDataURL('image/png');
  const file = dataUrlToFile(dataUrl);
  await uploadImage(file)
  handleClose();
}

const dataUrlToFile = (dataUrl) => {
  const arr = dataUrl.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new File([u8arr], 'cropped-image.png', {type: mime});
};

const handleClose = () => {
  dialogVisible.value = false;
  imageSrc.value = '';
};

onMounted(async () => {
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex('5-1')
  storeHeader.value.setTitle('客服');
  await initUserList();
  await IsInChatSession();
});
</script>

<style scoped lang="less">
.chat-container {
  display: flex;
  height: 100vh;
  background: #f9f9f9;
  font-family: "Arial", sans-serif;
  max-height: 94vh;
}

/* 左侧用户列表 */
.user-list {
  width: 300px;
  background: #ffffff;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
}

.search-bar {
  padding: 10px;
  border-bottom: 1px solid #e6e6e6;
  background: #f8f8f8;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #e6e6e6;
  position: relative;
}

.user-item:hover {
  background: #f0f8ff;
}

.user-item.selected {
  background: #e6f7ff;
}

.user-info {
  flex-grow: 1;
  margin-left: 10px;
  overflow: hidden;
}

.user-info h6 {
  margin: 0;
  font-size: 14px;
  font-weight: bold;
}

.user-info p {
  margin: 0;
  font-size: 12px;
  color: #777;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.delete-btn {
  display: none;
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
}

.user-item:hover .delete-btn {
  display: block;
}

/* 右侧聊天窗口 */
.chat-interface {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
}

.chat-header h5 {
  margin: 0;
  margin-left: 10px;
  font-size: 16px;
  color: #333;
}

.chat-body {
  flex-grow: 1;
  padding: 15px;
  overflow-y: auto;
  background: #fafafa;
}

.message {
  display: flex;
  align-items: flex-start; /* 消息和头像垂直对齐 */
  margin-bottom: 10px;

  &.self {
    flex-direction: row-reverse; /* 商家消息右侧显示 */
  }

  .avatar {
    margin: 0 10px; /* 调整头像和消息的间距 */
  }

  .message-content {
    padding: 10px;
    border-radius: 8px;
    max-width: 60%;
    background: #e9ecef; /* 默认背景色 */
  }

  &.self .message-content {
    background: #d4edda; /* 商家消息背景色 */
    color: #155724; /* 商家消息文本颜色 */
  }
}

.chat-input {
  display: flex;
  align-items: center;
  padding: 10px;
  border-top: 1px solid #e6e6e6;
  background: #fff;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.message-status {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  padding: 5px;
}

.read-status {
  color: #67C23A; /* 已读状态颜色 */
}

.unread-status {
  color: #F56C6C; /* 未读状态颜色 */
}
</style>