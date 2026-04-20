<template>
  <div class="user-center-container">
    <div class="center-content">
      <!-- Left Sidebar: Profile Card -->
      <div class="user-sidebar hover-lift">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" class="user-avatar" :src="form.avatarUrl">
               <span v-if="!form.avatarUrl && form.nickname">{{ form.nickname.charAt(0) }}</span>
               <el-icon v-else-if="!form.avatarUrl"><UserFilled /></el-icon>
            </el-avatar>
            <div class="status-ring"></div>
            <div class="upload-trigger">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
          <h2 class="user-name">{{ form.nickname || form.username || '茶友' }}</h2>
          <div class="user-badge">
            <span class="badge-text" v-if="form.level === 1">普通会员</span>
            <span class="badge-text gold" v-else>高级会员</span>
          </div>
        </div>

        <div class="info-list">
          <div class="info-row">
            <span class="label">账号ID</span>
            <span class="val">@{{ form.username }}</span>
          </div>
          <div class="info-row">
            <span class="label">注册时间</span>
            <span class="val">{{ formatDate(form.createTime) }}</span>
          </div>
          <el-divider class="tea-divider" />
          <div class="nav-menu">
            <div class="menu-item active">
              <el-icon><User /></el-icon> 资料设置
            </div>
            <div class="menu-item" @click="$router.push('/front/myorder')">
              <el-icon><List /></el-icon> 我的订单
            </div>
            <div class="menu-item logout" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon> 退出登录
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel: Edit Form -->
      <div class="edit-panel glass-morphism slide-up">
        <div class="panel-header">
          <div class="header-text">
            <h3>个人资料</h3>
            <p>完善您的信息，享受更优质的服务</p>
          </div>
          <button class="btn-save" @click="saveUserInfo" :disabled="loading">
            <span v-if="!loading">保存修改</span>
            <span v-else>保存中...</span>
          </button>
        </div>

        <el-form :model="form" label-position="top" class="tea-form">
          <div class="form-grid">
            <el-form-item label="登录账号 (不可修改)">
              <div class="input-wrapper disabled">
                <el-icon class="field-icon"><User /></el-icon>
                <input v-model="form.username" disabled readonly />
              </div>
            </el-form-item>

            <el-form-item label="昵称">
              <div class="input-wrapper">
                <el-icon class="field-icon"><Edit /></el-icon>
                <input v-model="form.nickname" placeholder="您的称呼" />
              </div>
            </el-form-item>

            <el-form-item label="手机号码">
              <div class="input-wrapper">
                <el-icon class="field-icon"><Iphone /></el-icon>
                <input v-model="form.phone" placeholder="绑定手机号" />
              </div>
            </el-form-item>

            <el-form-item label="登录密码">
              <div class="input-wrapper">
                <el-icon class="field-icon"><Lock /></el-icon>
                <input 
                  v-model="form.password" 
                  type="password" 
                  placeholder="修改密码 (留空则不修改)" 
                />
              </div>
            </el-form-item>

            <el-form-item label="收货地址" class="full-width">
              <div class="input-wrapper textarea-wrapper">
                <el-icon class="field-icon up-align"><Location /></el-icon>
                <textarea 
                  v-model="form.address" 
                  placeholder="请输入您的详细收货地址..." 
                  rows="3"
                ></textarea>
              </div>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Edit, Iphone, Lock, Location, List, SwitchButton, Camera } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()
const loading = ref(false)

const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  address: '',
  password: '',
  createTime: '',
  avatarUrl: ''
})

const formatDate = (time) => {
  if (!time) return '--'
  if (time.includes('T')) return time.split('T')[0]
  if (time.includes(' ')) return time.split(' ')[0]
  return time.substring(0, 10)
}

const loadUser = () => {
  const userStr = localStorage.getItem('tea-user')
  if (userStr) {
    const user = JSON.parse(userStr)
    Object.assign(form, user)
    form.password = '' 
  } else {
    router.push('/front/login')
  }
}

