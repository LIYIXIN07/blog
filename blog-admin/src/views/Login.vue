<template>
  <div class="login-page" @mousemove="onPageMouseMove">
    <div class="login-bg">
      <div class="login-bg-grid" :style="brandParallaxStyle" />
      <div class="login-bg-orb login-bg-orb--1" :style="brandParallaxStyle" />
      <div class="login-bg-orb login-bg-orb--2" />
      <div class="login-bg-orb login-bg-orb--3" />
    </div>

    <div class="login-shell">
      <aside class="login-brand" :style="brandParallaxStyle">
        <ParticleBackground :parallax-x="parallax.x" :parallax-y="parallax.y" />

        <div class="brand-content">
          <div class="brand-logo">
            <BrandLogo :size="44" />
            <div>
              <span class="brand-name">DollMeowOnly</span>
              <span class="brand-tag">Blog Admin</span>
            </div>
          </div>

          <h1 class="brand-headline">博客后台控制台</h1>
          <p class="brand-slogan">内容创作 · 数据洞察 · 一站管理</p>
          <p class="brand-desc">
            管理文章、分类、评论与站点数据，为创作者提供高效、稳定的内容运营体验。
          </p>

          <ul class="brand-features">
            <li><el-icon><EditPen /></el-icon> Markdown 文章编辑</li>
            <li><el-icon><DataLine /></el-icon> 访客与流量统计</li>
            <li><el-icon><Setting /></el-icon> 站点与友链配置</li>
          </ul>
        </div>
      </aside>

      <main class="login-panel">
        <div class="login-card login-card-enter">
          <div class="login-header">
            <h2 class="login-title">博客管理系统</h2>
            <p class="login-subtitle">欢迎登录后台</p>
          </div>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            size="large"
            class="login-form"
            @keyup.enter="handleSubmit"
          >
            <el-form-item prop="username">
              <div
                class="float-field"
                :class="{
                  active: focusField === 'username' || !!form.username,
                  focused: focusField === 'username',
                }"
              >
                <label class="float-label">用户名</label>
                <el-input
                  v-model="form.username"
                  placeholder=" "
                  autocomplete="username"
                  @focus="focusField = 'username'"
                  @blur="focusField = ''"
                >
                  <template #prefix>
                    <el-icon class="field-icon"><User /></el-icon>
                  </template>
                </el-input>
                <span class="field-underline" />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div
                class="float-field"
                :class="{
                  active: focusField === 'password' || !!form.password,
                  focused: focusField === 'password',
                }"
              >
                <label class="float-label">密码</label>
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder=" "
                  show-password
                  autocomplete="current-password"
                  @focus="focusField = 'password'"
                  @blur="focusField = ''"
                >
                  <template #prefix>
                    <el-icon class="field-icon"><Lock /></el-icon>
                  </template>
                </el-input>
                <span class="field-underline" />
              </div>
            </el-form-item>

            <div class="login-options">
              <label class="remember-check">
                <input v-model="rememberMe" type="checkbox" class="remember-input" />
                <span class="remember-box">
                  <svg class="remember-icon" viewBox="0 0 12 10" aria-hidden="true">
                    <polyline points="1 5 4.5 8.5 11 1" />
                  </svg>
                </span>
                <span class="remember-text">记住我</span>
              </label>
            </div>

            <el-form-item class="submit-item">
              <button
                type="button"
                class="login-btn"
                :class="{ loading }"
                :disabled="loading"
                @click="onLoginClick"
              >
                <span v-if="loading" class="btn-spinner" />
                <span class="btn-text">{{ loading ? '登录中...' : '登 录' }}</span>
                <span
                  v-for="ripple in ripples"
                  :key="ripple.id"
                  class="btn-ripple"
                  :style="{ left: `${ripple.x}px`, top: `${ripple.y}px` }"
                />
              </button>
            </el-form-item>
          </el-form>

          <div class="login-divider">
            <span>快捷访问</span>
          </div>

          <div class="oauth-row">
            <a
              href="https://github.com/telF4"
              target="_blank"
              rel="noopener noreferrer"
              class="oauth-btn"
              title="GitHub"
            >
              <span class="oauth-circle">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    fill="currentColor"
                    d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"
                  />
                </svg>
              </span>
            </a>
            <a
              href="https://gitee.com/DollMeowOnly"
              target="_blank"
              rel="noopener noreferrer"
              class="oauth-btn oauth-btn--gitee"
              title="Gitee"
            >
              <span class="oauth-circle">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    fill="currentColor"
                    d="M12 2C6.475 2 2 6.475 2 12s4.475 10 10 10 10-4.475 10-10S17.525 2 12 2zm4.89 6.883a.75.75 0 00-1.178-.92l-3.713 4.605-2.077-2.077a.75.75 0 00-1.06 1.06l2.625 2.625a.75.75 0 001.06 0l4.243-5.253z"
                  />
                </svg>
              </span>
            </a>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, EditPen, DataLine, Setting } from '@element-plus/icons-vue'
