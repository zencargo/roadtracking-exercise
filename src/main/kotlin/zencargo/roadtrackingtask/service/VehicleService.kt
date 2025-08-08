package zencargo.roadtrackingtask.service

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.stereotype.Service
import zencargo.roadtrackingtask.domain.GeoLocation
import zencargo.roadtrackingtask.domain.Status
import zencargo.roadtrackingtask.domain.Vehicle
import java.time.LocalDateTime
import java.util.UUID

@Service
class VehicleService(private val jdbcTemplate: JdbcTemplate) {
    fun searchVehiclesByPlate(plate: String): List<Vehicle> {
        val sql = "SELECT * FROM vehicles"
        
        return jdbcTemplate.query(sql) { rs, _ ->
            Vehicle(
                id = UUID.fromString(rs.getString("id")),
                registrationNumber = rs.getString("plate_number"),
                accountId = UUID.fromString(rs.getString("account_id")),
                position = GeoLocation(
                    latitude = rs.getDouble("latitude"),
                    longitude = rs.getDouble("longitude")
                ),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }
    }
    fun getAllVehicles(accountId: UUID): List<Vehicle> {
        val sql = "SELECT * FROM vehicles WHERE account_id = ?"

        return jdbcTemplate.query(
            sql,
            { rs, _ ->
                Vehicle(
                    id = UUID.fromString(rs.getString("id")),
                    registrationNumber = rs.getString("plate_number"),
                    accountId = UUID.fromString(rs.getString("account_id")),
                    position = GeoLocation(
                        latitude = rs.getDouble("latitude"),
                        longitude = rs.getDouble("longitude")
                    ),
                    status = Status.valueOf(rs.getString("status") ?: Status.ACTIVE.name),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            },
            accountId.toString()  
        )
    }

    fun registerVehicle(registrationNumber: String, accountId: UUID): Vehicle {
        val id = UUID.randomUUID()
        val now = LocalDateTime.now()

        val sql = """
            INSERT INTO vehicles (id, plate_number, account_id, latitude, longitude, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """

        jdbcTemplate.update(sql) { ps ->
            ps.setString(1, id.toString())
            ps.setString(2, registrationNumber)
            ps.setString(3, accountId.toString())
            ps.setDouble(4, 0.0)
            ps.setDouble(5, 0.0)
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(now))
        }

        return Vehicle(
            id = id,
            registrationNumber = registrationNumber,
            accountId = accountId,
            position = GeoLocation(0.0, 0.0),
            status = Status.ACTIVE,
            updatedAt = now
        )
    }


    fun getVehicleById(id: UUID): Vehicle? {
        val sql = "SELECT * FROM vehicles WHERE id = ?"

        val results = jdbcTemplate.query(sql, PreparedStatementSetter { ps ->
            ps.setString(1, id.toString())
        }) { rs, _ ->
            Vehicle(
                id = UUID.fromString(rs.getString("id")),
                registrationNumber = rs.getString("plate_number"),
                accountId = UUID.fromString(rs.getString("account_id")),
                position = GeoLocation(
                    latitude = rs.getDouble("latitude"),
                    longitude = rs.getDouble("longitude")
                ),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }

        return results.firstOrNull() 
    }

}
