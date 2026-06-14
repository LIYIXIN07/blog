<template>
  <div class="visitor-page">
    <el-form inline class="filter-form">
      <el-form-item label="最后访问时间">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="-"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" :icon="Search" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>

    <div class="table-wrapper">
      <el-table
        :data="list"
        v-loading="loading"
        stripe
        size="small"
        border
        style="width: 100%"
        class="visitor-table"
      >
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column prop="visitorUuid" label="访客标识" min-width="150" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="120" show-overflow-tooltip />
        <el-table-column prop="ipSource" label="IP来源" min-width="130" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" min-width="110" show-overflow-tooltip />
        <el-table-column prop="browser" label="浏览器" min-width="110" show-overflow-tooltip />
        <el-table-column label="首次访问" width="158" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.firstVisit) }}</template>
        </el-table-column>
        <el-table-column label="最后访问" width="158" show-overflow-tooltip>
          <template #header>
            <span class="header-label">最后访问</span>
            <el-tooltip content="每日凌晨自动更新" placement="top">
              <el-icon class="header-tip"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <template #default="{ row }">{{ formatTime(row.lastVisit) }}</template>
        </el-table-column>
        <el-table-column label="PV" width="65" align="center">
          <template #header>
            <span class="header-label">PV</span>
            <el-tooltip content="访客总浏览量，每日凌晨自动更新" placement="top">
              <el-icon class="header-tip"><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <template #default="{ row }">{{ row.pv ?? 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="188" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button type="warning" :icon="View" size="small" @click="showAccessLogs(row)">
                查看记录
              </el-button>
              <el-popconfirm title="确定删除吗？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button type="danger" :icon="Delete" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-pagination
      class="pager"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @current-change="loadData"
      @size-change="handleSearch"
    />

    <el-dialog
      v-model="logVisible"
      :title="`访问记录 - ${currentVisitor?.visitorUuid || ''}`"
      width="960px"
      destroy-on-close
    >
      <el-table :data="accessLogs" v-loading="logLoading" stripe size="small" max-height="480">
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column prop="uri" label="访问路径" min-width="220" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
        <el-table-column prop="ipSource" label="IP来源" width="180" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" width="120" show-overflow-tooltip />
        <el-table-column prop="browser" label="浏览器" width="120" show-overflow-tooltip />
        <el-table-column label="访问时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="dialog-pager"
        v-model:current-page="logQuery.pageNum"
        v-model:page-size="logQuery.pageSize"
        :total="logTotal"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, prev, pager, next"
        background
        small
        @current-change="loadAccessLogs"
        @size-change="loadAccessLogs"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Delete, QuestionFilled, Search, View } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { deleteVisitor, getVisitorList } from '@/api/statistics'
import { getLogList } from '@/api/log'
import type { AccessLog, VisitRecord } from '@/types'

const loading = ref(false)
const list = ref<VisitRecord[]>([])
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)

const logVisible = ref(false)
const logLoading = ref(false)
const accessLogs = ref<AccessLog[]>([])
const logTotal = ref(0)
const currentVisitor = ref<VisitRecord | null>(null)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  startTime: '',
  endTime: ''
})

const logQuery = reactive({
  pageNum: 1,
  pageSize: 10
})

const formatTime = (value?: string) => {
  if (!value) return '-'
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss')
}

const loadData = async () => {
  loading.value = true
  try {
    const result = await getVisitorList({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      startTime: query.startTime || undefined,
      endTime: query.endTime || undefined
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

const handleDelete = async (id: number) => {
  await deleteVisitor(id)
  ElMessage.success('删除成功')
  loadData()
}

const loadAccessLogs = async () => {
  if (!currentVisitor.value) return
  logLoading.value = true
  try {
    const result = await getLogList<AccessLog>('access', {
      pageNum: logQuery.pageNum,
      pageSize: logQuery.pageSize,
      visitorUuid: currentVisitor.value.visitorUuid
    })
    accessLogs.value = result.list
    logTotal.value = result.total
  } finally {
    logLoading.value = false
  }
}

const showAccessLogs = async (row: VisitRecord) => {
  currentVisitor.value = row
  logQuery.pageNum = 1
  logVisible.value = true
  await loadAccessLogs()
}

onMounted(loadData)
</script>

<style scoped>
.visitor-page {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  overflow: hidden;
}

.filter-form {
  margin-bottom: 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-wrapper {
  width: 100%;
  overflow: hidden;
}

.header-label {
  vertical-align: middle;
}

.header-tip {
  margin-left: 2px;
  color: #909399;
  vertical-align: middle;
  cursor: help;
  font-size: 14px;
}

.action-cell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  white-space: nowrap;
}

.action-cell :deep(.el-button) {
  margin: 0;
  padding: 5px 8px;
}

.action-cell :deep(.el-button + .el-button) {
  margin-left: 0;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}

.dialog-pager {
  margin-top: 12px;
  justify-content: flex-end;
}

:deep(.el-pagination) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.visitor-table .el-table__body-wrapper) {
  overflow-x: auto;
}

:deep(.visitor-table .el-table-fixed-column--right) {
  box-shadow: -4px 0 8px rgba(0, 0, 0, 0.06);
}
</style>
