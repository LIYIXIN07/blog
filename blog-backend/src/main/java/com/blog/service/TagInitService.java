package com.blog.service;

import com.blog.entity.Tag;
import com.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 默认标签初始化（对齐 NBlog 标签云，尽量丰富）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagInitService {

    private record DefaultTag(String name, String color) {
    }

    /** Semantic UI 风格配色 */
    private static final List<DefaultTag> DEFAULT_TAGS = List.of(
            // NBlog 标签云原标签
            new DefaultTag("密码学", "#a5673f"),
            new DefaultTag("RabbitMQ", "#f2711c"),
            new DefaultTag("消息队列", "#2185d0"),
            new DefaultTag("布隆过滤器", "#6435c9"),
            new DefaultTag("定时任务", "#00b5ad"),
            new DefaultTag("Redis", "#db2828"),
            new DefaultTag("Spring Boot", "#2185d0"),
            new DefaultTag("算法", "#1b1c1d"),
            new DefaultTag("数据结构", "#1b1c1d"),
            new DefaultTag("Sublime Text 3", "#f2711c"),
            new DefaultTag("Java", "#db2828"),
            new DefaultTag("Python 3", "#21ba45"),
            new DefaultTag("Vue Router", "#21ba45"),
            new DefaultTag("Vue", "#21ba45"),
            new DefaultTag("心情随写", "#6435c9"),
            new DefaultTag("DFS", "#f2711c"),
            new DefaultTag("BFS", "#db2828"),
            new DefaultTag("图像识别", "#00b5ad"),
            new DefaultTag("连连看", "#f2711c"),
            new DefaultTag("Typora", "#21ba45"),
            new DefaultTag("PicGo", "#1b1c1d"),
            new DefaultTag("GitHub", "#767676"),
            new DefaultTag("jsDelivr", "#a5673f"),
            new DefaultTag("Swing", "#e03997"),
            new DefaultTag("五子棋", "#1b1c1d"),
            new DefaultTag("Python-Flask", "#21ba45"),
            new DefaultTag("Nginx", "#e03997"),
            new DefaultTag("归并排序", "#6435c9"),
            new DefaultTag("败者树", "#21ba45"),
            new DefaultTag("外部排序", "#2185d0"),
            new DefaultTag("跳表SkipList", "#db2828"),
            new DefaultTag("KMP算法", "#1b1c1d"),
            new DefaultTag("字符串", "#a5673f"),
            // 后端 / 中间件
            new DefaultTag("Spring Cloud", "#6db33f"),
            new DefaultTag("Spring MVC", "#6db33f"),
            new DefaultTag("MyBatis", "#f89820"),
            new DefaultTag("MyBatis-Plus", "#f89820"),
            new DefaultTag("Maven", "#e37400"),
            new DefaultTag("Gradle", "#02303a"),
            new DefaultTag("Tomcat", "#f89820"),
            new DefaultTag("Netty", "#1b1c1d"),
            new DefaultTag("Kafka", "#1b1c1d"),
            new DefaultTag("Elasticsearch", "#005571"),
            new DefaultTag("MongoDB", "#13aa52"),
            new DefaultTag("MySQL", "#00758f"),
            new DefaultTag("PostgreSQL", "#336791"),
            new DefaultTag("Docker", "#2496ed"),
            new DefaultTag("Kubernetes", "#326ce5"),
            new DefaultTag("Linux", "#fcc624"),
            new DefaultTag("Shell", "#4eaa25"),
            new DefaultTag("Nacos", "#00b4ff"),
            new DefaultTag("Sentinel", "#00a8e8"),
            new DefaultTag("Seata", "#2c3e50"),
            new DefaultTag("Dubbo", "#0052cc"),
            new DefaultTag("Zookeeper", "#ffc107"),
            new DefaultTag("MinIO", "#c72c48"),
            new DefaultTag("OAuth2", "#eb5424"),
            new DefaultTag("JWT", "#000000"),
            new DefaultTag("Spring Security", "#6db33f"),
            new DefaultTag("Sa-Token", "#009688"),
            new DefaultTag("WebSocket", "#795548"),
            new DefaultTag("gRPC", "#244c5a"),
            new DefaultTag("RESTful", "#607d8b"),
            new DefaultTag("微服务", "#3f51b5"),
            new DefaultTag("高并发", "#e91e63"),
            new DefaultTag("分布式", "#673ab7"),
            new DefaultTag("性能优化", "#ff5722"),
            // 前端
            new DefaultTag("JavaScript", "#f7df1e"),
            new DefaultTag("TypeScript", "#3178c6"),
            new DefaultTag("Vue.js", "#42b883"),
            new DefaultTag("React", "#61dafb"),
            new DefaultTag("Node.js", "#339933"),
            new DefaultTag("Webpack", "#8dd6f9"),
            new DefaultTag("Vite", "#646cff"),
            new DefaultTag("Pinia", "#ffd859"),
            new DefaultTag("Element Plus", "#409eff"),
            new DefaultTag("Tailwind CSS", "#06b6d4"),
            new DefaultTag("Sass", "#cc6699"),
            new DefaultTag("HTML5", "#e34f26"),
            new DefaultTag("CSS3", "#1572b6"),
            new DefaultTag("Axios", "#5a29e4"),
            new DefaultTag("ECharts", "#aa344d"),
            new DefaultTag("前端开发", "#61dafb"),
            new DefaultTag("响应式布局", "#009688"),
            new DefaultTag("PWA", "#5a0fc8"),
            // Python / AI
            new DefaultTag("Python", "#3776ab"),
            new DefaultTag("Django", "#092e20"),
            new DefaultTag("FastAPI", "#009688"),
            new DefaultTag("NumPy", "#013243"),
            new DefaultTag("Pandas", "#150458"),
            new DefaultTag("机器学习", "#ff6f00"),
            new DefaultTag("深度学习", "#6200ea"),
            new DefaultTag("TensorFlow", "#ff6f00"),
            new DefaultTag("PyTorch", "#ee4c2c"),
            new DefaultTag("OpenCV", "#5c3ee8"),
            new DefaultTag("自然语言处理", "#00897b"),
            // 算法专题
            new DefaultTag("动态规划", "#1b1c1d"),
            new DefaultTag("贪心算法", "#795548"),
            new DefaultTag("回溯算法", "#5d4037"),
            new DefaultTag("分治算法", "#455a64"),
            new DefaultTag("快速排序", "#6d4c41"),
            new DefaultTag("堆排序", "#4e342e"),
            new DefaultTag("冒泡排序", "#8d6e63"),
            new DefaultTag("二叉树", "#37474f"),
            new DefaultTag("链表", "#546e7a"),
            new DefaultTag("栈", "#607d8b"),
            new DefaultTag("队列", "#78909c"),
            new DefaultTag("图论", "#263238"),
            new DefaultTag("哈希表", "#3e2723"),
            new DefaultTag("LeetCode", "#ffa116"),
            new DefaultTag("剑指Offer", "#ff9800"),
            new DefaultTag("A*算法", "#0097a7"),
            new DefaultTag("Dijkstra", "#00838f"),
            new DefaultTag("并查集", "#00695c"),
            new DefaultTag("Trie树", "#004d40"),
            new DefaultTag("线段树", "#1b5e20"),
            new DefaultTag("树状数组", "#33691e"),
            // 工具 / 效率
            new DefaultTag("Git", "#f05032"),
            new DefaultTag("IntelliJ IDEA", "#000000"),
            new DefaultTag("VS Code", "#007acc"),
            new DefaultTag("Postman", "#ff6c37"),
            new DefaultTag("Markdown", "#083fa1"),
            new DefaultTag("Vditor", "#4285f4"),
            new DefaultTag("npm", "#cb3837"),
            new DefaultTag("pnpm", "#f69220"),
            new DefaultTag("yarn", "#2c8ebb"),
            new DefaultTag("Chrome DevTools", "#4285f4"),
            new DefaultTag("Fiddler", "#0072c6"),
            new DefaultTag("Charles", "#333333"),
            // 计算机基础
            new DefaultTag("设计模式", "#512da8"),
            new DefaultTag("计算机网络", "#1565c0"),
            new DefaultTag("操作系统", "#283593"),
            new DefaultTag("数据库", "#006064"),
            new DefaultTag("SQL", "#336791"),
            new DefaultTag("NoSQL", "#4db33d"),
            new DefaultTag("编译原理", "#4527a0"),
            new DefaultTag("软件工程", "#3949ab"),
            // 测试 / 运维
            new DefaultTag("JUnit", "#25a162"),
            new DefaultTag("Mockito", "#7cb342"),
            new DefaultTag("JMeter", "#d32f2f"),
            new DefaultTag("CI/CD", "#ff4081"),
            new DefaultTag("Jenkins", "#d33833"),
            new DefaultTag("GitHub Actions", "#2088ff"),
            new DefaultTag("Prometheus", "#e6522c"),
            new DefaultTag("Grafana", "#f46800"),
            // 生活 / 博客
            new DefaultTag("工作日常", "#ff9800"),
            new DefaultTag("咖啡日记", "#795548"),
            new DefaultTag("影视分享", "#9c27b0"),
            new DefaultTag("学习笔记", "#2196f3"),
            new DefaultTag("随笔", "#9e9e9e"),
            new DefaultTag("读书笔记", "#607d8b"),
            new DefaultTag("旅行", "#4caf50"),
            new DefaultTag("摄影", "#ff5722"),
            // 小游戏 / 项目
            new DefaultTag("贪吃蛇", "#8bc34a"),
            new DefaultTag("俄罗斯方块", "#cddc39"),
            new DefaultTag("扫雷", "#ffeb3b"),
            new DefaultTag("2048", "#ffc107"),
            new DefaultTag("井字棋", "#ff9800"),
            new DefaultTag("仿B站", "#fb7299"),
            new DefaultTag("图书管理系统", "#3f51b5"),
            new DefaultTag("博客系统", "#48dbfb"),
            new DefaultTag("图床", "#00bcd4"),
            new DefaultTag("评论系统", "#009688")
    );

    private final TagRepository tagRepository;

    @Transactional
    public void ensureDefaultTags() {
        int created = 0;
        for (DefaultTag item : DEFAULT_TAGS) {
            if (tagRepository.findByName(item.name()).isPresent()) {
                continue;
            }
            Tag tag = new Tag();
            tag.setName(item.name());
            tag.setColor(item.color());
            tagRepository.save(tag);
            created++;
        }
        if (created > 0) {
            log.info("已初始化 {} 个默认标签", created);
        }
    }
}
