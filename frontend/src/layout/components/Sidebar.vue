<template>
  <div class="sidebar">
    <!-- Logo -->
    <div class="sidebar-logo" :class="{ collapsed: appStore.sidebarCollapsed }">
      <el-icon :size="32" color="#409EFF"><Key /></el-icon>
      <span v-show="!appStore.sidebarCollapsed" class="logo-text">账号管理</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar class="sidebar-menu-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-menu-item index="/accounts">
          <el-icon><User /></el-icon>
          <template #title>账号管理</template>
        </el-menu-item>

        <el-menu-item index="/platforms">
          <el-icon><Monitor /></el-icon>
          <template #title>平台管理</template>
        </el-menu-item>

        <el-menu-item index="/platform-types">
          <el-icon><Menu /></el-icon>
          <template #title>平台类型</template>
        </el-menu-item>

        <el-menu-item index="/favorites">
          <el-icon><Star /></el-icon>
          <template #title>我的收藏</template>
        </el-menu-item>

        <el-menu-item index="/password-tools">
          <el-icon><Key /></el-icon>
          <template #title>密码工具</template>
        </el-menu-item>

        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import {
  Key, Odometer, User, Monitor, Menu,
  Star, Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const appStore = useAppStore()

const activeMenu = computed(() => {
  return route.path
})
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: rgba(0, 0, 0, 0.2);

  .logo-text {
    font-size: 18px;
    font-weight: 600;
    color: #ffffff;
    white-space: nowrap;
  }

  &.collapsed {
    padding: 0;
  }
}

.sidebar-menu-wrapper {
  flex: 1;
  overflow: hidden;
}

.el-menu {
  border-right: none;

  .el-menu-item {
    height: 50px;
    line-height: 50px;

    &:hover {
      background-color: rgba(0, 0, 0, 0.1) !important;
    }

    &.is-active {
      background-color: rgba(64, 158, 255, 0.2) !important;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 3px;
        background: #409EFF;
      }
    }

    .el-icon {
      font-size: 18px;
    }
  }
}

:deep(.el-menu--collapse) {
  width: 64px;

  .el-menu-item {
    justify-content: center;
    padding: 0 !important;

    .el-icon {
      margin-right: 0;
    }
  }
}
</style>
