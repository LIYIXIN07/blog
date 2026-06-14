import type { EChartsOption } from 'echarts'
import type { CallbackDataParams } from 'echarts/types/dist/shared'

export interface CityVisitor {
  city: string
  uv: number
}

export interface MapPoint {
  name: string
  value: [number, number]
  uv: number
}

const INVALID_CITY_PATTERN = /电信|移动|联通|网通|铁通|公司|Corporation|Inc\.?/i

const PROVINCE_CAPITAL: Record<string, string> = {
  广东省: '广州市',
  浙江省: '杭州市',
  江苏省: '南京市',
  山东省: '济南市',
  四川省: '成都市',
  湖北省: '武汉市',
  湖南省: '长沙市',
  河南省: '郑州市',
  河北省: '石家庄市',
  福建省: '福州市',
  安徽省: '合肥市',
  江西省: '南昌市',
  陕西省: '西安市',
  辽宁省: '沈阳市',
  吉林省: '长春市',
  黑龙江省: '哈尔滨市',
  山西省: '太原市',
  云南省: '昆明市',
  贵州省: '贵阳市',
  广西壮族自治区: '南宁市',
  海南省: '海口市',
  甘肃省: '兰州市',
  青海省: '西宁市',
  内蒙古自治区: '呼和浩特市',
  宁夏回族自治区: '银川市',
  新疆维吾尔自治区: '乌鲁木齐市',
  西藏自治区: '拉萨市',
  北京市: '北京市',
  天津市: '天津市',
  上海市: '上海市',
  重庆市: '重庆市',
}

const MAP_AREA_STYLE = {
  areaColor: '#0d0059',
  borderColor: '#389dff',
  borderWidth: 1,
  shadowBlur: 5,
  shadowOffsetY: 8,
  shadowOffsetX: 0,
  shadowColor: '#01012a',
}

const MAP_EMPHASIS = {
  itemStyle: {
    areaColor: '#184cff',
    shadowOffsetX: 0,
    shadowOffsetY: 0,
    shadowBlur: 5,
    borderWidth: 0,
    shadowColor: 'rgba(0, 0, 0, 0.5)',
  },
}

export function resolveCityCoord(
  city: string,
  coordMap: Record<string, string[]>
): [number, number] | null {
  if (!city || INVALID_CITY_PATTERN.test(city)) {
    return null
  }

  const candidates = [
    city,
    city.endsWith('市') ? city : `${city}市`,
    city.endsWith('市') ? city.slice(0, -1) : city,
    city.replace(/(特别行政区|自治州|地区|盟)$/, ''),
  ]

  for (const name of candidates) {
    const coord = coordMap[name]
    if (coord?.length === 2) {
      return [Number(coord[0]), Number(coord[1])]
    }
  }

  const province = Object.keys(PROVINCE_CAPITAL).find((name) => city.includes(name.replace(/省|市|自治区/g, '')))
  if (province) {
    const capital = PROVINCE_CAPITAL[province]
    const coord = coordMap[capital]
    if (coord?.length === 2) {
      return [Number(coord[0]), Number(coord[1])]
    }
  }

  return null
}

export function convertCityVisitors(
  data: CityVisitor[] = [],
  coordMap: Record<string, string[]>
): MapPoint[] {
  return data
    .filter((item) => item.city && !INVALID_CITY_PATTERN.test(item.city))
    .map((item) => {
      const coord = resolveCityCoord(item.city, coordMap)
      if (!coord) return null
      return {
        name: item.city,
        value: coord,
        uv: item.uv,
      }
    })
    .filter((item): item is MapPoint => item !== null)
    .sort((a, b) => b.uv - a.uv)
}

export function buildVisitorMapOption(
  mapData: MapPoint[],
  title = '访客地图'
): EChartsOption {
  const topData = mapData.slice(0, 5)

  return {
    title: title
      ? {
          text: title,
          left: 'center',
          textStyle: { fontSize: 16, fontWeight: 600, color: '#303133' },
        }
      : undefined,
    tooltip: { show: false },
    geo: {
      map: 'china',
      roam: false,
      zoom: 1.24,
      center: [104.2, 36],
      label: { show: false },
      itemStyle: MAP_AREA_STYLE,
      emphasis: MAP_EMPHASIS,
    },
    series: [
      {
        type: 'map',
        map: 'china',
        geoIndex: 0,
        roam: false,
        zoom: 1.24,
        center: [104.2, 36],
        showLegendSymbol: false,
        label: { show: false },
        itemStyle: MAP_AREA_STYLE,
        emphasis: MAP_EMPHASIS,
      },
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        geoIndex: 0,
        data: mapData,
        symbol: 'circle',
        symbolSize: (_rawValue: number | number[], params: CallbackDataParams) => {
          const data = params.data as MapPoint | undefined
          return Math.max(5, Math.min(10, 4 + (data?.uv ?? 1)))
        },
        tooltip: {
          show: true,
          formatter: (params: CallbackDataParams) => {
            const data = params.data as MapPoint
            return `${data.name}<br/>访客数：${data.uv}`
          },
        },
        itemStyle: { color: '#0efacc' },
        zlevel: 2,
      },
      {
        type: 'effectScatter',
        coordinateSystem: 'geo',
        geoIndex: 0,
        data: topData,
        symbol: 'circle',
        symbolSize: (_rawValue: number | number[], params: CallbackDataParams) => {
          const data = params.data as MapPoint | undefined
          return Math.max(10, Math.min(18, 8 + (data?.uv ?? 1) * 2))
        },
        tooltip: {
          show: true,
          formatter: (params: CallbackDataParams) => {
            const data = params.data as MapPoint
            return `${data.name}<br/>访客数：${data.uv}`
          },
        },
        showEffectOn: 'render',
        rippleEffect: {
          brushType: 'stroke',
          color: '#0efacc',
          period: 4,
          scale: 4,
        },
        label: {
          formatter: '{b}',
          position: 'right',
          show: true,
          color: '#0efacc',
          fontSize: 11,
        },
        itemStyle: {
          color: '#0efacc',
          shadowBlur: 8,
          shadowColor: 'rgba(14, 250, 204, 0.45)',
        },
        zlevel: 3,
      },
    ],
  }
}
