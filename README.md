# 茶韵 - 茶叶在线商城系统

## 项目概述

茶韵是一个基于 **Spring Boot + Vue 3** 的全栈茶叶在线商城系统，提供前台购物与后台管理双模块。前台面向消费者，支持浏览商品、加入购物车、下单购买、查看订单等功能；后台面向管理员，支持茶叶管理、订单管理、用户管理、销售统计、轮播图管理及留言管理等功能。

## 主要功能

### 前台（消费者端）

| 功能 | 说明 |
|------|------|
| 商品浏览 | 首页展示茶叶列表、轮播图推荐、分类筛选 |
| 商品详情 | 查看茶叶详细信息、规格、库存 |
| 购物车 | 加入购物车、修改数量、批量结算 |
| 立即购买 | 跳过购物车直接下单 |
| 订单管理 | 查看我的订单、订单状态跟踪 |
| 用户中心 | 个人信息管理、收货地址管理 |
| 用户注册/登录 | 前台独立登录体系 |

### 后台（管理端）

| 功能 | 说明 |
|------|------|
| 茶叶管理 | 茶叶商品增删改查、图片上传、库存管理 |
| 订单管理 | 订单列表、发货操作、快递信息录入 |
| 用户管理 | 注册用户查看与管理 |
| 销售统计 | ECharts 数据可视化、茶叶销量排行 |
| 轮播图管理 | 首页轮播图配置 |
| 留言管理 | 用户留言查看与回复 |

## 核心文件结构

```
chayun/
├── tea_backend/                          # 后端 Spring Boot 项目
│   ├── pom.xml                           # Maven 依赖配置
│   ├── mvnw / mvnw.cmd                  # Maven Wrapper
│   ├── .mvn/wrapper/                    # Maven Wrapper 配置
│   └── src/
│       ├── main/
│       │   ├── java/com/xu/tea/
│       │   │   ├── TeaBackendApplication.java   # 启动类
│       │   │   ├── common/
│       │   │   │   └── Result.java              # 统一响应封装
│       │   │   ├── config/
│       │   │   │   └── WebConfig.java           # 静态资源映射配置
│       │   │   ├── controller/                  # REST 控制器
│       │   │   │   ├── AdminController.java     #   管理员
│       │   │   │   ├── CarouselController.java  #   轮播图
│       │   │   │   ├── CartController.java      #   购物车
│       │   │   │   ├── CommonController.java    #   文件上传
│       │   │   │   ├── MessageController.java   #   留言
│       │   │   │   ├── OrderController.java     #   订单
│       │   │   │   ├── TeaController.java       #   茶叶
│       │   │   │   └── UserController.java      #   用户
│       │   │   ├── entity/                      # 实体类 (Lombok @Data)
│       │   │   │   ├── Admin.java               #   管理员
│       │   │   │   ├── Carousel.java            #   轮播图
│       │   │   │   ├── Cart.java                #   购物车
│       │   │   │   ├── Message.java             #   留言
│       │   │   │   ├── Order.java               #   订单
│       │   │   │   ├── Reply.java               #   回复
│       │   │   │   ├── Tea.java                 #   茶叶
│       │   │   │   ├── TeaSalesDTO.java         #   销量统计DTO
│       │   │   │   └── User.java                #   用户
│       │   │   ├── mapper/                      # MyBatis Mapper 接口
│       │   │   │   ├── AdminMapper.java
│       │   │   │   ├── CarouselMapper.java
│       │   │   │   ├── CartMapper.java
│       │   │   │   ├── MessageMapper.java
│       │   │   │   ├── OrderMapper.java
│       │   │   │   ├── TeaMapper.java
│       │   │   │   └── UserMapper.java
│       │   │   └── service/
│       │   │       └── TeaService.java
│       │   └── resources/
│       │       ├── application.yml              # 应用配置
│       │       ├── mapper/*.xml                 # MyBatis XML 映射
│       │       ├── db_init_minimal.sql          # 数据库初始化 (精简)
│       │       ├── db_init_complete.sql         # 数据库初始化 (完整)
│       │       ├── init_order_table.sql         # 订单表建表
│       │       ├── fix_order_table.sql          # 订单表修复
│       │       └── static/                      # 静态资源目录
│       └── test/
│           └── java/com/xu/tea/
│               └── TeaBackendApplicationTests.java
│
└── tea_frontend/                         # 前端 Vue 3 项目
    ├── package.json                      # NPM 依赖配置
    ├── vite.config.js                    # Vite 构建配置
    ├── index.html                        # 入口 HTML
    └── src/
        ├── main.js                       # 应用入口 (挂载 Vue/Router/ElementPlus/Axios)
        ├── App.vue                       # 根组件
        ├── style.css                     # 全局样式
        ├── router/
        │   └── index.js                  # 路由配置 (前台 + 后台)
        ├── utils/
        │   └── message.js                # ElementPlus Message 默认值补丁
        ├── layout/
        │   ├── MyLayout.vue              # 后台管理布局 (侧边栏 + 顶栏)
        │   └── ClientLayout.vue          # 前台商城布局 (导航栏 + 底栏)
        ├── views/
        │   ├── login/Login.vue           # 后台登录页
        │   ├── admin/                    # (管理通用)
        │   ├── tea/TeaManage.vue         # 茶叶管理页
        │   ├── order/OrderManage.vue     # 订单管理页
        │   ├── user/UserManage.vue       # 用户管理页
        │   ├── stats/SalesStats.vue      # 销售统计页
        │   ├── marketing/CarouselManage.vue  # 轮播图管理页
        │   ├── Message/MessageManage.vue # 留言管理页
        │   └── front/                    # 前台页面
        │       ├── Home.vue              #   首页
        │       ├── TeaDetail.vue         #   商品详情页
        │       ├── FrontLogin.vue        #   前台登录页
        │       ├── Cart.vue              #   购物车页
        │       ├── MyOrder.vue           #   我的订单页
        │       └── UserCenter.vue        #   用户中心页
        ├── assets/                       # 静态资源 (Logo、Banner)
        ├── components/
        │   └── HelloWorld.vue            # 示例组件
        └── tests/
            └── message.test.js           # 消息工具测试
```

