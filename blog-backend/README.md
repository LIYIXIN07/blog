# 个人博客系统后端

基于Spring Boot 3.2.3开发的个人博客系统后端API。

## 技术栈

- **Spring Boot**: 3.2.3
- **Spring Security**: JWT认证
- **Spring Data JPA**: 数据持久化
- **Redis**: 接口缓存加速
- **MySQL**: 8.0+
- **JWT**: 0.12.5
- **Lombok**: 简化代码
- **Hutool**: 工具类库

## 项目结构

```
blog-backend/
├── src/main/java/com/blog/
│   ├── common/           # 公共模块
│   │   ├── result/       # 统一响应结果
│   │   └── exception/    # 异常处理
│   ├── config/           # 配置类
│   │   ├── SecurityConfig.java
│   │   ├── JwtUtils.java
│   │   └── JwtAuthenticationFilter.java
│   ├── controller/       # 控制器层
│   ├── dto/              # 数据传输对象
│   │   ├── request/
│   │   └── response/
│   ├── entity/           # 实体类
│   ├── repository/       # 数据访问层
│   └── service/          # 服务层
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/
│       └── init.sql
└── pom.xml
```

## 数据库配置

- **端口**: 3305
- **用户名**: root
- **密码**: 123456
- **数据库**: blog

## Redis 缓存

前台高频读接口（设置、分类、标签、文章列表、侧边栏 widget 等）已接入 **Redis 缓存**，可显著降低 MySQL 压力。

### 1. 启动 Redis

在项目根目录 `D:\markdownsystem`：

```bash
docker compose up -d redis
```

默认连接：`localhost:6379`（无密码）。可通过环境变量覆盖：

- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`

### 2. 启动后端

Redis 运行后再启动 Spring Boot，否则会连接失败。

### 缓存策略（摘要）

| 数据 | TTL |
|------|-----|
| 站点设置 / 关于页 / 友链 | 1 小时 |
| 分类 / 标签 / 归档 | 30 分钟 |
| 文章列表 / 搜索 / 最新 | 10 分钟 |
| 热门文章 | 5 分钟 |
| 随机文章 | 2 分钟 |
| 站点统计 | 3 分钟 |

后台发布、编辑、删除文章或修改分类/标签/设置时会自动清除相关缓存。

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/user` - 获取当前用户信息

### 文章接口
- `GET /api/articles` - 分页查询文章列表
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles` - 创建文章
- `PUT /api/articles` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章
- `POST /api/articles/batch-delete` - 批量删除文章
- `POST /api/articles/{id}/view` - 增加文章阅读量
- `GET /api/articles/latest` - 获取最新文章
- `GET /api/articles/hot` - 获取热门文章

### 分类接口
- `GET /api/categories` - 获取所有分类
- `GET /api/categories/{id}` - 获取分类详情
- `POST /api/categories` - 创建分类
- `PUT /api/categories` - 更新分类
- `DELETE /api/categories/{id}` - 删除分类

### 标签接口
- `GET /api/tags` - 获取所有标签
- `GET /api/tags/{id}` - 获取标签详情
- `POST /api/tags` - 创建标签
- `PUT /api/tags` - 更新标签
- `DELETE /api/tags/{id}` - 删除标签

### 系统设置接口
- `GET /api/settings` - 获取系统设置
- `PUT /api/settings` - 更新系统设置

### 友情链接接口
- `GET /api/friend-links` - 获取所有友情链接
- `POST /api/friend-links` - 创建友情链接
- `PUT /api/friend-links` - 更新友情链接
- `DELETE /api/friend-links/{id}` - 删除友情链接

## 启动方式

### 1. 创建数据库

执行 `src/main/resources/db/init.sql` 创建数据库和表结构。

### 2. 配置Maven

确保Maven已安装并配置正确，Maven地址：`D:\apache-maven-3.9.8`

### 3. 运行项目

```bash
# 进入项目目录
cd blog-backend

# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

或者使用IDE直接运行 `BlogApplication.java`。

## 默认账号

- **用户名**: admin
- **密码**: admin123

## 开发环境

- JDK 17+
- Maven 3.9.8
- MySQL 8.0+

## 生产部署

修改 `application-prod.yml` 配置文件，设置生产环境的数据库连接和其他参数。

```bash
# 打包生产版本
mvn clean package -Pprod

# 运行
java -jar target/blog-backend-1.0.0.jar
```

## 注意事项

1. 首次启动会自动创建管理员用户
2. JWT Token有效期24小时
3. 所有API接口已配置CORS，支持跨域访问
4. 前台公开接口无需认证，后台管理接口需要JWT Token认证