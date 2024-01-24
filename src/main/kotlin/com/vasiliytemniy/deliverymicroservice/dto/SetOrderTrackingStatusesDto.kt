package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


data class SetOrderTrackingStatusDto(
    val orderTrackingIdentifiers: List<OrderTrackingIdentifierDto>,
    val status: String,
    val deliveredAt: String?,
) {

    init {
        require(orderTrackingIdentifiers.isNotEmpty()) { "orderTrackingIdentifiers must not be empty" }
    }

    companion object
}


fun SetOrderTrackingStatusDto.Companion.of(request: OrderTracking.SetOrderTrackingStatusRequest): SetOrderTrackingStatusDto {

    val orderTrackingIdentifiers = request.orderTrackingIdentifiersList.toList()
        .map { OrderTrackingIdentifierDto.of(it) }

    return SetOrderTrackingStatusDto(orderTrackingIdentifiers, request.status, request.deliveredAt)
}