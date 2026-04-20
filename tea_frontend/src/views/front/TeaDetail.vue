<template>
  <div class="detail-page-wrapper">
    <!-- Top Navigation -->
    <div class="nav-bar container">
      <div class="nav-back hover-lift" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回选茶</span>
      </div>
    </div>

    <!-- Main Product Section -->
    <section class="product-hero container">
      <!-- Left: Image Gallery -->
      <div class="hero-gallery">
        <div class="main-image-frame fade-in">
          <img v-if="tea.imgUrl" :src="tea.imgUrl" :alt="tea.name" class="main-img"/>
          <div v-else class="image-placeholder">
            <el-icon :size="48"><Picture /></el-icon>
          </div>
        </div>
      </div>

      <!-- Right: Product Info -->
      <div class="hero-info slide-up">
        <h1 class="product-title">{{ tea.name }}</h1>
        <p class="product-subtitle">{{ tea.content || '暂无描述' }}</p>

        <!-- Price Card -->
        <div class="price-panel">
          <div class="price-main">
            <span class="currency">¥</span>
            <span class="amount">{{ currentPrice }}</span>
            <span v-if="specOptions.length > 0" class="price-suffix">起</span>
          </div>
          <div class="meta-stats">
            <div class="stat-item">
              <span class="label">库存</span>
              <span class="val">{{ tea.stock }}</span>
            </div>
            <div class="divider"></div>
            <div class="stat-item">
              <span class="label">热度</span>
              <span class="val">{{ tea.clickCount || 0 }}</span>
            </div>
            <div class="divider"></div>
            <div class="stat-item">
              <span class="label">分类</span>
              <span class="val">{{ tea.type }}</span>
            </div>
          </div>
        </div>

        <!-- Service Assurance -->
        <div class="assurance-row">
          <div class="assurance-item">
            <el-icon color="#4A7C59"><CircleCheckFilled /></el-icon>
            <span>原产地直供</span>
          </div>
          <div class="assurance-item">
            <el-icon color="#4A7C59"><CircleCheckFilled /></el-icon>
            <span>极致保鲜</span>
          </div>
          <div class="assurance-item">
            <el-icon color="#4A7C59"><CircleCheckFilled /></el-icon>
            <span>破损包赔</span>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="action-buttons">
          <button class="btn-secondary" @click="openSpecDialog">
            <el-icon><ShoppingCart /></el-icon> 加入购物车
          </button>
          <button class="btn-primary" @click="openSpecDialog">
            <el-icon><Wallet /></el-icon> 立即购买
          </button>
        </div>
      </div>
    </section>

    <!-- Details Content -->
    <section class="details-content container slide-up delay-100">
      <div class="section-tabs">
        <div class="tab-item active">商品详情</div>
        <div class="tab-item">用户评价</div>
      </div>
      <div class="content-body">
        <div class="rich-text">
          <p>{{ tea.content }}</p>
        </div>
        <div class="detail-images" v-if="tea.imgUrl">
          <img :src="tea.imgUrl" alt="详情展示" />
        </div>
      </div>
    </section>

    <!-- Floating Service Button -->
    <div class="service-fab" @click="serviceVisible = true">
      <el-icon :size="24"><Service /></el-icon>
      <span class="fab-label">客服</span>
    </div>

    <!-- Spec Selection Dialog -->
    <el-dialog v-model="specVisible" align-center class="tea-dialog" width="440px" destroy-on-close :show-close="false">
      <template #header="{ close }">
        <div class="dialog-header">
          <h3>选择规格</h3>
          <el-icon class="close-btn" @click="close"><Close /></el-icon>
        </div>
      </template>
      
      <div class="spec-body">
        <!-- Product Check -->
        <div class="spec-product-summary">
          <div class="summary-img">
            <img :src="tea.imgUrl" />
          </div>
          <div class="summary-info">
            <div class="summary-price">¥{{ currentPrice }}</div>
            <div class="summary-stock">库存 {{ tea.stock }} 件</div>
            <div class="summary-choice">已选: {{ selectedSpecName }}</div>
          </div>
        </div>

        <div class="spec-divider"></div>

        <!-- Specs -->
        <div class="spec-group">
          <div class="group-label">规格</div>
          <div class="tags-container">
            <template v-if="specOptions.length > 0">
              <div 
                v-for="(item, index) in specOptions" 
                :key="index"
                class="spec-chip"
                :class="{ active: selectedSpecName === item.name }"
                @click="selectSpec(item)"
              >
                {{ item.name }}
              </div>
            </template>
            <template v-else>
              <div class="spec-chip active">标准规格</div>
            </template>
          </div>
        </div>

        <!-- Count -->
        <div class="spec-group count-group">
          <div class="group-label">数量</div>
          <el-input-number v-model="count" :min="1" :max="tea.stock" size="small" />
        </div>
      </div>

      <template #footer>
        <div class="dialog-actions">
          <button class="btn-secondary flex-1" @click="confirmAddToCart">加入购物车</button>
          <button class="btn-primary flex-1" @click="confirmToBuy">立即购买</button>
        </div>
      </template>
    </el-dialog>

    <!-- Address & Payment Dialog -->
    <el-dialog v-model="addressVisible" align-center class="tea-dialog" width="480px" title="确认订单">
      <el-form :model="addressForm" label-position="top" ref="addressFormRef" :rules="addressRules" class="tea-form">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="addressForm.consignee" placeholder="您的姓名"/>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="您的手机号码"/>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="addressForm.address" type="textarea" :rows="2" placeholder="省市区街道门牌号"/>
        </el-form-item>
        
        <div class="order-total-bar">
          <span>订单合计</span>
          <span class="total-price">¥{{ (currentPrice * count).toFixed(2) }}</span>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-actions">
          <button class="btn-flat" @click="addressVisible = false">取消</button>
          <button class="btn-primary wide" :disabled="paying" @click="handlePayment">
             <el-icon v-if="paying" class="is-loading"><Loading /></el-icon>
             <span v-else>确认支付</span>
          </button>
        </div>
      </template>
    </el-dialog>

    <!-- Service Dialog -->
    <el-dialog v-model="serviceVisible" align-center class="tea-dialog" width="360px" title="联系客服">
      <div class="service-menu">
        <div class="menu-item" @click="showPhone = !showPhone">
          <div class="icon-circle phone-icon"><el-icon><PhoneFilled /></el-icon></div>
          <div class="menu-txt">
            <h4>电话咨询</h4>
            <p v-if="!showPhone">点击查看热线号码</p>
            <p v-else class="highlight-num">400-888-6666</p>
          </div>
        </div>
        <div class="menu-item" @click="openMessageForm">
          <div class="icon-circle msg-icon"><el-icon><ChatDotRound /></el-icon></div>
          <div class="menu-txt">
            <h4>在线留言</h4>
            <p>店主稍后回复</p>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- Message Form Dialog -->
    <el-dialog v-model="messageFormVisible" align-center class="tea-dialog" width="400px" title="留言反馈">
      <el-form :model="msgForm" label-position="top" class="tea-form">
        <el-form-item label="留言内容">
          <el-input v-model="msgForm.content" type="textarea" :rows="4" placeholder="请描述您的问题..." />
        </el-form-item>
        <el-form-item label="回访电话 (可选)">
          <el-input v-model="msgForm.phone" placeholder="方便我们联系您" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-actions">
          <button class="btn-flat" @click="messageFormVisible = false">取消</button>
          <button class="btn-primary" @click="submitMessage">发送留言</button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ChatDotRound, CircleCheckFilled, PhoneFilled, Picture, ShoppingCart, Wallet, Service, Close, Loading } from '@element-plus/icons-vue'
