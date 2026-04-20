<template>
  <div class="orders-page container">
    <!-- Header -->
    <div class="page-header slide-up">
      <h2 class="title">我的订单</h2>
      <p class="subtitle">MEMORIES OF TEA • 品茗足迹</p>
    </div>

    <!-- Status Tabs -->
    <div class="tabs-container slide-up delay-100">
      <div class="tea-tabs">
        <div 
          v-for="tab in tabs" 
          :key="tab.name" 
          class="tab-item"
          :class="{ active: activeName === tab.name }"
          @click="changeTab(tab.name)"
        >
          {{ tab.label }}
          <div class="active-dot"></div>
        </div>
      </div>
    </div>

    <!-- Order List -->
    <div class="order-list-area" v-loading="loading">
      <transition-group name="list-fade">
        <div v-if="filteredList.length > 0">
          <div v-for="order in filteredList" :key="order.id" class="order-card hover-lift">
            <!-- Card Header -->
            <div class="card-head">
              <div class="meta-info">
                <span class="date">{{ order.createTime }}</span>
                <span class="order-no">订单号: {{ order.orderNo || 'NO-'+order.id }}</span>
              </div>
              <div class="status-badge" :class="getStatusClass(order.state)">
                {{ order.state }}
              </div>
            </div>

            <!-- Card Body -->
            <div class="card-main">
              <div class="product-info">
                <div class="img-wrapper">
                  <img :src="order.imgUrl" alt="" @error="handleImgErr" />
                </div>
                <div class="text-content">
                  <h4 class="tea-name">{{ order.teaName || '精选茶品' }}</h4>
                  <p class="spec">{{ order.spec || '标准包装' }} x {{ order.count }}</p>
                  <div class="shipping-info">
                    <span class="consignee">{{ order.consignee }}</span>
                    <span class="phone">{{ order.phone }}</span>
                    <p class="address">{{ order.address }}</p>
                  </div>
                </div>
              </div>

              <div class="price-summary">
                <span class="label">实付款</span>
                <span class="amount"><small>¥</small>{{ order.totalPrice }}</span>
              </div>
            </div>

            <!-- Logistics (Conditional) -->
            <div class="logistics-info glass-morphism" v-if="['已发货', '已完成'].includes(order.state) && order.courierCompany">
              <el-icon><Van /></el-icon>
              <span>{{ order.courierCompany }} | 运单号: {{ order.trackingNumber }}</span>
            </div>

            <!-- Action Footer -->
            <div class="card-foot">
              <button 
                v-if="order.state === '已发货'" 
                class="btn-primary-sm" 
                @click="confirmReceive(order)"
              >
                确认收货
              </button>
              <button class="btn-text-del" @click="del(order.id)">
                <el-icon><Delete /></el-icon> 移除记录
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state fade-in" :key="'empty'">
          <el-empty description="此类别下暂无订单" :image-size="160">
            <button class="btn-primary" @click="$router.push('/front/home')">去品尝新茶</button>
          </el-empty>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Van, Delete } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const user = JSON.parse(localStorage.getItem('tea-user') || '{}')

const loading = ref(false)
const orderList = ref([])
const filteredList = ref([])
const activeName = ref('all')

const tabs = [
  { label: '全部', name: 'all' },
  { label: '待发货', name: '待发货' },
  { label: '运输中', name: '已发货' },
  { label: '已完成', name: '已完成' }
]

const loadOrders = async () => {
  if(!user.id) return
  loading.value = true
  try {
    const res = await axios.get('/order/list')
    if(res.data.code === '200') {
      orderList.value = res.data.data.filter(v => v.userId === user.id || v.username === user.username)
      filterOrders()
    }
  } catch(e) { ElMessage.error('订单加载失败') }
  finally { loading.value = false }
}

const filterOrders = () => {
  if (activeName.value === 'all') {
    filteredList.value = orderList.value
  } else {
    filteredList.value = orderList.value.filter(v => v.state === activeName.value)
  }
}

const changeTab = (name) => {
  activeName.value = name
  filterOrders()
}

const getStatusClass = (state) => {
  if (state === '待发货') return 'status-warning'
  if (state === '已发货') return 'status-success'
  if (state === '已完成') return 'status-done'
  return ''
}

const confirmReceive = (order) => {
  ElMessageBox.confirm('确认收到心爱之茶了吗？', '确认收货', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'success' })
    .then(async () => {
      order.state = '已完成'
      // axios.put('/order/update', order)
      ElMessage.success('愿茶香常伴左右')
    }).catch(() => {})
}

