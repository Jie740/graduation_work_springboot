package com.clj.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.ai.domain.AiKnowledgeBase;
import com.clj.ai.dto.AiKnowledgeBaseDto;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author ajie
* @description 针对表【ai_knowledge_base(AI知识库表)】的数据库操作Service
* @createDate 2026-08-18 18:05:50
*/
public interface AiKnowledgeBaseService extends IService<AiKnowledgeBase> {

    /**
     * 新增知识库
     *
     * @param knowledgeBaseDto 知识库新增请求（名称、描述、状态）
     */
    void add(AiKnowledgeBaseDto knowledgeBaseDto);

    /**
     * 删除知识库（逻辑删除）
     *
     * @param id 知识库ID
     */
    void delete(Long id);

    /**
     * 更新知识库
     *
     * @param knowledgeBase 知识库信息
     */
    void updateKnowledgeBase(AiKnowledgeBase knowledgeBase);

    /**
     * 根据ID查询知识库
     *
     * @param id 知识库ID
     * @return 知识库信息
     */
    AiKnowledgeBase getKnowledgeBaseById(Long id);

    /**
     * 分页查询知识库（支持按名称模糊搜索）
     *
     * @param keyword  名称关键字（可为空）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<AiKnowledgeBase> getKnowledgeBasesByPage(String keyword, Integer pageNum, Integer pageSize);
}
