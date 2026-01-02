<template>
  <div class="settings-container">
    <el-tabs type="border-card">
      <el-tab-pane label="个人信息">
        <el-card>
          <el-form :model="userForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="安全设置">
        <el-card>
          <div class="security-item">
            <div class="item-info">
              <div class="item-title">登录密码</div>
              <div class="item-desc">定期更换密码可以保护账号安全</div>
            </div>
            <el-button type="primary" @click="passwordDialogVisible = true">修改密码</el-button>
          </div>

          <el-divider />

          <div class="security-item">
            <div class="item-info">
              <div class="item-title">二次验证</div>
              <div class="item-desc">开启后查看密码需要二次确认</div>
            </div>
            <el-switch v-model="settings.secondaryVerify" />
          </div>

          <el-divider />

          <div class="security-item">
            <div class="item-info">
              <div class="item-title">会话超时</div>
              <div class="item-desc">设置登录状态保持时间</div>
            </div>
            <el-select v-model="settings.sessionTimeout" style="width: 150px">
              <el-option label="30分钟" :value="30" />
              <el-option label="1小时" :value="60" />
              <el-option label="4小时" :value="240" />
              <el-option label="1天" :value="1440" />
            </el-select>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="界面设置">
        <el-card>
          <div class="setting-item">
            <div class="item-info">
              <div class="item-title">暗黑模式</div>
              <div class="item-desc">开启后使用深色主题</div>
            </div>
            <el-switch v-model="appStore.isDarkMode" @change="appStore.toggleDarkMode" />
          </div>

          <el-divider />

          <div class="setting-item">
            <div class="item-info">
              <div class="item-title">侧边栏折叠</div>
              <div class="item-desc">折叠后侧边栏仅显示图标</div>
            </div>
            <el-switch v-model="appStore.sidebarCollapsed" />
          </div>

          <el-divider />

          <div class="setting-item">
            <div class="item-info">
              <div class="item-title">每页显示条数</div>
              <div class="item-desc">列表默认显示的数据条数</div>
            </div>
            <el-select v-model="settings.pageSize" style="width: 150px">
              <el-option label="10条" :value="10" />
              <el-option label="20条" :value="20" />
              <el-option label="50条" :value="50" />
            </el-select>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="关于">
        <el-card>
          <div class="about-section">
            <div class="app-info">
              <div class="app-logo">
                <el-icon :size="64" color="#409EFF"><Key /></el-icon>
              </div>
              <h2>账号密码管理系统</h2>
              <p class="version">版本 v1.2.0</p>
              <p class="description">安全、便捷、高效的账号密码管理工具</p>
            </div>

            <el-divider />

            <div class="tech-stack">
              <h3>技术栈</h3>
              <div class="tech-items">
                <el-tag>Vue 3</el-tag>
                <el-tag>TypeScript</el-tag>
                <el-tag>Element Plus</el-tag>
                <el-tag>Spring Boot 3</el-tag>
                <el-tag>MyBatis Plus</el-tag>
                <el-tag>MySQL</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="80px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Key } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/auth'

const appStore = useAppStore()
const userStore = useUserStore()

const userForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
})

const settings = reactive({
  secondaryVerify: true,
  sessionTimeout: 60,
  pageSize: 10,
})

const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

function handleSaveProfile() {
  ElMessage.success('保存成功')
}

async function handleChangePassword() {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
      ElMessage.success('密码修改成功，请重新登录')
      passwordDialogVisible.value = false
      userStore.logout()
    } catch (error) {
      console.error('修改密码失败:', error)
    }
  })
}

onMounted(() => {
  if (userStore.userInfo) {
    userForm.username = userStore.userInfo.username
    userForm.nickname = userStore.userInfo.nickname || ''
    userForm.email = userStore.userInfo.email || ''
    userForm.phone = userStore.userInfo.phone || ''
  }
})
</script>

<style lang="scss" scoped>
.settings-container {
  min-height: 100%;
}

.security-item,
.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;

  .item-info {
    .item-title {
      font-weight: 500;
      color: #303133;
    }

    .item-desc {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.about-section {
  .app-info {
    text-align: center;
    padding: 20px;

    .app-logo {
      margin-bottom: 16px;
    }

    h2 {
      margin: 0 0 8px;
      color: #303133;
    }

    .version {
      color: #909399;
      margin: 0 0 8px;
    }

    .description {
      color: #606266;
      margin: 0;
    }
  }

  .tech-stack {
    h3 {
      margin: 0 0 16px;
      color: #303133;
    }

    .tech-items {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
  }
}
</style>
