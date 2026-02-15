package org.jc.uptimemonitor.dao.config

import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.dao.impl.MonitoredEndpointsDaoImpl
import org.jc.uptimemonitor.dao.rowmapper.MonitoredEndpointsRowMapper
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Import(JdbcDataSourceBeans::class)
@Configuration
class DaoBeans {

    @Bean
    open fun monitoredEndpointsRowMapper(): RowMapper<MonitoredEndpoint> =
        MonitoredEndpointsRowMapper()

    @Bean
    open fun monitoredEndpointDao(
        jdbcTemplate: NamedParameterJdbcTemplate,
        monitoredEndpointsRowMapper: RowMapper<MonitoredEndpoint>
    ): MonitoredEndpointsDao =
        MonitoredEndpointsDaoImpl(jdbcTemplate, monitoredEndpointsRowMapper)
}