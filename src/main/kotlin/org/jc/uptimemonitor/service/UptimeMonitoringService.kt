package org.jc.uptimemonitor.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.slf4j.LoggerFactory

class UptimeMonitoringService(
    private val monitoredEndpointsDao: MonitoredEndpointsDao,
) {
    private val logger = LoggerFactory.getLogger(UptimeMonitoringService::class.java)

    suspend fun addNewEndpoint(
        request: MonitoredEndpointRequest,
    ): Boolean = withContext(Dispatchers.Default) {

        try {
            monitoredEndpointsDao.insert(request)
        } catch (e: Exception) {
            logger.error("Failed to insert endpoint", e)
            throw e
        }
    }
}