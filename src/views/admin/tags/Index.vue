<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">标签管理</h1>
      <el-button 
        type="primary"
        @click="showAddModal = true"
      >
        <Plus class="w-5 h-5 mr-1" />
        新增标签
      </el-button>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm overflow-hidden">
      <el-table 
        :data="tags" 
        border 
        stripe
        :loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="标签名称" width="200" />
        <el-table-column label="颜色" width="120">
          <template #default="scope">
            <span 
              class="inline-block w-8 h-8 rounded-full border"
              :style="{ backgroundColor: scope.row.color || '#e0f2fe' }"
            ></span>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数量" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <button 
              class="text-blue-500 hover:text-blue-600 mr-4"
              @click="editTag(scope.row)"
            >
              <Edit class="w-5 h-5" />
            </button>
            <button 
              class="text-red-500 hover:text-red-600"
              @click="deleteTag(scope.row.id)"
            >
              <Delete class="w-5 h-5" />
            </button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <el-dialog 
      :title="isEditing ? '编辑标签' : '新增标签'" 
      :visible.sync="showAddModal"
      width="400px"
    >
      <el-form :model="form" class="space-y-4">
        <el-form-item label="标签名称" prop="name">
          <el-input 
            v-model="form.name" 
            placeholder="请输入标签名称"
          />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="form.color" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button 
          type="primary"
          :loading="saving"
          @click="handleSave"
        >
          {{ isEditing ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTagList, createTag, updateTag, deleteTag as deleteTagApi } from '@/api/tag'
import type { Tag, TagRequest } from '@/types'

const tags = ref<Tag[]>([])
const loading = ref(false)
const saving = ref(false)
const showAddModal = ref(false)
const isEditing = ref(false)

const form = reactive<TagRequest>({
  id: undefined,
  name: '',
  color: '#e0f2fe'
})

const fetchTags = async () => {
  loading.value = true
  try {
    tags.value = await getTagList()
  } catch (error) {
    console.error('获取标签列表失败:', error)
    tags.value = []
  } finally {
    loading.value = false
  }
}

const editTag = (tag: Tag) => {
  isEditing.value = true
  form.id = tag.id
  form.name = tag.name
  form.color = tag.color || '#e0f2fe'
  showAddModal.value = true
}

const deleteTag = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteTagApi(id)
    ElMessage.success('删除成功')
    fetchTags()
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const handleSave = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入标签名称')
    return
  }
  
  saving.value = true
  try {
    if (isEditing.value) {
      await updateTag(form)
      ElMessage.success('标签更新成功')
    } else {
      await createTag(form)
      ElMessage.success('标签创建成功')
    }
    
    showAddModal.value = false
    fetchTags()
    resetForm()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.color = '#e0f2fe'
  isEditing.value = false
}

onMounted(() => {
  fetchTags()
})
</script>