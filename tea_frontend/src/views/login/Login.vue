<template>
  <div class="admin-login-page">
    <!-- Animated Background -->
    <div class="bg-nature">
      <div class="leaf leaf-1"></div>
      <div class="leaf leaf-2"></div>
      <div class="leaf leaf-3"></div>
    </div>

    <div class="login-container slide-in">
      <div class="brand-header">
        <h1 class="brand-title">茶韵商城</h1>
        <p class="brand-subtitle">ADMINISTRATION SYSTEM</p>
      </div>

      <div class="login-card glass-morphism">
        <div class="welcome-text">
          <h3>后台管理登录</h3>
          <p>请输入管理员账号进行操作</p>
        </div>

        <el-form :model="form" class="admin-form" size="large">
          <el-form-item>
            <div class="input-group" :class="{ focused: focusedInput === 'user' }">
              <el-icon><User /></el-icon>
              <input 
                v-model="form.username" 
                placeholder="管理员账号"
                @focus="focusedInput = 'user'"
                @blur="focusedInput = ''"
              />
            </div>
          </el-form-item>

          <el-form-item>
            <div class="input-group" :class="{ focused: focusedInput === 'pass' }">
              <el-icon><Lock /></el-icon>
              <input 
                v-model="form.password" 
                type="password" 
                placeholder="登录密码"
                @focus="focusedInput = 'pass'"
                @blur="focusedInput = ''"
                @keyup.enter="handleLogin"
              />
            </div>
          </el-form-item>

          <button class="btn-login" @click="handleLogin" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>立即登录</span>
            <el-icon v-if="!loading" class="arrow-icon"><Right /></el-icon>
          </button>
        </el-form>

        <div class="footer-link" @click="goHome">
          <el-icon><House /></el-icon> 返回商城首页
        </div>
      </div>
      
      <p class="copyright">© 2025 茶韵 Chayun. All Rights Reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Right, House } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const loading = ref(false)
const focusedInput = ref('')
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if(!form.username || !form.password) return ElMessage.warning('请输入完整账号信息')
  
  loading.value = true
  try {
    const res = await axios.post('/admin/login', form)
    if(res.data.code === '200') {
      localStorage.setItem('tea-admin-user', JSON.stringify(res.data.data))
      ElMessage.success('登录成功，欢迎回来')
      setTimeout(() => router.push('/tea'), 600)
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch(e) {
    ElMessage.error('服务器连接异常')
  } finally {
    loading.value = false
  }
}

const goHome = () => router.push('/front/home')
</script>

<style scoped>
.admin-login-page {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f1f8f3 0%, #e2e8e4 100%);
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* Background Aesthetics */
.bg-nature {
  position: absolute; width: 100%; height: 100%; overflow: hidden; z-index: 0;
}
.leaf {
  position: absolute; border-radius: 50%; opacity: 0.15; filter: blur(60px);
  animation: float 20s infinite ease-in-out;
}
.leaf-1 { width: 500px; height: 500px; background: #55ab62; top: -100px; left: -100px; animation-delay: 0s; }
.leaf-2 { width: 400px; height: 400px; background: #2c3e50; bottom: -50px; right: -50px; animation-delay: -5s; }
.leaf-3 { width: 200px; height: 200px; background: #d4af37; top: 40%; left: 60%; animation-delay: -10s; }

/* Main Container */
.login-container {
  z-index: 10;
  display: flex; flex-direction: column; align-items: center;
  width: 100%; max-width: 420px;
  padding: 0 20px;
}

.brand-header { text-align: center; margin-bottom: 30px; }
.brand-title {
  font-family: 'Songti SC', serif;
  font-size: 36px;
  color: #2c3e50;
  margin: 0 0 5px;
  letter-spacing: 4px;
}
.brand-subtitle {
  font-size: 12px;
  color: #7f8c8d;
  letter-spacing: 4px;
  text-transform: uppercase;
}

/* Glass Card */
.login-card {
  width: 100%;
  padding: 40px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
}
.login-card:hover { transform: translateY(-5px); }

.welcome-text { margin-bottom: 30px; text-align: center; }
.welcome-text h3 { margin: 0 0 8px; font-size: 20px; color: #333; }
.welcome-text p { margin: 0; font-size: 13px; color: #999; }

/* Input Styles */
.input-group {
  display: flex; align-items: center;
  background: #f7f9f8;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 0 16px;
  height: 50px;
  transition: all 0.3s ease;
}
.input-group .el-icon { font-size: 18px; color: #a4b0be; margin-right: 12px; }
.input-group input {
  border: none; background: none; outline: none;
  font-size: 15px; color: #2c3e50; flex: 1; height: 100%;
}
.input-group.focused {
  background: white;
  border-color: #55ab62;
  box-shadow: 0 0 0 4px rgba(85, 171, 98, 0.1);
}
.input-group.focused .el-icon { color: #55ab62; }

/* Button */
.btn-login {
  width: 100%;
  height: 50px;
  margin-top: 10px;
  border: none;
  border-radius: 12px;
  background: #2c3e50;
  color: white;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  display: flex; justify-content: center; align-items: center; gap: 10px;
  transition: all 0.3s;
  box-shadow: 0 10px 20px rgba(44, 62, 80, 0.2);
}
.btn-login:hover {
  background: #34495e;
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(44, 62, 80, 0.3);
}
.btn-login:disabled { background: #95a5a6; cursor: wait; transform: none; box-shadow: none; }

.arrow-icon { font-size: 14px; transition: transform 0.3s; }
.btn-login:hover .arrow-icon { transform: translateX(4px); }

.spinner {
  width: 20px; height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* Footer */
.footer-link {
  margin-top: 24px;
  text-align: center;
  font-size: 13px;
  color: #7f8c8d;
  cursor: pointer;
  display: flex; justify-content: center; align-items: center; gap: 6px;
  transition: color 0.2s;
}
.footer-link:hover { color: #55ab62; }

.copyright {
  margin-top: 40px;
  font-size: 12px;
  color: #bdc3c7;
  text-align: center;
}

/* Animations */
@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}
@keyframes spin { to { transform: rotate(360deg); } }

.slide-in { animation: slideUpFade 0.8s cubic-bezier(0.2, 0.8, 0.2, 1); }
@keyframes slideUpFade {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>