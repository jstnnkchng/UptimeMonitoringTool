package org.jc.uptimemonitor.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.model.MonitoredEndpoint
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

    suspend fun searchByUserId(
        userId: String,
    ): List<MonitoredEndpoint> = withContext(Dispatchers.Default) {
        try {
            monitoredEndpointsDao.getAllEndpointsByUserId(userId)
        } catch (e: Exception) {
            logger.error("Failed to get endpoints by userId", e)
            throw e
        }
    }

    suspend fun deleteByEndpointId(
        endpointId: Long,
    ): Boolean = withContext(Dispatchers.Default) {
        try {
            monitoredEndpointsDao.deleteByEndpointId(endpointId)
        } catch (e: Exception) {
            logger.error("Failed to delete endpoint by endpointId", e)
            throw e
        }
    }
}