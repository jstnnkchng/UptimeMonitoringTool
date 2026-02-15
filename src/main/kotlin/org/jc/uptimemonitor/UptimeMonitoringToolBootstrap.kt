package org.jc.uptimemonitor

import org.jc.uptimemonitor.config.ApiRestBeans
import org.jc.uptimemonitor.controller.ControllerAdvice
import org.jc.uptimemonitor.controller.UptimeMonitoringController
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class UptimeMonitoringToolBootstrap {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val logger = LoggerFactory.getLogger(UptimeMonitoringToolBootstrap::class.java)

            try {
                SpringApplicationBuilder(UptimeMonitoringToolBootstrap::class.java)
                    .bannerMode(Banner.Mode.OFF)
                    .headless(true)
                    .logStartupInfo(true)
                    .sources(
                        ApiRestBeans::class.java,
                        ControllerAdvice::class.java,
                        UptimeMonitoringController::class.java,
                    )
                    .build()
                    .run(*args)
            } catch (ex: Exception) {
                logger.error("Error starting UptimeMonitoringTool application: ${ex.message}")
            }
        }
    }
}
