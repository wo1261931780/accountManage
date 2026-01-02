<template>
  <div class="password-tools-container">
    <el-row :gutter="20">
      <!-- 密码生成器 -->
      <el-col :xs="24" :lg="12">
        <el-card class="tool-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="20" color="#409EFF"><Key /></el-icon>
              <span>密码生成器</span>
            </div>
          </template>

          <el-form label-width="100px">
            <el-form-item label="密码长度">
              <el-slider
                v-model="generateOptions.length"
                :min="8"
                :max="32"
                :marks="{ 8: '8', 16: '16', 24: '24', 32: '32' }"
                show-input
              />
            </el-form-item>

            <el-form-item label="包含字符">
              <el-checkbox-group v-model="charOptions">
                <el-checkbox label="uppercase">大写字母</el-checkbox>
                <el-checkbox label="lowercase">小写字母</el-checkbox>
                <el-checkbox label="numbers">数字</el-checkbox>
                <el-checkbox label="symbols">特殊符号</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="排除字符">
              <el-input
                v-model="generateOptions.excludeChars"
                placeholder="输入要排除的字符，如: 0O1lI"
              />
            </el-form-item>
          </el-form>

          <div class="generated-password">
            <div class="password-display">
              <span class="password-text">{{ generatedPassword || '点击生成密码' }}</span>
              <el-button
                type="primary"
                :icon="CopyDocument"
                :disabled="!generatedPassword"
                @click="copyPassword"
              >
                复制
              </el-button>
            </div>
            <el-button type="primary" size="large" @click="handleGenerate">
              <el-icon><Refresh /></el-icon>
              生成密码
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 密码强度检测 -->
      <el-col :xs="24" :lg="12">
        <el-card class="tool-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="20" color="#67C23A"><Shield /></el-icon>
              <span>密码强度检测</span>
            </div>
          </template>

          <el-form label-width="100px">
            <el-form-item label="输入密码">
              <el-input
                v-model="checkPassword"
                type="password"
                show-password
                placeholder="输入要检测的密码"
                @input="handleCheckStrength"
              />
            </el-form-item>
          </el-form>

          <div class="strength-result" v-if="strengthResult">
            <div class="strength-bar">
              <div
                class="strength-progress"
                :style="{ width: strengthResult.score * 25 + '%' }"
                :class="getStrengthClass(strengthResult.score)"
              ></div>
            </div>
            <div class="strength-level" :class="getStrengthClass(strengthResult.score)">
              强度: {{ strengthResult.level }}
            </div>

            <div class="suggestions" v-if="strengthResult.suggestions?.length">
              <div class="suggestion-title">改进建议:</div>
              <ul>
                <li v-for="(suggestion, index) in strengthResult.suggestions" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 密码过期提醒 -->
    <el-card class="expiration-card">
      <template #header>
        <div class="card-header">
          <el-icon :size="20" color="#E6A23C"><Warning /></el-icon>
          <span>密码过期提醒</span>
        </div>
      </template>

      <el-table :data="expiringPasswords" stripe>
        <el-table-column prop="platformName" label="平台" width="150" />
        <el-table-column prop="accountName" label="账号" min-width="150" />
        <el-table-column label="过期时间" width="150">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isExpired(row.expireDate) }">
              {{ row.expireDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="剩余天数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getDaysType(row.daysRemaining)">
              {{ row.daysRemaining > 0 ? row.daysRemaining + '天' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleUpdatePassword(row)">
              更新密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="expiringPasswords.length === 0" description="暂无即将过期的密码" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Key, CopyDocument, Refresh, Shield, Warning } from '@element-plus/icons-vue'
import { generatePassword, checkPasswordStrength, getPasswordExpiration } from '@/api/dashboard'
import type { PasswordStrength } from '@/types'
import dayjs from 'dayjs'

const router = useRouter()

// 生成密码
const generatedPassword = ref('')
const charOptions = ref(['uppercase', 'lowercase', 'numbers', 'symbols'])
const generateOptions = reactive({
  length: 16,
  excludeChars: '',
})

const generateOptionsComputed = computed(() => ({
  length: generateOptions.length,
  includeUppercase: charOptions.value.includes('uppercase'),
  includeLowercase: charOptions.value.includes('lowercase'),
  includeNumbers: charOptions.value.includes('numbers'),
  includeSymbols: charOptions.value.includes('symbols'),
  excludeChars: generateOptions.excludeChars,
}))

async function handleGenerate() {
  try {
    const res = await generatePassword(generateOptionsComputed.value)
    if (res.code === 200) {
      generatedPassword.value = res.data
    }
  } catch (error) {
    console.error('生成密码失败:', error)
  }
}

async function copyPassword() {
  try {
    await navigator.clipboard.writeText(generatedPassword.value)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 密码强度检测
const checkPassword = ref('')
const strengthResult = ref<PasswordStrength | null>(null)

async function handleCheckStrength() {
  if (!checkPassword.value) {
    strengthResult.value = null
    return
  }

  try {
    const res = await checkPasswordStrength(checkPassword.value)
    if (res.code === 200) {
      strengthResult.value = res.data
    }
  } catch (error) {
    console.error('检测失败:', error)
  }
}

function getStrengthClass(score: number) {
  if (score <= 1) return 'weak'
  if (score <= 2) return 'fair'
  if (score <= 3) return 'good'
  return 'strong'
}

// 密码过期
const expiringPasswords = ref<any[]>([])

async function loadExpiringPasswords() {
  try {
    const res = await getPasswordExpiration()
    if (res.code === 200) {
      expiringPasswords.value = res.data || []
    }
  } catch (error) {
    console.error('加载过期密码失败:', error)
  }
}

function isExpired(date: string) {
  return dayjs(date).isBefore(dayjs())
}

function getDaysType(days: number) {
  if (days <= 0) return 'danger'
  if (days <= 7) return 'warning'
  return 'info'
}

function handleUpdatePassword(row: any) {
  router.push({
    path: '/accounts',
    query: { keyword: row.accountName },
  })
}

onMounted(() => {
  loadExpiringPasswords()
})
</script>

<style lang="scss" scoped>
.password-tools-container {
  min-height: 100%;
}

.tool-card {
  margin-bottom: 20px;

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }
}

.generated-password {
  margin-top: 24px;

  .password-display {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 16px;

    .password-text {
      flex: 1;
      font-family: monospace;
      font-size: 18px;
      letter-spacing: 1px;
      word-break: break-all;
    }
  }

  .el-button {
    width: 100%;
  }
}

.strength-result {
  margin-top: 24px;

  .strength-bar {
    height: 8px;
    background: #ebeef5;
    border-radius: 4px;
    overflow: hidden;

    .strength-progress {
      height: 100%;
      transition: width 0.3s, background 0.3s;

      &.weak { background: #F56C6C; }
      &.fair { background: #E6A23C; }
      &.good { background: #409EFF; }
      &.strong { background: #67C23A; }
    }
  }

  .strength-level {
    margin-top: 8px;
    font-weight: 500;

    &.weak { color: #F56C6C; }
    &.fair { color: #E6A23C; }
    &.good { color: #409EFF; }
    &.strong { color: #67C23A; }
  }

  .suggestions {
    margin-top: 16px;
    padding: 12px;
    background: #fef0f0;
    border-radius: 8px;

    .suggestion-title {
      font-weight: 500;
      color: #F56C6C;
      margin-bottom: 8px;
    }

    ul {
      margin: 0;
      padding-left: 20px;

      li {
        font-size: 14px;
        color: #606266;
        margin-bottom: 4px;
      }
    }
  }
}

.expiration-card {
  margin-top: 20px;

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .text-danger {
    color: #F56C6C;
  }
}
</style>
