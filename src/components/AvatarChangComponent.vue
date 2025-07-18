<template>
  <a class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
    <div class="profile-header card-wrapper" @click="centerDialogVisible = true">
      <img :src="UserInfoForm.avatar" alt="User Avatar" class="profile-avatar" @mouseover="isHovering = true"
           @mouseout="isHovering = false">
      <div v-if="isHovering" class="edit-profile">更换头像</div>
    </div>
    <span class="fs-4" style="margin-left: 10px;">{{ UserInfoForm.nickname }}</span>
  </a>
  <el-dialog v-model="centerDialogVisible" title="更换头像" width="600px" align-center>
    <div class="avatar-upload-container">
      <div class="avatar-preview">
        <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview" class="avatar-preview-image"/>
        <div v-else class="avatar-placeholder">
          <span>头像预览</span>
        </div>
      </div>
      <div class="avatar-actions">
        <div class="preview-sizes">
          <h4>预览</h4>
          <div class="preview-size">
            <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview 100x100" width="100" height="100"
                 style="border-radius: 50%"/>
            <div v-else class="avatar-placeholder-small"></div>
            <span>100 x 100</span>
          </div>
          <div class="preview-size">
            <img v-if="avatarUrl" :src="avatarUrl" alt="avatar preview 50x50" width="50" height="50"
                 style="border-radius: 50%"/>
            <div v-else class="avatar-placeholder-small"></div>
            <span>50 x 50</span>
          </div>
        </div>
        <div class="avatar-edit-buttons">
          <el-button type="primary" @click="triggerFileInput">
            <i class="bi bi-upload"></i>&nbsp;上传
          </el-button>
          <el-button type="danger" @click="resetAvatar">
            <i class="bi bi-x-circle"></i>&nbsp;重置
          </el-button>
          <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*"
                 style="display: none"/>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="() => {resetAvatar(); centerDialogVisible = false;}">取消
        </el-button>
        <el-button type="primary" @click="() => {uploadAvatar(); centerDialogVisible = false;}">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const isHovering = ref(false);
const centerDialogVisible = ref(false);
const avatarUrl = ref('');
const fileInput = ref(null);
const MAX_FILE_SIZE = 3 * 1024 * 1024;
const errorMessage = ref('');

const triggerFileInput = () => {
  fileInput.value.click();
};

const handleFileChange = (event) => {
  const file = event.target.files[0];

  if (file) {
    // 检测文件是否为图片
    if (!file.type.startsWith('image/')) {
      ElMessage({
        type: 'error',
        message: '请选择图片文件（如 jpg、png 格式）',
      });
      // 重置文件选择框
      resetAvatar();
      return;
    }

    // 检测文件大小是否超过 3MB
    if (file.size > MAX_FILE_SIZE) {
      ElMessage({
        type: 'error',
        message: '文件大小不能超过 3MB',
      });
      // 重置文件选择框
      resetAvatar();
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      avatarUrl.value = e.target.result;
    };
    reader.onerror = () => {
      ElMessage({
        type: 'error',
        message: '文件读取失败，请重试',
      });
      resetAvatar();
    };
    reader.readAsDataURL(file);
  }
};

const resetAvatar = () => {
  avatarUrl.value = '';
  fileInput.value.value = null;
};

async function uploadAvatar() {
  const file = fileInput.value.files[0];
  if (!file) {
    errorMessage.value = '请选择一个图片文件';
    return;
  }
  const formData = new FormData();
  formData.append('avatar', file);
  formData.append('username', UserInfoForm.value.name)
  const response = await axios.post('/api/user/upload-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    withCredentials: true
  })
  if (response.data.code === 200) {
    ElMessage.success('头像上传成功');
    //更新session
    sessionStorage.setItem('userBaseInfo', JSON.stringify({
      id: UserInfoForm.value.id,
      username: UserInfoForm.value.name,
      avatar: "https://oss.yy0313.fit/UserAvatar/" + UserInfoForm.value.name + ".jpg",
      nickname: UserInfoForm.value.nickname,
    }));
    setTimeout(() => {
      window.location.reload();
    }, 1000);
  } else {
    ElMessage.error('头像上传失败，请重试');
  }
}

onMounted(() => {
  initUserSession();
});
</script>

<style scoped lang="less">
.profile-card {
  text-align: center;
  margin-bottom: 20px;
}

.card-wrapper {
  position: relative;
}

.profile-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  position: relative;
  z-index: 2;
  cursor: pointer;
}

.edit-profile {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  color: white;
  padding: 5px;
  background-color: rgba(0, 0, 0, 0.6);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex; /* 用于文字垂直居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  pointer-events: none;
  z-index: 3;
  opacity: 0.8;
}

.avatar-upload-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-preview {
  width: 250px;
  height: 250px;
  border-radius: 50%;
  border: 2px dashed #dcdcdc;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background-color: #f5f5f5;
  margin-top: 30px;
  margin-left: 50px;
}

.avatar-placeholder {
  font-size: 18px;
  color: #888;
  text-align: center;
}

.avatar-preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.preview-sizes {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-size {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-placeholder-small {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #dcdcdc;
}

.avatar-edit-buttons {
  display: flex;
  gap: 10px;
}
</style>