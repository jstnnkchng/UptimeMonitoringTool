package org.jc.uptimemonitor.controller

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.jc.uptimemonitor.service.UptimeMonitoringService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/v1/monitorEndpoints")
class UptimeMonitoringController(
    private val uptimeMonitoringService: UptimeMonitoringService,
) {

    private val logger = LoggerFactory.getLogger(UptimeMonitoringController::class.java)

    @PostMapping
    fun registerNewEndpoint(
        @RequestBody endpointRequest: MonitoredEndpointRequest
    ): CompletableFuture<ResponseEntity<Map<String, String>>> {

        logger.info("UptimeMonitoringController registering new endpoint")

        return CoroutineScope(CoroutineName("registerNewEndpoint") + Dispatchers.Default).future {
            val inserted = uptimeMonitoringService.addNewEndpoint(endpointRequest)
            if (inserted) {
                ResponseEntity.ok(mapOf("message" to "Endpoint registered successfully"))
            } else {
                ResponseEntity.unprocessableEntity()
                    .body(mapOf("message" to "Could not insert endpoint"))
            }
        }
    }
}
