<template>
  <div class="admin-login-container">

    <div class="bg-circle c1"></div>
    <div class="bg-circle c2"></div>

    <div class="login-card-box">
      <div class="card-header">
        <h2 class="app-title">茶韵商城 · 后台管理</h2>
        <p class="sub-title">Admin Management System</p>
      </div>

      <el-form :model="form" class="login-form">
        <el-form-item>
          <el-input
              v-model="form.username"
              placeholder="请输入管理员账号"
              :prefix-icon="User"
              size="large"
              class="custom-input"
          />
        </el-form-item>

        <el-form-item>
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入管理员密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              class="custom-input"
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="login-btn"
              size="large"
              @click="handleLogin"
              :loading="loading"
          >
            登 录 系 统
          </el-button>
        </el-form-item>
      </el-form>

      <div class="back-link" @click="goHome">
        <el-icon><House /></el-icon>
        <span>返回商城前台首页</span>
      </div>
    </div>

    <div class="copyright">© 2025 茶韵商城 - 致力于东方茶文化传播</div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, House } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const loading = ref(false)
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/admin/login', form)
    if(res.data.code === '200') {
      // 1. 存储用户信息 (保持原逻辑 key名: tea-admin-user)
      localStorage.setItem('tea-admin-user', JSON.stringify(res.data.data))

      // 2. 统一风格的提示
      ElMessage({
        message: '登录成功，欢迎管理员',
        type: 'success',
        customClass: 'tea-message', // 挂载自定义样式
        duration: 1500
      })

      // 3. 【核心修复】跳转到原代码指定的路径 /tea
      setTimeout(() => {
        router.push('/tea')
      }, 500)

    } else {
      ElMessage.error(res.data.msg)
    }
  } catch(e) {
    console.error(e)
    ElMessage.error('连接服务器失败')
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/front/home')
}
</script>

<style scoped>
/* 1. 整体背景：使用淡雅的灰绿色渐变，比原来的亮绿色更稳重 */
.admin-login-container {
  height: 100vh; width: 100%;
  background: linear-gradient(135deg, #f0f7f4 0%, #e1e9e5 100%);
  display: flex; flex-direction: column;
  justify-content: center; align-items: center;
  position: relative; overflow: hidden;
}

/* 装饰圆背景 */
.bg-circle { position: absolute; border-radius: 50%; filter: blur(80px); z-index: 0; }
.c1 { width: 400px; height: 400px; background: rgba(85, 171, 98, 0.12); top: -50px; left: -50px; }
.c2 { width: 350px; height: 350px; background: rgba(44, 62, 80, 0.05); bottom: -50px; right: -50px; }

/* 2. 登录卡片 */
.login-card-box {
  width: 440px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px); /* 磨砂玻璃质感 */
  border-radius: 16px;
  padding: 50px 40px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.05);
  z-index: 10;
  text-align: center;
  border: 1px solid rgba(255,255,255,0.8);
  animation: floatUp 0.6s ease-out;
}

/* 头部样式 */
.card-header { margin-bottom: 35px; }
.logo-icon { width: 50px; height: 50px; margin-bottom: 12px; }
.app-title { font-size: 26px; font-weight: 800; color: #2c3e50; margin-bottom: 5px; letter-spacing: 2px; }
.sub-title { font-size: 12px; color: #999; text-transform: uppercase; letter-spacing: 3px; }

/* 3. 输入框美化 (覆盖 Element Plus 样式) */
.custom-input :deep(.el-input__wrapper) {
  background-color: #f8fafc; box-shadow: none; border-radius: 30px; padding: 0 20px;
  border: 1px solid #eee; transition: all 0.3s;
}
.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: white; border-color: #55ab62;
  box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.15) !important;
}

/* 4. 登录按钮 */
.login-btn {
  width: 100%; height: 45px; border-radius: 30px;
  font-size: 16px; letter-spacing: 4px; font-weight: bold;
  background: linear-gradient(135deg, #55ab62, #429e50); border: none; margin-top: 10px;
  box-shadow: 0 8px 20px rgba(85, 171, 98, 0.25); transition: all 0.3s;
}
.login-btn:hover { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(85, 171, 98, 0.35); }

/* 底部链接 */
.back-link {
  margin-top: 25px; color: #999; font-size: 13px; cursor: pointer; transition: all 0.3s;
  display: flex; justify-content: center; align-items: center; gap: 6px;
}
.back-link:hover { color: #55ab62; }

.copyright { position: absolute; bottom: 20px; font-size: 12px; color: #b0b0b0; }

@keyframes floatUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>

<style>
.el-message.tea-message {
  background-color: #f0f9eb !important;
  border-color: #e1f3d8 !important;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.1) !important;
}
.el-message.tea-message .el-message__content {
  color: #55ab62 !important;
  font-weight: bold;
}
.el-message.tea-message .el-icon {
  color: #55ab62 !important;
}
</style>