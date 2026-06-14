/**
 * 批量写入默认标签（UTF-8，避免 PowerShell 乱码）
 * 用法: node scripts/seed-tags.mjs
 */
const API = 'http://localhost:8080/api'

const TAGS = [
  ['密码学', '#a5673f'], ['RabbitMQ', '#f2711c'], ['消息队列', '#2185d0'],
  ['布隆过滤器', '#6435c9'], ['定时任务', '#00b5ad'], ['Redis', '#db2828'],
  ['Spring Boot', '#2185d0'], ['算法', '#1b1c1d'], ['数据结构', '#1b1c1d'],
  ['Sublime Text 3', '#f2711c'], ['Java', '#db2828'], ['Python 3', '#21ba45'],
  ['Vue Router', '#21ba45'], ['Vue', '#21ba45'], ['心情随写', '#6435c9'],
  ['DFS', '#f2711c'], ['BFS', '#db2828'], ['图像识别', '#00b5ad'],
  ['连连看', '#f2711c'], ['Typora', '#21ba45'], ['PicGo', '#1b1c1d'],
  ['GitHub', '#767676'], ['jsDelivr', '#a5673f'], ['Swing', '#e03997'],
  ['五子棋', '#1b1c1d'], ['Python-Flask', '#21ba45'], ['Nginx', '#e03997'],
  ['归并排序', '#6435c9'], ['败者树', '#21ba45'], ['外部排序', '#2185d0'],
  ['跳表SkipList', '#db2828'], ['KMP算法', '#1b1c1d'], ['字符串', '#a5673f'],
  ['Spring Cloud', '#6db33f'], ['Spring MVC', '#6db33f'], ['MyBatis', '#f89820'],
  ['MyBatis-Plus', '#f89820'], ['Maven', '#e37400'], ['Gradle', '#02303a'],
  ['Tomcat', '#f89820'], ['Netty', '#1b1c1d'], ['Kafka', '#1b1c1d'],
  ['Elasticsearch', '#005571'], ['MongoDB', '#13aa52'], ['MySQL', '#00758f'],
  ['PostgreSQL', '#336791'], ['Docker', '#2496ed'], ['Kubernetes', '#326ce5'],
  ['Linux', '#fcc624'], ['Shell', '#4eaa25'], ['Nacos', '#00b4ff'],
  ['Sentinel', '#00a8e8'], ['Seata', '#2c3e50'], ['Dubbo', '#0052cc'],
  ['Zookeeper', '#ffc107'], ['MinIO', '#c72c48'], ['OAuth2', '#eb5424'],
  ['JWT', '#000000'], ['Spring Security', '#6db33f'], ['Sa-Token', '#009688'],
  ['WebSocket', '#795548'], ['gRPC', '#244c5a'], ['RESTful', '#607d8b'],
  ['微服务', '#3f51b5'], ['高并发', '#e91e63'], ['分布式', '#673ab7'],
  ['性能优化', '#ff5722'], ['JavaScript', '#f7df1e'], ['TypeScript', '#3178c6'],
  ['Vue.js', '#42b883'], ['React', '#61dafb'], ['Node.js', '#339933'],
  ['Webpack', '#8dd6f9'], ['Vite', '#646cff'], ['Pinia', '#ffd859'],
  ['Element Plus', '#409eff'], ['Tailwind CSS', '#06b6d4'], ['Sass', '#cc6699'],
  ['HTML5', '#e34f26'], ['CSS3', '#1572b6'], ['Axios', '#5a29e4'],
  ['ECharts', '#aa344d'], ['前端开发', '#61dafb'], ['响应式布局', '#009688'],
  ['PWA', '#5a0fc8'], ['Python', '#3776ab'], ['Django', '#092e20'],
  ['FastAPI', '#009688'], ['NumPy', '#013243'], ['Pandas', '#150458'],
  ['机器学习', '#ff6f00'], ['深度学习', '#6200ea'], ['TensorFlow', '#ff6f00'],
  ['PyTorch', '#ee4c2c'], ['OpenCV', '#5c3ee8'], ['自然语言处理', '#00897b'],
  ['动态规划', '#1b1c1d'], ['贪心算法', '#795548'], ['回溯算法', '#5d4037'],
  ['分治算法', '#455a64'], ['快速排序', '#6d4c41'], ['堆排序', '#4e342e'],
  ['冒泡排序', '#8d6e63'], ['二叉树', '#37474f'], ['链表', '#546e7a'],
  ['栈', '#607d8b'], ['队列', '#78909c'], ['图论', '#263238'],
  ['哈希表', '#3e2723'], ['LeetCode', '#ffa116'], ['剑指Offer', '#ff9800'],
  ['A*算法', '#0097a7'], ['Dijkstra', '#00838f'], ['并查集', '#00695c'],
  ['Trie树', '#004d40'], ['线段树', '#1b5e20'], ['树状数组', '#33691e'],
  ['Git', '#f05032'], ['IntelliJ IDEA', '#000000'], ['VS Code', '#007acc'],
  ['Postman', '#ff6c37'], ['Markdown', '#083fa1'], ['Vditor', '#4285f4'],
  ['npm', '#cb3837'], ['pnpm', '#f69220'], ['yarn', '#2c8ebb'],
  ['Chrome DevTools', '#4285f4'], ['Fiddler', '#0072c6'], ['Charles', '#333333'],
  ['设计模式', '#512da8'], ['计算机网络', '#1565c0'], ['操作系统', '#283593'],
  ['数据库', '#006064'], ['SQL', '#336791'], ['NoSQL', '#4db33d'],
  ['编译原理', '#4527a0'], ['软件工程', '#3949ab'], ['JUnit', '#25a162'],
  ['Mockito', '#7cb342'], ['JMeter', '#d32f2f'], ['CI/CD', '#ff4081'],
  ['Jenkins', '#d33833'], ['GitHub Actions', '#2088ff'], ['Prometheus', '#e6522c'],
  ['Grafana', '#f46800'], ['工作日常', '#ff9800'], ['咖啡日记', '#795548'],
  ['影视分享', '#9c27b0'], ['学习笔记', '#2196f3'], ['随笔', '#9e9e9e'],
  ['读书笔记', '#607d8b'], ['旅行', '#4caf50'], ['摄影', '#ff5722'],
  ['贪吃蛇', '#8bc34a'], ['俄罗斯方块', '#cddc39'], ['扫雷', '#ffeb3b'],
  ['2048', '#ffc107'], ['井字棋', '#ff9800'], ['仿B站', '#fb7299'],
  ['图书管理系统', '#3f51b5'], ['博客系统', '#48dbfb'], ['图床', '#00bcd4'],
  ['评论系统', '#009688']
]

