package com.blog.dto.response;

import lombok.Data;

@Data
public class CategoryStatDTO {
    private Long id;
    private String name;
    private Integer count;
    private String color;
}
