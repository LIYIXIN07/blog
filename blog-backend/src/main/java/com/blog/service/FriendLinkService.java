package com.blog.service;

import com.blog.config.CacheNames;
import com.blog.dto.request.FriendLinkRequest;
import com.blog.entity.FriendLink;
import com.blog.repository.FriendLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendLinkService {

    private final FriendLinkRepository friendLinkRepository;

    @Cacheable(value = CacheNames.FRIEND_LINKS)
    public List<FriendLink> getAllFriendLinks() {
        return friendLinkRepository.findAllByOrderBySortOrderAsc();
    }

    @CacheEvict(value = CacheNames.FRIEND_LINKS, allEntries = true)
    @Transactional
    public Long createFriendLink(FriendLinkRequest request) {
        FriendLink link = new FriendLink();
        link.setName(request.getName());
        link.setUrl(request.getUrl());
        link.setAvatar(request.getAvatar());
        link.setDescription(request.getDescription());
        link.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        return friendLinkRepository.save(link).getId();
    }

    @CacheEvict(value = CacheNames.FRIEND_LINKS, allEntries = true)
    @Transactional
    public void updateFriendLink(FriendLinkRequest request) {
        FriendLink link = friendLinkRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("友情链接不存在"));

        link.setName(request.getName());
        link.setUrl(request.getUrl());
        link.setAvatar(request.getAvatar());
        link.setDescription(request.getDescription());
        link.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        friendLinkRepository.save(link);
    }

    @CacheEvict(value = CacheNames.FRIEND_LINKS, allEntries = true)
    @Transactional
    public void deleteFriendLink(Long id) {
        friendLinkRepository.deleteById(id);
    }
}
