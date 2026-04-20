<template>
  <div class="client-layout">
    <!-- Navbar -->
    <header class="nav-bar-container" :class="{ 'scrolled': isScrolled }">
      <div class="nav-content container">
        
        <!-- Branding -->
        <div class="brand-section" @click="router.push('/front/home')">
          <img :src="logoImage" alt="茶韵" class="brand-logo" />
          <div class="brand-text">
            <h1>茶韵商城</h1>
            <span class="brand-sub">Premium Selection</span>
          </div>
        </div>

        <!-- Center Navigation -->
        <nav class="nav-menu">
          <router-link to="/front/home" class="nav-link" active-class="active">
            <span>首页</span>
          </router-link>
          <router-link to="/front/cart" class="nav-link" active-class="active">
            <span>选茶</span>
          </router-link>
          <router-link to="/front/myorder" class="nav-link" active-class="active">
            <span>订单</span>
          </router-link>
          <div class="nav-indicator"></div>
        </nav>

        <!-- Right Tools -->
        <div class="nav-tools">
          <div class="search-wrap">
            <el-input
              v-model="keyword"
              placeholder="搜索茶品..."
              class="search-input"
              :prefix-icon="Search"
              @keyup.enter="handleSearch"
            />
          </div>

          <!-- User Menu -->
          <div class="user-menu">
            <template v-if="user.id">
              <el-dropdown trigger="hover" popper-class="premium-dropdown">
                <div class="avatar-capsule">
                  <el-avatar :size="32" :src="user.avatarUrl" class="user-avatar">
                   {{ user.nickname?.charAt(0).toUpperCase() }}
                  </el-avatar>
                  <span class="user-name">{{ user.nickname }}</span>
                  <el-icon class="dropdown-arrow"><CaretBottom /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <div class="menu-header">
                      <p>你好, {{ user.nickname }}</p>
                    </div>
                    <el-dropdown-item @click="router.push('/front/user')">
                      <el-icon><User /></el-icon> 个人中心
                    </el-dropdown-item>
                    <el-dropdown-item @click="router.push('/front/myorder')">
                      <el-icon><List /></el-icon> 我的订单
                    </el-dropdown-item>
                    <el-divider class="menu-divider" />
                    <el-dropdown-item @click="handleLogout" class="logout-item">
                      <el-icon><SwitchButton /></el-icon> 退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <div v-else class="auth-buttons">
              <button class="btn-login" @click="router.push('/front/login')">登录</button>
            </div>
          </div>
        </div>

      </div>
    </header>

    <!-- Content Area -->
    <main class="main-body">
      <router-view v-slot="{ Component }">
        <transition name="fade-up" mode="out-in">
          <component :is="Component" :key="route.fullPath" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="site-footer">
      <div class="footer-content container">
        <div class="footer-brand">
          <h3>茶韵商城</h3>
          <p>一叶见方寸，一茶识人生</p>
        </div>
        <div class="footer-links">
          <span>关于我们</span>
          <span>联系客服</span>
          <span>隐私政策</span>
        </div>
        <div class="copyright">
          © 2025 茶韵 Chayun. All Rights Reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, User, List, SwitchButton, CaretBottom } from '@element-plus/icons-vue'
import logoImage from '../assets/chayunlogo.png'

const router = useRouter()
const route = useRoute()
const user = ref({})
const keyword = ref('')
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const handleSearch = () => {
  if(!keyword.value) return
  router.push({ path: '/front/home', query: { name: keyword.value } })
}

const getUser = () => {
  user.value = JSON.parse(localStorage.getItem('tea-user') || '{}')
}

const handleLogout = () => {
  localStorage.removeItem('tea-user')
  user.value = {}
  router.push('/front/login')
}

// Event Bus
const refreshUser = () => getUser()

onMounted(() => {
  getUser()
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('refreshUser', refreshUser)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('refreshUser', refreshUser)
})
</script>

<style scoped>
/* Layout Base */
.client-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-body);
}

