package com.clj;

import com.pgvector.PGvector;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PostgreSQL 连接 + pgvector 写入/相似度查询测试（纯 JDBC，不依赖 Spring 上下文）
 * 配置与 application-dev.yaml 中 spring.datasource.postgres 保持一致
 */
class PostgresConnectionTest {

    private static final String URL = "jdbc:postgresql://192.168.127.128:5432/agriculture_ai";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    @Test
    void connectInsertAndVectorSimilarityQuery() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 注册 pgvector 类型，使驱动 getObject 直接返回 PGvector（pgvector-java 官方用法）
            PGvector.addVectorType(conn);
            // 1. 连接成功，获取 PostgreSQL 版本
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT version()");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString(1)).contains("PostgreSQL");
            }

            // 2. pgvector 扩展已安装
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT extversion FROM pg_extension WHERE extname = 'vector'")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString(1)).isNotBlank();
            }

            // 3. VECTOR(1024) 写入
            float[] vec = new float[1024];
            for (int i = 0; i < vec.length; i++) {
                vec[i] = (i % 100) / 100f;
            }
            PGvector embedding = new PGvector(vec);
            long documentId = 900000002L;
            long insertedId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ai_rag_chunk (document_id, chunk_index, content, token_count, " +
                            "embedding, embedding_model, embedding_version, metadata, is_active, " +
                            "crtim, uptim, is_deleted) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), TRUE, now(), now(), FALSE)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, documentId);
                ps.setInt(2, 0);
                ps.setString(3, "pgvector 连接测试分块");
                ps.setInt(4, 12);
                ps.setObject(5, embedding);
                ps.setString(6, "test-model");
                ps.setString(7, "v1");
                ps.setString(8, "{\"source\":\"junit\"}");
                assertThat(ps.executeUpdate()).isEqualTo(1);
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    assertThat(keys.next()).isTrue();
                    insertedId = keys.getLong(1);
                }
            }

            try {
                // 4. VECTOR 相似度查询（余弦相似度）
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, 1 - (embedding <=> ?) AS similarity " +
                                "FROM ai_rag_chunk WHERE is_deleted = FALSE AND is_active = TRUE " +
                                "ORDER BY embedding <=> ? LIMIT 3")) {
                    ps.setObject(1, embedding);
                    ps.setObject(2, embedding);
                    try (ResultSet rs = ps.executeQuery()) {
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getLong("id")).isEqualTo(insertedId);
                        assertThat(rs.getDouble("similarity")).isGreaterThan(0.999);
                    }
                }

                // 5. 读取 embedding 并校验维度
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT embedding FROM ai_rag_chunk WHERE id = " + insertedId)) {
                    assertThat(rs.next()).isTrue();
                    PGvector readBack = rs.getObject(1, PGvector.class);
                    assertThat(readBack.toArray()).hasSize(1024);
                }
            } finally {
                // 清理测试数据
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("DELETE FROM ai_rag_chunk WHERE id = " + insertedId);
                }
            }
        }
    }
}
