package org.jc.uptimemonitor.dao.rowmapper

import org.jc.uptimemonitor.enums.Frequency
import org.jc.uptimemonitor.model.MonitoredEndpoint
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class MonitoredEndpointsRowMapper : RowMapper<MonitoredEndpoint> {
    override fun mapRow(rs: ResultSet, rowNum: Int): MonitoredEndpoint {
        return MonitoredEndpoint(
            active = rs.getBoolean("active"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            email = rs.getString("email"),
            expectedResponse = rs.getString("expected_response"),
            expectedStatusCode = rs.getInt("expected_status_code"),
            frequency = Frequency.fromString(rs.getString("frequency")),
            url = rs.getString("url"),
            userId = rs.getString("user_id")
        )
    }
}