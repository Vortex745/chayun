<template>
  <div class="detail-page">
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon>
          <ArrowLeft/>
        </el-icon>
        <span>返回上一页</span>
      </div>
    </div>

    <div class="product-showcase">
      <div class="gallery-section">
        <div class="main-image-box">
          <img v-if="tea.imgUrl" :src="tea.imgUrl" alt="商品图片"/>
          <div v-else class="image-placeholder">
            <el-icon>
              <Picture/>
            </el-icon>
          </div>
        </div>
      </div>

      <div class="info-section">
        <h1 class="product-title">{{ tea.name }}</h1>
        <p class="product-desc">{{ tea.content || '暂无描述' }}</p>

        <div class="price-card">
          <div class="price-row">
            <span class="currency">¥</span>
            <span class="amount">{{ currentPrice }}</span>
            <span v-if="specOptions.length > 0" class="price-tip">起</span>
          </div>
          <div class="meta-row">
            <span class="meta-item">库存: {{ tea.stock }}</span><span class="divider">|</span>
            <span class="meta-item">热度: {{ tea.clickCount || 0 }}</span><span class="divider">|</span>
            <span class="meta-item">分类: {{ tea.type }}</span>
          </div>
        </div>

        <div class="action-area">
          <div class="btn-group">
            <el-button :icon="ShoppingCart" class="main-btn-cart" size="large" @click="openSpecDialog">加入购物车
            </el-button>
            <el-button class="main-btn-buy" size="large" @click="openSpecDialog">立即购买</el-button>
          </div>
        </div>

        <div class="service-tags">
          <span class="tag"><el-icon><CircleCheckFilled/></el-icon> 原产地直供</span>
          <span class="tag"><el-icon><CircleCheckFilled/></el-icon> 坏单包赔</span>
        </div>
      </div>
    </div>

    <div class="detail-tabs">
      <div class="tab-header">商品详情</div>
      <div class="tab-content">
        <p class="content-text">{{ tea.content }}</p>
        <div class="image-list">
          <img v-if="tea.imgUrl" :src="tea.imgUrl" alt="详情图"/>
        </div>
      </div>
    </div>

    <div class="float-service" @click="serviceVisible = true">
      <el-icon size="24">
        <Service/>
      </el-icon>
      <span class="service-text">客服</span>
    </div>

    <el-dialog v-model="specVisible" :show-close="true" align-center class="custom-dialog" title="选择规格"
               width="480px">
      <div class="spec-content">
        <div class="spec-header">
          <div class="img-box"><img :src="tea.imgUrl" class="spec-img"></div>
          <div class="spec-info">
            <div class="spec-price"><span class="symbol">¥</span>{{ currentPrice }}</div>
            <div class="spec-stock">剩余库存：{{ tea.stock }}</div>
            <div class="spec-selected">已选：{{ selectedSpecName }}</div>
          </div>
        </div>
        <el-divider border-style="dashed" style="margin: 15px 0"/>

        <div class="spec-row">
          <div class="label">商品规格</div>
          <div class="tags-wrapper">
            <template v-if="specOptions.length > 0">
              <span
                  v-for="(item, index) in specOptions"
                  :key="index"
                  :class="['spec-tag', { active: selectedSpecName === item.name }]"
                  @click="selectSpec(item)"
              >
                {{ item.name }}
              </span>
            </template>
            <template v-else>
              <span class="spec-tag active">标准规格</span>
            </template>
          </div>
        </div>

        <div class="spec-row count-row">
          <div class="label">购买数量</div>
          <el-input-number v-model="count" :max="tea.stock" :min="1"/>
        </div>
      </div>

      <template #footer>
        <div class="spec-footer">
          <el-button class="footer-btn btn-cart" @click="confirmAddToCart">加入购物车</el-button>
          <el-button class="footer-btn btn-buy" @click="confirmToBuy">立即购买</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="payVisible" align-center append-to-body class="pay-dialog" title="收银台" width="400px">
      <div class="pay-content">
        <div class="order-summary">
          <p>订单金额</p>
          <div class="total-amount">¥ {{ (currentPrice * count).toFixed(2) }}</div>
        </div>
        <div class="pay-methods">
          <div class="method-item active">
            <el-icon color="#09bb07" size="20">
              <Wallet/>
            </el-icon>
            <span>微信支付</span>
            <el-icon class="check"><Select/></el-icon>
          </div>
          <div class="method-item">
            <el-icon color="#00a0e9" size="20">
              <CreditCard/>
            </el-icon>
            <span>支付宝</span></div>
        </div>
      </div>
      <template #footer>
        <el-button :loading="paying" class="pay-btn" type="primary" @click="handlePayment">确认支付</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="serviceVisible" align-center append-to-body class="custom-dialog" title="客户服务"
               width="400px">
      <div class="service-options">
        <div class="option-item" @click="showPhone = !showPhone">
          <div class="icon-wrap phone">
            <el-icon>
              <PhoneFilled/>
            </el-icon>
          </div>
          <div class="opt-text">
            <div class="opt-title">电话客服</div>
            <div v-if="!showPhone" class="opt-desc">点击查看号码</div>
            <div v-else class="opt-phone">400-888-6666</div>
          </div>
        </div>
        <div class="option-item" @click="openMessageForm">
          <div class="icon-wrap msg">
            <el-icon>
              <ChatDotRound/>
            </el-icon>
          </div>
          <div class="opt-text">
            <div class="opt-title">在线留言</div>
            <div class="opt-desc">店主稍后会回复您的消息</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="messageFormVisible" append-to-body class="custom-dialog" destroy-on-close title="给商家留言"
               width="420px">
      <el-form :model="msgForm" label-position="top">
        <el-form-item label="留言内容">
          <el-input
              v-model="msgForm.content"
              :rows="4"
              placeholder="请填写您的问题，我们会尽快联系您..."
              resize="none"
              type="textarea"
          />
        </el-form-item>
        <el-form-item label="联系电话 (选填)">
          <el-input v-model="msgForm.phone" placeholder="方便我们回访"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="messageFormVisible = false">取消</el-button>
          <el-button color="#55ab62" type="primary" @click="submitMessage">发送</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {
  ArrowLeft,
  ChatDotRound,
  CircleCheckFilled,
  CreditCard,
  PhoneFilled,
  Picture,
  Select,
  Service,
  ShoppingCart,
  Wallet
} from '@element-plus/icons-vue'