import ParticleBackground from '@/components/ParticleBackground.vue'
import BrandLogo from '@/components/BrandLogo.vue'
import { useAuthStore } from '@/stores/auth'

const REMEMBER_KEY = 'admin_remember_username'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)
const focusField = ref('')
const parallax = reactive({ x: 0, y: 0 })
const ripples = ref<{ id: number; x: number; y: number }[]>([])

const form = reactive({
  username: '',
  password: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const brandParallaxStyle = computed(() => ({
  transform: `translate(${parallax.x * 8}px, ${parallax.y * 8}px)`,
}))

function onPageMouseMove(e: MouseEvent) {
  const x = (e.clientX / window.innerWidth - 0.5) * 2
  const y = (e.clientY / window.innerHeight - 0.5) * 2
  parallax.x = x
  parallax.y = y
}

function spawnRipple(e: MouseEvent) {
  const btn = e.currentTarget as HTMLElement
  const rect = btn.getBoundingClientRect()
  const id = Date.now()
  ripples.value.push({ id, x: e.clientX - rect.left, y: e.clientY - rect.top })
  setTimeout(() => {
    ripples.value = ripples.value.filter((r) => r.id !== id)
  }, 650)
}

async function handleSubmit() {
  if (!formRef.value || loading.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form)
    if (rememberMe.value) {
      localStorage.setItem(REMEMBER_KEY, form.username)
    } else {
      localStorage.removeItem(REMEMBER_KEY)
    }
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } catch {
    ElMessage.error('登录失败，请检查账号密码或后端服务')
  } finally {
    loading.value = false
  }
}

function onLoginClick(e: MouseEvent) {
  spawnRipple(e)
  handleSubmit()
}

onMounted(() => {
  const saved = localStorage.getItem(REMEMBER_KEY)
  if (saved) {
    form.username = saved
    rememberMe.value = true
  }
})
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #0b1224 0%, #1e3a8a 40%, #3730a3 72%, #4f46e5 100%);
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.login-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
  background-size: 52px 52px;
  mask-image: radial-gradient(ellipse 80% 70% at 30% 50%, black 20%, transparent 75%);
}

.login-bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  transition: transform 0.5s ease;
}

.login-bg-orb--1 {
  width: 480px;
  height: 480px;
  background: #6366f1;
  top: -140px;
  left: -100px;
  opacity: 0.5;
}

.login-bg-orb--2 {
  width: 380px;
  height: 380px;
  background: #0ea5e9;
  bottom: -120px;
  left: 20%;
  opacity: 0.35;
}

.login-bg-orb--3 {
  width: 300px;
  height: 300px;
  background: #a78bfa;
  top: 35%;
  left: 28%;
  opacity: 0.28;
}

.login-shell {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
}

/* ---- 左侧品牌 ---- */
.login-brand {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  padding: 60px 72px;
  color: #fff;
  transition: transform 0.45s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.brand-content {
  position: relative;
  z-index: 2;
  max-width: 480px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 36px;
}

.brand-name {
  display: block;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.brand-tag {
  display: block;
  font-size: 12px;
  color: rgba(186, 210, 255, 0.75);
  margin-top: 2px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.brand-headline {
  margin: 0 0 10px;
  font-size: 36px;
  font-weight: 700;
  line-height: 1.25;
  letter-spacing: 0.02em;
}

.brand-slogan {
  margin: 0 0 18px;
  font-size: 15px;
  font-weight: 400;
  color: #b0c4de;
  letter-spacing: 0.06em;
}

.brand-desc {
  font-size: 15px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.65);
  margin: 0 0 36px;
}

.brand-features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.brand-features li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.82);
}

