<template>
  <el-button type="primary" style="background-color: #e02020; border-color: #e02020; margin-bottom: 20px;"
             @click="showAddAddressDialog = true">
    添加收货地址
  </el-button>

  <el-dialog v-model="showAddAddressDialog" title="添加收货地址" width="700px" align-center
             style="border-radius: 20px;">
    <div class="address-form">
      <div class="row">
        <label for="details" class="form-label col-5 mt-2" style="align-items: center;">
          <a style="color: #e02020;">*&nbsp;&nbsp;</a> 详细地址：</label>
        <el-cascader class="col-7" v-model="selectedAddress" :options="addressOptions" placeholder="请选择省/市/区/街道"
                     style="width: 55%; margin-bottom: 20px;" @change="handleAddressChange"/>
      </div>
      <div class="form-group">
        <div class="row">
          <label for="details" class="form-label col-5 mt-3">
            <a style="color: #e02020;">*&nbsp;&nbsp;</a>
            详细地址：</label>
          <el-input v-model="detailedAddress" type="textarea" class="col-8"
                    placeholder="请输入详细地址，详细地址长度需要在2-120个汉字或字符，不能包含特殊字符" maxlength="120"
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
          <el-input v-model="phoneNumber" placeholder="手机号必须填写一项，手机号码不能包含空格" maxlength="11"
                    style="width: 50%;" class="col-7"/>
        </div>
        <span class="error-message" v-if="phoneNumberError">手机号码不能为空</span>
      </div>
      <el-checkbox v-model="setAsDefault" style="margin-top: 20px;">设置为默认收货地址</el-checkbox>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="clearForm" class="cancel-button">取消</el-button>
        <el-button type="primary" @click="saveAddress" class="confirm-button">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import provincesData from '@/assets/province.json';
import citiesData from '@/assets/city.json';
import areasData from '@/assets/area.json';
import streetsData from '@/assets/street.json';
import {ElMessage} from "element-plus";
import axios from "axios";
import useUser from "@/composables/useUser";

const showAddAddressDialog = ref(false);
const selectedAddress = ref([]);
const detailedAddress = ref('');
const consignee = ref('');
const phoneNumber = ref('');
const setAsDefault = ref(false);

const detailedAddressError = ref(false);
const consigneeError = ref(false);
const phoneNumberError = ref(false);

const addressOptions = ref([]);

const {UserInfoForm, initUserSession} = useUser()

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

const handleAddressChange = (value) => {
  console.log('Selected address:', value);
};

async function saveAddress() {
  detailedAddressError.value = !detailedAddress.value;
  consigneeError.value = !consignee.value;
  phoneNumberError.value = !phoneNumber.value;

  if (detailedAddressError.value || consigneeError.value || phoneNumberError.value || !selectedAddress.value.length) {
    return;
  }
  try {
    const response = await axios.post('/api/addresses/addAddress', {
      userId: UserInfoForm.value.id,
      consignee: consignee.value,
      phone: "86-" + phoneNumber.value,
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
    showAddAddressDialog.value = false;
  }
}

function clearForm() {
  detailedAddress.value = '';
  consignee.value = '';
  phoneNumber.value = '';
  setAsDefault.value = false;
  detailedAddressError.value = false;
  consigneeError.value = false;
  phoneNumberError.value = false;
  selectedAddress.value = [];
  setAsDefault.value = false;
  showAddAddressDialog.value = false;
}

onMounted(() => {
  initUserSession()
  loadAddressOptions()
})
</script>

<style scoped>
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
</style>
