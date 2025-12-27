<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-title">🍵 茶韵商城后台</div>

      <el-form :model="form" class="login-form">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%;" size="large" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if(!form.username || !form.password) {
    return ElMessage.warning('请输入账号和密码')
  }

  loading.value = true
  try {
    const res = await axios.post('/admin/login', form)
    if(res.data.code === '200') {
      ElMessage.success('登录成功')

      // 1. 把用户信息存到浏览器的 localStorage 里 (相当于领了通行证)
      // JSON.stringify 是把对象转成字符串
      localStorage.setItem('tea-admin-user', JSON.stringify(res.data.data))

      // 2. 跳转到首页
      router.push('/tea')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch(e) {
    ElMessage.error('连接服务器失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%); /* 绿色渐变背景 */
}
.login-card { width: 400px; padding: 20px; border-radius: 10px; }
.login-title { text-align: center; font-size: 24px; font-weight: bold; color: #333; margin-bottom: 30px; }
</style>