<template>
  <div class="comment-section m-box">
    <div class="comment-form">
      <h3 class="comment-form-title">
        发表评论
        <el-button v-if="replyTargetId" size="small" type="primary" plain @click="cancelReply">
          取消回复
        </el-button>
      </h3>

      <el-input
        v-model="form.content"
        type="textarea"
        :rows="5"
        maxlength="250"
        show-word-limit
        placeholder="评论千万条，友善第一条"
      />

      <el-form :inline="true" :model="form" class="comment-meta-form">
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="昵称（必填）">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="邮箱（必填）">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.website" placeholder="https://（可选）">
            <template #prefix><el-icon><Link /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="订阅回复">
          <el-switch v-model="form.notice" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">发表评论</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="comment-divider" />

    <div class="comment-list-header">
      <h3>
        Comments | 共 {{ stats.allComment }} 条评论
        <span v-if="stats.closeComment > 0">（{{ stats.closeComment }} 条评论被隐藏）</span>
      </h3>
      <p v-if="stats.allComment === 0" class="comment-empty-tip">快来抢沙发！</p>
    </div>

    <div v-loading="loading" class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <img :src="comment.avatar" class="comment-avatar" alt="" />
        <div class="comment-body">
          <div class="comment-meta">
            <a
              class="comment-nickname"
              :href="comment.website || undefined"
              :target="comment.website ? '_blank' : undefined"
              rel="noopener noreferrer"
            >
              {{ comment.nickname }}
            </a>
            <el-tag v-if="comment.adminComment" size="small" type="info">博主</el-tag>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
            <el-button size="small" type="primary" plain @click="setReply(comment.id, comment.nickname)">
              回复
            </el-button>
          </div>
          <div class="comment-content">{{ comment.content }}</div>

          <div v-if="comment.replies?.length" class="comment-replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="comment-item reply-item">
              <img :src="reply.avatar" class="comment-avatar" alt="" />
              <div class="comment-body">
                <div class="comment-meta">
                  <a
                    class="comment-nickname"
                    :href="reply.website || undefined"
                    :target="reply.website ? '_blank' : undefined"
                    rel="noopener noreferrer"
                  >
                    {{ reply.nickname }}
                  </a>
                  <el-tag v-if="reply.adminComment" size="small" type="info">博主</el-tag>
                  <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
                  <el-button size="small" type="primary" plain @click="setReply(comment.id, comment.nickname)">
                    回复
                  </el-button>
                </div>
                <div class="comment-content">
                  <span v-if="reply.parentNickname" class="reply-at">@{{ reply.parentNickname }} </span>
                  {{ reply.content }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="total > pageSize" class="comment-pager">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="loadComments"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Link, Message, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import {
  getPublicComments,
  submitPublicComment,
  type PublicComment
} from '@/api/comment'

const props = defineProps<{
  pageType: number
  articleId?: number
}>()

const loading = ref(false)
const submitting = ref(false)
const comments = ref<PublicComment[]>([])
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)
const replyTargetId = ref<number | null>(null)
const replyTargetName = ref('')

const stats = reactive({
  allComment: 0,
  closeComment: 0
})

const form = reactive({
  content: '',
  nickname: localStorage.getItem('comment_nickname') || '',
  email: localStorage.getItem('comment_email') || '',
  website: localStorage.getItem('comment_website') || '',
  notice: false
})

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm')

const loadComments = async () => {
  loading.value = true
  try {
    const result = await getPublicComments({
      pageType: props.pageType,
      articleId: props.articleId,
      pageNum: pageNum.value,
      pageSize
    })
    comments.value = result.comments.list
    total.value = result.comments.total
    stats.allComment = result.allComment
    stats.closeComment = result.closeComment
  } finally {
    loading.value = false
  }
}

const setReply = (parentId: number, nickname: string) => {
  replyTargetId.value = parentId
  replyTargetName.value = nickname
  form.content = `@${nickname} `
}

const cancelReply = () => {
  replyTargetId.value = null
  replyTargetName.value = ''
}

const handleSubmit = async () => {
  if (!form.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!form.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  if (!form.email.trim()) {
    ElMessage.warning('请输入邮箱')
    return
  }

  submitting.value = true
  try {
    await submitPublicComment({
      content: form.content.trim(),
      nickname: form.nickname.trim(),
      email: form.email.trim(),
      website: form.website.trim() || undefined,
      notice: form.notice,
      pageType: props.pageType,
      articleId: props.articleId,
      parentId: replyTargetId.value || -1
    })
    localStorage.setItem('comment_nickname', form.nickname.trim())
    localStorage.setItem('comment_email', form.email.trim())
    localStorage.setItem('comment_website', form.website.trim())
    ElMessage.success('评论成功')
    form.content = ''
    cancelReply()
    pageNum.value = 1
    await loadComments()
  } finally {
    submitting.value = false
  }
}

onMounted(loadComments)
</script>

<style scoped>
.comment-section {
  background: #fff;
  border-radius: 4px;
  padding: 24px;
  margin-top: 20px;
}

.comment-form-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comment-meta-form {
  margin-top: 12px;
}

.comment-meta-form :deep(.el-form-item) {
  margin-bottom: 8px;
}

.comment-divider {
  height: 1px;
  background: #ebeef5;
  margin: 20px 0;
}

.comment-list-header h3 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comment-empty-tip {
  margin: 0 0 12px;
  color: #909399;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-nickname {
  font-weight: 600;
  color: #333;
  text-decoration: none;
}

.comment-time {
  color: #909399;
  font-size: 13px;
}

.comment-content {
  color: #555;
  line-height: 1.7;
  word-break: break-word;
}

.comment-replies {
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid #eef2f7;
}

.reply-item {
  padding: 12px 0;
}

.reply-at {
  color: #409eff;
}

.comment-pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
