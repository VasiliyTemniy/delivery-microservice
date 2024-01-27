package com.vasiliytemniy.deliverymicroservice.dto


import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


data class OrderTrackingExternalIdDto(
    val orderId: String,
    val pointNumber: Int
) {
    companion object
}

fun OrderTrackingExternalIdDto.Companion.of(record: OrderTracking.OrderTrackingExternalId): OrderTrackingExternalIdDto {
    return OrderTrackingExternalIdDto(record.orderId, record.pointNumber)
}