## 环境配置要求

### 基础环境

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 后端运行环境 (已验证 JDK 23) |
| Node.js | 18+ | 前端运行环境 |
| Maven | 3.8+ | 后端构建工具 (项目自带 Maven Wrapper) |
| MySQL | 5.7+ / 8.0 | 数据库 |

### 环境变量

确保 `JAVA_HOME` 已正确配置指向 JDK 安装目录，例如：

```powershell
# PowerShell 临时设置
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
```

### 数据库配置

1. 创建 MySQL 数据库 `tea_mall`：

```sql
CREATE DATABASE IF NOT EXISTS tea_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 执行初始化脚本（按需选择）：
   - `tea_backend/src/main/resources/db_init_minimal.sql` — 精简版，仅建表
   - `tea_backend/src/main/resources/db_init_complete.sql` — 完整版，含注释

3. 修改数据库连接信息（如需）：

```yaml
# tea_backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tea_mall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: 123456
```

### 图片上传目录

后端配置了本地图片存储路径，需确保目录存在：

```yaml
# application.yml
tea:
  images:
    path: D:/tea_images/
```

请手动创建 `D:/tea_images/` 目录，或修改为其他可用路径。

## 运行说明

### 1. 启动后端

```powershell
cd tea_backend

# 方式一：使用 Maven Wrapper（推荐，无需本地安装 Maven）
.\mvnw spring-boot:run

# 方式二：使用本地 Maven
mvn spring-boot:run
```

后端启动成功后访问：`http://localhost:8080`

### 2. 启动前端

