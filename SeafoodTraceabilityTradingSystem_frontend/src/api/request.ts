import axios from 'axios';
import {ElMessage} from 'element-plus';

// 创建 axios 实例
const request = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL || '',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    },
    withCredentials: true
});

// 请求拦截器 — 注入 Token
request.interceptors.request.use(
    config => {
        // 从 sessionStorage 读取用户 Token
        const userBaseInfo = sessionStorage.getItem('userBaseInfo')
        if (userBaseInfo) {
            const parsed = JSON.parse(userBaseInfo)
            if (parsed.token) {
                config.headers['Authorization'] = Bearer 
            }
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器 — 统一错误处理
request.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.response) {
            const status = error.response.status
            switch (status) {
                case 401:
                    ElMessage.error('登录已过期，请重新登录')
                    // 清除 session 并跳转
                    sessionStorage.removeItem('userBaseInfo')
                    window.location.href = '/login'
                    break
                case 403:
                    ElMessage.error('没有权限执行此操作')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器内部错误，请稍后再试')
                    break
                default:
                    ElMessage.error(error.response.data?.message || '请求失败')
            }
        } else if (error.request) {
            ElMessage.error('网络异常，请检查网络连接')
        } else {
            ElMessage.error('请求配置错误')
        }
        return Promise.reject(error)
    }
)

export default request
