<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.error-page {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-content {
  text-align: center;
  color: white;

  .error-code {
    font-size: 120px;
    font-weight: 700;
    margin: 0;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  }

  .error-message {
    font-size: 20px;
    margin: 20px 0 40px;
    opacity: 0.9;
  }
}
</style>
