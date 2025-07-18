<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider"></ShopSliderComponent>
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="p-4" v-loading="loading">
        <el-tabs v-model="activeTab" class="demo-tabs">
          <!-- 溯源管理 -->
          <el-tab-pane name="trace">
            <template #label>
              <i class="bi bi-bar-chart" style="font-size: 16px; color: #333;"></i>
              <span style="margin-left: 5px; font-weight: bold; font-size: 16px; color: #333;">溯源管理</span>
            </template>
            <div>
              <!-- 折叠面板 -->
              <el-collapse v-model="activePanels" class="trace-collapse">
                <el-collapse-item
                    v-for="item in traceData"
                    :key="item.id"
                    :title="item.title"
                    :name="item.id"
                >
                  <template #title>
                    <div class="collapse-title">
                      <span style="padding-left: 10px; padding-right: 10px;">{{ item.firstTitle }}</span>
                      <el-tag type="info">{{ formatDate(item.date) }}</el-tag>
                    </div>
                  </template>
                  <!-- 时间线 -->
                  <el-timeline>
                    <el-timeline-item
                        v-for="(entry, index) in item.entries"
                        :key="index"
                        :timestamp="formatDate(entry.timestamp)"
                        placement="top"
                    >
                      <el-card>
                        <div class="timeline-entry">
                          <p style="color: #333; font-size: 14px; margin-bottom: 0; text-align: start;">操作员:
                            {{ entry.operator }}</p>
                          <p class="timeline-content">{{ entry.content }}</p>
                          <div class="timeline-images">
                            <el-image
                                v-for="(image, imgIndex) in entry.images"
                                :key="imgIndex"
                                :src="image"
                                class="timeline-image"
                                style="margin-right: 8px;"
                                fit="cover"
                            ></el-image>
                          </div>
                        </div>
                      </el-card>
                    </el-timeline-item>
                  </el-timeline>
                  <!-- 增加溯源信息按钮 -->
                  <div class="add-trace-button">
                    <el-button type="primary" @click="openAddTraceDialog(item.id)">增加溯源信息</el-button>
                  </div>

                </el-collapse-item>
              </el-collapse>
              <!-- 增加溯源信息弹窗 -->
              <el-dialog
                  title="新增溯源信息"
                  v-model="dialogTraceVisible"
                  width="50%"
                  :append-to-body="true"
                  :before-close="closeAddTraceDialog"
              >
                <el-form
                    ref="traceForm"
                    :model="traceForm"
                    label-width="120px"
                    class="trace-form"
                >
                  <el-form-item label="溯源描述">
                    <el-input
                        v-model="info"
                        type="textarea"
                        rows="5"
                        placeholder="请输入溯源详细描述"
                        maxlength="200"
                        show-word-limit
                    ></el-input>
                  </el-form-item>

                  <el-form-item label="上传图片">
                    <el-upload
                        list-type="picture-card"
                        :auto-upload="false"
                        :limit="3"
                        accept="image/*"
                        :on-change="handleFileChange"
                        :on-preview="handlePictureCardPreview"
                        :on-remove="handleRemove"
                        :file-list="traceForm.images"
                    >
                      <el-icon>
                        <Plus/>
                      </el-icon>
                    </el-upload>
                    <br>
                    <div class="el-upload__tip">最多上传 3 张图片，支持 JPG/PNG 格式。</div>
                  </el-form-item>

                  <el-dialog v-model="dialogVisible">
                    <img :src="dialogImageUrl" alt="Preview Image"/>
                  </el-dialog>
                </el-form>
                <template #footer>
                  <el-button @click="closeAddTraceDialog">取消</el-button>
                  <el-button type="primary" @click="addTraceInfo(selectID)">提交</el-button>
                </template>
              </el-dialog>
            </div>
          </el-tab-pane>

          <!-- 新增溯源 -->
          <el-tab-pane name="add-trace">
            <template #label>
              <i class="bi bi-plus-circle" style="font-size: 16px; color: #333;"></i>
              <span style="margin-left: 5px; font-weight: bold; font-size: 16px; color: #333;">新增溯源</span>
            </template>
            <div class="row">
              <!-- 步骤条 -->
              <div class="col-3 step-container">
                <el-steps direction="vertical" :active="activeStep" finish-status="success">
                  <el-step title="选择商品"></el-step>
                  <el-step title="录入溯源信息"></el-step>
                </el-steps>
              </div>

              <!-- 步骤内容 -->
              <div class="col-9">
                <!-- 第一步：选择商品 -->
                <div v-if="activeStep === 0">
                  <h3 class="step-title">选择商品</h3>
                  <el-divider></el-divider>
                  <div class="search-container ">
                    <el-input
                        class="search-input"
                        v-model="searchQuery"
                        placeholder="请输入商品名称或商品ID进行搜索"
                        clearable
                        @keyup.enter="searchProducts"
                    />
                    <el-button type="primary" @click="searchProducts">
                      <el-icon>
                        <Search/>
                      </el-icon>
                      搜索
                    </el-button>
                  </div>
                  <div class="row product-grid">
                    <div
                        class="col-md-4 mb-3"
                        v-for="product in filteredProducts"
                        :key="product.id"
                    >
                      <el-card
                          shadow="hover"
                          :class="{'selected-card': selectedProducts.includes(product.goodID)}"
                          class="product-card"
                          @click="toggleProduct(product.goodID)"
                      >
                        <img
                            :src="product.imageUrl"
                            alt="商品图片"
                            class="product-image"
                        />
                        <p class="product-name">{{ product.name }}</p>
                      </el-card>
                    </div>
                  </div>
                  <div class="step-actions">
                    <el-button type="primary" @click="nextStep">下一步</el-button>
                  </div>
                </div>

                <!-- 第二步：录入溯源信息 -->
                <div v-else-if="activeStep === 1">
                  <h3 class="step-title">录入溯源信息</h3>
                  <el-divider></el-divider>
                  <el-form
                      ref="traceForm"
                      :model="traceForm"
                      label-width="120px"
                      class="trace-form"
                  >
                    <el-form-item label="溯源描述">
                      <el-input
                          v-model="info"
                          type="textarea"
                          rows="5"
                          placeholder="请输入溯源详细描述"
                          maxlength="200"
                          show-word-limit
                      ></el-input>
                    </el-form-item>

                    <el-form-item label="上传图片">
                      <el-upload
                          list-type="picture-card"
                          :auto-upload="false"
                          :limit="3"
                          accept="image/*"
                          :on-change="handleFileChange"
                          :on-preview="handlePictureCardPreview"
                          :on-remove="handleRemove"
                          :file-list="traceForm.images"
                      >
                        <el-icon>
                          <Plus/>
                        </el-icon>
                      </el-upload>
                      <br>
                      <div class="el-upload__tip">最多上传 3 张图片，支持 JPG/PNG 格式。</div>
                    </el-form-item>

                    <el-dialog v-model="dialogVisible">
                      <img :src="dialogImageUrl" alt="Preview Image"/>
                    </el-dialog>
                  </el-form>
                  <div class="step-actions">
                    <el-button @click="prevStep">上一步</el-button>
                    <el-button type="primary" @click="submitTrace">提交</el-button>
                  </div>
                </div>

              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="fixed-selected-products" v-if="activeTab === 'add-trace' && activeStep === 0">
        <h4>已选商品</h4>
        <div v-if="selectedProducts.length === 0">
          <p>尚未选择任何商品。</p>
        </div>
        <el-card
            v-for="id in selectedProducts"
            :key="id"
            class="selected-product-card"
            shadow="hover"
        >
          <div class="d-flex align-items-center">
            <img
                :src="getProductById(id).imageUrl"
                alt="商品图片"
                class="product-thumbnail"
            />
            <div class="ml-2">
              <p class="selected-product-name">{{ getProductById(id).name }}</p>
              <el-button
                  type="text"
                  size="small"
                  @click="removeProduct(id)"
                  class="remove-btn"
              >
                移除
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>

