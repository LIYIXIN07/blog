package com.blog.repository;

import com.blog.entity.ScheduledTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {

    Page<ScheduledTask> findAllByOrderByIdAsc(Pageable pageable);
}
