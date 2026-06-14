<template>
  <div class="tasks-page">
    <el-form inline class="toolbar">
      <el-form-item>
        <el-button type="primary" @click="openAdd">添加</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="warning" @click="goLogPage">日志</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="任务ID" prop="id" width="80" />
      <el-table-column label="Bean" prop="beanName" min-width="160" show-overflow-tooltip />
      <el-table-column label="方法名" prop="methodName" min-width="120" show-overflow-tooltip />
      <el-table-column label="参数" prop="params" min-width="100" show-overflow-tooltip />
      <el-table-column label="Cron" prop="cronExpression" min-width="130" show-overflow-tooltip />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 1"
            @change="(val: boolean) => toggleStatus(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="290" fixed="right">
        <template #default="{ row }">
          <el-button type="warning" size="small" @click="handleRun(row.id)">执行一次</el-button>
          <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      background
      class="pager"
      @current-change="loadData"
      @size-change="handleSearch"
    />

    <el-dialog
      v-model="addVisible"
      title="添加任务"
      width="50%"
      :close-on-click-modal="false"
      @closed="resetAddForm"
    >
      <el-form ref="addFormRef" :model="addForm" :rules="rules" label-width="80px">
        <el-form-item label="Bean" prop="beanName">
          <el-input v-model="addForm.beanName" />
        </el-form-item>
        <el-form-item label="方法名" prop="methodName">
          <el-input v-model="addForm.methodName" />
        </el-form-item>
        <el-form-item label="参数" prop="params">
          <el-input v-model="addForm.params" />
        </el-form-item>
        <el-form-item label="Cron" prop="cronExpression">
          <el-input v-model="addForm.cronExpression" placeholder="0 0 2 * * ?" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="editVisible"
      title="编辑任务"
      width="50%"
      :close-on-click-modal="false"
      @closed="resetEditForm"
    >
      <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="80px">
        <el-form-item label="Bean" prop="beanName">
          <el-input v-model="editForm.beanName" />
        </el-form-item>
        <el-form-item label="方法名" prop="methodName">
          <el-input v-model="editForm.methodName" />
        </el-form-item>
        <el-form-item label="参数" prop="params">
          <el-input v-model="editForm.params" />
        </el-form-item>
        <el-form-item label="Cron" prop="cronExpression">
          <el-input v-model="editForm.cronExpression" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createScheduledTask,
  deleteScheduledTask,
  getScheduledTasks,
  runScheduledTask,
  updateScheduledTask
} from '@/api/system'
import type { ScheduledTask } from '@/types'
import { formatDate } from '@/utils/date'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const addVisible = ref(false)
const editVisible = ref(false)
const list = ref<ScheduledTask[]>([])
const total = ref(0)
const addFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()

const query = reactive({ pageNum: 1, pageSize: 10 })

const addForm = reactive({
  beanName: '',
  methodName: '',
  params: '',
  cronExpression: '',
  remark: ''
})

const editForm = reactive({
  id: 0,
  beanName: '',
  methodName: '',
  params: '',
  cronExpression: '',
  remark: ''
})

const rules: FormRules = {
  beanName: [{ required: true, message: '请输入 Bean 名称', trigger: 'blur' }],
  methodName: [{ required: true, message: '请输入方法名', trigger: 'blur' }],
  cronExpression: [{ required: true, message: '请输入 Cron 表达式', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const result = await getScheduledTasks(query)
    list.value = result.list
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const openAdd = () => {
  addVisible.value = true
}

const openEdit = (row: ScheduledTask) => {
  editForm.id = row.id
  editForm.beanName = row.beanName
  editForm.methodName = row.methodName
  editForm.params = row.params || ''
  editForm.cronExpression = row.cronExpression
  editForm.remark = row.remark || ''
  editVisible.value = true
}

const resetAddForm = () => {
  addForm.beanName = ''
  addForm.methodName = ''
  addForm.params = ''
  addForm.cronExpression = ''
  addForm.remark = ''
  addFormRef.value?.resetFields()
}

const resetEditForm = () => {
  editFormRef.value?.resetFields()
}

const handleAdd = async () => {
  if (!addFormRef.value) return
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createScheduledTask({ ...addForm })
    ElMessage.success('添加成功')
    addVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleSave = async () => {
  if (!editFormRef.value) return
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await updateScheduledTask(editForm.id, {
      beanName: editForm.beanName,
      methodName: editForm.methodName,
      params: editForm.params,
      cronExpression: editForm.cronExpression,
      remark: editForm.remark
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (row: ScheduledTask, enabled: boolean) => {
  const action = enabled ? '启用' : '暂停'
  try {
    await ElMessageBox.confirm(`确认要${action}该任务吗？`, '提示', { type: 'warning' })
    await updateScheduledTask(row.id, { status: enabled ? 1 : 0 })
    ElMessage.success(`任务已${action}`)
    loadData()
  } catch {
    // cancelled
  }
}

const handleRun = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认要立即执行一次该任务吗？', '提示', { type: 'warning' })
    await runScheduledTask(id)
    ElMessage.success('任务已执行')
    loadData()
  } catch {
    // cancelled
  }
}

const handleDelete = async (id: number) => {
  await deleteScheduledTask(id)
  ElMessage.success('删除成功')
  loadData()
}

const goLogPage = () => {
  router.push('/logs/task')
}

onMounted(loadData)
</script>

<style scoped>
.tasks-page {
  background: #fff;
  padding: 4px 0;
}

.toolbar :deep(.el-form-item) {
  margin-bottom: 0;
}

.toolbar {
  margin-bottom: 16px;
}

.pager {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
