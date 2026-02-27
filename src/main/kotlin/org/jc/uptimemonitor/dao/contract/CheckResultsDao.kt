package org.jc.uptimemonitor.dao.contract

/** DAO Contracts for check_results table */
interface CheckResultsDao {

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
    suspend fun insert(
        endpointId: Long,
        statusCode: Int?,
        responseBody: String?,
        passed: Boolean,
        failureReason: String?,
    ): Boolean
}