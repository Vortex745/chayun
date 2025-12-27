<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="title">📊 销售数据统计</div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-title">总订单数</div>
          <div class="card-num">12</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card" style="background: #e1f3d8;">
          <div class="card-title">总销售额</div>
          <div class="card-num">￥484.5</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px;">
      <div id="mainChart" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, getCurrentInstance } from 'vue'
import * as echarts from 'echarts' // 引入 echarts

const { proxy } = getCurrentInstance()
const axios = proxy.$http

const initChart = async () => {
  // 1. 获取 DOM 元素
  const chartDom = document.getElementById('mainChart')
  const myChart = echarts.init(chartDom)

  // 2. 显示加载动画
  myChart.showLoading()

  try {
    // 3. 请求后端数据
    const res = await axios.get('/order/stats')
    myChart.hideLoading()

    if (res.data.code === '200') {
      const data = res.data.data // 后端返回 [{name: '西湖龙井', value: 5}, ...]

      // 提取 x轴(名称) 和 y轴(数量) 数据
      const names = data.map(item => item.name)
      const values = data.map(item => item.value)

      // 4. 配置图表选项
      const option = {
        title: { text: '茶叶销量排行榜' },
        tooltip: {},
        xAxis: { data: names },
        yAxis: {},
        series: [{
          name: '销量',
          type: 'bar', // 柱状图
          data: values,
          itemStyle: { color: '#409EFF' },
          barWidth: '40%'
        }]
      }
      // 5. 渲染
      myChart.setOption(option)
    }
  } catch (e) {
    console.error(e)
    myChart.hideLoading()
  }
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.title { font-weight: bold; }
.data-card { text-align: center; padding: 20px; }
.card-title { color: #909399; font-size: 14px; margin-bottom: 10px; }
.card-num { font-size: 24px; font-weight: bold; color: #303133; }
</style>