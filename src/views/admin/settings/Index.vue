<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">系统设置</h1>
      <el-button 
        type="primary"
        :loading="saving"
        @click="handleSave"
      >
        <Save class="w-5 h-5 mr-1" />
        保存设置
      </el-button>
    </div>
    
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-6 flex items-center">
          <Globe class="w-5 h-5 mr-2 text-blue-500" />
          网站基本信息
        </h3>
        
        <el-form :model="settings" class="space-y-6">
          <el-form-item label="网站名称">
            <el-input 
              v-model="settings.siteName" 
              placeholder="请输入网站名称"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="网站描述">
            <el-textarea 
              v-model="settings.siteDescription" 
              placeholder="请输入网站描述"
              :rows="3"
            />
          </el-form-item>

          <el-form-item label="ICP备案号">
            <el-input
              v-model="settings.icp"
              placeholder="例如：赣ICP备2026013392号-1"
              size="large"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-6 flex items-center">
          <User class="w-5 h-5 mr-2 text-blue-500" />
          博主信息
        </h3>
        
        <el-form :model="settings" class="space-y-6">
          <el-form-item label="博主名称">
            <el-input 
              v-model="settings.authorName" 
              placeholder="请输入博主名称"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="博主简介">
            <el-textarea 
              v-model="settings.authorBio" 
              placeholder="请输入博主简介"
              :rows="3"
            />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input 
              v-model="settings.email" 
              placeholder="请输入邮箱地址"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="GitHub">
            <el-input 
              v-model="settings.github" 
              placeholder="请输入GitHub链接"
              size="large"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <div class="mt-6 bg-white rounded-lg shadow-sm p-6">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-bold text-gray-800 flex items-center">
          <Link class="w-5 h-5 mr-2 text-blue-500" />
          友情链接
        </h3>
        <el-button 
          type="primary"
          @click="showLinkModal = true"
        >
          <Plus class="w-5 h-5 mr-1" />
          新增链接
        </el-button>
      </div>
      
      <div v-if="settings.links.length === 0" class="text-center py-8">
        <Link class="w-12 h-12 text-gray-300 mx-auto mb-2" />
        <p class="text-gray-500">暂无友情链接</p>
      </div>
      
      <el-table v-else :data="settings.links" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="url" label="链接地址" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <button 
              class="text-blue-500 hover:text-blue-600 mr-4"
              @click="editLink(scope.row)"
            >
              <Edit class="w-5 h-5" />
            </button>
            <button 
              class="text-red-500 hover:text-red-600"
              @click="deleteLink(scope.row.id)"
            >
              <Delete class="w-5 h-5" />
            </button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <el-dialog 
      :title="isEditingLink ? '编辑友情链接' : '新增友情链接'" 
      :visible.sync="showLinkModal"
      width="400px"
    >
      <el-form :model="linkForm" class="space-y-4">
        <el-form-item label="名称" prop="name">
          <el-input 
            v-model="linkForm.name" 
            placeholder="请输入链接名称"
          />
        </el-form-item>
        <el-form-item label="链接地址" prop="url">
          <el-input 
            v-model="linkForm.url" 
            placeholder="请输入链接地址"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-textarea 
            v-model="linkForm.description" 
            placeholder="请输入描述"
            :rows="2"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showLinkModal = false">取消</el-button>
        <el-button 
          type="primary"
          @click="handleSaveLink"
        >
          {{ isEditingLink ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { DocumentChecked as Save, Platform as Globe, User, Link, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { createFriendLink, updateFriendLink, deleteFriendLink } from '@/api/user'
import type { FriendLink } from '@/types'

const saving = ref(false)
const showLinkModal = ref(false)
const isEditingLink = ref(false)

const settingsStore = useSettingsStore()

const settings = reactive({
  siteName: '',
  siteDescription: '',
  authorName: '',
  authorAvatar: '',
  authorBio: '',
  email: '',
  github: '',
  icp: '',
  links: [] as FriendLink[]
})

const linkForm = reactive({
  id: 0,
  name: '',
  url: '',
  description: ''
})

const fetchSettings = async () => {
  try {
    const data = await settingsStore.fetchSettings()
    if (data) {
      settings.siteName = data.siteName || ''
      settings.siteDescription = data.siteDescription || ''
      settings.authorName = data.authorName || ''
      settings.authorAvatar = data.authorAvatar || ''
      settings.authorBio = data.authorBio || ''
      settings.email = data.email || ''
      settings.github = data.github || ''
      settings.icp = data.icp || ''
    }
  } catch (error) {
    console.error('获取设置失败:', error)
  }
  
  try {
    settings.links = await settingsStore.fetchFriendLinks()
  } catch (error) {
    console.error('获取友情链接失败:', error)
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await settingsStore.saveSettings(settings)
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const editLink = (link: FriendLink) => {
  isEditingLink.value = true
  linkForm.id = link.id
  linkForm.name = link.name
  linkForm.url = link.url
  linkForm.description = link.description || ''
  showLinkModal.value = true
}

const deleteLink = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个友情链接吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteFriendLink(id)
    settings.links = settings.links.filter(link => link.id !== id)
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const handleSaveLink = async () => {
  if (!linkForm.name.trim()) {
    ElMessage.warning('请输入链接名称')
    return
  }
  if (!linkForm.url.trim()) {
    ElMessage.warning('请输入链接地址')
    return
  }
  
  try {
    if (isEditingLink.value) {
      await updateFriendLink(linkForm)
      const index = settings.links.findIndex(l => l.id === linkForm.id)
      if (index !== -1) {
        settings.links[index] = { ...linkForm }
      }
      ElMessage.success('友情链接更新成功')
    } else {
      const id = await createFriendLink({
        name: linkForm.name,
        url: linkForm.url,
        description: linkForm.description
      })
      settings.links.push({
        id,
        name: linkForm.name,
        url: linkForm.url,
        description: linkForm.description
      })
      ElMessage.success('友情链接创建成功')
    }
    
    showLinkModal.value = false
    resetLinkForm()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const resetLinkForm = () => {
  linkForm.id = 0
  linkForm.name = ''
  linkForm.url = ''
  linkForm.description = ''
  isEditingLink.value = false
}

onMounted(() => {
  fetchSettings()
})
</script>