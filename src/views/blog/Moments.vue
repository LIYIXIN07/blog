<template>
  <div>
    <div class="nblog-page-title m-box">动态</div>

    <div v-if="loading" class="nblog-empty m-box">加载中...</div>
    <div v-else-if="!moments.length" class="nblog-empty m-box">暂无动态</div>
    <div v-else class="moment-list">
      <article v-for="item in moments" :key="item.id" class="moment-item m-box">
        <div class="moment-content">{{ item.content }}</div>
        <div class="moment-meta">
          <span>{{ formatDate(item.createdAt) }}</span>
          <span>{{ item.likes || 0 }} 赞</span>
        </div>
      </article>

      <div v-if="total > query.pageSize" class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getPublicMoments, type MomentItem } from '@/api/moment'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const moments = ref<MomentItem[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const loadData = async () => {
  loading.value = true
  try {
    const result = await getPublicMoments(query.pageNum, query.pageSize)
    moments.value = result.list
    total.value = result.total
  } catch {
    moments.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.nblog-page-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  padding: 16px 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  border-top: 3px solid #48dbfb;
}

.nblog-empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background: #fff;
  border-radius: 4px;
}

.moment-item {
  background: #fff;
  border-radius: 4px;
  padding: 20px 24px;
  margin-bottom: 16px;
}

.moment-content {
  color: #444;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.moment-meta {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
  font-size: 13px;
  color: #999;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}
</style>
