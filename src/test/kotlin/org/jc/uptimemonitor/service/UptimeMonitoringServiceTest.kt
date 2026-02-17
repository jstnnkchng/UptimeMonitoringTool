package org.jc.uptimemonitor.service

import kotlinx.coroutines.test.runTest
import org.jc.uptimemonitor.dao.contract.MonitoredEndpointsDao
import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.jc.uptimemonitor.model.MonitoredEndpointRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDateTime
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

    private val testEndpointDetails = MonitoredEndpoint(
        active = true,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        email = "user@example.com",
        expectedResponse = null,
        expectedStatusCode = 200,
        frequency = Frequency.DAILY,
        url = "https://example.com/health",
        userId = "user-1"
    )
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

    // -- searchByUserId tests --

    @Test
    fun `searchByUserId returns list of endpoints`() = runTest {
        val expected = listOf(testEndpointDetails)

        whenever(mockDao.getAllEndpointsByUserId(eq("user-1")))
            .thenReturn(expected)

        val result = service.searchByUserId("user-1")

        assertEquals(expected, result)
        verify(mockDao).getAllEndpointsByUserId(eq("user-1"))
    }

    @Test
    fun `searchByUserId returns empty list when no endpoints found`() = runTest {
        whenever(mockDao.getAllEndpointsByUserId(eq("nonexistent-user")))
            .thenReturn(emptyList())

        val result = service.searchByUserId("nonexistent-user")

        assertEquals(emptyList<MonitoredEndpoint>(), result)
        verify(mockDao).getAllEndpointsByUserId(eq("nonexistent-user"))
    }

    @Test
    fun `searchByUserId throws when database fails`() = runTest {
        whenever(mockDao.getAllEndpointsByUserId(eq("user-1")))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            service.searchByUserId("user-1")
        }
        verify(mockDao).getAllEndpointsByUserId(eq("user-1"))
    }

    // -- deleteByEndpointId tests --

    @Test
    fun `deleteByEndpointId returns true when row is deleted`() = runTest {
        whenever(mockDao.deleteByEndpointId(eq(1L)))
            .thenReturn(true)

        val result = service.deleteByEndpointId(1L)

        assertTrue(result)
        verify(mockDao).deleteByEndpointId(eq(1L))
    }

    @Test
    fun `deleteByEndpointId returns false when no row found`() = runTest {
        whenever(mockDao.deleteByEndpointId(eq(999L)))
            .thenReturn(false)

        val result = service.deleteByEndpointId(999L)

        assertFalse(result)
        verify(mockDao).deleteByEndpointId(eq(999L))
    }

    @Test
    fun `deleteByEndpointId throws when database fails`() = runTest {
        whenever(mockDao.deleteByEndpointId(eq(1L)))
            .thenThrow(object : DataAccessException("DB connection refused") {})

        assertThrows<DataAccessException> {
            service.deleteByEndpointId(1L)
        }
        verify(mockDao).deleteByEndpointId(eq(1L))
    }
}