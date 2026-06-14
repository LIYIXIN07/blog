package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文章数据访问层
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据状态查询文章列表
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据分类查询文章列表
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findByCategoryIdAndStatus(Long categoryId, Integer status, Pageable pageable);

    /**
     * 根据标签查询文章列表
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.id = :tagId AND a.status = :status")
    Page<Article> findByTagIdAndStatus(@Param("tagId") Long tagId, @Param("status") Integer status, Pageable pageable);

    /**
     * 根据标题模糊查询
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findByTitleContainingAndStatus(String title, Integer status, Pageable pageable);

    /**
     * 查询最新文章
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    @Query("SELECT a FROM Article a WHERE a.status = 1 ORDER BY a.createdAt DESC")
    List<Article> findLatestArticles(Pageable pageable);

    /**
     * 查询热门文章（按阅读量）
     */
    @EntityGraph(attributePaths = {"category", "tags"})
    @Query("SELECT a FROM Article a WHERE a.status = 1 ORDER BY a.viewCount DESC")
    List<Article> findHotArticles(Pageable pageable);

    /**
     * 根据ID查询文章（包含分类和标签）
     */
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category LEFT JOIN FETCH a.tags WHERE a.id = :id")
    Optional<Article> findByIdWithCategoryAndTags(@Param("id") Long id);

    /**
     * 统计文章总数
     */
    long countByStatus(Integer status);

    long countByCategoryIdAndStatus(Long categoryId, Integer status);

    /**
     * 统计总阅读量
     */
    @Query("SELECT SUM(a.viewCount) FROM Article a")
    Long sumViewCount();

    @Query(value = """
            SELECT * FROM article
            WHERE status = 1
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<Article> findRandomArticles(@Param("limit") int limit);

    @Query(value = """
            SELECT YEAR(created_at) AS yearVal,
                   MONTH(created_at) AS monthVal,
                   COUNT(*) AS total
            FROM article
            WHERE status = 1
            GROUP BY YEAR(created_at), MONTH(created_at)
            ORDER BY yearVal DESC, monthVal DESC
            """, nativeQuery = true)
    List<Object[]> findArchiveStats();

    @EntityGraph(attributePaths = {"category", "tags"})
    @Query("""
            SELECT a FROM Article a
            WHERE a.status = :status
              AND YEAR(a.createdAt) = :year
              AND MONTH(a.createdAt) = :month
            """)
    Page<Article> findByYearMonthAndStatus(@Param("year") Integer year,
                                           @Param("month") Integer month,
                                           @Param("status") Integer status,
                                           Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findByTitleContaining(String title, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findByCategoryId(Long categoryId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Article> findAll(Pageable pageable);

    /**
     * 查询全部已发布文章（用于 sitemap）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 ORDER BY a.updatedAt DESC")
    List<Article> findAllPublishedForSitemap();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id AND a.status = 1")
    int incrementViewCount(@Param("id") Long id);
}