.brand-features .el-icon {
  font-size: 18px;
  color: #93c5fd;
}

/* ---- 右侧登录 ---- */
.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 56px 40px 24px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 44px 40px 36px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.12);
}

.login-card-enter {
  animation: cardEnter 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  color: #1e293b;
}

.login-subtitle {
  margin: 0;
  font-size: 14px;
  font-weight: 400;
  color: #94a3b8;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 浮动标签 + 底部高亮线 */
.float-field {
  position: relative;
  width: 100%;
  padding-bottom: 2px;
}

.float-label {
  position: absolute;
  left: 44px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 15px;
  color: #94a3b8;
  pointer-events: none;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2;
  padding: 0 4px;
}

.float-field.active .float-label {
  top: -2px;
  left: 38px;
  transform: translateY(-50%) scale(0.78);
  color: #6366f1;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.92);
}

.field-underline {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  background: linear-gradient(90deg, #6366f1, #818cf8);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 2px;
}

.float-field.focused .field-underline {
  transform: scaleX(1);
}

.float-field :deep(.el-input__wrapper) {
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 12px 12px 8px 8px;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  background: rgba(248, 250, 252, 0.65);
  transition: border-color 0.25s, background 0.25s;
}

.float-field :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

.float-field.focused :deep(.el-input__wrapper) {
  border-color: rgba(99, 102, 241, 0.45);
  background: rgba(255, 255, 255, 0.95);
}

.float-field :deep(.el-input__inner) {
  height: 50px;
  font-size: 15px;
  color: #1e293b;
}

.field-icon {
  font-size: 18px;
  color: #94a3b8;
  transition: color 0.25s;
}

.float-field.focused .field-icon {
  color: #6366f1;
}

/* 自定义记住我 */
.login-options {
  margin: -6px 0 22px;
}

.remember-check {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.remember-input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.remember-box {
  width: 18px;
  height: 18px;
  border: 2px solid #cbd5e1;
  border-radius: 5px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
}

.remember-icon {
  width: 12px;
  height: 10px;
  fill: none;
  stroke: #fff;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 16;
  stroke-dashoffset: 16;
  transition: stroke-dashoffset 0.3s ease;
}

.remember-input:checked + .remember-box {
  background: linear-gradient(135deg, #6366f1, #818cf8);
  border-color: #6366f1;
  transform: scale(1.05);
}

.remember-input:checked + .remember-box .remember-icon {
  stroke-dashoffset: 0;
}

.remember-text {
  font-size: 14px;
  color: #64748b;
}

.submit-item {
  margin-bottom: 0 !important;
}

.login-btn {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 50px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.14em;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 50%, #818cf8 100%);
  box-shadow: 0 4px 20px rgba(99, 102, 241, 0.42);
  transition: transform 0.2s, box-shadow 0.2s, filter 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover:not(:disabled) {
  filter: brightness(1.07);
  box-shadow: 0 8px 28px rgba(99, 102, 241, 0.55);
  transform: translateY(-1px);
}

.login-btn:disabled {
  opacity: 0.85;
  cursor: not-allowed;
}

.btn-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-ripple {
  position: absolute;
  width: 12px;
  height: 12px;
  margin: -6px 0 0 -6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  animation: ripple 0.65s ease-out forwards;
  pointer-events: none;
}

@keyframes ripple {
  to {
    transform: scale(28);
    opacity: 0;
  }
}

.login-divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 28px 0 22px;
  color: #94a3b8;
  font-size: 12px;
}

.login-divider::before,
.login-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(148, 163, 184, 0.35), transparent);
}

.oauth-row {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.oauth-btn {
  text-decoration: none;
}

.oauth-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #334155;
  transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.25s, color 0.25s;
}

.oauth-circle svg {
  width: 22px;
  height: 22px;
}

.oauth-btn:hover .oauth-circle {
  transform: translateY(-4px);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.15);
  color: #1e293b;
}

.oauth-btn--gitee:hover .oauth-circle {
  color: #c71d23;
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-brand {
    display: none;
  }

  .login-panel {
    padding: 24px;
  }

  .login-card {
    padding: 36px 28px 28px;
  }
}
</style>
