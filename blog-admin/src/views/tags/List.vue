<template>
  <el-card shadow="never" class="page-card">
    <div class="page-header">
      <div class="page-title">标签管理</div>
      <el-button type="primary" @click="openDialog()">新增标签</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="color" label="颜色" width="120">
        <template #default="{ row }">
          <el-tag :color="row.color" effect="dark">{{ row.color || '默认' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="articleCount" label="文章数" width="100" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑标签' : '新增标签'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-input v-model="form.color" placeholder="#409EFF" />
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
import { createTag, deleteTag, getTagList, updateTag } from '@/api/tag'
import type { Tag } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const visible = ref(false)
const list = ref<Tag[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  name: '',
  color: '#409EFF'
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    list.value = await getTagList()
  } finally {
    loading.value = false
  }
}

const openDialog = (row?: Tag) => {
  form.id = row?.id
  form.name = row?.name || ''
  form.color = row?.color || '#409EFF'
  visible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (form.id) {
      await updateTag(form)
      ElMessage.success('更新成功')
    } else {
      await createTag(form)
      ElMessage.success('创建成功')
    }
    visible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该标签吗？', '提示', { type: 'warning' })
  await deleteTag(id)
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
