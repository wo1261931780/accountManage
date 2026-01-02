<template>
  <div class="platform-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="平台类型">
          <el-select
            v-model="queryForm.platformTypeId"
            placeholder="全部类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in platformTypes"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="平台名称/编码"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增平台</el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="platformCode" label="平台编码" width="120" />
        <el-table-column prop="platformName" label="平台名称" min-width="150">
          <template #default="{ row }">
            <div class="platform-name">
              <span>{{ row.platformName }}</span>
              <span v-if="row.platformNameEn" class="name-en">{{ row.platformNameEn }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="platformTypeName" label="平台类型" width="120" />
        <el-table-column label="官网" min-width="200">
          <template #default="{ row }">
            <el-link
              v-if="row.platformUrl"
              :href="row.platformUrl"
              target="_blank"
              type="primary"
            >
              {{ row.platformUrl }}
            </el-link>
            <span v-else class="text-secondary">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="countryName" label="国家/地区" width="100" />
        <el-table-column prop="accountCount" label="账号数" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑平台' : '新增平台'"
      width="550px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="平台类型" prop="platformTypeId">
          <el-select v-model="formData.platformTypeId" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in platformTypes"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="平台编码" prop="platformCode">
          <el-input v-model="formData.platformCode" placeholder="如: WECHAT" />
        </el-form-item>

        <el-form-item label="平台名称" prop="platformName">
          <el-input v-model="formData.platformName" placeholder="中文名称" />
        </el-form-item>

        <el-form-item label="英文名称">
          <el-input v-model="formData.platformNameEn" placeholder="英文名称（可选）" />
        </el-form-item>

        <el-form-item label="官网地址">
          <el-input v-model="formData.platformUrl" placeholder="https://..." />
        </el-form-item>

        <el-form-item label="国家/地区">
          <el-select v-model="formData.country" placeholder="请选择" style="width: 100%">
            <el-option label="中国" value="CN" />
            <el-option label="美国" value="US" />
            <el-option label="日本" value="JP" />
            <el-option label="韩国" value="KR" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="平台描述"
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getPlatformList, createPlatform, updatePlatform, deletePlatform,
  getAllPlatformTypes
} from '@/api/platform'
import type { Platform, PlatformType } from '@/types'

const platformTypes = ref<PlatformType[]>([])
const loading = ref(false)
const tableData = ref<Platform[]>([])

const queryForm = reactive({
  platformTypeId: undefined as number | undefined,
  keyword: '',
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  id: undefined as number | undefined,
  platformTypeId: undefined as number | undefined,
  platformCode: '',
  platformName: '',
  platformNameEn: '',
  platformUrl: '',
  country: 'CN',
  status: 1,
  description: '',
})

const formRules: FormRules = {
  platformTypeId: [{ required: true, message: '请选择平台类型', trigger: 'change' }],
  platformCode: [{ required: true, message: '请输入平台编码', trigger: 'blur' }],
  platformName: [{ required: true, message: '请输入平台名称', trigger: 'blur' }],
}

async function loadPlatformTypes() {
  try {
    const res = await getAllPlatformTypes()
    if (res.code === 200) {
      platformTypes.value = res.data
    }
  } catch (error) {
    console.error('加载平台类型失败:', error)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPlatformList({
      current: pagination.current,
      size: pagination.size,
      ...queryForm,
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  loadData()
}

function handleReset() {
  queryForm.platformTypeId = undefined
  queryForm.keyword = ''
  handleSearch()
}

function handleSizeChange(size: number) {
  pagination.size = size
  loadData()
}

function handleCurrentChange(current: number) {
  pagination.current = current
  loadData()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(formData, {
    id: undefined,
    platformTypeId: undefined,
    platformCode: '',
    platformName: '',
    platformNameEn: '',
    platformUrl: '',
    country: 'CN',
    status: 1,
    description: '',
  })
  dialogVisible.value = true
}

function handleEdit(row: Platform) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    platformTypeId: row.platformTypeId,
    platformCode: row.platformCode,
    platformName: row.platformName,
    platformNameEn: row.platformNameEn,
    platformUrl: row.platformUrl,
    country: row.country || 'CN',
    status: row.status,
    description: row.description,
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
        await updatePlatform(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createPlatform(formData)
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

async function handleDelete(row: Platform) {
  try {
    await ElMessageBox.confirm(`确定要删除平台"${row.platformName}"吗？`, '提示', {
      type: 'warning',
    })
    await deletePlatform(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadPlatformTypes()
  loadData()
})
</script>

<style lang="scss" scoped>
.platform-container {
  min-height: 100%;
}

.search-card,
.action-card {
  margin-bottom: 16px;
}

.search-card :deep(.el-card__body) {
  padding-bottom: 0;
}

.platform-name {
  .name-en {
    font-size: 12px;
    color: #909399;
    margin-left: 8px;
  }
}

.text-secondary {
  color: #909399;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
