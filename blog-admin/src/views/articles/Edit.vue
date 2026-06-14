<template>
  <div class="editor-page" v-loading="loading">
    <div class="editor-header">
      <div>
        <h2 class="editor-title">{{ isEdit ? '编辑文章' : '写文章' }}</h2>
        <p class="editor-subtitle">正文支持 Markdown，可用工具栏插入图片；首图建议上传至图床</p>
      </div>
      <div class="header-actions">
        <el-button @click="router.push('/articles')">返回</el-button>
        <el-button :loading="submitting" @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitting" @click="openPublishDialog">
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="editor-form"
      @submit.prevent
    >
      <section class="editor-section">
        <el-row :gutter="20">
          <el-col :xs="24" :lg="16">
            <el-form-item label="文章标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入标题"
                maxlength="200"
                show-word-limit
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :lg="8">
            <el-form-item label="文章首图" prop="coverImage">
              <div class="cover-input-row">
                <el-input
                  v-model="form.coverImage"
                  maxlength="2048"
                  show-word-limit
                  placeholder="上传图片或粘贴 .jpg / .png 直链"
                />
                <ImageUploadButton button-text="上传首图" @success="onCoverUploaded" />
              </div>
              <div class="field-tip field-tip-left">
                推荐使用「上传首图」或侧边栏「图床管理」配置后上传；搜索页链接无法作为封面
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="meta-row">
          <el-col :xs="24" :md="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" filterable style="width: 100%">
                <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="标签" prop="tagIds">
              <el-select
                v-model="form.tagIds"
                multiple
                filterable
                collapse-tags
                collapse-tags-tooltip
                placeholder="请选择标签"
                style="width: 100%"
              >
                <el-option v-for="item in tags" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div v-if="form.coverImage" class="cover-preview">
          <img :src="form.coverImage" alt="首图预览" referrerpolicy="no-referrer" @error="onCoverError" />
        </div>
      </section>

      <section class="editor-section">
        <el-form-item label="文章摘要" prop="summary" class="summary-field">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="5"
            maxlength="500"
            show-word-limit
            placeholder="简短介绍文章，会显示在列表页（建议 100 字以内，勿粘贴整篇正文）"
          />
          <div class="field-tip">
            <el-button link type="primary" @click="generateSummaryFromContent">从正文自动生成摘要</el-button>
          </div>
        </el-form-item>
      </section>

      <section class="editor-section editor-section-main">
        <el-form-item label="文章正文" prop="content" class="editor-field">
          <VditorEditor
            ref="contentEditorRef"
            v-model="form.content"
            :height="contentEditorHeight"
            placeholder="开始撰写正文..."
            :cache-id="`article-content-${articleId || 'new'}`"
            @update:model-value="validateContent"
          />
        </el-form-item>
      </section>
    </el-form>

    <el-dialog v-model="publishVisible" title="发布设置" width="420px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" active-text="置顶文章" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="publishStatus">
            <el-radio :label="1">公开发布</el-radio>
            <el-radio :label="0">保存草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmPublish">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import VditorEditor from '@/components/VditorEditor.vue'
