<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider"></ShopSliderComponent>
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="p-4" v-loading="loading">
        <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleTabClick">
          <el-tab-pane label="已上架商品" name="first">
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
            <div v-for="(product, productIndex) in filteredProducts" :key="productIndex" class="product-form-container">
              <div class="product-header">
                <h2 class="form-title" v-if="!product.isEdited">{{ product.name }}</h2>
                <el-input v-model="product.name" type="textarea" placeholder="请输入商品名称" v-else/>
                <div>
                  <div v-if="!product.isEdited">
                    <el-button type="danger" @click="removeProduct(product)" size="small"
                               v-if="product.status === true">下架
                    </el-button>
                    <el-button type="danger" @click="removeProduct(product)" size="small" v-else>上架
                    </el-button>
                    <el-button type="primary" @click="editProduct(product)" size="small">修改</el-button>
                  </div>
                  <div v-else>
                    <el-button type="primary" @click="saveProduct(product)" size="small">保存</el-button>
                    <el-button type="danger" @click="cancelEdit(product)" size="small">取消</el-button>
                  </div>
                </div>
              </div>
              <el-divider/>

              <!-- 商品信息 -->
              <div class="product-info">
                <div class="image-preview">
                  <template v-if="product.isEdited">
                    <el-upload
                        class="upload-demo"
                        :show-file-list="false"
                        :on-change="(file) =>  handleImageChangeMain(file, productIndex)"
                        :before-upload="beforeSpecImageUpload"
                        accept="image/*"
                        drag
                    >
                      <div v-if="!product.imageUrl" class="image-placeholder">
                        <el-icon>
                          <plus/>
                        </el-icon>
                        <p>点击上传或拖拽图片</p>
                      </div>
                      <el-image
                          v-else
                          :src="product.imageUrl"
                          alt="商品图片预览"
                          style="width: 200px; height: 200px; object-fit: cover; border-radius: 10px;"
                      />
                    </el-upload>
                  </template>
                  <template v-else>
                    <el-image
                        style="width: 200px; height: 200px; object-fit: cover; border-radius: 10px;"
                        :src="product.imageUrl"
                        fit="cover"
                    />
                  </template>
                </div>
                <div class="info-fields">
                  <el-form :model="product" label-width="80px">
                    <el-form-item label="生产地">
                      <el-input v-model="product.origin" placeholder="请输入生产地"
                                :disabled="!product.isEdited"/>
                    </el-form-item>
                  </el-form>
                </div>
              </div>

              <el-divider/>

              <!-- 商品规格 -->
              <h3 class="spec-title">商品规格</h3>
              <div v-for="(spec, specIndex) in product.specs" :key="specIndex" class="spec-item">
                <div class="spec-image">
                  <template v-if="spec.isEdited">
                    <el-upload
                        class="upload-demo"
                        :show-file-list="false"
                        :on-change="(file) => handleSpecImageChangeMain(file, productIndex, specIndex)"
                        :before-upload="beforeSpecImageUpload"
                        accept="image/*"
                        drag
                    >
                      <div v-if="!spec.imageUrl" class="image-placeholder">
                        <el-icon>
                          <plus/>
                        </el-icon>
                        <p>点击上传或拖拽图片</p>
                      </div>
                      <el-image
                          v-else
                          :src="spec.imageUrl"
                          alt="规格图片预览"
                          style="width: 100px; height: 100px; object-fit: cover; border-radius: 10px;"
                      />
                    </el-upload>
                  </template>
                  <template v-else>
                    <el-image
                        style="width: 100px; height: 100px; object-fit: cover; border-radius: 10px;"
                        :src="spec.imageUrl"
                        fit="cover"
                    />
                  </template>
                </div>
                <div class="spec-fields">
                  <el-form label-width="80px">
                    <el-form-item label="规格名称">
                      <el-input v-model="spec.name" :disabled="!spec.isEdited"/>
                    </el-form-item>
                    <el-form-item label="价格">
                      <el-input v-model="spec.price" :disabled="!spec.isEdited"/>
                    </el-form-item>
                    <el-form-item label="库存">
                      <el-input v-model="spec.stock" :disabled="!spec.isEdited"/>
                    </el-form-item>
                  </el-form>
                </div>
                <div class="spec-actions">
                  <div v-if="!spec.isEdited">
                    <el-button type="danger" size="small" @click="removeSpec(product,spec)"
                               v-if="spec.status === true">下架
                    </el-button>
                    <el-button type="danger" size="small" @click="removeSpec(product, spec)" v-else>上架
                    </el-button>
                    <el-button type="primary" size="small" @click="editSpec(spec)">修改</el-button>
                  </div>
                  <div v-else>
                    <el-button type="primary" size="small" @click="saveSpec(product, spec)">保存</el-button>
                    <el-button type="danger" size="small" @click="cancelEditSpec(spec)">取消</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="上架商品" name="second">
            <div class="product-form-container">
              <div>
                <h2 class="form-title">填写商品信息
                  <el-button type="primary" @click="submitGood">
                    完成
                  </el-button>
                </h2>
              </div>
              <el-divider/>

              <!-- 商品信息 -->
              <div class="product-info">
                <div class="image-preview">
                  <el-upload
                      class="upload-demo"
                      :show-file-list="false"
                      :on-change="handleImageChange"
                      accept="image/*"
                      drag
                  >
                    <div v-if="!productForm.imageUrl" class="image-placeholder">
                      <el-icon>
                        <plus/>
                      </el-icon>
                      <p>点击上传或拖拽图片</p>
                    </div>
                    <img
                        v-else
                        :src="productForm.imageUrl"
                        alt="商品图片预览"
                        style="width: 200px; height: 200px; object-fit: cover; border-radius: 10px; cursor: pointer;"
                    />
                  </el-upload>
                </div>
                <div class="info-fields">
                  <el-form :model="productForm" label-width="80px">
                    <el-form-item label="商品名称">
                      <el-input v-model="productForm.name" type="textarea" placeholder="请输入商品名称"/>
                    </el-form-item>
                    <el-form-item label="生产地">
                      <el-input v-model="productForm.origin" placeholder="请输入生产地"/>
                    </el-form-item>
                  </el-form>
                </div>
              </div>

              <el-divider/>

              <!-- 商品规格 -->
              <h3 class="spec-title">商品规格</h3>
              <div
                  v-for="(spec, index) in productForm.specs"
                  :key="index"
                  class="spec-item"
              >
                <div class="spec-image">
                  <el-upload
                      class="upload-demo"
                      :show-file-list="false"
                      :on-change="(file) => handleSpecImageChange(file, index)"
                      accept="image/*"
                      drag
                  >
                    <div v-if="!spec.imageUrl" class="image-placeholder">
                      <el-icon>
                        <plus/>
                      </el-icon>
                      <p>点击上传或拖拽图片</p>
                    </div>
                    <img
                        v-else
                        :src="spec.imageUrl"
                        alt="规格图片预览"
                        style="width: 100px; height: 100px; object-fit: cover; border-radius: 10px;"
                    />
                  </el-upload>
                </div>
                <div class="spec-fields">
                  <el-form label-width="80px">
                    <el-form-item label="规格名称">
                      <el-input v-model="spec.name" placeholder="请输入规格名称"/>
                    </el-form-item>
                    <el-form-item label="价格">
                      <el-input v-model="spec.price" placeholder="请输入价格"/>
                    </el-form-item>
                    <el-form-item label="库存">
                      <el-input v-model="spec.stock" placeholder="请输入库存"/>
                    </el-form-item>
                  </el-form>
                </div>
              </div>

              <div class="add-spec">
                <el-button
                    type="primary"
                    @click="addSpec"
                    plain
                    size="small"
                >
                  <el-icon>
                    <plus/>
                  </el-icon>
                  添加规格
                </el-button>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import {onMounted, ref} from "vue";