const del = (id) => {
  ElMessageBox.confirm('确定要移除此条记录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(async () => {
      await axios.delete('/order/delete/' + id)
      ElMessage.success('已移除')
      loadOrders()
    }).catch(() => {})
}

const handleImgErr = (e) => {
  e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="100" height="100"%3E%3Crect fill="%23f5f5f5" width="100" height="100"/%3E%3C/svg%3E'
}

onMounted(() => loadOrders())
</script>

<style scoped>
.orders-page {
  padding-top: 40px;
  padding-bottom: 80px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}
.page-header .title {
  font-family: 'Songti SC', serif;
  font-size: 32px;
  color: var(--text-main);
  margin-bottom: 8px;
  letter-spacing: 4px;
}
.page-header .subtitle {
  font-size: 13px;
  color: var(--text-placeholder);
  letter-spacing: 2px;
}

/* Tabs */
.tabs-container {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}
.tea-tabs {
  display: flex;
  background: white;
  padding: 6px;
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-sm);
  border: 1px solid #f0f4f2;
}
.tab-item {
  padding: 10px 28px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
  border-radius: var(--radius-full);
}
.tab-item.active {
  color: var(--tea-primary);
  font-weight: 600;
  background: var(--tea-primary-light);
}
.active-dot {
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 4px;
  background: var(--tea-primary);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s;
}
.tab-item.active .active-dot { opacity: 1; }

/* Cards */
.order-list-area {
  max-width: 900px;
  margin: 0 auto;
}
.order-card {
  background: white;
  border-radius: var(--radius-lg);
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid #f0f4f2;
  overflow: hidden;
  transition: all 0.3s ease;
}

.card-head {
  padding: 16px 24px;
  background: #f9fafb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f2f2f2;
}
.meta-info { display: flex; gap: 20px; font-size: 13px; color: #9da3af; }
.status-badge {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  font-weight: 500;
}
.status-warning { background: #fffbeb; color: #d97706; }
.status-success { background: #ecfdf5; color: #059669; }
.status-done { background: #f3f4f6; color: #6b7280; }

.card-main {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.product-info { display: flex; gap: 20px; flex: 1; }
.img-wrapper {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid #f0f0f0;
  flex-shrink: 0;
}
.img-wrapper img { width: 100%; height: 100%; object-fit: cover; }

.text-content h4 { margin: 0 0 6px; font-size: 18px; color: var(--text-main); }
.text-content .spec { font-size: 13px; color: var(--text-placeholder); margin-bottom: 15px; }
.shipping-info { font-size: 12px; color: #9da3af; line-height: 1.6; }
.shipping-info .consignee { font-weight: 600; color: #6b7280; margin-right: 8px; }

.price-summary { text-align: right; min-width: 120px; }
.price-summary .label { font-size: 12px; color: var(--text-placeholder); display: block; margin-bottom: 4px; }
.price-summary .amount { font-size: 24px; font-weight: 800; color: var(--text-main); }
.amount small { font-size: 14px; margin-right: 2px; }

.logistics-info {
  margin: 0 24px 20px;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 12px;
  color: var(--tea-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}
.glass-morphism { background: var(--tea-primary-light); border: 1px solid #e1f0e6; }

.card-foot {
  padding: 16px 24px;
  border-top: 1px solid #f9fafb;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  align-items: center;
}

/* Custom Buttons */
.btn-primary-sm {
  background: var(--tea-primary);
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-primary-sm:hover { background: var(--tea-primary-hover); }

.btn-text-del {
  background: none;
  border: none;
  font-size: 12px;
  color: #d1d5db;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: color 0.2s;
}
.btn-text-del:hover { color: var(--tea-danger); }

/* Animation Utils */
.slide-up { animation: slideUp 0.6s ease-out forwards; opacity: 0; transform: translateY(20px); }
.delay-100 { animation-delay: 0.1s; }
.hover-lift:hover { transform: translateY(-4px); box-shadow: var(--shadow-md); }

@keyframes slideUp { to { opacity: 1; transform: translateY(0); } }

.list-fade-enter-active, .list-fade-leave-active { transition: all 0.5s ease; }
.list-fade-enter-from, .list-fade-leave-to { opacity: 0; transform: translateX(30px); }

/* Empty State */
.empty-state { padding: 60px 0; }
.btn-primary { 
  background: var(--tea-primary); color: white; border: none; padding: 12px 32px; border-radius: var(--radius-full); cursor: pointer; transition: all 0.3s;
}

@media (max-width: 640px) {
  .card-head { flex-direction: column; align-items: flex-start; gap: 10px; }
  .card-main { flex-direction: column; gap: 20px; }
  .price-summary { text-align: left; }
}
</style>