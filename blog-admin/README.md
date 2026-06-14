# 博客管理系统

基于 Vue 3 + TypeScript + Element Plus 的博客后台管理系统。

## 技术栈

- **框架**: Vue 3.4+
- **类型系统**: TypeScript 5.4+
- **UI 框架**: Element Plus 2.7+
- **状态管理**: Pinia 2.1+
- **路由**: Vue Router 4.3+
- **样式**: Tailwind CSS 3.4+
- **构建工具**: Vite 5.2+
- **图表**: ECharts 5.5+
- **富文本编辑器**: Vditor 3.10+

## 功能模块

- ✅ 登录页面 - 支持用户名密码登录
- ✅ 仪表盘 - 数据统计与可视化
- ✅ 文章管理 - 文章列表、新增、编辑、删除
- ✅ 分类管理 - 分类的增删改查
- ✅ 标签管理 - 标签的增删改查，支持颜色选择
- ✅ 友情链接 - 友情链接的增删改查
- ✅ 系统设置 - 站点信息、作者信息配置

## 项目结构

```
src/
├── api/              # API 请求封装
│   ├── article.ts    # 文章相关接口
│   ├── category.ts   # 分类相关接口
│   ├── tag.ts        # 标签相关接口
│   ├── user.ts       # 用户/认证相关接口
│   └── request.ts    # 请求拦截器配置
├── assets/
│   └── styles/       # 全局样式
├── router/           # 路由配置
├── stores/           # Pinia 状态管理
├── types/            # TypeScript 类型定义
├── utils/            # 工具函数
└── views/            # 页面组件
    ├── articles/     # 文章管理页面
    ├── categories/   # 分类管理页面
    ├── tags/         # 标签管理页面
    ├── Login.vue     # 登录页面
    ├── Layout.vue    # 布局组件
    ├── Dashboard.vue # 仪表盘页面
    ├── Settings.vue  # 系统设置页面
    ├── FriendLinks.vue # 友情链接页面
    └── NotFound.vue  # 404页面
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

## 环境配置

在 `.env.development` 文件中配置后端 API 地址：

```env
VITE_APP_TITLE=博客管理系统
VITE_API_BASE_URL=http://121.4.27.29:8080/api
```

## 默认账号

- 用户名: `admin`
- 密码: `123456`

## 接口文档

后端 API 部署在 `http://121.4.27.29:8080/api`，接口规范参考 RESTful 标准。