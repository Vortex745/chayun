<template>
  <div class="home-container">

    <div class="banner-section module-card">
      <el-skeleton :loading="loading && bannerList.length === 0" animated>
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 400px;" />
        </template>
        <template #default>
          <el-carousel v-if="bannerList.length > 0" height="400px" class="main-carousel" :interval="5000" arrow="hover">
            <el-carousel-item v-for="item in bannerList" :key="item.id">
              <div class="banner-image" :style="{ backgroundImage: `url(${item.imgUrl})` }">
                <div class="banner-content" v-if="item.linkText">
                  <h3>{{ item.linkText }}</h3>
                  <el-button type="success" round class="banner-btn">立刻查看</el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
          <div v-else class="banner-empty">
            <el-icon :size="48" color="#dcece3"><Picture /></el-icon>
            <p>茶韵悠长 · 静候开启</p>
          </div>
        </template>
      </el-skeleton>
    </div>

    <div class="category-section">
      <div class="cat-capsule-group">
        <div
            class="cat-capsule"
            :class="{ active: currentType === '全部' }"
            @click="filterByType('全部')"
        >
          全部好茶
        </div>
        <div
            class="cat-capsule"
            v-for="type in teaTypes"
            :key="type"
            :class="{ active: currentType === type }"
            @click="filterByType(type)"
        >
          {{ type }}
        </div>
      </div>
    </div>

    <div class="goods-section module-card">
      <div class="section-header">
        <div class="title-box">
          <span class="main-title">{{ currentType === '全部' ? '当季热销' : currentType + '精选' }}</span>
          <span class="sub-title" v-if="currentType === '全部'">Nature's Gift · 大自然的馈赠</span>
        </div>
        <transition name="el-fade-in">
          <el-tag
              v-if="currentKeyword"
              closable
              @close="clearSearch"
              type="success"
              effect="dark"
              class="search-tag"
          >
            🔍 搜索: {{ currentKeyword }}
          </el-tag>
        </transition>
      </div>

      <div v-if="loading" class="goods-grid">
        <div v-for="i in 8" :key="i" class="goods-card skeleton-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 200px; border-radius: 12px 12px 0 0;" />
              <div style="padding: 18px;">
                <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 10px;" />
                <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 15px;">
                  <el-skeleton-item variant="text" style="width: 30%" />
                  <el-skeleton-item variant="text" style="width: 20%" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <div v-else class="goods-grid">
        <div
            class="goods-card"
            v-for="tea in teaList"
            :key="tea.id"
            @click="toDetail(tea.id)"
        >
          <div class="card-img-box">
            <img :src="tea.imgUrl" :alt="tea.name" loading="lazy" />
            <div class="hover-mask">
              <span class="view-text">查看详情</span>
            </div>
          </div>
          <div class="card-info">
            <div class="tea-title" :title="tea.name">{{ tea.name }}</div>
            <div class="tea-tags">
              <span class="tag-pill">{{ tea.type || '精选' }}</span>
            </div>
            <div class="card-bottom">
              <div class="price-box">
                <span class="symbol">¥</span>
                <span class="price-num">{{ tea.price }}</span>
              </div>
              <div class="sales-info">近期热卖</div>
            </div>
          </div>
        </div>
      </div>

      <el-empty
          v-if="!loading && teaList.length === 0"
          image-size="160"
          description="暂无相关茶品，去看看其他分类吧~"
      >
        <template #image>
          <img src="https://cdn-icons-png.flaticon.com/512/743/743131.png" style="opacity: 0.5" />
        </template>
      </el-empty>
    </div>

  </div>
</template>

<script setup>
// 🚀【关键点 1】必须显式声明组件名称，ClientLayout 中的 KeepAlive 才能识别并缓存它！
defineOptions({
  name: 'Home'
})

import { ref, onMounted, watch, getCurrentInstance, onActivated } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()
const route = useRoute()

const teaList = ref([])
const bannerList = ref([])
// 🚀【关键点 2】初始 loading 设为 true，确保骨架屏先展示
const loading = ref(true)
const currentKeyword = ref('')
const currentType = ref('全部')
const teaTypes = ['绿茶', '红茶', '乌龙茶', '白茶', '普洱茶', '花茶']

const toDetail = (id) => {
  router.push(`/front/detail/${id}`)
}

const loadTeaList = async () => {
  // 开始请求时显示 loading
  loading.value = true

  let url = '/tea/list'
  if (currentKeyword.value) {
    url = `/tea/search?name=${currentKeyword.value}`
    currentType.value = '全部'
  } else if (currentType.value !== '全部') {
    url = `/tea/type?type=${currentType.value}`
  }

  try {
    const res = await axios.get(url)
    if(res.data.code === '200') teaList.value = res.data.data
  } catch(e) { console.error(e) }
  finally {
    // 延迟 300ms 关闭 loading，防止请求太快导致骨架屏一闪而过，视觉更平滑
    // 如果追求极致快，可以去掉 setTimeout，直接写 loading.value = false
    setTimeout(() => { loading.value = false }, 300)
  }
}

