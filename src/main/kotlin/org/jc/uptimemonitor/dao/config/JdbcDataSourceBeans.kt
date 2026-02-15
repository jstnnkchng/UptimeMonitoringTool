package org.jc.uptimemonitor.dao.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class JdbcDataSourceBeans {
    @Bean
    open fun postgresDataSource(env: Environment): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"))
        dataSource.setUrl(env.getRequiredProperty("spring.datasource.url"))
        dataSource.setUsername(env.getRequiredProperty("spring.datasource.username"))
        dataSource.setPassword(env.getRequiredProperty("spring.datasource.password"))
        return dataSource
    }

    @Bean
    open fun jdbcTemplatePostgres(postgresDataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(postgresDataSource)
    }
}