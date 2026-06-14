<template>
  <el-card shadow="never" class="page-card">
    <div class="page-header">
      <div class="page-title">友链管理</div>
      <el-button type="primary" @click="openDialog()">新增友链</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="url" label="链接" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑友链' : '新增友链'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="form.url" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatar" placeholder="头像 URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { createFriendLink, deleteFriendLink, getFriendLinks, updateFriendLink } from '@/api/friendLink'
import type { FriendLink } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const visible = ref(false)
const list = ref<FriendLink[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  name: '',
  url: '',
  avatar: '',
  description: '',
  sortOrder: 0
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入链接', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    list.value = await getFriendLinks()
  } finally {
    loading.value = false
  }
}

const openDialog = (row?: FriendLink & { sortOrder?: number }) => {
  form.id = row?.id
  form.name = row?.name || ''
  form.url = row?.url || ''
  form.avatar = row?.avatar || ''
  form.description = row?.description || ''
  form.sortOrder = row?.sortOrder || 0
  visible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) {
      await updateFriendLink(form as FriendLink & { sortOrder: number })
    } else {
      await createFriendLink(form)
    }
    ElMessage.success('保存成功')
    visible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该友链吗？', '提示', { type: 'warning' })
  await deleteFriendLink(id)
  ElMessage.success('删除成功')
  loadData()
}

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
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}
</style>
