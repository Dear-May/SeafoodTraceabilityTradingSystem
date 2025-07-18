<template>
  <div v-loading="loading">
    <!-- 顶部用户信息与统计卡片 -->
    <div class="d-flex justify-content-between flex-wrap">
      <!-- 左侧用户信息卡片保持不变 -->
      <div class="card mb-3" style="width: 48%; min-width: 350px; max-height: 168px;">
        <div class="card-body d-flex align-items-center">
          <img class="rounded-circle me-3" :src="UserForm.avatar" alt="Admin" width="80" height="80"/>
          <div>
            <h4 class="card-title mb-0">{{ UserForm.nickname }}</h4>
            <small class="text-muted" v-if="UserForm.role==='merchant'">店长</small>
            <small class="text-muted" v-else-if="UserForm.role==='staff'">店员</small>
            <div class="mt-2 text-muted" style="text-align: left;">
              <div>手机号：{{ UserForm.phone }}</div>
              <div>邮箱：{{ UserForm.email }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧统计卡片区域 -->
      <div class="d-flex flex-wrap" style="width: 48%; min-width:350px;" v-if="UserForm.role==='merchant'">
        <div
            v-for="item in topStats"
            :key="item.title"
            class="stat-card me-3 mb-3"
        >
          <div class="stat-amount">{{ item.amount }}</div>
          <div class="stat-desc">{{ item.title }}</div>
        </div>
      </div>
      <div v-else class="d-flex flex-wrap" style="width: 48%; min-width:350px;">
        <el-calendar
            :model-value="currentDate"
        >
          <!-- 自定义头部 -->
          <template #header="{ date }">
            <div class="calendar-header">
              <h2>员工考勤表</h2>
              <span>当前月份：{{ date }}</span>
            </div>
          </template>

          <!-- 自定义日期单元格 -->
          <template #date-cell="{ data }">
            <div v-if="!isFutureDate(data.day)" class="date-cell">
              <!-- 显示日期 -->
              {{ data.day.split('-').slice(1).join('-') }}

              <!-- 考勤状态 -->
              <div style="width: 100%;" @click="handleAttendance(data.day)">
                <el-tag
                    :type="getAttendanceStatus(data.day) === '已签到' ? 'success' : 'info'"
                >
                  {{ getAttendanceStatus(data.day) }}
                </el-tag>
              </div>
            </div>
          </template>

        </el-calendar>
      </div>
    </div>

    <!-- 下方数据表格与图表保持不变 -->
    <div class="row mt-4">
      <div class="col-12 col-lg-6 mb-3">
        <div class="card">
          <div class="card-header">
            商品购买情况
          </div>
          <div class="card-body p-0">
            <table class="table mb-0 table-striped">
              <thead>
              <tr>
                <th>商品</th>
                <th>今日购买</th>
                <th>本月购买</th>
                <th>总购买</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="course in courseData" :key="course.name">
                <td>{{ course.name }}</td>
                <td>{{ course.today }}</td>
                <td>{{ course.month }}</td>
                <td>{{ course.total }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <!-- 折线图示例 -->
      <div class="col-12 col-lg-6 mb-3">
        <div class="card">
          <div class="card-header">
            销量趋势
          </div>
          <div class="card-body">
            <div ref="lineChart" style="width:100%;height:300px;"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部两个图表 -->
    <div class="row mt-3">
      <!-- 柱状图 -->
      <div class="col-12 col-lg-6 mb-3">
        <div class="card">
          <div class="card-header">
            用户情况
          </div>
          <div class="card-body">
            <div ref="barChart" style="width:100%;height:300px;"></div>
          </div>
        </div>
      </div>
      <!-- 饼图 -->
      <div class="col-12 col-lg-6 mb-3">
        <div class="card">
          <div class="card-header">
            订单状态占比
          </div>
          <div class="card-body">
            <div ref="pieChart" style="width:100%;height:300px;"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts'
import {useUserShop} from "@/composables/useShopUser";
import axios from 'axios'
import {ElMessage} from "element-plus";

const {UserForm, initUserSession, initShopInfo, shopForm} = useUserShop();

// 顶部小卡片数据
const topStats = ref([]);
const loading = ref(false);

async function initTopStats() {
  try {
    const response = await axios.post('/api/shop/info/getCardInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    if (response.data != null)
      topStats.value = response.data;
    else
      ElMessage.error("统计卡片数据获取失败")
  } catch (err) {
    ElMessage.error("统计卡片数据获取失败")
  }
}

const currentDate = ref(new Date())
const signedInData = ref([]);

async function initAttendanceInfo() {
  try {
    const response = await axios.post('/api/shop/staff/attendance/getShopStaffAttendance', {
      userId: UserForm.value.id
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    if (response.data != null) {
      signedInData.value = response.data;
    }
  } catch (error) {
    ElMessage.error("获取员工考勤信息失败")
  }
}

const isFutureDate = (date) => {
  const givenDate = new Date(date);
  return givenDate > currentDate.value;
};

const getAttendanceStatus = (date) => {
  const today = new Date(date).toISOString().split("T")[0];
  const isSignedIn = signedInData.value.some((item) => item.date === today);
  return isSignedIn ? "已签到" : "未签到";
};

const extractDate = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

async function handleAttendance(date) {
  if (date === extractDate(currentDate.value) && getAttendanceStatus(date) !== "已签到") {
    try {
      const response = await axios.post('/api/shop/staff/attendance/signIn', {
        userId: UserForm.value.id,
        date: date,
        shopID: shopForm.value.shopID,
      }, {
        headers: {
          'Content-Type': 'application/json',
        },
        withCredentials: true,
      });
      if (response.data.code === 200) {
        ElMessage.success("签到成功")
        await initAttendanceInfo();
      } else if (response.data.code === 400)
        ElMessage.error("签到失败")
      else if (response.data.code === 401)
        ElMessage.error("签到时间与服务器时间不符")
    } catch (error) {
      ElMessage.error("签到失败")
    }
  }
}

const courseData = ref([]);

async function initCourseData() {
  try {
    const response = await axios.post('/api/shop/info/getProductBuyInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    if (response.data != null)
      courseData.value = response.data;
    else
      ElMessage.error("商品购买数据获取失败")
  } catch (err) {
    ElMessage.error("商品购买数据获取失败")
  }
}

const lineChart = ref(null);

async function initLineChart() {
  try {
    const response = await axios.post('/api/shop/info/getProductTrendInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    console.log(response.data)
    if (response.data != null) {
      const myLineChart = echarts.init(lineChart.value);
      myLineChart.setOption({
        tooltip: response.data.tooltip,
        legend: response.data.legend,
        xAxis: response.data.xAxis,
        yAxis: response.data.yAxis,
        series: response.data.series
      });
    }
  } catch (err) {
    ElMessage.error("销量趋势数据获取失败")
  }
}

const barChart = ref(null)

async function initBarChart() {
  try {
    const response = await axios.post('/api/shop/info/getUserInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    if (response.data != null) {
      const myBarChart = echarts.init(barChart.value)
      myBarChart.setOption({
        tooltip: response.data.tooltip,
        legend: response.data.legend,
        xAxis: response.data.xAxis,
        yAxis: response.data.yAxis,
        series: response.data.series
      });
    }
  } catch (err) {
    ElMessage.error("用户情况数据获取失败")
  }
}

const pieChart = ref(null)

async function initPieChart() {
  try {
    const response = await axios.post('/api/shop/info/getOrderStatusInfo', {
      shopID: shopForm.value.shopID,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    });
    if (response.data != null) {
      const myPieChart = echarts.init(pieChart.value)
      myPieChart.setOption({
        tooltip: response.data.tooltip,
        legend: response.data.legend,
        series: response.data.series
      });
    }
  } catch (err) {
    ElMessage.error("订单状态占比数据获取失败")
  }
}

async function initData() {
  if (UserForm.value.role === 'merchant')
    await initTopStats();
  else if (UserForm.value.role === 'staff')
    await initAttendanceInfo();
  await initCourseData();
  await initLineChart();
  await initBarChart();
  await initPieChart();
}

onMounted(async () => {
  loading.value = true;
  initUserSession();
  await initShopInfo();
  await initData();
  loading.value = false;
})
</script>

<style scoped>
.card {
  border-radius: 6px;
}

.card-header {
  font-weight: 600;
}

/* 新增：统计卡片样式 */
.stat-card {
  background: #00c1de;
  color: #fff;
  padding: 10px 20px;
  border-radius: 4px;
  width: 150px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-card .stat-amount {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-card .stat-desc {
  font-size: 14px;
}

.date-cell {
  position: relative;
  text-align: center;
  font-size: 14px;
}

.today-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 半透明黑色遮罩 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  z-index: 1;
}

.el-tag {
  margin-top: 5px;
  font-size: 12px;
}
</style>
