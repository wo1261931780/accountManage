import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'

// 配置NProgress
NProgress.configure({ showSpinner: false })

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' },
      },
      {
        path: 'accounts',
        name: 'Accounts',
        component: () => import('@/views/account/index.vue'),
        meta: { title: '账号管理', icon: 'User' },
      },
      {
        path: 'platforms',
        name: 'Platforms',
        component: () => import('@/views/platform/index.vue'),
        meta: { title: '平台管理', icon: 'Platform' },
      },
      {
        path: 'platform-types',
        name: 'PlatformTypes',
        component: () => import('@/views/platform-type/index.vue'),
        meta: { title: '平台类型', icon: 'Menu' },
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/favorites/index.vue'),
        meta: { title: '我的收藏', icon: 'Star' },
      },
      {
        path: 'password-tools',
        name: 'PasswordTools',
        component: () => import('@/views/password-tools/index.vue'),
        meta: { title: '密码工具', icon: 'Key' },
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/index.vue'),
        meta: { title: '系统设置', icon: 'Setting' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 白名单路由
const whiteList = ['/login']

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  // 设置页面标题
  document.title = `${to.meta.title || '账号管理'} - 账号密码管理系统`

  const userStore = useUserStore()

  if (userStore.token) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      // 获取用户信息
      if (!userStore.userInfo) {
        try {
          await userStore.fetchUserInfo()
          next({ ...to, replace: true })
        } catch (error) {
          userStore.clearToken()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
