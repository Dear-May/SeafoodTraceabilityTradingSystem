// 路由守卫
export function setupGuards(router) {
    router.beforeEach((to, from, next) => {
        // 设置页面标题
        document.title = to.meta.title || '默认标题'

        // Auth 检查：需要登录的路由
        const requiresAuth = to.meta.requiresAuth
        if (requiresAuth) {
            const userBaseInfo = sessionStorage.getItem('userBaseInfo')
            if (!userBaseInfo) {
                // 未登录，跳转到登录页
                return next({ path: '/login', query: { redirect: to.fullPath } })
            }
        }

        next()
    })
}
