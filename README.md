<div align="center">

# 🍵 茶云商城

**基于 Spring Boot 3 + Vue 3 的全栈茶叶在线商城系统**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-42b883?logo=vue.js)](https://vuejs.org/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.5-blue)](https://baomidou.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange?logo=mysql)](https://www.mysql.com/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-latest-409eff?logo=element)](https://element-plus.org/)

</div>

---

## 📖 项目简介

**茶云商城**是一个前后端分离的全栈电商系统，专注于茶叶品类。系统分为**前台商城**与**后台管理**两大模块：

- **前台**：面向消费者，支持商品浏览、购物车、订单、用户中心等核心电商功能
- **后台**：面向管理员，支持商品管理、订单处理、用户管理、销售统计、轮播图与留言管理

---

## ✨ 功能模块

### 🛍️ 前台（消费者端）

| 功能 | 说明 |
|------|------|
| 商品浏览 | 首页轮播推荐、茶叶列表展示、分类筛选、关键词搜索 |
| 商品详情 | 查看茶叶详情、规格说明、库存信息 |
| 购物车 | 加入购物车、修改数量、批量结算 |
| 立即购买 | 跳过购物车直接生成订单 |
| 我的订单 | 查看历史订单、跟踪物流状态 |
| 用户中心 | 个人信息管理、收货地址修改 |
| 注册/登录 | 前台独立登录体系，与后台管理员账号隔离 |

### 🖥️ 后台（管理端）

| 功能 | 说明 |
|------|------|
| 茶叶管理 | 商品增删改查、图片上传、库存管理 |
| 订单管理 | 订单列表查看、发货操作、快递信息录入 |
| 用户管理 | 注册用户查看与管理 |
| 销售统计 | ECharts 可视化图表、茶叶销量排行榜 |
| 轮播图管理 | 首页轮播图的增删改与排序配置 |
| 留言管理 | 查看用户留言、管理员回复 |

---

## 🛠️ 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心应用框架 |
| MyBatis + MyBatis-Plus | 3.0.3 / 3.5.5 | ORM 持久层框架 |
| MySQL Connector | 最新 | 数据库驱动 |
| Lombok | 1.18.42 | 代码简化注解 |
| HikariCP | 内置 | 高性能数据库连接池 |
| Spring Validation | 内置 | 参数校验 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端核心框架（Composition API） |
| Vite (rolldown-vite) | 极速构建工具 |
| Element Plus | UI 组件库 |
| Vue Router 4 | 客户端路由管理 |
| Axios | HTTP 请求封装 |
| ECharts 6 | 数据可视化图表 |
| Pinia | 全局状态管理 |

---

## 📁 项目结构

```
chayun/
├── init_all.sql                          # 🗄️ 数据库一键初始化脚本（建库+建表+示例数据）
├── README.md
│
├── tea_backend/                          # 后端 Spring Boot 项目
│   ├── pom.xml                           # Maven 依赖配置
│   ├── mvnw / mvnw.cmd                   # Maven Wrapper（免安装 Maven）
│   └── src/main/
│       ├── java/com/xu/tea/
│       │   ├── TeaBackendApplication.java      # 启动入口
│       │   ├── common/Result.java              # 统一响应体封装
│       │   ├── config/WebConfig.java           # 静态资源映射、跨域配置
│       │   ├── controller/                     # REST API 控制器层
│       │   │   ├── AdminController.java        #   管理员登录
│       │   │   ├── CarouselController.java     #   轮播图管理
│       │   │   ├── CartController.java         #   购物车
│       │   │   ├── CommonController.java       #   文件上传
│       │   │   ├── MessageController.java      #   留言管理
│       │   │   ├── OrderController.java        #   订单管理
│       │   │   ├── TeaController.java          #   茶叶商品
│       │   │   └── UserController.java         #   用户管理
│       │   ├── entity/                         # 实体类（Lombok @Data）
│       │   │   ├── Admin / User / Tea          #   核心实体
│       │   │   ├── Cart / Order                #   交易实体
│       │   │   ├── Carousel / Message / Reply  #   内容实体
│       │   │   └── TeaSalesDTO.java            #   销量统计 DTO
│       │   ├── mapper/                         # MyBatis Mapper 接口
│       │   └── service/TeaService.java         # 业务服务层
│       └── resources/
│           ├── application.yml                 # 应用核心配置
│           └── mapper/*.xml                    # MyBatis SQL 映射文件
│
└── tea_frontend/                         # 前端 Vue 3 项目
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js                       # 应用入口（挂载 Vue/Router/ElementPlus/Axios）
        ├── style.css                     # 全局样式
        ├── router/index.js               # 路由配置（前台 + 后台路由）
        ├── layout/
        │   ├── Mylayout.vue              # 后台管理布局（侧边栏 + 顶栏）
        │   └── ClientLayout.vue          # 前台商城布局（导航栏 + 页脚）
        ├── views/
        │   ├── login/Login.vue           # 后台登录页
        │   ├── tea/TeaManage.vue         # 茶叶管理
        │   ├── order/OrderManage.vue     # 订单管理
        │   ├── user/UserManage.vue       # 用户管理
        │   ├── stats/SalesStats.vue      # 销售统计（ECharts）
        │   ├── marketing/CarouselManage.vue  # 轮播图管理
        │   ├── Message/MessageManage.vue     # 留言管理
        │   └── front/                    # 前台页面
        │       ├── Home.vue              #   首页
        │       ├── TeaDetail.vue         #   商品详情
        │       ├── FrontLogin.vue        #   前台登录/注册
        │       ├── Cart.vue              #   购物车
        │       ├── MyOrder.vue           #   我的订单
        │       └── UserCenter.vue        #   用户中心
        ├── assets/                       # 静态资源（Logo、Banner 图片）
        └── utils/message.js              # Element Plus Message 工具封装
```

---

## 🚀 快速开始

### 环境要求

| 依赖 | 版本要求 |
|------|----------|
| JDK | 17 或以上（已验证 JDK 23） |
| Node.js | 18 或以上 |
| MySQL | 5.7 / 8.0 均可 |
| Maven | 3.8+（或直接使用项目自带的 `mvnw`） |

---

### 第一步：初始化数据库

在 MySQL 客户端中执行根目录下的一键初始化脚本：

```sql
source /your/path/to/chayun/init_all.sql;
```

> 脚本会自动完成：建库 → 建表（8张）→ 插入示例数据（管理员、商品、用户、订单等）

**默认管理员账号：**

| 字段 | 值 |
|------|----|
| 用户名 | `admin` |
| 密码 | `123456` |

---

### 第二步：配置并启动后端

**修改数据库连接（如需）：**

```yaml
# tea_backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tea_mall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: 123456   # ← 改成你的 MySQL 密码

tea:
  images:
    path: D:/tea_images/   # ← 图片存储目录，请提前创建
```

**创建图片存储目录：**

```powershell
mkdir D:\tea_images
```

**启动后端：**

```powershell
cd tea_backend

# 推荐：使用 Maven Wrapper（无需本地安装 Maven）
.\mvnw spring-boot:run

# 或使用本地 Maven
mvn spring-boot:run
```

后端启动成功 → `http://localhost:8080`

---

### 第三步：启动前端

```powershell
cd tea_frontend

# 首次运行安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动成功 → `http://localhost:5173`

---

### 访问地址汇总

| 页面 | 地址 |
|------|------|
| 🏠 前台首页 | http://localhost:5173/front/home |
| 🔑 前台登录/注册 | http://localhost:5173/front/login |
| 🛡️ 后台登录 | http://localhost:5173/login |
| ⚙️ 后台管理（茶叶） | http://localhost:5173/tea |

---

## 📡 API 接口一览

后端 API 基础路径：`http://localhost:8080`

| 模块 | 路径前缀 | 主要功能 |
|------|----------|----------|
| 茶叶商品 | `/tea` | 列表查询、详情、增删改查、库存扣减 |
| 订单 | `/order` | 下单、立即购买、批量结算、发货、统计 |
| 购物车 | `/cart` | 列表、添加、删除、修改数量 |
| 用户 | `/user` | 注册、登录、个人信息修改 |
| 管理员 | `/admin` | 管理员登录 |
| 轮播图 | `/carousel` | 列表、增删改、排序 |
| 留言 | `/message` | 留言列表、新增留言、回复留言 |
| 文件上传 | `/common` | 图片本地上传 |

---

## 🗄️ 数据库表结构

| 表名 | 说明 |
|------|------|
| `t_admin` | 管理员账号表 |
| `t_user` | 前台用户表 |
| `t_tea` | 茶叶商品表（含价格、库存、分类、规格） |
| `t_cart` | 购物车表 |
| `t_order` | 订单表（含收货人、快递单号信息） |
| `t_carousel` | 首页轮播图表 |
| `t_message` | 用户留言表 |
| `t_reply` | 管理员回复表（与留言关联） |

> 完整建表 SQL 及示例数据见根目录：[`init_all.sql`](./init_all.sql)

---

## ❓ 常见问题

**Q：后端启动失败，提示找不到 JAVA_HOME？**
> 确保环境变量 `JAVA_HOME` 已指向 JDK 17+ 安装目录。

**Q：数据库连接失败？**
> 1. 确认 MySQL 服务正在运行
> 2. 检查 `application.yml` 中的数据库地址、用户名、密码
> 3. 确认 `tea_mall` 数据库已通过 `init_all.sql` 创建

**Q：商品图片无法显示？**
> 确认 `D:/tea_images/` 目录存在，或修改 `application.yml` 中的 `tea.images.path` 为有效路径。

**Q：前端请求跨域报错？**
> 后端已通过 `@CrossOrigin` 注解全局开启跨域支持，确认 Axios 的 `baseURL` 配置为 `http://localhost:8080`。

**Q：Maven 编译报错？**
> 执行 `mvn clean` 清理 `target/` 后重新运行。

---

## 📝 开发注意事项

- 后端实体类使用 Lombok `@Data` 注解，无需手写 getter/setter
- MyBatis 已开启驼峰转换（`map-underscore-to-camel-case: true`），数据库字段 `img_url` 自动映射为 Java 字段 `imgUrl`
- 后台路由守卫：管理页面访问前检查 `localStorage` 中是否存在 `tea-admin-user`
- 前台普通用户无需登录可浏览商品，下单/购物车等功能需先登录

---

<div align="center">

Made with ❤️ by Xu

</div>
