package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.ai.domain.AiKnowledgeBase;
import com.clj.ai.dto.AiKnowledgeBaseDto;
import com.clj.ai.service.AiKnowledgeBaseService;
import com.clj.ai.mapper.AiKnowledgeBaseMapper;
import com.clj.common.exception.BusinessException;
import com.clj.security.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @author ajie
* @description 针对表【ai_knowledge_base(AI知识库表)】的数据库操作Service实现
* @createDate 2026-08-18 18:05:50
*/
@Service
public class AiKnowledgeBaseServiceImpl extends ServiceImpl<AiKnowledgeBaseMapper, AiKnowledgeBase>
    implements AiKnowledgeBaseService{

    @Override
    public void add(AiKnowledgeBaseDto knowledgeBaseDto) {
        if (!StringUtils.hasText(knowledgeBaseDto.getName())) {
            throw new BusinessException("知识库名称不能为空");
        }

        Long userId = SecurityUtil.getUserId();
        Date now = new Date();

        AiKnowledgeBase knowledgeBase = new AiKnowledgeBase();
        knowledgeBase.setName(knowledgeBaseDto.getName());
        knowledgeBase.setDescription(knowledgeBaseDto.getDescription());
        knowledgeBase.setStatus(knowledgeBaseDto.getStatus() != null ? knowledgeBaseDto.getStatus() : 1);
        knowledgeBase.setDocumentCount(0);
        knowledgeBase.setIsDeleted(false);
        knowledgeBase.setCrtim(now);
        knowledgeBase.setCruid(userId);
        knowledgeBase.setUptim(now);
        knowledgeBase.setUpuid(userId);

        this.save(knowledgeBase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AiKnowledgeBase knowledgeBase = this.getById(id);
        if (knowledgeBase == null || Boolean.TRUE.equals(knowledgeBase.getIsDeleted())) {
            throw new BusinessException("知识库不存在");
        }

        Long userId = SecurityUtil.getUserId();
        Date now = new Date();

        LambdaUpdateWrapper<AiKnowledgeBase> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiKnowledgeBase::getId, id)
                .set(AiKnowledgeBase::getIsDeleted, true)
                .set(AiKnowledgeBase::getDeletedAt, now)
                .set(AiKnowledgeBase::getDeletedBy, userId);
        this.update(updateWrapper);
    }

    @Override
    public void updateKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase.getId() == null) {
            throw new BusinessException("知识库ID不能为空");
        }

        AiKnowledgeBase existing = this.getById(knowledgeBase.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getIsDeleted())) {
            throw new BusinessException("知识库不存在");
        }

        Long userId = SecurityUtil.getUserId();
        knowledgeBase.setUptim(new Date());
        knowledgeBase.setUpuid(userId);
        // 审计字段与逻辑删除字段不允许通过更新接口修改
        knowledgeBase.setDocumentCount(null);
        knowledgeBase.setCrtim(null);
        knowledgeBase.setCruid(null);
        knowledgeBase.setIsDeleted(null);
        knowledgeBase.setDeletedAt(null);
        knowledgeBase.setDeletedBy(null);

        this.updateById(knowledgeBase);
    }

    @Override
    public AiKnowledgeBase getKnowledgeBaseById(Long id) {
        AiKnowledgeBase knowledgeBase = this.getById(id);
        if (knowledgeBase == null || Boolean.TRUE.equals(knowledgeBase.getIsDeleted())) {
            throw new BusinessException("知识库不存在");
        }
        return knowledgeBase;
    }

    @Override
    public Page<AiKnowledgeBase> getKnowledgeBasesByPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<AiKnowledgeBase> page = new Page<>(pageNum, pageSize);
        return this.lambdaQuery()
                .eq(AiKnowledgeBase::getIsDeleted, false)
                .like(StringUtils.hasText(keyword), AiKnowledgeBase::getName, keyword)
                .orderByDesc(AiKnowledgeBase::getCrtim)
                .page(page);
    }
}
