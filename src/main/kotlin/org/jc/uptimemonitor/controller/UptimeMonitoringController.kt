package org.jc.uptimemonitor.controller

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.jc.uptimemonitor.service.UptimeMonitoringService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping
    fun searchByUserId(
        @RequestParam userId: String
    ): CompletableFuture<ResponseEntity<List<MonitoredEndpoint>>> {

        logger.info("UptimeMonitoringController searching for user id $userId")

        return CoroutineScope(CoroutineName("searchByUserId") + Dispatchers.Default).future {
            ResponseEntity.ok(uptimeMonitoringService.searchByUserId(userId))
        }
    }

    @DeleteMapping
    fun deleteEndpointById(
        @RequestParam endpointId: Long
    ): CompletableFuture<ResponseEntity<Map<String, String>>> {

        logger.info("UptimeMonitoringController delete for endpoint id $endpointId")

        return CoroutineScope(CoroutineName("deleteEndpoint") + Dispatchers.Default).future {
            val deleted = uptimeMonitoringService.deleteByEndpointId(endpointId)
            if (deleted) {
                ResponseEntity.ok(mapOf("message" to "Endpoint deleted successfully"))
            } else {
                ResponseEntity.unprocessableEntity()
                    .body(mapOf("message" to "Could not delete endpoint"))
            }
        }
    }
}
