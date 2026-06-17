# 🐟 海潮 - 海鲜溯源交易平台（Java 后端）

> 基于 Spring Boot 2.6.13 构建的海鲜电商后端服务，提供商品管理、订单管理、用户鉴权、溯源查询、实时通信等完整 RESTful API。

## 📋 项目概览

海潮后端为前端提供完整的 API 支撑，涵盖用户端、商家端和管理后台三大模块。

| 模块 | 说明 |
|------|------|
| **用户服务** | 注册登录、用户信息、地址管理、收藏足迹 |
| **商品服务** | 商品CRUD、规格管理、ES搜索、以图搜图 |
| **订单服务** | 订单流转、退换货、评论、支付 |
| **商家服务** | 店铺管理、员工管理、订单处理、直播管理 |
| **即时通讯** | WebSocket 聊天、直播间互动、通知推送 |
| **溯源服务** | 食品溯源链查询、区块链存证对接 |

## 🚀 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 2.6.13 | 应用框架 |
| [Spring Security](https://spring.io/projects/spring-security) | 5.6.x | 安全认证 (BCrypt) |
| [MyBatis](https://mybatis.org/) | 2.2.x | ORM 持久层 |
| [Redis](https://redis.io/) | (Spring Data Redis) | 缓存加速 |
| [Elasticsearch](https://www.elastic.co/) | 7.x | 商品全文搜索 |
| [MySQL](https://www.mysql.com/) | 8.x | 关系型数据库 |
| [WebSocket](https://developer.mozilla.org/WebSockets) | — | 实时通信 |
| [Druid](https://github.com/alibaba/druid) | 1.2.x | 数据库连接池 |
| [JUnit 5](https://junit.org/junit5/) | — | 单元测试 |

## 📁 项目结构

```
src/main/java/com/shopping_c_backend/
├── ShoppingCBackendApplication.java    # 启动类
├── common/                             # 公共模块
│   ├── cache/                          # 缓存抽象 (CacheService)
│   ├── config/                         # 全局配置 (Web/Redis/Security/ES/WebSocket)
│   ├── constant/                       # 常量定义
│   ├── util/                           # 工具类 (无MD5，已替换为BCrypt)
│   └── web/                            # 统一响应 (Result / ResultCode)
├── module/                             # 业务模块
│   ├── address/                        # 地址管理
│   ├── admin/                          # 管理员
│   ├── auth/                           # 第三方OAuth登录
│   ├── cart/                           # 购物车
│   ├── chat/                           # 聊天/WebSocket
│   ├── favorite/                       # 收藏
│   ├── footprint/                      # 足迹
│   ├── goods/                          # 商品
│   ├── live/                           # 直播
│   ├── order/                          # 订单/评论
│   ├── search/                         # ES搜索/以图搜图
│   ├── shop/                           # 店铺/员工
│   ├── trace/                          # 溯源
│   └── user/                           # 用户
└── websocket/                          # WebSocket 核心
    ├── core/                           # 路由/会话管理
    └── handler/                        # 消息处理器
```

## 🔧 快速开始

### 前置条件

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6+
- Elasticsearch 7.x (可选)

### 配置

1. 复制环境配置模板：
```bash
cp src/main/resources/application-dev.yml src/main/resources/application.yml
```

2. 修改 `application.yml` 中的数据库、Redis 连接信息

3. 编译运行：
```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_HOST` | 数据库地址 | `localhost` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_NAME` | 数据库名 | `shopping_c` |
| `DB_USER` | 数据库用户 | `root` |
| `DB_PASSWORD` | 数据库密码 | — |
| `REDIS_HOST` | Redis地址 | `localhost` |
| `REDIS_PORT` | Redis端口 | `6379` |
| `ES_HOST` | ES地址 | `localhost` |

## 🔒 安全说明

- 密码使用 BCrypt 加密存储
- 敏感配置已移至环境变量，不包含硬编码密码
- 统一 CORS 配置，无跨域安全隐患
- 输入内容经过 HTML 防XSS过滤

## 📦 Docker 部署

```bash
# 构建镜像
docker build -t seafood-java-backend .

# 运行
docker run -p 8080:8080 seafood-java-backend
```

## 📄 许可

MIT License
