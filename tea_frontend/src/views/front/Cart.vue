<template>
  <div class="cart-page-wrapper container">
    <!-- Header -->
    <div class="page-header slide-up">
      <h2 class="title">我的茶篮子</h2>
      <p class="subtitle">PREMIUM SELECTION • 精挑细选</p>
    </div>

    <div v-loading="loading" class="cart-content-area">
      <!-- Empty State -->
      <div v-if="!loading && cartList.length === 0" class="empty-state fade-in">
        <el-empty description="您的茶篮子空空如也" :image-size="200">
          <button class="btn-primary" @click="$router.push('/front/home')">去选购好茶</button>
        </el-empty>
      </div>

      <!-- Cart List -->
      <div v-if="cartList.length > 0" class="cart-main slide-up delay-100">
        <!-- Table Header (Desktop) -->
        <div class="cart-thead">
          <div class="col-check">
            <el-checkbox v-model="isAllSelected" class="tea-checkbox" />
          </div>
          <div class="col-info">商品信息</div>
          <div class="col-price">单价</div>
          <div class="col-num">数量</div>
          <div class="col-total">小计</div>
          <div class="col-action">操作</div>
        </div>

        <!-- List Body -->
        <div class="cart-tbody">
          <div v-for="item in cartList" :key="item.id" class="cart-row hover-lift">
            <div class="col-check">
              <el-checkbox v-model="item.selected" class="tea-checkbox" />
            </div>

            <div class="col-info product-info" @click="goDetail(item.teaId)">
              <div class="product-img">
                <img :src="item.tea?.imgUrl" :alt="item.tea?.name" />
              </div>
              <div class="product-txt">
                <h4 class="name">{{ item.tea?.name }}</h4>
                <div class="tags">
                  <span class="spec-tag" v-if="item.spec">{{ item.spec }}</span>
                  <span class="type-tag" v-else>有机直采</span>
                </div>
              </div>
            </div>

            <div class="col-price">
              <span class="price">¥{{ item.tea?.price }}</span>
            </div>

            <div class="col-num">
              <el-input-number
                v-model="item.count"
                :min="1"
                :max="item.tea?.stock"
                size="small"
                @change="(val) => updateCount(item, val)"
                class="tea-input-number"
              />
            </div>

            <div class="col-total">
              <span class="total-price">¥{{ (item.tea?.price * item.count).toFixed(2) }}</span>
            </div>

            <div class="col-action">
              <button class="btn-icon-del" @click="deleteItem(item.id)">
                <el-icon><Delete /></el-icon>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Sticky Checkout Bar -->
      <div v-if="cartList.length > 0" class="checkout-bar-wrapper">
        <div class="checkout-bar glass-morphism slide-up delay-200">
          <div class="bar-left">
            <el-checkbox v-model="isAllSelected" class="tea-checkbox">全选</el-checkbox>
            <span class="selected-summary">已选 <span class="highlight">{{ selectedCount }}</span> 件</span>
          </div>
          <div class="bar-right">
            <div class="total-panel">
              <span class="label">合计:</span>
              <span class="price">¥{{ totalPrice }}</span>
            </div>
            <button 
              class="btn-primary checkout-btn" 
              :disabled="selectedCount === 0"
              @click="handlePreCheckout"
            >
              立即结算
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Confirm Dialog -->
    <el-dialog v-model="settleVisible" align-center class="tea-dialog" width="480px" title="核对订单">
      <div class="dialog-inner">
        <div class="settle-list">
          <div v-for="item in selectedList" :key="item.id" class="settle-item">
            <img :src="item.tea?.imgUrl" class="s-img" />
            <div class="s-info">
              <div class="s-name">{{ item.tea?.name }}</div>
              <div class="s-meta">x{{ item.count }} · {{ item.spec || '标准' }}</div>
            </div>
            <div class="s-total">¥{{ (item.tea?.price * item.count).toFixed(2) }}</div>
          </div>
        </div>
        <div class="settle-summary">
          <span>共 {{ selectedCount }} 件宝贝</span>
          <div class="final-pay">实付：<span class="val">¥{{ totalPrice }}</span></div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-actions">
          <button class="btn-flat" @click="settleVisible = false">返回修改</button>
          <button class="btn-primary" @click="showAddressDialog">确认无误</button>
        </div>
      </template>
    </el-dialog>

    <!-- Address Dialog -->
    <el-dialog v-model="addressVisible" align-center class="tea-dialog" width="480px" title="收货信息">
      <el-form :model="addressForm" :rules="addressRules" label-position="top" ref="addressFormRef" class="tea-form">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="addressForm.consignee" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="手机号码" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="addressForm.address" type="textarea" :rows="3" placeholder="省市区街道、楼牌号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-actions">
          <button class="btn-flat" @click="addressVisible = false">取消</button>
          <button class="btn-primary" :loading="submitting" @click="confirmSubmitOrder">提交并支付</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { runWithStatusMessage } from '../../utils/message'

