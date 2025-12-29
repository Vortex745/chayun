<template>
  <div class="user-center-wrapper">
    <div class="profile-card">
      <div class="profile-layout">

        <div class="left-panel">
          <div class="avatar-section">
            <div class="avatar-box">
              <el-avatar :size="90" class="user-avatar">
                <span v-if="!form.nickname">User</span>
                <span v-else>{{ form.nickname.charAt(0) }}</span>
              </el-avatar>
              <div class="avatar-ring"></div>
            </div>
            <h3 class="user-name">{{ form.nickname || form.username }}</h3>
            <div class="user-tag">普通会员</div>
          </div>

          <div class="info-group">
            <div class="info-item">
              <span class="label">账号 ID</span>
              <span class="value">@{{ form.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册日期</span>
              <span class="value">{{ formatDate(form.createTime) }}</span>
            </div>
          </div>
        </div>

        <div class="right-panel">
          <div class="panel-header">
            <div class="title-block">
              <span class="main-title">个人资料设置</span>
              <span class="sub-title">完善您的基本信息</span>
            </div>
          </div>

          <el-form :model="form" label-position="top" class="edit-form" size="large">

            <el-row :gutter="30">
              <el-col :span="12">
                <el-form-item label="登录账号">
                  <el-input v-model="form.username" disabled class="custom-input disabled-input">
                    <template #prefix><el-icon><User /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户昵称">
                  <el-input v-model="form.nickname" placeholder="请输入昵称" class="custom-input">
                    <template #prefix><el-icon><Edit /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="30">
              <el-col :span="12">
                <el-form-item label="手机号码">
                  <el-input v-model="form.phone" placeholder="绑定手机号" class="custom-input">
                    <template #prefix><el-icon><Iphone /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="登录密码">
                  <el-input
                      v-model="form.password"
                      type="password"
                      placeholder="不修改请留空"
                      show-password
                      class="custom-input"
                  >
                    <template #prefix><el-icon><Lock /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="收货地址">
              <el-input
                  v-model="form.address"
                  placeholder="请输入详细收货地址"
                  class="custom-input"
              >
                <template #prefix><el-icon><Location /></el-icon></template>
              </el-input>
            </el-form-item>

            <div class="form-footer">
              <el-button type="primary" class="save-btn" @click="saveUserInfo" :loading="loading">
                保存修改
              </el-button>
            </div>
          </el-form>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Edit, Iphone, Lock, Location } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const loading = ref(false)

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  address: '',
  password: '',
  createTime: ''
})

// 🔥 核心修复：日期格式化逻辑
const formatDate = (time) => {
  if (!time) return '--'
  // 1. 如果包含 'T' (例如 2025-12-26T11:41...)，按 T 分割取前半部分
  if (time.includes('T')) {
    return time.split('T')[0]
  }
  // 2. 如果包含空格 (例如 2025-12-26 11:41...)，按空格分割
  if (time.includes(' ')) {
    return time.split(' ')[0]
  }
  // 3. 兜底：只取前10位
  return time.substring(0, 10)
}

const loadUser = () => {
  const userStr = localStorage.getItem('tea-user')
  if (userStr) {
    const user = JSON.parse(userStr)
    Object.assign(form, user)
    form.password = ''
  }
}

const saveUserInfo = async () => {
  loading.value = true
  try {
    const params = { ...form }
    if (!params.password) delete params.password

    const res = await axios.put('/user/update', params)

    if (res.data.code === '200') {
      ElMessage.success('保存成功')
      const oldUser = JSON.parse(localStorage.getItem('tea-user'))
      const newUser = { ...oldUser, ...params }
      delete newUser.password
      localStorage.setItem('tea-user', JSON.stringify(newUser))
      window.dispatchEvent(new Event('refreshUser'))
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('网络异常')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadUser())
</script>

<style scoped>
/* 容器：适中的边距 */
.user-center-wrapper {
  max-width: 960px;
  margin: 50px auto;
  padding: 0 20px;
  animation: fadeIn 0.6s ease;
}

.profile-card {
  background: white; border-radius: 20px; overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.06);
  border: 1px solid rgba(0,0,0,0.02);
}

.profile-layout {
  display: flex;
  min-height: 520px; /* 🔥 高度调整：比之前高一点，比第一版矮一点，视觉更舒适 */
}

/* === 左侧面板 === */
.left-panel {
  width: 280px;
  background: linear-gradient(180deg, #f7fbf9 0%, #ffffff 100%);
  padding: 50px 30px;
  display: flex; flex-direction: column; align-items: center; justify-content: flex-start;
  border-right: 1px solid #f0f0f0;
}

.avatar-section { text-align: center; margin-bottom: 50px; }
.avatar-box { position: relative; display: inline-block; margin-bottom: 20px; }
.user-avatar {
  background: #55ab62; color: white; font-size: 36px; font-weight: bold;
  border: 4px solid white; box-shadow: 0 8px 20px rgba(85, 171, 98, 0.25);
}
.avatar-ring {
  position: absolute; top: -8px; left: -8px; right: -8px; bottom: -8px;
  border: 2px dashed #bce3c9; border-radius: 50%;
  animation: spin 25s linear infinite;
}

.user-name { font-size: 22px; color: #333; font-weight: 800; margin: 0 0 10px 0; letter-spacing: 0.5px; }
.user-tag {
  display: inline-block; background: #e8f5e9; color: #55ab62;
  font-size: 13px; padding: 4px 12px; border-radius: 12px; font-weight: 600;
}

.info-group { width: 100%; padding: 0 10px; }
.info-item { display: flex; justify-content: space-between; margin-bottom: 18px; font-size: 14px; border-bottom: 1px dashed #eee; padding-bottom: 12px; }
.info-item:last-child { border-bottom: none; }
.info-item .label { color: #999; }
.info-item .value { color: #555; font-weight: 500; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; }

/* === 右侧面板 === */
.right-panel { flex: 1; padding: 40px 50px; display: flex; flex-direction: column; justify-content: center; }

.panel-header { margin-bottom: 35px; border-left: 5px solid #55ab62; padding-left: 15px; }
.main-title { font-size: 20px; font-weight: 800; color: #2c3e50; display: block; margin-bottom: 4px; }
.sub-title { font-size: 13px; color: #999; }

/* 表单样式 */
.edit-form :deep(.el-form-item) { margin-bottom: 24px; } /* 增加间距，不拥挤 */
.edit-form :deep(.el-form-item__label) { padding-bottom: 8px; font-weight: 500; color: #606266; }

.custom-input :deep(.el-input__wrapper) {
  background-color: #f8fafc; box-shadow: none; border-radius: 8px; padding: 0 15px; height: 42px; /* 适中的高度 */
  border: 1px solid #eef2f6; transition: all 0.3s;
}
.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: white; border-color: #55ab62; box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1);
}
.disabled-input :deep(.el-input__wrapper) { background-color: #f5f7fa; color: #a8abb2; }

.form-footer { margin-top: 15px; }
.save-btn {
  width: 100%; height: 45px; border-radius: 25px; font-size: 16px; letter-spacing: 2px;
  background: linear-gradient(135deg, #55ab62, #429e50); border: none; font-weight: bold;
  box-shadow: 0 8px 20px rgba(85, 171, 98, 0.2); transition: all 0.3s;
}
.save-btn:hover { box-shadow: 0 10px 25px rgba(85, 171, 98, 0.3); transform: translateY(-2px); }

@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
</style>