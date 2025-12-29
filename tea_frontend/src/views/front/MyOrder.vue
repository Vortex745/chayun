<template>
  <div class="main-content">

    <div class="page-header">
      <h2>我的订单</h2>
      <p>记录每一次美好的品茗之旅</p>
    </div>

    <div class="tabs-wrapper">
      <el-tabs v-model="activeName" @tab-click="filterOrders" class="custom-tabs">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待发货" name="待发货"></el-tab-pane>
        <el-tab-pane label="待收货" name="已发货"></el-tab-pane>
        <el-tab-pane label="已完成" name="已完成"></el-tab-pane>
      </el-tabs>
    </div>

    <div class="order-list" v-loading="loading">
      <div v-if="filteredList.length > 0">
        <div class="order-card" v-for="order in filteredList" :key="order.id">

          <div class="card-header">
            <div class="header-left">
              <span class="order-time">{{ order.createTime }}</span>
              <span class="order-no">订单号：{{ order.orderNo || 'NO-'+order.id }}</span>
            </div>
            <div class="header-right">
              <el-tag v-if="order.state === '待发货'" type="warning" effect="light">等待卖家发货</el-tag>
              <el-tag v-else-if="order.state === '已发货'" type="success" effect="light">卖家已发货</el-tag>
              <el-tag v-else type="info" effect="plain">{{ order.state }}</el-tag>
            </div>
          </div>

          <div class="card-body">
            <div class="product-brief">
              <div class="icon-box">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="info">
                <div class="name">{{ order.goodsName || '茶韵精选好茶' }}</div>
                <div class="desc">收货人：{{ order.username }} | {{ order.address }}</div>
              </div>
            </div>
            <div class="price-brief">
              <span class="label">实付金额</span>
              <span class="amount"><small>¥</small>{{ order.totalPrice }}</span>
            </div>
          </div>

          <div class="logistics-bar" v-if="['已发货', '已完成'].includes(order.state) && order.courierCompany">
            <el-icon><Van /></el-icon>
            <span v-if="order.deliveryMethod === '自提'">配送方式：客户自提</span>
            <span v-else>{{ order.courierCompany }} 运单号：{{ order.trackingNumber }}</span>
          </div>

          <div class="card-footer">
            <el-button v-if="order.state === '已发货'" type="primary" plain round size="small" @click="confirmReceive(order)">确认收货</el-button>
            <el-button type="danger" link size="small" @click="del(order.id)">删除订单</el-button>
          </div>

        </div>
      </div>

      <el-empty v-else description="暂无相关订单" :image-size="120"></el-empty>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, Van } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const user = JSON.parse(localStorage.getItem('tea-user') || '{}')

const loading = ref(false)
const orderList = ref([])
const filteredList = ref([])
const activeName = ref('all')

const loadOrders = async () => {
  if(!user.id) return
  loading.value = true
  try {
    // 假设后端有个接口查用户订单，这里暂用 list 模拟，实际应为 /order/user/{userId}
    // 如果没有专门接口，就查 all 然后前端过滤 userId (不推荐但可用)
    // 现假设有：GET /order/list (返回所有，这里仅作演示过滤)
    const res = await axios.get('/order/list')
    if(res.data.code === '200') {
      // 简单模拟：前端过滤当前用户
      const allOrders = res.data.data.filter(v => v.userId === user.id || v.username === user.username)
      orderList.value = allOrders
      filterOrders()
    }
  } catch(e) {}
  finally { loading.value = false }
}

// 前端过滤 Tab
const filterOrders = () => {
  if(activeName.value === 'all') {
    filteredList.value = orderList.value
  } else {
    filteredList.value = orderList.value.filter(v => v.state === activeName.value)
  }
}

const confirmReceive = (order) => {
  ElMessageBox.confirm('确认收到货物了吗？', '提示', { type: 'success' })
      .then(async () => {
        // 这里调用更新状态接口，简化演示
        order.state = '已完成'
        // 实际应调用 axios.put('/order/update', order)
        ElMessage.success('交易完成！')
      }).catch(() => {})
}

const del = (id) => {
  ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
      .then(async () => {
        await axios.delete('/order/delete/' + id)
        ElMessage.success('删除成功')
        loadOrders()
      }).catch(() => {})
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.main-content { max-width: 1000px; margin: 0 auto; }
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h2 { color: #2c3e50; font-weight: 800; margin-bottom: 5px; }
.page-header p { color: #999; font-size: 13px; letter-spacing: 1px; }

/* Tabs 样式优化 */
.tabs-wrapper { display: flex; justify-content: center; margin-bottom: 20px; }
:deep(.el-tabs__nav-wrap::after) { height: 1px; background-color: #f0f0f0; }
:deep(.el-tabs__item) { font-size: 15px; }
:deep(.el-tabs__item.is-active) { color: #55ab62; font-weight: bold; }
:deep(.el-tabs__active-bar) { background-color: #55ab62; }

/* 订单卡片 */
.order-card {
  background: white; border-radius: 12px; margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03); overflow: hidden;
  border: 1px solid #f7f9fc; transition: all 0.3s;
}
.order-card:hover { box-shadow: 0 8px 25px rgba(0,0,0,0.06); transform: translateY(-2px); }

/* 头部 */
.card-header {
  padding: 12px 20px; background: #fafafa; border-bottom: 1px solid #f0f0f0;
  display: flex; justify-content: space-between; align-items: center; font-size: 13px; color: #666;
}
.header-left span { margin-right: 15px; }

/* 内容区 */
.card-body { padding: 20px; display: flex; justify-content: space-between; align-items: center; }
.product-brief { display: flex; align-items: center; gap: 15px; }
.icon-box {
  width: 50px; height: 50px; background: #eef7f2; color: #55ab62;
  border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;
}
.info .name { font-weight: bold; color: #333; margin-bottom: 5px; font-size: 15px; }
.info .desc { font-size: 13px; color: #999; }

.price-brief { text-align: right; }
.price-brief .label { font-size: 12px; color: #999; display: block; }
.price-brief .amount { font-size: 20px; color: #333; font-weight: bold; }
.price-brief .amount small { font-size: 14px; }

/* 物流条 */
.logistics-bar {
  background: #fdfdfd; padding: 8px 20px; font-size: 12px; color: #55ab62;
  border-top: 1px dashed #f0f0f0; display: flex; align-items: center; gap: 8px;
}

/* 底部操作 */
.card-footer {
  padding: 12px 20px; border-top: 1px solid #f0f0f0; text-align: right;
}
</style>