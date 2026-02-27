package org.jc.uptimemonitor.dao.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jc.uptimemonitor.dao.contract.CheckResultsDao
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/** Implementation of DAO Contracts for check_results table */
class CheckResultsDaoImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) : CheckResultsDao {
    private val logger = LoggerFactory.getLogger(CheckResultsDaoImpl::class.java)

    companion object {
        const val INSERT_OP = "INSERT_CHECK_RESULT"
    }

    private val INSERT_SQL = """
        INSERT INTO check_results (
        endpoint_id,
        status_code,
        response_body,
        passed,
        failure_reason)
        VALUES (
        :endpoint_id,
        :status_code,
        :response_body,
        :passed,
        :failure_reason)
    """.trimIndent()

    /**
     * Inserts a new check result
     *
     * @param endpointId the endpoint that was checked
     * @param statusCode actual HTTP status code (null if connection failed)
     * @param responseBody actual response body
     * @param passed whether the check matched expectations
     * @param failureReason description of why the check failed (null if passed)
     * @return result of insertion
     */
    override suspend fun insert(
        endpointId: Long,
        statusCode: Int?,
        responseBody: String?,
        passed: Boolean,
        failureReason: String?,
    ): Boolean =
        withContext(Dispatchers.IO) {

        logger.info(INSERT_OP)

        val namedParameters = MapSqlParameterSource()
            .addValue("endpoint_id", endpointId)
            .addValue("status_code", statusCode)
            .addValue("response_body", responseBody)
            .addValue("passed", passed)
            .addValue("failure_reason", failureReason)

        val rowsAffected = jdbcTemplate.update(INSERT_SQL, namedParameters)

        rowsAffected > 0
    }
}
