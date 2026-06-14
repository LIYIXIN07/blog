<template>
  <div class="nblog-widget m-box visitor-map-widget">
    <div class="nblog-widget-header">
      <el-icon><Location /></el-icon>
      访客地图
    </div>
    <div class="visitor-map-body">
      <div ref="chartRef" class="visitor-map-chart" />
      <div v-if="!loading && !hasData" class="visitor-map-empty">暂无访客地理数据</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts/core'
import { MapChart, ScatterChart, EffectScatterChart } from 'echarts/charts'
import { GeoComponent, TooltipComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { Location } from '@element-plus/icons-vue'
import chinaJson from '@/assets/china.json'
import geoCoordMap from '@/utils/city2coord.json'
import { getVisitorMap } from '@/api/statistics'
import { buildVisitorMapOption, convertCityVisitors } from '@/utils/visitorMap'
import type { CityVisitor } from '@/utils/visitorMap'

const chartRef = ref<HTMLElement>()
const loading = ref(true)
const cityVisitors = ref<CityVisitor[]>([])
let chart: echarts.ECharts | null = null
let registered = false

echarts.use([
  MapChart,
  ScatterChart,
  EffectScatterChart,
  GeoComponent,
  TooltipComponent,
  TitleComponent,
  CanvasRenderer,
])

const hasData = computed(() => cityVisitors.value.length > 0)

const ensureMapRegistered = () => {
  if (!registered) {
    echarts.registerMap('china', chinaJson as any)
    registered = true
  }
}

const renderChart = () => {
  if (!chartRef.value) return
  ensureMapRegistered()
  const mapData = convertCityVisitors(cityVisitors.value, geoCoordMap as Record<string, string[]>)
  chart?.dispose()
  chart = echarts.init(chartRef.value)
  chart.setOption(buildVisitorMapOption(mapData, ''))
}

const resizeChart = () => chart?.resize()

onMounted(async () => {
  try {
    cityVisitors.value = await getVisitorMap()
  } catch {
    cityVisitors.value = []
  } finally {
    loading.value = false
    renderChart()
    window.addEventListener('resize', resizeChart)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  chart?.dispose()
  chart = null
})
</script>

<style scoped>
.visitor-map-widget {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
  overflow: hidden;
}

.nblog-widget-header {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background: rgba(0, 0, 0, 0.03);
  border-bottom: 1px solid #eee;
  border-top: 3px solid #ffc107;
  display: flex;
  align-items: center;
  gap: 8px;
}

.visitor-map-body {
  position: relative;
  padding: 8px;
}

.visitor-map-chart {
  width: 100%;
  height: 280px;
}

@media (max-width: 768px) {
  .visitor-map-chart {
    height: 220px;
  }
}

.visitor-map-empty {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 13px;
  background: rgba(255, 255, 255, 0.92);
}
</style>
