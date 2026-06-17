// 公开路由
const routes = [
    {
        path: '/',
        name: 'IndexView',
        component: () => import('@/views/public/IndexView.vue'),
        meta: {
            title: "海潮 - 海鲜食品溯源交易平台"
        }
    },
    {
        path: '/login',
        name: 'LoginView',
        component: () => import('@/views/public/LoginView.vue'),
        meta: {
            title: "登录 - 海潮",
            keywords: "海鲜",
            description: "海鲜"
        }
    },
    {
        path: '/index',
        name: 'IndexViewAlias',
        component: () => import('@/views/public/IndexView.vue'),
        meta: {
            title: "海潮 - 海鲜食品溯源交易平台"
        }
    },
    {
        path: '/register',
        name: 'RegisterView',
        component: () => import('@/views/public/RegisterView.vue'),
        meta: {
            title: "海潮 - 注册新用户"
        }
    },
    {
        path: '/forgotPassword',
        name: 'ForgotPasswordView',
        component: () => import('@/views/public/ForgetPasswordView.vue'),
        meta: {
            title: "海潮 - 忘记密码"
        }
    },
    {
        path: '/search',
        name: 'SearchView',
        component: () => import('@/views/public/SearchView.vue'),
        meta: {
            title: "海潮 - 搜索页面"
        }
    },
    {
        path: '/searchByImage',
        name: 'SearchByImageView',
        component: () => import('@/views/public/SearchByImageView.vue'),
        meta: {
            title: "海潮 - 以图搜图"
        }
    },
    {
        path: '/test',
        name: 'TestView',
        component: () => import('@/views/public/TestView.vue'),
        meta: {
            title: "测试页面"
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFoundView',
        component: () => import('@/views/public/NotFoundView.vue'),
        meta: {
            title: "页面丢失了！"
        }
    }
];

export default routes;
