package org.jc.uptimemonitor.dao.config

import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.dao.impl.MonitoredEndpointsDaoImpl
import org.jc.uptimemonitor.dao.rowmapper.MonitoredEndpointsRowMapper
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class DaoBeans {

    @Bean
    fun monitoredEndpointsRowMapper(): RowMapper<MonitoredEndpoint> =
        MonitoredEndpointsRowMapper()

    @Bean
    fun monitoredEndpointDao(
        jdbcTemplate: NamedParameterJdbcTemplate,
        monitoredEndpointsRowMapper: RowMapper<MonitoredEndpoint>
    ): MonitoredEndpointsDao =
        MonitoredEndpointsDaoImpl(jdbcTemplate, monitoredEndpointsRowMapper)
}