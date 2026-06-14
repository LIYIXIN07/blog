<template>
  <div class="about-page" v-loading="loading">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="标题" prop="title" class="title-item">
        <el-input v-model="form.title" placeholder="请输入标题" />
      </el-form-item>

      <el-row :gutter="20" class="meta-row">
        <el-col :span="12">
          <el-form-item label="网易云歌曲ID" prop="musicId">
            <el-input
              v-model="form.musicId"
              placeholder="请输入网易云歌曲ID（可选）"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="评论开关">
            <el-switch v-model="form.commentEnabled" active-text="评论" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="正文" prop="content" class="editor-item">
        <VditorEditor
          ref="editorRef"
          v-model="form.content"
          mode="sv"
          :height="560"
          cache-id="blog-admin-about"
          placeholder="编写关于我页面内容..."
        />
      </el-form-item>

      <el-form-item class="submit-item">
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          保存
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import VditorEditor from '@/components/VditorEditor.vue'
import { getAbout, updateAbout } from '@/api/about'

const formRef = ref<FormInstance>()
const editorRef = ref<InstanceType<typeof VditorEditor>>()
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  title: '关于我',
  musicId: '',
  content: '',
  commentEnabled: true
})

const validateMusicId = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value || /^\d+$/.test(value)) {
    callback()
    return
  }
  callback(new Error('歌曲ID有误'))
}

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'change' }],
  musicId: [{ validator: validateMusicId, trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await getAbout()
    form.title = data.title || '关于我'
    form.musicId = data.musicId || ''
    form.commentEnabled = data.commentEnabled !== false
    form.content = data.content || ''
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await updateAbout({
      title: form.title,
      musicId: form.musicId || undefined,
      content: editorRef.value?.getValue() || form.content,
      commentEnabled: form.commentEnabled
    })
    ElMessage.success('保存成功')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.about-page {
  background: #fff;
  padding: 4px 0;
}

.title-item,
.meta-row {
  max-width: 50%;
}

.editor-item {
  margin-bottom: 8px;
}

.submit-item {
  margin-top: 8px;
  margin-bottom: 0;
  text-align: right;
}

.submit-item :deep(.el-form-item__content) {
  justify-content: flex-end;
}
</style>
