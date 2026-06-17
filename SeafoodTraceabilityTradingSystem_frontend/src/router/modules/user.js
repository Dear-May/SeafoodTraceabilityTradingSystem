// 用户路由（需要登录）
const routes = [
    {
        path: '/mySpace',
        name: 'MySpaceView',
        component: () => import('@/views/user/MySpaceView.vue'),
        meta: {
            title: "我的海潮",
            requiresAuth: true
        }
    },
    {
        path: '/mySpace/BucketList',
        name: 'BucketView',
        component: () => import('@/views/user/BucketView.vue'),
        meta: {
            title: "我的购物车 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/mySpace/myOrder',
        name: 'MyOrderView',
        component: () => import('@/views/user/MyOrderView.vue'),
        meta: {
            title: "我的订单 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/mySpace/myFavorite',
        name: 'MyFavoriteView',
        component: () => import('@/views/user/MyFavoriteView.vue'),
        meta: {
            title: "我的收藏 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/mySpace/footMark',
        name: 'FootMarkView',
        component: () => import('@/views/user/FootMarkView.vue'),
        meta: {
            title: "我的足迹 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/mySpace/myComment',
        name: 'MyCommentView',
        component: () => import('@/views/user/MyCommentView.vue'),
        meta: {
            title: "我的评价 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/profile/account_info',
        name: 'AccountInformationView',
        component: () => import('@/views/user/AccountInformationView.vue'),
        meta: {
            title: "账号管理 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/profile/account_profile',
        name: 'AccountProfileView',
        component: () => import('@/views/user/AccountProfileView.vue'),
        meta: {
            title: "个性设置 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/profile/address',
        name: 'AddressView',
        component: () => import('@/views/user/AddressView.vue'),
        meta: {
            title: "收货地址管理 - 海潮",
            requiresAuth: true
        }
    },
    {
        path: '/payGoods/ready',
        name: 'PayGoodsView',
        component: () => import('@/views/user/PayGoodsView.vue'),
        meta: {
            title: "提交订单",
            requiresAuth: true
        }
    },
    {
        path: '/payGoods/submitOrder',
        name: 'submitOrderView',
        component: () => import('@/views/user/SubmitOrderView.vue'),
        meta: {
            title: "支付订单",
            requiresAuth: true
        }
    },
    {
        path: '/talkToStore',
        name: 'TalkToStoreView',
        component: () => import('@/views/user/TalkToStoreView.vue'),
        meta: {
            title: "联系商家",
            requiresAuth: true
        }
    },
    {
        path: '/live/show',
        name: 'LiveView',
        component: () => import('@/views/user/LiveView.vue'),
        meta: {
            title: "直播间",
            requiresAuth: true
        }
    },
    {
        path: '/goodDetail',
        name: 'GoodInfoView',
        component: () => import('@/views/goods/GoodInfoView.vue'),
        meta: {
            title: "商品详情"
        }
    },
    {
        path: '/shop',
        name: 'ShopView',
        component: () => import('@/views/goods/ShopView.vue'),
        meta: {
            title: "商城"
        }
    },
    {
        path: '/accessLogin',
        name: 'AccessLoginView',
        component: () => import('@/views/auth/AccessLoginView.vue'),
        meta: {
            title: "海潮 - 访问登录页面"
        }
    },
    {
        path: '/accessResult',
        name: 'AccessResultView',
        component: () => import('@/components/auth/AccessResultComponent.vue'),
    }
];

export default routes;