</template>

<script setup>
import {onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import StoreHeaderView from "@/components/StoreHeaderView.vue";
import {useUserShop} from "@/composables/useShopUser";
import {Plus, Search} from "@element-plus/icons-vue";
import axios from "axios";

const {UserForm, shopForm, initUserSession, initShopInfo} = useUserShop();
const shopSlider = ref(null);
const storeHeader = ref(null);
// 当前激活的标签页
const activeTab = ref("trace");
const loading = ref(false);
const traceData = ref([]);
const activePanels = ref([]);

async function initTraceData() {
  try {
    const response = await axios.post('/api/trace/getShopTrace', {
      ShopID: shopForm.value.shopID,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data != null) {
      traceData.value = response.data;
    } else
      ElMessage.error("获取溯源信息失败");
  } catch (err) {
    ElMessage.error("获取溯源信息失败");
  }
}

function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份需加1，并补零
  const day = String(date.getDate()).padStart(2, '0'); // 补零

  return `${year}-${month}-${day}`;
}

const dialogTraceVisible = ref(false);
const selectID = ref(null);
const openAddTraceDialog = (id) => {
  dialogTraceVisible.value = true;
  selectID.value = id;
};
const closeAddTraceDialog = () => {
  dialogTraceVisible.value = false;
  traceForm.value = {info: "", images: []};
  info.value = "";
}

// 当前步骤
const activeStep = ref(0);

// 示例商品数据
const products = ref([]);
const filteredProducts = ref([]);

async function initProducts() {
  try {
    const response = await axios.post('/api/shop/getAllGoodInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data != null) {
      products.value = response.data;
      filteredProducts.value = products.value;
    }
  } catch (err) {
    ElMessage.error("获取商品信息失败");
  }
}

// 搜索框内容
const searchQuery = ref("");

// 过滤后的商品列表
function searchProducts() {
  if (searchQuery.value === "") {
    filteredProducts.value = products.value;
  } else {
    filteredProducts.value = products.value
        .filter(item =>
            item.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
            item.goodID.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
        .map(item => ({
          ...item,
          specs: item.specs.map(spec => ({...spec, imageUrl: spec.imageUrl.replace("@", "")}))
        }));
  }
}

const toggleProduct = (id) => {
  const index = selectedProducts.value.indexOf(id);
  if (index === -1) {
    selectedProducts.value.push(id);
  } else {
    selectedProducts.value.splice(index, 1);
  }
};

// 已选择的商品
const selectedProducts = ref([]);

const getProductById = (id) => {
  return products.value.find((product) => product.goodID === id) || {};
};

const removeProduct = (id) => {
  const index = selectedProducts.value.indexOf(id);
  if (index > -1) {
    selectedProducts.value.splice(index, 1);
  }
};

// 溯源信息表单
const traceForm = ref({
  info: "",
  images: []
})
const info = ref("");
const dialogVisible = ref(false); // 控制图片预览弹窗
const dialogImageUrl = ref(""); // 预览图片的路径
const MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

const handleFileChange = (file) => {
  if (file.size > MAX_FILE_SIZE) {
    ElMessage({
      type: 'error',
      message: '文件大小不能超过 3MB',
    });
    return;
  }
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = (e) => {
    if (!traceForm.value.images)
      traceForm.value.images = [];
    traceForm.value.images.push({
      name: file.name,
      url: e.url || URL.createObjectURL(file.raw),
      file: file.raw
    });
  };
};

const handleRemove = (file) => {
  traceForm.value.images = traceForm.value.images.filter(item => item.file !== file.raw);
};

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url; // 获取图片的 URL 或原始文件
  dialogVisible.value = true;
};

// 下一步
const nextStep = () => {
  if (activeStep.value === 0 && selectedProducts.value.length === 0) {
    ElMessage.error("请选择至少一个商品！");
    return;
  }
  activeStep.value++;
};

// 上一步
const prevStep = () => {
  if (activeStep.value === 1) {
    traceForm.value = {info: "", images: []};
  }
  if (activeStep.value > 0) activeStep.value--;
};

async function submitTraceImg(traceID) {
  const formData = new FormData();
  for (const file of traceForm.value.images) {
    formData.append('images', file.file);
  }
  formData.append('traceInfoID', traceID);
  try {
    const response = await axios.post('/api/trace/uploadImg', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("溯源信息提交成功！");
      // 清空表单
      info.value = "";
      traceForm.value = {info: "", images: []};
      selectedProducts.value = [];
      activeStep.value = 0;
      activeTab.value = "trace";
      selectID.value = null;
      dialogTraceVisible.value = false;
      await initTraceData();
    } else {
      ElMessage.error("上传图片失败");
    }
  } catch (err) {
    ElMessage.error("上传图片失败");
  }
}

// 提交溯源信息
async function submitTrace() {
  traceForm.value.info = info.value;
  if (!traceForm.value.info.trim()) {
    ElMessage.error("请填写溯源信息！");
    return;
  }
  if (!traceForm.value.images) {
    ElMessage.error("请上传至少一张图片！");
    return;
  }
  try {
    const response = await axios.post('/api/trace/createTrace', {
      goodIDs: selectedProducts.value,
      info: traceForm.value.info,
      userID: UserForm.value.id,
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.result.code === 200) {
      if (traceForm.value.images) {
        await submitTraceImg(response.data.traceID[1]);
      } else {
        ElMessage.success("溯源信息提交成功！");
        // 清空表单
        info.value = "";
        traceForm.value = {info: "", images: []};
        selectedProducts.value = [];
        activeStep.value = 0;
        activeTab.value = "trace";
        dialogTraceVisible.value = false;
        await initTraceData();
      }
    } else {
      console.log(response.data.result.code);
      ElMessage.error("提交溯源信息失败");
    }
  } catch (err) {
    ElMessage.error("提交溯源信息失败");
  }
}

async function addTraceInfo(traceId) {
  traceForm.value.info = info.value;
  if (!traceForm.value.info.trim()) {
    ElMessage.error("请填写溯源信息！");
    return;
  }
  if (!traceForm.value.images) {
    ElMessage.error("请上传至少一张图片！");
    return;
  }
  try {
    const response = await axios.post('/api/trace/addTrace', {
      traceID: traceId,
      info: traceForm.value.info,
      userID: UserForm.value.id,
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.result.code === 200) {
      if (traceForm.value.images) {
        await submitTraceImg(response.data.InfoID);
      } else {
        ElMessage.success("溯源信息提交成功！");
        // 清空表单
        info.value = "";
        traceForm.value = {info: "", images: []};
        selectID.value = null;
        await initTraceData();
      }
    } else {
      ElMessage.error("提交溯源信息失败1")
    }
  } catch (err) {
    ElMessage.error("提交溯源信息失败");
  }
}

onMounted(async () => {
  loading.value = true;
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex('2-2')
  storeHeader.value.setTitle("溯源管理")
  await initTraceData();
  await initProducts();
  loading.value = false;
});
</script>

<style lang="less" scoped>
.step-container {
  padding-right: 20px;
  border-right: 1px solid #e6e6e6;
}

.step-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.search-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  padding-right: 20px;

  .search-input {
    padding: 10px;
    font-size: 16px;
    border: 2px solid #ccc;
    border-radius: 8px 0 0 8px;
    outline: none;
    width: 200px;
    border-right: none;
  }

  .search-button {
    padding: 10px 20px;
    background-color: #e02020;
    color: white;
    font-size: 16px;
    border: 2px solid #e02020;
    border-radius: 0 8px 8px 0;
    cursor: pointer;
    font-weight: bold;
  }

  .search-button:hover {
    background-color: #c21a1a;
    border-color: #c21a1a;
  }
}

.product-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  min-height: 60vh;
}

.product-card {
  text-align: center;
  padding: 15px;
  cursor: pointer;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

.product-card.selected-card {
  border-color: #409eff;
  background-color: #f0faff;
}

.product-image {
  width: 100px;
  height: 100px;
  margin-bottom: 10px;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid #e6e6e6;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.step-actions {
  margin-top: 20px;
  text-align: right;
}

.el-upload {
  margin-top: 10px;
}

.trace-form {
  padding: 20px;
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.trace-form .el-form-item {
  margin-bottom: 20px;
}

.el-upload {
  margin-top: 10px;
}

.el-upload .el-icon-plus {
  font-size: 24px;
  color: #409eff;
}

.el-upload__tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.step-actions {
  margin-top: 20px;
  text-align: right;
}

.step-actions .el-button {
  margin-left: 10px;
}

.fixed-selected-products {
  position: fixed;
  top: 300px;
  right: 20px;
  width: 300px;
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.fixed-selected-products h4 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.selected-product-card {
  margin-bottom: 12px;
  padding: 8px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
}

.product-thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
}

.selected-product-name {
  margin: 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.remove-btn {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

/* 折叠面板整体样式 */
.trace-collapse {
  margin-bottom: 20px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 折叠面板标题 */
.collapse-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.collapse-title .el-tag {
  font-size: 12px;
  background-color: #f5f7fa;
  color: #409eff;
}

/* 时间线样式 */
.timeline-entry {
  padding: 12px 0;
}

.timeline-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
  text-align: start;
}

.timeline-entry img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-top: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 增加溯源信息按钮 */
.add-trace-button {
  text-align: right;
  margin-top: 20px;
  margin-right: 20px;
}

.add-trace-button .el-button {
  padding: 10px 24px;
  font-size: 14px;
}

/* 弹窗样式 */
.el-dialog {
  border-radius: 10px;
}

.el-dialog__header {
  font-weight: bold;
  font-size: 18px;
  color: #333;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-upload .el-icon-plus {
  font-size: 36px;
  color: #409eff;
}

.el-upload__tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  text-align: left;
}

.el-upload__trigger:hover {
  background-color: #f5f7fa;
  border: 1px dashed #409eff;
}

.el-timeline {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-top: 10px;
}

.el-timeline-item__content {
  font-size: 14px;
  color: #333;
}

.timeline-images {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.timeline-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

</style>

