<template>
  <HeaderComponent/>
  <div class="container-fluid mt-5">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-1 sidebar p-0" style="margin-top: 5vh;">
        <div class="d-flex flex-column flex-shrink-0 bg-light" style="height: 79vh">
          <ul class="nav nav-pills nav-flush flex-column mb-auto text-center">
            <el-tooltip
                class="box-item"
                effect="dark"
                content="商家消息"
                placement="right"
            >
              <li class="nav-item">
                <a href="#" class="nav-link active py-3 border-bottom rounded-0" aria-current="page"
                   data-bs-toggle="tooltip" data-bs-placement="right">
                  <i class="bi bi-chat-right-text-fill" style="font-size: 1.5rem;"></i>
                </a>
              </li>
            </el-tooltip>
            <el-tooltip
                class="box-item"
                effect="dark"
                content="用户消息"
                placement="right"
            >
              <li>
                <a href="#" class="nav-link py-3 border-bottom rounded-0" title="Dashboard" data-bs-toggle="tooltip"
                   data-bs-placement="right">
                  <i class="bi bi-people-fill" style="font-size: 1.5rem;"></i>
                </a>
              </li>
            </el-tooltip>
            <el-tooltip
                class="box-item"
                effect="dark"
                content="官方客服"
                placement="right"
            >
              <li>
                <a href="#" class="nav-link py-3 border-bottom rounded-0" title="Orders" data-bs-toggle="tooltip"
                   data-bs-placement="right">
                  <i class="bi bi-table"></i>
                </a>
              </li>
            </el-tooltip>
            <el-tooltip
                class="box-item"
                effect="dark"
                content="官方消息"
                placement="right"
            >
              <li>
                <a href="#" class="nav-link py-3 border-bottom rounded-0" title="Products" data-bs-toggle="tooltip"
                   data-bs-placement="right">
                  <i class="bi bi-grid"></i>
                </a>
              </li>
            </el-tooltip>
            <li>
              <a href="#" class="nav-link py-3 border-bottom rounded-0" title="Customers" data-bs-toggle="tooltip"
                 data-bs-placement="right">
                <i class="bi bi-people"></i>
              </a>
            </li>
          </ul>
        </div>
      </div>
      <!-- Main Content -->
      <div class="col-11 main-content" style="padding: 5px;">
        <div class="chat-container d-flex">
          <!-- Contact List Section -->
          <div class="contact-list bg-white border-end p-2" style="width: 25%; border-radius:20px 0 0 20px;">
            <div class="search-box mb-3">
              <div style="text-align: start" class="mb-1">
                <i class="bi bi-chat-heart"></i>&nbsp;&nbsp;<a style="font-weight: bold">聊天列表</a>
              </div>
              <input type="text" class="form-control" placeholder="搜索最近联系人..." style="border-radius: 20px;">
            </div>
            <ul class="list-group list-group-flush">
              <li
                  v-for="(contact, index) in contacts"
                  :key="index"
                  class="list-group-item d-flex justify-content-between align-items-center"
                  @click="selectContact(index)"
                  :class="{ 'active': selectedContactIndex === index }">
                <div>
                  <img :src="contact.avatar" alt="Avatar" class="rounded-circle me-2"
                       style="width: 30px; height: 30px;">
                  <span>{{ contact.name }}</span>
                  <p class="text-secondary" v-html="contact.lastMessage"></p>
                </div>
                <div>
                  <span v-if="contact.unread > 0" class="badge bg-danger text-white rounded-pill me-2">{{
                      contact.unread
                    }}</span>
                  <span class="text-muted">{{ contact.time }}</span>
                  <button @click.stop="deleteContact(index)" class="btn btn-close ms-2"></button>
                </div>
              </li>
            </ul>
          </div>
          <!-- Chat Section -->
          <el-container>
            <div v-if="selectedContactIndex !== null" class="chat-section flex-grow-1 p-2">
              <el-header>
                <div class="chat-header border-bottom pb-2 mb-3" style="text-align: start">
                  <h5>{{ selectedContact.name }}</h5>
                </div>
              </el-header>
              <div class="chat-messages mb-3" style="height: 50vh; overflow-y: auto;" ref="chatWindowRef">
                <div
                    v-for="(message, messageIndex) in chatData[selectedContact.id]"
                    :key="messageIndex"
                    class="message"
                    :class="{ self: message.isSelf }"
                >
                  <el-avatar
                      v-if="!message.isSelf"
                      :src="selectedContact.avatar"
                      size="default"
                      class="avatar"
                  ></el-avatar>
                  <el-avatar
                      v-if="message.isSelf"
                      :src="UserInfoForm.avatar"
                      size="default"
                      class="avatar"
                  ></el-avatar>
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
                <!-- Additional messages here -->
              </div>
              <el-footer>
                <div class="input-toolbar d-flex align-items-center mb-2">
                  <transition name="fade">
                    <emoji-picker
                        v-if="showEmojiPicker"
                        @emoji-click="addEmoji"
                        style="position: absolute; bottom:300px; left: 600px;"
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
                <div class="input-group">
                  <textarea v-model="newMessage"
                            class="form-control border-0 bg-transparent"
                            placeholder="请输入消息，按Enter键 或 点击发送按钮发送"
                            @keydown.enter="send"
                            style="min-height: 95px; resize: none;"></textarea>
                </div>
                <div style="text-align: right;">
                  <el-button type="primary" @click="send" style="border-radius: 20px;">发送</el-button>
                </div>
              </el-footer>
            </div>
          </el-container>
          <!-- Additional Info Section -->
          <div class="additional-info bg-white border-start p-2" v-if="selectedContactIndex !== null"
               style="width: 20%; margin-top: 5vh; border-radius: 0 20px 20px 0;">

            <div class="info-header mb-3">
              <h6>正在咨询的宝贝</h6>
            </div>
            <div v-for="product in products" :key="product.id" class="product-item d-flex align-items-center mb-2">
              <img :src="product.image" alt="Product" class="me-2" style="width: 40px; height: 40px;">
              <div>
                <p class="mb-1">{{ product.name }}</p>
                <span class="text-muted">{{ product.price }}</span>
              </div>
              <button class="btn btn-outline-primary btn-sm" style="border-radius: 20px;"
                      @click="sendProductLink(product)">发送宝贝链接
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <RightWidgetComponent/>
  <FooterComponent ref="footer" @initUserChat="handleInitChat"></FooterComponent>
