package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking

data class ReorderOrderTrackingsDto(
    val orderId: Long,
    val fromPointNumberToPointNumber: Map<Int, Int>
) {

    init {
        require(fromPointNumberToPointNumber.isNotEmpty()) { "fromPointNumberToPointNumber must not be empty" }
    }

    companion object
}

fun ReorderOrderTrackingsDto.Companion.of(request: OrderTracking.ReorderRequest): ReorderOrderTrackingsDto {
    return ReorderOrderTrackingsDto(
        request.orderId,
        request.fromPointNumberToPointNumberList.associate { it.key to it.value }
    )
}