<template>
  <el-card shadow="never" class="page-card">
    <div class="page-header">
      <div class="page-title">动态管理</div>
      <el-button type="primary" @click="$router.push('/moments/create')">写动态</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
      <el-table-column label="发布状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.published" @change="(val: boolean) => togglePublished(row.id, val)" />
        </template>
      </el-table-column>
      <el-table-column prop="likes" label="点赞数" width="90" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/moments/edit/${row.id}`)">编辑</el-button>
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
        @size-change="loadData"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteMoment, getMomentList, updateMomentPublished } from '@/api/moment'
import type { Moment } from '@/types'

const loading = ref(false)
const list = ref<Moment[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const loadData = async () => {
  loading.value = true
  try {
    const result = await getMomentList(query)
    list.value = result.list
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const togglePublished = async (id: number, published: boolean) => {
  try {
    await updateMomentPublished(id, published)
    ElMessage.success('状态已更新')
  } catch {
    loadData()
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该动态吗？', '提示', { type: 'warning' })
  await deleteMoment(id)
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

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
