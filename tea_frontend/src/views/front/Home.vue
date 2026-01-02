<template>
  <div class="home-container">

    <div class="banner-section module-card">
      <el-skeleton :loading="loading && bannerList.length === 0" animated>
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 400px;" />
        </template>
        <template #default>
          <el-carousel v-if="bannerList.length > 0" height="400px" class="main-carousel" :interval="8000" arrow="hover">
            <el-carousel-item v-for="item in bannerList" :key="item.id">

              <div v-if="isVideo(item.imgUrl)" class="banner-video-wrap">
                <video
                    :src="item.imgUrl"
                    autoplay
                    muted
                    loop
                    playsinline
                    class="banner-video"
                ></video>
                <div class="banner-content" v-if="item.linkText">
                  <h3>{{ item.linkText }}</h3>
                  <el-button type="success" round class="banner-btn">立刻查看</el-button>
                </div>
              </div>

              <div v-else class="banner-image" :style="{ backgroundImage: `url(${item.imgUrl})` }">
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
        <div class="cat-capsule" :class="{ active: currentType === '全部' }" @click="filterByType('全部')">全部好茶</div>
        <div class="cat-capsule" v-for="type in teaTypes" :key="type" :class="{ active: currentType === type }" @click="filterByType(type)">{{ type }}</div>
      </div>
    </div>

    <div class="goods-section module-card">
      <div class="section-header">
        <div class="title-box">
          <span class="main-title">{{ currentType === '全部' ? '当季热销' : currentType + '精选' }}</span>
          <span class="sub-title" v-if="currentType === '全部'">Nature's Gift · 大自然的馈赠</span>
        </div>
        <transition name="el-fade-in">
          <el-tag v-if="currentKeyword" closable @close="clearSearch" type="success" effect="dark" class="search-tag">🔍 搜索: {{ currentKeyword }}</el-tag>
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
        <div class="goods-card" v-for="tea in teaList" :key="tea.id" @click="toDetail(tea.id)">
          <div class="card-img-box">
            <img :src="tea.imgUrl" :alt="tea.name" loading="lazy" />
            <div class="hover-mask"><span class="view-text">查看详情</span></div>
          </div>
          <div class="card-info">
            <div class="tea-title" :title="tea.name">{{ tea.name }}</div>
            <div class="tea-tags"><span class="tag-pill">{{ tea.type || '精选' }}</span></div>
            <div class="card-bottom">
              <div class="price-box"><span class="symbol">¥</span><span class="price-num">{{ tea.price }}</span></div>
              <div class="sales-info">近期热卖</div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && teaList.length === 0" image-size="160" description="暂无相关茶品，去看看其他分类吧~">
        <template #image>
          <img src="https://cdn-icons-png.flaticon.com/512/743/743131.png" style="opacity: 0.5" />
        </template>
      </el-empty>
    </div>

    <div class="float-message" @click="openMessageCenter">
      <el-badge :is-dot="hasNewReply" class="badge-dot">
        <el-icon size="24"><BellFilled /></el-icon>
      </el-badge>
      <span class="msg-text">消息</span>
    </div>

    <el-drawer
        v-model="msgDrawerVisible"
        title="我的消息"
        direction="rtl"
        size="420px"
        class="custom-drawer"
    >
      <div class="msg-list">
        <div v-if="myMessages.length > 0">
          <div class="msg-card" v-for="msg in myMessages" :key="msg.id">

            <div class="msg-meta">
              <span class="time">{{ formatTime(msg.createTime) }}</span>
              <span class="status-dot" :class="{ replied: msg.replyList && msg.replyList.length > 0 }"></span>
              <span class="status-text">{{ (msg.replyList && msg.replyList.length > 0) ? '已回复' : '待处理' }}</span>
            </div>

            <div class="msg-body">
              <div class="chat-bubble user-bubble">
                <div class="bubble-title">我的留言</div>
                <div class="bubble-content">{{ msg.content }}</div>
              </div>

              <template v-if="msg.replyList && msg.replyList.length > 0">
                <div
                    class="chat-bubble admin-bubble"
                    v-for="reply in msg.replyList"
                    :key="reply.id"
                >
                  <div class="bubble-title">
                    <span class="role-badge"><el-icon><Service /></el-icon> 客服</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="bubble-content">{{ reply.content }}</div>
                </div>
              </template>

              <div class="waiting-reply" v-else>
                <el-icon class="is-loading"><Loading /></el-icon> 正在等待客服小姐姐回复...
              </div>
            </div>

          </div>
        </div>

        <el-empty v-else description="暂无消息记录" :image-size="120"></el-empty>
      </div>
    </el-drawer>

  </div>
