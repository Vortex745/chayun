<template>
  <div class="home-wrapper">
    
    <!-- Hero Banner Section -->
    <div class="hero-container container">
      <el-skeleton :loading="loading && bannerList.length === 0" animated class="hero-skeleton">
        <template #template>
          <el-skeleton-item variant="image" class="hero-skeleton-img" />
        </template>
        <template #default>
          <div class="hero-card">
            <el-carousel v-if="bannerList.length > 0" height="460px" :interval="6000" arrow="hover" trigger="click" indicator-position="none">
              <el-carousel-item v-for="item in bannerList" :key="item.id">
                <div class="banner-item">
                  
                  <div v-if="isVideo(item.imgUrl)" class="media-wrap">
                    <video :src="item.imgUrl" autoplay muted loop playsinline class="banner-video"></video>
                  </div>
                  <div v-else class="media-wrap" :style="{ backgroundImage: `url(${item.imgUrl})` }"></div>

                  <!-- Text Overlay with Gradient -->
                  <div class="banner-overlay" v-if="item.linkText">
                    <div class="banner-text-content">
                      <h2 class="slide-up-fade">{{ item.linkText }}</h2>
                      <button class="cta-button slide-up-fade delay-100" @click="toBannerLink(item)">
                        Explore Selection <el-icon class="icon"><ArrowRight /></el-icon>
                      </button>
                    </div>
                  </div>

                </div>
              </el-carousel-item>
            </el-carousel>

            <!-- Empty State -->
            <div v-else class="banner-empty">
              <div class="empty-content">
                <el-icon :size="64" color="#a8c6b5"><Picture /></el-icon>
                <p>Seeking the finest tea...</p>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- Sticky Category Nav -->
    <div class="sticky-nav-wrap">
      <div class="glass-nav">
        <div 
          class="nav-item" 
          :class="{ active: currentType === '全部' }" 
          @click="filterByType('全部')"
        >
          <span>全部好茶</span>
        </div>
        <div 
          class="nav-item" 
          v-for="type in teaTypes" 
          :key="type"
          :class="{ active: currentType === type }"
          @click="filterByType(type)"
        >
          <span>{{ type }}</span>
        </div>
      </div>
    </div>

    <!-- Main Product Section -->
    <main class="products-container container">
      
      <div class="section-header">
        <div class="header-left">
          <h2 class="section-title">{{ currentType === '全部' ? '当季热销' : currentType }}</h2>
          <span class="section-subtitle">Nature's Gift · 大自然的馈赠</span>
        </div>
        
        <transition name="fade-slide">
          <div v-if="currentKeyword" class="search-indicator" @click="clearSearch">
            <span>搜索: {{ currentKeyword }}</span>
            <el-icon class="close-icon"><Close /></el-icon>
          </div>
        </transition>
      </div>

      <div v-if="loading" class="goods-grid">
        <div v-for="i in 8" :key="i" class="goods-card skeleton-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 260px; border-radius: 12px;" />
              <div style="padding: 16px 0;">
                <el-skeleton-item variant="p" style="width: 60%" />
                <el-skeleton-item variant="p" style="width: 30%; margin-top: 8px;" />
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
          <div class="card-image-wrap">
            <img :src="tea.imgUrl" :alt="tea.name" loading="lazy" />
            <div class="card-overlay">
              <span class="view-btn">查看详情</span>
            </div>
          </div>
          
          <div class="card-content">
            <h3 class="tea-name" :title="tea.name">{{ tea.name }}</h3>
            <div class="tea-meta">
              <span class="tea-tag">{{ tea.type || '精选' }}</span>
              <div class="price-row">
                <span class="currency">¥</span>
                <span class="amount">{{ tea.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && teaList.length === 0" description="暂无相关茶品" :image-size="200" />
    </main>

    <!-- Floating Message Button -->
    <div class="floating-fab" @click="openMessageCenter" :class="{ 'has-new': hasNewReply }">
      <div class="fab-content">
        <el-icon :size="24"><BellFilled /></el-icon>
        <span class="fab-badge" v-if="hasNewReply"></span>
      </div>
    </div>

    <!-- Message Drawer (Preserved Logic, Updated UI) -->
    <el-drawer
      v-model="msgDrawerVisible"
      title="消息中心"
      direction="rtl"
      size="400px"
      class="custom-drawer"
    >
      <div class="msg-list-wrap">
        <template v-if="myMessages.length > 0">
          <div class="msg-item" v-for="msg in myMessages" :key="msg.id">
            <div class="msg-header">
              <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
              <span class="msg-status" :class="{ replied: msg.replyList && msg.replyList.length > 0 }">
                {{ (msg.replyList && msg.replyList.length > 0) ? '已回复' : '待处理' }}
              </span>
            </div>
            
            <div class="bubble user-bubble">
              <div class="bubble-txt">{{ msg.content }}</div>
            </div>

            <template v-if="msg.replyList && msg.replyList.length > 0">
              <div class="bubble admin-bubble" v-for="reply in msg.replyList" :key="reply.id">
                <div class="admin-header">
                  <el-icon><Service /></el-icon> 客服回复
                </div>
                <div class="bubble-txt">{{ reply.content }}</div>
              </div>
            </template>
             <div class="waiting-txt" v-else>
                <el-icon class="is-loading"><Loading /></el-icon> 等待回复中...
              </div>
          </div>
        </template>
        <el-empty v-else description="暂无消息" :image-size="100"></el-empty>
      </div>
    </el-drawer>

  </div>
</template>

<script setup>
defineOptions({ name: 'Home' })

import { ref, onMounted, watch, getCurrentInstance, onActivated, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Picture, BellFilled, Service, Loading, ArrowRight, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const router = useRouter()
const route = useRoute()

const teaList = ref([])
const bannerList = ref([])
const loading = ref(true)
const currentKeyword = ref('')
const currentType = ref('全部')
const teaTypes = ['绿茶', '红茶', '乌龙茶', '白茶', '普洱茶', '花茶']

const msgDrawerVisible = ref(false)
const myMessages = ref([])
const user = ref({})

// Helpers
const isVideo = (url) => {
  if (!url) return false
  const lower = url.toLowerCase()
  return lower.endsWith('.mp4') || lower.endsWith('.webm') || lower.endsWith('.ogg') || lower.endsWith('.mov')
}

const formatTime = (timeStr) => {
  if(!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

// Actions
const refreshUser = () => {
  const userStr = localStorage.getItem('tea-user')
  try {
    user.value = userStr ? JSON.parse(userStr) : {}
  } catch(e) {
    user.value = {}
  }
}

const hasNewReply = computed(() => {
  return myMessages.value.some(m => m.replyList && m.replyList.length > 0)
})

const toDetail = (id) => {
  router.push(`/front/detail/${id}`)
}

const toBannerLink = (item) => {
  // Placeholder for banner action
  console.log('Banner clicked', item)
}

const filterByType = (type) => {
  currentKeyword.value = ''
  currentType.value = type
  loadTeaList()
}

const clearSearch = () => {
  currentKeyword.value = ''
  loadTeaList()
}

// API Calls
const loadTeaList = async () => {
  loading.value = true
  let url = '/tea/list'
  if (currentKeyword.value) {
    url = `/tea/search?name=${currentKeyword.value}`
    currentType.value = '全部' // Reset type visual when searching
  } else if (currentType.value !== '全部') {
    url = `/tea/type?type=${currentType.value}`
  }

  try {
    const res = await axios.get(url)
    if(res.data.code === '200') teaList.value = res.data.data
  } catch(e) { console.error(e) }
  finally {
    setTimeout(() => { loading.value = false }, 300)
  }
}

const loadBannerList = async () => {
  try {
    const res = await axios.get('/carousel/active')
    if(res.data.code === '200') bannerList.value = res.data.data
  } catch(e) {}
}

const loadMyMessages = async () => {
  if (!user.value.id) {
    myMessages.value = []
    return
  }
  try {
    const res = await axios.get(`/message/user/${user.value.id}`)
    if(res.data.code === '200') {
      myMessages.value = res.data.data || []
    }
  } catch(e) {}
}

const openMessageCenter = () => {
  refreshUser()
  if (!user.value.id) {
    ElMessage.warning('请先登录查看消息')
    router.push('/front/login')
    return
  }
  loadMyMessages()
  msgDrawerVisible.value = true
}

// Lifecycle
onMounted(() => {
  refreshUser()
  loadBannerList()
  if (route.query.name) currentKeyword.value = route.query.name
  loadTeaList()
  loadMyMessages()
})

onActivated(() => {
  refreshUser()
  loadMyMessages()
})

watch(() => route.query.name, (newName) => {
  currentKeyword.value = newName || ''
  loadTeaList()
})
</script>

<style scoped>
/* Hero Section */
.hero-container {
  margin-top: 24px;
}
.hero-skeleton {
  width: 100%;
  height: 460px;
}
.hero-skeleton-img {
  width: 100%;
  height: 100%;
  border-radius: var(--radius-lg);
}

.hero-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  position: relative;
  background: #000;
}
.banner-item {
  width: 100%;
  height: 100%;
  position: relative;
}
.media-wrap {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}
.banner-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(0,0,0,0.6) 0%, rgba(0,0,0,0) 60%);
  display: flex;
  align-items: center;
  padding-left: 80px;
}
.banner-text-content {
  color: #fff;
  max-width: 500px;
}
.banner-text-content h2 {
  font-size: 48px;
  font-weight: 300;
  margin-bottom: 32px;
  letter-spacing: 2px;
  line-height: 1.2;
}
.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: var(--tea-primary);
  color: white;
  border-radius: var(--radius-full);
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  backdrop-filter: blur(4px);
}
.cta-button:hover {
  background: white;
  color: var(--tea-primary);
  transform: translateX(4px);
}
.banner-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f4f1;
}
.empty-content {
  text-align: center;
  color: #8da399;
}

