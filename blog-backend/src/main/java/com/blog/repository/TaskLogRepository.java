package com.blog.repository;

import com.blog.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    @Query("""
            SELECT l FROM TaskLog l
            WHERE (:startTime IS NULL OR l.createdAt >= :startTime)
              AND (:endTime IS NULL OR l.createdAt <= :endTime)
            ORDER BY l.createdAt DESC
            """)
    Page<TaskLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  Pageable pageable);
}
