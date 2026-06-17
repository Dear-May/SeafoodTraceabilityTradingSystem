import {defineStore} from 'pinia';
import {login as apiLogin, getUserBaseInfo} from '@/api/user';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: {
            id: '',
            username: '',
            avatar: '',
            nickname: '',
            token: ''
        },
        isLoggedIn: false
    }),

    getters: {
        userId: (state) => state.user.id,
        userName: (state) => state.user.username,
        userAvatar: (state) => state.user.avatar,
        userNickname: (state) => state.user.nickname,
        token: (state) => state.user.token
    },

    actions: {
        // 从 sessionStorage 恢复用户状态
        initFromSession() {
            const stored = sessionStorage.getItem('userBaseInfo')
            if (stored) {
                try {
                    const parsed = JSON.parse(stored)
                    this.user = {
                        id: parsed.id || '',
                        username: parsed.username || '',
                        avatar: parsed.avatar || '',
                        nickname: parsed.nickname || '',
                        token: parsed.token || ''
                    }
                    this.isLoggedIn = true
                } catch {
                    this.clearUser()
                }
            }
        },

        // 登录成功后保存用户信息
        setUser(userData) {
            this.user = {
                id: userData.id || '',
                username: userData.username || '',
                avatar: userData.avatar || '',
                nickname: userData.nickname || '',
                token: userData.token || ''
            }
            this.isLoggedIn = true
            sessionStorage.setItem('userBaseInfo', JSON.stringify(this.user))
        },

        // 更新用户信息
        updateUserInfo(partial) {
            Object.assign(this.user, partial)
            sessionStorage.setItem('userBaseInfo', JSON.stringify(this.user))
        },

        // 清除用户状态（登出）
        clearUser() {
            this.user = {id: '', username: '', avatar: '', nickname: '', token: ''}
            this.isLoggedIn = false
            sessionStorage.removeItem('userBaseInfo')
        },

        // 登录并保存状态
        async login(credentials) {
            const response = await apiLogin(credentials)
            if (response.data) {
                this.setUser(response.data)
            }
            return response
        },

        // 登出
        logout() {
            this.clearUser()
        }
    }
})