import { runWithStatusMessage } from '../../utils/message'

defineOptions({ name: 'TeaDetail' })

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const route = useRoute()
const router = useRouter()

const tea = ref({})
const count = ref(1)
const user = ref({})
const specVisible = ref(false)
const addressVisible = ref(false)
const paying = ref(false)

// Forms
const addressFormRef = ref(null)
const addressForm = reactive({ consignee: '', phone: '', address: '' })
const addressRules = {
  consignee: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const serviceVisible = ref(false)
const messageFormVisible = ref(false)
const showPhone = ref(false)
const msgForm = reactive({ content: '', phone: '' })

// Specs
const selectedSpecName = ref('标准规格')
const specOptions = computed(() => {
  if (tea.value?.specs) {
    try {
      if (tea.value.specs.includes('[')) return JSON.parse(tea.value.specs)
    } catch (e) {}
  }
  return []
})

const currentPrice = computed(() => {
  if (specOptions.value.length > 0) {
    const target = specOptions.value.find(s => s.name === selectedSpecName.value)
    return target ? target.price : specOptions.value[0].price
  }
  return tea.value.price
})

// Lifecycle
onMounted(() => {
  window.scrollTo(0, 0)
  const userStr = localStorage.getItem('tea-user')
  if (userStr) user.value = JSON.parse(userStr)
  const id = route.params.id
  if (id) loadTeaDetail(id)
})

// Methods
const goBack = () => {
  window.history.length > 1 ? router.back() : router.push('/front/home')
}

const loadTeaDetail = async (id) => {
  try {
    const res = await axios.get(`/tea/${id}`)
    if (res.data.code === '200') {
      tea.value = res.data.data
      if (specOptions.value.length > 0) selectedSpecName.value = specOptions.value[0].name
    }
  } catch (e) {
    ElMessage.error('加载商品失败')
  }
}

const selectSpec = (item) => selectedSpecName.value = item.name

const openSpecDialog = () => {
  if (!user.value.id) return (ElMessage.warning('请先登录'), router.push('/front/login'))
  specVisible.value = true
}

const confirmAddToCart = async () => {
  try {
    const res = await runWithStatusMessage({
      pendingMessage: '正在加入购物车...',
      successMessage: '已加入购物车',
      errorMessage: '加入购物车失败',
      task: () => axios.post('/cart/add', {
        userId: user.value.id,
        teaId: tea.value.id,
        count: count.value,
        spec: selectedSpecName.value
      })
    })
    if (res.data.code === '200') {
      specVisible.value = false
    } else {
      ElMessage.error(res.data.msg || '加入购物车失败')
    }
  } catch (e) {}
}

const confirmToBuy = () => {
  specVisible.value = false
  addressVisible.value = true
  addressForm.consignee = user.value.name || user.value.username || ''
  addressForm.phone = user.value.phone || ''
  addressForm.address = user.value.address || ''
}

const handlePayment = async () => {
  if (!addressFormRef.value) return
  try { await addressFormRef.value.validate() } catch { return }
  
  paying.value = true
  try {
    const res = await runWithStatusMessage({
      pendingMessage: '正在创建订单...',
      successMessage: '下单成功，正在为您跳转',
      errorMessage: '下单失败，请稍后重试',
      task: () => axios.post('/order/buyNow', {
        userId: user.value.id,
        teaId: tea.value.id,
        teaName: tea.value.name,
        imgUrl: tea.value.imgUrl,
        count: count.value,
        price: currentPrice.value,
        consignee: addressForm.consignee,
        phone: addressForm.phone,
        address: addressForm.address,
        spec: selectedSpecName.value
      })
    })
    if (res.data.code === '200') {
      addressVisible.value = false
      loadTeaDetail(tea.value.id)
      ElMessage.info('2秒后返回首页')
      setTimeout(() => router.push('/front/home'), 2000)
    } else {
      ElMessage.error(res.data.msg || '下单失败')
    }
  } catch (e) {}
  finally { paying.value = false }
}

const openMessageForm = () => {
  if (!user.value.id) return (ElMessage.warning('请先登录'), router.push('/front/login'))
  msgForm.content = ''
  msgForm.phone = user.value.phone || ''
  serviceVisible.value = false
  messageFormVisible.value = true
}

const submitMessage = async () => {
  if (!msgForm.content) return ElMessage.warning('请填写内容')
  try {
    const res = await axios.post('/message/add', {
      userId: user.value.id,
      username: user.value.username || user.value.name,
      content: msgForm.content,
      phone: msgForm.phone
    })
    if (res.data.code === '200') {
      ElMessage.success('留言成功')
      messageFormVisible.value = false
    } else ElMessage.error(res.data.msg)
  } catch (e) { ElMessage.error('发送失败') }
}
</script>

<style scoped>
/* Base Layout */
.detail-page-wrapper {
  padding-bottom: 80px;
  background-color: var(--bg-body);
  min-height: 100vh;
}

.nav-bar {
  padding: 24px 0;
}
.nav-back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  font-weight: 500;
  padding: 8px 16px;
  background: white;
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-sm);
  transition: all 0.2s ease;
}
.nav-back:hover {
  color: var(--tea-primary);
  transform: translateX(-4px);
}

