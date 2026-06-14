<template>
  <div class="comment-list-page">
    <el-row class="search-row">
      <el-col :span="10">
        <el-select
          v-model="selectedPage"
          placeholder="请选择页面"
          clearable
          filterable
          style="min-width: 400px"
          @change="handleSearch"
        >
          <el-option label="关于我" value="page:1" />
          <el-option label="友人帐" value="page:2" />
          <el-option
            v-for="item in articleOptions"
            :key="item.id"
            :label="item.title"
            :value="`article:${item.id}`"
          />
        </el-select>
      </el-col>
    </el-row>

    <el-table
      :data="list"
      v-loading="loading"
      stripe
      row-key="id"
      :tree-props="{ children: 'replies' }"
      indent="0"
    >
      <el-table-column label="评论ID" prop="id" width="90" />
      <el-table-column label="头像" width="70" align="center">
        <template #default="{ row }">
          <el-avatar :size="50" shape="square" fit="cover" :src="row.avatar || defaultAvatar">
            {{ row.nickname?.slice(0, 1) || '?' }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="昵称" min-width="120" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.nickname }}
          <el-tag v-if="row.adminComment" size="small" type="warning" effect="dark" class="admin-tag">
            我
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="邮箱" prop="email" min-width="120" show-overflow-tooltip />
      <el-table-column label="网站" prop="website" min-width="120" show-overflow-tooltip />
      <el-table-column label="IP" prop="ip" width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ formatIp(row.ip) }}</template>
      </el-table-column>
      <el-table-column label="评论内容" prop="content" min-width="160" show-overflow-tooltip />
      <el-table-column label="QQ" prop="qq" width="115" show-overflow-tooltip />
      <el-table-column label="所在页面" min-width="150" show-overflow-tooltip>
        <template #default="{ row }">
          <el-link v-if="row.pageType === 1" type="success" :underline="false">关于我</el-link>
          <el-link v-else-if="row.pageType === 2" type="success" :underline="false">友人帐</el-link>
          <el-link v-else type="success" :underline="false">
            {{ row.articleTitle || `文章#${row.articleId || '-'}` }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="发表时间" width="170">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="是否公开" width="90" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.published"
            @change="(val: boolean) => togglePublished(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="邮件提醒" width="90" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.notice"
            @change="(val: boolean) => toggleNotice(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog v-model="editVisible" title="编辑评论" width="50%" :close-on-click-modal="false">
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="80px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="editForm.avatar" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="网站">
          <el-input v-model="editForm.website" />
        </el-form-item>
        <el-form-item label="IP">
          <el-input v-model="editForm.ip" />
        </el-form-item>
        <el-form-item label="评论内容" prop="content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="5"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getArticleList } from '@/api/article'
import {
  deleteComment,
  getCommentList,
  updateComment,
  updateCommentNotice,
  updateCommentPublished
} from '@/api/comment'
import type { ArticleListItem, Comment } from '@/types'
import { formatDate } from '@/utils/date'

const defaultAvatar = 'https://cravatar.cn/avatar/?d=identicon'
const loading = ref(false)
const submitting = ref(false)
const editVisible = ref(false)
const list = ref<Comment[]>([])
const total = ref(0)
const articleOptions = ref<ArticleListItem[]>([])
const formRef = ref<FormInstance>()
const selectedPage = ref<string>()

const query = reactive({ pageNum: 1, pageSize: 10 })
const filter = reactive<{ pageType?: number; articleId?: number }>({
  pageType: undefined,
  articleId: undefined
})

const editForm = reactive({
  id: 0,
  nickname: '',
  email: '',
  avatar: '',
  website: '',
  ip: '',
  content: ''
})

const rules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  content: [{ required: true, message: '请输入评论内容', trigger: 'blur' }]
}

const formatIp = (ip?: string) => {
  if (!ip) return '-'
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '127.0.0.1'
  return ip
}

const normalizeComments = (items: Comment[]): Comment[] =>
  items.map(item => ({
    ...item,
    published: item.published !== false,
    notice: Boolean(item.notice),
    replies: item.replies?.length ? normalizeComments(item.replies) : []
  }))

const applyPageFilter = () => {
  filter.pageType = undefined
  filter.articleId = undefined
  if (!selectedPage.value) return

  if (selectedPage.value.startsWith('page:')) {
    filter.pageType = Number(selectedPage.value.split(':')[1])
    return
  }

  if (selectedPage.value.startsWith('article:')) {
    filter.pageType = 0
    filter.articleId = Number(selectedPage.value.split(':')[1])
  }
}

const loadArticles = async () => {
  const result = await getArticleList({ pageNum: 1, pageSize: 200 })
  articleOptions.value = result.list
}

const loadData = async () => {
  loading.value = true
  try {
    applyPageFilter()
    const result = await getCommentList({
      ...query,
      pageType: filter.pageType,
      articleId: filter.articleId
    })
    list.value = normalizeComments(result.list)
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const togglePublished = async (row: Comment, published: boolean) => {
  try {
    await updateCommentPublished(row.id, published)
    ElMessage.success('状态已更新')
  } catch {
    row.published = !published
  }
}

const toggleNotice = async (row: Comment, notice: boolean) => {
  try {
    await updateCommentNotice(row.id, notice)
    ElMessage.success('状态已更新')
  } catch {
    row.notice = !notice
  }
}

const openEdit = (row: Comment) => {
  editForm.id = row.id
  editForm.nickname = row.nickname
  editForm.email = row.email || ''
  editForm.avatar = row.avatar || ''
  editForm.website = row.website || ''
  editForm.ip = row.ip || ''
  editForm.content = row.content
  editVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await updateComment({ ...editForm })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该评论及其回复吗？', '提示', { type: 'warning' })
  await deleteComment(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  await loadArticles()
  await loadData()
})
</script>

<style scoped>
.comment-list-page {
  background: #fff;
  padding: 4px 0;
}

.search-row {
  margin-bottom: 16px;
}

.pager {
  margin-top: 20px;
  justify-content: flex-end;
}

.admin-tag {
  margin-left: 5px;
}
</style>
