<template>
  <div class="detail-page">

    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回上一页</span>
      </div>
    </div>

    <div class="product-showcase">
      <div class="gallery-section">
        <div class="main-image-box">
          <img v-if="tea.imgUrl" :src="tea.imgUrl" alt="商品图片" />
          <div v-else class="image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </div>
      </div>

      <div class="info-section">
        <h1 class="product-title">{{ tea.name }}</h1>
        <p class="product-desc">{{ tea.content || '暂无描述，品味自然之美' }}</p>

        <div class="price-card">
          <div class="price-row">
            <span class="currency">¥</span>
            <span class="amount">{{ tea.price }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-item">库存: {{ tea.stock }}</span>
            <span class="divider">|</span>
            <span class="meta-item">销量: {{ Math.floor(Math.random() * 800) + 50 }}</span> <span class="divider">|</span>
            <span class="meta-item">好评率: 99%</span>
          </div>
        </div>

        <div class="action-area">
          <div class="quantity-box">
            <span class="label">数量</span>
            <el-input-number
                v-model="count"
                :min="1"
                :max="tea.stock"
                size="large"
                class="custom-stepper"
            />
          </div>

          <div class="btn-group">
            <el-button class="btn-cart" size="large" :icon="ShoppingCart" @click="addToCart">
              加入购物车
            </el-button>
            <el-button class="btn-buy" size="large" @click="buyNow">
              立即购买
            </el-button>
          </div>
        </div>

        <div class="service-tags">
          <span class="tag"><el-icon><CircleCheckFilled /></el-icon> 原产地直供</span>
          <span class="tag"><el-icon><CircleCheckFilled /></el-icon> 48小时发货</span>
          <span class="tag"><el-icon><CircleCheckFilled /></el-icon> 坏单包赔</span>
        </div>
      </div>
    </div>

    <div class="detail-tabs">
      <div class="tab-header">商品详情</div>
      <div class="tab-content">
        <p class="content-text">{{ tea.content }}</p>
        <div class="image-list">
          <img v-if="tea.imgUrl" :src="tea.imgUrl" alt="详情图" />
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, ArrowLeft, Picture, CircleCheckFilled } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const route = useRoute()
const router = useRouter()

const tea = ref({})
const count = ref(1)

// 返回逻辑
const goBack = () => {
  // 如果有历史记录则返回，否则回首页
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/front/home')
  }
}

// 核心：页面加载
onMounted(async () => {
  // 🚀 修复问题：强制滚动到顶部
  window.scrollTo(0, 0)

  const id = route.params.id
  if (id) {
    loadTeaDetail(id)
  }
})

