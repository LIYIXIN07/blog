# 上线前自动化测试
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $Root) { $Root = "." }
$ProjectRoot = Resolve-Path (Join-Path $Root "..")

Write-Host "`n========== 博客系统自动化测试 ==========" -ForegroundColor Cyan
Write-Host "项目目录: $ProjectRoot`n"

$failed = $false

function Run-Step($name, $scriptBlock) {
    Write-Host "`n--- $name ---" -ForegroundColor Yellow
    try {
        & $scriptBlock
        Write-Host "OK: $name" -ForegroundColor Green
    } catch {
        Write-Host "FAIL: $name" -ForegroundColor Red
        Write-Host $_.Exception.Message
        $script:failed = $true
    }
}

Run-Step "前端单元测试 (Vitest)" {
    Push-Location $ProjectRoot
    npm run test
    Pop-Location
}

Run-Step "API 冒烟测试" {
    Push-Location $ProjectRoot
    node scripts/test-api.mjs
    Pop-Location
}

Run-Step "E2E 测试 (Playwright)" {
    Push-Location $ProjectRoot
    $env:E2E_SKIP_SERVER = "1"
    npm run test:e2e
    Pop-Location
}

Run-Step "后端集成测试 (Maven)" {
    $mvn = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $mvn) {
        Write-Host "跳过: 未找到 mvn，请安装 Maven 后执行 cd blog-backend && mvn test" -ForegroundColor DarkYellow
        return
    }
    Push-Location (Join-Path $ProjectRoot "blog-backend")
    mvn -q test
    Pop-Location
}

Write-Host "`n========================================" -ForegroundColor Cyan
if ($failed) {
    Write-Host "存在失败项，请修复后再上线。" -ForegroundColor Red
    exit 1
}
Write-Host "全部测试通过，可以准备上线。" -ForegroundColor Green
exit 0
