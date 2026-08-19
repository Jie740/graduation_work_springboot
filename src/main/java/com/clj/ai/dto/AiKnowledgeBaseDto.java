package com.clj.ai.dto;

import lombok.Data;

/**
 * AI知识库新增请求 DTO
 */
@Data
public class AiKnowledgeBaseDto {

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;
}
