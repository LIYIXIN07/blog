<template>
  <div ref="editorRef" class="vditor-editor" :style="{ minHeight: `${height}px` }" />
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { uploadImage } from '@/api/upload'

const props = withDefaults(
  defineProps<{
    modelValue?: string
    height?: number
    placeholder?: string
    cacheId?: string
    mode?: 'ir' | 'sv' | 'wysiwyg'
  }>(),
  {
    modelValue: '',
    height: 360,
    placeholder: '开始编辑...',
    cacheId: 'vditor-default',
    mode: 'ir',
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: string]
  ready: []
}>()

const editorRef = ref<HTMLElement>()
let vditor: Vditor | null = null
let editorReady = false
let pendingContent = props.modelValue

const toolbar = [
  'emoji',
  'headings',
  'bold',
  'italic',
  'strike',
  'link',
  'image',
  '|',
  'list',
  'ordered-list',
  'check',
  'outdent',
  'indent',
  '|',
  'quote',
  'line',
  'code',
  'inline-code',
  'table',
  '|',
  'undo',
  'redo',
  '|',
  'edit-mode',
  'preview',
  'both',
  'fullscreen'
]

const initEditor = () => {
  if (!editorRef.value || vditor) return

  vditor = new Vditor(editorRef.value, {
    height: props.height,
    width: '100%',
    mode: props.mode,
    placeholder: props.placeholder,
    theme: 'classic',
    icon: 'material',
    cache: {
      enable: true,
      id: props.cacheId
    },
    preview: {
      theme: { current: 'light' },
      markdown: { toc: true }
    },
    toolbar,
    upload: {
      max: 10 * 1024 * 1024,
      accept: 'image/*',
      handler: (files: File[]) => {
        if (!files.length) {
          return Promise.reject(new Error('请选择图片'))
        }
        return uploadImage(files[0]).then((result) => result.url)
      }
    },
    counter: {
      enable: true,
      type: 'text'
    },
    after: () => {
      editorReady = true
      if (pendingContent) {
        vditor?.setValue(pendingContent)
        pendingContent = ''
      }
      emit('ready')
    },
    input: (value) => {
      emit('update:modelValue', value)
    }
  })
}

const getValue = () => vditor?.getValue() ?? props.modelValue ?? ''

const setValue = (value: string) => {
  if (editorReady && vditor) {
    vditor.setValue(value)
  } else {
    pendingContent = value
  }
}

watch(
  () => props.modelValue,
  (value) => {
    if (!editorReady || !vditor) {
      pendingContent = value
      return
    }
    if (value !== vditor.getValue()) {
      vditor.setValue(value)
    }
  }
)

defineExpose({ getValue, setValue })

onMounted(initEditor)

onBeforeUnmount(() => {
  vditor?.destroy()
  vditor = null
})
</script>

<style scoped>
.vditor-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  background: #fff;
}

.vditor-editor :deep(.vditor) {
  width: 100% !important;
  border: none;
}

.vditor-editor :deep(.vditor-toolbar) {
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
  padding-left: 8px;
}

.vditor-editor :deep(.vditor-content) {
  width: 100%;
}

.vditor-editor :deep(.vditor-ir__editor),
.vditor-editor :deep(.vditor-wysiwyg),
.vditor-editor :deep(.vditor-sv textarea) {
  min-height: inherit;
  padding: 16px 20px !important;
  line-height: 1.75;
  font-size: 15px;
}

.vditor-editor :deep(.vditor-reset) {
  padding: 16px 20px !important;
}
</style>
