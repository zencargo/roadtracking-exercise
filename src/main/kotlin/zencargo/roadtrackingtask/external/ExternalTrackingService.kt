package zencargo.roadtrackingtask.external

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import zencargo.roadtrackingtask.domain.GeoLocation
import java.util.UUID

@Service
class ExternalTrackingService {
    private val restTemplate = RestTemplate()
    private val logger: Logger = LoggerFactory.getLogger(ExternalTrackingService::class.java)

    fun getVehiclePosition(vehicleId: UUID): GeoLocation? {
        val url = "http://external-tracking-api/api/vehicle/$vehicleId/position"

        val response = restTemplate.getForEntity(url, GeoLocation::class.java)

        return if (response.statusCode.is2xxSuccessful && response.body != null) {
            response.body
        } else {
            logger.warn("Failed to fetch position for vehicleId=$vehicleId. Status: ${response.statusCode}")
            null
        }
    }
}
