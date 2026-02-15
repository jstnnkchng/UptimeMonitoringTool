package org.jc.uptimemonitor.dao.contract

import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint

/** DAO Contracts for monitored_endpoints table */
interface MonitoredEndpointsDao {

    /**
     * Inserts a new MonitoredEndpoint
     *
     * @param monitoredEndpointRequest MonitoredEndpoint info to store
     * @return result of insertion
     */
    suspend fun insert(
        monitoredEndpointRequest: MonitoredEndpoint
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
}