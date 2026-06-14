# 自动化测试指南

上线前请按顺序执行以下测试。

## 前置条件

| 服务 | 地址 | 说明 |
|------|------|------|
| MySQL | localhost:3305 | 库 `blog`，用于 API 冒烟 |
| 后端 | http://localhost:8080/api | Spring Boot |
| 前台 | http://localhost:3000 | Vite dev（E2E 可自动启动） |

## 1. 安装测试依赖（首次）

```bash
cd D:\markdownsystem
npm install
npx playwright install chromium
```

## 2. 后端集成测试（H2 内存库，无需 MySQL）

需要 **Java 17+** 和 **Maven**：

```bash
cd blog-backend
mvn test
```

覆盖内容：
- 应用上下文启动
- 公开 API（about、tags、settings、articles 等）
- 管理员登录 / JWT
- 默认数据初始化

## 3. 前端单元测试

```bash
cd D:\markdownsystem
npm run test
```

## 4. API 冒烟测试（需后端已启动）

```bash
npm run test:api
# 或指定地址
set API_BASE=http://localhost:8080/api
node scripts/test-api.mjs
```

## 5. E2E 端到端测试

确保后端在跑（前台通过 proxy 访问 `/api`）：

```bash
npm run test:e2e
```

若前台已在 3000 端口运行，Playwright 会复用现有服务。

## 6. 一键脚本（Windows）

```powershell
.\scripts\run-tests.ps1
```

## 7. 上线前检查清单

- [ ] `mvn test` 全部通过
- [ ] `npm run test` 全部通过
- [ ] `npm run test:api` 全部通过（后端 + MySQL 运行中）
- [ ] `npm run test:e2e` 全部通过
- [ ] `npm run build` 前台构建成功
- [ ] `blog-admin` 构建成功
- [ ] 生产环境 `application-prod.yml` 已配置数据库与 JWT 密钥

## CI

推送到 `main`/`master` 分支时，GitHub Actions 会自动运行后端 + 前端单元 + E2E 测试。
