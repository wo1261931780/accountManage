<template>
  <div class="platform-type-container">
    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增类型</el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="typeCode" label="类型编码" width="150" />
        <el-table-column prop="typeName" label="类型名称" min-width="150">
          <template #default="{ row }">
            <div class="type-name">
              <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
              <span>{{ row.typeName }}</span>
              <span v-if="row.typeNameEn" class="name-en">{{ row.typeNameEn }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑类型' : '新增类型'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="formData.typeCode" placeholder="如: SOCIAL" />
        </el-form-item>

        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="formData.typeName" placeholder="中文名称" />
        </el-form-item>

        <el-form-item label="英文名称">
          <el-input v-model="formData.typeNameEn" placeholder="英文名称（可选）" />
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>

        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAllPlatformTypes, createPlatformType, updatePlatformType, deletePlatformType
} from '@/api/platform'
import type { PlatformType } from '@/types'

const loading = ref(false)
const tableData = ref<PlatformType[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  id: undefined as number | undefined,
  typeCode: '',
  typeName: '',
  typeNameEn: '',
  sortOrder: 0,
  status: 1,
  remark: '',
})

const formRules: FormRules = {
  typeCode: [{ required: true, message: '请输入类型编码', trigger: 'blur' }],
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const res = await getAllPlatformTypes()
    if (res.code === 200) {
      tableData.value = res.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(formData, {
    id: undefined,
    typeCode: '',
    typeName: '',
    typeNameEn: '',
    sortOrder: 0,
    status: 1,
    remark: '',
  })
  dialogVisible.value = true
}

function handleEdit(row: PlatformType) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    typeCode: row.typeCode,
    typeName: row.typeName,
    typeNameEn: row.typeNameEn,
    sortOrder: row.sortOrder,
    status: row.status,
    remark: row.remark,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value && formData.id) {
        await updatePlatformType(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createPlatformType(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row: PlatformType) {
  try {
    await ElMessageBox.confirm(`确定要删除类型"${row.typeName}"吗？`, '提示', {
      type: 'warning',
    })
    await deletePlatformType(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.platform-type-container {
  min-height: 100%;
}

.action-card {
  margin-bottom: 16px;
}

.type-name {
  display: flex;
  align-items: center;
  gap: 8px;

  .name-en {
    font-size: 12px;
    color: #909399;
  }
}
</style>
