# 🐟 海潮 - 海鲜溯源交易平台

> 全栈海鲜电商交易平台，支持商品浏览、溯源查询、以图搜图、直播带货、实时聊天等完整电商功能。
> 
> **技术栈：** Vue 3 (前端) + Spring Boot (Java 后端) + Django (Python 后端/图像搜索)

---

## 📋 项目概览

海潮是一个专注于**海鲜食品溯源交易**的全栈电商平台，采用微服务架构设计，分为三个独立端：

| 端 | 技术栈 | 职责 | 仓库分支 |
|------|---------|------|-----------|
| **前端** | Vue 3 + Element Plus + Pinia + TypeScript | 用户端/商家端/管理后台 UI | [`vue`](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/vue) |
| **Java 后端** | Spring Boot 2.6.13 + MyBatis + Redis + ES | 核心业务 API（商品/订单/用户/溯源/WebSocket） | [`java`](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/java) |
| **Python 后端** | Django 5 + DRF + Milvus | 图像搜索与 AI 服务 | [`python`](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/python) |

## ✨ 功能特性

### 🛒 用户端
- 商品浏览与全文检索（Elasticsearch）
- 以图搜图（Milvus 向量检索）
- 购物车、下单支付、订单管理
- 食品溯源链查询（全流程追溯）
- 商品收藏、浏览足迹
- 商品直播、实时聊天
- 天气服务、地址管理

### 🏪 商家端
- 商品管理（CRUD + 规格管理）
- 订单处理与退换货
- 溯源数据管理
- 员工权限管理
- 直播间管理
- 客户聊天

### 🔧 管理后台
- 商家审核入驻
- MySQL → ES 数据同步
- 平台运营监控

## 🏗️ 项目结构

```
SeafoodTraceabilityTradingSystem/
├── SeafoodTraceabilityTradingSystem_frontend/    # Vue 3 前端
│   ├── src/
│   │   ├── views/                                # 页面视图（按模块分组）
│   │   ├── components/                           # 可复用组件
│   │   ├── api/                                  # API 服务层
│   │   ├── router/                               # 路由（模块化）
│   │   ├── store/                                # Pinia 状态管理
│   │   ├── composables/                          # 组合式函数
│   │   ├── assets/                               # 静态资源
│   │   └── utils/                                # 工具类
│   ├── package.json
│   └── vue.config.js
├── SeafoodTraceabilityTradingSystem_backend_Java/ # Spring Boot 后端
│   ├── src/main/java/com/shopping_c_backend/
│   │   ├── common/                               # 公共模块（配置/缓存/常量/工具）
│   │   ├── module/                               # 业务模块
│   │   └── websocket/                            # WebSocket 通信
│   └── pom.xml
├── SeafoodTraceabilityTradingSystem_backend_Python/ # Django 后端
│   ├── pythonapi/
│   ├── search/                                   # 图像搜索
│   └── manage.py
├── docker-compose.yml                            # 全栈编排
├── docker-compose.dev.yml                        # 基础设施（仅依赖服务）
└── .github/workflows/ci.yml                      # CI 流水线
```

## 🚀 快速启动

### 方式一：Docker Compose（推荐）

```bash
# 克隆仓库
git clone https://github.com/Dear-May/SeafoodTraceabilityTradingSystem.git
cd SeafoodTraceabilityTradingSystem

# 启动全部服务
docker-compose up -d
```

### 方式二：本地开发

**1. 启动基础设施（MySQL + Redis + ES + Milvus）**

```bash
docker-compose -f docker-compose.dev.yml up -d
```

**2. 启动 Java 后端**

```bash
cd SeafoodTraceabilityTradingSystem_backend_Java
cp src/main/resources/application-dev.yml src/main/resources/application.yml
# 编辑 application.yml 配置数据库连接
mvn spring-boot:run
```

**3. 启动 Python 后端**

```bash
cd SeafoodTraceabilityTradingSystem_backend_Python
cp .env.example .env
# 编辑 .env 配置密钥和数据库连接
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
```

**4. 启动前端**

```bash
cd SeafoodTraceabilityTradingSystem_frontend
npm install
npm run serve
```

访问 http://localhost:8080 即可使用。

## 🧩 分支说明

| 分支 | 内容 | 快速链接 |
|------|------|----------|
| `main` | 全栈完整代码 | [README](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem) |
| `vue` | 仅 Vue 前端 | [查看](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/vue) |
| `java` | 仅 Java 后端 | [查看](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/java) |
| `python` | 仅 Python 后端 | [查看](https://github.com/Dear-May/SeafoodTraceabilityTradingSystem/tree/python) |

每个独立分支都包含专属 README，点击即可查看对应端的详细文档。

## 🔒 安全说明

- 密码使用 **BCrypt** 加密存储（已替换 MD5）
- 敏感配置（数据库密码、API 密钥、SECRET_KEY）均通过 **环境变量** 注入
- 统一 CORS 配置，防止跨域攻击
- 输入内容经过 **HTML 防 XSS 过滤**
- 生产模式 `DEBUG = False`

## 🛠️ 技术栈详情

### 前端
| 技术 | 用途 |
|------|------|
| Vue 3 (Composition API) | 前端框架 |
| Vue Router 4 | 路由管理（懒加载 + 导航守卫） |
| Pinia | 状态管理 |
| Element Plus | UI 组件库 |
| Axios | HTTP 请求（统一拦截器） |
| ECharts | 数据可视化 |
| Marked | Markdown 渲染 |
| TypeScript | 类型支持 |

### Java 后端
| 技术 | 用途 |
|------|------|
| Spring Boot 2.6.13 | 应用框架 |
| Spring Security (BCrypt) | 安全认证 |
| MyBatis | ORM 持久层 |
| Redis | 缓存加速 |
| Elasticsearch | 商品全文搜索 |
| WebSocket | 实时通信 |
| Druid | 数据库连接池 |

### Python 后端
| 技术 | 用途 |
|------|------|
| Django 5 | Web 框架 |
| Django REST Framework | RESTful API |
| Milvus (pymilvus) | 向量检索 |

## 📄 许可

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。
