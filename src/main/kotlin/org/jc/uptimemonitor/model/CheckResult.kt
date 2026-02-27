package org.jc.uptimemonitor.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/** POJO for check result data */
data class CheckResult(
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val checkedAt: LocalDateTime,
    val endpointId: Long,
    val failureReason: String?,
    val id: Long,
    val passed: Boolean,
    val responseBody: String?,
    val statusCode: Int?,
)