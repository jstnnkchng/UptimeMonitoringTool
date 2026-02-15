package org.jc.uptimemonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UptimeMonitoringToolApplication

fun main(args: Array<String>) {
    runApplication<UptimeMonitoringToolApplication>(*args)
}
