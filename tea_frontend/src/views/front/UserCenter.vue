<template>
  <div class="user-center-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>👤 个人中心</span>
        </div>
      </template>

      <el-row :gutter="40">
        <el-col :span="8" style="text-align: center; border-right: 1px solid #eee;">
          <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          <div style="margin-top: 20px; font-weight: bold; font-size: 18px;">{{ form.username }}</div>
          <div style="color: #999; margin-top: 10px;">注册时间：{{ form.createTime }}</div>
        </el-col>

        <el-col :span="16">
          <el-form :model="form" label-width="80px" style="max-width: 500px;">
            <el-form-item label="账号">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input type="textarea" v-model="form.address" rows="3" />
            </el-form-item>
            <el-form-item label="修改密码">
              <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveUserInfo" :loading="loading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const loading = ref(false)

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  address: '',
  password: '', // 仅在修改时有值
  createTime: ''
})

// 1. 加载用户信息
const loadUser = () => {
  const userStr = localStorage.getItem('tea-user')
  if (userStr) {
    const user = JSON.parse(userStr)
    // 把缓存里的信息回填到表单
    Object.assign(form, user)
    // 密码默认清空，不显示旧密码
    form.password = ''
  }
}

// 2. 保存修改
const saveUserInfo = async () => {
  loading.value = true
  try {
    // 构造提交参数
    const params = { ...form }
    // 如果密码框是空的，说明用户不想改密码，删掉这个字段不传给后端
    if (!params.password) delete params.password

    const res = await axios.put('/user/update', params)

    if (res.data.code === '200') {
      ElMessage.success({ message: '修改成功', duration: 2000 })

      // 更新本地缓存 (非常重要！否则刷新页面又变回去了)
      // 注意：这里我们偷懒直接把表单数据存回去，严谨做法是重新调后端查一次
      const oldUser = JSON.parse(localStorage.getItem('tea-user'))
      const newUser = { ...oldUser, ...params }
      localStorage.setItem('tea-user', JSON.stringify(newUser))

      // 通知顶部导航栏更新名字 (之前写的那个监听器)
      window.dispatchEvent(new Event('refreshUser'))
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUser()
})
</script>

<style scoped>
.user-center-container { width: 1000px; margin: 0 auto; padding-bottom: 50px; }
.card-header { font-weight: bold; font-size: 16px; }
</style>