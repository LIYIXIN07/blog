<template>
  <div v-loading="loading">
    <el-row :gutter="16" class="stat-row">
      <el-col v-for="item in statCards" :key="item.label" :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" :style="{ background: item.bg }">
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="panel-card">
      <template #header>
        <div class="panel-header">
          <span>访问量趋势（近 30 天）</span>
          <div class="panel-summary">
            <span>文章阅读 {{ formatNum(dashboard?.totalViews) }}</span>
            <span>独立访客 {{ formatNum(dashboard?.totalVisitors) }}</span>
          </div>
        </div>
      </template>
      <div ref="trafficChartRef" class="chart-box" />
    </el-card>

    <el-card shadow="never" class="panel-card">
      <template #header>访客地图</template>
      <div ref="mapChartRef" class="chart-box small-chart" />
    </el-card>

    <el-row :gutter="16" class="bottom-row">
      <el-col :span="10">
        <el-card shadow="never" class="panel-card">
          <template #header>分类文章分布</template>
          <div ref="categoryChartRef" class="chart-box small-chart" />
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>最近访客</span>
              <el-button link type="primary" @click="$router.push('/statistics/visitors')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentVisitors" border stripe size="small">
            <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
            <el-table-column prop="ipSource" label="来源" min-width="120" show-overflow-tooltip />
            <el-table-column prop="browser" label="浏览器" width="110" show-overflow-tooltip />
            <el-table-column prop="pv" label="PV" width="70" align="center" />
            <el-table-column prop="lastVisit" label="最后访问" width="160" />
          </el-table>
          <el-empty v-if="!recentVisitors.length" description="暂无访客数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { View, User, Document, Timer } from '@element-plus/icons-vue'
import chinaJson from '@/assets/china.json'
import geoCoordMap from '@/utils/city2coord.json'
import { getDashboardStats, getSiteStats, getVisitorList } from '@/api/statistics'
import { buildVisitorMapOption, convertCityVisitors } from '@/utils/visitorMap'
import type { DashboardStats, SiteStats, VisitRecord } from '@/types'

echarts.registerMap('china', chinaJson as any)

const loading = ref(false)
const siteStats = ref<SiteStats | null>(null)
const dashboard = ref<DashboardStats | null>(null)
const recentVisitors = ref<VisitRecord[]>([])

const trafficChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
const mapChartRef = ref<HTMLElement>()
let trafficChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null
let mapChart: echarts.ECharts | null = null

const formatNum = (num?: number | null) => {
  if (num == null) return '0'
  if (num >= 10000) return `${(num / 10000).toFixed(1)}万`
  return String(num)
}

const statCards = computed(() => [
  {
    label: '总访问量 (PV)',
    value: formatNum(siteStats.value?.totalPv),
    icon: View,
    bg: 'linear-gradient(135deg, #f56c6c, #ff9a9a)'
  },
  {
    label: '独立访客 (UV)',
    value: formatNum(siteStats.value?.totalUv),
    icon: User,
    bg: 'linear-gradient(135deg, #409eff, #79bbff)'
  },
  {
    label: '已发布文章',
    value: formatNum(siteStats.value?.totalArticles),
    icon: Document,
    bg: 'linear-gradient(135deg, #67c23a, #95d475)'
  },
  {
    label: '运行天数',
    value: `${siteStats.value?.runDays ?? 0} 天`,
    icon: Timer,
    bg: 'linear-gradient(135deg, #e6a23c, #f3d19e)'
  }
])

const renderTrafficChart = () => {
  if (!trafficChartRef.value || !dashboard.value) return
  trafficChart = echarts.init(trafficChartRef.value)
  trafficChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['访问量(PV)', '独立访客(UV)'] },
    grid: { left: 48, right: 24, top: 40, bottom: 32 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dashboard.value.traffic.map(item => item.date)
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '访问量(PV)',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.12 },
        itemStyle: { color: '#f56c6c' },
        data: dashboard.value.traffic.map(item => item.pv)
      },
      {
        name: '独立访客(UV)',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.12 },
        itemStyle: { color: '#409eff' },
        data: dashboard.value.traffic.map(item => item.uv)
      }
    ]
  })
}

const renderCategoryChart = () => {
  if (!categoryChartRef.value || !dashboard.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie',
      radius: ['38%', '68%'],
      center: ['50%', '44%'],
      data: dashboard.value.categories.map(item => ({
        name: item.name,
        value: item.count,
        itemStyle: { color: item.color }
      }))
    }]
  })
}

const renderMapChart = () => {
  if (!mapChartRef.value || !dashboard.value) return
  const mapData = convertCityVisitors(
    dashboard.value.cityVisitors,
    geoCoordMap as Record<string, string[]>
  )
  mapChart = echarts.init(mapChartRef.value)
  mapChart.setOption(buildVisitorMapOption(mapData))
}

const resizeCharts = () => {
  trafficChart?.resize()
  categoryChart?.resize()
  mapChart?.resize()
}

onMounted(async () => {
  loading.value = true
  try {
    const [site, dash, visitors] = await Promise.all([
      getSiteStats(),
      getDashboardStats(),
      getVisitorList({ pageNum: 1, pageSize: 5 })
    ])
    siteStats.value = site
    dashboard.value = dash
    recentVisitors.value = visitors.list
    renderTrafficChart()
    renderCategoryChart()
    renderMapChart()
    window.addEventListener('resize', resizeCharts)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  trafficChart?.dispose()
  categoryChart?.dispose()
  mapChart?.dispose()
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  border: none;
  margin-bottom: 16px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.panel-card {
  border: none;
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-summary {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 13px;
}

.chart-box {
  width: 100%;
  height: 360px;
}

.small-chart {
  height: 300px;
}

.bottom-row {
  margin-bottom: 0;
}
</style>
