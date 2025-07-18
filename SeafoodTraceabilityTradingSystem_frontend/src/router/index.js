// 引入Vue3以及新的vue-router
import {createRouter, createWebHistory} from 'vue-router';
import Login from '@/views/LoginView.vue';
import Index from "@/views/IndexView.vue";
import ForgetPasswordView from "@/views/ForgetPasswordView.vue";
import RegisterView from "@/views/RegisterView.vue";
import TestView from "@/views/TestView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import AccessLoginView from "@/views/AccessLoginView.vue";
import AccessResult from "@/components/AccessResultComponent.vue";
import MySpaceView from "@/views/MySpaceView.vue";
import AccountInformationView from "@/views/AccountInformationView.vue";
import AccountProfileView from "@/views/AccountProfileView.vue";
import AddressView from "@/views/AddressView.vue";
import TalkToStoreView from "@/views/TalkToStoreView.vue";
import GoodInfoView from "@/views/GoodInfoView.vue";
import FootMarkView from "@/views/FootMarkView.vue";
import MyFavoriteView from "@/views/MyFavoriteView.vue";
import BucketView from "@/views/BucketView.vue";
import PayGoodsView from "@/views/PayGoodsView.vue";
import submitOrderView from "@/views/SubmitOrderView.vue";
import MyOrderView from "@/views/MyOrderView.vue";
import SearchView from "@/views/SearchView.vue";
import MyCommentView from "@/views/MyCommentView.vue";
import ShopView from "@/views/ShopView.vue";
import StoreLoginView from "@/views/Store/StoreLoginView.vue";
import StoreRegisterView from "@/views/Store/StoreRegisterView.vue";
import StoreIndexView from "@/views/Store/StoreIndexView.vue";
import StoreGoodView from "@/views/Store/StoreGoodView.vue";
import StoreOrderListView from "@/views/Store/StoreOrderListView.vue";
import StoreOrderReturnsView from "@/views/Store/StoreOrderReturnsView.vue";
import StoreStaffMangerView from "@/views/Store/StoreStaffMangerView.vue";
import StoreTraceabilityManageView from "@/views/Store/StoreTraceabilityManageView.vue";
import StoreTalkView from "@/views/Store/StoreTalkView.vue";
import AdminIndexView from "@/views/Admin/AdminIndexView.vue";
import AdminSyncView from "@/views/Admin/AdminSyncView.vue";
import SearchByImageView from "@/views/SearchByImageView.vue";
import StoreLiveView from "@/views/Store/StoreLiveView.vue";
import LiveView from "@/views/LiveView.vue";

