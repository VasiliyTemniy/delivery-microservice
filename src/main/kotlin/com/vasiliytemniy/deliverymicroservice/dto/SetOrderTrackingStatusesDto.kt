package com.vasiliytemniy.deliverymicroservice.dto

import com.vasiliytemniy.deliverymicroservice.utils.parseOptionalDate
import com.vasiliytemniy.grpc.ordertracking.service.OrderTracking

data class SetOrderTrackingStatusesDto(
    val orderTrackingExternalIds: List<OrderTrackingExternalIdDto>,
    val status: String,
    val deliveredAt: String?,
) {

    val parsedDeliveredAt = parseOptionalDate(deliveredAt)

    init {
        require(orderTrackingExternalIds.isNotEmpty()) { "orderTrackingExternalIds must not be empty" }
    }

    companion object
}


fun SetOrderTrackingStatusesDto.Companion.of(request: OrderTracking.SetStatusesRequest): SetOrderTrackingStatusesDto {

    val orderTrackingExternalIds = request.orderTrackingExternalIdsList.toList()
        .map { OrderTrackingExternalIdDto.of(it) }

    return SetOrderTrackingStatusesDto(orderTrackingExternalIds, request.status, request.deliveredAt)
}