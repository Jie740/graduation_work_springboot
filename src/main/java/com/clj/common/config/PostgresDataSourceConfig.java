package com.clj.common.config;

import com.clj.ai.config.PgVectorTypeHandler;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * PostgreSQL 数据源配置
 * 专门用于 AI/RAG（pgvector），使用原生 MyBatis，Mapper 扫描范围 com.clj.ai.mapper
 *
 * 配置来源：spring.datasource.postgres.*（application-*.yaml）
 */
@Configuration
@MapperScan(basePackages = "com.clj.ai.mapper",
        sqlSessionFactoryRef = "postgresSqlSessionFactory",
        sqlSessionTemplateRef = "postgresSqlSessionTemplate")
public class PostgresDataSourceConfig {

    /**
     * PostgreSQL 数据源（Hikari 连接池）
     */
    @Bean(name = "postgresDataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public HikariDataSource postgresDataSource() {
        return new HikariDataSource();
    }

    /**
     * PostgreSQL SqlSessionFactory（MyBatis-Plus），负责 com.clj.ai.mapper 下的 AI/RAG Mapper
     * 使用 MybatisSqlSessionFactoryBean 以支持 MyBatis-Plus 的通用 CRUD 方法
     */
    @Bean(name = "postgresSqlSessionFactory")
    public SqlSessionFactory postgresSqlSessionFactory(
            @Qualifier("postgresDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        
        // MyBatis-Plus 配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        // 启用 MyBatis-Plus 的通用方法自动注册
        configuration.setUseGeneratedKeys(true);
        configuration.setUseColumnLabel(true);
        factory.setConfiguration(configuration);
        
        // 注册 pgvector 类型处理器，保证 VECTOR 列参数/结果正确转换
        factory.setTypeHandlers(new PgVectorTypeHandler());
        
        // 仅加载 AI/RAG 的 Mapper XML
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:com/clj/ai/mapper/*.xml"));
        
        // 设置类型别名包，让 MyBatis-Plus 能够识别实体类
        factory.setTypeAliasesPackage("com.clj.ai.domain");
        
        return factory.getObject();
    }

    /**
     * PostgreSQL SqlSessionTemplate
     */
    @Bean(name = "postgresSqlSessionTemplate")
    public SqlSessionTemplate postgresSqlSessionTemplate(
            @Qualifier("postgresSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * PostgreSQL 事务管理器
     * 使用方式：@Transactional(transactionManager = "postgresTransactionManager")
     */
    @Bean(name = "postgresTransactionManager")
    public DataSourceTransactionManager postgresTransactionManager(
            @Qualifier("postgresDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
