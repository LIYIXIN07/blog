<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">分类管理</h1>
      <el-button 
        type="primary"
        @click="showAddModal = true"
      >
        <Plus class="w-5 h-5 mr-1" />
        新增分类
      </el-button>
    </div>
    
    <div class="bg-white rounded-lg shadow-sm overflow-hidden">
      <el-table 
        :data="categories" 
        border 
        stripe
        :loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="articleCount" label="文章数量" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <button 
              class="text-blue-500 hover:text-blue-600 mr-4"
              @click="editCategory(scope.row)"
            >
              <Edit class="w-5 h-5" />
            </button>
            <button 
              class="text-red-500 hover:text-red-600"
              @click="deleteCategory(scope.row.id)"
            >
              <Delete class="w-5 h-5" />
            </button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <el-dialog 
      :title="isEditing ? '编辑分类' : '新增分类'" 
      :visible.sync="showAddModal"
      width="400px"
    >
      <el-form :model="form" class="space-y-4">
        <el-form-item label="分类名称" prop="name">
          <el-input 
            v-model="form.name" 
            placeholder="请输入分类名称"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-textarea 
            v-model="form.description" 
            placeholder="请输入分类描述"
            :rows="3"
          />
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
import { getCategoryList, createCategory, updateCategory, deleteCategory as deleteCategoryApi } from '@/api/category'
import type { Category, CategoryRequest } from '@/types'

const categories = ref<Category[]>([])
const loading = ref(false)
const saving = ref(false)
const showAddModal = ref(false)
const isEditing = ref(false)

const form = reactive<CategoryRequest>({
  id: undefined,
  name: '',
  description: ''
})

const fetchCategories = async () => {
  loading.value = true
  try {
    categories.value = await getCategoryList()
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = []
  } finally {
    loading.value = false
  }
}

const editCategory = (category: Category) => {
  isEditing.value = true
  form.id = category.id
  form.name = category.name
  form.description = category.description || ''
  showAddModal.value = true
}

const deleteCategory = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCategoryApi(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const handleSave = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  saving.value = true
  try {
    if (isEditing.value) {
      await updateCategory(form)
      ElMessage.success('分类更新成功')
    } else {
      await createCategory(form)
      ElMessage.success('分类创建成功')
    }
    
    showAddModal.value = false
    fetchCategories()
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
  form.description = ''
  isEditing.value = false
}

onMounted(() => {
  fetchCategories()
})
</script>