package org.jc.uptimemonitor.service

import kotlinx.coroutines.test.runTest
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.dao.DataAccessException

/** Test class for UptimeMonitoringService */
class UptimeMonitoringServiceTest {

    private val mockDao: MonitoredEndpointsDao = mock()
    private lateinit var service: UptimeMonitoringService

    private val testRequest = MonitoredEndpointRequest(
        email = "user@example.com",
        expectedResponse = null,
        expectedStatusCode = 200,
        frequency = "DAILY",
        url = "https://example.com/health",
        userId = "user-1"
    )

    @BeforeEach
    fun setUp() {
        service = UptimeMonitoringService(mockDao)
    }

    // -- addNewEndpoint tests --

    @Test
    fun `addNewEndpoint returns true when insert succeeds`() = runTest {
        whenever(mockDao.insert(eq(testRequest)))
            .thenReturn(true)

        val result = service.addNewEndpoint(testRequest)

        assertTrue(result)

        verify(mockDao).insert(eq(testRequest))
    }

    @Test
    fun `addNewEndpoint returns false when no insert occurs`() = runTest {
        whenever(mockDao.insert(eq(testRequest)))
            .thenReturn(false)

        val result = service.addNewEndpoint(testRequest)

        assertFalse(result)

        verify(mockDao).insert(eq(testRequest))
    }

    @Test
    fun `addNewEndpoint throws when database fails`() = runTest {
        whenever(mockDao.insert(eq(testRequest)))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            service.addNewEndpoint(testRequest)
        }

        verify(mockDao).insert(eq(testRequest))
    }
}