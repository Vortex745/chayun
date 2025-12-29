<template>
  <div class="main-content">

    <div class="page-header">
      <h2>我的茶篮子</h2>
      <p>精选好茶，待君品尝</p>
    </div>

    <div class="cart-container" v-loading="loading">

      <div class="cart-header" v-if="cartList.length > 0">
        <div class="col-info">商品信息</div>
        <div class="col-price">单价</div>
        <div class="col-num">数量</div>
        <div class="col-total">小计</div>
        <div class="col-action">操作</div>
      </div>

      <div class="cart-list" v-if="cartList.length > 0">
        <div class="cart-item" v-for="item in cartList" :key="item.id">

          <div class="col-info item-info">
            <div class="img-wrapper">
              <img :src="item.imgUrl" alt="商品图片" />
            </div>
            <div class="info-text">
              <div class="name">{{ item.name }}</div>
              <div class="tag">精选好茶</div>
            </div>
          </div>

          <div class="col-price">
            <span class="price-symbol">¥</span>{{ item.price }}
          </div>

          <div class="col-num">
            <el-input-number
                v-model="item.num"
                :min="1"
                :max="item.stock"
                size="small"
                @change="handleNumChange(item)"
                class="green-stepper"
            />
          </div>

          <div class="col-total highlight">
            <span class="price-symbol">¥</span>{{ (item.price * item.num).toFixed(2) }}
          </div>

          <div class="col-action">
            <el-button type="danger" link :icon="Delete" @click="del(item.id)">移除</el-button>
          </div>
        </div>
      </div>

      <div v-else class="empty-cart">
        <el-empty description="茶篮子空空如也，去选点好茶吧~" :image-size="160">
          <el-button type="success" round @click="$router.push('/front/home')">去逛逛</el-button>
        </el-empty>
      </div>

    </div>

    <div class="cart-footer" v-if="cartList.length > 0">
      <div class="left">
        已选 <b>{{ cartList.length }}</b> 件商品
      </div>
      <div class="right">
        <span class="total-label">合计：</span>
        <span class="total-price"><small>¥</small>{{ totalPrice }}</span>
        <el-button class="checkout-btn" @click="pay">立即结算</el-button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const user = JSON.parse(localStorage.getItem('tea-user') || '{}')
const cartList = ref([])
const loading = ref(false)

// 计算总价
const totalPrice = computed(() => {
  let sum = 0
  cartList.value.forEach(item => {
    sum += item.price * item.num
  })
  return sum.toFixed(2)
})

const loadCart = async () => {
  if(!user.id) return
  loading.value = true
  try {
    const res = await axios.get('/cart/list/' + user.id)
    if(res.data.code === '200') {
      cartList.value = res.data.data
    }
  } catch(e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleNumChange = (item) => {
  // 更新后端数量（可选）
  axios.put('/cart/update', item)
}

const del = (id) => {
  ElMessageBox.confirm('确定将此商品移出购物车吗？', '提示', { type: 'warning' })
      .then(async () => {
        const res = await axios.delete('/cart/delete/' + id)
        if(res.data.code === '200') {
          ElMessage.success('移除成功')
          loadCart()
        }
      }).catch(() => {})
}

const pay = async () => {
  if (!user.id) return router.push('/front/login')

  // 模拟下单逻辑：创建订单 -> 清空购物车
  // 这里假设把购物车所有商品合并成一个订单，或者每个商品一个订单
  // 简单起见，我们假设后端有一个 /order/add 接口能处理购物车下单
  // 或者这里可以遍历下单。

  // 这是一个演示逻辑，具体看你的后端实现
  // 这里我们假设直接把第一件商品下单演示流程
  if(cartList.value.length === 0) return

  // 简单模拟：遍历下单 (实际开发通常是传 ids 给后端批量下单)
  try {
    for (let item of cartList.value) {
      const orderData = {
        userId: user.id,
        username: user.nickname,
        totalPrice: item.price * item.num,
        address: user.address || '默认地址',
        // ...其他字段
      }
      await axios.post('/order/add', orderData)
      // 顺便删掉购物车
      await axios.delete('/cart/delete/' + item.id)
    }
    ElMessage.success('下单成功，请前往订单页查看')
    loadCart()
    router.push('/front/myorder')
  } catch(e) {
    ElMessage.error('下单失败')
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.main-content { max-width: 1100px; margin: 0 auto; padding-bottom: 100px; }

/* 页面标题 */
.page-header { margin-bottom: 30px; text-align: center; }
.page-header h2 { font-size: 28px; color: #2c3e50; font-weight: 800; margin-bottom: 10px; }
.page-header p { color: #999; font-size: 14px; letter-spacing: 2px; }

/* 列表头部 */
.cart-header {
  display: flex; padding: 15px 20px; background: #fff;
  border-radius: 12px 12px 0 0; color: #666; font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
}

/* 列表项 */
.cart-list { background: #fff; border-radius: 0 0 12px 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); overflow: hidden; }
.cart-item {
  display: flex; align-items: center; padding: 25px 20px;
  border-bottom: 1px solid #f7f9fc; transition: background 0.3s;
}
.cart-item:hover { background: #fbfdfc; }
.cart-item:last-child { border-bottom: none; }

/* 列宽控制 */
.col-info { flex: 2; text-align: left; }
.col-price, .col-num, .col-total, .col-action { flex: 1; text-align: center; }

/* 商品信息列 */
.item-info { display: flex; align-items: center; gap: 20px; }
.img-wrapper {
  width: 80px; height: 80px; border-radius: 8px; overflow: hidden;
  border: 1px solid #f0f0f0; flex-shrink: 0;
}
.img-wrapper img { width: 100%; height: 100%; object-fit: cover; }
.info-text .name { font-size: 16px; font-weight: bold; color: #333; margin-bottom: 8px; }
.info-text .tag { font-size: 12px; color: #55ab62; background: #e8f5e9; padding: 2px 8px; border-radius: 4px; display: inline-block; }

/* 价格与高亮 */
.price-symbol { font-size: 12px; margin-right: 2px; }
.highlight { color: #ff6b6b; font-weight: bold; font-size: 16px; }

/* 空状态 */
.empty-cart { background: white; padding: 40px; border-radius: 12px; text-align: center; }

/* 底部结算栏 */
.cart-footer {
  position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%);
  width: 1100px; background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  padding: 15px 30px; border-radius: 50px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  display: flex; justify-content: space-between; align-items: center;
  z-index: 100; border: 1px solid #fff;
}
.cart-footer .left b { color: #55ab62; font-size: 18px; margin: 0 4px; }
.cart-footer .right { display: flex; align-items: center; gap: 20px; }
.total-label { font-size: 14px; color: #666; }
.total-price { font-size: 24px; color: #ff6b6b; font-weight: 800; margin-right: 10px; }
.checkout-btn {
  background: linear-gradient(135deg, #55ab62, #3d8b4a);
  border: none; color: white; padding: 12px 35px; border-radius: 30px;
  font-size: 16px; font-weight: bold; letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(85, 171, 98, 0.4); transition: transform 0.2s;
}
.checkout-btn:hover { transform: translateY(-2px); }

/* 修改 input-number 样式 */
:deep(.green-stepper .el-input__wrapper) { box-shadow: none; background: #f2f4f6; }
:deep(.green-stepper .el-input-number__decrease), :deep(.green-stepper .el-input-number__increase) { background: #f2f4f6; border: none; }
:deep(.green-stepper .el-input-number__decrease:hover), :deep(.green-stepper .el-input-number__increase:hover) { color: #55ab62; }
</style>