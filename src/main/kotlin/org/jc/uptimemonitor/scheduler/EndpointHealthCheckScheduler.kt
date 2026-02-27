package org.jc.uptimemonitor.scheduler

import kotlinx.coroutines.runBlocking
import org.jc.uptimemonitor.dao.contract.CheckResultsDao
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.client.RestTemplate

/** Scheduled health check runner that periodically checks all monitored endpoints */
class EndpointHealthCheckScheduler(
    private val monitoredEndpointsDao: MonitoredEndpointsDao,
    private val checkResultsDao: CheckResultsDao,
    private val restTemplate: RestTemplate,
) {
    private val logger = LoggerFactory.getLogger(EndpointHealthCheckScheduler::class.java)

    @Scheduled(fixedRate = 60_000)
    fun runHealthChecks() {
        logger.info("Starting scheduled health check run")

        runBlocking {
            for (frequency in Frequency.entries) {
                val endpoints = try {
                    monitoredEndpointsDao.getEndpointsDueForCheck(frequency)
                } catch (e: Exception) {
                    logger.error("Failed to fetch endpoints for frequency={}", frequency, e)
                    continue
                }

                logger.info("Found {} endpoints due for {} check", endpoints.size, frequency)

                for (endpoint in endpoints) {
                    checkEndpoint(endpoint)
                }
            }
        }

        logger.info("Completed scheduled health check run")
    }

    internal suspend fun checkEndpoint(endpoint: MonitoredEndpoint) {
        val url = endpoint.url
        val endpointId = endpoint.endpointId

        logger.info("Checking endpoint id={} url={}", endpointId, url)

        var statusCode: Int? = null
        var responseBody: String? = null
        var passed = false
        var failureReason: String? = null

        try {
            val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)
            statusCode = response.statusCode.value()
            responseBody = response.body

            if (statusCode != endpoint.expectedStatusCode) {
                failureReason = "Expected status ${endpoint.expectedStatusCode} but got $statusCode"
            } else if (endpoint.expectedResponse != null && responseBody != endpoint.expectedResponse) {
                failureReason = "Response body does not match expected response"
            } else {
                passed = true
            }
        } catch (e: Exception) {
            failureReason = "Connection failed: ${e.message}"
            logger.warn("Health check failed for endpoint id={} url={}: {}", endpointId, url, e.message)
        }

        if (passed) {
            logger.info("PASSED: endpoint id={} url={}", endpointId, url)
        } else {
            logger.warn("FAILED: endpoint id={} url={} reason={}", endpointId, url, failureReason)
        }

        try {
            checkResultsDao.insert(
                endpointId = endpointId,
                statusCode = statusCode,
                responseBody = responseBody,
                passed = passed,
                failureReason = failureReason,
            )
            monitoredEndpointsDao.updateLastCheckedAt(endpointId)
        } catch (e: Exception) {
            logger.error("Failed to persist check result for endpoint id={}", endpointId, e)
        }
    }
}