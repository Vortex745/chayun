<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="title">👥 会员管理</div>
    </el-card>

    <el-card shadow="never" style="margin-top: 15px;">
      <el-button :icon="Refresh" circle @click="loadUsers" style="margin-bottom: 15px;" />

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="address" label="默认地址" show-overflow-tooltip />
        <el-table-column prop="createTime" label="注册时间" width="180" align="center" />

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row.id)">注销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Delete } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const tableData = ref([])
const loading = ref(false)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/user/list')
    if(res.data.code === '200') tableData.value = res.data.data
  } catch(e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要注销该用户吗？此操作不可恢复。', '警告', { type: 'warning' })
      .then(async () => {
        const res = await axios.delete(`/user/delete/${id}`)
        if(res.data.code === '200') {
          ElMessage.success('用户已注销')
          loadUsers()
        }
      }).catch(() => {})
}

onMounted(() => loadUsers())
</script>
<style scoped> .title { font-weight: bold; } </style>