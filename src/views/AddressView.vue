<template>
  <div class="account-management-page">
    <HeaderComponent/>
    <div class="container-fluid">
      <div class="row">
        <div class="d-flex flex-column flex-shrink-0 p-3 bg-body-tertiary sidebar"
             style="width: 280px; height: 75vh;">
          <AvatarChangComponent/>
          <hr>
          <el-menu
              active-text-color="#e02020"
              background-color="#d3d3d3"
              class="el-menu-vertical-demo"
              default-active="2"
              text-color="#303133"
              @open="handleOpen"
              @close="handleClose"
              style="border-radius: 10px;"
          >
            <el-sub-menu index="1">
              <template #title>
                <el-icon>
                  <user/>
                </el-icon>
                <span style="font-weight: bold; font-size: 18px;">账号管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="1-1" style="font-size: 16px; font-weight: bold;" @click="handleAccountInfo">
                  安全设置
                </el-menu-item>
                <el-menu-item index="1-2" style="font-size: 16px; font-weight: bold;" @click="handlePersonalSetting">
                  个性设置
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="2">
              <el-icon>
                <Location/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;">售后地址管理</span>
            </el-menu-item>
            <el-menu-item index="3">
              <el-icon>
                <document/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;">帮助中心</span>
            </el-menu-item>
            <el-menu-item index="4">
              <el-icon>
                <setting/>
              </el-icon>
              <span style="font-weight: bold; font-size: 18px;" @click="handleForgetPassword">忘记密码</span>
            </el-menu-item>
          </el-menu>
          <template>
            <footer>
              <hr>
            </footer>
          </template>
        </div>
        <div class="col-9 main-content">
          <div class="row">
            <div class="col-10">
              <h4 style="text-align: left; font-weight: bold;">我的收货地址</h4>
              <p style="text-align: left;" class="mt-2">常用地址</p>
            </div>
            <div class="col-2">
              <AddAddressComponent/>
            </div>
          </div>
          <div class="binding-table">
            <el-table :data="addressList" style="width: 100%" class="m-3">
              <el-table-column prop="receiver" label="收货人"></el-table-column>
              <el-table-column prop="phone" label="电话/手机"></el-table-column>
              <el-table-column prop="region" label="所在地区" width="200"></el-table-column>
              <el-table-column prop="address" label="详细地址"></el-table-column>
              <el-table-column prop="postalCode" label="邮编"></el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="scope">
                  <el-button type="text" size="small" @click="updateAddressBaseInfo(scope.row)">修改</el-button>
                  <el-button type="text" size="small" @click="deleteAddress(scope.row)">删除</el-button>
                </template>
              </el-table-column>
              <el-table-column label="移动设置" width="150">
                <template #default="scope">
                  <el-button v-if="scope.row.isDefault" type="primary" size="small" disabled>默认地址</el-button>
                  <el-button v-else type="text" size="small" @click="setDefault(scope.row)">设为默认</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-dialog v-model="showUpdateAddressDialog" title="添加收货地址" width="700px" align-center
                     style="border-radius: 20px;">
            <div class="address-form">
              <div class="row">
                <label for="details" class="form-label col-5 mt-2" style="align-items: center;">
                  <a style="color: #e02020;">*&nbsp;&nbsp;</a> 详细地址：</label>
                <el-cascader class="col-7" v-model="selectedAddress" :options="addressOptions"
                             placeholder="请选择省/市/区/街道"
                             style="width: 55%; margin-bottom: 20px;"/>
              </div>
              <div class="form-group">
                <div class="row">
                  <label for="details" class="form-label col-5 mt-3">
                    <a style="color: #e02020;">*&nbsp;&nbsp;</a>
                    详细地址：</label>
                  <el-input v-model="detailedAddress" type="textarea" class="col-8"
                            placeholder="请输入详细地址，详细地址长度需要在2-120个汉字或字符，不能包含特殊字符"
                            maxlength="120"
                            show-word-limit style="width: 55%;"/>
                </div>
                <span class="error-message"
                      v-if="detailedAddressError">详细地址长度需要在2-120个汉字或字符，不能包含特殊字符</span>
              </div>
              <div class="form-group">
                <div class="row">
                  <label for="consignee" class="form-label col-5 mt-3">
                    <a style="color: #e02020;">*&nbsp;&nbsp;</a>
                    收货人姓名：</label>
                  <el-input v-model="consignee" placeholder="长度不超过25个字符，不能包含特殊字符" maxlength="25"
                            style="width: 55%;" class="col-7"/>
                </div>
                <span class="error-message"
                      v-if="consigneeError">收货人姓名长度需要在1-25个汉字或字符之间，不能包含特殊字符</span>
              </div>
              <div class="form-group">
                <div class="row">
                  <label for="phone" class="form-label col-5 mt-3">
                    <a style="color: #e02020;">*&nbsp;&nbsp;</a>
                    手机号：</label>
                  <el-input v-model="phoneNumber" placeholder="手机号必须填写一项，手机号码不能包含空格"
                            maxlength="11"
                            style="width: 50%;" class="col-7"/>
                </div>
                <span class="error-message" v-if="phoneNumberError">手机号码不能为空</span>
              </div>
              <el-checkbox v-model="setAsDefault" style="margin-top: 20px;">设置为默认收货地址</el-checkbox>
            </div>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="clearForm_update" class="cancel-button">取消</el-button>
                <el-button type="primary" @click="saveAddress" class="confirm-button">确认</el-button>
              </div>
            </template>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
  <RightWidgetComponent/>
  <FooterComponent/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import router from "@/router";
