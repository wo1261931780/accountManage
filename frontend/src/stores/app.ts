import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏折叠状态
  const sidebarCollapsed = ref(false)

  // 暗黑模式
  const isDarkMode = ref(localStorage.getItem('darkMode') === 'true')

  // 当前激活的菜单
  const activeMenu = ref('')

  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 切换暗黑模式
  function toggleDarkMode() {
    isDarkMode.value = !isDarkMode.value
    localStorage.setItem('darkMode', String(isDarkMode.value))
    updateDarkMode()
  }

  // 更新暗黑模式
  function updateDarkMode() {
    if (isDarkMode.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 设置激活菜单
  function setActiveMenu(menu: string) {
    activeMenu.value = menu
  }

  // 初始化
  function init() {
    updateDarkMode()
  }

  return {
    sidebarCollapsed,
    isDarkMode,
    activeMenu,
    toggleSidebar,
    toggleDarkMode,
    setActiveMenu,
    init,
  }
})
