<template>
  <div class="favorites-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
          <span class="count">共 {{ total }} 个账号</span>
        </div>
      </template>

      <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏账号" />

      <div v-else class="favorites-list">
        <div
          v-for="item in favorites"
          :key="item.id"
          class="favorite-item"
        >
          <div class="item-content">
            <div class="item-main">
              <span class="platform-name">{{ item.platformName }}</span>
              <span class="account-name">{{ item.accountName }}</span>
            </div>
            <div class="item-time">
              收藏于 {{ formatTime(item.createTime) }}
            </div>
          </div>
          <div class="item-actions">
            <el-button type="primary" link @click="handleView(item)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button type="warning" link @click="handlePin(item)">
              <el-icon><Top /></el-icon>
            </el-button>
            <el-button type="danger" link @click="handleRemove(item)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="total > pagination.size" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Top, Delete } from '@element-plus/icons-vue'
import { getFavorites, removeFavorite, pinFavorite } from '@/api/dashboard'
import type { FavoriteAccount } from '@/types'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const favorites = ref<FavoriteAccount[]>([])
const total = ref(0)
const pagination = reactive({
  current: 1,
  size: 20,
})

function formatTime(time: string) {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getFavorites(pagination.current, pagination.size)
    if (res.code === 200) {
      favorites.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载收藏失败:', error)
  } finally {
    loading.value = false
  }
}

function handleView(item: FavoriteAccount) {
  router.push({
    path: '/accounts',
    query: { keyword: item.accountName },
  })
}

async function handlePin(item: FavoriteAccount) {
  try {
    await pinFavorite(item.accountId)
    ElMessage.success('已置顶')
    loadData()
  } catch (error) {
    console.error('置顶失败:', error)
  }
}

async function handleRemove(item: FavoriteAccount) {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', { type: 'warning' })
    await removeFavorite(item.accountId)
    ElMessage.success('已取消收藏')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.favorites-container {
  min-height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .count {
    font-size: 14px;
    color: #909399;
    font-weight: normal;
  }
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background: #f0f2f5;
  }

  .item-content {
    .item-main {
      display: flex;
      align-items: center;
      gap: 12px;

      .platform-name {
        font-weight: 500;
        color: #303133;
      }

      .account-name {
        color: #606266;
      }
    }

    .item-time {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }

  .item-actions {
    display: flex;
    gap: 8px;
  }
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
