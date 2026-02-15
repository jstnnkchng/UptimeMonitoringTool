package org.jc.uptimemonitor.model

import org.jc.uptimemonitor.enums.Frequency

/** POJO for insertion request to monitored_endpoints table */
data class MonitoredEndpointRequest(
    val email: String,
    val expectedResponse: String?,
    val expectedStatusCode: Int,
    val frequency: String,
    val url: String,
    val userId: String,
) {
    init {
        require(frequency in Frequency.entries.map { it.name }) {
            "frequency must be one of: ${Frequency.entries.joinToString()}"
        }
    }
}