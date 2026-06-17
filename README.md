# 🐟 海潮 - 海鲜溯源交易平台（前端）

> 基于 Vue 3 构建的现代化海鲜电商前端应用，支持商品浏览、溯源查询、图像搜索、直播带货等完整电商功能。

## 📋 项目概览

海潮是一个专注于**海鲜食品溯源交易**的电商平台，前端提供完整的用户端、商家端和管理后台。

| 模块 | 说明 |
|------|------|
| **用户端** | 商品浏览、搜索、购物车、订单、支付、评价、收藏、足迹 |
| **商家端** | 商品管理、订单处理、退换货、员工管理、直播、聊天 |
| **管理后台** | 数据同步、商家审核、权限管理 |
| **特色功能** | 以图搜图、食品溯源溯源链查询、商品直播、天气服务 |

## 🚀 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| [Vue 3](https://vuejs.org/) | ^3.2.13 | 前端框架 (Composition API) |
| [Vue Router 4](https://router.vuejs.org/) | ^4.4.5 | 路由管理 (懒加载 + 导航守卫) |
| [Pinia](https://pinia.vuejs.org/) | ^2.2.6 | 状态管理 |
| [Element Plus](https://element-plus.org/) | ^2.8.5 | UI 组件库 |
| [Axios](https://axios-http.com/) | ^1.7.7 | HTTP 请求 (统一拦截器) |
| [ECharts](https://echarts.apache.org/) | ^5.5.1 | 数据可视化 |
| [Bootstrap 5](https://getbootstrap.com/) | ^5.3.3 | 布局样式 |
| [Vue CLI](https://cli.vuejs.org/) | ~5.0.0 | 项目脚手架 |
| [TypeScript](https://www.typescriptlang.org/) | ^5.0.0 | 类型支持 |

## 📁 项目结构

```
src/
├── api/                          # API 服务层
│   ├── request.ts                # axios 实例 + 拦截器
│   ├── user.js / .ts             # 用户相关 API
│   ├── goods.js / .ts            # 商品相关 API
│   ├── order.js / .ts            # 订单相关 API
│   └── store.js / .ts            # 店铺相关 API
├── assets/                       # 静态资源
│   ├── css/                      # 全局样式
│   │   ├── GoodShowTemp.css
│   │   └── utilities.css         # 工具类样式库
│   ├── images/                   # 图片资源
│   └── *.json                    # 地区/分类数据
├── components/                   # 可复用组件
│   ├── auth/                     # 登录相关组件
│   │   ├── OAuthLoginButton.vue  # 通用第三方登录按钮
│   │   ├── SocialLoginButton.vue
│   │   ├── WechatLoginComponent.vue
│   │   └── AccessResultComponent.vue
│   ├── background/               # 背景特效
│   │   └── BubbleBackground.vue
│   ├── user/                     # 用户相关组件
│   │   ├── AvatarChangComponent.vue
│   │   └── AddAddressComponent.vue
│   ├── OrderCard.vue             # 订单卡片组件
│   ├── ReturnOrderCard.vue       # 退货订单卡片组件
│   ├── WeatherIcon.vue           # 天气图标组件
│   ├── HeaderComponent.vue       # 通用头部导航
│   ├── FooterComponent.vue       # 通用底部
│   ├── SidebarComponent.vue      # 侧边栏
│   ├── RightWidgetComponent.vue  # 右侧浮窗
│   ├── ShopSliderComponent.vue   # 商家侧边菜单
│   ├── StoreHeaderView.vue       # 商家头部
│   ├── OceanWaveLogoComponent.vue
│   ├── DashboardView.vue         # 数据仪表盘
│   └── FooterLoginComponent.vue
├── composables/                  # 组合式逻辑
│   ├── useUser.js / .ts          # 用户会话管理
│   ├── useShopUser.js / .ts      # 商家会话管理
│   └── useSmsCode.js / .ts       # 短信验证码逻辑
├── layouts/                      # 布局组件 (预留)
├── router/                       # 路由配置
│   ├── index.js                  # 路由入口
│   ├── guards.js                 # 导航守卫 (鉴权 + 标题)
│   └── modules/                  # 路由模块
│       ├── public.js             # 公开路由
│       ├── user.js               # 用户路由 (需登录)
│       ├── store.js              # 商家路由
│       └── admin.js              # 管理后台路由
├── stores/                       # Pinia 状态管理
│   ├── themeStore.js / .ts       # 主题 (日间/夜间模式)
│   ├── authStore.js / .ts        # 用户认证
│   └── cartStore.js / .ts        # 购物车
├── utils/                        # 工具函数
│   └── AbstractShapeBg.module.js
├── views/                        # 页面视图
│   ├── public/                   # 公开页面
│   │   ├── IndexView.vue         # 首页 (含商品推荐/分类/轮播)
│   │   ├── LoginView.vue         # 登录 (支持第三方)
│   │   ├── RegisterView.vue      # 注册
│   │   ├── ForgetPasswordView.vue
│   │   ├── SearchView.vue        # 文本搜索
│   │   ├── SearchByImageView.vue # 以图搜图
│   │   ├── NotFoundView.vue      # 404
│   │   └── TestView.vue
│   ├── user/                     # 用户中心
│   │   ├── MySpaceView.vue       # 个人空间
│   │   ├── AccountInformationView.vue
│   │   ├── AccountProfileView.vue
│   │   ├── AddressView.vue
│   │   ├── MyOrderView.vue       # 订单列表 (6种状态)
│   │   ├── BucketView.vue        # 购物车
│   │   ├── PayGoodsView.vue      # 结算
│   │   ├── SubmitOrderView.vue   # 支付
│   │   ├── MyFavoriteView.vue    # 收藏
│   │   ├── FootMarkView.vue      # 足迹
│   │   ├── MyCommentView.vue     # 评价
│   │   ├── TalkToStoreView.vue   # 联系商家
│   │   └── LiveView.vue          # 直播间
│   ├── goods/                    # 商品页面
│   │   ├── GoodInfoView.vue      # 商品详情
│   │   └── ShopView.vue          # 店铺主页
│   ├── auth/                     # 第三方登录回调
│   ├── Store/                    # 商家管理
│   │   ├── StoreLoginView.vue
│   │   ├── StoreRegisterView.vue
│   │   ├── StoreIndexView.vue    # 商家首页
│   │   ├── StoreGoodView.vue     # 商品管理
│   │   ├── StoreOrderListView.vue
│   │   ├── StoreOrderReturnsView.vue
│   │   ├── StoreStaffMangerView.vue
│   │   ├── StoreTalkView.vue
│   │   ├── StoreLiveView.vue
│   │   └── StoreTraceabilityManageView.vue
│   └── Admin/                    # 管理后台
│       ├── AdminIndexView.vue
│       └── AdminSyncView.vue
├── main.js / .ts                 # 应用入口
├── App.vue                       # 根组件
└── env.d.ts                      # TypeScript 类型声明
```

## 🛠️ 本地开发

### 前置依赖

- Node.js >= 16
- npm >= 8 (或 yarn)

### 安装运行

```bash
# 安装依赖
npm install

# 启动开发服务器 (默认 http://localhost:8080)
npm run serve

# 构建生产版本
npm run build

# 代码检查
npm run lint
```

### 环境变量

在项目根目录创建 \.env.local\ 文件：

```env
# API 服务地址 (默认空，使用同域)
VUE_APP_API_BASE_URL=http://localhost:8888
```

## 🧩 后端服务

前端需要以下后端服务配合运行：

| 服务 | 端口 | 技术栈 | 位置 |
|------|------|--------|------|
| Java 后端 | 8888 | Spring Boot + MyBatis + Redis + ES | \../SeafoodTraceabilityTradingSystem_backend_Java\ |
| Python 后端 | 8889 | Django + DRF + Milvus | \../SeafoodTraceabilityTradingSystem_backend_Python\ |

### Docker 一键启动

在项目根目录使用 Docker Compose 启动所有服务：

```bash
# 启动全部服务
docker-compose up -d

# 仅启动基础设施 (本地开发)
docker-compose -f docker-compose.dev.yml up -d
```

## ✨ 核心功能

### 🛒 电商核心
- 商品分类浏览 + 智能推荐 (协同过滤)
- 全文搜索 (ElasticSearch) + 以图搜图 (Milvus 向量检索)
- 购物车 / 订单 / 支付 / 评价 完整链路

### 🔍 食品溯源
- 商品溯源信息查询 (捕捞/养殖 → 加工 → 运输 → 销售)
- 区块链式溯源链展示

### 📺 直播带货
- 商家直播间
- 实时聊天 + 商品链接

### 🌤️ 附加功能
- 天气预报 + 动态图标
- 第三方登录 (Gitee/GitHub/Google/微信)
- 夜间模式
- 短信/邮箱验证

## 📊 代码质量

### 性能优化
- ✅ 路由全部懒加载
- ✅ 组件化复用 (OrderCard / WeatherIcon / OAuthLoginButton)
- ✅ CSS 工具类替代内联样式 (60%+ 减少)
- ✅ computed 替代模板过滤方法

### 架构优化
- ✅ 目录模块化 (views/components/api 分层)
- ✅ API 服务层 (统一拦截器 + Token 注入 + 错误处理)
- ✅ Pinia 状态管理 (authStore / cartStore / themeStore)
- ✅ 导航守卫 (路由鉴权 + 动态标题)

### 技术债务
- ⏳ TypeScript 迁移中 (12 个核心文件已完成)
- ⏳ 部分视图仍有分散的 inline style
- ⏳ 商城订单列表可继续复用 OrderCard

## 📄 许可证

MIT
