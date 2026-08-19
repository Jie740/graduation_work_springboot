package com.clj.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PgVector EmbeddingStore 配置
 * 用于向量数据的持久化存储和检索
 */
@Configuration
@RequiredArgsConstructor
public class PgVectorEmbeddingStoreConfig {
    private final PgVectorProperties properties;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(properties.getHost())
                .port(properties.getPort())
                .database(properties.getDatabase())
                .user(properties.getUser())
                .password(properties.getPassword())
                .table(properties.getTable())
                .dimension(properties.getDimension())
                .useIndex(properties.getUseIndex())
                .indexListSize(properties.getIndexListSize())
                .build();
    }


}
