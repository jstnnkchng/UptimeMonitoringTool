package org.jc.uptimemonitor.dao

import kotlinx.coroutines.test.runTest
import org.jc.uptimemonitor.dao.impl.CheckResultsDaoImpl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource

/** Test class for CheckResultsDaoImpl contracts */
class CheckResultsDaoImplTest {

    private val mockJdbcTemplate: NamedParameterJdbcTemplate = mock()
    private lateinit var dao: CheckResultsDaoImpl

    private val insertSql = """
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

    @BeforeEach
    fun setUp() {
        dao = CheckResultsDaoImpl(mockJdbcTemplate)
    }

    // -- insert tests --

    @Test
    fun `insert returns true when row is inserted`() = runTest {
        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenReturn(1)

        val result = dao.insert(
            endpointId = 12345L,
            statusCode = 200,
            responseBody = "OK",
            passed = true,
            failureReason = null,
        )

        assertTrue(result)
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }

    @Test
    fun `insert returns true for failed check result`() = runTest {
        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenReturn(1)

        val result = dao.insert(
            endpointId = 12345L,
            statusCode = 503,
            responseBody = "Service Unavailable",
            passed = false,
            failureReason = "Expected status 200 but got 503",
        )

        assertTrue(result)
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }

    @Test
    fun `insert returns false when no row is inserted`() = runTest {
        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenReturn(0)

        val result = dao.insert(
            endpointId = 12345L,
            statusCode = 200,
            responseBody = "OK",
            passed = true,
            failureReason = null,
        )

        assertFalse(result)
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }

    @Test
    fun `insert throws when database fails`() = runTest {
        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            dao.insert(
                endpointId = 12345L,
                statusCode = 200,
                responseBody = "OK",
                passed = true,
                failureReason = null,
            )
        }
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }
}
