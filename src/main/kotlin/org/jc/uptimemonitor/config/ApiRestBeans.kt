package org.jc.uptimemonitor.config

import org.jc.uptimemonitor.dao.config.DaoBeans
import org.jc.uptimemonitor.dao.contract.CheckResultsDao
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.scheduler.EndpointHealthCheckScheduler
import org.jc.uptimemonitor.service.UptimeMonitoringService
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Import(DaoBeans::class)
@Configuration
class ApiRestBeans {

    @Bean
    fun uptimeMonitoringService(
        monitoredEndpointsDao: MonitoredEndpointsDao,
    ): UptimeMonitoringService =
        UptimeMonitoringService(monitoredEndpointsDao)

    @Bean
    fun healthCheckRestTemplate(): RestTemplate =
        RestTemplateBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build()

    @Bean
    fun endpointHealthCheckScheduler(
        monitoredEndpointsDao: MonitoredEndpointsDao,
        checkResultsDao: CheckResultsDao,
        healthCheckRestTemplate: RestTemplate,
    ): EndpointHealthCheckScheduler =
        EndpointHealthCheckScheduler(monitoredEndpointsDao, checkResultsDao, healthCheckRestTemplate)
}