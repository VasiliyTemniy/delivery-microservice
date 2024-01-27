package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.utils.parseOptionalDate
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class UpdateOrderTrackingDto (
    val orderId: Long,
    val pointNumber: Int,
    val fromFacilityId: Long,
    val destinationId: Long,
    val destinationType: String,
    val carrierId: Long,
    val status: String,
    val deliveryCost: Int,
    val currency: String,
    val currencyDecimalMultiplier: Int,
    val massControlValue: Int?,
    val massMeasure: String?,
    val estimatedDeliveryAt: String?,
    val deliveredAt: String?
) {

    val parsedEstimatedDeliveryAt: LocalDateTime? = parseOptionalDate(estimatedDeliveryAt)
    val parsedDeliveredAt: LocalDateTime? = parseOptionalDate(deliveredAt)

    companion object
}

fun UpdateOrderTrackingDto.Companion.of(request: OrderTracking.UpdateRequest): UpdateOrderTrackingDto {
    return UpdateOrderTrackingDto(
        orderId = request.orderId,
        pointNumber = request.pointNumber,
        fromFacilityId = request.fromFacilityId,
        destinationId = request.destinationId,
        destinationType = request.destinationType,
        carrierId = request.carrierId,
        status = request.status,
        deliveryCost = request.deliveryCost,
        currency = request.currency,
        currencyDecimalMultiplier = request.currencyDecimalMultiplier,
        massControlValue = request.massControlValue,
        massMeasure = request.massMeasure,
        estimatedDeliveryAt = request.estimatedDeliveryAt,
        deliveredAt = request.deliveredAt
    )
}