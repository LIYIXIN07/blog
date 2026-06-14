package com.blog.repository;

import com.blog.entity.ImageBedConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageBedConfigRepository extends JpaRepository<ImageBedConfig, Long> {

    Optional<ImageBedConfig> findByType(String type);
}
