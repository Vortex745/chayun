<template>
  <div class="client-layout">
    <div class="nav-bar">
      <div class="nav-content">
        <div class="left-part">
          <div class="logo-box" @click="router.push('/front/home')">
            <img :src="logoImage" alt="logo" class="logo-img" />
            <span class="logo-text">茶韵商城</span>
          </div>
          <div class="nav-menu-container" ref="navMenuRef">
            <div class="sliding-bar" :style="slidingBarStyle"></div>
            <router-link to="/front/home" class="menu-item" active-class="active-link"><span>首页精选</span></router-link>
            <router-link to="/front/cart" class="menu-item" active-class="active-link"><span>购物车</span></router-link>
            <router-link to="/front/myorder" class="menu-item" active-class="active-link"><span>我的订单</span></router-link>
          </div>
        </div>

        <div class="right-part">
          <el-input
              v-model="keyword"
              placeholder="寻一味好茶..."
              class="nav-search"
              :prefix-icon="Search"
              clearable
              @keyup.enter="handleSearch"
          />

          <div class="user-action">
            <template v-if="user.id">
              <el-dropdown trigger="click" popper-class="tea-dropdown-popper">
                <div class="user-profile">
                  <el-avatar :size="34" class="header-avatar">{{ user.nickname?.charAt(0) }}</el-avatar>
                  <span class="username">{{ user.nickname }}</span>
                  <el-icon class="arrow-icon"><CaretBottom /></el-icon>
                </div>

                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/front/user')">
                      <el-icon><User /></el-icon> 个人中心
                    </el-dropdown-item>
                    <el-dropdown-item @click="router.push('/front/myorder')">
                      <el-icon><List /></el-icon> 我的订单
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="handleLogout" class="logout-item">
                      <el-icon><SwitchButton /></el-icon> 退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>

            <span v-else class="login-link" @click="$router.push('/front/login')">登录 / 注册</span>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-page" mode="out-in">
          <keep-alive include="Home">
            <component :is="Component" />
          </keep-alive>
        </transition>
      </router-view>
    </div>

    <div class="footer">
      <p>© 2025 茶韵商城 - 品味自然之美</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 引入所需图标
import { Search, User, List, SwitchButton, CaretBottom } from '@element-plus/icons-vue'
import logoImage from '../assets/chayunlogo.png'

const router = useRouter()
const route = useRoute()
const user = ref({})
const keyword = ref('')

const navMenuRef = ref(null)
const slidingBarStyle = reactive({ left: '0px', width: '0px', opacity: 0 })

const updateSliderPosition = async () => {
  await nextTick()
  if (!navMenuRef.value) return
  const activeItem = navMenuRef.value.querySelector('.active-link')
  if (activeItem) {
    slidingBarStyle.left = `${activeItem.offsetLeft}px`
    slidingBarStyle.width = `${activeItem.offsetWidth}px`
    slidingBarStyle.opacity = 1
  } else {
    slidingBarStyle.opacity = 0
  }
}

watch(() => route.path, () => updateSliderPosition(), { immediate: true })
const handleResize = () => updateSliderPosition()

const handleSearch = () => router.push({ path: '/front/home', query: { name: keyword.value } })
const getUserFromStorage = () => user.value = JSON.parse(localStorage.getItem('tea-user') || '{}')
const handleLogout = () => {
  localStorage.removeItem('tea-user')
  user.value = {}
  router.push('/front/login')
}
const handleRefreshEvent = () => getUserFromStorage()

