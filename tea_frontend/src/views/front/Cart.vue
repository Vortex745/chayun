<template>
  <div class="main-content">

    <div class="page-header">
      <h2>我的茶篮子</h2>
      <p>精选好茶，待君品尝</p>
    </div>

    <div v-loading="loading" class="cart-container">

      <div v-if="!loading && cartList.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也，快去选购吧" :image-size="180">
          <el-button type="success" round class="go-home-btn" @click="$router.push('/front/home')">去逛逛</el-button>
        </el-empty>
      </div>

      <div v-if="cartList.length > 0" class="cart-wrapper">

        <div class="cart-header">
          <div class="col-check">
            <el-checkbox v-model="isAllSelected" size="large" class="green-checkbox"/>
          </div>
          <div class="col-info">商品信息</div>
          <div class="col-price">单价</div>
          <div class="col-num">数量</div>
          <div class="col-total">小计</div>
          <div class="col-action">操作</div>
        </div>

        <div class="cart-list">
          <div v-for="item in cartList" :key="item.id" class="cart-item">

            <div class="col-check">
              <el-checkbox v-model="item.selected" size="large" class="green-checkbox"/>
            </div>

            <div class="col-info item-info">
              <div class="img-wrapper" @click="goDetail(item.teaId)">
                <img :src="item.tea?.imgUrl" alt="商品图片" />
              </div>
              <div class="info-text">
                <div class="name" @click="goDetail(item.teaId)">{{ item.tea?.name }}</div>
                <div class="spec-tag" v-if="item.spec">{{ item.spec }}</div>
                <div class="tag" v-else>精选好茶</div>
              </div>
            </div>

            <div class="col-price">
              <span class="price-symbol">¥</span>{{ item.tea?.price }}
            </div>

            <div class="col-num">
              <el-input-number
                  v-model="item.count"
                  :max="item.tea?.stock || 100"
                  :min="1"
                  size="small"
                  @change="(val) => updateCount(item, val)"
                  class="custom-input-number"
              />
            </div>

            <div class="col-total">
              ¥ {{ (item.tea?.price * item.count).toFixed(2) }}
            </div>

            <div class="col-action">
              <span class="del-btn" @click="deleteItem(item.id)">
                <el-icon><Delete /></el-icon> 删除
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="cartList.length > 0" class="cart-footer-placeholder"></div> <div v-if="cartList.length > 0" class="cart-footer">
      <div class="left">
        <el-checkbox v-model="isAllSelected" size="large" class="green-checkbox">全选</el-checkbox>
        <span class="count-text">已选 <b>{{ selectedCount }}</b> 件</span>
      </div>
      <div class="right">
        <div class="price-info">
          <span class="total-label">合计 (不含运费):</span>
          <span class="total-price">
              <span class="symbol">¥</span>{{ totalPrice }}
            </span>
        </div>
        <el-button
            :disabled="selectedCount === 0"
            class="checkout-btn"
            type="primary"
            @click="handlePreCheckout"
        >
          去结算
        </el-button>
      </div>
    </div>

    </div>

    <el-dialog
        v-model="settleVisible"
        align-center
        class="custom-dialog"
        destroy-on-close
        title="订单确认"
        width="480px"
    >
      <div class="settle-content">
        <div class="settle-header-tip">
          <el-icon><Tickets/></el-icon> 请核对您的商品清单
        </div>

        <div class="settle-list">
          <div v-for="item in selectedList" :key="item.id" class="settle-item">
            <div class="s-img">
              <img :src="item.tea?.imgUrl" alt="">
            </div>
            <div class="s-info">
              <div class="s-name">{{ item.tea?.name }}</div>
              <div class="s-spec">数量: x {{ item.count }} <span v-if="item.spec">({{item.spec}})</span></div>
            </div>
            <div class="s-price">
              <span class="symbol">¥</span>{{ (item.tea?.price * item.count).toFixed(2) }}
            </div>
          </div>
        </div>

        <el-divider border-style="dashed" style="margin: 20px 0"/>

        <div class="settle-footer-info">
          <span class="total-count">共 <b>{{ selectedCount }}</b> 件商品</span>
          <div class="final-price-box">
            <span>实付金额：</span>
            <span class="final-price">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer-btns">
          <el-button class="btn-cancel" @click="settleVisible = false">再想想</el-button>
          <el-button
              :loading="submitting"
              class="btn-submit"
              type="primary"
              @click="submitOrder"
          >
            确认下单
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Tickets, Delete} from '@element-plus/icons-vue'

