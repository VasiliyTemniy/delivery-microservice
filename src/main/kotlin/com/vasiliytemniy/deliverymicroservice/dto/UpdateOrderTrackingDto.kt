package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


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
    companion object
}

fun UpdateOrderTrackingDto.Companion.of(request: OrderTracking.UpdateOrderTrackingRequest): UpdateOrderTrackingDto {
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