defineOptions({ name: 'Cart' })

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const loading = ref(false)
const cartList = ref([])
const user = ref({})
const settleVisible = ref(false)
const addressVisible = ref(false)
const submitting = ref(false)

const addressFormRef = ref(null)
const addressForm = reactive({ consignee: '', phone: '', address: '' })
const addressRules = {
  consignee: [{ required: true, message: '请填写收货人', trigger: 'blur' }],
  phone: [{ required: true, message: '请填写电话', trigger: 'blur' }],
  address: [{ required: true, message: '请填写地址', trigger: 'blur' }]
}

// Logic
const selectedList = computed(() => cartList.value.filter(i => i.selected && i.tea))
const totalPrice = computed(() => selectedList.value.reduce((s, i) => s + i.tea.price * i.count, 0).toFixed(2))
const selectedCount = computed(() => selectedList.value.length)
const isAllSelected = computed({
  get: () => cartList.value.length > 0 && cartList.value.every(i => i.selected),
  set: (val) => cartList.value.forEach(i => i.selected = val)
})

onMounted(() => loadCart())

const loadCart = async () => {
  const userStr = localStorage.getItem('tea-user')
  if (!userStr) return (ElMessage.warning('请先登录'), router.push('/front/login'))
  user.value = JSON.parse(userStr)
  loading.value = true
  try {
    const res = await axios.get('/cart/list', { params: { userId: user.value.id } })
    if (res.data.code === '200') cartList.value = (res.data.data || []).map(i => ({ ...i, selected: false }))
  } catch (e) {} finally { loading.value = false }
}

const updateCount = async (item, val) => {
  try { await axios.post(`/cart/update/${item.id}/${val}`) } catch (e) { loadCart() }
}

const deleteItem = (id) => {
  ElMessageBox.confirm('确认移除该茶品？', '提示', { type: 'warning', confirmButtonText: '移除', cancelButtonText: '取消' })
    .then(async () => {
      try {
        const res = await axios.delete(`/cart/delete/${id}`)
        if (res.data.code === '200') (ElMessage.success('已移除'), cartList.value = cartList.value.filter(i => i.id !== id))
      } catch (e) {}
    }).catch(() => {})
}

const goDetail = (tid) => router.push(`/front/detail/${tid}`)
const handlePreCheckout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请先选择要结算的茶品')
    return
  }
  settleVisible.value = true
}
const showAddressDialog = () => {
  settleVisible.value = false
  addressVisible.value = true
  addressForm.consignee = user.value.name || user.value.username || ''
  addressForm.phone = user.value.phone || ''
  addressForm.address = user.value.address || ''
}

const confirmSubmitOrder = async () => {
  if (!addressFormRef.value) return
  try { await addressFormRef.value.validate() } catch { return }
  submitting.value = true
  try {
    const payload = {
      userId: user.value.id,
      consignee: addressForm.consignee,
      phone: addressForm.phone,
      address: addressForm.address,
      items: selectedList.value.map(item => ({
        cartId: item.id,
        teaId: item.tea.id,
        teaName: item.tea.name,
        imgUrl: item.tea.imgUrl,
        count: item.count,
        price: item.tea.price,
        spec: item.spec || '标准'
      }))
    }
    const res = await runWithStatusMessage({
      pendingMessage: '正在提交订单...',
      successMessage: '下单成功，正在整理茶篮',
      errorMessage: '提交订单失败，请稍后重试',
      task: () => axios.post('/order/checkout', payload)
    })

    if (res.data.code !== '200') {
      ElMessage.error(res.data.msg || '提交订单失败')
      return
    }

    addressVisible.value = false
    cartList.value = cartList.value.filter(i => !i.selected)
    ElMessage.info('2秒后返回首页')
    setTimeout(() => router.push('/front/home'), 2000)
  } catch (e) {} finally { submitting.value = false }
}
</script>

<style scoped>
.cart-page-wrapper {
  padding-top: 40px;
  padding-bottom: 120px;
}

