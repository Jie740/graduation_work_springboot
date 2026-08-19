package com.clj.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * MySQL 主数据源配置
 * 负责所有农业业务数据，使用 MyBatis-Plus，Mapper 扫描范围 com.clj.mapper
 *
 * 配置来源：spring.datasource.mysql.*（application-*.yaml）
 */
@Configuration
@MapperScan(basePackages = "com.clj.mapper",
        sqlSessionFactoryRef = "mysqlSqlSessionFactory",
        sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class MysqlDataSourceConfig {

    /**
     * MySQL 主数据源（默认数据源，Druid 连接池）
     */
    @Primary
    @Bean(name = "mysqlDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DruidDataSource mysqlDataSource() {
        return new DruidDataSource();
    }

    /**
     * MySQL SqlSessionFactory（MyBatis-Plus），只负责 com.clj.mapper 下的业务 Mapper
     */
    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource,
                                                    MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        // MyBatis-Plus 插件（分页等）
        factory.setPlugins(mybatisPlusInterceptor);
        // 与 application.yaml 中 mybatis-plus.configuration.map-underscore-to-camel-case 保持一致
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);
        return factory.getObject();
    }

    /**
     * MySQL SqlSessionTemplate
     */
    @Primary
    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(
            @Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * MySQL 事务管理器（默认事务管理器，@Transactional 不带限定符时使用）
     */
    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
