<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <!-- 左侧导航栏 -->
    <ShopSliderComponent ref="shopSlider"></ShopSliderComponent>

    <!-- 右侧内容区域 -->
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <!-- 主体内容 -->
      <div class="p-4">
        <div v-if="shopForm.status === '未审核' || shopForm.status === '审核失败'" style="padding: 20px;" class="row">
          <div style="height: 75vh; width: 25vh" class="col-3">
            <el-steps direction="vertical" :active="StepActive" finish-status="success">
              <el-step title="Step 1 上传营业执照"/>
              <el-step title="Step 2 核对信息"/>
              <el-step title="Step 3 提交审核"/>
            </el-steps>
          </div>
          <div class="col-9">
            <div v-if="StepActive === 0">
              <h3>上传营业执照</h3>
              <el-alert type="warning" :closable="false">
                <template #title>
                  <span>请上传有效的营业执照以便进行审核。</span>
                </template>
                <div>资料的准确性：在上传资料之前，请确保您的营业执照信息准确无误，包括公司名称、注册号、注册地址等。</div>
              </el-alert>
              <div class="mt-3">
                <el-upload
                    class="upload-demo"
                    drag
                    action="#"
                    :before-upload="beforeUpload"
                    :show-file-list="false"
                >
                  <el-icon class="el-icon--upload">
                    <plus/>
                  </el-icon>
                  <div class="el-upload__text">
                    将文件拖拽到此处，或 <em>点击上传</em>
                  </div>
                  <div class="el-upload__tip">仅支持 JPG/PNG 格式，文件大小不超过 5MB</div>
                </el-upload>

                <!-- 图片预览 -->
                <div v-if="imageUrl" class="mt-4">
                  <h5>已上传的图片预览：</h5>
                  <el-image-viewer
                      v-if="showViewer"
                      :url-list="[imageUrl]"
                      :show-download="true"
                      :show-rotate="true"
                      :show-zoom="true"
                      :show-fullscreen="true"
                      :show-indicator="true"
                      :z-index="1000"
                      @close="showViewer = false"
                  />
                  <img :src="imageUrl" alt="Uploaded Image"
                       style="max-width: 300px; border: 1px solid #ddd; border-radius: 5px;"
                       @click="showViewer = true"/>
                </div>
              </div>
            </div>

            <div v-else-if="StepActive === 1">
              <h3>核对信息</h3>
              <img :src="imageUrl" alt="Uploaded Image"
                   style="max-width: 300px; border: 1px solid #ddd; border-radius: 5px; margin-bottom: 12px;"/>
              <el-descriptions
                  class="margin-top"
                  title="营业执照信息"
                  :column="3"
                  :size="'default'"
                  border
              >
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      单位名称
                    </div>
                  </template>
                  {{ wordResult.name }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      社会信用代码
                    </div>
                  </template>
                  {{ wordResult.SocialCreditCode }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      地址
                    </div>
                  </template>
                  {{ wordResult.location }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      注册资本
                    </div>
                  </template>
                  {{ wordResult.registeredCapital }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      实收资本
                    </div>
                  </template>
                  {{ wordResult.paidInCapital }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      成立日期
                    </div>
                  </template>
                  {{ wordResult.dateOfEstablishment }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      经营范围
                    </div>
                  </template>
                  {{ wordResult.businessScope }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      法定代表人
                    </div>
                  </template>
                  {{ wordResult.legalRepresentative }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      企业类型
                    </div>
                  </template>
                  {{ wordResult.type }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      登记机关
                    </div>
                  </template>
                  {{ wordResult.registrationAuthority }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      组成形式
                    </div>
                  </template>
                  {{ wordResult.CompositionForm }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      证件编号
                    </div>
                  </template>
                  {{ wordResult.IDNumber }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      有效期
                    </div>
                  </template>
                  {{ wordResult.periodOfValidity }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      有效期起始日期
                    </div>
                  </template>
                  {{ wordResult.StartingDateOfValidityPeriod }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      核准日期
                    </div>
                  </template>
                  {{ wordResult.DateOfApproval }}
                </el-descriptions-item>
                <el-descriptions-item>
                  <template #label>
                    <div class="cell-item">
                      税务登记号
                    </div>
                  </template>
                  {{ wordResult.TaxRegistrationNumber }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div v-else-if="StepActive === 2">
              <h3>提交审核</h3>
              <a>您的材料将会提交给管理员进行审核，确认信息无误后，点击下一步即可。</a>
            </div>
            <el-button style="margin-top: 12px" @click="next" type="primary">下一步</el-button>
          </div>
        </div>
        <div v-else-if="shopForm.status === '审核中'">
          <h3>审核中</h3>
          <a>您的店铺正在等待管理员审核，请耐心等待。</a>
        </div>
        <DashboardView v-else-if="shopForm.status === '正常'"></DashboardView>
      </div>
    </div>
  </div>
</template>

<script setup>
import DashboardView from '../../components/DashboardView.vue'
import {onMounted, ref} from "vue";
import {useUserShop} from "@/composables/useShopUser";
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import {Plus} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import StoreHeaderView from "@/components/StoreHeaderView.vue";

const loading = ref(true)
const shopSlider = ref(null)
const storeHeader = ref(null)
const {shopForm, initUserSession, initShopInfo, UserForm} = useUserShop();
const StepActive = ref(0);
const next = async () => {
  if (StepActive.value === 0) {
    await uploadImage();

  }
  if (StepActive.value === 2) {
    await submitInfo();
  }
  if (StepActive.value++ > 2) {
    ElMessage.error("操作失败，请重新操作！")
  }
}
const uploadFile = ref(null);
const imageUrl = ref("");
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png";
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isJpgOrPng) {
    ElMessage.error("仅支持上传 JPG/PNG 格式的文件！");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("文件大小不能超过 5MB！");
    return false;
  }
  imageUrl.value = URL.createObjectURL(file);
  uploadFile.value = file;
  return true;
};

const showViewer = ref(false);
const wordResult = ref({
  shopID: "",
  id: "", // 识别结果ID
  SocialCreditCode: "", // 社会信用代码
  name: "", // 名称
  location: "", // 注册地址
  registeredCapital: "", // 注册资本
  paidInCapital: "", // 实缴资本
  dateOfEstablishment: "", // 成立日期
  businessScope: "", // 经营范围
  legalRepresentative: "", // 法定代表人
  type: "", // 企业类型
  registrationAuthority: "", // 登记机关
  CompositionForm: "", // 组成形式
  IDNumber: "", // 证件编号
  periodOfValidity: "", // 有效期限
  StartingDateOfValidityPeriod: "", // 有效期开始日期
  DateOfApproval: "", // 核准日期
  TaxRegistrationNumber: "", // 税务登记号
});

async function uploadImage() {
  const formData = new FormData();
  if (imageUrl.value === "") {
    ElMessage.error("请先上传图片！");
    return;
  }
  formData.append("image", uploadFile);
  try {
    const response = await axios.post("/api/shop/ocr", {
      image: uploadFile.value
    }, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
    if (response.data) {
      wordResult.value.id = response.data.log_id; // 设置识别结果的ID
      wordResult.value.shopID = UserForm.value.shopid;
      // 遍历 words_result 并匹配字段
      const resultMap = response.data.words_result;
      for (const key in resultMap) {
        const field = resultMap[key]?.words;
        switch (key) {
          case "单位名称":
            wordResult.value.name = field;
            break;
          case "社会信用代码":
            wordResult.value.SocialCreditCode = field;
            break;
          case "地址":
            wordResult.value.location = field;
            break;
          case "注册资本":
            wordResult.value.registeredCapital = field;
            break;
          case "实收资本":
            wordResult.value.paidInCapital = field;
            break;
          case "成立日期":
            wordResult.value.dateOfEstablishment = field;
            break;
          case "经营范围":
            wordResult.value.businessScope = field;
            break;
          case "法人":
            wordResult.value.legalRepresentative = field;
            break;
          case "类型":
            wordResult.value.type = field;
            break;
          case "登记机关":
            wordResult.value.registrationAuthority = field;
            break;
          case "组成形式":
            wordResult.value.CompositionForm = field;
            break;
          case "证件编号":
            wordResult.value.IDNumber = field;
            break;
          case "有效期":
            wordResult.value.periodOfValidity = field;
            break;
          case "有效期起始日期":
            wordResult.value.StartingDateOfValidityPeriod = field;
            break;
          case "核准日期":
            wordResult.value.DateOfApproval = field;
            break;
          case "税务登记号":
            wordResult.value.TaxRegistrationNumber = field;
            break;
          default:
            break;
        }
      }
    }
  } catch (error) {
    console.error("上传失败：", error);
    ElMessage.error("图片上传失败，请重试！");
  }
}

async function submitLicense() {
  const formData = new FormData();
  if (imageUrl.value === "") {
    ElMessage.error("请先上传图片！");
    return;
  }
  formData.append("image", uploadFile);
  try {
    const response = await axios.post("/api/shop/submitLicense", {
      image: uploadFile.value,
      id: wordResult.value.id,
      shopID: UserForm.value.shopid
    }, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
    if (response.data.code === 200) {
      ElMessage.success("资料上传成功，请等待管理员审核！");
      await initShopInfo();
      window.location.reload();
    } else {
      ElMessage.error("资料上传失败，请重试！");
    }
  } catch (error) {
    ElMessage.error("图片上传失败，请重试！");
  }
}

async function submitInfo() {
  try {
    const response = await axios.post('/api/shop/submitShopInfo', wordResult.value, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      await submitLicense();
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    ElMessage.error("提交失败，请重试！")
  }
}

onMounted(() => {
  initUserSession();
  initShopInfo();
  shopSlider.value.setActiveIndex('1-1')
  storeHeader.value.setTitle('店铺首页')
  setTimeout(() => {
    loading.value = false
  }, 10000)
})
</script>

<style scoped>
body, html {
  margin: 0;
  padding: 0;
  height: 100%;
}

</style>