const saveUserInfo = async () => {
  loading.value = true
  try {
    const params = { ...form }
    if (!params.password) delete params.password
    
    const res = await axios.put('/user/update', params)
    if (res.data.code === '200') {
      ElMessage.success('个人信息更新成功')
      // Update LocalStorage
      const oldUser = JSON.parse(localStorage.getItem('tea-user'))
      const newUser = { ...oldUser, ...params }
      delete newUser.password
      localStorage.setItem('tea-user', JSON.stringify(newUser))
      
      // Dispatch event to update Layout header
      window.dispatchEvent(new Event('refreshUser'))
    } else {
      ElMessage.error(res.data.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('tea-user')
  router.push('/front/login')
  ElMessage.success('已安全退出')
}

onMounted(() => loadUser())
</script>

<style scoped>
.user-center-container {
  max-width: 1000px;
  margin: 40px auto;
  padding: 0 20px;
  min-height: 80vh;
}

.center-content {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

/* Sidebar */
.user-sidebar {
  width: 280px;
  background: white;
  border-radius: var(--radius-lg);
  padding: 40px 24px;
  text-align: center;
  box-shadow: var(--shadow-sm);
  border: 1px solid #f0f4f2;
  transition: all 0.3s;
}
.hover-lift:hover { transform: translateY(-4px); box-shadow: var(--shadow-md); }

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 20px;
}
.user-avatar {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  font-size: 32px;
  font-weight: 600;
  border: 3px solid white;
  box-shadow: 0 8px 20px rgba(0,0,0,0.06);
}
.status-ring {
  position: absolute; top: -5px; left: -5px; right: -5px; bottom: -5px;
  border: 1px dashed var(--tea-primary);
  border-radius: 50%;
  opacity: 0.3;
  animation: spin 30s linear infinite;
}
.upload-trigger {
  position: absolute; bottom: 0; right: 0;
  width: 32px; height: 32px;
  background: white; border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex; align-items: center; justify-content: center;
  color: var(--text-secondary); cursor: pointer;
  transition: all 0.2s;
}
.upload-trigger:hover { color: var(--tea-primary); transform: scale(1.1); }

.user-name {
  font-family: 'Songti SC', serif;
  font-size: 22px;
  color: var(--text-main);
  margin-bottom: 8px;
}
.user-badge .badge-text {
  font-size: 11px;
  padding: 4px 12px;
  background: #f1f5f2;
  color: #6b7280;
  border-radius: var(--radius-full);
}
.user-badge .badge-text.gold { background: #fdf8e6; color: #b45309; }

.info-list { margin-top: 30px; text-align: left; }
.info-row {
  display: flex; justify-content: space-between;
  font-size: 13px; color: var(--text-secondary);
  margin-bottom: 12px;
}
.info-row .val { color: var(--text-main); font-weight: 500; font-family: monospace; }

.tea-divider { margin: 24px 0; border-color: #f0f0f0; }

.nav-menu .menu-item {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}
.menu-item:hover { background: #f9fafb; color: var(--text-main); }
.menu-item.active { background: var(--tea-primary-light); color: var(--tea-primary); font-weight: 600; }
.menu-item.logout { color: #ef4444; }
.menu-item.logout:hover { background: #fef2f2; }

/* Edit Panel */
.edit-panel {
  flex: 1;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 40px;
  border: 1px solid white;
  box-shadow: var(--shadow-sm);
}

.panel-header {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: 30px; border-bottom: 1px solid #f0f0f0; padding-bottom: 20px;
}
.header-text h3 { font-family: 'Songti SC', serif; font-size: 24px; margin: 0 0 6px; color: var(--text-main); }
.header-text p { font-size: 13px; color: var(--text-placeholder); margin: 0; }

.btn-save {
  background: var(--tea-primary); color: white; border: none;
  padding: 10px 28px; border-radius: var(--radius-full);
  font-weight: 600; cursor: pointer; transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.25);
}
.btn-save:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(85, 171, 98, 0.35); }
.btn-save:disabled { background: #ccc; cursor: not-allowed; box-shadow: none; transform: none; }

/* Custom Form */
.form-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 24px;
}
.full-width { grid-column: 1 / -1; }

:deep(.el-form-item__label) { color: var(--text-secondary); padding-bottom: 8px; }

.input-wrapper {
  position: relative;
  display: flex; align-items: center;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius-md);
  padding: 4px 12px;
  transition: all 0.3s;
}
.input-wrapper:focus-within {
  border-color: var(--tea-primary);
  box-shadow: 0 0 0 3px rgba(85, 171, 98, 0.1);
}
.input-wrapper.disabled { background: #f9fafb; border-color: #f3f4f6; cursor: not-allowed; }

.field-icon { font-size: 16px; color: #9ca3af; margin-right: 12px; }
.up-align { align-self: flex-start; margin-top: 10px; }

.input-wrapper input, .input-wrapper textarea {
  border: none; outline: none; background: none; flex: 1; padding: 10px 0;
  font-size: 14px; color: var(--text-main); width: 100%;
}
.input-wrapper textarea { resize: none; line-height: 1.6; }

/* Animation */
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
.slide-up { animation: slideUp 0.6s ease-out; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

@media (max-width: 768px) {
  .center-content { flex-direction: column; }
  .user-sidebar { width: 100%; }
  .form-grid { grid-template-columns: 1fr; }
}
</style>