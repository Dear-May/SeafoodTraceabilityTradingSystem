<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <ShopSliderComponent ref="shopSlider" @initApplications="initApplications"></ShopSliderComponent>
    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <StoreHeaderView ref="storeHeader"></StoreHeaderView>

      <div class="p-4">
        <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleTabClick">
          <!-- 全部员工 -->
          <el-tab-pane name="first">
            <template #label>
              <span>
                <el-badge :value="employees.length" :max="99" class="item">
                  <i class="bi bi-person-fill" style="font-size: 16px; color: #333;"/>
                  <a style="margin-left: 5px; font-weight: bold; font-size: 16px; color: #333;">全部员工</a>
                </el-badge>
              </span>
            </template>

            <!-- 搜索框 -->
            <el-input
                v-model="searchQuery"
                placeholder="搜索昵称、手机号、邮箱"
                class="mt-4 mb-4"
                clearable
                prefix-icon="el-icon-search"
                @input="filterEmployees"
            />

            <el-table :data="filteredEmployees" stripe border v-loading="loading" style="width: 100%;">
              <el-table-column prop="avatar" label="头像" width="220" align="center">
                <template #default="scope">
                  <el-avatar :src="scope.row.avatar" size="medium"/>
                </template>
              </el-table-column>
              <el-table-column prop="nickname" label="昵称" width="300" align="center"></el-table-column>
              <el-table-column prop="phone" label="手机号" width="400" align="center"></el-table-column>
              <el-table-column prop="email" label="邮箱" width="500" align="center"></el-table-column>
              <el-table-column label="操作" width="250" align="center">
                <template #default="scope">
                  <el-button
                      type="danger"
                      size="small"
                      @click="confirmFireEmployee(scope.row)"
                  >
                    离职
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 申请列表 -->
          <el-tab-pane name="second">
            <template #label>
              <span>
                <el-badge :value="applications.length" :max="99" class="item">
                  <i class="bi bi-arrow-down" style="font-size: 16px; color: #333;"/>
                  <a style="margin-left: 5px; font-weight: bold; font-size: 16px; color: #333;">申请列表</a>
                </el-badge>
              </span>
            </template>
            <el-table :data="applications" stripe border v-loading="loading" style="width: 100%;">
              <el-table-column prop="avatar" label="头像" width="220" align="center">
                <template #default="scope">
                  <el-avatar :src="scope.row.avatar" size="medium"/>
                </template>
              </el-table-column>
              <el-table-column prop="nickname" label="昵称" width="300" align="center"></el-table-column>
              <el-table-column prop="phone" label="手机号" width="400" align="center"></el-table-column>
              <el-table-column prop="email" label="邮箱" width="500" align="center"></el-table-column>
              <el-table-column label="操作" width="250" align="center">
                <template #default="scope">
                  <el-button
                      type="primary"
                      size="small"
                      @click="approveApplication(scope.row, true)"
                  >
                    允许
                  </el-button>
                  <el-button
                      type="danger"
                      size="small"
                      @click="approveApplication(scope.row, false)"
                  >
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>

        <!-- 离职确认弹窗 -->
        <el-dialog
            title="确认离职"
            v-model="fireDialogVisible"
            width="30%"
            @close="cancelFire"
        >
          <span>您确定要让员工 <strong>{{ selectedEmployee.nickname }}</strong> 离职吗？</span>
          <template #footer>
            <el-button @click="cancelFire">取消</el-button>
            <el-button type="danger" @click="fireEmployee">确认</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import ShopSliderComponent from "@/components/ShopSliderComponent.vue";
import {useUserShop} from "@/composables/useShopUser";
import {computed, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import axios from "axios";
import StoreHeaderView from "@/components/StoreHeaderView.vue";

const {shopForm, initUserSession, initShopInfo, UserForm} = useUserShop();
const shopSlider = ref(null);
const storeHeader = ref(null);
const activeName = ref("first");
const loading = ref(false);

function isMerchant() {
  if (UserForm.value.role !== "merchant") {
    ElMessage.error("您没有权限访问该页面");
    setTimeout(() => {
      window.location.href = "/store/index";
    }, 100);
    return false;
  }
  return true;
}

const employees = ref([]);

async function initEmployees() {
  try {
    const response = await axios.post("/api/shop/staff/getShopStaffInfo", {
      shopID: shopForm.value.shopID
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data !== null) {
      employees.value = response.data;
    }
  } catch (err) {
    ElMessage.error("获取员工信息失败");
  }
}

const applications = ref([]);

async function initApplications() {
  try {
    const response = await axios.post("/api/shop/staff/getAuditShopStaffInfo", {
      shopID: shopForm.value.shopID
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    });
    if (response.data !== null) {
      applications.value = response.data;
    }
  } catch (err) {
    ElMessage.error("获取申请信息失败");
  }
}

// 搜索功能
const searchQuery = ref("");
const filteredEmployees = computed(() =>
    employees.value.filter(
        (e) =>
            e.nickname.includes(searchQuery.value) ||
            e.phone.includes(searchQuery.value) ||
            e.email.includes(searchQuery.value)
    )
);

// 离职确认弹窗
const fireDialogVisible = ref(false);
const selectedEmployee = ref({});

// 打开离职确认弹窗
const confirmFireEmployee = (employee) => {
  selectedEmployee.value = employee;
  fireDialogVisible.value = true;
};

// 取消离职
const cancelFire = () => {
  fireDialogVisible.value = false;
  selectedEmployee.value = {};
};

// 确认离职
async function fireEmployee() {
  try {
    const response = await axios.post("/api/shop/staff/dismissStaff", {
      shopID: shopForm.value.shopID,
      id: selectedEmployee.value.id
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success(`员工 ${selectedEmployee.value.nickname} 已离职`);
      await initEmployees();
    } else {
      ElMessage.error("操作失败")
    }
  } catch (err) {
    ElMessage.error("操作失败")
  } finally {
    cancelFire();
  }
}

// 申请处理
async function approveApplication(application, result) {
  try {
    const response = await axios.post('/api/shop/staff/addStaff', {
      shopID: shopForm.value.shopID,
      id: application.id,
      result: result
    }, {
      headers: {
        "Content-Type": "application/json"
      },
      withCredentials: true
    })
    if (response.data.code === 200) {
      ElMessage.success(`已允许 ${application.nickname} 加入`);
      await initEmployees();
      await initApplications();
    }
  } catch (err) {
    ElMessage.error("操作失败");
  }
}

// 初始化数据
onMounted(async () => {
  loading.value = true;
  initUserSession();
  await initShopInfo();
  shopSlider.value.setActiveIndex("4-1");
  storeHeader.value.setTitle("店铺员工管理");
  if (isMerchant()) {
    await initEmployees();
    await initApplications();
  }
  loading.value = false;
});
</script>

<style lang="less" scoped>
.el-table th,
.el-table td {
  text-align: center;
}

.mt-4 {
  margin-top: 16px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
