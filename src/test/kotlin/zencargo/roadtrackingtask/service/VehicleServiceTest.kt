package zencargo.roadtrackingtask.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.RowMapper
import zencargo.roadtrackingtask.domain.GeoLocation
import zencargo.roadtrackingtask.domain.Status
import zencargo.roadtrackingtask.domain.Vehicle
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

class VehicleServiceTest {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var vehicleService: VehicleService

    @BeforeEach
    fun setup() {
        jdbcTemplate = mock(JdbcTemplate::class.java)
        vehicleService = VehicleService(jdbcTemplate)
    }

    @Test
    fun `searchVehiclesByPlate returns list of vehicles`() {
        val plate = "AB123"
        val resultSet = mock(ResultSet::class.java)

        val expectedVehicle = Vehicle(
            id = UUID.randomUUID(),
            registrationNumber = "AB123CD",
            accountId = UUID.randomUUID(),
            position = GeoLocation(51.0, -0.1),
            status = Status.ACTIVE,
            updatedAt = LocalDateTime.now()
        )

        `when`(resultSet.getString("id")).thenReturn(expectedVehicle.id.toString())
        `when`(resultSet.getString("plate_number")).thenReturn(expectedVehicle.registrationNumber)
        `when`(resultSet.getString("account_id")).thenReturn(expectedVehicle.accountId.toString())
        `when`(resultSet.getDouble("latitude")).thenReturn(expectedVehicle.position.latitude)
        `when`(resultSet.getDouble("longitude")).thenReturn(expectedVehicle.position.longitude)
        `when`(resultSet.getString("status")).thenReturn(expectedVehicle.status?.name)
        `when`(resultSet.getTimestamp("updated_at")).thenReturn(java.sql.Timestamp.valueOf(expectedVehicle.updatedAt))

        `when`(jdbcTemplate.query(anyString(), any<RowMapper<Vehicle>>())).thenAnswer {
            val mapper = it.arguments[1] as RowMapper<Vehicle>
            listOf(mapper.mapRow(resultSet, 0))
        }

        val result = vehicleService.searchVehiclesByPlate(plate)

        assertEquals(1, result.size)
        assertEquals(expectedVehicle.registrationNumber, result[0].registrationNumber)
        assertEquals(expectedVehicle.status, result[0].status)
        assertEquals(expectedVehicle.updatedAt, result[0].updatedAt)
    }

    @Test
    fun `getAllVehicles returns vehicles for account`() {
        val accountId = UUID.randomUUID()
        val expectedVehicle = Vehicle(
            id = UUID.randomUUID(),
            registrationNumber = "CD456EF",
            accountId = accountId,
            position = GeoLocation(52.0, 1.0),
            status = Status.ACTIVE,
            updatedAt = LocalDateTime.now()
        )

        val resultSet = mock(ResultSet::class.java)
        `when`(resultSet.getString("id")).thenReturn(expectedVehicle.id.toString())
        `when`(resultSet.getString("plate_number")).thenReturn(expectedVehicle.registrationNumber)
        `when`(resultSet.getString("account_id")).thenReturn(expectedVehicle.accountId.toString())
        `when`(resultSet.getDouble("latitude")).thenReturn(expectedVehicle.position.latitude)
        `when`(resultSet.getDouble("longitude")).thenReturn(expectedVehicle.position.longitude)
        `when`(resultSet.getTimestamp("updated_at")).thenReturn(java.sql.Timestamp.valueOf(expectedVehicle.updatedAt))
        `when`(resultSet.getString("status")).thenReturn(expectedVehicle.status?.name)

        `when`(jdbcTemplate.query(anyString(), any<RowMapper<Vehicle>>(), any()))
            .thenAnswer { invocation ->
                val rowMapper = invocation.getArgument<RowMapper<Vehicle>>(1)
                listOf(rowMapper.mapRow(resultSet, 0))
            }

        val result = vehicleService.getAllVehicles(accountId)

        assertEquals(1, result.size)
        assertEquals(expectedVehicle.accountId, result[0].accountId)
        assertEquals(expectedVehicle.status, result[0].status)
    }

}