import {useUserShop} from "@/composables/useShopUser";
import {Plus, Search} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import StoreHeaderView from "@/components/StoreHeaderView.vue";

const shopSlider = ref(null);
const storeHeader = ref(null);
const {shopForm, initUserSession, initShopInfo, UserForm} = useUserShop();
const activeName = ref("first");

const products = ref();
const loading = ref();

function isMerchant() {
  if (UserForm.value.role !== "merchant") {
    ElMessage.error("您没有权限访问该页面");
    setTimeout(() => {
      window.location.href = "/store/index";
    }, 1000);
    return false;
  }
  return true;
}

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

const filteredProducts = ref([]);
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

// 下架商品
async function removeProduct(product) {
  try {
    const response = await axios.post('/api/shop/updateGoodInfo', {
      name: product.name,
      goodID: product.goodID,
      updateStatus: !product.status,
      shopID: shopForm.value.shopID,
      origin: product.origin,
      imageUrl: product.imageUrl,
      type: "goodStatus"
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("更改状态成功");
      await initProducts();
    } else if (response.data.code === 400) {
      ElMessage.error("更改状态失败");
    }
  } catch (err) {
    ElMessage.error("更改状态失败");
  }
}

// 修改商品
const originalProductInfo = ref();

function editProduct(product) {
  originalProductInfo.value = {...product};
  product.isEdited = true;
}

const selectedGoodFile = ref(null);

function handleImageChangeMain(file, productIndex) {
  const reader = new FileReader();
  selectedGoodFile.value = file.raw;
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    products.value[productIndex].imageUrl = reader.result;
  };
}

