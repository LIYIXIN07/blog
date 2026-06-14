package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.dto.request.MomentRequest;
import com.blog.dto.response.MomentDTO;
import com.blog.entity.Moment;
import com.blog.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;

    public PageResult<MomentDTO> getMoments(Integer pageNum, Integer pageSize, boolean publishedOnly) {
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Moment> page = publishedOnly
                ? momentRepository.findByPublishedTrueOrderByCreatedAtDesc(pageable)
                : momentRepository.findAllByOrderByCreatedAtDesc(pageable);
        return new PageResult<>(
                page.getContent().stream().map(this::toDTO).toList(),
                page.getTotalElements(),
                pageNum,
                pageSize
        );
    }

    public MomentDTO getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("动态不存在"));
        return toDTO(moment);
    }

    @Transactional
    public Long createMoment(MomentRequest request) {
        Moment moment = new Moment();
        applyRequest(moment, request);
        if (moment.getCreatedAt() == null) {
            moment.setCreatedAt(LocalDateTime.now());
        }
        return momentRepository.save(moment).getId();
    }

    @Transactional
    public void updateMoment(MomentRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("动态ID不能为空");
        }
        Moment moment = momentRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("动态不存在"));
        applyRequest(moment, request);
        momentRepository.save(moment);
    }

    @Transactional
    public void updatePublished(Long id, Boolean published) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("动态不存在"));
        moment.setPublished(Boolean.TRUE.equals(published));
        momentRepository.save(moment);
    }

    @Transactional
    public void deleteMoment(Long id) {
        if (!momentRepository.existsById(id)) {
            throw new BusinessException("动态不存在");
        }
        momentRepository.deleteById(id);
    }

    private void applyRequest(Moment moment, MomentRequest request) {
        moment.setContent(request.getContent());
        if (request.getLikes() != null) {
            moment.setLikes(request.getLikes());
        } else if (moment.getLikes() == null) {
            moment.setLikes(0);
        }
        if (request.getPublished() != null) {
            moment.setPublished(request.getPublished());
        }
        if (request.getCreatedAt() != null) {
            moment.setCreatedAt(request.getCreatedAt());
        }
    }

    private MomentDTO toDTO(Moment moment) {
        MomentDTO dto = new MomentDTO();
        dto.setId(moment.getId());
        dto.setContent(moment.getContent());
        dto.setLikes(moment.getLikes());
        dto.setPublished(moment.getPublished());
        dto.setCreatedAt(moment.getCreatedAt());
        dto.setUpdatedAt(moment.getUpdatedAt());
        return dto;
    }
}
