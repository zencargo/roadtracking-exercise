package zencargo.roadtrackingtask.graphql

import zencargo.roadtrackingtask.dto.GraphQLPosition
import zencargo.roadtrackingtask.dto.GraphQLStatus
import zencargo.roadtrackingtask.dto.GraphQLVehicle
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import zencargo.roadtrackingtask.domain.Vehicle
import zencargo.roadtrackingtask.service.VehicleService
import java.util.UUID

@DgsComponent
class VehicleResolver(
    private val vehicleService: VehicleService
) {
    private var cachedVehicles: List<Vehicle>? = null

    private val logger = LoggerFactory.getLogger(VehicleResolver::class.java)


    private fun Vehicle.toGraphQL(): GraphQLVehicle = GraphQLVehicle(
        id = id.toString(),
        registrationNumber = registrationNumber,
        accountId = accountId.toString(),
        latestPosition = GraphQLPosition(
            latitude = position.latitude.toFloat(),
            longitude = position.longitude.toFloat(),
            timestamp = updatedAt.toString()
        ),
        status = when (status) {
            zencargo.roadtrackingtask.domain.Status.ACTIVE -> GraphQLStatus.ACTIVE
            zencargo.roadtrackingtask.domain.Status.INACTIVE -> GraphQLStatus.INACTIVE
            null -> GraphQLStatus.ACTIVE
        }
    )

    @DgsQuery
    fun vehicles(
        @InputArgument accountId: String,
        @InputArgument searchPlate: String?
    ): List<GraphQLVehicle> {
        val userId = SecurityContextHolder.getContext().authentication.name
        if (userId.isBlank()) {
            logger.warn("Unauthenticated access?")
        }
        return if (searchPlate != null) {
            vehicleService.searchVehiclesByPlate(searchPlate)
                .map { it.toGraphQL() }
        } else {
            if (cachedVehicles == null) {
                logger.info("Fetching vehicles from DB for accountId $accountId")
                cachedVehicles = vehicleService.getAllVehicles(UUID.fromString(accountId))
            }
            cachedVehicles!!.map { it.toGraphQL() }
        }
    }


    @DgsQuery
    fun vehicle(@InputArgument id: String): GraphQLVehicle? {
        val userId = SecurityContextHolder.getContext().authentication.name
        if (userId.isBlank()) {
            logger.warn("Unauthenticated access?")
        }

        return try {
            vehicleService.getVehicleById(UUID.fromString(id))?.toGraphQL()
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid UUID format: $id", e)
            null
        }
    }

    @DgsMutation
    fun registerVehicle(
        @InputArgument registrationNumber: String,
        @InputArgument accountId: String
    ): GraphQLVehicle {
        val userId = SecurityContextHolder.getContext().authentication.name
        if (userId.isBlank()) {
            throw SecurityException("Authentication required")
        }

        return try {
            val accountUUID = UUID.fromString(accountId)
            val vehicle = vehicleService.registerVehicle(registrationNumber, accountUUID)
            vehicle.toGraphQL()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid account ID format")
        }
    }
}
