<template>
  <div v-loading="loading" class="dashboard-page">
    <el-row :gutter="20" class="stat-row">
      <el-col v-for="item in statCards" :key="item.label" :span="6">
        <el-card shadow="never" class="stat-card" body-style="padding: 0">
          <div class="stat-card-inner">
            <div class="stat-icon-wrap" :style="{ color: item.color }">
              <el-icon :size="48"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-desc">
              <div class="stat-label">{{ item.label }}</div>
              <span class="stat-num">{{ item.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card shadow="never" class="dashboard-card">
          <div ref="categoryChartRef" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="dashboard-card">
          <div ref="tagChartRef" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="dashboard-card">
          <div ref="mapChartRef" class="chart-box" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="dashboard-card traffic-card">
      <div ref="trafficChartRef" class="chart-box traffic-chart" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { ChatDotRound, DataLine, Document, User } from '@element-plus/icons-vue'
import chinaJson from '@/assets/china.json'
import geoCoordMap from '@/utils/city2coord.json'
import { getDashboardStats } from '@/api/statistics'
import { buildVisitorMapOption, convertCityVisitors } from '@/utils/visitorMap'
import type { DashboardStats } from '@/types'

const PIE_PALETTE = ['#ff9800', '#ffc107', '#00bcd4', '#2196f3', '#9c27b0', '#4caf50', '#e91e63', '#795548']

const loading = ref(false)
const stats = ref<DashboardStats | null>(null)
const categoryChartRef = ref<HTMLElement>()
const tagChartRef = ref<HTMLElement>()
const mapChartRef = ref<HTMLElement>()
const trafficChartRef = ref<HTMLElement>()
let categoryChart: echarts.ECharts | null = null
let tagChart: echarts.ECharts | null = null
let mapChart: echarts.ECharts | null = null
let trafficChart: echarts.ECharts | null = null

echarts.registerMap('china', chinaJson as any)

const statCards = computed(() => [
  {
    label: '今日PV',
    value: stats.value?.todayPv ?? 0,
    icon: DataLine,
    color: '#409eff'
  },
  {
    label: '今日UV',
    value: stats.value?.todayUv ?? 0,
    icon: User,
    color: '#e6a23c'
  },
  {
    label: '文章数',
    value: stats.value?.totalArticles ?? 0,
    icon: Document,
    color: '#f56c6c'
  },
  {
    label: '评论数',
    value: stats.value?.totalComments ?? 0,
    icon: ChatDotRound,
    color: '#409eff'
  }
])

const buildRosePieOption = (title: string, data: { name: string; value: number }[]) => ({
  title: {
    text: title,
    left: 'center',
    textStyle: { fontSize: 16, fontWeight: 600, color: '#303133' }
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  legend: {
    left: 'center',
    bottom: 0,
    type: 'scroll',
    data: data.map(item => item.name),
    textStyle: { fontSize: 12, color: '#606266' }
  },
  series: [{
    name: '文章数量',
    type: 'pie',
    radius: [30, 110],
    center: ['50%', '46%'],
    roseType: 'area',
    data: data.map((item, index) => ({
      ...item,
      itemStyle: { color: PIE_PALETTE[index % PIE_PALETTE.length] }
    }))
  }]
})

const convertMapData = (data: DashboardStats['cityVisitors'] = []) =>
  convertCityVisitors(data, geoCoordMap as Record<string, string[]>)

const renderCategoryChart = () => {
  if (!categoryChartRef.value || !stats.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  const data = stats.value.categories.map(item => ({
    name: item.name,
    value: item.count
  }))
  categoryChart.setOption(buildRosePieOption('分类下文章数量', data))
}

const renderTagChart = () => {
  if (!tagChartRef.value || !stats.value) return
  tagChart = echarts.init(tagChartRef.value)
  const data = [...stats.value.tags]
    .sort((a, b) => (b.articleCount || 0) - (a.articleCount || 0))
    .map(item => ({
      name: item.name,
      value: item.articleCount || 0
    }))
  tagChart.setOption({
    ...buildRosePieOption('标签下文章数量', data),
    series: [{
      name: '文章数量',
      type: 'pie',
      radius: [30, 110],
      center: ['50%', '42%'],
      top: '-8%',
      roseType: 'area',
      data: data.map((item, index) => ({
        ...item,
        itemStyle: { color: PIE_PALETTE[index % PIE_PALETTE.length] }
      }))
    }]
  })
}

const renderMapChart = () => {
  if (!mapChartRef.value || !stats.value) return
  const mapData = convertMapData(stats.value.cityVisitors)
  mapChart = echarts.init(mapChartRef.value)
  mapChart.setOption(buildVisitorMapOption(mapData))
}

const renderTrafficChart = () => {
  if (!trafficChartRef.value || !stats.value) return
  trafficChart = echarts.init(trafficChartRef.value)
  const dates = stats.value.traffic.map(item => item.date)
  trafficChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      padding: [5, 10]
    },
    legend: {
      data: ['访问量(PV)', '独立访客(UV)'],
      textStyle: { color: '#303133' }
    },
    grid: { left: 10, right: 20, top: 40, bottom: 10, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisTick: { show: false }
    },
    series: [
      {
        name: '访问量(PV)',
        type: 'line',
        smooth: true,
        data: stats.value.traffic.map(item => item.pv),
        itemStyle: {
          color: '#FF005A'
        },
        lineStyle: {
          color: '#FF005A',
          width: 2
        },
        animationDuration: 2800,
        animationEasing: 'cubicInOut'
      },
      {
        name: '独立访客(UV)',
        type: 'line',
        smooth: true,
        data: stats.value.traffic.map(item => item.uv),
        itemStyle: {
          color: '#3888fa'
        },
        lineStyle: {
          color: '#3888fa',
          width: 2
        },
        areaStyle: {
          color: '#f3f8ff'
        },
        animationDuration: 2800,
        animationEasing: 'quadraticOut'
      }
    ]
  })
}

const resizeCharts = () => {
  categoryChart?.resize()
  tagChart?.resize()
  mapChart?.resize()
  trafficChart?.resize()
}

onMounted(async () => {
  loading.value = true
  try {
    stats.value = await getDashboardStats()
    renderCategoryChart()
    renderTagChart()
    renderMapChart()
    renderTrafficChart()
    window.addEventListener('resize', resizeCharts)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  categoryChart?.dispose()
  tagChart?.dispose()
  mapChart?.dispose()
  trafficChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.stat-card-inner {
  height: 108px;
  position: relative;
  overflow: hidden;
}

.stat-icon-wrap {
  float: left;
  margin: 14px 0 0 14px;
  padding: 16px;
}

.stat-desc {
  float: right;
  font-weight: 700;
  margin: 26px 26px 26px 0;
  text-align: right;
}

.stat-label {
  color: rgba(0, 0, 0, 0.45);
  font-size: 16px;
  margin-bottom: 12px;
}

.stat-num {
  font-size: 20px;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.dashboard-card {
  border: none;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.dashboard-card :deep(.el-card__body) {
  padding: 12px;
}

.chart-box {
  width: 100%;
  height: 500px;
}

.traffic-card {
  margin-bottom: 0;
}

.traffic-chart {
  height: 500px;
}
</style>