// 加载详情
const loadTeaDetail = async (id) => {
  try {
    const res = await axios.get(`/tea/${id}`)
    if (res.data.code === '200') {
      tea.value = res.data.data
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('加载商品失败')
  }
}

// 加入购物车
const addToCart = async () => {
  const userStr = localStorage.getItem('tea-user')
  if (!userStr) {
    ElMessage.warning('请先登录')
    router.push('/front/login')
    return
  }
  const user = JSON.parse(userStr)

  try {
    const res = await axios.post('/cart/add', {
      userId: user.id,
      teaId: tea.value.id,
      num: count.value // 注意：后端通常接收 num 字段，这里对应你原本逻辑
    })
    if (res.data.code === '200') {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 立即购买
const buyNow = async () => {
  await addToCart()
  router.push('/front/cart')
}
</script>

<style scoped>
/* 页面容器 */
.detail-page {
  max-width: 1200px; margin: 0 auto; padding: 20px;
  animation: fadeIn 0.6s ease;
}

/* 1. 顶部返回栏 */
.nav-header { margin-bottom: 20px; }
.back-btn {
  display: inline-flex; align-items: center; gap: 5px;
  cursor: pointer; color: #666; font-size: 15px; transition: color 0.3s;
  background: white; padding: 8px 16px; border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.back-btn:hover { color: #55ab62; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.2); }

/* 2. 商品展示区 (卡片风格) */
.product-showcase {
  background: white; border-radius: 16px; padding: 40px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
  display: flex; gap: 50px; margin-bottom: 30px;
}

/* 左侧图片 */
.gallery-section { flex: 1; max-width: 500px; }
.main-image-box {
  width: 100%; height: 450px; border-radius: 12px; overflow: hidden;
  border: 1px solid #f2f4f6; position: relative;
  background-color: #fcfdfd;
}
.main-image-box img {
  width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s;
}
.main-image-box:hover img { transform: scale(1.05); }
.image-placeholder {
  height: 100%; display: flex; align-items: center; justify-content: center; color: #ddd; font-size: 40px;
}

/* 右侧信息 */
.info-section { flex: 1; display: flex; flex-direction: column; }
.product-title { font-size: 30px; color: #2c3e50; font-weight: 800; margin-bottom: 12px; }
.product-desc { color: #999; font-size: 14px; margin-bottom: 25px; line-height: 1.6; }

/* 价格卡片 */
.price-card {
  background: linear-gradient(135deg, #f9fffb 0%, #f0f9f4 100%);
  padding: 20px; border-radius: 12px; margin-bottom: 30px;
  border: 1px solid #e1f0e6;
}
.price-row { color: #55ab62; margin-bottom: 8px; display: flex; align-items: baseline; }
.currency { font-size: 20px; font-weight: bold; margin-right: 4px; }
.amount { font-size: 36px; font-weight: 800; font-family: 'Arial', sans-serif; }
.meta-row { font-size: 13px; color: #888; display: flex; align-items: center; }
.divider { margin: 0 12px; color: #ccc; }

/* 操作区域 */
.action-area { margin-top: 10px; margin-bottom: 30px; }
.quantity-box { display: flex; align-items: center; margin-bottom: 25px; }
.quantity-box .label { margin-right: 15px; color: #666; font-size: 14px; }

.btn-group { display: flex; gap: 20px; }
.btn-cart {
  flex: 1; height: 50px; border-radius: 25px; font-size: 16px;
  border: 1px solid #55ab62; color: #55ab62; background: transparent;
  transition: all 0.3s;
}
.btn-cart:hover { background: #f0f9eb; }

.btn-buy {
  flex: 1; height: 50px; border-radius: 25px; font-size: 16px;
  background: linear-gradient(135deg, #55ab62, #429e50); border: none; color: white;
  box-shadow: 0 6px 15px rgba(85, 171, 98, 0.3); font-weight: bold;
  transition: all 0.3s;
}
.btn-buy:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(85, 171, 98, 0.4); }

/* 服务标签 */
.service-tags { display: flex; gap: 20px; }
.tag {
  display: flex; align-items: center; gap: 6px; font-size: 13px; color: #666;
}
.tag .el-icon { color: #55ab62; font-size: 16px; }

/* 3. 详情内容 */
.detail-tabs {
  background: white; border-radius: 16px; padding: 40px; box-shadow: 0 5px 20px rgba(0,0,0,0.02);
}
.tab-header {
  font-size: 18px; font-weight: bold; color: #333; margin-bottom: 20px;
  border-left: 4px solid #55ab62; padding-left: 12px;
}
.content-text { color: #555; line-height: 1.8; font-size: 15px; margin-bottom: 30px; }
.image-list { text-align: center; }
.image-list img { max-width: 100%; border-radius: 8px; margin-bottom: 10px; }

/* 深度选择器覆盖 Element 样式 */
:deep(.custom-stepper .el-input__wrapper) {
  background-color: #f7f9fc; box-shadow: none; border-radius: 8px;
}
:deep(.custom-stepper .el-input-number__decrease),
:deep(.custom-stepper .el-input-number__increase) {
  background: transparent; border: none; color: #55ab62;
}

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>