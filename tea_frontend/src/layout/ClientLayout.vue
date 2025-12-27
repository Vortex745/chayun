<template>
  <div class="client-layout">
    <div class="nav-bar">
      <div class="nav-content">
        <div class="logo">🍃 茶韵商城</div>
        <div class="menu">
          <router-link to="/front/home" class="menu-item">首页</router-link>
          <router-link to="/front/cart" class="menu-item">购物车</router-link>
          <router-link to="/front/myorder" class="menu-item">我的订单</router-link>
        </div>
        <div class="user-action">
          <div v-if="user.id">
            <span
              style="margin-right: 10px; color: #409EFF; cursor: pointer; font-weight: bold;"
              @click="router.push('/front/user')"
          >
  {{ user.nickname }}
</span>
            <span class="login-link" @click="handleLogout">[退出]</span>
          </div>
          <span v-else class="login-link" @click="$router.push('/front/login')">登录 / 注册</span>
        </div>
      </div>
    </div>

    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref({})

// 提取一个专门的方法：从缓存读取用户信息
const getUserFromStorage = () => {
  user.value = JSON.parse(localStorage.getItem('tea-user') || '{}')
}

const handleLogout = () => {
  localStorage.removeItem('tea-user')
  user.value = {}
  router.push('/front/login')
}

// 监听器：一旦接收到 'refreshUser' 事件，就重新读取
const handleRefreshEvent = () => {
  getUserFromStorage()
}

onMounted(() => {
  // 1. 刚打开页面时，读一次
  getUserFromStorage()
  // 2. 添加监听耳朵，等着别人通知
  window.addEventListener('refreshUser', handleRefreshEvent)
})

onUnmounted(() => {
  // 组件销毁时，移除监听（好习惯）
  window.removeEventListener('refreshUser', handleRefreshEvent)
})
</script>

<style scoped>
.nav-bar { height: 60px; background: white; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); position: fixed; top: 0; width: 100%; z-index: 999; }
.nav-content { width: 1200px; margin: 0 auto; height: 100%; display: flex; align-items: center; justify-content: space-between; }
.logo { font-size: 24px; font-weight: bold; color: #409EFF; }
.menu-item { margin: 0 20px; text-decoration: none; color: #333; font-size: 16px; }
.menu-item:hover, .router-link-active { color: #409EFF; }
.main-content { width: 1200px; margin: 80px auto 20px; min-height: 500px; }
.login-link { cursor: pointer; color: #666; font-size: 14px; }
.login-link:hover { color: #409EFF; }
</style>