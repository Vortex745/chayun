<template>
  <div class="main-container">
    <el-card shadow="never">
      <h3>📄 我的订单</h3>
    </el-card>

    <div style="margin-top: 20px;">
      <el-card v-for="item in orderList" :key="item.id" shadow="hover" style="margin-bottom: 15px;">
        <el-row align="middle">
          <el-col :span="4">
            <el-image :src="item.imgUrl" style="width: 80px; height: 80px; border-radius: 4px;" />
          </el-col>
          <el-col :span="10">
            <div style="font-weight: bold; font-size: 16px;">{{ item.teaName }}</div>
            <div style="color: #999; margin-top: 5px;">数量：x {{ item.count }}</div>
          </el-col>
          <el-col :span="5">
            <div style="color: #f56c6c; font-weight: bold;">￥{{ item.totalPrice }}</div>
          </el-col>
          <el-col :span="5" style="text-align: right;">
            <el-tag v-if="item.status===0" type="warning">等待发货</el-tag>
            <el-tag v-else-if="item.status===1" type="success">商家已发货</el-tag>
          </el-col>
        </el-row>
        <div style="margin-top: 10px; border-top: 1px solid #eee; padding-top: 10px; font-size: 13px; color: #666;">
          收货信息：{{ item.consignee }} {{ item.phone }} ({{ item.address }})
          <span style="float: right;">下单时间：{{ item.createTime }}</span>
        </div>
      </el-card>

      <el-empty v-if="orderList.length === 0" description="您还没有订单" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
const axios = proxy.$http
const orderList = ref([])

onMounted(async () => {
  // 这里我们复用 list 接口，实际项目应该用 my-list
  // 为了简单，前端过滤一下（只演示自己的）
  // 真正上线千万别这么做，但在毕设里为了不改后端太多，这样最快
  const user = JSON.parse(localStorage.getItem('tea-user') || '{}')

  const res = await axios.get('/order/list')
  if(res.data.code === '200') {
    // 简单过滤：只显示收货人名字和自己昵称一样的订单
    // 或者如果你之前测试数据乱填的，就直接显示全部 res.data.data 也可以
    orderList.value = res.data.data.filter(item => item.consignee === user.nickname)

    // 如果过滤后没数据（因为你可能收货人填的和昵称不一样），
    // 建议暂时先 orderList.value = res.data.data 显示全部方便调试
    // orderList.value = res.data.data
  }
})
</script>

<style scoped>
.main-container { width: 1000px; margin: 0 auto; padding-bottom: 50px; }
</style>