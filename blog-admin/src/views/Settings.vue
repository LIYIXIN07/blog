<template>
  <div class="site-setting-page" v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>基础设置</span>
          </template>
          <el-form label-position="right" label-width="110px">
            <el-form-item v-for="item in typeMap.type1" :key="item.id ?? item.key" :label="item.nameZh">
              <el-input v-model="item.value" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>资料卡</span>
          </template>
          <el-form label-position="right" label-width="110px">
            <el-form-item
              v-for="item in typeMap.type2"
              :key="item.id ?? item.key"
              :label="item.nameZh"
            >
              <div v-if="item.nameEn === 'favorite'" class="favorite-row">
                <el-input v-model="item.value" class="favorite-input" />
                <el-button type="danger" @click="deleteFavorite(item)">删除</el-button>
              </div>
              <el-input v-else v-model="item.value" />
            </el-form-item>
            <el-button type="primary" @click="addFavorite">添加自定义</el-button>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="badge-section">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span>页脚徽标</span>
          </template>
          <el-form
            v-for="badge in typeMap.type3"
            :key="badge.id ?? badge.key"
            :inline="true"
            class="badge-form"
          >
            <el-form-item label="title">
              <el-input v-model="badge.value.title" />
            </el-form-item>
            <el-form-item label="url">
              <el-input v-model="badge.value.url" />
            </el-form-item>
            <el-form-item label="subject">
              <el-input v-model="badge.value.subject" />
            </el-form-item>
            <el-form-item label="value">
              <el-input v-model="badge.value.value" />
            </el-form-item>
            <el-form-item label="color">
              <el-input v-model="badge.value.color" />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="deleteBadge(badge)">删除</el-button>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="addBadge">添加 badge</el-button>
        </el-card>
      </el-col>
    </el-row>

    <div class="submit-bar">
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getSiteSettingData,
  updateSiteSettings,
  type SiteSettingBadge,
  type SiteSettingGroup,
  type SiteSettingItem
} from '@/api/siteSetting'

const loading = ref(false)
const submitting = ref(false)
const deleteIds = ref<number[]>([])

const typeMap = reactive<SiteSettingGroup>({
  type1: [],
  type2: [],
  type3: []
})

const parseBadges = (data: SiteSettingGroup) => {
  typeMap.type1 = data.type1
  typeMap.type2 = data.type2
  typeMap.type3 = data.type3.map(item => ({
    ...item,
    value: typeof item.value === 'string' ? JSON.parse(item.value as unknown as string) : item.value
  }))
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await getSiteSettingData()
    parseBadges(data)
  } finally {
    loading.value = false
  }
}

const addFavorite = () => {
  typeMap.type2.push({
    key: Date.now(),
    nameEn: 'favorite',
    nameZh: '自定义',
    type: 2,
    value: '{"title":"","content":""}'
  })
}

const addBadge = () => {
  typeMap.type3.push({
    key: Date.now(),
    nameEn: 'badge',
    nameZh: '徽标',
    type: 3,
    value: {
      title: '',
      url: '',
      subject: '',
      value: '',
      color: ''
    }
  })
}

const deleteFavorite = (favorite: SiteSettingItem) => {
  if (favorite.id) {
    deleteIds.value.push(favorite.id)
  }
  typeMap.type2 = typeMap.type2.filter(item =>
    favorite.id ? item.id !== favorite.id : item.key !== favorite.key
  )
}

const deleteBadge = (badge: SiteSettingBadge) => {
  if (badge.id) {
    deleteIds.value.push(badge.id)
  }
  typeMap.type3 = typeMap.type3.filter(item =>
    badge.id ? item.id !== badge.id : item.key !== badge.key
  )
}

const submit = async () => {
  submitting.value = true
  try {
    const settings: SiteSettingItem[] = [
      ...typeMap.type1,
      ...typeMap.type2,
      ...typeMap.type3.map(item => ({
        id: item.id,
        nameEn: item.nameEn,
        nameZh: item.nameZh,
        type: item.type,
        value: JSON.stringify(item.value)
      }))
    ]
    await updateSiteSettings(settings, deleteIds.value)
    deleteIds.value = []
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.site-setting-page {
  min-height: 400px;
}

.badge-section {
  margin-top: 20px;
}

.badge-form {
  margin-bottom: 8px;
}

.favorite-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.favorite-input {
  flex: 1;
}

.submit-bar {
  margin-top: 30px;
  text-align: right;
}
</style>