// 定义路由
const routes = [
    {
        path: '/',
        component: Index,
        meta: {
            title: "海潮 - 海鲜食品溯源交易平台"
        }
    },
    {
        path: '/login',
        name: 'LoginView',
        component: Login,
        meta: {
            title: "登录 - 海潮",
            keywords: "海鲜",
            description: "海鲜"
        }
    },
    {
        path: '/index',
        name: 'IndexView',
        component: Index,
        meta: {
            title: "海潮 - 海鲜食品溯源交易平台"
        }
    },
    {
        path: '/register',
        name: 'RegisterView',
        component: () => RegisterView,
        meta: {
            title: "海潮 - 注册新用户"
        }
    },
    {
        path: '/forgotPassword',
        name: 'ForgotPasswordView',
        component: () => ForgetPasswordView,
        meta: {
            title: "海潮 - 忘记密码"
        }
    },
    {
        path: '/accessLogin',
        name: 'AccessLoginView',
        component: () => AccessLoginView,
        meta: {
            title: "海潮 - 访问登录页面"
        }
    },
    {
        path: '/accessResult',
        name: 'AccessResultView',
        component: () => AccessResult,
    },
    {
        path: '/search',
        name: 'SearchView',
        component: () => SearchView,
        meta: {
            title: "海潮 - 搜索页面"
        }
    },
    {
        path: '/searchByImage',
        name: 'SearchByImageView',
        component: () => SearchByImageView,
        meta: {
            title: "海潮 - 搜索页面"
        }
    },
    {
        path: '/mySpace',
        name: 'MySpaceView',
        component: () => MySpaceView,
        meta: {
            title: "我的海潮"
        }
    },
    {
        path: '/mySpace/BucketList',
        name: 'BucketView',
        component: () => BucketView,
        meta: {
            title: "我的购物车 - 海潮"
        }
    },
    {
        path: '/mySpace/myOrder',
        name: 'MyOrderView',
        component: () => MyOrderView,
        meta: {
            title: "我的订单 - 海潮"
        }
    },
    {
        path: '/mySpace/myFavorite',
        name: MyFavoriteView,
        component: () => MyFavoriteView,
        meta: {
            title: "我的收藏 - 海潮"
        }
    },
    {
        path: '/mySpace/footMark',
        name: FootMarkView,
        component: () => FootMarkView,
        meta: {
            title: "我的足迹 - 海潮"
        }
    },
    {
        path: '/profile/myComment',
        name: 'MyCommentView',
        component: () => MyCommentView,
        meta: {
            title: "我的评价 - 海潮"
        }
    },
    {
        path: '/profile/account_information',
        name: 'AccountInformationView',
        component: () => AccountInformationView,
        meta: {
            title: "账号管理 - 海潮"
        }
    },
    {
        path: '/profile/address',
        name: 'AddressView',
        component: AddressView,
        meta: {
            title: "收货地址管理 - 海潮"
        }
    },
    {
        path: '/profile/account_profile',
        name: 'AccountProfileView',
        component: () => AccountProfileView,
        meta: {
            title: "个性设置 - 海潮"
        }
    },
    {
        path: '/shop',
        name: 'ShopView',
        component: () => ShopView,
        meta: {
            title: "商城"
        }
    },
    {
        path: '/talkToStore',
        name: TalkToStoreView,
        component: () => TalkToStoreView,
        meta: {
            title: "联系商家"
        }

    },
    {
        path: '/goodDetail',
        name: 'GoodInfoView',
        component: () => GoodInfoView,
        meta: {
            title: "商品详情"
        }
    },
    {
        path: '/payGoods/ready',
        name: 'PayGoodsView',
        component: () => PayGoodsView,
        meta: {
            title: "提交订单"
        }
    },
    {
        path: '/payGoods/submitOrder',
        name: 'submitOrderView',
        component: () => submitOrderView,
        meta: {
            title: "支付订单"
        }
    },
    {
        path: '/live/show',
        name: 'LiveView',
        component: () => LiveView,
        meta: {
            title: "直播间"
        }
    },
    {
        path: '/store/login',
        name: 'StoreLoginView',
        component: () => StoreLoginView,
        meta: {
            title: "海潮 - 商家登录"
        }
    },
    {
        path: '/store/register',
        name: 'StoreRegisterView',
        component: () => StoreRegisterView,
        meta: {
            title: "海潮 - 商家注册"
        }
    },
    {
        path: '/store/index',
        name: 'StoreIndexView',
        component: () => StoreIndexView,
        meta: {
            title: "海潮 - 商家首页"
        }
    },
    {
        path: '/store/mangerGood',
        name: "StoreGoodView",
        component: () => StoreGoodView,
        meta: {
            title: "海潮 - 商品管理"
        }
    },
    {
        path: '/store/traceabilityManage',
        name: 'StoreTraceabilityManageView',
        component: () => StoreTraceabilityManageView,
        meta: {
            title: "海潮 - 追溯管理"
        }
    },
    {
        path: '/store/orderList',
        name: 'StoreOrderListView',
        component: () => StoreOrderListView,
        meta: {
            title: "海潮 - 订单管理"
        }
    },
    {
        path: '/store/orderReturns',
        name: 'StoreOrderReturnsView',
        component: () => StoreOrderReturnsView,
        meta: {
            title: "海潮 - 退换货管理"
        }
    },
    {
        path: '/store/staffManage',
        name: 'StaffManageView',
        component: () => StoreStaffMangerView,
        meta: {
            title: "海潮 - 员工管理"
        }
    },
    {
        path: '/store/talk',
        name: 'StoreTalkView',
        component: () => StoreTalkView,
        meta: {
            title: "海潮 - 商家聊天"
        }
    },
    {
        path: '/store/live',
        name: 'test1View',
        component: () => StoreLiveView,
        meta: {
            title: "海潮 - 商家直播间"
        }
    },
    {
        path: '/admin/index',
        name: 'AdminIndexView',
        component: () => AdminIndexView,
        meta: {
            title: "海潮 - 后台管理"
        }
    },
    {
        path: '/admin/goodSync',
        name: 'AdminSyncView',
        component: () => AdminSyncView,
        meta: {
            title: "海潮 - 数据同步"
        }
    },
    {
        path: '/test',
        name: 'TestView',
        component: () => TestView,
        meta: {
            title: "测试页面"
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFoundView',
        component: () => NotFoundView,
        meta: {
            title: "页面丢失了！"
        }
    }

];

// 创建路由器实例
const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 导出全局注册
export default router;
