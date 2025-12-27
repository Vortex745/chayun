<template>
  <div class="detail-container">
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/front/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>商品详情</el-breadcrumb-item>
        <el-breadcrumb-item>{{ tea.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-card shadow="never" class="main-card">
      <el-row :gutter="40">
        <el-col :span="10">
          <el-image :src="tea.imgUrl" class="main-image" fit="cover" :preview-src-list="[tea.imgUrl]" />
        </el-col>

        <el-col :span="14">
          <h1 class="tea-title">{{ tea.name }}</h1>
          <div class="tea-desc">{{ tea.content }}</div>

          <div class="price-box">
            <span class="label">价格</span>
            <span class="price">￥{{ tea.price }}</span>
          </div>

          <div class="meta-info">
            <span class="label">库存</span>
            <span>{{ tea.stock }} 件</span>
          </div>

          <div class="action-box">
            <span class="label">数量</span>
            <el-input-number v-model="count" :min="1" :max="tea.stock" size="large" />
          </div>

          <div class="btn-group">
            <el-button type="warning" size="large" @click="addToCart" :icon="ShoppingCart">加入购物车</el-button>
            <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <div class="detail-content">
      <div class="content-header">商品详情</div>
      <div class="content-body">
        <p>{{ tea.content }}</p>
        <el-image v-if="tea.imgUrl" :src="tea.imgUrl" style="max-width: 800px; margin-top: 20px;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const route = useRoute()
const router = useRouter()

const tea = ref({})
const count = ref(1)

// 1. 初始化加载
onMounted(async () => {
  const id = route.params.id
  if (id) {
    loadTeaDetail(id)
  }
})

// 2. 请求后端详情接口
const loadTeaDetail = async (id) => {
  try {
    const res = await axios.get(`/tea/${id}`)
    if (res.data.code === '200') {
      tea.value = res.data.data
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// 3. 加入购物车 (对接后端)
const addToCart = async () => {
  // 3.1 检查登录
  const userStr = localStorage.getItem('tea-user')
  if (!userStr) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  const user = JSON.parse(userStr)

  // 3.2 准备参数
  const cartItem = {
    userId: user.id,
    teaId: tea.value.id,
    count: count.value
  }

  // 3.3 发送请求
  try {
    const res = await axios.post('/cart/add', cartItem)
    if (res.data.code === '200') {
      ElMessage.success({ message: '加入购物车成功', duration: 2000 })
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 4. 立即购买
const buyNow = async () => {
  // 复用加入购物车逻辑，然后跳转
  await addToCart()
  router.push('/front/cart')
}
</script>

<style scoped>
.detail-container { width: 1200px; margin: 0 auto; padding-bottom: 50px; }
.breadcrumb { margin: 20px 0; font-size: 14px; }
.main-card { padding: 20px; }
.main-image { width: 100%; height: 400px; border-radius: 8px; border: 1px solid #eee; }
.tea-title { font-size: 24px; font-weight: bold; color: #333; margin-top: 0; }
.tea-desc { color: #999; margin-bottom: 20px; font-size: 14px; }
.price-box { background: #fff5f5; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
.price-box .label { color: #999; margin-right: 20px; }
.price-box .price { color: #f56c6c; font-size: 28px; font-weight: bold; }
.meta-info, .action-box { margin-bottom: 20px; display: flex; align-items: center; }
.meta-info .label, .action-box .label { width: 50px; color: #999; }
.btn-group { margin-top: 30px; }
.btn-group .el-button { margin-right: 20px; width: 160px; }
.detail-content { margin-top: 30px; background: white; min-height: 400px; border: 1px solid #eee; }
.content-header { background: #f5f7fa; padding: 15px 20px; font-weight: bold; border-bottom: 1px solid #eee; }
.content-body { padding: 40px; text-align: center; font-size: 16px; line-height: 1.8; color: #666; }
</style>