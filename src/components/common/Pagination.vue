<template>
  <div class="nblog-pagination-wrap">
    <el-pagination
      class="nblog-pagination"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  currentPage: number
  pageSize: number
  total: number
}>()

const emit = defineEmits<{
  (e: 'update:currentPage', value: number): void
  (e: 'update:pageSize', value: number): void
}>()

const handleSizeChange = (val: number) => {
  emit('update:pageSize', val)
}

const handleCurrentChange = (val: number) => {
  emit('update:currentPage', val)
}
</script>

<style scoped>
.nblog-pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 0 8px;
}

@media (max-width: 768px) {
  .nblog-pagination-wrap {
    margin-top: 20px;
  }

  .nblog-pagination-wrap :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
  }

  .nblog-pagination-wrap :deep(.el-pagination__sizes),
  .nblog-pagination-wrap :deep(.el-pagination__jump) {
    display: none;
  }

  .nblog-pagination-wrap :deep(.el-pagination__total) {
    width: 100%;
    text-align: center;
    margin: 0 0 4px;
  }
}
</style>