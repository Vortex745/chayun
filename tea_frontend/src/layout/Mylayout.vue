<template>
  <div class="common-layout">

    <el-container class="main-container">

      <el-aside class="glass-sidebar" width="240px">
        <div class="sidebar-inner">
          <div class="logo-area">
            <span class="logo-text">茶韵管理</span>
          </div>

          <el-menu
              :default-active="activePath"
              active-text-color="#fff"
              background-color="transparent"
              class="custom-menu"
              router
              text-color="#555"
          >
            <el-menu-item index="/tea">
              <el-icon>
                <CoffeeCup/>
              </el-icon>
              <span>茶叶管理</span>
            </el-menu-item>
            <el-menu-item index="/carousel">
              <el-icon>
                <Picture/>
              </el-icon>
              <span>轮播设置</span>
            </el-menu-item>
            <el-menu-item index="/orders">
              <el-icon>
                <List/>
              </el-icon>
              <span>订单管理</span>
            </el-menu-item>

            <el-menu-item index="/message" class="menu-item-relative">
              <el-icon><ChatLineRound /></el-icon>
              <span>会员留言</span>
              <el-badge :value="unreadCount" :max="99" class="msg-badge" :hidden="unreadCount === 0" />
            </el-menu-item>

            <el-menu-item index="/user">
              <el-icon>
                <User/>
              </el-icon>
              <span>会员管理</span>
            </el-menu-item>
            <el-menu-item index="/stats">
              <el-icon>
                <TrendCharts/>
              </el-icon>
              <span>销售统计</span>
            </el-menu-item>
          </el-menu>

          <div class="sidebar-footer">
            <p>© 2025 Admin Pro</p>
          </div>
        </div>
      </el-aside>

      <el-container class="right-container">

        <el-header class="glass-header">
          <div class="header-inner">
            <div class="header-left">
              <el-icon class="menu-trigger">
                <Menu/>
              </el-icon>
              <el-breadcrumb class="custom-breadcrumb" separator="/">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>系统管理</el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>

            <div class="header-right">
              <div class="action-btn">
                <el-icon>
                  <Bell/>
                </el-icon>
              </div>
              <el-dropdown trigger="click">
                <div class="user-badge">
                  <span class="welcome-text">{{ nickname }}</span>
                  <el-icon class="arrow-icon">
                    <CaretBottom/>
                  </el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu class="custom-dropdown">
                    <el-dropdown-item :icon="SwitchButton" divided style="color: #f56c6c;" @click="logout">退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>

        <el-main class="content-wrapper">
          <div class="content-card">
            <router-view v-slot="{ Component }">
              <transition name="smooth-slide">
                <component :is="Component"/>
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import {
  Bell,
  CaretBottom,
  ChatLineRound,
  CoffeeCup,
  List,
  Menu,
  Picture,
  SwitchButton,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import {computed, getCurrentInstance, onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'

const {proxy} = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()
const route = useRoute()
const adminUser = JSON.parse(localStorage.getItem('tea-admin-user') || '{}')
const nickname = adminUser.username || adminUser.nickname || '管理员'
const activePath = ref(route.path)

const unreadCount = ref(0)
let timer = null

const currentRouteName = computed(() => {
  const map = {
    '/tea': '茶叶管理',
    '/orders': '订单管理',
    '/user': '会员管理',
    '/stats': '销售统计',
    '/carousel': '轮播设置',
    '/message': '会员留言'
  }
  return map[route.path] || '当前页面'
})

// 【核心】轮询未读消息
const checkMessages = async () => {
  try {
    const res = await axios.get('/message/unread')
    if (res.data.code === '200') {
      unreadCount.value = res.data.data
    }
  } catch (e) {
  }
}

const logout = () => {
  localStorage.removeItem('tea-admin-user')
  ElMessage.success('已安全退出')
  router.push('/login')
}

onMounted(() => {
  checkMessages()
  // 每 10 秒轮询一次
  timer = setInterval(checkMessages, 10000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 全局背景 */
.common-layout {
  height: 100vh;
  width: 100vw;
  background: radial-gradient(circle at 10% 20%, rgb(239, 246, 242) 0%, rgb(224, 236, 230) 90%);
  overflow: hidden;
  font-family: 'PingFang SC', sans-serif;
}

.main-container {
  height: 100%;
  display: flex;
}

.right-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  flex: 1;
}

/* === 侧边栏 === */
.glass-sidebar {
  padding: 20px 0 20px 20px;
  background: transparent;
  overflow: visible;
}

.sidebar-inner {
  height: 100%;
  width: 100%;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 8px 0 30px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
}

.logo-area {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
  margin-bottom: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 800;
  color: #2c3e50;
  letter-spacing: 1px;
}

/* 菜单样式 */
.custom-menu {
  border-right: none;
  padding: 0 15px;
}

:deep(.el-menu-item) {
  border-radius: 12px;
  margin-bottom: 10px;
  height: 50px;
  font-weight: 500;
  color: #666;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(85, 171, 98, 0.08) !important;
  color: #55ab62 !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #55ab62, #429e50) !important;
  box-shadow: 0 6px 16px rgba(85, 171, 98, 0.25);
  color: white !important;
  font-weight: bold;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: white !important;
}

.sidebar-footer {
  margin-top: auto;
  padding: 20px;
  text-align: center;
  font-size: 12px;
  color: #999;
}

/* 角标微调 */
.msg-badge {
  margin-left: auto;
  margin-right: 5px;
}

:deep(.el-badge__content) {
  background-color: #f56c6c;
  border: none;
}

/* === 顶部栏 === */
.glass-header {
  height: 80px;
  padding: 20px 40px 0 20px;
  background: transparent;
}

.header-inner {
  height: 100%;
  width: 100%;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 25px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-trigger {
  font-size: 20px;
  color: #555;
  cursor: pointer;
}

:deep(.el-breadcrumb__inner) {
  color: #888 !important;
  font-weight: normal;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #2c3e50 !important;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.3s;
  color: #666;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.user-badge {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px 4px 4px;
  border-radius: 30px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: all 0.3s;
}

.user-badge:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.welcome-text {
  margin: 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.arrow-icon {
  font-size: 12px;
  color: #999;
}

/* === 内容区 === */
.content-wrapper {
  padding: 20px;
  overflow-y: auto;
  perspective: 1200px;
}

.content-card {
  position: relative;
  width: 100%;
  min-height: 100%;
}

/* 动画 */
.smooth-slide-enter-active, .smooth-slide-leave-active {
  transition: all 0.45s cubic-bezier(0.25, 1, 0.5, 1);
}

.smooth-slide-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 0;
}

.smooth-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
  z-index: 1;
}

.smooth-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.smooth-slide-enter-to, .smooth-slide-leave-from {
  opacity: 1;
  transform: translateX(0);
}
.menu-item-relative {
  position: relative;
  display: flex;
  align-items: center;
}

/* 修复角标样式 */
.msg-badge {
  position: absolute;
  right: 20px; /* 靠右距离 */
  top: 50%;
  transform: translateY(-50%); /* 垂直居中 */
  line-height: 1; /* 防止撑高 */
}

/* 覆盖 Element 的 badge 默认样式，让它更精致 */
:deep(.el-badge__content) {
  border: none;
  background-color: #ff4d4f; /* 鲜艳红 */
  height: 18px;
  line-height: 18px;
  padding: 0 5px;
}
.custom-dropdown {
  border-radius: 12px !important;
  padding: 6px !important;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1) !important;
  border: none !important;
}
</style>