/* Nav Bar */
.nav-bar-container {
  height: 80px;
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}
.nav-bar-container.scrolled {
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: var(--shadow-sm);
}

.nav-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Brand */
.brand-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.brand-logo {
  height: 40px;
  transition: height 0.3s;
}
.scrolled .brand-logo { height: 32px; }

.brand-text h1 {
  font-family: 'Songti SC', serif;
  font-size: 20px;
  margin: 0;
  color: var(--text-main);
  letter-spacing: 2px;
}
.brand-sub {
  font-size: 10px;
  color: var(--tea-primary);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Menu */
.nav-menu {
  display: flex;
  gap: 8px;
  position: relative;
  background: rgba(0,0,0,0.03);
  padding: 4px;
  border-radius: var(--radius-full);
}
.nav-link {
  padding: 8px 24px;
  font-size: 14px;
  color: var(--text-secondary);
  border-radius: var(--radius-full);
  transition: all 0.3s;
  position: relative;
  z-index: 2;
}
.nav-link span { position: relative; z-index: 2; }
.nav-link:hover {
  color: var(--tea-primary);
}
.nav-link.active {
  background: white;
  color: var(--tea-primary);
  font-weight: 600;
  box-shadow: var(--shadow-xs);
}

/* Tools */
.nav-tools {
  display: flex;
  align-items: center;
  gap: 24px;
}
.search-input {
  width: 200px;
  transition: width 0.3s;
}
.search-input:focus-within { width: 260px; }
:deep(.el-input__wrapper) {
  border-radius: var(--radius-full);
  box-shadow: none;
  background: rgba(0,0,0,0.03);
  padding: 4px 16px;
}
:deep(.el-input__wrapper.is-focus) {
  background: white;
  box-shadow: 0 0 0 1px var(--tea-primary) !important;
}

/* User Menu */
.avatar-capsule {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  background: white;
  border-radius: var(--radius-full);
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}
.avatar-capsule:hover {
  border-color: var(--tea-primary-light);
  box-shadow: var(--shadow-xs);
}
.user-avatar { 
  background: var(--tea-primary); 
  font-size: 14px;
  color: white;
}
.user-name { font-size: 13px; font-weight: 500; color: var(--text-main); max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dropdown-arrow { font-size: 12px; color: #bbb; }

.btn-login {
  background: var(--tea-primary);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-login:hover { background: var(--tea-primary-hover); }

/* Main Body */
.main-body {
  margin-top: 80px;
  flex: 1;
  width: 100%;
}

/* Footer */
.site-footer {
  background: white;
  padding: 60px 0 30px;
  border-top: 1px solid rgba(0,0,0,0.03);
  margin-top: auto;
}
.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}
.footer-brand h3 {
  font-family: 'Songti SC', serif;
  margin: 0 0 8px;
  color: var(--text-main);
  text-align: center;
}
.footer-brand p {
  color: var(--text-secondary);
  font-size: 13px;
  margin: 0;
}
.footer-links {
  display: flex;
  gap: 32px;
  font-size: 13px;
  color: var(--text-secondary);
}
.copyright {
  font-size: 12px;
  color: #ccc;
  margin-top: 20px;
}

/* Transitions */
.fade-up-enter-active, .fade-up-leave-active { transition: all 0.4s ease; }
.fade-up-enter-from { opacity: 0; transform: translateY(20px); }
.fade-up-leave-to { opacity: 0; transform: translateY(-20px); }
</style>

<style>
/* Global Dropdown Styles */
.premium-dropdown {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1) !important;
  padding: 8px !important;
}
.menu-header {
  padding: 8px 16px;
  font-size: 12px;
  color: #aaa;
  font-weight: 500;
}
.menu-divider { margin: 4px 0 !important; }
.el-dropdown-menu__item {
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 13px;
}
.el-dropdown-menu__item:hover {
  background: var(--tea-primary-light) !important;
  color: var(--tea-primary) !important;
}
.logout-item:hover {
  background: #fff1f0 !important;
  color: var(--tea-danger) !important;
}
</style>