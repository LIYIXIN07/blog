package com.blog.repository;

import com.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            WHERE (:articleId IS NULL OR c.articleId = :articleId)
              AND (:pageType IS NULL OR c.pageType = :pageType)
              AND (c.parentId IS NULL OR c.parentId = 0)
            ORDER BY c.createdAt DESC
            """)
    Page<Comment> findRootComments(@Param("articleId") Long articleId,
                                   @Param("pageType") Integer pageType,
                                   Pageable pageable);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    List<Comment> findByParentId(Long parentId);

    @Query("""
            SELECT COUNT(c) FROM Comment c
            WHERE (:articleId IS NULL OR c.articleId = :articleId)
              AND c.pageType = :pageType
              AND (c.parentId IS NULL OR c.parentId = 0)
            """)
    long countRootComments(@Param("articleId") Long articleId, @Param("pageType") Integer pageType);

    @Query("""
            SELECT COUNT(c) FROM Comment c
            WHERE (:articleId IS NULL OR c.articleId = :articleId)
              AND c.pageType = :pageType
              AND (c.parentId IS NULL OR c.parentId = 0)
              AND c.published = true
            """)
    long countPublishedRootComments(@Param("articleId") Long articleId, @Param("pageType") Integer pageType);

    @Query("""
            SELECT c FROM Comment c
            WHERE (:articleId IS NULL OR c.articleId = :articleId)
              AND c.pageType = :pageType
              AND (c.parentId IS NULL OR c.parentId = 0)
              AND c.published = true
            ORDER BY c.createdAt DESC
            """)
    Page<Comment> findPublishedRootComments(@Param("articleId") Long articleId,
                                            @Param("pageType") Integer pageType,
                                            Pageable pageable);

    List<Comment> findByParentIdAndPublishedTrueOrderByCreatedAtAsc(Long parentId);
}