const filterByType = (type) => {
  currentKeyword.value = ''
  currentType.value = type
  loadTeaList()
}

const clearSearch = () => {
  router.push('/front/home')
}

const loadBannerList = async () => {
  try {
    const res = await axios.get('/carousel/active')
    if(res.data.code === '200') bannerList.value = res.data.data
  } catch(e) {}
}

onMounted(() => {
  loadBannerList()
  if (route.query.name) currentKeyword.value = route.query.name
  loadTeaList()
})

// 🚀【关键点 3】配合 KeepAlive，当从详情页切回来时触发
onActivated(() => {
  // 这里可以什么都不做，数据会保留上次的状态（实现了 0 秒切换）
  // 只有当你需要每次切回来都强制刷新数据时，才在这里调用 loadTeaList()
})

watch(() => route.query.name, (newName) => {
  currentKeyword.value = newName || ''
  loadTeaList()
})
</script>

<style scoped>
/* 页面整体容器 */
.home-container {
  display: flex; flex-direction: column; gap: 25px;
  max-width: 1200px; margin: 0 auto;
}

/* 通用模块卡片样式 */
.module-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0,0,0,0.04);
  border: 1px solid rgba(85, 171, 98, 0.08);
}

/* --- 1. 轮播图 --- */
.banner-section :deep(.el-carousel__container) { height: 400px !important; }
.banner-image {
  width: 100%; height: 100%;
  background-position: center; background-size: cover;
  position: relative;
}
.banner-content {
  position: absolute; top: 50%; left: 60px; transform: translateY(-50%);
  color: #fff; text-shadow: 0 2px 10px rgba(0,0,0,0.2);
  animation: slideIn 0.8s ease-out;
}
.banner-content h3 { font-size: 32px; margin-bottom: 20px; font-weight: bold; letter-spacing: 2px; }
.banner-btn { padding: 12px 30px; font-weight: bold; letter-spacing: 1px; box-shadow: 0 4px 15px rgba(85, 171, 98, 0.4); }
.banner-empty { height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #a8c6b5; background: #f0f7f3; }

/* --- 2. 分类栏 --- */
.category-section { display: flex; justify-content: center; padding: 10px 0; }
.cat-capsule-group {
  display: flex; gap: 12px; padding: 6px;
  background: rgba(255,255,255,0.6); backdrop-filter: blur(10px);
  border-radius: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.03);
}
.cat-capsule {
  padding: 10px 28px; border-radius: 30px; cursor: pointer;
  color: #556677; font-size: 15px; font-weight: 500;
  transition: all 0.3s ease;
}
.cat-capsule:hover { color: #55ab62; background: #eef7f2; }
.cat-capsule.active {
  background: #55ab62; color: white;
  box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3); font-weight: bold;
}

/* --- 3. 商品列表 --- */
.goods-section { padding: 30px; }
.section-header {
  margin-bottom: 30px; display: flex; align-items: center; justify-content: space-between;
}
.title-box { border-left: 5px solid #55ab62; padding-left: 15px; }
.main-title { font-size: 24px; font-weight: 800; color: #2c3e50; margin-right: 15px; }
.sub-title { font-size: 14px; color: #9aaabf; font-weight: normal; }

/* Grid 网格布局 */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 25px;
}

.goods-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  cursor: pointer; transition: all 0.35s ease;
  border: 1px solid #f0f2f5;
}
.goods-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(85, 171, 98, 0.12);
  border-color: transparent;
}
/* 骨架屏专用样式 */
.skeleton-card { border: none; box-shadow: none; background: transparent; }

/* 图片容器 */
.card-img-box {
  height: 200px; width: 100%;
  position: relative; overflow: hidden;
  background-color: #f4f9f6;
}
.card-img-box img {
  width: 100%; height: 100%;
  object-fit: cover; object-position: center;
  transition: transform 0.6s cubic-bezier(0.2, 1, 0.3, 1);
}
.goods-card:hover .card-img-box img { transform: scale(1.08); }

.hover-mask {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(44, 62, 80, 0.4);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: all 0.3s ease;
}
.goods-card:hover .hover-mask { opacity: 1; }
.view-text {
  color: white; font-weight: bold; padding: 8px 20px;
  border: 2px solid white; border-radius: 30px; letter-spacing: 1px;
}

/* 卡片信息 */
.card-info { padding: 18px; }
.tea-title { font-size: 16px; font-weight: bold; color: #333; margin-bottom: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.tag-pill {
  font-size: 12px; color: #55ab62; background: #e8f5e9;
  padding: 4px 10px; border-radius: 20px; font-weight: 500;
}
.card-bottom { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 15px; }
.price-box { color: #ff6b6b; font-weight: 800; line-height: 1; }
.symbol { font-size: 16px; margin-right: 2px; }
.price-num { font-size: 24px; }
.sales-info { font-size: 13px; color: #bdc3c7; }

@keyframes slideIn { from { opacity: 0; transform: translateX(-30px) translateY(-50%); } to { opacity: 1; transform: translateX(0) translateY(-50%); } }
</style>