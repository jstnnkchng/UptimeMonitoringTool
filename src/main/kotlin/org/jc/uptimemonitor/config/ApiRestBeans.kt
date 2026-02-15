package org.jc.uptimemonitor.config

import org.jc.uptimemonitor.dao.config.DaoBeans
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.service.UptimeMonitoringService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(DaoBeans::class)
@Configuration
class ApiRestBeans {

    @Bean
    fun uptimeMonitoringService(
        monitoredEndpointsDao: MonitoredEndpointsDao,
    ): UptimeMonitoringService =
        UptimeMonitoringService(monitoredEndpointsDao)
}