<template>
  <el-card shadow="never" class="page-card" v-loading="loading">
    <div class="page-header">
      <div class="page-title">修改账户</div>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </div>

    <el-form ref="formRef" :model="form" label-width="100px" style="max-width: 560px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="头像">
        <el-input v-model="form.avatar" placeholder="头像 URL" />
      </el-form-item>

      <el-divider>修改密码（可选）</el-divider>

      <el-form-item label="原密码">
        <el-input v-model="form.oldPassword" type="password" show-password placeholder="不修改密码可留空" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" show-password placeholder="不修改密码可留空" />
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '@/api/user'
import { updateAccount } from '@/api/system'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  oldPassword: '',
  newPassword: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const user = await getCurrentUser()
    form.username = user.username
    form.nickname = user.nickname
    form.email = user.email || ''
    form.avatar = user.avatar || ''
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await updateAccount({
      nickname: form.nickname,
      email: form.email,
      avatar: form.avatar,
      oldPassword: form.oldPassword || undefined,
      newPassword: form.newPassword || undefined
    })
    authStore.updateUserInfo({
      nickname: form.nickname,
      email: form.email,
      avatar: form.avatar
    })
    form.oldPassword = ''
    form.newPassword = ''
    ElMessage.success('账户信息已更新')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-card { border: none; }
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
</style>
