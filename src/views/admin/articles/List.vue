<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">文章管理</h1>
      <router-link 
        to="/admin/articles/create" 
        class="flex items-center space-x-2 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
      >
        <Plus class="w-5 h-5" />
        <span>新增文章</span>
      </router-link>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
      <div class="flex flex-wrap gap-4 items-center">
        <el-input 
          v-model="searchForm.title" 
          placeholder="搜索文章标题" 
          prefix-icon="Search"
          class="w-64"
        />
        <el-select 
          v-model="searchForm.categoryId" 
          placeholder="选择分类"
          class="w-48"
        >
          <el-option label="全部" :value="0" />
          <el-option 
            v-for="category in categories" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id" 
          />
        </el-select>
        <el-select 
          v-model="searchForm.status" 
          placeholder="选择状态"
          class="w-32"
        >
          <el-option label="全部" :value="-1" />
          <el-option label="已发布" :value="1" />
          <el-option label="草稿" :value="0" />
        </el-select>
        <el-button type="primary" @click="fetchArticles">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm overflow-hidden">
      <el-table 
        :data="articles" 
        border 
        stripe
        :loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column 
          prop="status" 
          label="状态" 
          width="80"
          :formatter="formatStatus"
        />
        <el-table-column prop="viewCount" label="阅读量" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <router-link 
              :to="`/admin/articles/edit/${scope.row.id}`" 
              class="text-blue-500 hover:text-blue-600 mr-4"
            >
              <Edit class="w-5 h-5" />
            </router-link>
            <button 
              class="text-red-500 hover:text-red-600"
              @click="handleDelete(scope.row.id)"
            >
              <Delete class="w-5 h-5" />
            </button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="px-4 py-3 flex items-center justify-between border-t bg-gray-50">
        <div class="flex items-center space-x-4">
          <el-button 
            type="danger" 
            size="small"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除 ({{ selectedIds.length }})
          </el-button>
        </div>
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticleList, deleteArticle, batchDeleteArticles } from '@/api/article'
import { getCategoryList } from '@/api/category'
import type { ArticleListItem, Category } from '@/types'

const articles = ref<ArticleListItem[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])

const searchForm = reactive({
  title: '',
  categoryId: 0,
  status: -1
})

const fetchArticles = async () => {
  loading.value = true
  try {
    const params: any = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    if (searchForm.title) params.title = searchForm.title
    if (searchForm.categoryId > 0) params.categoryId = searchForm.categoryId
    if (searchForm.status >= 0) params.status = searchForm.status
    
    const result = await getArticleList(params)
    articles.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
    articles.value = []
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    categories.value = await getCategoryList()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const resetSearch = () => {
  searchForm.title = ''
  searchForm.categoryId = 0
  searchForm.status = -1
  pageNum.value = 1
  fetchArticles()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  pageNum.value = 1
}

const handleCurrentChange = (val: number) => {
  pageNum.value = val
}

const handleSelectionChange = (val: any[]) => {
  selectedIds.value = val.map(item => item.id)
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteArticle(id)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 篇文章吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await batchDeleteArticles(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchArticles()
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const formatStatus = (row: ArticleListItem) => {
  return row.status === 1 ? '已发布' : '草稿'
}

onMounted(() => {
  fetchCategories()
  fetchArticles()
})

watch([pageNum, pageSize], () => {
  fetchArticles()
})
</script>