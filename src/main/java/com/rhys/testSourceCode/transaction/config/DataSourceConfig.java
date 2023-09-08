package com.rhys.testSourceCode.transaction.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.*;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/9/8 10:15
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
@Configuration
public class DataSourceConfig {

    @Bean
    DruidDataSource getDruidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://101.133.157.40:3886/test?useUnicode=true&characterEncoding=utf-8");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("980512@Nsd");
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(10);
        return druidDataSource;
    }

    @Bean
    JdbcTemplate getJdbcTemplate(DruidDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    DataSourceTransactionManager getTransactionManager(DruidDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