const {proxy} = getCurrentInstance()
const axios = proxy.$http
const route = useRoute()
const router = useRouter()

const tea = ref({})
const count = ref(1)
const user = ref({})
const specVisible = ref(false)
const payVisible = ref(false)
const paying = ref(false)

// 客服相关状态
const serviceVisible = ref(false)
const messageFormVisible = ref(false)
const showPhone = ref(false)
const msgForm = reactive({content: '', phone: ''})

// 当前选中的规格名称
const selectedSpecName = ref('标准规格')

const specOptions = computed(() => {
  if (tea.value && tea.value.specs) {
    try {
      if (tea.value.specs.includes('[')) {
        return JSON.parse(tea.value.specs)
      }
    } catch (e) {
    }
  }
  return []
})

const currentPrice = computed(() => {
  if (specOptions.value.length > 0) {
    const target = specOptions.value.find(s => s.name === selectedSpecName.value)
    if (target) return target.price
    return specOptions.value[0].price
  }
  return tea.value.price
})

const goBack = () => {
  if (window.history.length > 1) router.back()
  else router.push('/front/home')
}

onMounted(() => {
  window.scrollTo(0, 0)
  const userStr = localStorage.getItem('tea-user')
  if (userStr) user.value = JSON.parse(userStr)

  const id = route.params.id
  if (id) loadTeaDetail(id)
})