.page-header {
  text-align: center;
  margin-bottom: 50px;
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

/* List Structure */
.cart-main {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  border: 1px solid #f0f4f2;
}

.cart-thead {
  display: flex;
  background: #f9fafb;
  padding: 16px 30px;
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
}

.cart-row {
  display: flex;
  align-items: center;
  padding: 30px;
  border-bottom: 1px solid #f7fbf8;
  transition: all 0.3s ease;
}
.cart-row:last-child { border-bottom: none; }
.cart-row:hover { background: #fdfefd; }

/* Columns */
.col-check { width: 40px; }
.col-info { flex: 4; display: flex; align-items: center; gap: 20px; cursor: pointer; }
.col-price { flex: 1; text-align: center; color: var(--text-main); font-weight: 500; }
.col-num { flex: 1.5; display: flex; justify-content: center; }
.col-total { flex: 1.2; text-align: center; color: var(--tea-primary); font-weight: 700; font-size: 17px; }
.col-action { width: 60px; text-align: center; }

/* Product Info */
.product-img {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: #f8f8f8;
  border: 1px solid #eee;
  flex-shrink: 0;
}
.product-img img { width: 100%; height: 100%; object-fit: cover; }
.product-txt h4 { margin: 0 0 10px; font-size: 17px; color: var(--text-main); }
.tags { display: flex; gap: 8px; }
.spec-tag { font-size: 11px; background: #f3f4f6; color: #6b7280; padding: 2px 8px; border-radius: 4px; }
.type-tag { font-size: 11px; background: var(--tea-primary-light); color: var(--tea-primary); padding: 2px 8px; border-radius: 4px; }

/* Components Override */
:deep(.tea-checkbox) {
  --el-checkbox-checked-bg-color: var(--tea-primary);
  --el-checkbox-checked-border-color: var(--tea-primary);
  --el-checkbox-input-border-color-hover: var(--tea-primary);
}
:deep(.tea-input-number) { width: 110px; }
:deep(.tea-input-number .el-input__wrapper) { border-radius: 20px; background: #f9fafb; box-shadow: none !important; }

/* Action Buttons */
.btn-icon-del {
  background: none; border: none; font-size: 20px; color: #9ca3af; transition: color 0.2s; cursor: pointer;
}
.btn-icon-del:hover { color: var(--tea-danger); }

/* Checkout Bar */
.checkout-bar-wrapper { height: 100px; }
.checkout-bar {
  position: fixed;
  bottom: 40px;
  left: 0;
  right: 0;
  margin: 0 auto;
  width: 1100px;
  max-width: 95%;
  height: 80px;
  padding: 0 32px;
  border-radius: var(--radius-full);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-xl);
  z-index: 1000;
  border: 1px solid rgba(255,255,255,0.6);
}
.glass-morphism {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
}

.bar-left { display: flex; align-items: center; gap: 30px; }
.selected-summary { font-size: 14px; color: var(--text-secondary); }
.highlight { color: var(--tea-primary); font-weight: 700; font-size: 18px; margin: 0 4px; }

.bar-right { display: flex; align-items: center; gap: 40px; }
.total-panel { display: flex; align-items: baseline; gap: 8px; }
.total-panel .label { font-size: 14px; color: var(--text-secondary); }
.total-panel .price { font-size: 28px; font-weight: 800; color: var(--tea-danger); font-family: 'Arial'; }

.checkout-btn {
  padding: 0 48px;
  min-width: 160px;
  height: 52px;
  font-size: 16px;
  border-radius: var(--radius-full);
}

/* Order Confirm Dialog */
.dialog-inner { padding: 0 24px 24px; }
.settle-list { max-height: 280px; overflow-y: auto; margin-bottom: 20px; padding-right: 8px; }
.settle-item { display: flex; align-items: center; gap: 16px; padding: 12px 0; border-bottom: 1px dashed #f0f0f0; }
.s-img { width: 50px; height: 50px; border-radius: 8px; object-fit: cover; }
.s-info { flex: 1; }
.s-name { font-size: 14px; font-weight: bold; margin-bottom: 4px; }
.s-meta { font-size: 11px; color: #999; }
.s-total { font-weight: bold; color: var(--text-main); }
.settle-summary { display: flex; justify-content: space-between; align-items: center; font-size: 14px; color: #666; }
.final-pay { font-weight: bold; color: var(--text-main); }
.final-pay .val { font-size: 24px; color: var(--tea-danger); margin-left: 8px; }

/* Empty State */
.empty-state { padding: 100px 0; }
.btn-primary { 
  display: inline-block;
  background: var(--tea-primary); color: white; border: none; padding: 12px 32px; border-radius: var(--radius-full); cursor: pointer; transition: all 0.3s;
}

/* Animation Utilities */
.slide-up { animation: slideUp 0.6s ease-out forwards; opacity: 0; transform: translateY(20px); }
.fade-in { animation: fadeIn 0.8s ease-out forwards; opacity: 0; }
.delay-100 { animation-delay: 0.1s; }
.delay-200 { animation-delay: 0.2s; }
.hover-lift:hover { transform: translateY(-4px); }

@keyframes slideUp { to { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { to { opacity: 1; } }

@media (max-width: 768px) {
  .cart-thead { display: none; }
  .cart-row { flex-wrap: wrap; padding: 20px; position: relative; }
  .col-check { position: absolute; left: 15px; top: 15px; }
  .col-info { width: 100%; padding-left: 30px; margin-bottom: 15px; }
  .col-price, .col-num, .col-total { width: 33.33%; font-size: 12px; }
  .col-action { position: absolute; right: 15px; top: 15px; }
  .checkout-bar { width: 95%; padding: 0 20px; }
  .bar-right { gap: 15px; }
  .checkout-btn { padding: 0 20px; min-width: 100px; }
}
</style>