async function uploadProductImage(product) {
  const formData = new FormData();
  formData.append("image", selectedGoodFile.value);
  formData.append("goodID", product.goodID)
  try {
    const response = await axios.post("/api/shop/updateGoodImage", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("上传图片成功");
    } else if (response.data.code === 400) {
      ElMessage.error("上传图片失败");
    }
  } catch (err) {
    ElMessage.error("上传图片失败");
  }
}

async function saveProduct(product) {
  if (product.name === "" || product.origin === "") {
    ElMessage.error("请填写完整商品信息");
    return;
  }
  const isChanged = product.imageUrl !== originalProductInfo.value.imageUrl;
  try {
    const response = await axios.post('/api/shop/updateGoodInfo', {
      name: product.name,
      goodID: product.goodID,
      shopID: shopForm.value.shopID,
      origin: product.origin,
      imageUrl: product.imageUrl,
      type: "goodInfo"
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("修改商品信息成功");
      if (isChanged)
        await uploadProductImage(product);
      window.location.reload();
    } else if (response.data.code === 400) {
      ElMessage.error("修改商品信息失败");
    }
  } catch (err) {
    ElMessage.error("修改商品信息失败");
  }
}

function cancelEdit(product) {
  product.isEdited = false;
  // 重置商品信息
  product.name = originalProductInfo.value.name;
  product.origin = originalProductInfo.value.origin;
  product.imageUrl = originalProductInfo.value.imageUrl;
  product.specs = originalProductInfo.value.specs
  originalProductInfo.value = null;
}

// 下架规格
async function removeSpec(product, spec) {
  try {
    const response = await axios.post('/api/shop/updateSpecInfo', {
      goodID: product.goodID,
      specID: spec.specID,
      updateStatus: !spec.status,
      specName: spec.name,
      specPrice: spec.price,
      specStock: spec.stock,
      imageUrl: spec.imageUrl,
      type: "specStatus"
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("更改规格状态成功");
      await initProducts();
      spec.isEdited = false;
    } else if (response.data.code === 400) {
      ElMessage.error("更改规格状态失败");
    }
  } catch (err) {
    ElMessage.error("更改规格状态失败");
  }
}

// 修改规格
const originalSpecInfo = ref();

function editSpec(spec) {
  spec.isEdited = true;
  originalSpecInfo.value = {...spec};
}

function beforeSpecImageUpload(file) {
  const isImage = file.type.startsWith("image/");
  if (!isImage) {
    ElMessage.error("仅支持图片文件！");
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error("文件大小不能超过 2MB！");
    return false;
  }
  return true;
}

const selectedFile = ref(null);

function handleSpecImageChangeMain(file, productIndex, specIndex) {
  const reader = new FileReader();
  selectedFile.value = file.raw;
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    products.value[productIndex].specs[specIndex].imageUrl = reader.result;
  };
}

async function uploadSpecImage(spec) {
  const formData = new FormData();
  formData.append("image", selectedFile.value);
  formData.append("specID", spec.specID)
  try {
    const response = await axios.post("/api/shop/updateSpecImage", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("上传图片成功");
    } else if (response.data.code === 400) {
      ElMessage.error("上传图片失败");
    }
  } catch (err) {
    ElMessage.error("上传图片失败");
  }
}

async function saveSpec(product, spec) {
  if (spec.name === "" || spec.price === "" || spec.stock === "") {
    ElMessage.error("请填写完整规格信息");
    return;
  }
  const isChanged = spec.imageUrl !== originalSpecInfo.value.imageUrl;
  try {
    const response = await axios.post('/api/shop/updateSpecInfo', {
      specName: spec.name,
      goodID: product.goodID,
      specID: spec.specID,
      specPrice: spec.price,
      specStock: spec.stock,
      imageUrl: spec.imageUrl,
      type: "specInfo"
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success("修改规格信息成功");
      if (isChanged)
        await uploadSpecImage(spec);
      window.location.reload();
    } else if (response.data.code === 400) {
      ElMessage.error("修改规格信息失败");
    }
  } catch (err) {
    ElMessage.error("修改规格信息失败");
  }
}

function cancelEditSpec(spec) {
  spec.isEdited = false;
  // 重置规格信息
  spec.name = originalSpecInfo.value.name;
  spec.price = originalSpecInfo.value.price;
  spec.stock = originalSpecInfo.value.stock;
  spec.imageUrl = originalSpecInfo.value.imageUrl;
  selectedFile.value = null;
  originalSpecInfo.value = null;
}

const productForm = ref({
  shopID: "",
  name: "",
  origin: "",
  imageUrl: "", // 商品主图预览URL
  specs: [
    {name: "", price: "", stock: "", imageUrl: ""}, // 规格初始项
  ],
});

const handleTabClick = () => {
  if (activeName.value === "second") {
    productForm.value.name = "";
    productForm.value.origin = "";
    productForm.value.imageUrl = "";
    productForm.value.specs = [
      {name: "", price: "", stock: "", imageUrl: ""},
    ];
  }
}

// 商品主图选择事件
const handleImageChange = (file) => {
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    productForm.value.imageUrl = reader.result;
  };
};

// 商品规格图片选择事件
const handleSpecImageChange = (file, index) => {
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    productForm.value.specs[index].imageUrl = reader.result;
  };
};

