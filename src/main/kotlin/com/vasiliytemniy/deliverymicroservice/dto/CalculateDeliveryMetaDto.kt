package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.domain.DeliveryCostCalculationType
import com.vasiliytemniy.deliverymicroservice.domain.DeliveryMetaCalculationType
import com.vasiliytemniy.deliverymicroservice.domain.DeliveryVehicleType
import com.vasiliytemniy.grpc.deliverymeta.service.DeliveryMeta

data class CalculateDeliveryMetaDto(
    val metaCalculationType: DeliveryMetaCalculationType,
    val fromAddress: String?,
    val toAddress: String?,
    val deliveryCostParams: DeliveryCostParamsDto?,
    val deliveryTimeParams: DeliveryTimeParamsDto?
) {
    companion object
}

data class DeliveryCostParamsDto(
    val calculationTypes: List<DeliveryCostCalculationType>,
    val fixedCostAddon: Int?,
    val distanceStepCost: Int?,
    val distanceStepQuantity: Int?,
    val distanceStepMeasure: String?,
    val massStepCost: Int?,
    val massStepQuantity: Int?,
    val massStepMeasure: String?,
    val volumeStepCost: Int?,
    val volumeStepQuantity: Int?,
    val volumeStepMeasure: String?,
    val mass: Int?,
    val volume: Int?
) {
    companion object
}

data class DeliveryTimeParamsDto(
    val deliveryVehicleType: DeliveryVehicleType,
    val estimatedDispatchTimeDeltaHours: Int, // sort, prepare, load, etc
    val estimatedDestinationTimeDeltaHours: Int, // unload, prepare, sort, etc
    val estimatedVehicleMedianSpeedKmH: Int?
) {
    companion object
}


fun CalculateDeliveryMetaDto.Companion.of(request: DeliveryMeta.CalculateDeliveryMetaRequest): CalculateDeliveryMetaDto {
    return CalculateDeliveryMetaDto(
        metaCalculationType = DeliveryMetaCalculationType.fromValue(request.metaCalculationType),
        fromAddress = request.fromAddress,
        toAddress = request.toAddress,
        deliveryCostParams = request.deliveryCostParams?.let { DeliveryCostParamsDto.of(it) },
        deliveryTimeParams = request.deliveryTimeParams?.let { DeliveryTimeParamsDto.of(it) }
    )
}

fun DeliveryCostParamsDto.Companion.of(messageObj: DeliveryMeta.DeliveryCostParams): DeliveryCostParamsDto {
    return DeliveryCostParamsDto(
        calculationTypes = messageObj.calculationTypeList.map { DeliveryCostCalculationType.fromValue(it) },
        fixedCostAddon = messageObj.fixedCostAddon,
        distanceStepCost = messageObj.distanceStepCost,
        distanceStepQuantity = messageObj.distanceStepQuantity,
        distanceStepMeasure = messageObj.distanceStepMeasure,
        massStepCost = messageObj.massStepCost,
        massStepQuantity = messageObj.massStepQuantity,
        massStepMeasure = messageObj.massStepMeasure,
        volumeStepCost = messageObj.volumeStepCost,
        volumeStepQuantity = messageObj.volumeStepQuantity,
        volumeStepMeasure = messageObj.volumeStepMeasure,
        mass = messageObj.mass,
        volume = messageObj.volume
    )
}

fun DeliveryTimeParamsDto.Companion.of(messageObj: DeliveryMeta.DeliveryTimeParams): DeliveryTimeParamsDto {
    return DeliveryTimeParamsDto(
        deliveryVehicleType = DeliveryVehicleType.fromValue(messageObj.deliveryVehicleType),
        estimatedDispatchTimeDeltaHours = messageObj.estimatedDispatchTimeDeltaMs,
        estimatedDestinationTimeDeltaHours = messageObj.estimatedDestinationTimeDeltaMs,
        estimatedVehicleMedianSpeedKmH = messageObj.estimatedVehicleMedianSpeedKmH
    )
}