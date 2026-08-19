package com.clj;

import com.clj.ai.dto.ChunkSimilarityDto;
import com.clj.ai.mapper.AiRagChunkMapper;
import com.clj.common.config.MybatisPlusConfig;
import com.clj.common.config.MysqlDataSourceConfig;
import com.clj.common.config.PostgresDataSourceConfig;
import com.clj.mapper.SysUserMapper;
import com.pgvector.PGvector;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySQL + PostgreSQL 双数据源测试
 * 验证：MySQL 业务 Mapper 正常、PostgreSQL RAG Mapper 正常、
 * VECTOR(1024) 写入与相似度查询、事务管理器分离、Mapper 互不扫描
 */
@SpringBootTest(classes = {DualDataSourceTest.class, MysqlDataSourceConfig.class,
        PostgresDataSourceConfig.class, MybatisPlusConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("dev")
@EnableAutoConfiguration
class DualDataSourceTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private AiRagChunkMapper aiRagChunkMapper;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    @Autowired
    @Qualifier("postgresDataSource")
    private DataSource postgresDataSource;

    @Autowired
    @Qualifier("mysqlSqlSessionTemplate")
    private SqlSessionTemplate mysqlSqlSessionTemplate;

    @Autowired
    @Qualifier("postgresSqlSessionTemplate")
    private SqlSessionTemplate postgresSqlSessionTemplate;

    @Autowired
    @Qualifier("transactionManager")
    private DataSourceTransactionManager mysqlTransactionManager;

    @Autowired
    @Qualifier("postgresTransactionManager")
    private DataSourceTransactionManager postgresTransactionManager;

    /**
     * 现有 MySQL CRUD 不受影响
     */
    @Test
    void mysqlBusinessMapperStillWorks() {
        Long count = sysUserMapper.selectCount(null);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    /**
     * PostgreSQL RAG Mapper：VECTOR(1024) 写入 + 相似度查询
     */
    @Test
    void postgresChunkInsertAndVectorSearch() {
        long documentId = 900000003L;
        float[] v1 = new float[1024];
        float[] v2 = new float[1024];
        for (int i = 0; i < 1024; i++) {
            v1[i] = i / 1024f;
        }
        v2[0] = 100f;

        AiRagChunk chunkA = buildChunk(documentId, 0, "双数据源测试分块 A", v1);
        AiRagChunk chunkB = buildChunk(documentId, 1, "双数据源测试分块 B", v2);
        try {
            assertThat(aiRagChunkMapper.insert(chunkA)).isEqualTo(1);
            assertThat(aiRagChunkMapper.insert(chunkB)).isEqualTo(1);
            assertThat(chunkA.getId()).isNotNull();

            // 读回 embedding，维度为 1024
            AiRagChunk readBack = aiRagChunkMapper.selectById(chunkA.getId());
            assertThat(readBack).isNotNull();
            assertThat(readBack.getEmbedding().toArray()).hasSize(1024);
            assertThat(readBack.getMetadata()).contains("dual-ds-test");

            // 相似度检索：与 v1 相同的分块应排第一且相似度接近 1
            List<ChunkSimilarityDto> results = aiRagChunkMapper.searchSimilar(new PGvector(v1), 5);
            assertThat(results).isNotEmpty();
            assertThat(results.get(0).getId()).isEqualTo(chunkA.getId());
            assertThat(results.get(0).getSimilarity()).isGreaterThan(0.999);
            assertThat(results.get(0).getMetadata()).contains("dual-ds-test");
        } finally {
            aiRagChunkMapper.deleteByDocumentId(documentId);
        }
    }

    /**
     * PostgreSQL 事务管理器独立可用：异常时回滚
     */
    @Test
    void postgresTransactionManagerRollbackWorks() {
        long documentId = 900000004L;
        TransactionTemplate tx = new TransactionTemplate(postgresTransactionManager);
        try {
            tx.execute(status -> {
                aiRagChunkMapper.insert(buildChunk(documentId, 0, "事务回滚测试分块", new float[1024]));
                throw new RuntimeException("强制回滚");
            });
        } catch (RuntimeException ignored) {
            // 预期异常：触发 PostgreSQL 事务回滚
        }
        assertThat(aiRagChunkMapper.selectByDocumentId(documentId)).isEmpty();
    }

    /**
     * MySQL / PostgreSQL Mapper 互不扫描，事务管理器分离
     */
    @Test
    void mappersAndTransactionManagersAreIsolated() {
        // MySQL 会话只包含业务 Mapper
        assertThat(mysqlSqlSessionTemplate.getConfiguration().hasMapper(SysUserMapper.class)).isTrue();
        assertThat(mysqlSqlSessionTemplate.getConfiguration().hasMapper(AiRagChunkMapper.class)).isFalse();
        // PostgreSQL 会话只包含 RAG Mapper
        assertThat(postgresSqlSessionTemplate.getConfiguration().hasMapper(AiRagChunkMapper.class)).isTrue();
        assertThat(postgresSqlSessionTemplate.getConfiguration().hasMapper(SysUserMapper.class)).isFalse();
        // 事务管理器分别绑定各自数据源
        assertThat(mysqlTransactionManager.getDataSource()).isSameAs(mysqlDataSource);
        assertThat(postgresTransactionManager.getDataSource()).isSameAs(postgresDataSource);
    }

    private AiRagChunk buildChunk(long documentId, int chunkIndex, String content, float[] vector) {
        AiRagChunk chunk = new AiRagChunk();
        chunk.setDocumentId(documentId);
        chunk.setChunkIndex(chunkIndex);
        chunk.setContent(content);
        chunk.setTokenCount(content.length());
        chunk.setEmbedding(new PGvector(vector));
        chunk.setEmbeddingModel("test-model");
        chunk.setEmbeddingVersion("v1");
        chunk.setMetadata("{\"source\":\"dual-ds-test\"}");
        chunk.setIsActive(true);
        chunk.setCrtim(new Date());
        chunk.setUptim(new Date());
        chunk.setIsDeleted(false);
        return chunk;
    }
}
