package com.clj.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.ai.domain.AiKnowledgeBase;
import com.clj.ai.dto.AiKnowledgeBaseDto;
import com.clj.ai.service.AiKnowledgeBaseService;
import com.clj.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI知识库管理控制器
 */
@RestController
@RequestMapping("/ai/knowledgeBase")
@RequiredArgsConstructor
public class AiKnowledgeBaseController {

    private final AiKnowledgeBaseService knowledgeBaseService;

    /**
     * 新增知识库
     *
     * @param knowledgeBaseDto 知识库新增请求（名称、描述、状态）
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody AiKnowledgeBaseDto knowledgeBaseDto) {
        knowledgeBaseService.add(knowledgeBaseDto);
        return Result.success();
    }

    /**
     * 删除知识库（逻辑删除）
     *
     * @param id 知识库ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        knowledgeBaseService.delete(id);
        return Result.success();
    }

    /**
     * 更新知识库
     *
     * @param knowledgeBase 知识库信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody AiKnowledgeBase knowledgeBase) {
        knowledgeBaseService.updateKnowledgeBase(knowledgeBase);
        return Result.success();
    }

    /**
     * 根据ID查询知识库
     *
     * @param id 知识库ID
     * @return 知识库信息
     */
    @GetMapping("/getById/{id}")
    public Result<AiKnowledgeBase> getById(@PathVariable("id") Long id) {
        return Result.success(knowledgeBaseService.getKnowledgeBaseById(id));
    }

    /**
     * 分页查询知识库
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/getByPage/{pageNum}/{pageSize}")
    public Result<Page<AiKnowledgeBase>> getByPage(@PathVariable("pageNum") Integer pageNum,
                                                   @PathVariable("pageSize") Integer pageSize) {
        return Result.success(knowledgeBaseService.getKnowledgeBasesByPage(null, pageNum, pageSize));
    }

    /**
     * 按名称关键字分页搜索知识库
     *
     * @param keyword  名称关键字
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @GetMapping("/searchByPage/{keyword}/{pageNum}/{pageSize}")
    public Result<Page<AiKnowledgeBase>> searchByPage(@PathVariable("keyword") String keyword,
                                                      @PathVariable("pageNum") Integer pageNum,
                                                      @PathVariable("pageSize") Integer pageSize) {
        return Result.success(knowledgeBaseService.getKnowledgeBasesByPage(keyword, pageNum, pageSize));
    }
}
