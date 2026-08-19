package com.clj;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySQL 连接测试（纯 JDBC，只读，不依赖 Spring 上下文）
 * 配置与 application-dev.yaml 中 spring.datasource.mysql 保持一致
 */
class MysqlConnectionTest {

    private static final String URL = "jdbc:mysql://192.168.127.128:3306/smart_agriculture"
            + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123";

    @Test
    void connectAndReadBusinessTable() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement()) {
            // 业务表 sys_user 存在且可查询
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sys_user")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getLong(1)).isGreaterThanOrEqualTo(0);
            }
        }
    }
}
