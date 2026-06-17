// 商家路由
export default [
    {
        path: '/store/login',
        name: 'StoreLoginView',
        component: () => import('@/views/Store/StoreLoginView.vue'),
        meta: {
            title: '海潮 - 商家登录'
        }
    },
    {
        path: '/store/register',
        name: 'StoreRegisterView',
        component: () => import('@/views/Store/StoreRegisterView.vue'),
        meta: {
            title: '海潮 - 商家注册'
        }
    },
    {
        path: '/store/index',
        name: 'StoreIndexView',
        component: () => import('@/views/Store/StoreIndexView.vue'),
        meta: {
            title: '海潮 - 商家首页'
        }
    },
    {
        path: '/store/mangerGood',
        name: 'StoreGoodView',
        component: () => import('@/views/Store/StoreGoodView.vue'),
        meta: {
            title: '海潮 - 商品管理'
        }
    },
    {
        path: '/store/traceabilityManage',
        name: 'StoreTraceabilityManageView',
        component: () => import('@/views/Store/StoreTraceabilityManageView.vue'),
        meta: {
            title: '海潮 - 追溯管理'
        }
    },
    {
        path: '/store/orderList',
        name: 'StoreOrderListView',
        component: () => import('@/views/Store/StoreOrderListView.vue'),
        meta: {
            title: '海潮 - 订单管理'
        }
    },
    {
        path: '/store/orderReturns',
        name: 'StoreOrderReturnsView',
        component: () => import('@/views/Store/StoreOrderReturnsView.vue'),
        meta: {
            title: '海潮 - 退换货管理'
        }
    },
    {
        path: '/store/staffManage',
        name: 'StaffManageView',
        component: () => import('@/views/Store/StoreStaffMangerView.vue'),
        meta: {
            title: '海潮 - 员工管理'
        }
    },
    {
        path: '/store/talk',
        name: 'StoreTalkView',
        component: () => import('@/views/Store/StoreTalkView.vue'),
        meta: {
            title: '海潮 - 商家聊天'
        }
    },
    {
        path: '/store/live',
        name: 'StoreLiveView',
        component: () => import('@/views/Store/StoreLiveView.vue'),
        meta: {
            title: '海潮 - 商家直播间'
        }
    }
];
