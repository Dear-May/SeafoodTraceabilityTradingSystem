// 管理后台路由
export default [
    {
        path: '/admin/index',
        name: 'AdminIndexView',
        component: () => import('@/views/Admin/AdminIndexView.vue'),
        meta: {
            title: '海潮 - 后台管理'
        }
    },
    {
        path: '/admin/goodSync',
        name: 'AdminSyncView',
        component: () => import('@/views/Admin/AdminSyncView.vue'),
        meta: {
            title: '海潮 - 数据同步'
        }
    }
];