/* Product Hero */
.product-hero {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Split screen */
  gap: 60px;
  margin-bottom: 60px;
  align-items: start;
}

.main-image-frame {
  width: 100%;
  aspect-ratio: 1; /* Square */
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #f8faf9;
  box-shadow: var(--shadow-md);
  position: relative;
}
.main-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.main-image-frame:hover .main-img {
  transform: scale(1.05);
}
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d1d5db;
}

/* Hero Info */
.hero-info {
  padding-top: 20px;
}
.product-title {
  font-family: 'Songti SC', serif;
  font-size: 36px;
  color: var(--text-main);
  margin: 0 0 12px;
  font-weight: 600;
}
.product-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: 32px;
  line-height: 1.6;
}

/* Price Panel */
.price-panel {
  background: linear-gradient(to right, rgba(74, 124, 89, 0.05), transparent);
  padding: 24px;
  border-radius: var(--radius-md);
  border-left: 4px solid var(--tea-primary);
  margin-bottom: 32px;
}
.price-main {
  color: var(--tea-danger);
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
}
.currency { font-size: 20px; font-weight: bold; margin-right: 4px; }
.amount { font-size: 40px; font-weight: 700; font-family: 'Arial', sans-serif; letter-spacing: -1px; }
.price-suffix { font-size: 14px; color: var(--text-secondary); margin-left: 8px; }

