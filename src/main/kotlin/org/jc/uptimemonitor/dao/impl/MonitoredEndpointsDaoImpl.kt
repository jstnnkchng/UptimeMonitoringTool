package org.jc.uptimemonitor.dao.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/** Implementation of DAO Contracts for monitored_endpoints table */
class MonitoredEndpointsDaoImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val monitoredEndpointsRowMapper: RowMapper<MonitoredEndpoint>
): MonitoredEndpointsDao {
    private val logger = LoggerFactory.getLogger(MonitoredEndpointsDaoImpl::class.java)

    companion object {
        const val INSERT_OP = "INSERT_OP"
        const val GET_BY_FREQUENCY = "GET_BY_FREQUENCY"
    }

    private val INSERTSQL = """
        INSERT INTO monitored_endpoints (
        email,
        expected_response,
        expected_status_code,
        frequency,
        url,
        user_id)
        VALUES (
        :email,
        :expected_response,
        :expected_status_code,
        :frequency,
        :url,
        :user_id)
    """.trimIndent()

    /**
     * Inserts a new MonitoredEndpoint
     *
     * @param monitoredEndpointRequest MonitoredEndpoint info to store
     * @return result of insertion
     */
    override suspend fun insert(
        monitoredEndpointRequest: MonitoredEndpointRequest,
    ): Boolean =
        withContext(Dispatchers.IO) {

        logger.info(INSERT_OP)

        val namedParameters = MapSqlParameterSource()
            addValue("email", monitoredEndpointRequest.email)
            .addValue("expected_response", monitoredEndpointRequest.expectedResponse)
            .addValue("expected_status_code", monitoredEndpointRequest.expectedStatusCode)
            .addValue("frequency", monitoredEndpointRequest.frequency)
            .addValue("url", monitoredEndpointRequest.url)
            .addValue("user_id", monitoredEndpointRequest.userId)

        val rowsAffected = jdbcTemplate.update(INSERTSQL, namedParameters)

        rowsAffected > 0
    }

    /**
     * Gets all MonitoredEndpoints for a given frequency
     *
     * @param frequency to filter by
     * @return List<MonitoredEndpoint>
     */
    override suspend fun getAllEndpointsByFrequency(
        frequency: Frequency
    ): List<MonitoredEndpoint> =
        withContext(Dispatchers.IO) {

        logger.info(GET_BY_FREQUENCY)

        val sql: String = """
            SELECT * FROM monitored_endpoints
            WHERE frequency = :frequency
            AND active = :active
        """.trimIndent()

        val namedParameters = MapSqlParameterSource()
            .addValue("frequency", frequency.toString())
            .addValue("active", true)

        jdbcTemplate.query(sql, namedParameters, monitoredEndpointsRowMapper)
    }
}