package zencargo.roadtrackingtask.domain

import java.time.LocalDateTime
import java.util.UUID

data class Vehicle(
    val id: UUID,
    val registrationNumber: String,
    val accountId: UUID,
    val position: GeoLocation,
    val status: Status? = Status.ACTIVE,
    val updatedAt: LocalDateTime
)

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

enum class Status {
    ACTIVE,
    INACTIVE
}
