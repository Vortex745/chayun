<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px" class="aside-menu">
        <div class="logo">🍵 茶韵管理端</div>
        <el-menu
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
            :default-active="activePath"
            router
        >
          <el-menu-item index="/tea">
            <el-icon><CoffeeCup /></el-icon>
            <span>茶叶管理</span>
          </el-menu-item>

          <el-menu-item index="/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>

          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>会员管理</span>
          </el-menu-item>

          <el-menu-item index="/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>销售统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
          </div>
          <div class="header-right">
            <span style="margin-right: 15px;">欢迎回来：{{ nickname }}</span>
            <el-button type="danger" link size="small" @click="logout">退出登录</el-button>
          </div>
        </el-header>

        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router' // 引入路由工具
import { CoffeeCup, List, User, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 1. 获取当前登录信息
const adminUser = JSON.parse(localStorage.getItem('tea-admin-user') || '{}')
const nickname = adminUser.nickname || '管理员'

// 2. 保持菜单高亮 (刷新后不仅白屏)
const activePath = ref(route.path)

// 3. 退出登录逻辑
const logout = () => {
  // 清除缓存
  localStorage.removeItem('tea-admin-user')
  // 强制跳转回登录页
  router.push('/login')
  // 提示
  // ElMessage.success('已退出') // 如果想提示可以加这行
}
</script>

<style scoped>
.common-layout { height: 100vh; display: flex; }
.el-container { height: 100%; }
.aside-menu { background-color: #545c64; color: white; }
.logo { height: 60px; line-height: 60px; text-align: center; font-size: 20px; font-weight: bold; background-color: #434a50; }
.header { background-color: #fff; border-bottom: 1px solid #ddd; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; font-size: 14px; }
.el-menu { border-right: none; }
.header-right { display: flex; align-items: center; }
</style>