# 🐟 海潮 - 海鲜溯源交易平台（Python 后端）

> 基于 Django 5 + Django REST Framework 构建的图像搜索与AI服务后端，提供以图搜图、食品溯源辅助等特色功能。

## 📋 项目概览

Python 后端为海鲜溯源平台提供 AI 驱动的特色服务，包括基于特征向量的以图搜图和 Milvus 向量数据库集成。

| 模块 | 说明 |
|------|------|
| **图像搜索** | 以图搜图、相似图片检索 |
| **向量检索** | Milvus 向量数据库集成 |
| **溯源辅助** | 溯源数据加工与查询支持 |

## 🚀 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| [Django](https://www.djangoproject.com/) | 5.x | Web 框架 |
| [Django REST Framework](https://www.django-rest-framework.org/) | — | RESTful API |
| [Milvus](https://milvus.io/) | — | 向量数据库 (pymilvus) |
| [MySQL](https://www.mysql.com/) | — | 关系型数据库 |

## 📁 项目结构

```
pythonapi/
├── manage.py                        # Django 管理入口
├── requirements.txt                 # Python 依赖
├── Dockerfile                       # Docker 部署配置
├── .env.example                     # 环境变量模板
├── pythonapi/
│   └── settings.py                  # Django 配置
├── search/
│   └── image_search_views.py        # 以图搜图视图
└── templates/                       # 模板文件
```

## 🔧 快速开始

### 前置条件

- Python 3.9+
- Milvus 向量数据库
- MySQL 8.0+

### 安装与运行

```bash
# 安装依赖
pip install -r requirements.txt

# 复制环境配置
cp .env.example .env
# 编辑 .env 文件填入实际配置

# 数据库迁移
python manage.py migrate

# 启动服务
python manage.py runserver 0.0.0.0:8000
```

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DJANGO_SECRET_KEY` | Django密钥 | — |
| `DJANGO_DEBUG` | 调试模式 | `False` |
| `DB_NAME` | 数据库名 | `oceanwave` |
| `DB_USER` | 数据库用户 | — |
| `DB_PASSWORD` | 数据库密码 | — |
| `DB_HOST` | 数据库地址 | `localhost` |
| `DB_PORT` | 数据库端口 | `3306` |
| `MILVUS_HOST` | Milvus地址 | `localhost` |
| `MILVUS_PORT` | Milvus端口 | `19530` |
| `ALLOWED_HOSTS` | 允许主机 | `localhost,127.0.0.1` |

## 🔒 安全说明

- `SECRET_KEY` 已移至环境变量，不硬编码
- 生产模式 `DEBUG = False`
- `ALLOWED_HOSTS` 需在环境变量中显式配置
- 数据库密码通过环境变量注入
- Milvus 连接使用惰性单例，避免模块级全局连接

## 📦 Docker 部署

```bash
docker build -t seafood-python-backend .
docker run -p 8000:8000 seafood-python-backend
```

## 📄 许可

MIT License
