package zencargo.roadtrackingtask.dto

data class GraphQLVehicle(
    val id: String,
    val registrationNumber: String,
    val accountId: String,
    val latestPosition: GraphQLPosition,
    val status: GraphQLStatus
)

data class GraphQLPosition(
    val latitude: Float,
    val longitude: Float,
    val timestamp: String
)

enum class GraphQLStatus {
    ACTIVE,
    INACTIVE
}