/* Slide Animations */
.slide-up-fade { animation: slideUpFade 0.8s ease-out forwards; opacity: 0; transform: translateY(20px); }
.delay-100 { animation-delay: 0.1s; }
@keyframes slideUpFade { to { opacity: 1; transform: translateY(0); } }

/* Sticky Nav */
.sticky-nav-wrap {
  position: sticky;
  top: 10px;
  z-index: 100;
  display: flex;
  justify-content: center;
  margin: 32px 0 40px;
  pointer-events: none; /* Let clicks pass through outer area */
}
.glass-nav {
  pointer-events: auto;
  display: flex;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  padding: 8px;
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-md);
  border: 1px solid rgba(255,255,255,0.4);
}
.nav-item {
  padding: 10px 24px;
  border-radius: var(--radius-full);
  cursor: pointer;
  color: var(--text-secondary);
  font-weight: 500;
  transition: all 0.3s var(--ease-smooth);
}
.nav-item:hover {
  color: var(--tea-primary);
  background: rgba(74, 124, 89, 0.05);
}
.nav-item.active {
  background: var(--tea-primary);
  color: white;
  box-shadow: var(--shadow-sm);
}

/* Products Section */
.products-container {
  padding-bottom: 60px;
  min-height: 600px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}
.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.section-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-main);
  margin: 0;
  font-family: "Songti SC", "SimSun", serif; /* Try to induce serif feel */
}
.section-subtitle {
  font-size: 14px;
  color: var(--text-placeholder);
  letter-spacing: 1px;
  text-transform: uppercase;
}
.search-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  padding: 8px 16px;
  border-radius: var(--radius-full);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.search-indicator:hover {
  background: #dcece3;
}

