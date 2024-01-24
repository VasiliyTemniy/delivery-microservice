package com.vasiliytemniy.deliverymicroservice.dto


import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


data class OrderTrackingIdentifierDto(
    val orderId: Long,
    val pointNumber: Int
) {
    companion object
}

fun OrderTrackingIdentifierDto.Companion.of(record: OrderTracking.OrderTrackingIdentifier): OrderTrackingIdentifierDto {
    return OrderTrackingIdentifierDto(record.orderId, record.pointNumber)
}