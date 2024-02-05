package com.vasiliytemniy.deliverymicroservice.services

import com.google.common.net.HttpHeaders
import com.vasiliytemniy.deliverymicroservice.domain.*
import com.vasiliytemniy.deliverymicroservice.dto.CalculateDeliveryMetaDto
import com.vasiliytemniy.deliverymicroservice.dto.DeliveryCostParamsDto
import com.vasiliytemniy.deliverymicroservice.dto.DeliveryTimeParamsDto
import com.vasiliytemniy.deliverymicroservice.dto.GeocodingPoint
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.slf4j.LoggerFactory


@Service
class DeliveryMetaServiceImpl(
    //TODO add redis caching! Redis Repository inject here
): DeliveryMetaService {


    private val graphhopperApiKey = dotenv()["GRAPHHOPPER_API_KEY"]

    @Value("\${spring.application.name}")
    private val applicationName: String? = null

    private val webClient = WebClient.builder()
        .defaultHeader(HttpHeaders.USER_AGENT, "$applicationName")
        .defaultHeader(HttpHeaders.REFERER, "https://github.com/vasiliytemniy/delivery-microservice")
        .build()

    override suspend fun calculateDeliveryMeta(requestDto: CalculateDeliveryMetaDto): DeliveryMeta {

        if (requestDto.metaCalculationType == DeliveryMetaCalculationType.ONLY_COST) {
            if (requestDto.deliveryCostParams == null) {
                throw IllegalArgumentException("Delivery cost params should not be null for 'only cost' calculation")
            }

            val distanceInMeters: Int? = if (requestDto.deliveryCostParams.calculationTypes.contains(DeliveryCostCalculationType.DISTANCE)) {
                if (requestDto.fromAddress == null || requestDto.toAddress == null) {
                    throw IllegalArgumentException("From and to addresses should not be null for distance-based delivery cost calculation")
                }
                getDistanceInMeters(requestDto.fromAddress, requestDto.toAddress)
            } else {
                null
            }

            // Early return
            return DeliveryMeta(
                cost = calculateDeliveryCost(
                    requestDto.deliveryCostParams,
                    distanceInMeters
                ),
                estimatedDeliveryHours = null
            )
        }

        // If requestDto.metaCalculationType != DeliveryMetaCalculationType.ONLY_COST then from and to addresses should not be null
        if (requestDto.fromAddress == null || requestDto.toAddress == null) {
            throw IllegalArgumentException("From and to addresses should not be null for estimated delivery time calculation")
        }
        val distanceInMeters: Int = getDistanceInMeters(requestDto.fromAddress, requestDto.toAddress)

        if (requestDto.metaCalculationType == DeliveryMetaCalculationType.ONLY_ESTIMATED_TIME) {
            if (requestDto.deliveryTimeParams == null) {
                throw IllegalArgumentException("Delivery time params should not be null for 'only estimated time' calculation")
            }

            // Early return
            return  DeliveryMeta(
                cost = null,
                estimatedDeliveryHours = calculateEstimatedDeliveryTime(
                    requestDto.deliveryTimeParams,
                    distanceInMeters
                )
            )
        }

        if (requestDto.metaCalculationType == DeliveryMetaCalculationType.COST_AND_ESTIMATED_TIME) {
            if (requestDto.deliveryCostParams == null || requestDto.deliveryTimeParams == null) {
                throw IllegalArgumentException("Delivery cost params and delivery time params should not be null for 'cost and estimated time' calculation")
            }

            // Early return
            return  DeliveryMeta(
                cost = calculateDeliveryCost(
                    requestDto.deliveryCostParams,
                    distanceInMeters
                ),
                estimatedDeliveryHours = calculateEstimatedDeliveryTime(
                    requestDto.deliveryTimeParams,
                    distanceInMeters
                )
            )
        }

        throw IllegalArgumentException("Unknown meta calculation type: ${requestDto.metaCalculationType}")
    }

    private suspend fun getDistanceInMeters(fromAddress: String, toAddress: String): Int {

        if (fromAddress == toAddress) {
            return 0
        }

        val (from, to) = requestGeocoding(fromAddress, toAddress)

        if (from == to) {
            return 0
        }

        return requestPathDistance(from, to)
    }

    private suspend fun requestGeocoding(fromAddress: String, toAddress: String): Pair<GeocodingPoint, GeocodingPoint> =
        withContext(Dispatchers.IO) {
            val from = async {
                webClient.get()
                    .uri("https://graphhopper.com/api/1/geocode?q=$fromAddress&key=$graphhopperApiKey")
                    .exchangeToMono { clientResponse ->
                        println("Response Headers: ${clientResponse.headers().asHttpHeaders()}")
                        clientResponse.bodyToMono(String::class.java)
                    }
                    .awaitFirst()
            }

            val to = async {
                webClient.get()
                    .uri("https://graphhopper.com/api/1/geocode?q=$toAddress&key=$graphhopperApiKey")
                    .exchangeToMono { clientResponse ->
                        println("Response Headers: ${clientResponse.headers().asHttpHeaders()}")
                        clientResponse.bodyToMono(String::class.java)
                    }
                    .awaitFirst()
            }

            println("from: ${from.await()}")
            println("to: ${to.await()}")

            TODO("Not yet implemented")
        }

    private suspend fun requestPathDistance(fromPoint: GeocodingPoint, toPoint: GeocodingPoint): Int =
        withContext(Dispatchers.IO) {

            val response = async {
                webClient.get()
                    .uri("https://graphhopper.com/api/1/route?point=${fromPoint.lat},${fromPoint.lng}&point=${toPoint.lat},${toPoint.lng}&key=$graphhopperApiKey")
                    .exchangeToMono { clientResponse ->
                        println("Response Headers: ${clientResponse.headers().asHttpHeaders()}")
                        clientResponse.bodyToMono(String::class.java)
                    }
                    .awaitFirst()
            }

            println("response: ${response.await()}")

            TODO("Not yet implemented")
        }

    private fun calculateEstimatedDeliveryTime(timeParams: DeliveryTimeParamsDto, distanceInMeters: Int): Int {
        val estimatedMedianSpeedKmH = timeParams.estimatedVehicleMedianSpeedKmH?:
            when (timeParams.deliveryVehicleType) {
                DeliveryVehicleType.CAR -> 70
                DeliveryVehicleType.TRUCK -> 60
                DeliveryVehicleType.PLANE -> 800
                DeliveryVehicleType.TRAIN -> 55
                DeliveryVehicleType.SHIP -> 30
                DeliveryVehicleType.BICYCLE -> 20
            }

        return ((distanceInMeters / 1000) / estimatedMedianSpeedKmH) +
                timeParams.estimatedDispatchTimeDeltaHours + timeParams.estimatedDestinationTimeDeltaHours
    }

    private fun calculateDeliveryCost(costParams: DeliveryCostParamsDto, distanceInMeters: Int?): Int {

        var cost = 0

        costParams.calculationTypes.forEach {
            when (it) {
                DeliveryCostCalculationType.FIXED -> {
                    if (costParams.fixedCostAddon == null) {
                        throw IllegalArgumentException("Fixed cost addon cannot be null if fixed cost calculation type is used")
                    }
                    cost += costParams.fixedCostAddon
                }
                DeliveryCostCalculationType.DISTANCE -> {
                    if (
                        costParams.distanceStepCost == null ||
                        costParams.distanceStepQuantity == null ||
                        costParams.distanceStepMeasure == null ||
                        distanceInMeters == null
                    ) {
                        throw IllegalArgumentException("Distance cost parameters cannot be null if distance cost calculation type is used")
                    }
                    cost += calculateDistanceBasedCost(
                        distanceInMeters,
                        costParams.distanceStepCost,
                        costParams.distanceStepQuantity,
                        costParams.distanceStepMeasure
                    )
                }
                DeliveryCostCalculationType.MASS -> {
                    if (
                        costParams.massStepCost == null ||
                        costParams.massStepQuantity == null ||
                        costParams.mass == null
                        // add measure check? Not used yet
                    ) {
                        throw IllegalArgumentException("Mass cost parameters cannot be null if mass cost calculation type is used")
                    }
                    cost += calculateMassBasedCost(
                        costParams.mass,
                        costParams.massStepCost,
                        costParams.massStepQuantity
                    )
                }
                DeliveryCostCalculationType.VOLUME -> {
                    if (
                        costParams.volumeStepCost == null ||
                        costParams.volumeStepQuantity == null ||
                        costParams.volume == null
                        // add measure check? Not used yet
                    ) {
                        throw IllegalArgumentException("Volume cost parameters cannot be null if volume cost calculation type is used")
                    }
                    cost += calculateVolumeBasedCost(
                        costParams.volume,
                        costParams.volumeStepCost,
                        costParams.volumeStepQuantity
                    )
                }
            }
        }

        return cost
    }

    private fun calculateMassBasedCost(mass: Int, massStepCost: Int, massStepQuantity: Int): Int =
        (mass / massStepQuantity) * massStepCost

    private fun calculateVolumeBasedCost(volume: Int, volumeStepCost: Int, volumeStepQuantity: Int): Int =
        (volume / volumeStepQuantity) * volumeStepCost

    private fun calculateDistanceBasedCost(
        distanceInMeters: Int,
        distanceStepCost: Int,
        distanceStepQuantity: Int,
        distanceStepMeasure: String
    ): Int {
        val distanceStepQuantityInMeters: Int = when (distanceStepMeasure) {
            "km" -> distanceStepQuantity * 1000
            "m" -> distanceStepQuantity
            "mi" -> distanceStepQuantity * 1609
            "yd" -> (distanceStepQuantity * 0.9144).toInt()
            else -> distanceStepQuantity // ignore invalid distance measures
        }

        return (distanceInMeters / distanceStepQuantityInMeters) * distanceStepCost
    }


    companion object {
        private val logger = LoggerFactory.getLogger(DeliveryMetaServiceImpl::class.java)
    }
}