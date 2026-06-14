package com.blog.repository;

import com.blog.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 系统设置数据访问层
 */
@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {

}