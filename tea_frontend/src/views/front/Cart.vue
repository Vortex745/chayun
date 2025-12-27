<template>
  <div class="cart-container">
    <div class="page-title">我的购物车</div>

    <el-empty v-if="cartList.length === 0" description="购物车空空如也，快去选购吧">
      <el-button type="primary" @click="$router.push('/front/home')">去逛逛</el-button>
    </el-empty>

    <div v-else>
      <el-card shadow="never" class="cart-item" v-for="item in cartList" :key="item.id">
        <el-row align="middle" :gutter="20">
          <el-col :span="3">
            <el-image :src="item.imgUrl" class="cart-img" fit="cover" />
          </el-col>

          <el-col :span="8">
            <div class="item-name">{{ item.teaName }}</div>
          </el-col>

          <el-col :span="3" class="item-price">
            ￥{{ item.price }}
          </el-col>

          <el-col :span="6">
            <el-input-number
                v-model="item.count"
                :min="1"
                size="small"
                @change="(val) => handleCountChange(item.id, val)"
            />
          </el-col>

          <el-col :span="4" style="text-align: right;">
            <div class="subtotal">￥{{ (item.price * item.count).toFixed(2) }}</div>
            <el-button type="danger" link size="small" @click="handleDelete(item.id)">删除</el-button>
          </el-col>
        </el-row>
      </el-card>

      <div class="footer-bar">
        <div class="footer-content">
          <div class="left">
            共 <span class="count">{{ cartList.length }}</span> 件商品
          </div>
          <div class="right">
            <span class="label">合计：</span>
            <span class="total-price">￥{{ totalPrice }}</span>
            <el-button type="primary" size="large" class="checkout-btn" @click="openCheckout">去结算</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="确认收货信息" width="400px">
      <el-form :model="addressForm" label-width="70px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.consignee" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="addressForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input type="textarea" v-model="addressForm.address" placeholder="请输入详细收货地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitting">确认下单</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()

const cartList = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)

// 地址表单
const addressForm = reactive({
  consignee: '',
  phone: '',
  address: ''
})

// === 核心逻辑 ===

// 1. 加载购物车
const loadCart = async () => {
  const userStr = localStorage.getItem('tea-user')
  if (!userStr) return

  const user = JSON.parse(userStr)
  try {
    const res = await axios.get(`/cart/list?userId=${user.id}`)
    if (res.data.code === '200') {
      cartList.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

// 2. 修改数量
const handleCountChange = async (id, count) => {
  try {
    await axios.post(`/cart/update/${id}/${count}`)
  } catch (e) {
    ElMessage.error('更新失败')
    loadCart()
  }
}

// 3. 删除商品
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要移出购物车吗？', '提示', { type: 'warning' })
      .then(async () => {
        const res = await axios.delete(`/cart/delete/${id}`)
        if (res.data.code === '200') {
          ElMessage.success('已删除')
          loadCart()
        }
      }).catch(() => {})
}

// 4. 打开结算弹窗
const openCheckout = () => {
  const user = JSON.parse(localStorage.getItem('tea-user') || '{}')
  // 自动填入注册时的信息，省去用户打字
  addressForm.consignee = user.nickname || ''
  addressForm.phone = user.phone || ''
  addressForm.address = user.address || ''
  dialogVisible.value = true
}

// 5. 提交订单
const submitOrder = async () => {
  if(!addressForm.consignee || !addressForm.phone || !addressForm.address) {
    return ElMessage.warning('请填写完整的收货信息')
  }

  const user = JSON.parse(localStorage.getItem('tea-user') || '{}')
  submitting.value = true

  try {
    const params = {
      userId: user.id,
      consignee: addressForm.consignee,
      phone: addressForm.phone,
      address: addressForm.address
    }
    const res = await axios.post('/order/add', params)

    if (res.data.code === '200') {
      ElMessage.success({ message: '下单成功！', duration: 2000 })
      dialogVisible.value = false
      // 跳转到“我的订单”页面
      router.push('/front/myorder')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('下单失败')
  } finally {
    submitting.value = false
  }
}

// 计算总价
const totalPrice = computed(() => {
  let sum = 0
  cartList.value.forEach(item => {
    sum += item.price * item.count
  })
  return sum.toFixed(2)
})

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-container { width: 1000px; margin: 0 auto; padding-bottom: 80px; }
.page-title { font-size: 24px; font-weight: bold; margin: 20px 0; }
.cart-item { margin-bottom: 15px; }
.cart-img { width: 80px; height: 80px; border-radius: 4px; }
.item-name { font-size: 16px; font-weight: bold; color: #333; }
.item-price { color: #666; }
.subtotal { color: #f56c6c; font-weight: bold; font-size: 16px; margin-bottom: 5px; }

.footer-bar {
  position: fixed; bottom: 0; left: 0; width: 100%; height: 70px;
  background: white; border-top: 1px solid #ddd; box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  display: flex; justify-content: center; align-items: center; z-index: 100;
}
.footer-content { width: 1000px; display: flex; justify-content: space-between; align-items: center; }
.count { color: #f56c6c; font-weight: bold; margin: 0 5px; }
.total-price { color: #f56c6c; font-size: 24px; font-weight: bold; margin-right: 20px; }
.checkout-btn { width: 140px; font-size: 16px; }
</style>