const loadTeaDetail = async (id) => {
  try {
    const res = await axios.get(`/tea/${id}`)
    if (res.data.code === '200') {
      tea.value = res.data.data
      if (specOptions.value.length > 0) {
        selectedSpecName.value = specOptions.value[0].name
      } else {
        selectedSpecName.value = '标准规格'
      }
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const selectSpec = (item) => {
  selectedSpecName.value = item.name
}

const openSpecDialog = () => {
  if (!user.value.id) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  specVisible.value = true
}

const confirmAddToCart = async () => {
  try {
    const res = await axios.post('/cart/add', {
      userId: user.value.id,
      teaId: tea.value.id,
      count: count.value,
      spec: selectedSpecName.value
    })
    if (res.data.code === '200') {
      ElMessage.success('已加入购物车')
      specVisible.value = false
    } else ElMessage.error(res.data.msg)
  } catch (e) {
    ElMessage.error('失败')
  }
}

const confirmToBuy = () => {
  specVisible.value = false
  payVisible.value = true
}

const handlePayment = async () => {
  paying.value = true
  try {
    const finalPrice = currentPrice.value
    const orderData = {
      userId: user.value.id,
      goodsName: tea.value.name,
      goodsId: tea.value.id,
      count: count.value,
      price: finalPrice,
      totalPrice: (finalPrice * count.value).toFixed(2),
      state: '待发货',
      spec: selectedSpecName.value
    }
    setTimeout(async () => {
      await axios.post('/order/add', orderData)
      paying.value = false
      payVisible.value = false
      ElMessage.success('支付成功！')
      router.push('/front/orders')
    }, 1500)
  } catch (e) {
    paying.value = false
    ElMessage.error('支付问题')
  }
}

// 打开留言表单
const openMessageForm = () => {
  if (!user.value.id) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  msgForm.content = ''
  msgForm.phone = user.value.phone || ''
  serviceVisible.value = false
  messageFormVisible.value = true
}

// 提交留言
const submitMessage = async () => {
  if (!msgForm.content) return ElMessage.warning('请填写留言内容')

  try {
    const res = await axios.post('/message/add', {
      userId: user.value.id,
      username: user.value.username || user.value.name,
      content: msgForm.content,
      phone: msgForm.phone
    })
    if (res.data.code === '200') {
      ElMessage.success('留言已发送，我们将尽快联系您')
      messageFormVisible.value = false
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
}
</script>

<style scoped>
/* 悬浮客服按钮 */
.float-service {
  position: fixed;
  bottom: 100px;
  right: 30px;
  z-index: 999;
  width: 50px;
  height: 50px;
  background: white;
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #55ab62;
  border: 1px solid #eef2f6;
}

.float-service:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(85, 171, 98, 0.25);
  background: #f0f9eb;
}

.service-text {
  font-size: 10px;
  margin-top: -2px;
  font-weight: bold;
}

/* 客服弹窗样式 */
.service-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 5px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f8fcf9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.option-item:hover {
  background: #f0f9eb;
  border-color: #55ab62;
}

.icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.icon-wrap.phone {
  background: linear-gradient(135deg, #55ab62, #85ce91);
}

.icon-wrap.msg {
  background: linear-gradient(135deg, #ff9800, #ffc107);
}

.opt-title {
  font-weight: bold;
  color: #333;
  font-size: 16px;
  margin-bottom: 4px;
}

.opt-desc {
  font-size: 13px;
  color: #999;
}

.opt-phone {
  font-size: 18px;
  color: #55ab62;
  font-weight: 800;
  font-family: Arial;
}

/* 复用样式 (省略部分未变动的 CSS) */
.detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  animation: fadeIn 0.6s ease;
}

.nav-header {
  margin-bottom: 20px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #666;
  font-size: 15px;
  background: white;
  padding: 8px 16px;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.product-showcase {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  display: flex;
  gap: 50px;
  margin-bottom: 30px;
}

.gallery-section {
  flex: 1;
  max-width: 500px;
}

.main-image-box {
  width: 100%;
  height: 450px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  background-color: #fcfdfd;
}

.main-image-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ddd;
  font-size: 40px;
}

.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 30px;
  color: #2c3e50;
  font-weight: 800;
  margin-bottom: 12px;
}

.product-desc {
  color: #999;
  font-size: 14px;
  margin-bottom: 25px;
  line-height: 1.6;
}

.price-card {
  background: linear-gradient(135deg, #f9fffb 0%, #f0f9f4 100%);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  border: 1px solid #e1f0e6;
}

.price-row {
  color: #55ab62;
  margin-bottom: 8px;
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 20px;
  font-weight: bold;
  margin-right: 4px;
}

.amount {
  font-size: 36px;
  font-weight: 800;
  font-family: 'Arial', sans-serif;
}

.price-tip {
  font-size: 12px;
  color: #999;
  margin-left: 5px;
}

.meta-row {
  font-size: 13px;
  color: #888;
  display: flex;
  align-items: center;
}

.divider {
  margin: 0 12px;
  color: #ccc;
}

.action-area {
  margin-top: 10px;
  margin-bottom: 30px;
}

.btn-group {
  display: flex;
  gap: 20px;
}

.main-btn-cart {
  flex: 1;
  height: 50px;
  border-radius: 25px;
  font-size: 16px;
  border: 1px solid #55ab62;
  color: #55ab62;
  background: transparent;
}

.main-btn-buy {
  flex: 1;
  height: 50px;
  border-radius: 25px;
  font-size: 16px;
  background: linear-gradient(135deg, #55ab62, #429e50);
  border: none;
  color: white;
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.3);
  font-weight: bold;
}

.service-tags {
  display: flex;
  gap: 20px;
}

.tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.tag .el-icon {
  color: #55ab62;
  font-size: 16px;
}

/* 【恢复】商品详情样式 */
.detail-tabs {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.02);
}

.tab-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  border-left: 4px solid #55ab62;
  padding-left: 12px;
}

.content-text {
  color: #555;
  line-height: 1.8;
  font-size: 15px;
  margin-bottom: 30px;
}

.image-list {
  text-align: center;
}

.image-list img {
  max-width: 100%;
  border-radius: 8px;
  margin-bottom: 10px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 弹窗样式 */
:deep(.custom-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

:deep(.custom-dialog .el-dialog__header) {
  padding: 20px 24px 10px;
  margin-right: 0;
}

:deep(.custom-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 800;
  color: #333;
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 10px 24px 20px;
}

:deep(.custom-dialog .el-dialog__footer) {
  padding: 0 24px 24px;
}

.spec-header {
  display: flex;
  gap: 16px;
  align-items: flex-end;
}

.img-box {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #f2f2f2;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.03);
  flex-shrink: 0;
}

.spec-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spec-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-bottom: 4px;
}

.spec-price {
  color: #55ab62;
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
}

.spec-price .symbol {
  font-size: 16px;
  margin-right: 2px;
}

.spec-stock {
  font-size: 12px;
  color: #999;
}

.spec-selected {
  font-size: 13px;
  color: #333;
  background: #f7f9fc;
  padding: 4px 8px;
  border-radius: 4px;
  margin-top: 5px;
  display: inline-block;
}

.spec-row {
  margin-bottom: 24px;
}

.spec-row.count-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.label {
  font-size: 14px;
  color: #333;
  font-weight: bold;
  margin-bottom: 12px;
}

.tags-wrapper {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.spec-tag {
  padding: 6px 18px;
  background: #f5f5f5;
  border: 1px solid transparent;
  border-radius: 20px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.spec-tag:hover {
  background: #eefdf2;
  color: #55ab62;
}

.spec-tag.active {
  background: #eefdf2;
  color: #55ab62;
  border-color: #55ab62;
  font-weight: bold;
  box-shadow: 0 2px 6px rgba(85, 171, 98, 0.1);
}

.spec-footer {
  display: flex;
  gap: 15px;
}

.footer-btn {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-cart {
  background-color: #ecf7f0;
  border: 1px solid #d1ead6;
  color: #55ab62;
}

.btn-cart:hover {
  background-color: #dff2e4;
  border-color: #55ab62;
  color: #3d8c48;
}

.btn-buy {
  background: linear-gradient(135deg, #55ab62 0%, #3d8c48 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3);
}

.btn-buy:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(85, 171, 98, 0.4);
}

.order-summary {
  text-align: center;
  margin-bottom: 25px;
}

.order-summary p {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.total-amount {
  font-size: 32px;
  font-weight: 800;
  color: #333;
}

.pay-methods {
  border-top: 1px dashed #eee;
  padding-top: 20px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.method-item.active {
  border-color: #55ab62;
  background: #f0f9f4;
}

.method-item .check {
  margin-left: auto;
  color: #55ab62;
}

.pay-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
  background: #55ab62;
  border-color: #55ab62;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>