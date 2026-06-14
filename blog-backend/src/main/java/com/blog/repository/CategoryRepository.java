package com.blog.repository;

import com.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分类数据访问层
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 根据名称查询分类
     */
    Optional<Category> findByName(String name);

    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询所有分类并按排序字段排序
     */
    List<Category> findAllByOrderBySortOrderAsc();

    /**
     * 查询分类及其文章数量
     */
    @Query("SELECT c, COUNT(a) FROM Category c LEFT JOIN Article a ON a.category = c AND a.status = 1 GROUP BY c")
    List<Object[]> findCategoryWithArticleCount();
}