```powershell
cd tea_frontend

# 首次运行需安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后访问：`http://localhost:5173`

### 3. 构建生产版本

```powershell
# 后端打包
cd tea_backend
mvn clean package -DskipTests
# 产物: target/tea_backend-0.0.1-SNAPSHOT.jar

# 前端构建
cd tea_frontend
npm run build
# 产物: dist/
```

### 4. 默认访问路径

| 页面 | 地址 |
|------|------|
| 前台首页 | `http://localhost:5173/front/home` |
| 前台登录 | `http://localhost:5173/front/login` |
| 后台登录 | `http://localhost:5173/login` |
| 后台管理 | `http://localhost:5173/tea` (需先登录) |

## 技术栈

### 后端

- **Spring Boot 3.2.5** — 应用框架
- **MyBatis-Plus 3.5.5** — ORM 框架
- **MySQL** — 数据库
- **Lombok** — 代码简化
- **HikariCP** — 数据库连接池

### 前端

- **Vue 3** — 前端框架
- **Vite (rolldown-vite)** — 构建工具
- **Element Plus** — UI 组件库
- **Vue Router 4** — 路由管理
- **Axios** — HTTP 请求
- **ECharts 6** — 数据可视化
- **Pinia** — 状态管理

## API 接口概览

后端 API 基础路径：`http://localhost:8080`

| 模块 | 路径前缀 | 主要接口 |
|------|----------|----------|
| 茶叶 | `/tea` | 列表、详情、增删改、库存扣减 |
| 订单 | `/order` | 列表、下单、立即购买、批量结算、发货、统计 |
| 购物车 | `/cart` | 列表、添加、删除、修改数量 |
| 用户 | `/user` | 注册、登录、信息修改 |
| 管理员 | `/admin` | 登录 |
| 轮播图 | `/carousel` | 列表、增删改 |
| 留言 | `/message` | 列表、添加、回复 |
| 文件上传 | `/common` | 图片上传 |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `t_tea` | 茶叶商品表 |
| `t_order` | 订单表 (含收货人、快递信息) |
| `t_cart` | 购物车表 |
| `t_user` | 用户表 |
| `t_admin` | 管理员表 |
| `t_carousel` | 轮播图表 |
| `t_message` | 留言表 |
| `t_reply` | 回复表 |

## 维护指南

### 日常维护

- **日志**：后端日志级别为 `debug`，输出到控制台（StdOutImpl），生产环境建议调整为 `info` 或 `warn`
- **数据库**：定期备份 `tea_mall` 数据库，建议使用 `mysqldump` 工具
- **图片资源**：上传的图片存储在 `D:/tea_images/`，需定期备份和清理无用图片

### 常见问题

1. **后端启动失败 — JAVA_HOME 未设置**
   - 确保环境变量 `JAVA_HOME` 指向 JDK 17+ 安装目录

2. **数据库连接失败**
   - 检查 MySQL 服务是否启动
   - 确认 `application.yml` 中的数据库地址、用户名、密码正确
   - 确保数据库 `tea_mall` 已创建

3. **图片无法显示**
   - 检查 `D:/tea_images/` 目录是否存在
   - 确认 `application.yml` 中 `tea.images.path` 配置正确

4. **前端跨域问题**
   - 后端已通过 `@CrossOrigin` 注解开启跨域支持
   - 确保 Axios baseURL 配置为 `http://localhost:8080`

5. **Maven 编译缓存问题**
   - 执行 `mvn clean` 清理 `target/` 目录后重新编译

### 开发注意事项

- 后端实体类使用 Lombok `@Data` 注解自动生成 getter/setter
- MyBatis 开启了驼峰转换 (`map-underscore-to-camel-case: true`)，数据库字段下划线自动映射为 Java 驼峰
- 前端路由守卫：后台管理页面需登录后访问（检查 `localStorage` 中的 `tea-admin-user`）
- 前台页面无需登录即可浏览，下单等功能需要前台用户登录
