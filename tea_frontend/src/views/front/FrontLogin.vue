<template>
  <div class="login-container">
    <!-- Background Decor -->
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    
    <div class="auth-panel slide-up">
      <!-- Left: Brand Visual -->
      <div class="visual-side">
        <div class="visual-overlay">
          <div class="brand-text">
            <h2>茶韵</h2>
            <p>CHAYUN</p>
          </div>
          <div class="quote-box">
            <p>"一叶见方寸，一茶识人生"</p>
          </div>
        </div>
      </div>

      <!-- Right: Auth Form -->
      <div class="form-side">
        <div class="form-wrapper">
          
          <div class="form-header fade-in">
            <h3 v-if="isLogin">欢迎回来</h3>
            <h3 v-else>创建账号</h3>
            <p class="subtitle">{{ isLogin ? '登录以继续您的品茶之旅' : '加入我们也，开启东方树叶的探索' }}</p>
          </div>

          <transition name="fade-slide" mode="out-in">
            <!-- Login Form -->
            <div v-if="isLogin" key="login" class="form-body">
              <el-form ref="loginRef" :model="loginForm" :rules="rules" size="large" @submit.prevent>
                <el-form-item prop="username">
                  <el-input 
                    v-model="loginForm.username" 
                    placeholder="请输入账号" 
                    :prefix-icon="User"
                    class="premium-input"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input 
                    v-model="loginForm.password" 
                    type="password" 
                    placeholder="请输入密码" 
                    :prefix-icon="Lock"
                    show-password
                    class="premium-input"
                    @keyup.enter="doLogin"
                  />
                </el-form-item>
                
                <div class="form-actions">
                  <el-button class="btn-primary" :loading="loading" @click="doLogin">
                    立即登录
                  </el-button>
                </div>
              </el-form>
              
              <div class="form-footer">
                <span>还没有账号？</span>
                <a class="link-highlight" @click="toggleMode">免费注册</a>
              </div>
            </div>

            <!-- Register Form -->
            <div v-else key="register" class="form-body">
              <el-form ref="regRef" :model="regForm" :rules="rules" size="large" @submit.prevent>
                <el-form-item prop="username">
                  <el-input 
                    v-model="regForm.username" 
                    placeholder="设置账号" 
                    :prefix-icon="User"
                    class="premium-input"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input 
                    v-model="regForm.password" 
                    type="password" 
                    placeholder="设置密码" 
                    :prefix-icon="Lock"
                    show-password
                    class="premium-input"
                  />
                </el-form-item>
                <el-form-item prop="nickname">
                  <el-input 
                    v-model="regForm.nickname" 
                    placeholder="您的昵称" 
                    :prefix-icon="Postcard"
                    class="premium-input"
                  />
                </el-form-item>

                <div class="form-actions">
                  <el-button class="btn-primary" :loading="loading" @click="doRegister">
                    立即注册
                  </el-button>
                </div>
              </el-form>

              <div class="form-footer">
                <span>已有账号？</span>
                <a class="link-highlight" @click="toggleMode">返回登录</a>
              </div>
            </div>
          </transition>

          <!-- Admin Entry -->
          <div class="admin-entry">
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
import bgImage from '../../assets/login-banner.jpg'

defineOptions({ name: 'FrontLogin' })

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
  loginRef.value?.clearValidate()
  regRef.value?.clearValidate()
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
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--bg-body);
  position: relative;
  overflow: hidden;
}

/* Background Shapes */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.6;
}
.shape-1 {
  width: 600px; height: 600px;
  background: radial-gradient(circle, #dcece3 0%, transparent 70%);
  top: -200px; left: -200px;
}
.shape-2 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, #fdeecd 0%, transparent 70%);
  bottom: -150px; right: -150px;
}

/* Main Card */
.auth-panel {
  width: 1000px;
  max-width: 90vw;
  height: 600px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl, 0 20px 50px rgba(74, 124, 89, 0.1));
  display: flex;
  overflow: hidden;
  z-index: 10;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

/* Visual Side */
.visual-side {
  width: 45%;
  background-image: v-bind('`url(${bgImage})`');
  background-size: cover;
  background-position: center;
  position: relative;
}
.visual-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(74, 124, 89, 0.7), rgba(44, 62, 80, 0.4));
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 60px 40px;
  color: white;
}
.brand-text h2 {
  font-family: 'Songti SC', serif;
  font-size: 3rem;
  margin: 0;
  letter-spacing: 0.2rem;
}
.brand-text p {
  letter-spacing: 0.4rem;
  font-size: 0.9rem;
  opacity: 0.8;
  margin-top: 8px;
}
.quote-box p {
  font-family: 'Songti SC', serif;
  font-size: 1.2rem;
  font-style: italic;
  opacity: 0.9;
}

/* Form Side */
.form-side {
  width: 55%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: 32px;
}
.form-header h3 {
  font-size: 28px;
  color: var(--text-main);
  margin: 0 0 8px;
  font-family: 'Songti SC', sans-serif;
  font-weight: 700;
}
.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
}

/* Inputs */
.premium-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-full);
  padding: 2px 16px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04) !important;
  background: #f9f9f9;
  transition: all 0.3s;
}
.premium-input :deep(.el-input__wrapper.is-focus) {
  background: white;
  box-shadow: 0 4px 12px rgba(74, 124, 89, 0.15) !important;
}

/* Buttons */
.btn-primary {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-full);
  font-size: 16px;
  background: var(--tea-primary);
  color: white;
  border: none;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(74, 124, 89, 0.3);
  margin-top: 16px;
  transition: all 0.3s;
}
.btn-primary:hover {
  background: var(--tea-primary-hover);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(74, 124, 89, 0.4);
}

/* Footer Links */
.form-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}
.link-highlight {
  color: var(--tea-primary);
  font-weight: 600;
  cursor: pointer;
  margin-left: 4px;
  transition: opacity 0.2s;
}
.link-highlight:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* Admin Entry */
.admin-entry {
  margin-top: 40px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}
.admin-link {
  font-size: 13px;
  color: #c0c4cc;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}
.admin-link:hover {
  color: var(--tea-primary);
}

/* Animations */
.slide-up { animation: slideUp 0.8s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateX(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-10px); }

@media (max-width: 768px) {
  .auth-panel { flex-direction: column; height: auto; max-width: 95vw; }
  .visual-side { width: 100%; height: 180px; }
  .visual-overlay { padding: 20px; justify-content: center; align-items: center; text-align: center; }
  .form-side { width: 100%; padding: 40px 20px; }
  .quote-box { display: none; }
}
</style>