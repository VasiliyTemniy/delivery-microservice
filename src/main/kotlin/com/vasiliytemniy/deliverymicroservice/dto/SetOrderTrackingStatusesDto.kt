package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


data class SetOrderTrackingStatusesDto(
    val orderTrackingIdentifiers: List<OrderTrackingIdentifierDto>,
    val status: String,
    val deliveredAt: String?,
) {

    init {
        require(orderTrackingIdentifiers.isNotEmpty()) { "orderTrackingIdentifiers must not be empty" }
    }

    companion object
}


fun SetOrderTrackingStatusesDto.Companion.of(request: OrderTracking.SetOrderTrackingStatusesRequest): SetOrderTrackingStatusesDto {

    val orderTrackingIdentifiers = request.orderTrackingIdentifiersList.toList()
        .map { OrderTrackingIdentifierDto.of(it) }

    return SetOrderTrackingStatusesDto(orderTrackingIdentifiers, request.status, request.deliveredAt)
}