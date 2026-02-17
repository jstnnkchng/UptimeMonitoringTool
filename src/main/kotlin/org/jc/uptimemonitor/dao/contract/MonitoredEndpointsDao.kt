package org.jc.uptimemonitor.dao.contract

import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.jc.uptimemonitor.model.MonitoredEndpointRequest

/** DAO Contracts for monitored_endpoints table */
interface MonitoredEndpointsDao {

    /**
     * Inserts a new MonitoredEndpoint
     *
     * @param monitoredEndpointRequest MonitoredEndpoint info to store
     * @return result of insertion
     */
    suspend fun insert(
        monitoredEndpointRequest: MonitoredEndpointRequest
    ): Boolean

    /**
     * Gets all MonitoredEndpoints for a given frequency
     *
     * @param frequency to filter by
     * @return List<MonitoredEndpoint>
     */
    suspend fun getAllEndpointsByFrequency(
        frequency: Frequency
    ): List<MonitoredEndpoint>

    /**
     * Gets all MonitoredEndpoints for a given userId
     *
     * @param userId to search by
     * @return List<MonitoredEndpoint>
     */
    suspend fun getAllEndpointsByUserId(
        userId: String
    ): List<MonitoredEndpoint>

    /**
     * Deletes a MonitoredEndpoint by a given endpoint id
     *
     * @param endpointId to search by
     * @return result of delete
     */
    suspend fun deleteByEndpointId(
        endpointId: Long
    ): Boolean
}