const {proxy} = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const loading = ref(false)
const cartList = ref([])
const user = ref({})

const settleVisible = ref(false)
const submitting = ref(false)

// --- 核心计算逻辑 ---
const selectedList = computed(() => {
  return cartList.value.filter(item => item.selected && item.tea)
})

const totalPrice = computed(() => {
  let sum = 0
  selectedList.value.forEach(item => {
    sum += item.tea.price * item.count
  })
  return sum.toFixed(2)
})

const selectedCount = computed(() => selectedList.value.length)

const isAllSelected = computed({
  get: () => {
    return cartList.value.length > 0 && cartList.value.every(item => item.selected)
  },
  set: (val) => {
    cartList.value.forEach(item => item.selected = val)
  }
})

// --- 生命周期与方法 ---
onMounted(() => {
  loadCart()
})

const loadCart = async () => {
  const userStr = localStorage.getItem('tea-user')
  if (!userStr) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  user.value = JSON.parse(userStr)

  loading.value = true
  try {
    const res = await axios.get('/cart/list', {
      params: {userId: user.value.id}
    })
    if (res.data.code === '200') {
      const list = res.data.data || []
      cartList.value = list.map(item => ({
        ...item,
        selected: false
      }))
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const updateCount = async (item, newCount) => {
  try {
    const res = await axios.post(`/cart/update/${item.id}/${newCount}`)
    if (res.data.code !== '200') {
      ElMessage.error('更新失败')
      loadCart()
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

const deleteItem = (id) => {
  ElMessageBox.confirm('确认要将这件商品移出购物车吗？', '提示', {
    type: 'warning',
    confirmButtonText: '狠心移除',
    cancelButtonText: '留下来',
    confirmButtonClass: 'el-button--danger'
  }).then(async () => {
    try {
      const res = await axios.delete(`/cart/delete/${id}`)
      if (res.data.code === '200') {
        ElMessage.success('已移除')
        cartList.value = cartList.value.filter(item => item.id !== id)
      } else {
        ElMessage.error(res.data.msg)
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const goDetail = (teaId) => {
  router.push(`/front/detail/${teaId}`)
}

const handlePreCheckout = () => {
  if (selectedCount.value === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }
  settleVisible.value = true
}

const submitOrder = async () => {
  submitting.value = true
  try {
    const orderData = selectedList.value.map(item => ({
      cartId: item.id,
      teaId: item.teaId,
      count: item.count,
      price: item.tea.price,
      spec: item.spec
    }))

    setTimeout(() => {
      ElMessage.success('下单成功！')
      settleVisible.value = false
      cartList.value = cartList.value.filter(item => !item.selected)
      submitting.value = false
      router.push('/front/orders')
    }, 1000)

  } catch (e) {
    ElMessage.error('下单失败')
    submitting.value = false
  }
}
</script>

<style scoped>
.main-content {
  max-width: 1100px; margin: 0 auto; padding: 20px; min-height: 80vh;
}

.page-header { margin-bottom: 40px; text-align: center; }
.page-header h2 { font-size: 32px; color: #2c3e50; margin-bottom: 10px; font-weight: 800; letter-spacing: 2px; }
.page-header p { color: #999; letter-spacing: 4px; font-size: 14px; text-transform: uppercase; }

/* 容器与卡片 */
.cart-wrapper {
  background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0,0,0,0.03);
  overflow: hidden; border: 1px solid #f2f7f4;
  /* 移除 margin-bottom，用 placeholder 占位 */
}

/* 列表头 */
.cart-header {
  display: flex; background: #f9fbf9; padding: 20px 30px;
  font-weight: 600; color: #666; font-size: 14px; align-items: center;
  border-bottom: 1px solid #edf2ef;
}

/* 列表项 - 增加 flex-shrink: 0 防止挤压 */
.cart-item {
  display: flex; align-items: center; padding: 30px;
  border-bottom: 1px solid #f7f9f8; transition: background 0.2s ease;
  position: relative; /* 固定定位基准 */
}
.cart-item:last-child { border-bottom: none; }
.cart-item:hover { background: #fafdfb; }

/* 统一列宽 - 使用固定宽度或百分比，避免内容撑开导致抖动 */
.col-check { width: 50px; text-align: center; flex-shrink: 0; }
.col-info { flex: 4; text-align: left; padding-left: 20px; min-width: 300px; }
.col-price { flex: 1; text-align: center; color: #666; font-weight: 500; min-width: 100px; }
.col-num { flex: 1.5; text-align: center; min-width: 120px; }
.col-total { flex: 1; text-align: center; font-weight: 800; color: #55ab62; font-size: 16px; min-width: 100px; }
.col-action { flex: 1; text-align: center; min-width: 80px; }

/* 商品信息 */
.item-info { display: flex; align-items: center; gap: 20px; }
.img-wrapper {
  width: 90px; height: 90px; border-radius: 12px; overflow: hidden;
  border: 1px solid #eee; cursor: pointer; flex-shrink: 0;
  transition: transform 0.3s;
}
.img-wrapper:hover { transform: scale(1.02); } /* 减小缩放幅度，防止抖动 */
.img-wrapper img { width: 100%; height: 100%; object-fit: cover; }

.info-text { display: flex; flex-direction: column; gap: 8px; }
.name { font-size: 16px; font-weight: bold; color: #333; cursor: pointer; transition: color 0.2s; }
.name:hover { color: #55ab62; }
.tag { font-size: 12px; color: #55ab62; background: #eef9f2; padding: 4px 10px; border-radius: 6px; width: fit-content; }
.spec-tag { font-size: 12px; color: #666; background: #f5f5f5; padding: 4px 10px; border-radius: 6px; width: fit-content; }

/* 数量输入框优化 */
:deep(.custom-input-number) { width: 100px; } /* 固定宽度 */
:deep(.custom-input-number .el-input__wrapper) {
  box-shadow: none !important; background: #f7f9f8; border-radius: 20px; padding: 0;
}
:deep(.custom-input-number .el-input__inner) { text-align: center; } /* 文字居中 */

/* 删除按钮 */
.del-btn {
  color: #999; cursor: pointer; font-size: 13px;
  display: inline-flex; align-items: center; justify-content: center; gap: 4px;
  transition: color 0.2s;
}
.del-btn:hover { color: #ff6b6b; } /* 去掉缩放，防止抖动 */

/* 复选框样式固定 */
:deep(.green-checkbox) { height: auto; } /* 防止高度变化 */
:deep(.green-checkbox .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #55ab62; border-color: #55ab62;
}
:deep(.green-checkbox .el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: #55ab62; border-color: #55ab62;
}
:deep(.green-checkbox .el-checkbox__inner:hover) {
  border-color: #55ab62;
}

/* 底部结算栏占位符 - 防止遮挡 */
.cart-footer-placeholder { height: 100px; }

/* 底部结算栏 */
.cart-footer {
  position: fixed; bottom: 40px; left: 50%; transform: translateX(-50%);
  width: 1000px; max-width: 90%;
  background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px);
  padding: 12px 30px; border-radius: 60px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  display: flex; justify-content: space-between; align-items: center;
  z-index: 999; border: 1px solid rgba(255,255,255,0.8);
}

.cart-footer .left { display: flex; align-items: center; gap: 20px; }
.count-text { font-size: 14px; color: #666; }
.cart-footer .left b { color: #55ab62; font-size: 18px; margin: 0 4px; }

.cart-footer .right { display: flex; align-items: center; gap: 25px; }
.total-label { font-size: 14px; color: #666; margin-right: 8px; }
.total-price { font-size: 26px; color: #ff6b6b; font-weight: 800; min-width: 80px; text-align: right; } /* 增加 min-width */
.total-price .symbol { font-size: 16px; font-weight: bold; margin-right: 2px; }

.checkout-btn {
  background: linear-gradient(135deg, #55ab62, #429e50); border: none;
  height: 46px; padding: 0 40px; border-radius: 25px;
  font-size: 16px; font-weight: bold; letter-spacing: 1px;
  box-shadow: 0 6px 20px rgba(85, 171, 98, 0.3);
  transition: transform 0.2s, box-shadow 0.2s; /* 平滑过渡 */
}
.checkout-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(85, 171, 98, 0.4); }
.checkout-btn.is-disabled { background: #ccc; box-shadow: none; transform: none; }

/* 空状态 */
.empty-cart { background: white; padding: 80px; border-radius: 20px; text-align: center; box-shadow: 0 10px 40px rgba(0,0,0,0.03); }
.go-home-btn { padding: 12px 35px; font-weight: bold; margin-top: 10px; background-color: #55ab62; border-color: #55ab62; }

/* 弹窗样式保持不变... */
:deep(.custom-dialog) { border-radius: 20px; overflow: hidden; box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15); }
:deep(.custom-dialog .el-dialog__header) { padding: 20px 30px 10px; margin-right: 0; text-align: left; }
:deep(.custom-dialog .el-dialog__title) { font-size: 18px; font-weight: 800; color: #333; }
:deep(.custom-dialog .el-dialog__body) { padding: 10px 30px 30px; }
:deep(.custom-dialog .el-dialog__footer) { padding: 0 30px 30px; }

.settle-header-tip { display: flex; align-items: center; gap: 8px; color: #666; font-size: 14px; margin-bottom: 20px; background: #f9fbf9; padding: 10px 15px; border-radius: 8px; border-left: 4px solid #55ab62; }
.settle-list { max-height: 250px; overflow-y: auto; padding-right: 5px; }
.settle-list::-webkit-scrollbar { width: 4px; }
.settle-list::-webkit-scrollbar-thumb { background: #eee; border-radius: 2px; }

.settle-item { display: flex; align-items: center; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #eee; }
.settle-item:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
.s-img { width: 50px; height: 50px; border-radius: 8px; overflow: hidden; border: 1px solid #f2f2f2; margin-right: 15px; flex-shrink: 0; }
.s-img img { width: 100%; height: 100%; object-fit: cover; }
.s-info { flex: 1; overflow: hidden; }
.s-name { font-size: 14px; color: #333; font-weight: bold; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.s-spec { font-size: 12px; color: #999; }
.s-price { font-size: 16px; color: #333; font-weight: bold; }

.settle-footer-info { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; font-size: 15px; }
.final-price { font-size: 26px; color: #ff6b6b; font-weight: 800; margin-left: 8px; }

.dialog-footer-btns { display: flex; gap: 15px; }
.btn-cancel { flex: 1; height: 44px; border-radius: 22px; font-weight: bold; font-size: 15px; background-color: #f5f5f5; border: 1px solid transparent; color: #666; }
.btn-cancel:hover { background-color: #eefdf2; color: #55ab62; }
.btn-submit { flex: 1; height: 44px; border-radius: 22px; font-weight: bold; font-size: 15px; background: linear-gradient(135deg, #55ab62 0%, #3d8c48 100%); border: none; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3); }
.btn-submit:hover { transform: translateY(-1px); }

@media (max-width: 768px) {
  .cart-footer { width: 92%; bottom: 15px; flex-direction: column; gap: 15px; height: auto; padding: 20px; border-radius: 20px; }
  .cart-header { display: none; }
  .cart-item { flex-wrap: wrap; position: relative; padding-left: 50px; }
  .col-check { position: absolute; left: 20px; top: 30px; }
  .col-info, .col-num, .col-price, .col-total { width: 100%; display: flex; justify-content: space-between; margin-bottom: 8px; padding: 0; }
  .col-action { position: absolute; top: 20px; right: 20px; }
}
</style>