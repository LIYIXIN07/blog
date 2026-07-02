<template>
  <el-card shadow="never" class="page-card">
    <div class="page-header">
      <div class="page-title">{{ pageTitle }}</div>
    </div>

    <el-form inline class="filter-form">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item v-if="logType === 'access'" label="访客标识">
        <el-input
          v-model="query.visitorUuid"
          placeholder="访客 UUID"
          clearable
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="70" />

      <template v-if="logType === 'login'">
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
        <el-table-column prop="ipSource" label="IP来源" min-width="120" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" min-width="110" show-overflow-tooltip />
        <el-table-column prop="browser" label="浏览器" min-width="110" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" min-width="140" show-overflow-tooltip />
      </template>

      <template v-else-if="logType === 'operation'">
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="module" label="模块" width="120" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" width="90" />
        <el-table-column prop="uri" label="请求路径" min-width="180" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
      </template>

      <template v-else-if="logType === 'exception'">
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" width="90" />
        <el-table-column prop="uri" label="请求路径" min-width="160" show-overflow-tooltip />
        <el-table-column prop="exceptionName" label="异常类型" min-width="160" show-overflow-tooltip />
        <el-table-column prop="message" label="异常信息" min-width="180" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
      </template>

      <template v-else-if="logType === 'access'">
        <el-table-column prop="visitorUuid" label="访客标识" min-width="150" show-overflow-tooltip />
        <el-table-column prop="uri" label="访问路径" min-width="180" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
        <el-table-column prop="ipSource" label="IP来源" min-width="120" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" min-width="110" show-overflow-tooltip />
        <el-table-column prop="browser" label="浏览器" min-width="110" show-overflow-tooltip />
      </template>

      <template v-else>
        <el-table-column prop="taskName" label="任务名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="执行结果" min-width="180" show-overflow-tooltip />
        <el-table-column label="耗时" width="100" align="center">
          <template #default="{ row }">{{ row.durationMs ?? 0 }} ms</template>
        </el-table-column>
      </template>

      <el-table-column label="时间" width="170">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>

      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button
            v-if="logType === 'operation' || logType === 'exception'"
            link
            type="primary"
            @click="showDetail(row)"
          >
            详情
          </el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="loadData"
        @size-change="handleSearch"
      />
    </div>

    <el-dialog v-model="detailVisible" :title="detailTitle" width="640px">
      <pre class="detail-content">{{ detailContent }}</pre>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteLog, getLogList } from '@/api/log'
import { formatDate } from '@/utils/date'
import type {
  AccessLog,
  ExceptionLog,
  LoginLog,
  LogType,
  OperationLog,
  TaskLog
} from '@/types'

type LogRecord = LoginLog | OperationLog | ExceptionLog | AccessLog | TaskLog

const logTitles: Record<LogType, string> = {
  task: '任务日志',
  login: '登录日志',
  operation: '操作日志',
  exception: '异常日志',
  access: '访问日志'
}

const route = useRoute()
const loading = ref(false)
const list = ref<LogRecord[]>([])
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)
const detailVisible = ref(false)
const detailTitle = ref('日志详情')
const detailContent = ref('')

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  startTime: '',
  endTime: '',
  visitorUuid: ''
})

const logType = computed<LogType>(() => {
  const type = String(route.params.type || 'login')
  if (type === 'login' || type === 'operation' || type === 'exception' || type === 'access' || type === 'task') {
    return type
  }
  return 'login'
})

const pageTitle = computed(() => logTitles[logType.value])

const loadData = async () => {
  loading.value = true
  try {
    const result = await getLogList<LogRecord>(logType.value, {
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      startTime: query.startTime || undefined,
      endTime: query.endTime || undefined,
      visitorUuid: logType.value === 'access' ? query.visitorUuid || undefined : undefined
    })
    list.value = result.list
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  query.startTime = dateRange.value?.[0] || ''
  query.endTime = dateRange.value?.[1] || ''
  loadData()
}

const resetSearch = () => {
  dateRange.value = null
  query.pageNum = 1
  query.startTime = ''
  query.endTime = ''
  query.visitorUuid = ''
  loadData()
}

const showDetail = (row: LogRecord) => {
  if (logType.value === 'operation') {
    const item = row as OperationLog
    detailTitle.value = '操作参数'
    detailContent.value = item.params || '无参数'
  } else {
    const item = row as ExceptionLog
    detailTitle.value = '异常堆栈'
    detailContent.value = item.stackTrace || item.message || '无堆栈信息'
  }
  detailVisible.value = true
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该日志吗？', '提示', { type: 'warning' })
  await deleteLog(logType.value, id)
  ElMessage.success('删除成功')
  loadData()
}

watch(
  () => route.params.type,
  () => {
    query.pageNum = 1
    query.visitorUuid = ''
    loadData()
  }
)

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

.filter-form {
  margin-bottom: 16px;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  margin: 0;
  max-height: 420px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  line-height: 1.6;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
}
</style>