// 添加规格
const addSpec = () => {
  productForm.value.specs.push({name: "", price: "", stock: "", imageUrl: ""});
};

async function submitGood() {
  try {
    const response = await axios.post("/api/shop/insertGoodInfo", {
      shopID: shopForm.value.shopID,
      name: productForm.value.name,
      origin: productForm.value.origin,
      imageUrl: productForm.value.imageUrl,
      specs: productForm.value.specs,
    });
    if (response.data.code === 200) {
      ElMessage.success("商品上架成功")
      window.location.reload();
    } else if (response.data.code === 400) {
      ElMessage.error("商品上架失败")
    }
  } catch (err) {
    ElMessage.error("商品上架失败")
  }
}

onMounted(async () => {
  loading.value = true;
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex("2-1");
  storeHeader.value.setTitle("商品管理");
  if (isMerchant()) {
    productForm.value.shopID = shopForm.value.shopID;
    await initProducts();
    loading.value = false;
  }
});
</script>

<style scoped lang="less">
.product-form-container {
  border: 1px solid #e6e6e6;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
  padding: 24px;
  background: #fff;
}

.search-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
  padding-right: 20px;
}

.search-input {
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ccc;
  border-radius: 8px 0 0 8px;
  outline: none;
  width: 200px;
  border-right: none;
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.product-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.image-preview {
  width: 200px;
  height: 200px;
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  background: #f5f5f5;
  cursor: pointer;
}

.image-placeholder {
  text-align: center;
  color: #999;
  font-size: 14px;
}

.spec-title {
  font-size: 18px;
  font-weight: 600;
  margin: 16px 0;
  color: #666;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.spec-fields {
  flex: 1;
}

.add-spec {
  text-align: center;
  margin-top: 16px;
}
</style>
