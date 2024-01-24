package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking


data class SetOrderTrackingStatusDto(
    val orderId: Long,
    val pointNumber: Int,
    val status: String,
    val deliveredAt: String?,
) {
    companion object
}


fun SetOrderTrackingStatusDto.Companion.of(request: OrderTracking.SetOrderTrackingStatusRequest): SetOrderTrackingStatusDto {
    return SetOrderTrackingStatusDto(request.orderId, request.pointNumber, request.status, request.deliveredAt)
}