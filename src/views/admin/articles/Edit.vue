<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">{{ isEdit ? '编辑文章' : '新增文章' }}</h1>
      <div class="flex items-center space-x-4">
        <el-button 
          type="default"
          @click="saveDraft"
        >
          保存草稿
        </el-button>
        <el-button 
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </div>
    
    <el-form ref="articleForm" :model="form" class="bg-white rounded-lg shadow-sm p-6 space-y-6">
      <el-form-item label="文章标题" prop="title">
        <el-input 
          v-model="form.title" 
          placeholder="请输入文章标题"
          size="large"
        />
      </el-form-item>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <el-form-item label="分类" prop="categoryId">
          <el-select 
            v-model="form.categoryId" 
            placeholder="请选择分类"
            size="large"
          >
            <el-option 
              v-for="category in categories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标签">
          <el-select 
            v-model="form.tagIds" 
            placeholder="请选择标签"
            multiple
            size="large"
          >
            <el-option 
              v-for="tag in tags" 
              :key="tag.id" 
              :label="tag.name" 
              :value="tag.id" 
            />
          </el-select>
        </el-form-item>
      </div>
      
      <el-form-item label="文章摘要" prop="summary">
        <el-textarea 
          v-model="form.summary" 
          placeholder="请输入文章摘要"
          :rows="3"
        />
      </el-form-item>
      
      <el-form-item label="文章内容" prop="content">
        <div ref="editorRef" class="vditor-wrapper"></div>
      </el-form-item>
      
      <el-form-item label="封面图片" prop="coverImage">
        <div class="flex items-center space-x-4">
          <el-input 
            v-model="form.coverImage" 
            placeholder="请输入封面图片URL"
            class="flex-1"
          />
          <el-button 
            type="primary"
            @click="uploadCover"
          >
            上传图片
          </el-button>
        </div>
        <div v-if="form.coverImage" class="mt-4">
          <img :src="form.coverImage" alt="封面预览" class="max-w-xs rounded-lg" />
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { createArticle, updateArticle, getArticleDetail } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'
import type { Category, Tag, Article, ArticleRequest } from '@/types'
import { ArticleStatus } from '@/types'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const articleId = computed(() => Number(route.params.id))

const loading = ref(false)
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const editorRef = ref<HTMLElement | null>(null)
let vditor: Vditor | null = null

const form = reactive<ArticleRequest>({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: 0,
  tagIds: [],
  status: ArticleStatus.PUBLISHED
})

const initEditor = () => {
  if (!editorRef.value) return
  
  vditor = new Vditor(editorRef.value, {
    height: 600,
    mode: 'sv',
    preview: {
      theme: {
        current: 'light',
      },
    },
    toolbar: [
      'headings', 'bold', 'italic', 'strike',
      '|', 'list', 'ordered-list', 'check',
      '|', 'quote', 'code', 'code-block',
      '|', 'table', 'link', 'image',
      '|', 'undo', 'redo'
    ],
    cache: {
      enable: true,
      id: `article-${articleId.value || 'new'}`
    },
    upload: {
      url: `${import.meta.env.VITE_API_BASE_URL || '/api'}/upload`,
      fieldName: 'file',
      success: (response: any) => {
        const result = JSON.parse(response)
        return result.data.url
      }
    }
  })
}

const fetchCategories = async () => {
  try {
    categories.value = await getCategoryList()
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const fetchTags = async () => {
  try {
    tags.value = await getTagList()
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

const fetchArticle = async () => {
  if (!isEdit.value) return
  
  try {
    const article: Article = await getArticleDetail(articleId.value)
    form.title = article.title
    form.summary = article.summary
    form.content = article.content
    form.coverImage = article.coverImage
    form.categoryId = article.categoryId
    form.tagIds = article.tags.map(tag => tag.id)
    
    setTimeout(() => {
      vditor?.setValue(article.content)
    }, 100)
  } catch (error) {
    console.error('获取文章详情失败:', error)
  }
}

const validateForm = () => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return false
  }
  if (!form.categoryId) {
    ElMessage.warning('请选择分类')
    return false
  }
  if (!form.summary.trim()) {
    ElMessage.warning('请输入文章摘要')
    return false
  }
  const content = vditor?.getValue() || ''
  if (!content.trim()) {
    ElMessage.warning('请输入文章内容')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  try {
    const content = vditor?.getValue() || ''
    
    const data: ArticleRequest = {
      ...form,
      content,
      status: ArticleStatus.PUBLISHED
    }
    
    if (isEdit.value) {
      data.id = articleId.value
      await updateArticle(data)
      ElMessage.success('文章更新成功')
    } else {
      await createArticle(data)
      ElMessage.success('文章创建成功')
    }
    
    router.push('/admin/articles')
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

const saveDraft = async () => {
  loading.value = true
  try {
    const content = vditor?.getValue() || ''
    
    const data: ArticleRequest = {
      ...form,
      content,
      status: ArticleStatus.DRAFT
    }
    
    if (isEdit.value) {
      data.id = articleId.value
      await updateArticle(data)
    } else {
      await createArticle(data)
    }
    
    ElMessage.success('草稿保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

const uploadCover = () => {
  ElMessage.info('图片上传功能需要后端支持')
}

onMounted(() => {
  initEditor()
  fetchCategories()
  fetchTags()
  
  if (isEdit.value) {
    fetchArticle()
  }
})

watch(form.tagIds, (newVal) => {
  console.log('Selected tags:', newVal)
})
</script>

<style scoped>
.vditor-wrapper :deep(.vditor-reset) {
  min-height: 500px;
}
</style>