.meta-stats {
  display: flex;
  align-items: center;
  gap: 0; 
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.stat-item .label { color: var(--text-placeholder); }
.stat-item .val { color: var(--text-main); font-weight: 500; }
.divider { 
  width: 1px; 
  height: 14px; 
  background: #e5e7eb; 
  margin: 0 16px; 
}

/* Assurance */
.assurance-row {
  display: flex;
  gap: 24px;
  margin-bottom: 40px;
}
.assurance-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 20px;
}
button {
  border: none;
  cursor: pointer;
  font-family: inherit;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 32px;
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
}
.btn-primary {
  background: var(--tea-primary);
  color: white;
  box-shadow: 0 8px 20px rgba(74, 124, 89, 0.25);
}
.btn-primary:hover {
  background: var(--tea-primary-hover);
  transform: translateY(-2px);
  box-shadow: 0 12px 25px rgba(74, 124, 89, 0.35);
}
.btn-secondary {
  background: white;
  color: var(--tea-primary);
  border: 1px solid var(--tea-primary);
}
.btn-secondary:hover {
  background: var(--tea-primary-light);
}

/* Details Content */
.details-content {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  padding: 40px;
  margin-top: 40px;
}
.section-tabs {
  display: flex;
  gap: 40px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 30px;
}
.tab-item {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-secondary);
  padding-bottom: 16px;
  cursor: pointer;
  position: relative;
}
.tab-item.active {
  color: var(--tea-primary);
}
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--tea-primary);
}
.rich-text {
  line-height: 1.8;
  color: #4b5563;
  margin-bottom: 40px;
  font-size: 16px;
}
.detail-images img {
  width: 100%;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}

/* FAB */
.service-fab {
  position: fixed;
  bottom: 80px;
  right: 40px;
  width: 50px;
  height: 50px;
  background: white;
  border-radius: 50%;
  box-shadow: var(--shadow-float);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--tea-primary);
  transition: transform 0.3s;
  z-index: 1000;
}
.service-fab:hover { transform: scale(1.1); }
.fab-label { font-size: 10px; font-weight: bold; margin-top: -2px; }

