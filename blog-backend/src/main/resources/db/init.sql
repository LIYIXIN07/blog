-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    status INT DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(20),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 文章表
CREATE TABLE IF NOT EXISTS article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    cover_image VARCHAR(2048),
    category_id BIGINT,
    author VARCHAR(50),
    view_count INT DEFAULT 0,
    status INT DEFAULT 0 COMMENT '0:草稿, 1:已发布',
    is_top TINYINT(1) DEFAULT 0,
    is_recommend TINYINT(1) DEFAULT 0,
    password VARCHAR(100) DEFAULT NULL,
    appreciation TINYINT(1) DEFAULT 0,
    comment_enabled TINYINT(1) DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS article_tag (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES article(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

-- 系统设置表
CREATE TABLE IF NOT EXISTS settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    site_name VARCHAR(100),
    site_description VARCHAR(255),
    author_name VARCHAR(50),
    author_avatar VARCHAR(255),
    author_bio VARCHAR(500),
    github VARCHAR(255),
    telegram VARCHAR(255),
    twitter VARCHAR(255),
    gitee VARCHAR(255),
    email VARCHAR(100),
    icp VARCHAR(100),
    site_start_date DATE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 友情链接表
CREATE TABLE IF NOT EXISTS friend_link (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 访客记录表
CREATE TABLE IF NOT EXISTS visit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    visitor_uuid VARCHAR(64) NOT NULL,
    ip VARCHAR(64),
    ip_source VARCHAR(255),
    os VARCHAR(100),
    browser VARCHAR(100),
    pv INT DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 图床配置表
CREATE TABLE IF NOT EXISTS image_bed_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(20) NOT NULL UNIQUE,
    enabled TINYINT(1) DEFAULT 0,
    config_json TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 定时任务表
CREATE TABLE IF NOT EXISTS scheduled_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    job_key VARCHAR(100) NOT NULL,
    bean_name VARCHAR(100),
    method_name VARCHAR(100),
    params VARCHAR(255),
    cron_expression VARCHAR(50),
    status INT DEFAULT 0,
    remark VARCHAR(255),
    last_run_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 登录日志
CREATE TABLE IF NOT EXISTS login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    ip VARCHAR(64),
    ip_source VARCHAR(255),
    os VARCHAR(100),
    browser VARCHAR(100),
    status INT,
    message VARCHAR(255),
    created_at DATETIME NOT NULL
);

-- 操作日志
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    module VARCHAR(50),
    description VARCHAR(255),
    method VARCHAR(10),
    uri VARCHAR(255),
    ip VARCHAR(64),
    params TEXT,
    created_at DATETIME NOT NULL
);

-- 异常日志
CREATE TABLE IF NOT EXISTS exception_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    uri VARCHAR(255),
    method VARCHAR(10),
    exception_name VARCHAR(100),
    message VARCHAR(500),
    stack_trace TEXT,
    ip VARCHAR(64),
    created_at DATETIME NOT NULL
);

-- 访问日志
CREATE TABLE IF NOT EXISTS access_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    visitor_uuid VARCHAR(64),
    uri VARCHAR(255),
    ip VARCHAR(64),
    ip_source VARCHAR(255),
    os VARCHAR(100),
    browser VARCHAR(100),
    created_at DATETIME NOT NULL
);

-- 任务日志
CREATE TABLE IF NOT EXISTS task_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(100),
    status INT,
    message VARCHAR(500),
    duration_ms BIGINT,
    created_at DATETIME NOT NULL
);

-- 动态表
CREATE TABLE IF NOT EXISTS moment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    likes INT DEFAULT 0,
    published TINYINT(1) DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(50),
    email VARCHAR(100),
    content VARCHAR(500) NOT NULL,
    avatar VARCHAR(255),
    website VARCHAR(255),
    ip VARCHAR(64),
    ip_source VARCHAR(255),
    published TINYINT(1) DEFAULT 1,
    admin_comment TINYINT(1) DEFAULT 0,
    page_type INT DEFAULT 0 COMMENT '0文章 1关于我 2友人帐',
    notice TINYINT(1) DEFAULT 0,
    parent_id BIGINT,
    article_id BIGINT,
    qq VARCHAR(20),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 关于我页面
CREATE TABLE IF NOT EXISTS about_page (
    id BIGINT PRIMARY KEY,
    title VARCHAR(200),
    music_id VARCHAR(50),
    content TEXT,
    comment_enabled TINYINT(1) DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

INSERT INTO about_page (id, title, music_id, content, comment_enabled, created_at) VALUES
(1, '关于我', '1473552803', '', 1, NOW())
ON DUPLICATE KEY UPDATE id = id;

-- 插入默认管理员用户（密码：admin123，已加密）
INSERT INTO user (username, password, nickname, avatar, role, status, created_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '/images/author-avatar.png', 'ADMIN', 1, NOW());

-- 插入默认分类
INSERT INTO category (name, description, sort_order, created_at) VALUES
('工作日常', '日常工作记录与分享', 1, NOW()),
('咖啡日记', '咖啡生活与品鉴记录', 2, NOW()),
('影视分享', '电影电视剧推荐与观感', 3, NOW()),
('心情随写', '随想随笔与心情记录', 4, NOW()),
('学习笔记', '学习过程中的笔记与总结', 5, NOW())
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    sort_order = VALUES(sort_order);

-- 插入默认标签（完整列表由 TagInitService 启动时补全）
INSERT INTO tag (name, color, created_at) VALUES
('Java', '#db2828', NOW()),
('Spring Boot', '#2185d0', NOW()),
('Redis', '#db2828', NOW()),
('Vue', '#21ba45', NOW()),
('算法', '#1b1c1d', NOW()),
('数据结构', '#1b1c1d', NOW()),
('GitHub', '#767676', NOW()),
('心情随写', '#6435c9', NOW())
ON DUPLICATE KEY UPDATE color = VALUES(color);

-- 插入默认系统设置
INSERT INTO settings (id, site_name, site_description, author_name, author_avatar, author_bio, github, telegram, twitter, gitee, created_at) VALUES
(1, '个人博客', '记录技术成长，分享学习心得', 'DollMeowOnly', '/images/author-avatar.png', '热爱技术，乐于分享',
 'https://github.com/telF4', 'https://t.me/ghxjsksnhdjjdj', 'https://x.com/jien386618', 'https://gitee.com/DollMeowOnly', NOW());