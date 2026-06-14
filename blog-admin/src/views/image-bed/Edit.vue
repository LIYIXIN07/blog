<template>
  <el-card shadow="never" class="page-card" v-loading="loading">
    <div class="page-header">
      <div class="page-title">{{ pageTitle }}</div>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存配置</el-button>
    </div>

    <el-form label-width="120px" class="config-form">
      <el-form-item label="启用当前图床">
        <el-switch v-model="form.enabled" />
        <span class="form-tip">启用后将自动关闭其他图床</span>
      </el-form-item>

      <el-form-item v-for="field in fields" :key="field.key" :label="field.label">
        <el-input
          v-model="form.config[field.key]"
          :placeholder="field.placeholder"
          :type="field.type || 'text'"
          :show-password="field.secret"
        />
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getImageBedConfig, updateImageBedConfig } from '@/api/imageBed'

const route = useRoute()
const loading = ref(false)
const submitting = ref(false)

const type = computed(() => String(route.params.type || 'local'))

const titleMap: Record<string, string> = {
  local: '配置',
  github: 'GitHub',
  upyun: '又拍云',
  tencent: '腾讯云'
}

const pageTitle = computed(() => `${titleMap[type.value] || type.value} 图床`)

const fieldMap: Record<string, Array<{ key: string; label: string; placeholder: string; secret?: boolean }>> = {
  local: [
    { key: 'storagePath', label: '存储路径', placeholder: '/upload（URL 前缀，默认即可）' },
    { key: 'domain', label: '访问域名', placeholder: '留空则自动使用服务器地址，如 https://your-domain.com/api' }
  ],
  github: [
    { key: 'repo', label: '仓库地址', placeholder: 'username/repo' },
    { key: 'token', label: 'Token', placeholder: 'GitHub Personal Access Token', secret: true },
    { key: 'branch', label: '分支', placeholder: 'main' },
    { key: 'path', label: '存储目录', placeholder: 'images' }
  ],
  upyun: [
    { key: 'bucket', label: '服务名', placeholder: 'bucket-name' },
    { key: 'operator', label: '操作员', placeholder: 'operator' },
    { key: 'password', label: '密码', placeholder: 'operator password', secret: true },
    { key: 'domain', label: '加速域名', placeholder: 'https://xxx.upaiyun.com' }
  ],
  tencent: [
    { key: 'secretId', label: 'SecretId', placeholder: '腾讯云 SecretId', secret: true },
    { key: 'secretKey', label: 'SecretKey', placeholder: '腾讯云 SecretKey', secret: true },
    { key: 'bucket', label: '存储桶', placeholder: 'bucket-appid' },
    { key: 'region', label: '地域', placeholder: 'ap-guangzhou' },
    { key: 'domain', label: '访问域名', placeholder: 'https://xxx.cos.ap-guangzhou.myqcloud.com' }
  ]
}

const fields = computed(() => fieldMap[type.value] || [])

const form = reactive({
  enabled: false,
  config: {} as Record<string, string>
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await getImageBedConfig(type.value)
    form.enabled = data.enabled
    form.config = { ...data.config }
    fields.value.forEach((field) => {
      if (form.config[field.key] === undefined) {
        form.config[field.key] = ''
      }
    })
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await updateImageBedConfig(type.value, {
      enabled: form.enabled,
      config: form.config
    })
    ElMessage.success('保存成功')
  } finally {
    submitting.value = false
  }
}

watch(type, loadData)
onMounted(loadData)
</script>

<style scoped>
.page-card {
  border: none;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}

.config-form {
  max-width: 720px;
}

.form-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}
</style>
