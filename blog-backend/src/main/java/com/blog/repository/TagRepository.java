package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问层
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据名称查询标签
     */
    Optional<Tag> findByName(String name);

    /**
     * 检查标签名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询标签及其文章数量
     */
    @Query("SELECT t, COUNT(a) FROM Tag t LEFT JOIN t.articles a ON a.status = 1 GROUP BY t")
    List<Object[]> findTagWithArticleCount();
}