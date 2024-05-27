package com.example.batch.conf;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.meta")
    DataSourceProperties metaProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.main")
    DataSourceProperties mainProperties() {
        return new DataSourceProperties();
    }

    @BatchDataSource
    @Bean
    public DataSource metaDataSource() {
        return metaProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public DataSource mainDataSource() {
        return mainProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(mainDataSource());
    }
}
