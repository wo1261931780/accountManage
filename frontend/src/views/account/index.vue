<template>
  <div class="account-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="账号名/别名/UID/手机/邮箱"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item label="平台类型">
          <el-select
            v-model="queryForm.platformTypeId"
            placeholder="全部类型"
            clearable
            style="width: 150px"
            @change="handlePlatformTypeChange"
          >
            <el-option
              v-for="item in platformTypes"
              :key="item.id"
              :label="item.typeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="平台">
          <el-select
            v-model="queryForm.platformId"
            placeholder="全部平台"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="item in filteredPlatforms"
              :key="item.id"
              :label="item.platformName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="queryForm.accountStatus"
            placeholder="全部状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="已冻结" :value="2" />
            <el-option label="待验证" :value="3" />
            <el-option label="已注销" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="重要程度">
          <el-select
            v-model="queryForm.importanceLevel"
            placeholder="全部"
            clearable
            style="width: 120px"
          >
            <el-option label="极高" :value="4" />
            <el-option label="高" :value="3" />
            <el-option label="中" :value="2" />
            <el-option label="低" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="left">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增账号</el-button>
          <el-button
            type="danger"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
        </div>
        <div class="right">
          <el-button :icon="Download" @click="handleExport">导出</el-button>
          <el-button :icon="Upload" @click="handleImportDialog">导入</el-button>
        </div>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />

        <el-table-column label="平台" width="150">
          <template #default="{ row }">
            <div class="platform-cell">
              <span>{{ row.platformName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="账号信息" min-width="200">
          <template #default="{ row }">
            <div class="account-info">
              <div class="account-name">
                {{ row.accountName }}
                <el-tag v-if="row.isMainAccount === 1" type="primary" size="small">主账号</el-tag>
              </div>
              <div class="account-alias" v-if="row.accountAlias">
                别名：{{ row.accountAlias }}
              </div>
              <div class="account-uid" v-if="row.uid">
                UID：{{ row.uid }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="密码" width="140">
          <template #default="{ row }">
            <div class="password-cell">
              <span class="password-mask">{{ row.password || '••••••••' }}</span>
              <div class="password-actions">
                <el-tooltip content="查看密码">
                  <el-button link type="primary" :icon="View" @click="handleViewPassword(row)" />
                </el-tooltip>
                <el-tooltip content="复制密码">
                  <el-button link type="success" :icon="CopyDocument" @click="handleCopyPassword(row)" />
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="绑定信息" width="160">
          <template #default="{ row }">
            <div class="bind-info">
              <div v-if="row.bindPhone">
                <el-icon><Phone /></el-icon>
                {{ row.bindPhone }}
              </div>
              <div v-if="row.bindEmail">
                <el-icon><Message /></el-icon>
                {{ row.bindEmail }}
              </div>
              <span v-if="!row.bindPhone && !row.bindEmail" class="text-secondary">-</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.accountStatus)" size="small">
              {{ row.accountStatusText }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="重要程度" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getImportanceType(row.importanceLevel)" size="small">
              {{ row.importanceLevelText }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleToggleFavorite(row)">
              {{ row.isFavorite ? '取消收藏' : '收藏' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑账号' : '新增账号'"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="平台" prop="platformId">
              <el-select
                v-model="formData.platformId"
                placeholder="请选择平台"
                filterable
                style="width: 100%"
              >
                <el-option-group
                  v-for="group in groupedPlatforms"
                  :key="group.typeId"
                  :label="group.typeName"
                >
                  <el-option
                    v-for="platform in group.platforms"
                    :key="platform.id"
                    :label="platform.platformName"
                    :value="platform.id"
                  />
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="账号名" prop="accountName">
              <el-input v-model="formData.accountName" placeholder="登录用户名" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                show-password
                placeholder="请输入密码"
              >
                <template #append>
                  <el-button :icon="Key" @click="handleGeneratePassword">生成</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="账号别名">
              <el-input v-model="formData.accountAlias" placeholder="昵称/显示名" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="UID">
              <el-input v-model="formData.uid" placeholder="用户唯一标识" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="绑定手机">
              <el-input v-model="formData.bindPhone" placeholder="绑定的手机号" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="绑定邮箱">
              <el-input v-model="formData.bindEmail" placeholder="绑定的邮箱" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="账号状态">
              <el-select v-model="formData.accountStatus" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="已冻结" :value="2" />
                <el-option label="待验证" :value="3" />
                <el-option label="已注销" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="重要程度">
              <el-select v-model="formData.importanceLevel" style="width: 100%">
                <el-option label="低" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="高" :value="3" />
                <el-option label="极高" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="密码有效期">
              <el-input-number
                v-model="formData.passwordValidDays"
                :min="0"
                :max="365"
                placeholder="天数，0表示永不过期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="是否主账号">
              <el-switch v-model="formData.isMainAccount" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="formData.remark"
                type="textarea"
                :rows="3"
                placeholder="备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="查看密码" width="400px">
      <div class="password-dialog">
        <div class="password-display">
          <span class="password-text">{{ viewingPassword }}</span>
          <el-button type="primary" :icon="CopyDocument" @click="copyToClipboard(viewingPassword)">
            复制
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  Search, Refresh, Plus, Delete, Download, Upload,
  View, CopyDocument, Phone, Message, Key
} from '@element-plus/icons-vue'
import {
  getAccountList, createAccount, updateAccount, deleteAccount,
  batchDeleteAccounts, getAccountPassword
} from '@/api/account'
import { getAllPlatformTypes, getPlatformList } from '@/api/platform'
import { addFavorite, removeFavorite } from '@/api/dashboard'
import { generatePassword } from '@/api/dashboard'
import type { Account, AccountForm, PlatformType, Platform, AccountQuery } from '@/types'

const route = useRoute()

// 平台数据
const platformTypes = ref<PlatformType[]>([])
const platforms = ref<Platform[]>([])

const filteredPlatforms = computed(() => {
  if (!queryForm.platformTypeId) return platforms.value
  return platforms.value.filter(p => p.platformTypeId === queryForm.platformTypeId)
})

const groupedPlatforms = computed(() => {
  const groups: { typeId: number; typeName: string; platforms: Platform[] }[] = []
  platformTypes.value.forEach(type => {
    const typePlatforms = platforms.value.filter(p => p.platformTypeId === type.id)
    if (typePlatforms.length > 0) {
      groups.push({
        typeId: type.id,
        typeName: type.typeName,
        platforms: typePlatforms,
      })
    }
  })
  return groups
})

// 查询表单
const queryForm = reactive<AccountQuery>({
  current: 1,
  size: 10,
  keyword: '',
  platformTypeId: undefined,
  platformId: undefined,
  accountStatus: undefined,
  importanceLevel: undefined,
})

// 表格数据
const loading = ref(false)
const tableData = ref<Account[]>([])
const selectedIds = ref<number[]>([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 表单相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<AccountForm>({
  platformId: 0,
  accountName: '',
  password: '',
  accountAlias: '',
  uid: '',
  bindPhone: '',
  bindEmail: '',
  accountStatus: 1,
  importanceLevel: 2,
  isMainAccount: 0,
  passwordValidDays: 90,
  remark: '',
})

const formRules: FormRules = {
  platformId: [{ required: true, message: '请选择平台', trigger: 'change' }],
  accountName: [{ required: true, message: '请输入账号名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// 密码查看
const passwordDialogVisible = ref(false)
const viewingPassword = ref('')

// 状态类型
function getStatusType(status: number) {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }
  return types[status] || 'info'
}

function getImportanceType(level: number) {
  const types: Record<number, string> = { 1: 'info', 2: '', 3: 'warning', 4: 'danger' }
  return types[level] || ''
}

// 平台类型变化
function handlePlatformTypeChange() {
  queryForm.platformId = undefined
}

// 加载平台数据
async function loadPlatformData() {
  try {
    const [typesRes, platformsRes] = await Promise.all([
      getAllPlatformTypes(),
      getPlatformList({ current: 1, size: 1000 }),
    ])

    if (typesRes.code === 200) {
      platformTypes.value = typesRes.data
    }
    if (platformsRes.code === 200) {
      platforms.value = platformsRes.data.records
    }
  } catch (error) {
    console.error('加载平台数据失败:', error)
  }
}

// 加载账号列表
async function loadAccountList() {
  loading.value = true
  try {
    const res = await getAccountList({
      ...queryForm,
      current: pagination.current,
      size: pagination.size,
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载账号列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pagination.current = 1
  loadAccountList()
}

// 重置
function handleReset() {
  queryForm.keyword = ''
  queryForm.platformTypeId = undefined
  queryForm.platformId = undefined
  queryForm.accountStatus = undefined
  queryForm.importanceLevel = undefined
  handleSearch()
}

// 分页
function handleSizeChange(size: number) {
  pagination.size = size
  loadAccountList()
}

function handleCurrentChange(current: number) {
  pagination.current = current
  loadAccountList()
}

// 选择
function handleSelectionChange(rows: Account[]) {
  selectedIds.value = rows.map(row => row.id)
}

// 新增
function handleAdd() {
  isEdit.value = false
  Object.assign(formData, {
    id: undefined,
    platformId: 0,
    accountName: '',
    password: '',
    accountAlias: '',
    uid: '',
    bindPhone: '',
    bindEmail: '',
    accountStatus: 1,
    importanceLevel: 2,
    isMainAccount: 0,
    passwordValidDays: 90,
    remark: '',
  })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row: Account) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    platformId: row.platformId,
    accountName: row.accountName,
    password: '', // 编辑时密码为空，不修改则保持原样
    accountAlias: row.accountAlias,
    uid: row.uid,
    bindPhone: row.bindPhone,
    bindEmail: row.bindEmail,
    accountStatus: row.accountStatus,
    importanceLevel: row.importanceLevel,
    isMainAccount: row.isMainAccount,
    remark: row.remark,
  })
  dialogVisible.value = true
}

// 生成密码
async function handleGeneratePassword() {
  try {
    const res = await generatePassword()
    if (res.code === 200) {
      formData.password = res.data
      ElMessage.success('密码已生成')
    }
  } catch (error) {
    console.error('生成密码失败:', error)
  }
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value && formData.id) {
        await updateAccount(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createAccount(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadAccountList()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除
async function handleDelete(row: Account) {
  try {
    await ElMessageBox.confirm(`确定要删除账号"${row.accountName}"吗？`, '提示', {
      type: 'warning',
    })
    await deleteAccount(row.id)
    ElMessage.success('删除成功')
    loadAccountList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个账号吗？`, '提示', {
      type: 'warning',
    })
    await batchDeleteAccounts(selectedIds.value)
    ElMessage.success('删除成功')
    selectedIds.value = []
    loadAccountList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 查看密码
async function handleViewPassword(row: Account) {
  try {
    const res = await getAccountPassword(row.id)
    if (res.code === 200) {
      viewingPassword.value = res.data
      passwordDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取密码失败:', error)
  }
}

// 复制密码
async function handleCopyPassword(row: Account) {
  try {
    const res = await getAccountPassword(row.id)
    if (res.code === 200) {
      await copyToClipboard(res.data)
    }
  } catch (error) {
    console.error('复制密码失败:', error)
  }
}

// 复制到剪贴板
async function copyToClipboard(text: string) {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 收藏切换
async function handleToggleFavorite(row: Account & { isFavorite?: boolean }) {
  try {
    if (row.isFavorite) {
      await removeFavorite(row.id)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(row.id)
      ElMessage.success('已添加收藏')
    }
    row.isFavorite = !row.isFavorite
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 导出
function handleExport() {
  window.open('/api/v1/account/excel/export', '_blank')
}

// 导入对话框
function handleImportDialog() {
  ElMessage.info('导入功能开发中...')
}

// 初始化
onMounted(() => {
  // 检查URL参数
  if (route.query.keyword) {
    queryForm.keyword = route.query.keyword as string
  }

  loadPlatformData()
  loadAccountList()
})
</script>

<style lang="scss" scoped>
.account-container {
  min-height: 100%;
}

.search-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding-bottom: 0;
  }
}

.action-card {
  margin-bottom: 16px;

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left, .right {
      display: flex;
      gap: 8px;
    }
  }
}

.table-card {
  .platform-cell {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .account-info {
    .account-name {
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .account-alias,
    .account-uid {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }

  .password-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .password-mask {
      font-family: monospace;
      letter-spacing: 2px;
    }

    .password-actions {
      display: flex;
      gap: 4px;
    }
  }

  .bind-info {
    font-size: 12px;

    > div {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-bottom: 2px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .text-secondary {
      color: #909399;
    }
  }
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.password-dialog {
  .password-display {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;

    .password-text {
      flex: 1;
      font-family: monospace;
      font-size: 18px;
      letter-spacing: 1px;
      word-break: break-all;
    }
  }
}
</style>