</template>

<script setup>
import {computed, nextTick, onMounted, ref} from 'vue';
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import useUser from "@/composables/useUser";
import axios from "axios";
import {ElMessage} from "element-plus";
import 'emoji-picker-element';
import html2canvas from 'html2canvas';
import VueCropper from 'vue-cropperjs';
import 'cropperjs/dist/cropper.css';

const {UserInfoForm, initUserSession} = useUser();
const footer = ref(null);
const contacts = ref([]);

async function initContacts() {
  try {
    const response = await axios.post('/api/chat/getChatSession', {
      userId: UserInfoForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data !== null)
      contacts.value = response.data;
  } catch (err) {
    ElMessage.error('获取聊天列表失败，请稍后再试')
  }
}

async function IsInChatSession() {
  const shopId = new URLSearchParams(window.location.search).get('id');
  if (shopId) {
    const contactExists = ref(false);
    if (Array.isArray(contacts.value) && contacts.value.length > 0) {
      contacts.value.forEach(contact => {
        if (contact.id === parseInt(shopId)) {
          contactExists.value = true;
        }
      });
    }
    if (!contactExists.value) {
      try {
        const response = await axios.post('/api/chat/createChatSession', {
          userId: UserInfoForm.value.id,
          shopId: parseInt(shopId)
        }, {
          headers: {
            'Content-Type': 'application/json'
          },
          withCredentials: true
        });
        if (response.data.code === 200) {
          await initContacts();
        } else {
          ElMessage.error('获取聊天列表失败，请稍后再试')
        }
      } catch (err) {
        ElMessage.error('获取聊天列表失败，请稍后再试')
      }
    }
    contacts.value = contacts.value.map(contact => {
      if (contact.id === parseInt(shopId)) {
        selectedContact.value = contact;
      }
      return contact;
    });
    // 切换到指定联系人，并展示聊天窗口
    selectedContactIndex.value = contacts.value.findIndex(contact => contact.id === parseInt(shopId));
    await selectContact(selectedContactIndex.value);
  }
}

const chatData = ref({});

const selectedContactIndex = ref(null);
const newMessage = ref('');

const selectedContact = computed(() => {
  return selectedContactIndex.value !== null ? contacts.value[selectedContactIndex.value] : null;
});

async function selectContact(index) {
  selectedContactIndex.value = index;
  try {
    const response = await axios.post('/api/chat/getChatDetail', {
      userId: UserInfoForm.value.id,
      shopId: contacts.value[index].id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data !== null) {
      const chatId = Object.keys(response.data)[0];
      chatData.value[chatId] = response.data[chatId].map(msg => ({
        content: msg.content,
        isSelf: msg.isSelf,
        isRead: msg.isRead,
        contentType: msg.contentType
      }));
    }
  } catch (err) {
    ElMessage.error('获取聊天详情失败，请稍后再试')
  }
  await getProducts();
}

async function handleInitChat(chatId) {
  if (chatId) {
    selectedContactIndex.value = contacts.value.findIndex(contact => contact.id === parseInt(chatId));
    await selectContact(selectedContactIndex.value);
    await initContacts();
  }
}

async function deleteContact(index) {
  const contactId = contacts.value[index].id;
  try {
    const response = await axios.post('/api/chat/deleteChatSession', {
      userId: UserInfoForm.value.id,
      shopId: contactId
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      delete chatData.value[contactId];
      if (selectedContactIndex.value === index) {
        selectedContactIndex.value = null;
      }
      await initContacts();
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
  newMessage.value += event.detail.unicode;
};

async function uploadImage(file) {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', UserInfoForm.value.id);
    formData.append('shopId', selectedContact.value.id);
    const response = await axios.post('/api/chat/uploadImage', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await initContacts();
      await selectContact(selectedContactIndex.value);
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

async function send() {
  if (newMessage.value.trim() === '') return;
  try {
    const response = await axios.post('/api/chat/sendMessage', {
      userId: UserInfoForm.value.id,
      shopId: selectedContact.value.id,
      message: newMessage.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await selectContact(selectedContactIndex.value);
      await initContacts();
      newMessage.value = '';
      await nextTick(() => {
        const chatWindow = chatWindowRef.value;
        if (chatWindow) {
          chatWindow.scrollTop = chatWindow.scrollHeight;
        }
      });
    } else if (response.data.code === 400) {
      ElMessage.error('发送消息失败，请稍后再试')
    }
  } catch (err) {
    ElMessage.error('发送消息失败，请稍后再试')
  }
}

const products = ref([]);

async function getProducts() {
  try {
    const response = await axios.post('/api/pay/chat/getChatOrderList', {
      userId: UserInfoForm.value.id,
      shopId: selectedContact.value.id
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data !== null)
      products.value = response.data;
  } catch (err) {
    ElMessage.error('获取商品列表失败，请稍后再试')
  }
}

async function sendProductLink(product) {
  const link = {
    image: product.image,
    price: product.price,
    name: product.name,
    id: product.id
  };
  try {
    const response = await axios.post('/api/chat/sendOrderLink', {
      userId: UserInfoForm.value.id,
      shopId: selectedContact.value.id,
      link: JSON.stringify(link)
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await selectContact(selectedContactIndex.value);
      await initContacts();
      await nextTick(() => {
        const chatWindow = chatWindowRef.value;
        if (chatWindow) {
          chatWindow.scrollTop = chatWindow.scrollHeight;
        }
      });
    } else if (response.data.code === 400) {
      ElMessage.error('发送商品链接失败，请稍后再试')
    }
  } catch (err) {
    ElMessage.error('发送商品链接失败，请稍后再试')
  }
}

onMounted(async () => {
  initUserSession();
  await initContacts();
  await IsInChatSession();
});

</script>

<style scoped lang="less">
.sidebar {
  background-color: #f8f9fa;
}

.nav-link {
  color: #495057;

  &:hover {
    background-color: #96CDCD;
    color: #e9ecef;
  }

  &.active {
    background-color: #e9ecef;
    color: #96CDCD;
  }
}

.main-content {
  font-size: 16px;
  background-color: #f1f1f1;
  margin-top: 5vh;
}

.chat-container {
  height: 100%;
}

.contact-list {
  overflow-y: auto;
}

.chat-section {
  overflow-y: auto;
}

.additional-info {
  overflow-y: auto;
}

.input-group {
  position: sticky;
  bottom: 0;
}

.input-toolbar {
  color: #495057;
}

.btn-primary {
  background-color: #007bff;
  border-color: #007bff;
}

p.text-secondary {
  margin: 0;
  font-size: 0.9em;
}

.badge.bg-danger {
  padding: 4px 8px;
  font-size: 0.75em;
}

.chat-list {
  width: 25%;
}

.chat-section {
  width: 75%;
}

.active {
  background-color: #e9ecef;
  cursor: pointer;
}

.message-item.other {
  background-color: #e9ecef;
  text-align: left;
}

.message-item.self {
  background-color: #dcf8c6;
  text-align: right;
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
