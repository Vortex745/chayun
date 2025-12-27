<template>
  <div>
    <el-carousel height="300px" style="margin-bottom: 20px; border-radius: 8px;">
      <el-carousel-item v-for="item in 3" :key="item">
        <div class="banner-placeholder">🌿 好茶，源自深山 (广告位 {{ item }})</div>
      </el-carousel-item>
    </el-carousel>

    <h2 style="font-weight: normal; border-left: 4px solid #409EFF; padding-left: 10px;">热销好茶</h2>

    <el-row :gutter="20">
      <el-col :span="6" v-for="tea in teaList" :key="tea.id" style="margin-bottom: 20px;">
        <el-card
            :body-style="{ padding: '0px' }"
            shadow="hover"
            class="goods-card"
            @click="router.push(`/front/detail/${tea.id}`)"
        >
          <img :src="tea.imgUrl" class="image">
          <div style="padding: 14px;">
            <div class="goods-name">{{ tea.name }}</div>
            <div class="goods-desc">{{ tea.content }}</div>
            <div class="bottom">
              <span class="price">￥{{ tea.price }}</span>
              <el-button type="primary" size="small" plain>加入购物车</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http
const teaList = ref([])
const router = useRouter()

onMounted(async () => {
  // 复用后端的查询接口！
  const res = await axios.get('/tea/list')
  if(res.data.code === '200') {
    teaList.value = res.data.data
  }
})
</script>

<style scoped>
.banner-placeholder { width: 100%; height: 100%; background: #eef5fe; display: flex; justify-content: center; align-items: center; color: #409EFF; font-size: 24px; }
.image { width: 100%; height: 200px; object-fit: cover; display: block; }
.goods-name { font-size: 16px; font-weight: bold; margin-bottom: 5px; }
.goods-desc { font-size: 12px; color: #999; margin-bottom: 10px; height: 16px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bottom { display: flex; justify-content: space-between; align-items: center; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.goods-card { transition: all 0.3s; cursor: pointer; }
.goods-card:hover { transform: translateY(-5px); }
</style>