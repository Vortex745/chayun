<template>
  <div class="login-wrapper">

    <div class="bg-blob blob-1"></div>
    <div class="bg-blob blob-2"></div>

    <div class="auth-card-container">

      <div class="visual-side" :style="{ backgroundImage: `url(${bgImage})` }">
      </div>

      <div class="form-side">
        <div class="form-content">

          <transition name="fade-slide" mode="out-in">
            <div v-if="isLogin" key="login" class="form-inner">
              <div class="form-header">
                <h3>欢迎回来 🍵</h3>
                <p>登录您的账号，继续品味自然</p>
              </div>

              <el-form ref="loginRef" :model="loginForm" :rules="rules" size="large" class="custom-form">
                <el-form-item prop="username">
                  <el-input
                      v-model="loginForm.username"
                      placeholder="请输入账号"
                      :prefix-icon="User"
                      class="rounded-input"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input
                      v-model="loginForm.password"
                      type="password"
                      placeholder="请输入密码"
                      :prefix-icon="Lock"
                      show-password
                      class="rounded-input"
                      @keyup.enter="doLogin"
                  />
                </el-form-item>
                <el-button type="primary" class="action-btn" :loading="loading" @click="doLogin">
                  立即登录
                </el-button>
              </el-form>

              <div class="form-footer">
                <span>还没有账号？</span>
                <span class="link-text" @click="toggleMode">免费注册</span>
              </div>
            </div>

            <div v-else key="register" class="form-inner">
              <div class="form-header">
                <h3>创建账号 🌱</h3>
                <p>加入我们，探索东方树叶的魅力</p>
              </div>

              <el-form ref="regRef" :model="regForm" :rules="rules" size="large" class="custom-form">
                <el-form-item prop="username">
                  <el-input v-model="regForm.username" placeholder="设置账号" :prefix-icon="User" class="rounded-input"/>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input v-model="regForm.password" type="password" placeholder="设置密码" :prefix-icon="Lock" show-password class="rounded-input"/>
                </el-form-item>
                <el-form-item prop="nickname">
                  <el-input v-model="regForm.nickname" placeholder="您的昵称" :prefix-icon="Postcard" class="rounded-input"/>
                </el-form-item>

                <el-button type="success" class="action-btn reg-btn" :loading="loading" @click="doRegister">
                  立即注册
                </el-button>
              </el-form>

              <div class="form-footer">
                <span>已有账号？</span>
                <span class="link-text" @click="toggleMode">返回登录</span>
              </div>
            </div>
          </transition>

          <div class="extra-links">
            <span class="admin-link" @click="toAdminLogin">
              <el-icon><Setting /></el-icon> 管理员入口
            </span>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard, Setting } from '@element-plus/icons-vue'

// 🔥🔥🔥 修复核心：正确引入图片 🔥🔥🔥
// 假设 FrontLogin.vue 在 src/views/front/ 目录下
// 图片在 src/assets/ 目录下
// 路径应该是：../../assets/xxx
import bgImage from '../../assets/login-banner.jpg'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const isLogin = ref(true)
const loading = ref(false)
const loginRef = ref(null)
const regRef = ref(null)

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '', nickname: '' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  if(loginRef.value) loginRef.value.clearValidate()
  if(regRef.value) regRef.value.clearValidate()
}

const doLogin = async () => {
  if (!loginRef.value) return
  await loginRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await axios.post('/user/login', loginForm)
        if(res.data.code === '200') {
          ElMessage.success('欢迎回来')
          localStorage.setItem('tea-user', JSON.stringify(res.data.data))
          window.dispatchEvent(new Event('refreshUser'))
          router.push('/front/home')
        } else {
          ElMessage.error(res.data.msg)
        }
      } catch(e) { ElMessage.error('服务异常') }
      finally { loading.value = false }
    }
  })
}

const doRegister = async () => {
  if (!regRef.value) return
  await regRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await axios.post('/user/register', regForm)
        if(res.data.code === '200') {
          ElMessage.success('注册成功，请登录')
          isLogin.value = true
        } else {
          ElMessage.error(res.data.msg)
        }
      } catch(e) { ElMessage.error('服务异常') }
      finally { loading.value = false }
    }
  })
}

const toAdminLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 1. 布局容器 */
.login-wrapper {
  min-height: calc(100vh - 140px);
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

/* 2. 背景装饰 */
.bg-blob { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.5; z-index: 0; }
.blob-1 { width: 400px; height: 400px; background: #d4eadd; top: 0; left: 0; }
.blob-2 { width: 350px; height: 350px; background: #ffe4d6; bottom: 0; right: 0; }

/* 3. 主卡片容器 */
.auth-card-container {
  width: 900px; height: 550px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.08);
  display: flex; overflow: hidden;
  z-index: 10;
  border: 1px solid rgba(255,255,255,0.8);
}

/* --- 【左侧】纯视觉海报区 --- */
.visual-side {
  width: 40%;
  height: 100%;
  position: relative;
  /* 背景图相关样式 */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* --- 【右侧】表单区 --- */
.form-side {
  width: 60%;
  padding: 40px 50px;
  display: flex; flex-direction: column; justify-content: center; position: relative;
  background: white;
}
.form-content { width: 100%; max-width: 340px; margin: 0 auto; }

.form-header { text-align: left; margin-bottom: 25px; }
.form-header h3 { font-size: 24px; color: #333; font-weight: 800; margin-bottom: 8px; }
.form-header p { font-size: 13px; color: #999; }

/* 输入框 */
.rounded-input :deep(.el-input__wrapper) {
  border-radius: 30px; background-color: #f7f9fb; box-shadow: none !important; padding: 0 20px;
  border: 1px solid transparent; transition: all 0.3s;
}
.rounded-input :deep(.el-input__wrapper.is-focus) {
  background-color: white; border-color: #55ab62; box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1) !important;
}

/* 按钮 */
.action-btn {
  width: 100%; height: 42px; border-radius: 30px; font-size: 15px; letter-spacing: 1px; font-weight: bold;
  background: linear-gradient(135deg, #55ab62, #429e50); border: none;
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.25);
  transition: all 0.3s; margin-top: 15px;
}
.action-btn:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(85, 171, 98, 0.35); }
.reg-btn { background: linear-gradient(135deg, #42b983, #349669); }

/* 链接 */
.form-footer { margin-top: 20px; text-align: center; font-size: 13px; color: #999; }
.link-text { color: #55ab62; font-weight: bold; cursor: pointer; margin-left: 5px; }
.link-text:hover { text-decoration: underline; }

.extra-links {
  margin-top: 40px; display: flex; justify-content: center; font-size: 12px; color: #ccc;
}
.admin-link {
  cursor: pointer; display: flex; align-items: center; gap: 4px; transition: color 0.3s;
}
.admin-link:hover { color: #55ab62; }

/* 动画 */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.35s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateX(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-10px); }
</style>