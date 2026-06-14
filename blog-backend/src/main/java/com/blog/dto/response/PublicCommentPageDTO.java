package com.blog.dto.response;

import com.blog.common.result.PageResult;
import lombok.Data;

@Data
public class PublicCommentPageDTO {

    private Long allComment;
    private Long closeComment;
    private PageResult<PublicCommentDTO> comments;
}
