<template>
  <div class="d-flex" style="height: 100vh; overflow: hidden;">
    <aside class="bg-dark text-light" style="width: 200px; padding-top: 20px;">
      <div class="text-center mb-4">
        <a style="font-size: 16px; font-weight: bold">后台管理</a>
      </div>
      <el-menu
          :default-openeds="['1']"
          :default-active="'1-1'"
          class="border-0"
          text-color="#fff"
          active-text-color="#ffd04b"
          background-color="#333"
      >
        <el-sub-menu index="1">
          <template #title>首页</template>
          <el-menu-item index="1-1">首页</el-menu-item>
          <el-menu-item index="1-2" @click="() => router.push('/admin/goodSync?id='+id)">搜索引擎商品数据同步
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <div class="flex-grow-1 overflow-auto" style="background: #f5f7fa;">
      <nav class="navbar navbar-light bg-white border-bottom shadow-sm">
        <div class="container-fluid">
          <span class="navbar-brand mb-0 h1">店铺审核</span>
        </div>
      </nav>

      <div class="p-4">
        <el-collapse v-model="activeNames">
          <el-collapse-item v-for="(shop, index) in shops" :key="index" :title="shop.name" :name="index">
            <table border="1">
              <thead>
              <tr>
                <th>项目</th>
                <th>信息</th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td>ID</td>
                <td>{{ shop.licenseInfo.id }}</td>
              </tr>
              <tr>
                <td>店铺ID</td>
                <td>{{ shop.licenseInfo.shopid }}</td>
              </tr>
              <tr>
                <td>插入时间</td>
                <td>{{ formatDate(shop.licenseInfo.inserttime) }}</td>
              </tr>
              <tr>
                <td>更新时间</td>
                <td>{{ formatDate(shop.licenseInfo.updatetime) }}</td>
              </tr>
              <tr>
                <td>状态</td>
                <td>{{ shop.licenseInfo.status }}</td>
              </tr>
              <tr>
                <td>统一社会信用代码</td>
                <td>{{ shop.licenseInfo.socialcreditcode }}</td>
              </tr>
              <tr>
                <td>名称</td>
                <td>{{ shop.licenseInfo.name }}</td>
              </tr>
              <tr>
                <td>地址</td>
                <td>{{ shop.licenseInfo.location }}</td>
              </tr>
              <tr>
                <td>注册资本</td>
                <td>{{ shop.licenseInfo.registeredcapital }}</td>
              </tr>
              <tr>
                <td>实收资本</td>
                <td>{{ shop.licenseInfo.paidincapital }}</td>
              </tr>
              <tr>
                <td>成立日期</td>
                <td>{{ shop.licenseInfo.dateofestablishment }}</td>
              </tr>
              <tr>
                <td>经营范围</td>
                <td>{{ shop.licenseInfo.businessscope }}</td>
              </tr>
              <tr>
                <td>法定代表人</td>
                <td>{{ shop.licenseInfo.legalrepresentative }}</td>
              </tr>
              <tr>
                <td>公司类型</td>
                <td>{{ shop.licenseInfo.type }}</td>
              </tr>
              <tr>
                <td>登记机关</td>
                <td>{{ shop.licenseInfo.registrationauthority }}</td>
              </tr>
              <tr>
                <td>组成形式</td>
                <td>{{ shop.licenseInfo.compositionform }}</td>
              </tr>
              <tr>
                <td>身份证号</td>
                <td>{{ shop.licenseInfo.idnumber }}</td>
              </tr>
              <tr>
                <td>有效期至</td>
                <td>{{ shop.licenseInfo.periodOfValidity }}</td>
              </tr>
              <tr>
                <td>有效期起始日期</td>
                <td>{{ shop.licenseInfo.startingdateofvalidityperiod }}</td>
              </tr>
              <tr>
                <td>批准日期</td>
                <td>{{ shop.licenseInfo.dateofapproval }}</td>
              </tr>
              <tr>
                <td>税务登记号</td>
                <td>{{ shop.licenseInfo.taxregistrationnumber }}</td>
              </tr>
              </tbody>
            </table>
            <el-image :src="shop.licenseImage" style="width: 50vw; height: auto"
                      :preview-src-list="[shop.licenseImage]"></el-image>
            <div class="mt-2">
              <el-button type="success" @click="approveShop(index)">通过审核</el-button>
              <el-button type="danger" @click="rejectShop(index)">退回申请</el-button>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

let id = null;

async function checkUserPermission() {
  id = new URLSearchParams(window.location.search).get('id');
  if (!id) {
    ElMessage.error('权限校验失败');
    await router.replace('/')
  } else {
    try {
      const response = await axios.post('/api/admin/checkUserPermission', {
        id: id,
      }, {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true
      });
      if (response.data.code !== 200) {
        ElMessage.error('无权限访问')
        await router.replace('/')
      }
    } catch (error) {
      ElMessage.error('获取权限失败')
      await router.replace('/')
    }
  }
}

const shops = ref([]);

const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以需要加1
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

async function initShops() {
  try {
    const response = await axios.post('/api/admin/getAllReviewLicenses', {}, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data !== null) {
      shops.value = response.data;
      console.log(shops.value);
    }
  } catch (error) {
    ElMessage.error('获取商家信息失败')
  }
}

const activeNames = ref([]);

async function approveShop(index) {
  try {
    const response = await axios.post('/api/admin/updateReviewLicenseStatus', {
      id: shops.value[index].licenseInfo.id,
      status: "通过"
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('审核通过')
      await initShops();
    } else
      ElMessage.error('审核失败1')
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

async function rejectShop(index) {
  try {
    const response = await axios.post('/api/admin/updateReviewLicenseStatus', {
      id: shops.value[index].licenseInfo.id,
      status: "未通过"
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      await initShops();
      ElMessage.success('退回申请成功')
    } else
      ElMessage.error('退回申请失败')
  } catch (error) {
    ElMessage.error('退回申请失败')
  }
}

onMounted(async () => {
  await checkUserPermission();
  await initShops();
});
</script>

<style scoped lang="less">
td {
  padding: 10px;
  border: 1px solid #ccc;
  font-size: 14px;
  font-weight: bold;
}
</style>