/* Grid */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 32px;
}
.goods-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-xs);
  transition: all 0.4s var(--ease-spring);
  cursor: pointer;
  border: 1px solid transparent;
}
.goods-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-md);
  border-color: rgba(74, 124, 89, 0.1);
}

.card-image-wrap {
  width: 100%;
  height: 280px;
  position: relative;
  overflow: hidden;
  background: #f8f8f8;
}
.card-image-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}
.goods-card:hover img {
  transform: scale(1.08); /* Gentle zoom */
}
.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.goods-card:hover .card-overlay {
  opacity: 1;
}
.view-btn {
  background: white;
  color: var(--tea-primary);
  padding: 10px 24px;
  border-radius: var(--radius-full);
  font-weight: 600;
  transform: translateY(10px);
  transition: transform 0.3s;
}
.goods-card:hover .view-btn {
  transform: translateY(0);
}

.card-content {
  padding: 20px;
}
.tea-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.tea-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tea-tag {
  background: var(--tea-primary-light);
  color: var(--tea-primary);
  font-size: 12px;
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  font-weight: 500;
}
.price-row {
  color: var(--tea-danger);
  font-weight: 700;
}
.currency { font-size: 14px; margin-right: 2px; }
.amount { font-size: 20px; }

/* FAB */
.floating-fab {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 900;
  filter: drop-shadow(0 8px 16px rgba(74, 124, 89, 0.3));
  transition: transform 0.3s var(--ease-spring);
  cursor: pointer;
}
.fab-content {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--tea-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid white; /* Double ring effect */
}
.floating-fab:hover {
  transform: scale(1.1);
}
.fab-badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 14px;
  height: 14px;
  background: #ff4757;
  border-radius: 50%;
  border: 2px solid white;
}

/* Message Drawer Styles */
.msg-list-wrap {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.msg-item {
  background: #f9f9f9;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #eee;
}
.msg-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}
.bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}
.user-bubble {
  background: white;
  color: #333;
  border: 1px solid #eee;
}
.admin-bubble {
  background: var(--tea-primary-light);
  color: var(--tea-primary-dark);
}
.admin-header {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.waiting-txt {
  font-size: 12px;
  color: #ccc;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
}
</style>
