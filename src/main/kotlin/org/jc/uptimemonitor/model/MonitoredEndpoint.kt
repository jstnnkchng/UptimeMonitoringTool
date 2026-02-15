package org.jc.uptimemonitor.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.jc.uptimemonitor.enums.Frequency
import java.time.LocalDateTime

data class MonitoredEndpoint(
    val active: Boolean,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    val email: String,
    val expectedResponse: String?,
    val expectedStatusCode: Int,
    val frequency: Frequency,
    val url: String,
    val userId: String,
)
