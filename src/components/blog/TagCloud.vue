<template>
  <div class="nblog-widget m-box nblog-tag-cloud-widget">
    <div class="nblog-widget-header">
      <el-icon><PriceTag /></el-icon>标签云
      <span v-if="tags.length" class="nblog-tag-cloud-count">{{ tags.length }}</span>
    </div>
    <div class="nblog-tag-cloud-wrap">
      <div
        ref="bodyRef"
        class="nblog-tag-cloud-body"
        :class="{ 'nblog-tag-cloud-body--collapsed': collapsed && overflowed }"
      >
        <router-link
          v-for="(tag, index) in tags"
          :key="tag.id"
          :to="`/tag/${tag.id}`"
          class="nblog-tag-cloud-label"
          :style="{ backgroundColor: tagColor(tag, index) }"
        >
          {{ tag.name }}
        </router-link>
        <div v-if="!tags.length" class="nblog-muted">暂无标签</div>
      </div>
      <button
        v-if="overflowed"
        type="button"
        class="nblog-tag-cloud-toggle"
        @click="collapsed = !collapsed"
      >
        <el-icon>
          <ArrowDown v-if="collapsed" />
          <ArrowUp v-else />
        </el-icon>
        {{ collapsed ? `展开全部 (${tags.length})` : '收起' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { ArrowDown, ArrowUp, PriceTag } from '@element-plus/icons-vue'
import { getTagList } from '@/api/tag'
import type { Tag } from '@/types'

const COLLAPSED_HEIGHT = 118

const PALETTE = [
  '#21ba45',
  '#a5673f',
  '#f2711c',
  '#2185d0',
  '#6435c9',
  '#db2828',
  '#1b1c1d',
  '#00b5ad',
  '#b5cc18',
  '#e03997',
  '#fbbd08',
  '#767676'
]

const tags = ref<Tag[]>([])
const collapsed = ref(true)
const overflowed = ref(false)
const bodyRef = ref<HTMLElement>()

const tagColor = (tag: Tag, index: number) => tag.color || PALETTE[index % PALETTE.length]

const measureOverflow = async () => {
  await nextTick()
  const el = bodyRef.value
  if (!el || !tags.value.length) {
    overflowed.value = false
    return
  }
  overflowed.value = el.scrollHeight > COLLAPSED_HEIGHT + 8
}

onMounted(async () => {
  try {
    tags.value = await getTagList()
    await measureOverflow()
  } catch {
    tags.value = []
  }
  window.addEventListener('resize', measureOverflow)
})

onUnmounted(() => {
  window.removeEventListener('resize', measureOverflow)
})

watch(tags, measureOverflow)
</script>

<style scoped>
.nblog-tag-cloud-widget {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  overflow: hidden;
}

.nblog-widget-header {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background: rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nblog-tag-cloud-count {
  margin-left: auto;
  font-size: 12px;
  font-weight: 400;
  color: #999;
}

.nblog-tag-cloud-wrap {
  background: #ffecb3;
}

.nblog-tag-cloud-body {
  padding: 10px 10px 6px;
  max-height: 9999px;
  overflow: hidden;
  transition: max-height 0.35s ease;
}

.nblog-tag-cloud-body--collapsed {
  max-height: 118px;
  mask-image: linear-gradient(to bottom, #000 70%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, #000 70%, transparent 100%);
}

.nblog-tag-cloud-label {
  display: inline-block;
  margin: 3px;
  padding: 5px 9px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
  line-height: 1.2;
  transition: opacity 0.2s, transform 0.2s;
}

.nblog-tag-cloud-label:hover {
  opacity: 0.88;
  transform: scale(1.05);
}

.nblog-tag-cloud-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-top: 1px dashed rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.35);
  color: #666;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.nblog-tag-cloud-toggle:hover {
  background: rgba(255, 255, 255, 0.6);
  color: #333;
}

.nblog-muted {
  font-size: 13px;
  color: #999;
  padding: 8px 4px;
}
</style>
