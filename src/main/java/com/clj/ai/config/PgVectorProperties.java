package com.clj.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.pgvector")
public class PgVectorProperties {
    private String host = "192.168.127.128";
    private Integer port = 5432;
    private String database = "agriculture_ai";
    private String user = "postgres";
    private String password = "123456";
    private String table = "ai_rag_embedding";
    private Integer dimension = 1024;
    private Boolean useIndex = false;
    private Integer indexListSize = 100;
    private Boolean dropExisting = false;
}