onMounted(() => {
  getUserFromStorage()
  window.addEventListener('refreshUser', handleRefreshEvent)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('refreshUser', handleRefreshEvent)
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 保持原有布局样式 */
.client-layout { background-color: #f7f9fc; min-height: 100vh; display: flex; flex-direction: column; }
.nav-bar { height: 68px; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(20px); box-shadow: 0 2px 15px rgba(0,0,0,0.04); position: fixed; top: 0; width: 100%; z-index: 999; border-bottom: 1px solid rgba(0,0,0,0.03); }
.nav-content { width: 1200px; margin: 0 auto; height: 100%; display: flex; align-items: center; justify-content: space-between; }
.left-part { display: flex; align-items: center; gap: 50px; height: 100%; }
.logo-box { display: flex; align-items: center; cursor: pointer; gap: 10px; }
.logo-img { height: 38px; width: auto; object-fit: contain; filter: drop-shadow(0 2px 4px rgba(85, 171, 98, 0.2)); }
.logo-text { font-size: 22px; font-weight: 800; color: #2c3e50; letter-spacing: 1px; background: linear-gradient(135deg, #55ab62, #2c3e50); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.nav-menu-container { display: flex; gap: 8px; position: relative; padding: 5px; background: rgba(0,0,0,0.02); border-radius: 30px; }
.sliding-bar { position: absolute; top: 5px; bottom: 5px; left: 0; background: #55ab62; border-radius: 25px; transition: all 0.35s cubic-bezier(0.25, 0.8, 0.25, 1); z-index: 0; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25); }
.menu-item { position: relative; z-index: 1; text-decoration: none; color: #666; font-size: 15px; font-weight: 500; padding: 8px 24px; border-radius: 25px; transition: color 0.3s ease; display: flex; align-items: center; justify-content: center; }
.menu-item:not(.active-link):hover { color: #55ab62; background-color: rgba(85, 171, 98, 0.08); }
.active-link { color: white !important; font-weight: bold; }
.right-part { display: flex; align-items: center; gap: 25px; }
.nav-search { width: 240px; transition: width 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); }
.nav-search:focus-within { width: 300px; }
:deep(.el-input__wrapper) { border-radius: 25px; background-color: #f4f6f8; box-shadow: none; padding-left: 15px; transition: all 0.3s; }
:deep(.el-input__wrapper.is-focus) { background-color: white; box-shadow: 0 0 0 2px rgba(85, 171, 98, 0.3); }

/* 用户头像胶囊样式优化 */
.user-profile {
  display: flex; align-items: center; cursor: pointer;
  padding: 5px 12px 5px 5px; border-radius: 30px;
  transition: all 0.3s; background: rgba(0,0,0,0.03);
  border: 1px solid transparent;
}
.user-profile:hover {
  background: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border-color: #f0f0f0;
}
.header-avatar { background: #55ab62; color: white; border: 2px solid #fff; box-shadow: 0 2px 6px rgba(85,171,98,0.2); }
.username { margin-left: 10px; font-size: 14px; font-weight: 500; color: #333; }
.arrow-icon { margin-left: 6px; font-size: 12px; color: #999; transition: transform 0.3s; }
.user-profile:hover .arrow-icon { transform: rotate(180deg); color: #55ab62; }

.login-link { cursor: pointer; color: white; background: #55ab62; font-size: 14px; font-weight: bold; padding: 9px 22px; border-radius: 25px; transition: all 0.3s; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3); }
.login-link:hover { background-color: #479e53; transform: translateY(-2px); }
.main-content { width: 1200px; margin: 85px auto 40px; min-height: 600px; }
.footer { text-align: center; color: #bdc3c7; padding: 30px 0; font-size: 13px; }
.fade-page-enter-active, .fade-page-leave-active { transition: opacity 0.3s ease; }
.fade-page-enter-from, .fade-page-leave-to { opacity: 0; }
</style>

<style>
/* 必须写在 scoped 之外才能生效 */
.tea-dropdown-popper {
  border-radius: 12px !important;
  padding: 6px !important;
  border: none !important;
  box-shadow: 0 8px 25px rgba(0,0,0,0.12) !important;
}

.tea-dropdown-popper .el-dropdown-menu__item {
  border-radius: 8px;
  padding: 10px 16px;
  margin-bottom: 2px;
  font-size: 14px;
  color: #555;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tea-dropdown-popper .el-dropdown-menu__item .el-icon {
  margin-right: 0;
  font-size: 16px;
  color: #888;
}

/* 悬停效果：品牌绿背景 */
.tea-dropdown-popper .el-dropdown-menu__item:not(.is-disabled):hover {
  background-color: #f0f9eb !important;
  color: #55ab62 !important;
}
.tea-dropdown-popper .el-dropdown-menu__item:hover .el-icon {
  color: #55ab62 !important;
}

/* 退出登录：悬停变红 */
.tea-dropdown-popper .logout-item {
  margin-top: 5px;
  border-top: 1px solid #f5f5f5; /* 手动分割线，比默认的好看 */
}
.tea-dropdown-popper .logout-item:hover {
  background-color: #fef0f0 !important;
  color: #f56c6c !important;
}
.tea-dropdown-popper .logout-item:hover .el-icon {
  color: #f56c6c !important;
}

/* 隐藏默认的 ugly 分割线 */
.tea-dropdown-popper .el-dropdown-menu__item--divided {
  border-top: none !important;
  margin-top: 0 !important;
}
.tea-dropdown-popper .el-dropdown-menu__item--divided::before {
  display: none !important;
}
</style>