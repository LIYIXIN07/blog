package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.request.CommentSubmitRequest;
import com.blog.dto.response.CommentDTO;
import com.blog.dto.response.PublicCommentDTO;
import com.blog.dto.response.PublicCommentPageDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.util.CommentAvatarUtils;
import com.blog.util.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final AboutService aboutService;

    public static final int PAGE_TYPE_ARTICLE = 0;
    public static final int PAGE_TYPE_ABOUT = 1;
    public static final int PAGE_TYPE_FRIENDS = 2;

    public PageResult<CommentDTO> getComments(Integer pageNum, Integer pageSize,
                                              Integer pageType, Long articleId) {
        Page<Comment> page = commentRepository.findRootComments(
                articleId, pageType, PageRequest.of(pageNum - 1, pageSize));

        Map<Long, String> articleTitles = loadArticleTitles(page.getContent());
        List<CommentDTO> list = page.getContent().stream()
                .map(comment -> toDTO(comment, articleTitles))
                .peek(this::attachReplies)
                .toList();

        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    public PublicCommentPageDTO getPublicComments(Integer pageNum, Integer pageSize,
                                                  Integer pageType, Long articleId) {
        assertCommentOpen(pageType, articleId);

        long allComment = commentRepository.countRootComments(articleId, pageType);
        long openComment = commentRepository.countPublishedRootComments(articleId, pageType);

        Page<Comment> page = commentRepository.findPublishedRootComments(
                articleId, pageType, PageRequest.of(pageNum - 1, pageSize));

        List<PublicCommentDTO> list = page.getContent().stream()
                .map(this::toPublicDTO)
                .peek(this::attachPublicReplies)
                .toList();

        PublicCommentPageDTO result = new PublicCommentPageDTO();
        result.setAllComment(allComment);
        result.setCloseComment(Math.max(allComment - openComment, 0));
        result.setComments(new PageResult<>(list, page.getTotalElements(), pageNum, pageSize));
        return result;
    }

    @Transactional
    public void createPublicComment(CommentSubmitRequest request, String ip) {
        assertCommentOpen(request.getPageType(), request.getArticleId());

        Comment comment = new Comment();
        comment.setContent(request.getContent().trim());
        comment.setNickname(request.getNickname().trim());
        comment.setEmail(request.getEmail().trim());
        comment.setWebsite(normalizeWebsite(request.getWebsite()));
        comment.setNotice(Boolean.TRUE.equals(request.getNotice()));
        comment.setPageType(request.getPageType());
        comment.setArticleId(request.getPageType() == PAGE_TYPE_ARTICLE ? request.getArticleId() : null);
        comment.setPublished(true);
        comment.setAdminComment(false);
        comment.setIp(ip);
        comment.setIpSource(IpUtils.resolveIpSource(ip));
        comment.setAvatar(CommentAvatarUtils.resolveAvatar(comment.getEmail(), comment.getQq(), null));

        Long parentId = request.getParentId();
        if (parentId != null && parentId > 0) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException("回复的评论不存在"));
            if (!parent.getPageType().equals(request.getPageType())) {
                throw new BusinessException("评论页面不匹配");
            }
            comment.setParentId(parentId);
        }

        commentRepository.save(comment);
    }

    @Transactional
    public void updatePublished(Long id, Boolean published) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("评论不存在"));
        comment.setPublished(Boolean.TRUE.equals(published));
        commentRepository.save(comment);
    }

    @Transactional
    public void updateNotice(Long id, Boolean notice) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("评论不存在"));
        comment.setNotice(Boolean.TRUE.equals(notice));
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(CommentRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("评论ID不能为空");
        }
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("评论不存在"));
        comment.setNickname(request.getNickname());
        comment.setEmail(request.getEmail());
        comment.setContent(request.getContent());
        comment.setAvatar(request.getAvatar());
        comment.setWebsite(request.getWebsite());
        if (request.getIp() != null) {
            comment.setIp(request.getIp());
            comment.setIpSource(IpUtils.resolveIpSource(request.getIp()));
        }
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("评论不存在"));
        deleteWithReplies(comment.getId());
    }

    private void deleteWithReplies(Long id) {
        List<Comment> replies = commentRepository.findByParentId(id);
        for (Comment reply : replies) {
            deleteWithReplies(reply.getId());
        }
        commentRepository.deleteById(id);
    }

    private void attachReplies(CommentDTO dto) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(dto.getId());
        if (replies.isEmpty()) {
            return;
        }
        Map<Long, String> titles = loadArticleTitles(replies);
        dto.setReplies(replies.stream().map(c -> toDTO(c, titles)).toList());
    }

    private Map<Long, String> loadArticleTitles(List<Comment> comments) {
        List<Long> articleIds = comments.stream()
                .map(Comment::getArticleId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (articleIds.isEmpty()) {
            return Map.of();
        }
        return articleRepository.findAllById(articleIds).stream()
                .collect(Collectors.toMap(Article::getId, Article::getTitle));
    }

    private CommentDTO toDTO(Comment comment, Map<Long, String> articleTitles) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setNickname(comment.getNickname());
        dto.setEmail(comment.getEmail());
        dto.setContent(comment.getContent());
        dto.setAvatar(comment.getAvatar());
        dto.setWebsite(comment.getWebsite());
        dto.setIp(comment.getIp());
        dto.setIpSource(comment.getIpSource() != null ? comment.getIpSource() : IpUtils.resolveIpSource(comment.getIp()));
        dto.setPublished(comment.getPublished());
        dto.setAdminComment(comment.getAdminComment());
        dto.setPageType(comment.getPageType());
        dto.setNotice(comment.getNotice());
        dto.setParentId(comment.getParentId());
        dto.setArticleId(comment.getArticleId());
        dto.setQq(comment.getQq());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getArticleId() != null) {
            dto.setArticleTitle(articleTitles.get(comment.getArticleId()));
        }
        return dto;
    }

    private void assertCommentOpen(Integer pageType, Long articleId) {
        if (pageType == null) {
            throw new BusinessException("页面类型不能为空");
        }
        if (pageType == PAGE_TYPE_ABOUT && !aboutService.isCommentEnabled()) {
            throw new BusinessException("评论已关闭");
        }
        if (pageType == PAGE_TYPE_ARTICLE && articleId != null) {
            articleRepository.findById(articleId)
                    .orElseThrow(() -> new BusinessException("文章不存在"));
        }
    }

    private String normalizeWebsite(String website) {
        if (website == null || website.isBlank()) {
            return null;
        }
        String value = website.trim();
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }
        return value;
    }

    private PublicCommentDTO toPublicDTO(Comment comment) {
        PublicCommentDTO dto = new PublicCommentDTO();
        dto.setId(comment.getId());
        dto.setNickname(comment.getNickname());
        dto.setContent(comment.getContent());
        dto.setWebsite(comment.getWebsite());
        dto.setAdminComment(comment.getAdminComment());
        dto.setParentId(comment.getParentId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAvatar(CommentAvatarUtils.resolveAvatar(comment.getEmail(), comment.getQq(), comment.getAvatar()));
        return dto;
    }

    private void attachPublicReplies(PublicCommentDTO dto) {
        List<Comment> replies = commentRepository.findByParentIdAndPublishedTrueOrderByCreatedAtAsc(dto.getId());
        if (replies.isEmpty()) {
            return;
        }
        Comment parent = commentRepository.findById(dto.getId()).orElse(null);
        dto.setReplies(replies.stream().map(reply -> {
            PublicCommentDTO item = toPublicDTO(reply);
            if (reply.getParentId() != null && reply.getParentId() > 0 && parent != null) {
                item.setParentNickname(parent.getNickname());
            }
            return item;
        }).toList());
    }
}
