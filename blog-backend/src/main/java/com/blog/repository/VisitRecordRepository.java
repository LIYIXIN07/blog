package com.blog.repository;

import com.blog.entity.VisitRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    Optional<VisitRecord> findByVisitorUuid(String visitorUuid);

    @Query("""
            SELECT v FROM VisitRecord v
            WHERE (:startTime IS NULL OR v.updatedAt >= :startTime)
              AND (:endTime IS NULL OR v.updatedAt <= :endTime)
            ORDER BY v.updatedAt DESC
            """)
    Page<VisitRecord> findByVisitTimeRange(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           Pageable pageable);

    @Query(value = """
            SELECT DATE(updated_at) AS visitDate,
                   SUM(pv) AS pv,
                   COUNT(DISTINCT visitor_uuid) AS uv
            FROM visit_record
            WHERE updated_at >= :startTime
            GROUP BY DATE(updated_at)
            ORDER BY visitDate
            """, nativeQuery = true)
    List<Object[]> findTrafficStats(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COALESCE(SUM(v.pv), 0) FROM VisitRecord v")
    Long sumTotalPv();

    @Query("SELECT v.ip, v.ipSource FROM VisitRecord v")
    List<Object[]> findIpSources();

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE VisitRecord v
            SET v.pv = v.pv + 1,
                v.ip = :ip,
                v.ipSource = :ipSource,
                v.os = :os,
                v.browser = :browser
            WHERE v.visitorUuid = :visitorUuid
            """)
    int incrementPv(@Param("visitorUuid") String visitorUuid,
                    @Param("ip") String ip,
                    @Param("ipSource") String ipSource,
                    @Param("os") String os,
                    @Param("browser") String browser);
}