import axios from "axios";
import AddAddressComponent from "@/components/AddAddressComponent.vue"
import {Document, Location, Setting, User} from "@element-plus/icons-vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import AvatarChangComponent from "@/components/AvatarChangComponent.vue";
import RightWidgetComponent from "@/components/RightWidgetComponent.vue";
import {ElMessage} from "element-plus";
import provincesData from "@/assets/province.json";
import citiesData from "@/assets/city.json";
import areasData from "@/assets/area.json";
import streetsData from "@/assets/street.json";
import useUser from "@/composables/useUser";

const {UserInfoForm, initUserSession} = useUser()

const addressList = ref([]);

async function getAddressList() {
  try {
    const response = await axios.post('/api/addresses/getAllAddresses', {
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data) {
      addressList.value = response.data.map(address => ({
        id: address.id,
        receiver: address.consignee,
        phone: address.phone,
        region: address.area,
        address: address.detailed_address,
        postalCode: '000000',
        isDefault: address.status === 'default'
      }));
    } else {
      ElMessage.error('获取地址列表失败');
    }
  } catch (err) {
    ElMessage.error('获取地址列表失败');
  }
}

const showUpdateAddressDialog = ref(false);
const selectedAddress = ref([]);
const detailedAddress = ref('');
const consignee = ref('');
const phoneNumber = ref('');
const setAsDefault = ref(false);
const updateid = ref('');

const detailedAddressError = ref(false);
const consigneeError = ref(false);
const phoneNumberError = ref(false);

const addressOptions = ref([]);

async function getAddressOptions(region) {
  const lastAddress = ref();
  let isCity = false;
  try {
    const response = await axios.post('/api/addresses/addressToId', {
      address: region
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data) {
      try {
        lastAddress.value = response.data.options[0].children[0].children[0].children[0];
      } catch (e) {
        lastAddress.value = response.data.options[0].children[0].children[0];
        isCity = true;
      }
      if (!isCity)
        selectedAddress.value = [response.data.options[0].value, response.data.options[0].children[0].value, response.data.options[0].children[0].children[0].value, lastAddress.value.value];
      else
        selectedAddress.value = [response.data.options[0].value, response.data.options[0].children[0].value, lastAddress.value.value];
    } else {
      ElMessage.error('获取地址失败');
    }
  } catch (err) {
    console.log(err);
  }
}

function updateAddressBaseInfo(row) {
  selectedAddress.value = row.region;
  detailedAddress.value = row.address;
  consignee.value = row.receiver;
  phoneNumber.value = row.phone;
  setAsDefault.value = row.isDefault;
  getAddressOptions(row.region);
  updateid.value = row.id;
  showUpdateAddressDialog.value = true;
}

const loadAddressOptions = () => {
  addressOptions.value = provincesData.map(province => ({
    value: province.id,
    label: province.name,
    children: (citiesData[province.id] || []).map(city => ({
      value: city.id,
      label: city.name,
      children: (areasData[city.id] || []).map(area => ({
        value: area.id,
        label: area.name,
        children: (streetsData[area.id] || []).map(street => ({
          value: street.id,
          label: street.name,
        }))
      }))
    }))
  }));
};

async function saveAddress() {
  detailedAddressError.value = !detailedAddress.value;
  consigneeError.value = !consignee.value;
  phoneNumberError.value = !phoneNumber.value;

  if (detailedAddressError.value || consigneeError.value || phoneNumberError.value || !selectedAddress.value.length) {
    return;
  }
  try {
    const response = await axios.post('/api/addresses/updateAddress', {
      userId: UserInfoForm.value.id,
      addressId: updateid.value,
      consignee: consignee.value,
      phone: phoneNumber.value,
      area: selectedAddress.value.join(' '),
      detailed_address: detailedAddress.value,
      is_default: setAsDefault.value,
    }, {
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true,
    })
    if (response.status === 200) {
      ElMessage.success("添加地址成功");
      setTimeout(() => {
        window.location.reload();
      }, 500);
    } else {
      ElMessage.error("添加地址失败");
    }
  } catch (e) {
    ElMessage.error("添加地址失败");
  } finally {
    showUpdateAddressDialog.value = false;
  }
}

function clearForm_update() {
  selectedAddress.value = [];
  detailedAddress.value = '';
  consignee.value = '';
  phoneNumber.value = '';
  setAsDefault.value = false;
  detailedAddressError.value = false;
  consigneeError.value = false;
  phoneNumberError.value = false;
  updateid.value = "";
  showUpdateAddressDialog.value = false;
}

async function deleteAddress(row) {
  try {
    const response = await axios.post('/api/addresses/deleteAddress', {
      addressId: row.id,
      userId: UserInfoForm.value.id,
    }, {
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    });
    if (response.data.code === 200) {
      ElMessage.success('删除地址成功');
      setTimeout(() => {
        window.location.reload();
      }, 500);
    } else {
      ElMessage.error('删除地址失败');
    }
  } catch (err) {
    ElMessage.error('删除地址失败');
  }
}

async function setDefault(row) {
  try {
    const response = await axios.post('/api/addresses/updateDefault', {
          userId: UserInfoForm.value.id,
          addressId: row.id,
        }, {
          headers: {
            'Content-Type': 'application/json;charset=UTF-8'
          },
          withCredentials: true
        }
    );
    if (response.data.code === 200) {
      ElMessage.success('设置默认地址成功');
      addressList.value.forEach(address => address.isDefault = false);
      row.isDefault = true;
    } else {
      ElMessage.error('设置默认地址失败');
    }
  } catch (err) {
    ElMessage.error('设置默认地址失败');
  }
}

function handleAccountInfo() {
  setTimeout(() => {
    router.push('/profile/account_information');
  }, 500);
}

function handlePersonalSetting() {
  setTimeout(() => {
    router.push('/profile/account_profile');
  }, 500);
}

function handleForgetPassword() {
  setTimeout(() => {
    router.replace('/forgotPassword');
  }, 500);
}

onMounted(async () => {
  initUserSession()
  await getAddressList();
  loadAddressOptions();
})
</script>

<style lang="less" scoped>
.account-management-page {
  font-family: Arial, sans-serif;
  display: flex;
  padding: 20px;
}

.sidebar {
  margin-top: 90px;
  background-color: #ffffff;
  padding: 20px;
  border-right: 1px solid #eaeaea;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  height: 70%;
}

.nav {
  list-style: none;
  padding: 0;
}

.nav-item {
  margin-bottom: 15px;
}

.nav-link {
  font-weight: bold;
  color: #e02020;
  text-decoration: none;
}

.nav-link.active {
  background-color: #e02020;
  color: white !important;
}

.sub-nav {
  list-style: none;
  padding-left: 20px;
}

.sub-nav li {
  margin-bottom: 5px;
}

.main-content {
  margin-top: 90px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.binding-table {
  margin-top: 20px;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
}

.error-message {
  color: #e02020;
  font-size: 13px;
  margin-top: 5px;
}

.cancel-button {
  color: #e02020;
  font-weight: bold;
}

.confirm-button {
  background-color: #e02020;
  border-color: #e02020;
  font-weight: bold;
}

.el-button[type="primary"] {
  background-color: #e02020;
  border-color: #e02020;
}

.night-mode {
  background-color: #2e2e2e;
  color: #e0e0e0;
}

.night-mode .account-management-page {
  background-color: #2e2e2e;
}

.night-mode .row {
  background-color: #2e2e2e;
  color: #e0e0e0;
}

.night-mode .body {
  background-color: #2e2e2e;
}

.night-mode .header {
  background-color: #3c3c3c;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.night-mode .sidebar {
  background-color: #3c3c3c;
  color: #e0e0e0;
  border-right: 1px solid #444;
}

.night-mode .sidebar .el-menu-item {
  background-color: #3c3c3c;
  color: #e0e0e0;
}

.night-mode .sidebar.el-menu-item:hover {
  background-color: #4c4c4c;
}

.night-mode .main-content,
.night-mode .profile-section,
.night-mode .order-section,
.night-mode .logistics-section,
.night-mode .logistics-item,
.night-mode .profile-card,
.night-mode .promotion-card {
  background-color: #3c3c3c;
  color: #e0e0e0;
}

.night-mode .order-icon,
.night-mode .logistics-action {
  color: #ffa500;
}

.night-mode .right-widget {
  background-color: #3c3c3c;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.5);
}

.night-mode .footer {
  background-color: #2e2e2e;
  border-top: 1px solid #333;
}

.night-mode .footer-links a {
  color: #ffffff;
}

.night-mode .footer-links a:hover {
  color: #e02020;
}
</style>