import ImageUploadButton from '@/components/ImageUploadButton.vue'
import { createArticle, getArticleDetail, updateArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'
import { normalizeMarkdownContent, extractDirectImageUrl } from '@/utils/markdown'
import type { Category, Tag } from '@/types'

const SUMMARY_MAX = 500
const COVER_IMAGE_MAX = 2048

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const contentEditorRef = ref<InstanceType<typeof VditorEditor>>()
const loading = ref(false)
const submitting = ref(false)
const publishVisible = ref(false)
const publishStatus = ref(1)
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const viewportHeight = ref(typeof window !== 'undefined' ? window.innerHeight : 900)

const articleId = computed(() => (route.params.id ? Number(route.params.id) : null))
const isEdit = computed(() => !!articleId.value)

const contentEditorHeight = computed(() => {
  const dynamic = viewportHeight.value - 360
  return Math.max(560, Math.min(dynamic, 880))
})

const form = reactive({
  id: undefined as number | undefined,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  status: 0,
  isTop: false
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  summary: [
    { required: true, message: '请输入文章摘要', trigger: 'blur' },
    { max: SUMMARY_MAX, message: `摘要不能超过 ${SUMMARY_MAX} 字`, trigger: 'blur' }
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入文章正文', trigger: 'blur' }],
  coverImage: [
    {
      max: COVER_IMAGE_MAX,
      message: `首图 URL 不能超过 ${COVER_IMAGE_MAX} 个字符`,
      trigger: 'blur'
    }
  ]
}

const stripMarkdown = (text: string) =>
  text
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`[^`]*`/g, ' ')
    .replace(/!\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/\[[^\]]*]\([^)]*\)/g, '$1')
    .replace(/[#>*\-_~]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()

const syncContent = () => {
  form.content = contentEditorRef.value?.getValue() || form.content
}

const normalizeContent = () => {
  syncContent()
  const normalized = normalizeMarkdownContent(form.content)
  if (normalized !== form.content) {
    form.content = normalized
    contentEditorRef.value?.setValue(normalized)
  }
}

const validateContent = () => {
  syncContent()
  formRef.value?.validateField('content').catch(() => undefined)
}

const generateSummaryFromContent = () => {
  syncContent()
  if (!form.content.trim()) {
    ElMessage.warning('请先填写正文')
    return
  }
  const plain = stripMarkdown(form.content)
  form.summary = plain.length > SUMMARY_MAX ? `${plain.slice(0, SUMMARY_MAX - 3)}...` : plain
  formRef.value?.validateField('summary').catch(() => undefined)
  ElMessage.success('已从正文生成摘要')
}

const onResize = () => {
  viewportHeight.value = window.innerHeight
}

const loadOptions = async () => {
  const [categoryList, tagList] = await Promise.all([getCategoryList(), getTagList()])
  categories.value = categoryList
  tags.value = tagList
}

const loadDetail = async () => {
  if (!isEdit.value || !articleId.value) return
  loading.value = true
  try {
    const data = await getArticleDetail(articleId.value)
    form.id = data.id
    form.title = data.title
    form.summary = data.summary
    form.content = normalizeMarkdownContent(data.content)
    form.coverImage = data.coverImage || ''
    form.categoryId = data.categoryId
    form.tagIds = data.tags?.map((item) => item.id) || []
    form.status = data.status
    form.isTop = Boolean(data.isTop)
    publishStatus.value = data.status === 1 ? 1 : 0
    await nextTick()
    contentEditorRef.value?.setValue(form.content)
  } finally {
    loading.value = false
  }
}

const validateBeforeSubmit = async () => {
  normalizeContent()
  if (!formRef.value) return false
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return false
  if (!form.summary.trim()) {
    ElMessage.warning('请输入文章摘要')
    return false
  }
  if (form.summary.trim().length > SUMMARY_MAX) {
    ElMessage.warning(`摘要不能超过 ${SUMMARY_MAX} 字`)
    return false
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入文章正文')
    return false
  }
  return true
}

const resolveCoverImage = () => {
  const raw = form.coverImage.trim()
  if (!raw) return undefined
  return extractDirectImageUrl(raw) || raw
}

const submitArticle = async (status: number) => {
  const valid = await validateBeforeSubmit()
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      id: form.id,
      title: form.title.trim(),
      summary: form.summary.trim(),
      content: form.content.trim(),
      coverImage: resolveCoverImage(),
      categoryId: form.categoryId!,
      tagIds: form.tagIds,
      status,
      isTop: form.isTop
    }
    if (isEdit.value) {
      await updateArticle(payload)
      ElMessage.success(status === 1 ? '文章已更新' : '草稿已保存')
    } else {
      await createArticle(payload)
      ElMessage.success(status === 1 ? '文章已发布' : '草稿已保存')
    }
    router.push('/articles')
  } catch {
    // 错误提示由 request 拦截器处理
  } finally {
    submitting.value = false
    publishVisible.value = false
  }
}

const saveDraft = () => submitArticle(0)

const openPublishDialog = async () => {
  syncContent()
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!form.content.trim()) {
    ElMessage.warning('请输入文章正文')
    return
  }
  publishStatus.value = 1
  publishVisible.value = true
}

const confirmPublish = () => submitArticle(publishStatus.value)

const onCoverUploaded = (url: string) => {
  form.coverImage = url
}

const onCoverError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

onMounted(async () => {
  window.addEventListener('resize', onResize)
  await loadOptions()
  await loadDetail()
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped>
.editor-page {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 24px 28px 32px;
  min-height: calc(100vh - 88px);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.editor-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.editor-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.editor-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #909399;
}

.header-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
  flex-shrink: 0;
}

.editor-form {
  width: 100%;
}

.editor-section {
  width: 100%;
  margin-bottom: 28px;
  padding-bottom: 4px;
}

.editor-section-main {
  margin-bottom: 0;
}

.editor-form :deep(.el-form-item) {
  width: 100%;
  margin-bottom: 22px;
}

.editor-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  padding-bottom: 8px;
}

.summary-field :deep(.el-textarea__inner) {
  min-height: 120px;
  line-height: 1.7;
  font-size: 14px;
}

.field-tip {
  margin-top: 8px;
  text-align: right;
}

.field-tip-left {
  text-align: left;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.editor-field {
  margin-bottom: 0;
}

.editor-field :deep(.el-form-item__content) {
  width: 100%;
  line-height: normal;
}

.meta-row {
  margin-top: 0;
}

.cover-preview {
  margin: 4px 0 8px;
}

.cover-input-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  width: 100%;
}

.cover-input-row :deep(.el-input) {
  flex: 1;
}

.cover-preview img {
  max-width: 320px;
  max-height: 180px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  object-fit: cover;
}

@media (max-width: 768px) {
  .editor-page {
    padding: 16px;
  }

  .editor-header {
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
