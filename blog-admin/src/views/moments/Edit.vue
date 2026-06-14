<template>
  <div class="editor-page" v-loading="loading">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="editor-form"
      @submit.prevent
    >
      <el-form-item label="动态内容" prop="content" class="editor-field">
        <VditorEditor
          ref="contentEditorRef"
          v-model="form.content"
          :height="480"
          placeholder="开始编辑..."
          :cache-id="`moment-content-${momentId || 'new'}`"
          @update:model-value="validateContent"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <el-form-item label="点赞数">
            <el-input-number v-model="form.likes" :min="0" controls-position="right" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :md="12">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="form.createdAt"
              type="datetime"
              placeholder="可选，默认此刻"
              value-format="YYYY-MM-DDTHH:mm:ss"
              :editable="false"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <div class="form-footer">
        <el-button @click="router.push('/moments')">返回</el-button>
        <el-button type="info" :loading="submitting" @click="submit(false)">仅自己可见</el-button>
        <el-button type="primary" :loading="submitting" @click="submit(true)">发布动态</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import VditorEditor from '@/components/VditorEditor.vue'
import { createMoment, getMomentDetail, updateMoment } from '@/api/moment'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const contentEditorRef = ref<InstanceType<typeof VditorEditor>>()
const loading = ref(false)
const submitting = ref(false)

const momentId = computed(() => (route.params.id ? Number(route.params.id) : null))
const isEdit = computed(() => !!momentId.value)

const form = reactive({
  content: '',
  likes: 0,
  published: true,
  createdAt: ''
})

const rules: FormRules = {
  content: [{ required: true, message: '请输入动态内容', trigger: 'blur' }]
}

const validateContent = () => {
  formRef.value?.validateField('content').catch(() => undefined)
}

const loadDetail = async () => {
  if (!momentId.value) return
  loading.value = true
  try {
    const data = await getMomentDetail(momentId.value)
    form.content = data.content
    form.likes = data.likes || 0
    form.published = data.published
    form.createdAt = data.createdAt || ''
    await nextTick()
    contentEditorRef.value?.setValue(data.content)
  } finally {
    loading.value = false
  }
}

const submit = async (published: boolean) => {
  form.content = contentEditorRef.value?.getValue() || form.content
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!form.content.trim()) {
    ElMessage.warning('请输入动态内容')
    return
  }

  submitting.value = true
  try {
    const payload = {
      id: momentId.value || undefined,
      content: form.content.trim(),
      likes: form.likes,
      published,
      createdAt: form.createdAt || undefined
    }
    if (isEdit.value) {
      await updateMoment(payload)
      ElMessage.success(published ? '动态已发布' : '已设为仅自己可见')
    } else {
      await createMoment(payload)
      ElMessage.success(published ? '动态已发布' : '已保存为私密动态')
    }
    router.push('/moments')
  } finally {
    submitting.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.editor-page {
  background: #fff;
  border-radius: 4px;
  padding: 24px;
  min-height: calc(100vh - 140px);
}

.editor-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
  padding-bottom: 8px;
}

.editor-field {
  margin-bottom: 24px;
}

.editor-field :deep(.el-form-item__content) {
  line-height: normal;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
