package com.blog.repository;

import com.blog.entity.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 友情链接数据访问层
 */
@Repository
public interface FriendLinkRepository extends JpaRepository<FriendLink, Long> {

    /**
     * 查询所有友情链接并按排序字段排序
     */
    List<FriendLink> findAllByOrderBySortOrderAsc();
}