async function main() {
  const loginRes = await fetch(`${API}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'admin', password: 'admin123' })
  })
  const loginJson = await loginRes.json()
  if (loginJson.code !== 200) {
    console.error('登录失败', loginJson)
    process.exit(1)
  }
  const token = loginJson.data.token
  const headers = {
    'Content-Type': 'application/json; charset=utf-8',
    Authorization: `Bearer ${token}`
  }

  const existingRes = await fetch(`${API}/tags`, { headers })
  const existingJson = await existingRes.json()
  const existingNames = new Set((existingJson.data || []).map((t) => t.name))

  let created = 0
  let skipped = 0
  for (const [name, color] of TAGS) {
    if (existingNames.has(name)) {
      skipped++
      continue
    }
    const res = await fetch(`${API}/tags`, {
      method: 'POST',
      headers,
      body: JSON.stringify({ name, color })
    })
    const json = await res.json()
    if (json.code === 200) {
      created++
      existingNames.add(name)
    } else {
      console.warn('失败:', name, json.message)
    }
  }

  const finalRes = await fetch(`${API}/tags`)
  const finalJson = await finalRes.json()
  console.log(`新增 ${created} 个，跳过 ${skipped} 个，当前共 ${finalJson.data?.length || 0} 个标签`)
}

main().catch((e) => {
  console.error(e)
  process.exit(1)
})
