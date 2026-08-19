package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.ai.domain.AiRagRetrieval;
import com.clj.ai.service.AiRagRetrievalService;
import com.clj.ai.mapper.AiRagRetrievalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RAG 检索溯源服务实现
 */
@Slf4j
@Service
public class AiRagRetrievalServiceImpl extends ServiceImpl<AiRagRetrievalMapper, AiRagRetrieval>
        implements AiRagRetrievalService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRetrievals(Long messageId, List<RetrievalInfo> retrievals) {
        if (retrievals == null || retrievals.isEmpty()) {
            return;
        }

        List<AiRagRetrieval> records = new ArrayList<>();
        Date now = new Date();

        for (RetrievalInfo info : retrievals) {
            AiRagRetrieval record = new AiRagRetrieval();
            record.setMessageId(messageId);
            record.setDocumentId(info.documentId());
            record.setChunkId(info.chunkId());
            record.setScore(info.score());
            record.setCreatedTime(now);
            records.add(record);
        }

        this.saveBatch(records);
        log.info("已保存 {} 条 RAG 检索溯源记录, messageId={}", records.size(), messageId);
    }
}




