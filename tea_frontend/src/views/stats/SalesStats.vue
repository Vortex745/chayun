<template>
  <div class="stats-container">

    <div class="overview-section">
      <el-row :gutter="25">
        <el-col :span="12">
          <div class="stat-card card-blue">
            <div class="stat-icon"><el-icon><List /></el-icon></div>
            <div class="stat-info">
              <div class="label">总订单量 (单)</div>
              <div class="value">12</div>
            </div>
            <div class="bg-decoration"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-card card-green">
            <div class="stat-icon"><el-icon><Money /></el-icon></div>
            <div class="stat-info">
              <div class="label">总销售额 (元)</div>
              <div class="value">¥ 484.50</div>
            </div>
            <div class="bg-decoration"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-card class="chart-card" shadow="never">
      <template #header>
        <div class="chart-header">
          <span class="title">🍰 茶叶销量占比分析</span>
          <el-tag type="success" effect="plain" round size="small">实时数据</el-tag>
        </div>
      </template>
      <div id="mainChart" style="width: 100%; height: 500px;"></div>
    </el-card>

  </div>
</template>

<script setup>
import { onMounted, getCurrentInstance } from 'vue'
import * as echarts from 'echarts'
import { List, Money } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const axios = proxy.$http

const initChart = async () => {
  const chartDom = document.getElementById('mainChart')
  const myChart = echarts.init(chartDom)
  myChart.showLoading({ color: '#55ab62' })

  try {
    const res = await axios.get('/order/stats')
    myChart.hideLoading()

    if (res.data.code === '200') {
      const data = res.data.data // 数据格式应为 [{name: '西湖龙井', value: 10}, ...]

      const option = {
        // 提示框
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)' // 显示格式：名称: 数值 (百分比)
        },
        // 图例 (底部)
        legend: {
          bottom: '5%',
          left: 'center',
          icon: 'circle'
        },
        // 自定义清新配色
        color: ['#55ab62', '#3bb2b8', '#ffc069', '#ff85c0', '#95de64', '#5cdbd3'],

        series: [
          {
            name: '销量占比',
            type: 'pie',
            radius: ['40%', '70%'], // 内圆和外圆半径，做成环形图
            center: ['50%', '45%'], // 调整位置，给底部图例留空间
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            // 中间默认显示的文字
            label: {
              show: false,
              position: 'center'
            },
            // 鼠标悬停时中间显示的文字
            emphasis: {
              label: {
                show: true,
                fontSize: 28,
                fontWeight: 'bold',
                color: '#333'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }
      myChart.setOption(option)

      // 监听窗口大小改变，自适应图表
      window.addEventListener('resize', () => {
        myChart.resize()
      })
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
.stats-container { padding: 10px; }

/* 1. 数据概览卡片 */
.stat-card {
  height: 120px; border-radius: 16px; color: white;
  display: flex; align-items: center; padding: 0 40px;
  position: relative; overflow: hidden;
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  transition: transform 0.3s; cursor: default;
}
.stat-card:hover { transform: translateY(-5px); }

/* 蓝色卡片 */
.card-blue { background: linear-gradient(135deg, #74ebd5 0%, #9face6 100%); }
/* 绿色卡片 (品牌色) */
.card-green { background: linear-gradient(135deg, #81fbb8 10%, #28c76f 100%); }

.stat-icon {
  width: 60px; height: 60px; background: rgba(255,255,255,0.2);
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 28px; margin-right: 25px; z-index: 2;
}
.stat-info { z-index: 2; }
.stat-info .label { font-size: 14px; opacity: 0.9; margin-bottom: 5px; }
.stat-info .value { font-size: 32px; font-weight: bold; font-family: Arial; }

/* 背景装饰圆 */
.bg-decoration {
  position: absolute; right: -20px; top: -20px;
  width: 150px; height: 150px; border-radius: 50%;
  background: rgba(255,255,255,0.1); z-index: 1;
}

/* 2. 图表卡片 */
.chart-card { border-radius: 16px; border: none; box-shadow: 0 4px 20px rgba(0,0,0,0.03); margin-top: 30px; }
.chart-header { display: flex; align-items: center; justify-content: space-between; }
.chart-header .title { font-size: 18px; font-weight: bold; color: #333; border-left: 4px solid #55ab62; padding-left: 12px; }
</style>