</template>

<script setup>
defineOptions({ name: 'Home' })

import { ref, onMounted, watch, getCurrentInstance, onActivated, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Picture, BellFilled, Service, Loading } from '@element-plus/icons-vue'
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

// 【新增】判断视频函数
const isVideo = (url) => {
  if (!url) return false
  const lower = url.toLowerCase()
  return lower.endsWith('.mp4') || lower.endsWith('.webm') || lower.endsWith('.ogg') || lower.endsWith('.mov')
}

const refreshUser = () => {
  const userStr = localStorage.getItem('tea-user')
  if (userStr) {
    try {
      user.value = JSON.parse(userStr)
    } catch(e) {
      user.value = {}
    }
  } else {
    user.value = {}
  }
}

const hasNewReply = computed(() => {
  return myMessages.value.some(m => m.replyList && m.replyList.length > 0)
})

const toDetail = (id) => {
  router.push(`/front/detail/${id}`)
}

const loadTeaList = async () => {
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

const loadMyMessages = async () => {
  if (!user.value.id) {
    myMessages.value = []
    return
  }
  try {
    const res = await axios.get('/message/list')
    if(res.data.code === '200') {
      myMessages.value = res.data.data.filter(m => m.userId === user.value.id)
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

const formatTime = (timeStr) => {
  if(!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 16)
}

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
/* 轮播图视频相关样式 */
.banner-section :deep(.el-carousel__container) { height: 400px !important; }
.banner-image { width: 100%; height: 100%; background-position: center; background-size: cover; position: relative; }

/* 视频容器 */
.banner-video-wrap { width: 100%; height: 100%; position: relative; overflow: hidden; background: black; }
.banner-video {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 关键：让视频像图片一样填满容器 */
  display: block;
}

.banner-content { position: absolute; top: 50%; left: 60px; transform: translateY(-50%); color: #fff; text-shadow: 0 2px 10px rgba(0,0,0,0.5); animation: slideIn 0.8s ease-out; z-index: 2; }
.banner-content h3 { font-size: 32px; margin-bottom: 20px; font-weight: bold; letter-spacing: 2px; }
.banner-btn { padding: 12px 30px; font-weight: bold; letter-spacing: 1px; box-shadow: 0 4px 15px rgba(85, 171, 98, 0.4); }
.banner-empty { height: 400px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #a8c6b5; background: #f0f7f3; }

/* 基础样式保持不变 */
.home-container { display: flex; flex-direction: column; gap: 25px; max-width: 1200px; margin: 0 auto; }
.module-card { background: #fff; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 25px rgba(0,0,0,0.04); border: 1px solid rgba(85, 171, 98, 0.08); }
.category-section { display: flex; justify-content: center; padding: 10px 0; }
.cat-capsule-group { display: flex; gap: 12px; padding: 6px; background: rgba(255,255,255,0.6); backdrop-filter: blur(10px); border-radius: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); }
.cat-capsule { padding: 10px 28px; border-radius: 30px; cursor: pointer; color: #556677; font-size: 15px; font-weight: 500; transition: all 0.3s ease; }
.cat-capsule:hover { color: #55ab62; background: #eef7f2; }
.cat-capsule.active { background: #55ab62; color: white; box-shadow: 0 4px 12px rgba(85, 171, 98, 0.3); font-weight: bold; }
.goods-section { padding: 30px; }
.section-header { margin-bottom: 30px; display: flex; align-items: center; justify-content: space-between; }
.title-box { border-left: 5px solid #55ab62; padding-left: 15px; }
.main-title { font-size: 24px; font-weight: 800; color: #2c3e50; margin-right: 15px; }
.sub-title { font-size: 14px; color: #9aaabf; font-weight: normal; }
.goods-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 25px; }
.goods-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.35s ease; border: 1px solid #f0f2f5; }
.goods-card:hover { transform: translateY(-8px); box-shadow: 0 15px 35px rgba(85, 171, 98, 0.12); border-color: transparent; }
.skeleton-card { border: none; box-shadow: none; background: transparent; }
.card-img-box { height: 200px; width: 100%; position: relative; overflow: hidden; background-color: #f4f9f6; }
.card-img-box img { width: 100%; height: 100%; object-fit: cover; object-position: center; transition: transform 0.6s cubic-bezier(0.2, 1, 0.3, 1); }
.goods-card:hover .card-img-box img { transform: scale(1.08); }
.hover-mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(44, 62, 80, 0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: all 0.3s ease; }
.goods-card:hover .hover-mask { opacity: 1; }
.view-text { color: white; font-weight: bold; padding: 8px 20px; border: 2px solid white; border-radius: 30px; letter-spacing: 1px; }
.card-info { padding: 18px; }
.tea-title { font-size: 16px; font-weight: bold; color: #333; margin-bottom: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.tag-pill { font-size: 12px; color: #55ab62; background: #e8f5e9; padding: 4px 10px; border-radius: 20px; font-weight: 500; }
.card-bottom { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 15px; }
.price-box { color: #ff6b6b; font-weight: 800; line-height: 1; }
.symbol { font-size: 16px; margin-right: 2px; }
.price-num { font-size: 24px; }
.sales-info { font-size: 13px; color: #bdc3c7; }
@keyframes slideIn { from { opacity: 0; transform: translateX(-30px) translateY(-50%); } to { opacity: 1; transform: translateX(0) translateY(-50%); } }

.float-message {
  position: fixed; bottom: 40px; right: 30px; z-index: 990;
  width: 50px; height: 50px; background: white;
  border-radius: 50%; box-shadow: 0 4px 20px rgba(85, 171, 98, 0.25);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s; color: #55ab62; border: 1px solid #eef7f2;
}
.float-message:hover { transform: translateY(-3px); box-shadow: 0 8px 25px rgba(85, 171, 98, 0.35); background: #f0f9eb; }
.msg-text { font-size: 10px; margin-top: -2px; font-weight: bold; }
.badge-dot :deep(.el-badge__content.is-fixed) { top: 0; right: 0; transform: translateY(-50%) translateX(50%); border: 1px solid white; }

.msg-list { padding: 15px; display: flex; flex-direction: column; gap: 20px; }
.msg-card { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); border: 1px solid #f2f2f2; overflow: hidden; transition: all 0.3s; }
.msg-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.msg-meta { padding: 10px 15px; background: #fdfdfd; border-bottom: 1px solid #f8f8f8; display: flex; align-items: center; gap: 8px; font-size: 12px; color: #999; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; background: #ccc; }
.status-dot.replied { background: #55ab62; }
.status-text { font-weight: 500; }
.msg-body { padding: 15px; display: flex; flex-direction: column; gap: 12px; }

/* 聊天气泡优化 */
.chat-bubble { border-radius: 8px; padding: 10px 12px; font-size: 14px; line-height: 1.5; position: relative; margin-bottom: 8px; }
.bubble-title { font-size: 12px; margin-bottom: 4px; opacity: 0.8; display: flex; align-items: center; justify-content: space-between; }
.reply-time { font-size: 10px; opacity: 0.6; font-weight: normal; }
.role-badge { display: flex; align-items: center; gap: 4px; font-weight: bold; }

.user-bubble { background: #f4f6f8; color: #333; border-top-left-radius: 2px; }
.user-bubble .bubble-title { color: #666; font-weight: bold; justify-content: flex-start; }

.admin-bubble { background: #eef9f2; color: #333; border-top-right-radius: 2px; border: 1px solid #e1f3d8; }
.admin-bubble .bubble-title { color: #55ab62; }

.waiting-reply { font-size: 12px; color: #aaa; font-style: italic; display: flex; align-items: center; gap: 5px; margin-left: 5px; }
</style>