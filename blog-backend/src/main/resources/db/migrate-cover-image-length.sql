-- 扩大文章首图 URL 字段长度（已有库执行一次即可）
USE blog;

ALTER TABLE article MODIFY COLUMN cover_image VARCHAR(2048);
