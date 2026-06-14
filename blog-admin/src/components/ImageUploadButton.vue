<template>
  <el-upload
    class="image-upload-button"
    :show-file-list="false"
    accept="image/*"
    :disabled="disabled || uploading"
    :http-request="handleUpload"
  >
    <slot>
      <el-button :type="buttonType" :plain="plain" :loading="uploading" :disabled="disabled">
        {{ buttonText }}
      </el-button>
    </slot>
  </el-upload>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, type UploadRequestOptions } from 'element-plus'
import { uploadImage } from '@/api/upload'

withDefaults(
  defineProps<{
    buttonText?: string
    buttonType?: '' | 'primary' | 'success' | 'warning' | 'danger' | 'info'
    plain?: boolean
    disabled?: boolean
  }>(),
  {
    buttonText: '上传图片',
    buttonType: 'primary',
    plain: true,
    disabled: false
  }
)

const emit = defineEmits<{
  success: [url: string]
  error: [message: string]
}>()

const uploading = ref(false)

const handleUpload = async (options: UploadRequestOptions) => {
  uploading.value = true
  try {
    const result = await uploadImage(options.file as File)
    emit('success', result.url)
    ElMessage.success('图片上传成功')
  } catch (error) {
    const message = error instanceof Error ? error.message : '图片上传失败'
    emit('error', message)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.image-upload-button {
  display: inline-block;
}
</style>