/* Dialog Customization */
:deep(.tea-dialog) {
  border-radius: 20px;
  overflow: hidden;
}
:deep(.el-dialog__header) { margin: 0; padding: 0; }
:deep(.el-dialog__body) { padding: 0; }
:deep(.el-dialog__footer) { padding: 0; }

.dialog-header {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.dialog-header h3 { margin: 0; font-size: 18px; color: var(--text-main); }
.close-btn { cursor: pointer; color: #999; font-size: 20px; }

.spec-body { padding: 0 24px 24px; }
.spec-product-summary { display: flex; gap: 16px; margin-bottom: 20px; }
.summary-img { width: 80px; height: 80px; border-radius: 8px; overflow: hidden; background: #f5f5f5; }
.summary-img img { width: 100%; height: 100%; object-fit: cover; }
.summary-info { display: flex; flex-direction: column; justify-content: center; }
.summary-price { font-size: 20px; color: var(--tea-danger); font-weight: bold; }
.summary-stock { font-size: 12px; color: #999; margin: 4px 0; }
.summary-choice { font-size: 13px; color: var(--text-main); }

.spec-divider { height: 1px; background: #f0f0f0; margin-bottom: 20px; }

.spec-group { margin-bottom: 20px; }
.group-label { font-size: 14px; color: var(--text-main); font-weight: 500; margin-bottom: 12px; }
.tags-container { display: flex; flex-wrap: wrap; gap: 12px; }
.spec-chip {
  padding: 8px 16px;
  background: #f5f7fa;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--text-main);
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}
.spec-chip.active {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  border-color: var(--tea-primary);
  font-weight: 500;
}
.count-group { display: flex; align-items: center; justify-content: space-between; }

.dialog-actions {
  padding: 16px 24px 24px;
  display: flex;
  gap: 16px;
}
.flex-1 { flex: 1; }
.wide { width: 100%; }

/* Service Menu */
.service-menu { padding: 10px 24px 30px; }
.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: background 0.2s;
}
.menu-item:hover { background: #f0f9eb; }
.icon-circle {
  width: 40px; height: 40px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 20px;
}
.phone-icon { background: #55ab62; }
.msg-icon { background: #f59e0b; }
.menu-txt h4 { margin: 0 0 4px; font-size: 15px; color: var(--text-main); }
.menu-txt p { margin: 0; font-size: 12px; color: #999; }
.highlight-num { color: var(--tea-primary); font-weight: bold; font-size: 16px; }

/* Form Styles */
.tea-form { padding: 0 24px 24px; }
.btn-flat { background: #f5f7fa; color: #666; padding: 12px 24px; }
.btn-flat:hover { background: #e5e7eb; }
.order-total-bar {
  display: flex; justify-content: space-between; align-items: center;
  margin-top: 10px; padding-top: 20px; border-top: 1px dashed #eee;
  font-weight: bold;
}
.total-price { color: var(--tea-danger); font-size: 20px; }

/* Animations */
.slide-up { animation: slideUp 0.6s ease-out forwards; opacity: 0; transform: translateY(20px); }
.fade-in { animation: fadeIn 0.8s ease-out forwards; opacity: 0; }
.delay-100 { animation-delay: 0.1s; }
.hover-lift:hover { transform: translateY(-2px); }

@keyframes slideUp { to { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { to { opacity: 1; } }

@media (max-width: 768px) {
  .product-hero { grid-template-columns: 1fr; gap: 30px; }
  .action-buttons { position: fixed; bottom: 0; left: 0; width: 100%; background: white; padding: 16px; box-shadow: 0 -4px 10px rgba(0,0,0,0.05); z-index: 100; justify-content: space-between; gap: 10px; }
  .btn-secondary, .btn-primary { flex: 1; padding: 12px; font-size: 14px; }
  .details-content { margin-bottom: 80px; }
  .service-fab { bottom: 90px; }
}
</style>
