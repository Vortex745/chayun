<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="title">📦 订单管理</div>
    </el-card>

    <el-card shadow="never" style="margin-top: 15px;">
      <div style="margin-bottom: 15px;">
        <el-button :icon="Refresh" circle @click="loadOrders" />
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="订单号" width="80" align="center" />

        <el-table-column label="商品信息" width="200">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-image :src="row.imgUrl" style="width: 40px; height: 40px; margin-right: 10px; border-radius: 4px;" />
              <div>
                <div style="font-weight: bold;">{{ row.teaName }}</div>
                <div style="font-size: 12px; color: #999;">x {{ row.count }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c;">￥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>

        <el-table-column label="收货信息" min-width="200">
          <template #default="{ row }">
            <div>{{ row.consignee }} ({{ row.phone }})</div>
            <div style="font-size: 12px; color: #666;">{{ row.address }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待发货</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已发货</el-tag>
            <el-tag v-else type="info">已完成</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" size="small" @click="handleShip(row.id)">
              发货
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http

const tableData = ref([])
const loading = ref(false)

// 1. 加载订单
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await axios.get('/order/list')
    if (res.data.code === '200') {
      tableData.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 2. 发货
const handleShip = async (id) => {
  try {
    const res = await axios.post(`/order/ship/${id}`)
    if(res.data.code === '200') {
      ElMessage.success('发货成功！')
      loadOrders() // 刷新状态
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 3. 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除此订单吗？', '提示', { type: 'warning' })
      .then(async () => {
        const res = await axios.delete(`/order/delete/${id}`)
        if(res.data.code === '200') {
          ElMessage.success('删除成功')
          loadOrders()
        }
      })
      .catch(() => {})
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.title { font-weight: bold; color: #333; }
</style>