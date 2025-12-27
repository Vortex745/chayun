<template>
  <div class="login-bg">
    <div class="auth-box">
      <div class="auth-left">
        <div class="mask">
          <h2>茶韵 · 东方好茶</h2>
          <p>一杯好茶，品味人生浮沉。</p>
        </div>
      </div>

      <div class="auth-right">
        <div v-if="isLogin" class="form-container">
          <h3>用户登录</h3>
          <el-form size="large">
            <el-form-item>
              <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="UserIcon" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-button type="primary" style="width: 100%" @click="doLogin" :loading="loading">立即登录</el-button>
          </el-form>
          <div class="switch-type">
            还没有账号？ <span @click="isLogin = false">去注册</span>
            <span style="margin: 0 10px; color: #e0e0e0;">|</span>
            <span class="admin-link" @click="toAdminLogin">
      <el-icon style="vertical-align: middle;"><Setting /></el-icon> 管理员入口
    </span>
          </div>
        </div>

        <div v-else class="form-container">
          <h3>新用户注册</h3>
          <el-form size="large">
            <el-form-item>
              <el-input v-model="regForm.username" placeholder="设置账号" :prefix-icon="UserIcon" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" placeholder="设置密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.nickname" placeholder="您的昵称" :prefix-icon="Postcard" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.phone" placeholder="手机号码" :prefix-icon="Iphone" />
            </el-form-item>
            <el-button type="success" style="width: 100%" @click="doRegister" :loading="loading">立即注册</el-button>
          </el-form>
          <div class="switch-type">
            已有账号？ <span @click="isLogin = true">去登录</span>
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
import { User as UserIcon, Lock, Postcard, Iphone, Setting } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const isLogin = ref(true) // 控制显示登录还是注册
const loading = ref(false)

// 登录数据
const loginForm = reactive({ username: '', password: '' })
// 注册数据
const regForm = reactive({ username: '', password: '', nickname: '', phone: '' })

// 登录逻辑
const doLogin = async () => {
  if(!loginForm.username || !loginForm.password) return ElMessage.warning('请填写完整')

  loading.value = true
  try {
    const res = await axios.post('/user/login', loginForm)
    if(res.data.code === '200') {
      ElMessage.success('登录成功')
      // 存入 localStorage，注意 key 是 'tea-user'，和管理员的 'tea-admin-user' 区分开
      localStorage.setItem('tea-user', JSON.stringify(res.data.data))
      window.dispatchEvent(new Event('refreshUser'))
      // 跳转回商城首页
      router.push('/front/home')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch(e) { ElMessage.error('登录失败') }
  finally { loading.value = false }
}

// 注册逻辑
const doRegister = async () => {
  if(!regForm.username || !regForm.password) return ElMessage.warning('请填写完整')

  loading.value = true
  try {
    const res = await axios.post('/user/register', regForm)
    if(res.data.code === '200') {
      ElMessage.success('注册成功，请登录')
      isLogin.value = true // 切换回登录页
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch(e) { ElMessage.error('注册失败') }
  finally { loading.value = false }
}
const toAdminLogin = () => {
  // 跳转到我们之前写好的后台登录页路由 '/login'
  router.push('/login')
}
</script>

<style scoped>
.login-bg { height: 100vh; background: #f0f2f5; display: flex; justify-content: center; align-items: center; }
.auth-box { width: 800px; height: 500px; background: white; border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); display: flex; overflow: hidden; }
.auth-left { width: 50%; background: url('https://img.zcool.cn/community/01f4095e21545ea80120a895e63821.jpg@1280w_1l_2o_100sh.jpg') no-repeat center/cover; position: relative; }
.mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); display: flex; flex-direction: column; justify-content: center; align-items: center; color: white; }
.auth-right { width: 50%; padding: 40px; display: flex; flex-direction: column; justify-content: center; }
.form-container h3 { text-align: center; margin-bottom: 30px; color: #333; }
.switch-type { text-align: right; margin-top: 15px; font-size: 14px; color: #666; }
.switch-type span { color: #409EFF; cursor: pointer; font-weight: bold; }
.admin-link {
  color: #909399; /* 灰色，低调一点 */
  font-size: 13px;
  cursor: pointer;
  transition: color 0.3s;
}
.admin-link:hover {
  color: #333; /* 鼠标悬停变黑 */
}
</style>