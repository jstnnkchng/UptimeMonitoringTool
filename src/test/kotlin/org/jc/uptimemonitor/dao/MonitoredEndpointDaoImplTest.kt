package org.jc.uptimemonitor.dao

import kotlinx.coroutines.test.runTest
import org.jc.uptimemonitor.dao.impl.MonitoredEndpointsDaoImpl
import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import java.time.LocalDateTime

/** Test class for MonitoredEndpointDaoImpl contracts */
class MonitoredEndpointDaoImplTest {

    private val mockJdbcTemplate: NamedParameterJdbcTemplate = mock()
    private val mockRowMapper: RowMapper<MonitoredEndpoint> = mock()
    private lateinit var dao: MonitoredEndpointsDaoImpl

    private val getAllEndpointsByFrequencySql: String = """
        SELECT * FROM monitored_endpoints
        WHERE frequency = :frequency
        AND active = :active
    """.trimIndent()
    private val insertSql = """
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

    private val testMonitoredEndpointRequest = MonitoredEndpointRequest(
        email = "user@example.com",
        expectedResponse = null,
        expectedStatusCode = 200,
        frequency = "DAILY",
        url = "https://example.com/health",
        userId = "user-1"
    )
    private val testMonitoredEndpoint = MonitoredEndpoint(
        active = true,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        email = "user@example.com",
        expectedResponse = null,
        expectedStatusCode = 200,
        frequency = Frequency.DAILY,
        url = "https://example.com/health",
        userId = "user-1"
    )

    @BeforeEach
    fun setUp() {
        dao = MonitoredEndpointsDaoImpl(mockJdbcTemplate, mockRowMapper)
    }

    // -- insert tests --

    @Test
    fun `insert returns true when row is inserted`() = runTest {
        val request = testMonitoredEndpointRequest

        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenReturn(1)

        val result = dao.insert(request)

        assertTrue(result)
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }

    @Test
    fun `insert throws when database fails`() = runTest {
        val request = testMonitoredEndpointRequest

        whenever(mockJdbcTemplate.update(eq(insertSql), any<SqlParameterSource>()))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            dao.insert(request)
        }
        verify(mockJdbcTemplate).update(eq(insertSql), any<SqlParameterSource>())
    }

    // -- getAllEndpointsByFrequency tests --

    @Test
    fun `getAllEndpointsByFrequency returns list of endpoints`() = runTest {
        val expected = listOf(testMonitoredEndpoint)

        whenever(mockJdbcTemplate.query(eq(getAllEndpointsByFrequencySql), any<SqlParameterSource>(), eq(mockRowMapper)))
            .thenReturn(expected)

        val result = dao.getAllEndpointsByFrequency(Frequency.DAILY)

        assertEquals(expected, result)
        verify(mockJdbcTemplate).query(eq(getAllEndpointsByFrequencySql), any<SqlParameterSource>(), eq(mockRowMapper))
    }

    @Test
    fun `getAllEndpointsByFrequency throws when database fails`() = runTest {
        whenever(mockJdbcTemplate.query(eq(getAllEndpointsByFrequencySql), any<SqlParameterSource>(), eq(mockRowMapper)))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            dao.getAllEndpointsByFrequency(Frequency.EVERY_15_MINUTES)
        }
        verify(mockJdbcTemplate).query(eq(getAllEndpointsByFrequencySql), any<SqlParameterSource>(), eq(mockRowMapper))
    }
}