package org.jc.uptimemonitor.scheduler

import kotlinx.coroutines.test.runTest
import org.jc.uptimemonitor.dao.contract.CheckResultsDao
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

/** Test class for EndpointHealthCheckScheduler */
class EndpointHealthCheckSchedulerTest {

    private val mockMonitoredEndpointsDao: MonitoredEndpointsDao = mock()
    private val mockCheckResultsDao: CheckResultsDao = mock()
    private val mockRestTemplate: RestTemplate = mock()
    private lateinit var scheduler: EndpointHealthCheckScheduler

    private val testEndpoint = MonitoredEndpoint(
        active = true,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        email = "user@example.com",
        endpointId = 12345,
        expectedResponse = null,
        expectedStatusCode = 200,
        frequency = Frequency.DAILY,
        lastCheckedAt = null,
        url = "https://example.com/health",
        userId = "user-1"
    )

    @BeforeEach
    fun setUp() {
        scheduler = EndpointHealthCheckScheduler(
            mockMonitoredEndpointsDao,
            mockCheckResultsDao,
            mockRestTemplate,
        )
    }

    // -- runHealthChecks tests --

    @Test
    fun `runHealthChecks fetches endpoints for all frequency tiers`() {
        for (frequency in Frequency.entries) {
            whenever(kotlinx.coroutines.runBlocking { mockMonitoredEndpointsDao.getEndpointsDueForCheck(eq(frequency)) })
                .thenReturn(emptyList())
        }

        scheduler.runHealthChecks()

        for (frequency in Frequency.entries) {
            kotlinx.coroutines.runBlocking {
                verify(mockMonitoredEndpointsDao).getEndpointsDueForCheck(eq(frequency))
            }
        }
    }

    // -- checkEndpoint tests --

    @Test
    fun `checkEndpoint records pass when status code matches`() = runTest {
        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("OK"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), any()))
            .thenReturn(true)
        whenever(mockMonitoredEndpointsDao.updateLastCheckedAt(eq(12345L)))
            .thenReturn(true)

        scheduler.checkEndpoint(testEndpoint)

        verify(mockRestTemplate).getForEntity(eq("https://example.com/health"), eq(String::class.java))
        verify(mockCheckResultsDao).insert(
            endpointId = eq(12345L),
            statusCode = eq(200),
            responseBody = eq("OK"),
            passed = eq(true),
            failureReason = eq(null),
        )
        verify(mockMonitoredEndpointsDao).updateLastCheckedAt(eq(12345L))
    }

    @Test
    fun `checkEndpoint records fail when status code does not match`() = runTest {
        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("down"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), any()))
            .thenReturn(true)
        whenever(mockMonitoredEndpointsDao.updateLastCheckedAt(eq(12345L)))
            .thenReturn(true)

        scheduler.checkEndpoint(testEndpoint)

        verify(mockCheckResultsDao).insert(
            endpointId = eq(12345L),
            statusCode = eq(503),
            responseBody = eq("down"),
            passed = eq(false),
            failureReason = eq("Expected status 200 but got 503"),
        )
        verify(mockMonitoredEndpointsDao).updateLastCheckedAt(eq(12345L))
    }

    @Test
    fun `checkEndpoint records fail when expected response body does not match`() = runTest {
        val endpointWithExpectedBody = testEndpoint.copy(expectedResponse = "healthy")

        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("unhealthy"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), any()))
            .thenReturn(true)
        whenever(mockMonitoredEndpointsDao.updateLastCheckedAt(eq(12345L)))
            .thenReturn(true)

        scheduler.checkEndpoint(endpointWithExpectedBody)

        verify(mockCheckResultsDao).insert(
            endpointId = eq(12345L),
            statusCode = eq(200),
            responseBody = eq("unhealthy"),
            passed = eq(false),
            failureReason = eq("Response body does not match expected response"),
        )
    }

    @Test
    fun `checkEndpoint records pass when expected response body matches`() = runTest {
        val endpointWithExpectedBody = testEndpoint.copy(expectedResponse = "healthy")

        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("healthy"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), any()))
            .thenReturn(true)
        whenever(mockMonitoredEndpointsDao.updateLastCheckedAt(eq(12345L)))
            .thenReturn(true)

        scheduler.checkEndpoint(endpointWithExpectedBody)

        verify(mockCheckResultsDao).insert(
            endpointId = eq(12345L),
            statusCode = eq(200),
            responseBody = eq("healthy"),
            passed = eq(true),
            failureReason = eq(null),
        )
    }

    @Test
    fun `checkEndpoint handles connection failure gracefully`() = runTest {
        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenThrow(ResourceAccessException("Connection refused"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), any()))
            .thenReturn(true)
        whenever(mockMonitoredEndpointsDao.updateLastCheckedAt(eq(12345L)))
            .thenReturn(true)

        scheduler.checkEndpoint(testEndpoint)

        verify(mockCheckResultsDao).insert(
            endpointId = eq(12345L),
            statusCode = eq(null),
            responseBody = eq(null),
            passed = eq(false),
            failureReason = eq("Connection failed: Connection refused"),
        )
        verify(mockMonitoredEndpointsDao).updateLastCheckedAt(eq(12345L))
    }

    @Test
    fun `checkEndpoint does not crash when persisting result fails`() = runTest {
        whenever(mockRestTemplate.getForEntity(eq("https://example.com/health"), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("OK"))
        whenever(mockCheckResultsDao.insert(any(), any(), any(), any(), eq(null)))
            .thenThrow(RuntimeException("DB error"))

        scheduler.checkEndpoint(testEndpoint)

        verify(mockRestTemplate).getForEntity(eq("https://example.com/health"), eq(String::class.java))
        verify(mockMonitoredEndpointsDao, never()).updateLastCheckedAt(any())
    }
}
