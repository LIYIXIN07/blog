<template>
  <div class="article-list-page">
    <el-row class="search-row">
      <el-col :span="12">
        <el-input
          v-model="query.title"
          placeholder="请输入标题"
          clearable
          size="default"
          class="search-input"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prepend>
            <el-select
              v-model="query.categoryId"
              placeholder="请选择分类"
              clearable
              style="width: 160px"
              @change="handleSearch"
            >
              <el-option
                v-for="item in categoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="置顶" width="80" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.isTop"
            @change="(val: boolean) => handleTopChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="推荐" width="80" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.isRecommend"
            @change="(val: boolean) => handleRecommendChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="可见性" width="110" align="center">
        <template #default="{ row }">
          <el-link type="primary" :underline="false" @click="openVisibilityDialog(row)">
            <el-icon><EditPen /></el-icon>
            {{ getVisibilityLabel(row) }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="最近更新" width="170">
        <template #default="{ row }">{{ formatDate(row.updatedAt || row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="$router.push(`/articles/edit/${row.id}`)">
            编辑
          </el-button>
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

    <el-dialog v-model="visibilityVisible" title="博客可见性" width="480px">
      <el-form label-width="0" @submit.prevent>
        <el-form-item>
          <el-radio-group v-model="visibilityRadio">
            <el-radio :value="1">公开</el-radio>
            <el-radio :value="2">私密</el-radio>
            <el-radio :value="3">密码保护</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="visibilityRadio === 3" label="密码" label-width="50px">
          <el-input v-model="visForm.password" placeholder="请输入访问密码" show-password />
        </el-form-item>
        <el-form-item v-if="visibilityRadio !== 2">
          <el-row :gutter="12" class="vis-switch-row">
            <el-col :span="6">
              <el-switch v-model="visForm.appreciation" active-text="赞赏" />
            </el-col>
            <el-col :span="6">
              <el-switch v-model="visForm.isRecommend" active-text="推荐" />
            </el-col>
            <el-col :span="6">
              <el-switch v-model="visForm.commentEnabled" active-text="评论" />
            </el-col>
            <el-col :span="6">
              <el-switch v-model="visForm.isTop" active-text="置顶" />
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visibilityVisible = false">取消</el-button>
        <el-button type="primary" @click="saveVisibility">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Search, EditPen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  deleteArticle,
  getArticleList,
  updateArticleRecommend,
  updateArticleTop,
  updateArticleVisibility
} from '@/api/article'
import { getCategoryList } from '@/api/category'
import type { ArticleListItem, ArticleVisibilityRequest, Category } from '@/types'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const list = ref<ArticleListItem[]>([])
const total = ref(0)
const categoryList = ref<Category[]>([])
const visibilityVisible = ref(false)
const visibilityRadio = ref(1)
const editingArticleId = ref<number | null>(null)

const visForm = reactive({
  appreciation: false,
  isRecommend: false,
  commentEnabled: true,
  isTop: false,
  password: ''
})

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined
})

const loadCategories = async () => {
  categoryList.value = await getCategoryList()
}

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (query.title) params.title = query.title
    if (query.categoryId) params.categoryId = query.categoryId
    if (query.status !== undefined) params.status = query.status

    const result = await getArticleList(params)
    list.value = result.list.map(item => ({
      ...item,
      isTop: Boolean(item.isTop),
      isRecommend: Boolean(item.isRecommend),
      appreciation: Boolean(item.appreciation),
      commentEnabled: item.commentEnabled !== false
    }))
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const handleTopChange = async (row: ArticleListItem, val: boolean) => {
  try {
    await updateArticleTop(row.id, val)
    ElMessage.success('置顶状态已更新')
  } catch {
    row.isTop = !val
  }
}

const handleRecommendChange = async (row: ArticleListItem, val: boolean) => {
  try {
    await updateArticleRecommend(row.id, val)
    ElMessage.success('推荐状态已更新')
  } catch {
    row.isRecommend = !val
  }
}

const getVisibilityLabel = (row: ArticleListItem) => {
  if (row.status !== 1) return '私密'
  if (row.password) return '密码保护'
  return '公开'
}

const openVisibilityDialog = (row: ArticleListItem) => {
  editingArticleId.value = row.id
  visForm.appreciation = Boolean(row.appreciation)
  visForm.isRecommend = Boolean(row.isRecommend)
  visForm.commentEnabled = row.commentEnabled !== false
  visForm.isTop = Boolean(row.isTop)
  visForm.password = row.password || ''

  if (row.status !== 1) {
    visibilityRadio.value = 2
  } else if (row.password) {
    visibilityRadio.value = 3
  } else {
    visibilityRadio.value = 1
  }
  visibilityVisible.value = true
}

const saveVisibility = async () => {
  if (editingArticleId.value == null) return
  if (visibilityRadio.value === 3 && !visForm.password.trim()) {
    ElMessage.error('密码保护模式必须填写密码')
    return
  }

  const payload: ArticleVisibilityRequest = {
    appreciation: visForm.appreciation,
    isRecommend: visForm.isRecommend,
    commentEnabled: visForm.commentEnabled,
    isTop: visForm.isTop,
    published: visibilityRadio.value !== 2,
    password: visibilityRadio.value === 3 ? visForm.password.trim() : ''
  }

  if (visibilityRadio.value === 2) {
    payload.appreciation = false
    payload.isRecommend = false
    payload.commentEnabled = false
    payload.isTop = false
    payload.password = ''
  }

  await updateArticleVisibility(editingArticleId.value, payload)
  ElMessage.success('可见性已更新')
  visibilityVisible.value = false
  loadData()
}

const handleDelete = async (id: number) => {
  await deleteArticle(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  await loadCategories()
  await loadData()
})
</script>

<style scoped>
.article-list-page {
  background: #fff;
  padding: 4px 0;
}

.search-row {
  margin-bottom: 16px;
}

.search-input {
  min-width: 480px;
}

.pager {
  margin-top: 20px;
  justify-content: flex-end;
}

.vis-switch-row {
  width: 100%;
}
</style>
