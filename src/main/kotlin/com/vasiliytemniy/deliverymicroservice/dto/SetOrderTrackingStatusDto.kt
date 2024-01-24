package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


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