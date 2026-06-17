import request from './request';

// 店铺相关 API
export function findShopById(params) {
    return request.post('/api/shop/findShopById', params)
}

export function getCardInfo(params) {
    return request.post('/api/shop/info/getCardInfo', params)
}

export function getUserInfo(params) {
    return request.post('/api/shop/info/getUserInfo', params)
}

// 员工管理
export function getShopStaffAttendance(params) {
    return request.post('/api/shop/staff/attendance/getShopStaffAttendance', params)
}

export function signIn(data) {
    return request.post('/api/shop/staff/attendance/signIn', data)
}

export function getShopStaffInfo(params) {
    return request.post('/api/shop/staff/getShopStaffInfo', params)
}

export function getAuditShopStaffInfo(params) {
    return request.post('/api/shop/staff/getAuditShopStaffInfo', params)
}

export function dismissStaff(data) {
    return request.post('/api/shop/staff/dismissStaff', data)
}

export function addStaff(data) {
    return request.post('/api/shop/staff/addStaff', data)
}

// 店铺用户认证
export function shopLogin(data) {
    return request.post('/api/shop/user/login', data)
}

export function shopRegister(data) {
    return request.post('/api/shop/user/register', data)
}

export function getShopUser(params) {
    return request.post('/api/shop/user/getShopUser', params)
}

export function isAccessShop(params) {
    return request.post('/api/shop/user/isAccessShop', params)
}

// 店铺入驻
export function submitLicense(data) {
    return request.post('/api/shop/submitLicense', data)
}

export function submitShopInfo(data) {
    return request.post('/api/shop/submitShopInfo', data)
}

export function ocr(data) {
    return request.post('/api/shop/ocr', data)
}

// 商家聊天
export function getTalkList(params) {
    return request.post('/api/shop/chat/getTalkList', params)
}

export function getTalkDetail(params) {
    return request.post('/api/shop/chat/getTalkDetail', params)
}

export function deleteChatSession(data) {
    return request.post('/api/shop/chat/deleteChatSession', data)
}

export function uploadChatImage(formData) {
    return request.post('/api/shop/chat/uploadImage', formData, {
        headers: {'Content-Type': 'multipart/form-data'}
    })
}

export function sendMessage(data) {
    return request.post('/api/shop/chat/sendMessage', data)
}
