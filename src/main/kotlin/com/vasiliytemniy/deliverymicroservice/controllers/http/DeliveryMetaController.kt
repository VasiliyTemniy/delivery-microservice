package com.vasiliytemniy.deliverymicroservice.controllers.http

import com.vasiliytemniy.deliverymicroservice.domain.DeliveryCostCalculationType
import com.vasiliytemniy.deliverymicroservice.domain.DeliveryMetaCalculationType
import com.vasiliytemniy.deliverymicroservice.domain.DeliveryVehicleType
import com.vasiliytemniy.deliverymicroservice.domain.toSuccessHttpResponse
import com.vasiliytemniy.deliverymicroservice.dto.CalculateDeliveryMetaDto
import com.vasiliytemniy.deliverymicroservice.dto.DeliveryCostParamsDto
import com.vasiliytemniy.deliverymicroservice.dto.DeliveryTimeParamsDto
import com.vasiliytemniy.deliverymicroservice.dto.SuccessDeliveryMetaResponse
import com.vasiliytemniy.deliverymicroservice.services.DeliveryMetaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Delivery meta", description = "Delivery meta calculation REST API")
@RestController
@RequestMapping(path = ["/api/v1/delivery-meta"])
class DeliveryMetaController(
    val deliveryMetaService: DeliveryMetaService
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "calculateDeliveryMeta",
        summary = "Calculate delivery meta",
        operationId = "calculateDeliveryMeta",
        description = "Calculate delivery meta"
    )
    suspend fun calculateDeliveryMeta(
        @RequestParam(name = "meta-calculation-type", defaultValue = "cost_and_estimated_time") metaCalculationType: String,
        @RequestParam(name = "from-address") fromAddress: String?,
        @RequestParam(name = "to-address") toAddress: String?,
        @RequestParam(name = "cost-calculation-types") costCalculationTypes: List<String>?,
        @RequestParam(name = "fixed-cost-addon") fixedCostAddon: Int?,
        @RequestParam(name = "distance-step-cost") distanceStepCost: Int?,
        @RequestParam(name = "distance-step-quantity") distanceStepQuantity: Int?,
        @RequestParam(name = "distance-step-measure") distanceStepMeasure: String?,
        @RequestParam(name = "mass-step-cost") massStepCost: Int?,
        @RequestParam(name = "mass-step-quantity") massStepQuantity: Int?,
        @RequestParam(name = "mass-step-measure") massStepMeasure: String?,
        @RequestParam(name = "volume-step-cost") volumeStepCost: Int?,
        @RequestParam(name = "volume-step-quantity") volumeStepQuantity: Int?,
        @RequestParam(name = "volume-step-measure") volumeStepMeasure: String?,
        @RequestParam(name = "mass") mass: Int?,
        @RequestParam(name = "volume") volume: Int?,
        @RequestParam(name = "delivery-vehicle-type", defaultValue = "car") deliveryVehicleType: String,
        @RequestParam(name = "estimated-dispatch-time-delta-hours", defaultValue = "0") estimatedDispatchTimeDeltaHours: Int,
        @RequestParam(name = "estimated-destination-time-delta-hours", defaultValue = "0") estimatedDestinationTimeDeltaHours: Int,
        @RequestParam(name = "estimated-vehicle-median-speed-km-h") estimatedVehicleMedianSpeedKmH: Int?
    ): ResponseEntity<SuccessDeliveryMetaResponse> =
        withTimeout(TIMEOUT_MILLIS) {
            ResponseEntity
                .status(200)
                .body(deliveryMetaService.calculateDeliveryMeta(
                    CalculateDeliveryMetaDto(
                        DeliveryMetaCalculationType.fromValue(metaCalculationType),
                        fromAddress,
                        toAddress,
                        DeliveryCostParamsDto(
                            (costCalculationTypes?:emptyList()).map { DeliveryCostCalculationType.fromValue(it) },
                            fixedCostAddon,
                            distanceStepCost,
                            distanceStepQuantity,
                            distanceStepMeasure,
                            massStepCost,
                            massStepQuantity,
                            massStepMeasure,
                            volumeStepCost,
                            volumeStepQuantity,
                            volumeStepMeasure,
                            mass,
                            volume
                        ),
                        DeliveryTimeParamsDto(
                            DeliveryVehicleType.fromValue(deliveryVehicleType),
                            estimatedDispatchTimeDeltaHours,
                            estimatedDestinationTimeDeltaHours,
                            estimatedVehicleMedianSpeedKmH
                        )
                    )
                ).toSuccessHttpResponse())
                .also { log.info("response: $it") }
        }

    companion object {
        private val log = LoggerFactory.getLogger(DeliveryMetaController::class.java)
        private const val TIMEOUT_MILLIS = 10000L
    }
}