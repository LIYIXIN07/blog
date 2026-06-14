package com.blog.repository;

import com.blog.entity.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    @Query("""
            SELECT l FROM AccessLog l
            WHERE (:visitorUuid IS NULL OR l.visitorUuid = :visitorUuid)
              AND (:startTime IS NULL OR l.createdAt >= :startTime)
              AND (:endTime IS NULL OR l.createdAt <= :endTime)
            ORDER BY l.createdAt DESC
            """)
    Page<AccessLog> findByTimeRange(@Param("visitorUuid") String visitorUuid,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    Pageable pageable);

    @Modifying
    @Query("DELETE FROM AccessLog l WHERE l.visitorUuid = :visitorUuid")
    void deleteByVisitorUuid(@Param("visitorUuid") String visitorUuid);

    @Query("SELECT COUNT(l) FROM AccessLog l WHERE l.createdAt >= :startTime")
    long countSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(DISTINCT l.visitorUuid) FROM AccessLog l WHERE l.createdAt >= :startTime")
    long countDistinctVisitorsSince(@Param("startTime") LocalDateTime startTime);
}
