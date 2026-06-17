import request from './request';

// 用户认证 API
export function login(data) {
    return request.post('/api/user/login', data)
}

export function register(data) {
    return request.post('/api/user/register', data)
}

export function forgetPassword(data) {
    return request.post('/api/user/forgetPassword', data)
}

export function getUserBaseInfo(params) {
    return request.post('/api/user/getUserBaseInfo', params)
}

export function findUserById(params) {
    return request.post('/api/user/findUserById', params)
}

export function getUserOtherInfo(params) {
    return request.post('/api/user/getUserOtherInfo', params)
}

export function updateUserOtherInfo(data) {
    return request.post('/api/user/updateUserOtherInfo', data)
}

export function updateUserPhone(data) {
    return request.post('/api/user/updateUserPhone', data)
}

export function updateUserEmail(data) {
    return request.post('/api/user/updateUserEmail', data)
}

export function bindAccount(data) {
    return request.post('/api/user/bindAccount', data)
}

export function unbindAccount(data) {
    return request.post('/api/user/unbindAccount', data)
}

export function uploadAvatar(formData) {
    return request.post('/api/user/upload-avatar', formData, {
        headers: {'Content-Type': 'multipart/form-data'}
    })
}

export function selectPhone(params) {
    return request.post('/api/user/register/selectPhone', params)
}

// 短信验证码
export function sendSmsCode(data) {
    return request.post('/api/sms/smsXxs', data)
}

export function validateSmsCode(data) {
    return request.post('/api/sms/validateNum', data)
}

// 邮箱验证码
export function sendEmailCode(data) {
    return request.post('/api/email/send', data)
}

export function validateEmailCode(data) {
    return request.post('/api/email/validateNum', data)
}

// 第三方登录回调
export function thirdLoginGetUserBaseInfo(data) {
    return request.post('/api/callback/ThirdLogin/getUserBaseInfo', data)
}

export function thirdLoginUserInfo(data) {
    return request.post('/api/callback/ThirdLogin/userInfo', data)
}

export function giteeAuth(data) {
    return request.post('/api/callback/gitee-auth', data)
}

export function githubAuth(data) {
    return request.post('/api/callback/github-auth', data)
}

export function googleAuth(data) {
    return request.post('/api/callback/google-auth', data)
}
