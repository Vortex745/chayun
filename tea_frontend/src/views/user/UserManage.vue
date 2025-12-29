<template>
  <div class="page-container">

    <div class="action-bar">
      <div class="bar-left">
        <span class="header-icon">👥</span>
        <span class="page-title">会员管理</span>
      </div>
      <div class="bar-right">
        <el-button :icon="Refresh" circle class="icon-btn" @click="loadUsers" title="刷新列表" />
      </div>
    </div>

    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          v-loading="loading"
          :header-cell-style="{ background: '#f8fdfa', color: '#666', fontWeight: '600', height: '50px' }"
          :row-style="{ height: '65px' }"
          highlight-current-row
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column label="用户信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">{{ (row.nickname || row.username).charAt(0) }}</el-avatar>
              <div class="user-text">
                <div class="username">{{ row.username }}</div>
                <div class="nickname">{{ row.nickname || '未设置昵称' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="手机号码" width="150" align="center">
          <template #default="{ row }">
            <span class="phone-text">{{ row.phone || '--' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="address" label="默认收货地址" min-width="250" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="address-box">
              <el-icon class="addr-icon"><Location /></el-icon>
              <span>{{ row.address || '暂无地址' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="注册时间" width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link class="btn-danger" :icon="Delete" @click="handleDelete(row.id)">注销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Delete, Location } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const tableData = ref([])
const loading = ref(false)

// 时间格式化工具
const formatDate = (time) => {
  if (!time) return '--'
  return time.replace('T', ' ').split('.')[0] // 简单处理：去掉T和毫秒
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/user/list')
    if(res.data.code === '200') tableData.value = res.data.data
  } catch(e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要注销该用户吗？此操作不可恢复。', '警告', {
    type: 'warning',
    confirmButtonText: '确定注销',
    confirmButtonClass: 'btn-confirm-delete' // 可选：自定义类名
  })
      .then(async () => {
        try {
          const res = await axios.delete(`/user/delete/${id}`)
          if(res.data.code === '200') {
            ElMessage.success('用户已注销')
            loadUsers()
          } else { ElMessage.error(res.data.msg) }
        } catch(e) { ElMessage.error('操作失败') }
      }).catch(() => {})
}

onMounted(() => loadUsers())
</script>

<style scoped>
.page-container { height: 100%; display: flex; flex-direction: column; gap: 20px; }

/* 1. 操作栏 (统一风格) */
.action-bar {
  background: white; padding: 15px 25px; border-radius: 16px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}
.bar-left { display: flex; align-items: center; gap: 10px; }
.header-icon { font-size: 20px; }
.page-title { font-size: 18px; font-weight: 800; color: #2c3e50; }

.icon-btn { color: #666; transition: all 0.3s; }
.icon-btn:hover { color: #55ab62; background: #eef7f2; transform: rotate(180deg); }

/* 2. 表格区域 */
.table-container { background: white; border-radius: 16px; padding: 5px; flex: 1; box-shadow: 0 4px 20px rgba(0,0,0,0.03); overflow: hidden; }

/* 表格内样式细节 */
.user-info { display: flex; align-items: center; gap: 12px; }
.user-avatar { background: #55ab62; color: white; font-weight: bold; font-size: 14px; border: 2px solid #f0f9eb; }
.user-text { display: flex; flex-direction: column; line-height: 1.3; }
.username { font-weight: bold; color: #333; font-size: 14px; }
.nickname { font-size: 12px; color: #999; }

.phone-text { font-family: 'Arial', sans-serif; color: #555; font-weight: 500; letter-spacing: 0.5px; }

.address-box { display: flex; align-items: center; color: #666; font-size: 13px; }
.addr-icon { margin-right: 4px; color: #999; }

.time-text { color: #999; font-size: 12px; font-family: monospace; }

.btn-danger { color: #f56c6c; }
.btn-danger:hover { background: #fef0f0; }
</style>